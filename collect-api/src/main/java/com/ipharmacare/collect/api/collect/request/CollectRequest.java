package com.ipharmacare.collect.api.collect.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("数据采集请求对象")
public class CollectRequest {

    @ApiModelProperty(value = "数据端1、2、3...")
    private String sourceType;

    @ApiModelProperty(value = "业务类型1、2、3...")
    private String dataType;

//    @ApiModelProperty(value = "系统标识，由采集系统分配的key")
//    private String systemKey;

    @ApiModelProperty(value = "是否加密，1是、0否，默认否")
    private Integer isEncryption = 0;

    /**
     * 采集时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern= "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "数据生成时间")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "加密数据集合")
    private List<String> encryptionList;

}
