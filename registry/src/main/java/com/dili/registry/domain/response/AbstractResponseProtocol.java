package com.dili.registry.domain.response;

import com.dili.registry.domain.AbstractProtocol;
import com.dili.ss.util.ByteArrayUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ren HongWei
 * @date 2019-09-02 15:34
 * @description
 **/
@NoArgsConstructor
public abstract class AbstractResponseProtocol extends AbstractProtocol {

    /**
     * 帧来源: 0: 主站 1:APP 2:眼镜终端
     */
    private static final Byte SOURCE = 0;

    /**
     * 传输类型:0:请求帧 1:应答帧 2:主动上报
     */
    private static final Byte TRANSFER_TYPE = 1;

    public AbstractResponseProtocol(Integer terminalId, Byte cmd) {
        this.setTerminalId(terminalId);
        this.setCmd(cmd);
    }

    /**
     * 获取所有字节长度
     *
     * @return 字节数量
     */
    private int getTotalLength() {

        if (getInformation() == null) {
            setInformation(ByteArrayUtils.toObjects(contentToInformation()));
        }

        return 16 + getInformation().length;
    }

    /**
     * 帧长度
     *
     * @return 帧长度
     */
    public Short getLength() {
        //总长度减去开始标识的长度
        return (short) (getTotalLength() - 2);
    }


    /**
     * 将实际内容转为信息域
     *
     * @return 信息域内容
     */
    public abstract byte[] contentToInformation();


    /**
     * 将数据转为字节数组
     *
     * @return 字节数组
     */
    public byte[] toByteArray() {
        contentToInformation();
        ByteBuf buf = Unpooled.buffer(getTotalLength());
        buf.writeBytes(ByteArrayUtils.short2bytes2(STX));
        buf.writeShort(getLength());
        buf.writeInt(getTerminalId());
        buf.writeByte(getCmd());
        buf.writeByte(SOURCE);
        buf.writeByte(TRANSFER_TYPE);
        buf.writeBytes(ByteArrayUtils.toPrimitives(getInformation()));
        buf.writeByte(END_MARK);
        buf.writeInt(getCheckSum(buf));
        return buf.array();
    }

    /**
     * 获取校验和
     *
     * @return 校验和
     */
    private int getCheckSum(ByteBuf buf) {
        if (buf == null) {
            return 0;
        }
        List<Integer> byteList = new ArrayList<>();
        for (byte value : buf.array()) {
            byteList.add((value & 0xff));
        }


        return byteList.stream().mapToInt(ele -> ele).sum();
    }
}
