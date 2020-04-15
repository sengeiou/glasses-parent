package com.dili.registry.consts;

import org.springframework.stereotype.Component;

/**
 * Netty命令
 * @author wangmi
 */
@Component
public class NettyCMD {
    /**
     * 注册服务端命令
     */
    public final static String REGISTER = "register";
    /**
     * 分配服务端命令
     */
    public final static String ALLOCATE = "allocate";
    /**
     * 注册中心响应完成注册命令
     */
    public final static String REGISTERED = "registered";

    /**
     * 服务端拼命令
     */
    public final static String PING = "ping";
}
