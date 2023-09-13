package com.ipharmacare.oss.property;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@NoArgsConstructor
@Getter
@Setter
@ConfigurationProperties(prefix = "tencentcloud")
public class TencentcloudProperties {
    private String appId;
    private String secretId;
    private String secretKey;
}
