package com.ipharmacare.oss.property;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@NoArgsConstructor
@Getter
@Setter
@ConfigurationProperties(prefix = "tencentcloud.cos")
public class TencentCosProperties {
    private String region;
    private String bucketName;
    private String accessDomain;
}