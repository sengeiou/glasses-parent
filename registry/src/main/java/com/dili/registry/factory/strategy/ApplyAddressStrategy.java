package com.dili.registry.factory.strategy;

import com.dili.registry.boot.RegistryNettyConfig;
import com.dili.registry.consts.NettyConstant;
import com.dili.registry.domain.BaseProtocol;
import com.dili.registry.domain.request.ApplyAddressRequestProtocol;
import com.dili.registry.domain.response.ApplyAddressResponseProtocol;
import com.dili.ss.util.ByteArrayUtils;
import com.dili.ss.util.DateUtils;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ren HongWei
 * @date 2019-09-10 10:47
 * @description
 **/
@Service
@Slf4j
public class ApplyAddressStrategy implements ProtocolExecStrategy<BaseProtocol> {

    private final RegistryNettyConfig registryNettyConfig;

    @Autowired
    public ApplyAddressStrategy(RegistryNettyConfig registryNettyConfig) {
        this.registryNettyConfig = registryNettyConfig;
    }

    @Override
    public void exec(ChannelHandlerContext context, BaseProtocol protocol) {
        //如果当前没有Server端连接，则无法分配服务端地址
        if (NettyConstant.SERVER_CACHE.isEmpty()) {
            return;
        }
        //最小连接数的Server的连接数
        int min = 99999;
        //最小连接数的Server的host:port
        String address = null;
        for (Map.Entry<String, AtomicInteger> entry : NettyConstant.SERVER_CACHE.entrySet()) {
            if (entry.getValue().get() < min) {
                min = entry.getValue().get();
                address = entry.getKey();
            }
        }
        if (address == null) {
            return;
        }

        ApplyAddressRequestProtocol requestProtocol = new ApplyAddressRequestProtocol();
        requestProtocol.parseDatas(protocol);
        ApplyAddressResponseProtocol responseProtocol = new ApplyAddressResponseProtocol();
        responseProtocol.setType(requestProtocol.getType());
        responseProtocol.setImei(requestProtocol.getImei());
        responseProtocol.setHost(address);
        responseProtocol.setLength("");
        responseProtocol.setPort(String.valueOf(NettyConstant.SERVER_CACHE.get(address).get()));
        context.writeAndFlush(responseProtocol.encodeDatas());
    }

    @Override
    public String getType() {
        return "01";
    }
}
