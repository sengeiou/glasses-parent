package com.dili.registry.handler;

import com.dili.registry.consts.NettyCMD;
import com.dili.registry.consts.NettyConstant;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 注册中心处理器
 * 处理心跳、服务端注册和分配服务端等命令
 * @author wangmi
 */
@Component
@ChannelHandler.Sharable
public class RegistryServerHandler extends SimpleChannelInboundHandler<String> {

    private final static Logger LOGGER = LoggerFactory.getLogger(RegistryServerHandler.class);

    /**
     * 心跳固定值: pone
     */
    private ByteBuf HEART_BEAT;

    {
        try {
            HEART_BEAT = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("pong".getBytes(NettyConstant.CHARSET)));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消绑定
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
//        NettySocketHolder.remove((NioSocketChannel) ctx.channel());
        //服务端断开时清除缓存
//        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
//        String clientIp = inetSocketAddress.getAddress().getHostAddress();
//        int clientPort = inetSocketAddress.getPort();
//        String key = new StringBuilder(clientIp).append(":").append(clientPort).toString();
        //根据channelId获取服务端地址
        String key = NettyConstant.TERMINAL_CHANNEL_MAP.get(ctx.channel().id());
        LOGGER.info("Server端["+key+"]断开连接");
        //服务端断开时，清空计数
        if (NettyConstant.SERVER_CACHE.containsKey(key)) {
            //NettyConstant.SERVER_CACHE.put(key, new AtomicInteger(0));
            NettyConstant.SERVER_CACHE.remove(key);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt ;
            if (idleStateEvent.state() == IdleState.READER_IDLE){
                LOGGER.info("已经60秒没有收到信息！");
                //向客户端发送消息
//                ctx.writeAndFlush(HEART_BEAT).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                ctx.disconnect();
            }
        }else {
            super.userEventTriggered(ctx, evt);
        }
//        if (evt instanceof IdleStateEvent) {
//            if (((IdleStateEvent)evt).state() == IdleState.READER_IDLE) {
//                ChannelFuture channelFuture = ctx.writeAndFlush("Time out,You will close");
//                channelFuture.addListener(new ChannelFutureListener() {
//                    @Override
//                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
//                        ctx.channel().close();
//                    }
//                });
//            }
//        } else {
//            super.userEventTriggered(ctx, evt);
//        }
    }

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, String cmd) throws Exception {
        LOGGER.info("收到命令={}", cmd);
        //保存客户端与 Channel 之间的关系
        //当有多个客户端连上来时，服务端需要区分开，不然响应消息就会发生混乱。
        //所以每当有个连接上来的时候，我们都将当前的 Channel 与连上的客户端 ID 进行关联（因此每个连上的客户端 ID 都必须唯一）。
        //这里采用了一个 Map 来保存这个关系，并且在断开连接时自动取消这个关联。
//        NettySocketHolder.put(heartbeatProtocol.getId(),(NioSocketChannel)channelHandlerContext.channel());

        //处理服务端的注册和PING命令
        if(cmd.startsWith(NettyCMD.REGISTER)) {
            //解析Server端对终端公开的端口
            int port = Integer.parseInt(cmd.substring(cmd.lastIndexOf(" ")+1));
            //缓存服务端和连接数
            registerServer(channelHandlerContext, port);
        }else if(NettyCMD.PING.equals(cmd)){
            channelHandlerContext.writeAndFlush(HEART_BEAT);
        }
    }

    /**
     * 注册服务端和连接数
     * @param channelHandlerContext
     */
    private void registerServer(ChannelHandlerContext channelHandlerContext, int port){
        InetSocketAddress inetSocketAddress = (InetSocketAddress) channelHandlerContext.channel().remoteAddress();
        String clientIp = inetSocketAddress.getAddress().getHostAddress();
        //Server端对终端公布的地址
        String key = new StringBuilder(clientIp).append(":").append(port).toString();
        //保存channelId和服务端地址的关系，用于设备连接和断开时的计数
        if(!NettyConstant.TERMINAL_CHANNEL_MAP.contains(channelHandlerContext.channel().id())){
            NettyConstant.TERMINAL_CHANNEL_MAP.put(channelHandlerContext.channel().id(), key);
        }
        //重置Server地址的连接数为0
        NettyConstant.SERVER_CACHE.put(key, new AtomicInteger(0));
        LOGGER.info(new StringBuilder().append("Server[").append(key).append("]已经连接到注册中心").toString());
        channelHandlerContext.writeAndFlush(NettyCMD.REGISTERED+":"+key);
    }

}