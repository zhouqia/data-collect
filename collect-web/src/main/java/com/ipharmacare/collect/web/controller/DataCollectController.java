package com.ipharmacare.collect.web.controller;

import com.ipharmacare.collect.api.base.BaseResp;
import com.ipharmacare.collect.api.base.Resps;
import com.ipharmacare.collect.api.collect.request.CollectRequest;
import com.ipharmacare.collect.service.collect.CollectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@RestController
@Api(tags = "数据采集")
@RequestMapping("/data")
@Slf4j
public class DataCollectController {

    @Autowired
    private CollectService collectService;


    @ApiOperation(value = "用户数据采集", notes = "用户数据采集")
    @PostMapping("/collect")
    public BaseResp collect(@RequestBody CollectRequest req){
        BaseResp dataResp = Resps.newBaseResp();
        log.info("req:{}",req);
        collectService.collect(req);
        return dataResp;
    }

    /**
     * 前端框架限制，只能传参字符串
     * @param string
     * @return
     */
    @ApiOperation(value = "用户数据采集", notes = "用户数据采集")
    @PostMapping("/collectString")
    public BaseResp collectString(@RequestParam String string){
        BaseResp dataResp = Resps.newBaseResp();
        log.info("req:{}",string);
        collectService.collectString(string);
        return dataResp;
    }




}
