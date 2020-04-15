package com.dili.glasses.factory.strategy.data;

import com.dili.ss.util.ByteArrayUtils;
import com.dili.ss.util.DateUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Ren HongWei
 * @date 2019-09-14 14:51
 * @description
 **/
@Component
@Slf4j
public class TerminalTimeStrategy implements DataStrategy<Date> {

    /**
     * 开始时间毫秒数,精确到s
     */
    private static final long START_DATE_MS = DateUtils.formatDateStr2Date("2018-01-01 00:00:00").getTime() / 1000;

    /**
     * 数据字节长度
     */
    private static final int DATA_LENGTH = 4;

    @Override
    public Date parseData(byte[] data) {
        if (data.length != DATA_LENGTH) {
            log.error("【终端时间】解析错误：数据长度不正确");
            return null;
        }

        int changedTimes = ByteArrayUtils.byte2int2(data);
        return new Date((START_DATE_MS + changedTimes) * 1000);
    }

    public static void main(String[] args) {
        ByteBuf buf = Unpooled.buffer(4);
        buf.writeByte((byte)-109);
        buf.writeByte((byte)-2);
        buf.writeByte((byte)103);
        buf.writeByte((byte)-5);

        int changedTimes = ByteArrayUtils.byte2int2(buf.array());
        System.out.println("时间：" + changedTimes);
    }
}
