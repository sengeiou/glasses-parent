package com.dili.registry.handler;

import com.dili.registry.boot.RegistryNettyConfig;
import com.dili.registry.consts.NettyConstant;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;

/**
 * netty服务端处理类
 *
 * @author wangmi
 **/
@Component
@ChannelHandler.Sharable
public class RegistryNettyServerHandlerBak extends ChannelHandlerAdapter {
    protected static final Logger log = LoggerFactory.getLogger(RegistryNettyServerHandlerBak.class);
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
     * 注册中心连接配置
     */
    @Autowired
    private RegistryNettyConfig registryNettyConfig;

    /**
     * 有客户端连接服务器会触发此函数
     *
     * @param ctx
     * @return: void
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {

        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = insocket.getAddress().getHostAddress();
        int clientPort = insocket.getPort();

        //获取连接通道唯一标识
        ChannelId channelId = ctx.channel().id();

        System.out.println();
        //如果map中不包含此连接，就保存连接
        if (NettyConstant.SERVER_CHANNEL_MAP.containsKey(channelId)) {
            log.info("客户端【" + channelId + "】是连接状态，连接通道数量: " + NettyConstant.SERVER_CHANNEL_MAP.size());
        } else {
            //保存连接
            NettyConstant.SERVER_CHANNEL_MAP.put(channelId, ctx);
            log.info("客户端【" + channelId + "】连接注册中心[IP:" + clientIp + "--->PORT:" + clientPort + "]");
            log.info("连接通道数量: " + NettyConstant.SERVER_CHANNEL_MAP.size());
        }
    }

    /**
     * 有客户端终止连接服务器会触发此函数
     *
     * @param ctx
     * @return: void
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = insocket.getAddress().getHostAddress();
        ChannelId channelId = ctx.channel().id();
        //包含此客户端才去删除
        if (NettyConstant.SERVER_CHANNEL_MAP.containsKey(channelId)) {
            //删除连接
            NettyConstant.SERVER_CHANNEL_MAP.remove(channelId);
            System.out.println();
            log.info("客户端【" + channelId + "】退出注册中心[IP:" + clientIp + "--->PORT:" + insocket.getPort() + "]");
            log.info("连接通道数量: " + NettyConstant.SERVER_CHANNEL_MAP.size());
        }
    }

    /**
     * 有客户端发消息会触发此函数
     *
     * @param ctx
     * @return: void
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println();
        log.info("加载客户端报文......");
        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        log.info("channel address【" + address.getHostName() + ":" + address.getPort() + "】");
        log.info("channel id:【" + ctx.channel().id() + "】" + " msg:" + msg);
        //下面可以解析数据，保存数据，生成返回报文，将需要返回报文写入write函数
        //响应客户端
        this.channelWrite(ctx.channel().id(), msg);
    }

    /**
     * 服务端给客户端发送消息
     *
     * @param msg       需要发送的消息内容
     * @param channelId 连接通道唯一id
     * @return: void
     */
    public void channelWrite(ChannelId channelId, Object msg) throws Exception {
        ChannelHandlerContext ctx = NettyConstant.SERVER_CHANNEL_MAP.get(channelId);
        if (ctx == null) {
            log.info("通道【" + channelId + "】不存在");
            return;
        }
        if (msg == null && msg == "") {
            log.info("服务端响应空的消息");
            return;
        }
        //将客户端的信息直接返回写入ctx
        ctx.write(msg);
        //刷新缓存区
        ctx.flush();
    }

    /**
     * 发生异常会触发此函数
     *
     * @param ctx
     * @return: void
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        log.info(ctx.channel().id() + " 发生了错误,此连接被关闭" + "此时连通数量: " + NettyConstant.SERVER_CHANNEL_MAP.size() + "错误信息：" + cause.getMessage());
        //cause.printStackTrace();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        String socketString = ctx.channel().remoteAddress().toString();
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                log.info("Client: " + socketString + " READER_IDLE 读超时");
                ctx.disconnect();
            } else if (event.state() == IdleState.WRITER_IDLE) {
                log.info("Client: " + socketString + " WRITER_IDLE 写超时");
                ctx.disconnect();
            } else if (event.state() == IdleState.ALL_IDLE) {
                log.info("Client: " + socketString + " ALL_IDLE 总超时");
                ctx.disconnect();
            }
        }
    }

}