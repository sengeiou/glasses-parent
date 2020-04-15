package com.dili.client.handler;

import com.dili.client.consts.NettyConstant;
import com.dili.client.domain.ApplyAddressProtocol;
import com.dili.client.domain.GlassesProtocol;
import com.dili.client.domain.Protocol;
import com.dili.client.factory.ProtocolConvertFactory;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 获取服务端地址处理类
 * @author wangmi
 **/
public class FindServerHandler extends ChannelHandlerAdapter {
    protected static final Logger log = LoggerFactory.getLogger(FindServerHandler.class);

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
        log.info("客户端收到服务端地址:" + msg);
//        String[] address = ((String)msg).split(":");
//        NettyConstant.serverHost = address[0];
//        NettyConstant.serverPort = Integer.parseInt(address[1]);

        Protocol protocol = ProtocolConvertFactory.convert((GlassesProtocol)msg);
        if(protocol instanceof ApplyAddressProtocol){
            ApplyAddressProtocol applyAddressProtocol = (ApplyAddressProtocol)protocol;
            applyAddressProtocol.getHostPort();
            //TODO 解析host:port
//            new Thread(new NettyClient(host, port)).start();
        }

        //收到服务端地址后，断开与注册中心的连接
        ctx.channel().close();
        //TODO 连接服务端
//        new Thread(new NettyClient(address[0], Integer.parseInt(address[1]))).start();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.info("客户端发生异常【" + cause.getMessage() + "】");
        ctx.close();
    }

}