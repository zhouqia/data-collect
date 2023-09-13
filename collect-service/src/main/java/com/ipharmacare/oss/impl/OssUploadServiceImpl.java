package com.ipharmacare.oss.impl;


import com.ipharmacare.collect.common.core.exception.CollectBadRequestException;
import com.ipharmacare.oss.OssUploadService;
import com.ipharmacare.oss.property.TencentCosProperties;
import com.ipharmacare.oss.property.TencentcloudProperties;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.StorageClass;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.auth.COSCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;


@Service
@Slf4j
public class OssUploadServiceImpl implements OssUploadService {

    private static final long NEED_PART_SIZE = 500 * 1024 * 1024L;

    private final TencentcloudProperties tencentcloudProperties;
    private final TencentCosProperties tencentCosProperties;

    @Autowired
    public OssUploadServiceImpl(TencentcloudProperties tencentcloudProperties, TencentCosProperties tencentCosProperties) {
        this.tencentcloudProperties = tencentcloudProperties;
        this.tencentCosProperties = tencentCosProperties;
    }

    /**
     * 上传文件
     *
     * @param inputStream
     * @param objectName
     */
    @Override
    public String uploadOssFile(InputStream inputStream, String objectName) {
        log.info("上传文件...开始: objectName[{}]", objectName);
        String uri;
        try {
            COSCredentials cred = new BasicCOSCredentials(tencentcloudProperties.getSecretId(), tencentcloudProperties.getSecretKey());
            Region region = new Region(tencentCosProperties.getRegion());
            ClientConfig clientConfig = new ClientConfig(region);
            clientConfig.setHttpProtocol(HttpProtocol.https);
            COSClient cosClient = new COSClient(cred, clientConfig);
            long available = inputStream.available();
            if (available < NEED_PART_SIZE) {
                ObjectMetadata meta = new ObjectMetadata();
                meta.setContentLength(available);
                PutObjectRequest putObjectRequest = new PutObjectRequest(tencentCosProperties.getBucketName(), objectName, inputStream, meta);
                putObjectRequest.setMetadata(meta);
                putObjectRequest.setStorageClass(StorageClass.Standard_IA);
                cosClient.putObject(putObjectRequest);
                cosClient.shutdown();
                uri = tencentCosProperties.getAccessDomain() + objectName;
            } else {
                throw new CollectBadRequestException("文件过大");
            }
            log.info("上传文件...结束: uri[{}]", uri);
            return uri;
        } catch (IOException e) {
            throw new RuntimeException(String.format("uploadOssFile error : bucket -> %s", tencentCosProperties.getBucketName()), e);
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void delFile(String url) {
        //初始化用户身份信息
        String secretId = tencentcloudProperties.getSecretId();
        String secretKey = tencentcloudProperties.getSecretKey();
        String bucketName = tencentCosProperties.getBucketName();

        COSCredentials cred = new BasicCOSCredentials(secretId,secretKey);
        //设置bucket信息
        Region region = new Region(tencentCosProperties.getRegion());

        ClientConfig clientConfig = new ClientConfig(region);
        //生产cos客户端
        COSClient cosClient = new COSClient(cred,clientConfig);
        cosClient.deleteObject(bucketName,url);
        cosClient.shutdown();
    }




}