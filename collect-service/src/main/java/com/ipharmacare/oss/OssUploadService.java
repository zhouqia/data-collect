package com.ipharmacare.oss;

/**
 * @Author: zhouqiang
 * @Date: 2023/6/7 12:28
 */


import java.io.InputStream;


public interface OssUploadService {

    /**
     * 上传数据文件
     * @param inputStream
     * @param objectName
     * @return
     */
    String uploadOssFile(InputStream inputStream, String objectName);


    /**
     * 删除腾讯桶中的文件
     * @param url
     */
    void delFile(String url);



}
