package com.dili.client.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端处理类
 * @author wangmi
 **/
public class ClientHandler extends ChannelHandlerAdapter {
    protected static final Logger log = LoggerFactory.getLogger(ClientHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("ClientHandler Active");
    }

    /**
     * 有服务端端终止连接服务器会触发此函数
     * @param ctx
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        ctx.close();
        log.info("服务端终止了服务");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        log.info("客户端收到服务端消息:" + msg);
//        try {
//            ByteBuf buf = (ByteBuf) msg;
//            byte[] data = new byte[buf.readableBytes()];
//            buf.readBytes(data);
//            System.out.println("客户端收到数据：" + new String(data).trim());
//        } finally {
//            ReferenceCountUtil.release(msg);
//        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.info("客户端发生异常【" + cause.getMessage() + "】");
        ctx.close();
    }

}