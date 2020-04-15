package com.dili.glasses.boot;

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
    private Integer id;
    //注册中心服务id
    @Value("${port:12121}")
    private Integer port;
    //注册中心服务端口
    @Value("${host:127.0.0.1}")
    private String host;
    //一段时间内没有数据读取触发
    @Value("${readerIdleTimeSeconds:0}")
    private Integer readerIdleTimeSeconds;
    //一段时间内没有数据发送触发
    @Value("${readerIdleTimeSeconds:0}")
    private Integer writerIdleTimeSeconds;
    //以上两种满足其中一个即可
    @Value("${readerIdleTimeSeconds:0}")
    private Integer allIdleTimeSeconds;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public Integer getReaderIdleTimeSeconds() {
        return readerIdleTimeSeconds;
    }

    public void setReaderIdleTimeSeconds(Integer readerIdleTimeSeconds) {
        this.readerIdleTimeSeconds = readerIdleTimeSeconds;
    }

    public Integer getWriterIdleTimeSeconds() {
        return writerIdleTimeSeconds;
    }

    public void setWriterIdleTimeSeconds(Integer writerIdleTimeSeconds) {
        this.writerIdleTimeSeconds = writerIdleTimeSeconds;
    }

    public Integer getAllIdleTimeSeconds() {
        return allIdleTimeSeconds;
    }

    public void setAllIdleTimeSeconds(Integer allIdleTimeSeconds) {
        this.allIdleTimeSeconds = allIdleTimeSeconds;
    }
}
