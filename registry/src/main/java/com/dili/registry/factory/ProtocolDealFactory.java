package com.dili.registry.factory;


import com.dili.registry.domain.AbstractProtocol;
import com.dili.registry.domain.GlassesProtocol;
import com.dili.registry.domain.request.AbstractRequestProtocol;
import com.dili.registry.factory.strategy.ApplyAddressStrategy;
import com.dili.registry.factory.strategy.ProtocolExecStrategy;
import com.dili.ss.util.ByteArrayUtils;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * byte[]转Protocol工厂
 *
 * @author wangmi
 */
@Slf4j
@Component
public class ProtocolDealFactory {

    private static final Map<String, ProtocolExecStrategy> CACHE = new HashMap<>();

    private final ApplyAddressStrategy applyAddressStrategy;

    @Autowired
    public ProtocolDealFactory(ApplyAddressStrategy applyAddressStrategy) {
        this.applyAddressStrategy = applyAddressStrategy;
    }


    @PostConstruct
    public void initMethod() {
        CACHE.put("2_8", applyAddressStrategy);
    }


    @SuppressWarnings("unchecked")
    public void exec(ChannelHandlerContext ctx, GlassesProtocol protocol) {
        byte[] content = protocol.getContent();
        Byte cmd = content[4];
        byte transferType = content[6];

        String key = transferType + "_" + cmd;

        if (CACHE.containsKey(key)) {
            try {
                ProtocolExecStrategy strategy = CACHE.get(key);
                AbstractRequestProtocol convert = convert(protocol, strategy.getRequestProtocolClass());
                strategy.exec(ctx, convert);
            } catch (Exception e) {
                log.error("设备端请求协议解析失败：{}", e.getMessage());
            }

        }
    }


    /**
     * 根据类型，选择对应的策略器执行
     *
     * @param protocol 协议
     */
    private <T extends AbstractRequestProtocol> T convert(GlassesProtocol protocol, Class<T> clazz) throws IllegalAccessException, InstantiationException {
        T instance = clazz.newInstance();
        parseBaseInfo(protocol, instance);
        instance.parseInformation(instance.getInformation());
        return instance;
    }


    /**
     * 基础内容解析
     *
     * @param glassesProtocol 眼镜基础协议
     * @param protocol        解析后的协议
     */
    private static void parseBaseInfo(GlassesProtocol glassesProtocol, AbstractProtocol protocol) {
        byte[] content = glassesProtocol.getContent();
        byte[] terminalIdByte = Arrays.copyOfRange(content, 0, 4);
        protocol.setTerminalId(ByteArrayUtils.byte2int2(terminalIdByte));
        protocol.setCmd(content[4]);
        //信息域长度 为数据域长度 - 固定长度
        int informationLength = glassesProtocol.getLength() - 14;
        byte[] informationByte = Arrays.copyOfRange(content, 7, 7 + informationLength);
        protocol.setInformation(ByteArrayUtils.toObjects(informationByte));
    }
}
