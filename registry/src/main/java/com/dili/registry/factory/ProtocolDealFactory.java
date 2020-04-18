package com.dili.registry.factory;


import com.dili.registry.domain.AbstractProtocol;
import com.dili.registry.factory.strategy.ProtocolExecStrategy;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * byte[]转Protocol工厂
 *
 * @author wangmi
 */
@Slf4j
@Component
public class ProtocolDealFactory {

    /**
     * 保存命令对应的执行策略
     */
    private static final Map<String, ProtocolExecStrategy> CACHE = new HashMap<>();

    @Autowired
    private List<ProtocolExecStrategy> protocolExecStrategies;

    @PostConstruct
    public void initMethod() {
        for (ProtocolExecStrategy protocolExecStrategy : protocolExecStrategies) {
            CACHE.put(protocolExecStrategy.getType(), protocolExecStrategy);
        }
    }


    @SuppressWarnings("unchecked")
    public void exec(ChannelHandlerContext ctx, AbstractProtocol protocol) {
        if (CACHE.containsKey(protocol.getType())) {
            try {
                ProtocolExecStrategy strategy = CACHE.get(protocol.getType());
                protocol.parseDatas();
                strategy.exec(ctx, protocol);
            } catch (Exception e) {
                log.error("设备端请求协议解析失败：{}", e);
            }
        }else{
            log.error("未知类型命令：{}", protocol.getType());
        }
    }

}
