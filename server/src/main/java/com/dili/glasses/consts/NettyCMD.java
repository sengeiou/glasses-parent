package com.dili.glasses.consts;

import org.springframework.stereotype.Component;

/**
 * Netty 命令
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
}
