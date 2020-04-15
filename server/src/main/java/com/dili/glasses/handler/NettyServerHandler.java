package com.dili.glasses.handler;

import com.dili.glasses.boot.NettyServerConfig;
import com.dili.glasses.consts.NettyConstant;
import com.dili.glasses.domain.GlassesProtocol;
import com.dili.glasses.factory.ProtocolDealFactory;
import com.dili.glasses.service.TerminalService;
import com.dili.ss.redis.service.RedisUtil;
import com.dili.ss.util.DateUtils;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Date;

/**
 * netty服务端和终端通信类
 *
 * @author wangmi
 **/
@Component
@ChannelHandler.Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler {
    protected static final Logger log = LoggerFactory.getLogger(NettyServerHandler.class);

    private final NettyServerConfig nettyServerConfig;

    private final ProtocolDealFactory protocolDealFactory;

    private final RedisUtil redisUtil;

    private final TerminalService terminalService;

    @Autowired
    public NettyServerHandler(ProtocolDealFactory protocolDealFactory, NettyServerConfig nettyServerConfig, RedisUtil redisUtil, TerminalService terminalService) {
        this.protocolDealFactory = protocolDealFactory;
        this.nettyServerConfig = nettyServerConfig;
        this.redisUtil = redisUtil;
        this.terminalService = terminalService;
    }

    /**
     * 有客户端连接服务器会触发此函数
     * 保存channelId对应的连接到NettyConstant.CHANNEL_MAP
     *
     * @param ctx 上线文信息
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = insocket.getAddress().getHostAddress();
        int clientPort = insocket.getPort();
        //获取连接通道唯一标识
        ChannelId channelId = ctx.channel().id();
        //如果map中不包含此连接，就保存连接
        if (NettyConstant.CHANNEL_MAP.containsKey(channelId)) {
            log.info("客户端【" + channelId + "】是连接状态，连接通道数量: " + NettyConstant.CHANNEL_MAP.size());
        } else {
            //保存ChannelId与连接上下文的关系
            NettyConstant.CHANNEL_MAP.put(channelId, ctx);
            log.info("客户端【" + channelId + "】连接netty服务器[IP:" + clientIp + "--->PORT:" + clientPort + "]");
            log.info("连接通道数量: " + NettyConstant.CHANNEL_MAP.size());
        }
    }

    /**
     * 有客户端终止连接服务器会触发此函数
     * 从NettyConstant.CHANNEL_MAP清除channelId对应的连接
     *
     * @param ctx
     * @return: void
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        offline(ctx);
    }

    /**
     * 有客户端发消息会触发此函数
     *
     * @param ctx 上线文
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("加载客户端报文......");
        log.info("【" + ctx.channel().id() + "】" + " :" + msg.toString());

        protocolDealFactory.exec(ctx, (GlassesProtocol) msg);

        log.info("response time:" + DateUtils.format(new Date()));
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
        log.info(ctx.channel().id() + " 发生了错误,此连接被关闭" + "此时连通数量: " + NettyConstant.CHANNEL_MAP.size());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        //心跳处理
        String socketString = ctx.channel().remoteAddress().toString();
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                log.info("Client: " + socketString + " READER_IDLE 读超时");
                offline(ctx);
                ctx.disconnect();
            } else if (event.state() == IdleState.WRITER_IDLE) {
                log.info("Client: " + socketString + " WRITER_IDLE 写超时");
                offline(ctx);
                ctx.disconnect();
            } else if (event.state() == IdleState.ALL_IDLE) {
                log.info("Client: " + socketString + " ALL_IDLE 总超时");
                offline(ctx);
                ctx.disconnect();
            }
        }
    }

    /**
     * 设备下线
     * 清空NettyConstant.CHANNEL_MAP中ChannelId对应的连接
     * 重置redis中key为:NettyConstant.TERMINAL_KEY+终端号 的value
     *
     * @param ctx 连接上线文
     */
    private void offline(ChannelHandlerContext ctx) {
        InetSocketAddress inSocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = inSocket.getAddress().getHostAddress();
        ChannelId channelId = ctx.channel().id();
        //删除ChannelId与连接上下文的缓存
        NettyConstant.CHANNEL_MAP.remove(channelId);

        //设置redis中的设备信息为空
        Integer terminalId = NettyConstant.CHANNEL_ID_TERMINAL_MAP.get(channelId);
        if (terminalId != null) {
            redisUtil.set(NettyConstant.TERMINAL_KEY + terminalId, null);
            //更新设备在线状态
            terminalService.updateOnlineStatus(terminalId, false);
            NettyConstant.TERMINAL_CHANNEL_ID_MAP.remove(terminalId);
        }

        //删除ChannelId和终端号的缓存
        NettyConstant.CHANNEL_ID_TERMINAL_MAP.remove(channelId);
        NettyConstant.CHANNEL_MAP.remove(channelId);

        log.info("客户端【" + channelId + "】退出Server,终端地址[" + clientIp + ":" + inSocket.getPort() + "]");
        log.info("连接通道数量: " + NettyConstant.CHANNEL_MAP.size());
    }

}