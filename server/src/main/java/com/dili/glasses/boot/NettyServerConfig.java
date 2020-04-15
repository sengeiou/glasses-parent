package com.dili.glasses.boot;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取properties配置文件中的信息
 *
 * @author wangmi
 */
@Component
@ConfigurationProperties(prefix = "netty.server")
@Data
public class NettyServerConfig {

    /**
     * 服务器端口号
     */
    private int port;

    /**
     * 服务器地址
     */
    private String host;

    /**
     * 一段时间内没有数据读取触发
     */
    private Integer readerIdleTimeSeconds;

    /**
     * 一段时间内没有数据发送触发
     */
    private Integer writerIdleTimeSeconds;

    /**
     * 以上两种满足其中一个即可
     */
    private Integer allIdleTimeSeconds;
}
