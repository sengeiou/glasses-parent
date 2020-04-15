package com.dili.client.factory;


import com.dili.client.domain.ApplyAddressProtocol;
import com.dili.client.domain.GlassesProtocol;
import com.dili.client.domain.Protocol;
import com.dili.ss.util.ByteArrayUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * byte[]转Protocol工厂
 * @author wangmi
 */
public class ProtocolConvertFactory {

    static final Map<String, Strategy> cache = new HashMap<>();

    static {
        //key为"传输类型_命令字"
        cache.put("2_8", new ProtocolConvertFactory.FindIpStrategy());
    }

    /**
     * 根据类型，选择对应的策略器执行
     * @param glassesProtocol  要转换的类型
     * @return
     */
    public static Protocol convert(GlassesProtocol glassesProtocol) {
        byte[] content = glassesProtocol.getContent();
        Byte cmd = content[4];
        byte transferType = content[6];
        //根据命令字和传输类型来确定解析策略
        Strategy strategy = cache.get(cmd+"_"+transferType);
        if(strategy == null){
            return null;
        }
        try {
            return strategy.convert(glassesProtocol);
        } catch (Exception e) {
            //转换失败返原值
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 策略接口
     */
    private interface Strategy {
        /**
         * 类型转换
         * @param glassesProtocol
         * @return
         */
        Protocol convert(GlassesProtocol glassesProtocol);
    }

    /**
     * 获取IP策略
     * 查询服务器IP和时间，然后更新眼镜的时间
     */
    private static class FindIpStrategy implements Strategy {

        @Override
        public Protocol convert(GlassesProtocol glassesProtocol) {
            if(glassesProtocol == null){
                return null;
            }
            byte[] content = glassesProtocol.getContent();
            ApplyAddressProtocol protocol = new ApplyAddressProtocol();
            protocol.setStx(glassesProtocol.getStx());
            protocol.setLength(glassesProtocol.getLength());
            byte[] terminalIdByte = Arrays.copyOfRange(content, 0,3);
            protocol.setTerminalId(ByteArrayUtils.byte2int2(terminalIdByte));
            protocol.setCmd(content[4]);
            protocol.setSource(content[5]);
            protocol.setTransferType(content[6]);
            byte[] terminalTimeByte = Arrays.copyOfRange(content, 7,10);
            protocol.setTerminalTime(ByteArrayUtils.byte2int2(terminalTimeByte));
            protocol.setEndMark(content[11]);
            byte[] checkSumByte = Arrays.copyOfRange(content, 12,15);
            protocol.setChecksum(ByteArrayUtils.byte2int2(checkSumByte));
            return protocol;
        }
    }


}
