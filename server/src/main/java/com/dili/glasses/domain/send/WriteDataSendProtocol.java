package com.dili.glasses.domain.send;

import com.dili.ss.util.ByteArrayUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ren HongWei
 * @date 2019-09-10 17:22
 * @description 获取数据响应
 **/
@Getter
@Setter
public class WriteDataSendProtocol extends AbstractSendProtocol {

    /**
     * 记录号
     */
    private Integer recordNo;

    /**
     * 数据map
     */
    private Map<Byte, ParamValue> params;


    @Override
    public byte[] contentToInformation() {
        //将数据转换为byte
        ByteBuf buf = Unpooled.buffer(1024);
        int size = 1;
        buf.writeByte((byte) params.size());

        params.forEach((sign, paramValue) -> {
            buf.writeByte(sign);
            buf.writeByte(paramValue.getValueLength());
            buf.writeBytes(paramValue.getValue());
        });
        buf.writeInt(recordNo);

        //计算实际长度
        size += params.size() * 2;
        size += params.values().stream().mapToInt(ParamValue::getValueLength).sum();

        return Arrays.copyOfRange(buf.array(), 0, size + 4);
    }

    public WriteDataSendProtocol(Integer terminalId, Byte cmd, int recordNo, Map<Byte, Byte[]> params) {
        super(terminalId, cmd);
        this.recordNo = recordNo;
        this.params = new HashMap<>();
        params.forEach((key, value) -> {
            ParamValue paramValue = new ParamValue();
            paramValue.setValue(ByteArrayUtils.toPrimitives(value));
            paramValue.setValueLength((byte) value.length);
            this.params.put(key, paramValue);
        });
    }

    @Data
    public static class ParamValue {
        /**
         * 参数长度
         */
        private byte valueLength;

        /**
         * 参数值
         */
        private byte[] value;
    }
}
