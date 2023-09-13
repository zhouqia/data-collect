package com.ipharmacare.collect.service.collect.handle;

import com.alibaba.fastjson.JSONObject;
import com.ipharmacare.oss.OssUploadService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StopWatch;
import sun.nio.ch.FileChannelImpl;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
    数据采集原理：
 1.服务启动时，判断是否有缓存文件，有的话，则将该缓存文件中的数文件名存入canSaveToDbList；
 2.服务运行状态中，一个文件满足条件(写满5M，按小时采集存入临时文件 可配置)，之后状态置为不再采集 存入canSaveToDbList，交由 单独的线程将该缓存文件中的记录批量同步到oss，同步万之后，将该缓存文件删除；
 3.服务运行状态中，将采集到的记录，暂存到本地缓存中；

 数据丢失的场景：
 1.服务器断电，pageCache中的数据未落盘；
 2.线程池中的任务尚未执行，相关记录尚未写入pageCache；
 * @Author: zhouqiang
 * @Date: 2023/5/30 15:27
 */
@Configuration
@Slf4j
public class DataHandlerHelper implements ApplicationRunner {

    private static final String FILE_SUFFIX = ".txt";
    private static volatile Long FILE_NUM = 1L;

    /**
     * 本地缓存目录 如果为空，则默认 System.getProperty("user.dir")+"/data/"
     */
    @Value("${collect.cache.path:}")
    private String cachePath;
    private static final String DEFAULT_CACHE_PATH = System.getProperty("user.dir")+ System.getProperty("file.separator") +"data" + System.getProperty("file.separator");

    /**
     * 一个文件满足条件(默认5M， 可配置)
     */
    @Value("${collect.cache.file.size:0}")
    private Long cacheFileSize;
    private static final Long DEFAULT_CACHE_FILE_SIZE = 1024 * 1024 * 5L;

    /**
     * 缓存文件已满足条件(存满 文件大小可配置)
     * 存储文件地址
     */
    private static CopyOnWriteArrayList<String> canSaveToDbList = new CopyOnWriteArrayList();

    /**
     * 当前可使用的MappedByteBuffer,若为空，可基于获取新的文件地址创建新的MappedByteBuffer
     */
    private static ImmutablePair<String, MappedByteBuffer> curMappedByteBufferPair = null;


    @Autowired
    private OssUploadService ossUploadService;

    /**
     * 生成文件名，并返回完整地址信息
     * @return
     */
    private synchronized String getFileFullPath(){
        String path = StringUtils.isEmpty(cachePath)? DEFAULT_CACHE_PATH : cachePath;
        String leftSlash = System.getProperty("file.separator");
        if(!path.endsWith(leftSlash)){
            path = StringUtils.join(path, leftSlash);
        }
        return StringUtils.join(
                path,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHH-mmss-")),
                FILE_NUM++,
                FILE_SUFFIX
        ).trim();
    }


    /**
     * 记录地址创建MappedByteBuffer对象
     * @param cachePath
     * @return
     */
    private  MappedByteBuffer getMappedByteBuffer(String cachePath){
        log.info("基于地址创建本地文件：{}", cachePath);
        FileChannel fci = null;
        RandomAccessFile randomAccessFile = null;
        try {
            File cacheFile = new File(cachePath);
            if (!cacheFile.exists()){
                cacheFile.getParentFile().mkdirs();
                cacheFile.createNewFile();
            }

            randomAccessFile = new RandomAccessFile(cacheFile, "rw");
            fci = randomAccessFile.getChannel();
            Long fileSize = cacheFileSize > 0 ? cacheFileSize * 1024 * 1024 : DEFAULT_CACHE_FILE_SIZE;
            MappedByteBuffer mappedByteBuffer = fci.map(FileChannel.MapMode.READ_WRITE, 0, fileSize);
            return mappedByteBuffer;
        }catch (Exception e){
            log.error("创建MappedByteBuffer失败： ", e);
        }finally {
            try {
                if(fci != null){
                    fci.close();
                }
                if(randomAccessFile != null){
                    randomAccessFile.close();
                }
            }catch (Exception e){
                log.error("文件关闭失败: ", e);
            }
        }
        return null;
    }


