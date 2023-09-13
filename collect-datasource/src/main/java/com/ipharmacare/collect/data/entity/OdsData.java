package com.ipharmacare.collect.data.entity;

import lombok.Data;


/**
 * @Author: zhouqiang
 * @Date: 2023/6/1 13:52
 */
@Data
public class OdsData {

    /**
     * uuid 唯一标识
     */
    private String uuid;

    /**
     * 是否为密文  1是、0否，默认否
     */
    private Integer isEncryption;

    /**
     * 来源端标识(某个前端(比如yzh_wechat,yzh_h5,yzh_app)，某个后端(yzh-center,yzh_csp,yzh_patient))
     */
    private String sourceType;

    /**
     * 业务类型(根据业务类型 解析数据)
     */
    private String dataType;

    /**
     * 采集时间时间戳
     */
    private Long recordTime;

    /**
     * 存表时间 时间戳
     */
    private Long createTime;

    /**
     * 采集的内容字符串
     */
    private String content;



    public static void main(String[] args) {
        System.out.print(System.getProperty("line.separator"));
    }
}
