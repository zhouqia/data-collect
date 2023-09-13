package com.ipharmacare.collect.service.collect.impl;

import com.alibaba.fastjson.JSONObject;
import com.ipharmacare.collect.api.collect.request.CollectRequest;
import com.ipharmacare.collect.data.entity.OdsData;
import com.ipharmacare.collect.service.collect.CollectService;
import com.ipharmacare.collect.service.collect.handle.DataToLocalHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.ZoneOffset;
import java.util.UUID;


/**
 * @Author: zhouqiang
 * @Date: 2023/5/30 11:37
 */
@Service
public class CollectServiceImpl implements CollectService {


    @Autowired
    private DataToLocalHandler dataToLocalHandler;


    @Override
    public void collect(CollectRequest req) {
        if(req == null || CollectionUtils.isEmpty(req.getEncryptionList())){
            return;
        }
        for (String encryption : req.getEncryptionList()) {
            OdsData odsData = new OdsData();
            odsData.setUuid(UUID.randomUUID().toString().replace("-",""));
            odsData.setIsEncryption(req.getIsEncryption() != null ? req.getIsEncryption() : 0);
            odsData.setSourceType(req.getSourceType());
            odsData.setDataType(req.getDataType());
            if(req.getStartTime() != null){
                odsData.setRecordTime(req.getStartTime().toInstant(ZoneOffset.ofHours(8)).toEpochMilli());
            }
            odsData.setCreateTime(System.currentTimeMillis());
            odsData.setContent(encryption);
            dataToLocalHandler.save(odsData);
        }
    }

    @Override
    public void collectString(String string) {
        if(StringUtils.isEmpty(string.trim())){
            return;
        }
        CollectRequest req = JSONObject.parseObject(string, CollectRequest.class);
        this.collect(req);
    }


}
