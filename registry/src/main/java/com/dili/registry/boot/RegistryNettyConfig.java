package com.dili.registry.boot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取properties配置文件中的信息
 *
 * @author wangmi
 */
@Component
@ConfigurationProperties(prefix = "netty.registry")
public class RegistryNettyConfig {

    /**
     * 注册中心服务id
     */
    private Integer id;

    /**
     * 服务器IP
     */
    private String serverHost;

    /**
     * 注册中心Server服务端口
     */
    private Integer serverPort;

    /**
     * 注册中心终端服务端口
     */
    private Integer terminalPort;

    /**
     * 一段时间内没有数据读取触发
     */
    @Value("${readerIdleTimeSeconds:0}")
    private Integer readerIdleTimeSeconds;

    /**
     * 一段时间内没有数据发送触发
     */
    @Value("${writerIdleTimeSeconds:0}")
    private Integer writerIdleTimeSeconds;

    /**
     * 以上两种满足其中一个即可
     */
    @Value("${allIdleTimeSeconds:0}")
    private Integer allIdleTimeSeconds;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public Integer getTerminalPort() {
        return terminalPort;
    }

    public void setTerminalPort(Integer terminalPort) {
        this.terminalPort = terminalPort;
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
