package com.dili.glasses.factory;


import com.dili.glasses.domain.AbstractProtocol;
import com.dili.glasses.domain.GlassesProtocol;
import com.dili.glasses.domain.receive.AbstractReceiveProtocol;
import com.dili.glasses.factory.strategy.protocol.*;
import com.dili.glasses.utils.MessageDelaySender;
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

    private final LoginStrategy loginStrategy;

    private final LogoutStrategy logoutStrategy;

    private final HeartBeatStrategy heartBeatStrategy;

    private final ReportDataStrategy reportDataStrategy;

    private final ReadDataStrategy readDataStrategy;

    private final WriteDataStrategy writeDataStrategy;

    private final MessageDelaySender messageDelaySender;

    @Autowired
    public ProtocolDealFactory(LoginStrategy loginStrategy, LogoutStrategy logoutStrategy,
                               HeartBeatStrategy heartBeatStrategy, ReportDataStrategy reportDataStrategy,
                               ReadDataStrategy readDataStrategy, WriteDataStrategy writeDataStrategy, MessageDelaySender messageDelaySender) {
        this.loginStrategy = loginStrategy;
        this.logoutStrategy = logoutStrategy;
        this.heartBeatStrategy = heartBeatStrategy;
        this.reportDataStrategy = reportDataStrategy;
        this.readDataStrategy = readDataStrategy;
        this.writeDataStrategy = writeDataStrategy;
        this.messageDelaySender = messageDelaySender;
    }


    @PostConstruct
    public void initMethod() {
        CACHE.put("2_1", heartBeatStrategy);
        CACHE.put("2_2", loginStrategy);
//        CACHE.put("2_3", logoutStrategy);
        CACHE.put("1_4", readDataStrategy);
        CACHE.put("2_5", writeDataStrategy);
        CACHE.put("2_6", reportDataStrategy);
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
                AbstractReceiveProtocol convert = convert(protocol, strategy.getRequestProtocolClass());
                strategy.exec(ctx, convert);
            } catch (Exception e) {
                log.error("设备端请求协议解析失败：{}", e);
            }
        }
    }


    /**
     * 根据类型，选择对应的策略器执行
     *
     * @param protocol 协议
     */
    private <T extends AbstractReceiveProtocol> T convert(GlassesProtocol protocol, Class<T> clazz) throws IllegalAccessException, InstantiationException {
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
