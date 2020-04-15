package com.dili.registry.factory.strategy;

import com.dili.registry.boot.RegistryNettyConfig;
import com.dili.registry.consts.NettyConstant;
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
public class ApplyAddressStrategy extends BaseStrategy<ApplyAddressRequestProtocol> {

    private final RegistryNettyConfig registryNettyConfig;

    @Autowired
    public ApplyAddressStrategy(RegistryNettyConfig registryNettyConfig) {
        this.registryNettyConfig = registryNettyConfig;
    }

    @Override
    public void exec(ChannelHandlerContext context, ApplyAddressRequestProtocol protocol) {
        //如果当前没有Server端连接，则无法分配服务端地址
        if (NettyConstant.SERVER_CACHE.isEmpty()) {
            return;
        }
        int min = 99999;
        String address = null;
        for (Map.Entry<String, AtomicInteger> entry : NettyConstant.SERVER_CACHE.entrySet()) {
            if (entry.getValue().get() < min) {
                min = entry.getValue().get();
                address = entry.getKey();
            }
        }
        if (address != null) {

            NettyConstant.SERVER_CACHE.put(address, NettyConstant.SERVER_CACHE.get(address));
            long startTime = DateUtils.formatDateStr2Date("2018-01-01 00:00:00").getTime();

            int terminalTime = (int)((System.currentTimeMillis() - startTime) / 1000);

            Byte[] hostPort = new Byte[6];
            String[] splits = address.split(":");

            //docker容器安装问题临时处理方案
            if (StringUtils.isNotBlank(registryNettyConfig.getServerHost())) {
                splits[0] = registryNettyConfig.getServerHost();
            }

            String[] hosts = splits[0].split("\\.");
            String port = splits[1];
            for (int i = 0; i < hosts.length; i++) {
                hostPort[i] = (byte) Integer.parseInt(hosts[i]);
            }
            byte[] portBytes = ByteArrayUtils.short2bytes2(Short.parseShort(port));
            hostPort[4] = portBytes[0];
            hostPort[5] = portBytes[1];

            ApplyAddressResponseProtocol responseProtocol = new ApplyAddressResponseProtocol(protocol.getTerminalId(), protocol.getCmd(), terminalTime, hostPort);

            context.writeAndFlush(responseProtocol.toByteArray());
        }
    }

    @Override
    public Class<ApplyAddressRequestProtocol> getRequestProtocolClass() {
        return ApplyAddressRequestProtocol.class;
    }
}
