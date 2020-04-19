package com.dili.registry.handler;

import com.dili.registry.domain.BaseProtocol;
import com.dili.registry.factory.ProtocolDealFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 注册中心处理器
 * 处理心跳、服务端注册和分配服务端等命令
 *
 * @author wangmi
 */
@Component
@ChannelHandler.Sharable
public class RegistryTerminalHandler extends ChannelHandlerAdapter {

    private final static Logger LOGGER = LoggerFactory.getLogger(RegistryTerminalHandler.class);

    @Autowired
    ProtocolDealFactory protocolDealFactory;

    /**
     * 取消绑定
     *
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
//        String key = NettyConstant.TERMINAL_CHANNEL_MAP.get(ctx.channel().id());
//        LOGGER.info("Server端["+key+"]断开连接");
//        //服务端断开时，清空计数
//        if (NettyConstant.SERVER_CACHE.containsKey(key)) {
//            NettyConstant.SERVER_CACHE.put(key, new AtomicInteger(0));
//        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                LOGGER.info("注册中心已经60秒没有收到信息，断开连接！");
                ctx.disconnect();
                //向客户端发送消息 TODO
//                ctx.writeAndFlush("").addListener(ChannelFutureListener.CLOSE_ON_FAILURE) ;
            }
        } else {
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
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object cmd) throws Exception {
        LOGGER.info("收到命令={}", cmd);
        //保存客户端与 Channel 之间的关系
        //当有多个客户端连上来时，服务端需要区分开，不然响应消息就会发生混乱。
        //所以每当有个连接上来的时候，我们都将当前的 Channel 与连上的客户端 ID 进行关联（因此每个连上的客户端 ID 都必须唯一）。
        //这里采用了一个 Map 来保存这个关系，并且在断开连接时自动取消这个关联。
        protocolDealFactory.exec(channelHandlerContext, (BaseProtocol) cmd);
    }
}