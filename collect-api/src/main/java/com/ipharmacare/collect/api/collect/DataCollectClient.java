package com.ipharmacare.collect.api.collect;


import com.ipharmacare.collect.api.base.BaseResp;
import com.ipharmacare.collect.api.collect.request.CollectRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author:
 * @Date:
 */
@FeignClient(name = "server-behavior-data-collect", contextId = "serverBehaviorDataCollect" + "CollectClient")
public interface DataCollectClient {


    /**
     * 数据采集
     * @param req
     * @return
     */
    @PostMapping({"/data/collect"})
    BaseResp collect(@RequestBody CollectRequest req);
}
