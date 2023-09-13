package com.ipharmacare.collect.service.collect.handle;

import com.alibaba.fastjson.JSONObject;
import com.ipharmacare.collect.data.entity.OdsData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.concurrent.*;


/**
 * @Author: zhouqiang
 * @Date: 2023/5/30 15:27
 */
@Component
@Slf4j
//@Async("asyncToLocalExecutor")
public class DataToLocalHandler {

    @Autowired
    private DataHandlerHelper dataHandlerHelper;

    /**
     * 自定义线程池,随时观察线程池内部情况
     */
    private static ExecutorService aThreadPool = new ThreadPoolExecutor(
            1,
            1,
            10L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(),
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "asyncToLocal-" + r.hashCode());
                }
            }){
        @Override
        public void execute(Runnable command) {
            super.execute(() -> {
                try {
                    log.info("线程池内部情况:  核心线程数: {}, 等待队列长度: {}", super.getCorePoolSize(), super.getQueue().size());
                    command.run();
                } finally {
                }
            });
        }
    };

    /**
     * 信息写入本地临时文件
     * @param odsData
     */
    public void save(OdsData odsData){
        aThreadPool.execute(() ->{
            String content = StringUtils.join(JSONObject.toJSONString(odsData), "\n");
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            dataHandlerHelper.writeToLocal(content);
            stopWatch.stop();
            log.info("一条记录写入本地临时文件耗时： {}", stopWatch.getTotalTimeMillis());
        });
    }



}
