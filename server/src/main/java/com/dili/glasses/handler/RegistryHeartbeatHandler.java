package com.dili.glasses.handler;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 注册中心心跳处理器
 * @author wangmi
 */
public class RegistryHeartbeatHandler extends SimpleChannelInboundHandler<String> {

    private final static Logger LOGGER = LoggerFactory.getLogger(RegistryHeartbeatHandler.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt ;
            if (idleStateEvent.state() == IdleState.WRITER_IDLE){
                LOGGER.info("已经 20 秒没有发送信息！");
                //向服务端发送消息
                ctx.writeAndFlush("ping").addListener(ChannelFutureListener.CLOSE_ON_FAILURE) ;
            }
        }
        super.userEventTriggered(ctx, evt);
    }


    /**
     * 注册中心宕机会触发此函数
     * @param ctx
     * @return: void
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
//        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
//        String registryIp = insocket.getAddress().getHostAddress();
//        ChannelId registryChannelId = ctx.channel().id();
        LOGGER.error("Server端断开与注册中心连接");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        //从服务端收到消息时被调用
        LOGGER.info("客户端收到消息={}",msg);
//        try {
//            Thread.sleep(11000L);
//        } catch (InterruptedException e) {
//        }
//        //向服务端发送消息
//        HeartbeatProtocol heartBeat = SpringUtil.getBean("heartBeat", HeartbeatProtocol.class);
//        channelHandlerContext.writeAndFlush(heartBeat);
    }
}