    /**
     * 获取缓存MappedByteBuffer(写入数据使用) 没有则创建
     * @return
     */
    private ImmutablePair<String, MappedByteBuffer> getMappedByteBuffer(){
        if(curMappedByteBufferPair != null){
            return curMappedByteBufferPair;
        }else {
            String fileFullPath = this.getFileFullPath();
            log.info("fileFullPath: {}", fileFullPath);
            MappedByteBuffer mappedByteBuffer = getMappedByteBuffer(fileFullPath);
            if(mappedByteBuffer == null){
                return null;
            }
            curMappedByteBufferPair = ImmutablePair.of(fileFullPath, mappedByteBuffer);
            return curMappedByteBufferPair;
        }
    }

    /**
     * MappedByteBuffer 可写入容量检测
     * @param mappedByteBuffer
     * @param dataSize
     * @return
     */
    private Boolean checkBounds(MappedByteBuffer mappedByteBuffer, int dataSize){
        return  mappedByteBuffer.remaining() > dataSize ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * 一个文件最长能写多少时间  yyyyMMddHH
     * @return
     */
    private Boolean checkTime(String fileFullPath){
        String[] paths = fileFullPath.split("/|\\\\");
        String timeString = paths[paths.length-1].substring(0, 10);
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHH")).equals(timeString);
    }

    /**
     * 数据写入本地缓存
     * @param content
     */
    public void writeToLocal(String content){
        if(StringUtils.isEmpty(content)){
            return;
        }
        byte[] data = content.getBytes();
        try {
            ImmutablePair<String, MappedByteBuffer> mappedByteBufferPair = getMappedByteBuffer();
            String fileFullPath =  mappedByteBufferPair.getLeft();
            MappedByteBuffer mappedByteBuffer =  mappedByteBufferPair.getRight();
            if(!checkBounds(mappedByteBuffer, data.length) || !checkTime(fileFullPath)){
//        检测容量不够时，直接将该 MappedByteBuffer 移入 canSaveToDbList，并重置关闭硬盘映射文件权柄
                log.info("关闭文件：{}", fileFullPath);
                closeCacheFile(mappedByteBuffer);
                canSaveToDbList.add(fileFullPath);
                curMappedByteBufferPair = null;
                mappedByteBufferPair = getMappedByteBuffer();
                mappedByteBuffer =  mappedByteBufferPair.getRight();
            }
            if(mappedByteBufferPair == null || mappedByteBuffer == null){
                log.error("创建MappedByteBuffer失败,需要保存的数据： {}", content);
                return;
            }

            mappedByteBuffer.put(data);
//            手动强制落盘会降低效率,有需要的话，可添加配置，当前未添加
//            mappedByteBuffer.force();
        } catch (Exception e){
            log.error("数据写入MappedByteBuffer失败", e);
        }
    }


    /**
     * 服务启动时，将缓存目录中未来得及批量同步到db中的缓存文件 放入canSaveToDbList中
     */
    private void initCanSaveToDbList(){
        try {
            String path = StringUtils.isEmpty(cachePath)? DEFAULT_CACHE_PATH : cachePath;
            log.info("path: {}", path);
            File dir = new File(path);
            if (dir.exists()){
                File[] cacheFiles = dir.listFiles();
                if(cacheFiles.length > 0){
                    for(File cacheFile : cacheFiles){
                        canSaveToDbList.add(cacheFile.getAbsolutePath());
                    }
                }
            }
        }catch (Exception e){
         log.error("服务启动时，将临时文件目录中未来得及批量同步到db中的临时文件 放入canSaveToDbList中操作失败", e);
        }
        log.info("有以下文件待同步到oss： {}", JSONObject.toJSONString(canSaveToDbList));
    }


    /**
     * 定时将缓存文件同步到oss,并删除缓存文件
     */
    private void batchSaveToDb(){
        log.info("定时将临时文件同步到oss,并删除临时文件 , canSaveToDbList = {}", JSONObject.toJSONString(canSaveToDbList));
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            if(CollectionUtils.isNotEmpty(canSaveToDbList)){
                List<String> tempList = new ArrayList<>();
                for(String filePath : canSaveToDbList){
                    File file = new File(filePath);
                    String objectName = StringUtils.join(
                            "collect/" ,
                            DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now()),
                            "/",
                            file.getName()
                    );
                    ossUploadService.uploadOssFile(this.getFileInputStream(file), objectName);
                    deleteCacheFile(filePath);
                    tempList.add(filePath);
                }
                for(String filePath : tempList){
                    canSaveToDbList.remove(filePath);
                }
            }
        }catch (Exception e){
            log.error("定时将cache临时文件中的数据转存如db中操作失败", e);
        }
        stopWatch.stop();
        log.info("删除临时文件耗时： {}", stopWatch.getTotalTimeMillis());
    }

    /**
     * 清除无效内容,获取一个新的InputStream
     * @param file
     * @return
     */
    private InputStream getFileInputStream(File file) {
        ByteArrayInputStream byteArrayInputStream = null;
        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            final int extra = 1024 * 512;
            int count = extra;
            byte[] buf = new byte[count];
            int j=0;
            fileInputStream = new FileInputStream(file);
            inputStreamReader = new InputStreamReader(fileInputStream,"UTF-8");
            bufferedReader = new BufferedReader(inputStreamReader);
            String content;
            while ((content=bufferedReader.readLine())!=null){
                if(!content.trim().equals("")){
                    byte[] bytes = content.getBytes();
                    for(byte b : bytes){
                        buf[j] = b;
                        j++;
                        if(j>=count){
                            count = count + extra;
                            buf = copyOf(buf,count);
                        }
                    }
                    buf[j] = '\n';
                    j++;
                    if(j>=count){
                        count = count + extra;
                        buf = copyOf(buf,count);
                    }
                }
            }
            byteArrayInputStream = new ByteArrayInputStream(buf);
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStreamReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return byteArrayInputStream;
    }

    /**
     * 读取数据并批量写入clickhouse
     * @param mappedByteBuffer
     */
    private void saveToDb(MappedByteBuffer mappedByteBuffer) {
        mappedByteBuffer.rewind();

        List<String> contentList = new ArrayList<>();
        final int extra = 500;
        int count = extra;
        byte[] buf = new byte[count];
        int j=0;
        char ch ='\0';
        boolean flag = false;
        while(mappedByteBuffer.remaining()>0){
            byte by = mappedByteBuffer.get();
            ch =(char)by;
            switch(ch){
                case '\n':
                    flag = true;
                    break;
                default:
                    buf[j] = by;
                    break;
            }
            j++;
            if(!flag && j>=count){
                count = count + extra;
                buf = copyOf(buf,count);
            }
            if(flag){
                String line = new String(buf);
                if(StringUtils.isNotEmpty(line)){
                    contentList.add(line);
                }
                flag = false;
                buf = null;
                count = extra;
                buf = new byte[count];
                j =0;
            }
        }
        //处理最后一次读取
        if(j>0){
            String line = new String(buf);
            if(StringUtils.isNotEmpty(line)){
                contentList.add(line);
            }
        }

//        dataToDbHandler.saveToDb(contentList);
    }

    /**
     * 扩充数组的容量
     * @param original
     * @param newLength
     * @return
     */
    public static byte[] copyOf(byte[] original,int newLength){
        byte[] copy = new byte[newLength];
        System.arraycopy(original,0,copy,0,Math.min(original.length,newLength));
        return copy;
    }

    /**
     * 释放mappedByteBuffer 并关闭缓存文件
     * @param mappedByteBuffer
     */
    private void closeCacheFile(MappedByteBuffer mappedByteBuffer){
//      在关闭资源时执行以下代码释放内存
        Method m = null;
        try {
            m = FileChannelImpl.class.getDeclaredMethod("unmap", MappedByteBuffer.class);
            m.setAccessible(true);
            m.invoke(FileChannelImpl.class, mappedByteBuffer);
        } catch (Exception e) {
            log.error("手动执行unmap失败", e);
        }
    }

    /**
     * 删除临时文件
     * @param cachePath
     */
    private void deleteCacheFile(String cachePath){
        try {
            if(!new File(cachePath).delete()){
                log.error("临时文件删除失败：path: {}", cachePath);
            }else {
                log.info("临时文件删除成功： {}", cachePath);
            }
        } catch (Exception e) {
            log.error("手动执行unmap失败", e);
        }
    }


    @Override
    public void run(ApplicationArguments args) {
        logParameter();
        initCanSaveToDbList();
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> batchSaveToDb(), 1,1,  TimeUnit.MINUTES);
    }

    /**
     * 打印配置信息
     */
    private void logParameter(){
        log.info("cachePath: {}", cachePath);
        log.info("DEFAULT_CACHE_PATH: {}", DEFAULT_CACHE_PATH);
        log.info("cacheFileSize: {}M", cacheFileSize);
        log.info("DEFAULT_CACHE_FILE_SIZE: {}M", DEFAULT_CACHE_FILE_SIZE/1024/1024);
    }

}
