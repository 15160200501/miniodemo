package com.example.config;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Description :
 * Created by huming on 2020/09/07 10:55
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {

    @ApiModelProperty("endPoint是一个URL，域名，IPv4或者IPv6地址")
    private String endpoint;

    @ApiModelProperty("accessKey类似于用户ID，用于唯一标识你的账户")
    private String accessKey;

    @ApiModelProperty("secretKey是你账户的密码")
    private String secretKey;

    @ApiModelProperty("默认存储桶")
    private String bucketName;

}
