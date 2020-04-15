package com.dili.glasses.factory.strategy.protocol;

import com.dili.glasses.consts.NettyConstant;
import com.dili.glasses.domain.receive.LogoutReceiveProtocol;
import com.dili.glasses.domain.send.SimpleSendProtocol;
import com.dili.glasses.service.TerminalService;
import com.dili.glasses.utils.MessageDelaySender;
import com.dili.ss.redis.service.RedisUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * @author Ren HongWei
 * @date 2019-09-02 22:14
 * @description
 **/
@Slf4j
@Component
public class LogoutStrategy extends BaseStrategy<LogoutReceiveProtocol> {

    private final RedisUtil redisUtil;

    private final TerminalService terminalService;

    private final MessageDelaySender messageDelaySender;

    @Autowired
    public LogoutStrategy(RedisUtil redisUtil, TerminalService terminalService, MessageDelaySender messageDelaySender) {
        super(messageDelaySender);
        this.redisUtil = redisUtil;
        this.terminalService = terminalService;
        this.messageDelaySender = messageDelaySender;
    }

    @Override
    public void exec(ChannelHandlerContext context, LogoutReceiveProtocol protocol) {
        InetSocketAddress inSocket = (InetSocketAddress) context.channel().remoteAddress();
        String clientIp = inSocket.getAddress().getHostAddress();

        NettyConstant.TERMINAL_CHANNEL_ID_MAP.remove(protocol.getTerminalId());
        NettyConstant.CHANNEL_ID_TERMINAL_MAP.remove(context.channel().id());
        String key = NettyConstant.TERMINAL_KEY + protocol.getTerminalId();
        //清空redis缓存, key为终端号
        redisUtil.set(key, null);

        //更新设备在线状态
        terminalService.updateOnlineStatus(protocol.getTerminalId(), false);

        log.info("客户端【" + context.channel().id() + "】退出Server,终端地址[" + clientIp + ":" + inSocket.getPort() + "]");
        log.info("连接通道数量: " + NettyConstant.CHANNEL_MAP.size());
        response(context, protocol);
    }


    /**
     * 响应客户端
     *
     * @param context         信道上下文
     * @param requestProtocol 请求内容
     */
    private void response(ChannelHandlerContext context, LogoutReceiveProtocol requestProtocol) {
        SimpleSendProtocol logoutResponseProtocol = new SimpleSendProtocol(requestProtocol.getTerminalId(), requestProtocol.getCmd(), true);
        channelWrite(context.channel().id(), logoutResponseProtocol.toByteArray());
    }

    @Override
    public Class<LogoutReceiveProtocol> getRequestProtocolClass() {
        return LogoutReceiveProtocol.class;
    }
}
