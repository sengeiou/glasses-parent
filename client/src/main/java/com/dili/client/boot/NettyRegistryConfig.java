package com.dili.client.boot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取properties配置文件中的信息
 * @author wangmi
 */
@Component
@ConfigurationProperties(prefix = "netty.registry")
public class NettyRegistryConfig {
    //注册中心服务id
    @Value("${port:12121}")
    private Integer port;
    //注册中心服务端口
    @Value("${host:127.0.0.1}")
    private String host;


    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

}
