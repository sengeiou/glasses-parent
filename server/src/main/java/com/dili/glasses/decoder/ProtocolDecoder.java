package com.dili.glasses.decoder;

import com.dili.glasses.consts.NettyConstant;
import com.dili.glasses.domain.GlassesProtocol;
import com.dili.ss.util.ByteArrayUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <pre>
 * 自己定义的协议
 *  数据包格式
 * +——----——+——-----——+——----——+
 * |协议开始标志|  长度             |   数据       |
 * +——----——+——-----——+——----——+
 * 1.协议开始标志head_data，为int类型的数据，16进制表示为0X76
 * 2.传输数据的长度contentLength，int类型
 * 3.要传输的数据,长度不应该超过2048，防止socket流的攻击
 * </pre>
 */
@Slf4j
public class ProtocolDecoder extends ByteToMessageDecoder {
    /**
     * <pre>
     * 协议开始的标准head_data，short类型，占据2个字节.
     * 表示数据的长度contentLength，short类型，占据2个字节.
     * </pre>
     */
    public final int BASE_LENGTH = 2 + 2;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer,
                          List<Object> out) throws Exception {
        // 可读长度必须大于基本长度
        if (buffer.readableBytes() >= BASE_LENGTH) {
            // 防止socket字节流攻击
            // 防止，客户端传来的数据过大
            // 因为，太大的数据，是不合理的
            if (buffer.readableBytes() > 4096) {
                buffer.skipBytes(buffer.readableBytes());
            }

            // 记录包头开始的index
            int beginReader;

            while (true) {
                // 获取包头开始的index
                beginReader = buffer.readerIndex();
                // 标记包头开始的index
                buffer.markReaderIndex();
                // 读到了协议的开始标志，结束while循环
                if (buffer.readShort() == NettyConstant.HEAD_DATA) {
                    break;
                }

                // 未读到包头，略过一个字节
                // 每次略过，一个字节，去读取，包头信息的开始标记
                buffer.resetReaderIndex();
                buffer.readByte();

                // 当略过，一个字节之后，
                // 数据包的长度，又变得不满足
                // 此时，应该结束。等待后面的数据到达
                if (buffer.readableBytes() < BASE_LENGTH) {
                    return;
                }
            }

            // 消息的长度
            int length = buffer.readShort();
            // 判断请求数据包数据是否到齐
            if (buffer.readableBytes() < length - 2) {
                // 还原读指针
                buffer.readerIndex(beginReader);
                return;
            }

            // 读取data数据
            byte[] data = new byte[length - 2];
            buffer.readBytes(data);

            GlassesProtocol protocol = new GlassesProtocol((short) length, data);
            if (!checkSum(protocol)) {
                log.error("校验和不通过");
                return;
            }
            out.add(protocol);
        }
    }

    /**
     * 计算校验和
     *
     * @param protocol 协议
     * @return 是否通过
     */
    protected boolean checkSum(GlassesProtocol protocol) {
        int contentLength = protocol.getContent().length;

        //终端传递的校验和
        int receivedSum = ByteArrayUtils.byte2int2(Arrays.copyOfRange(protocol.getContent(), contentLength - BASE_LENGTH, contentLength));

        //计算校验和
        ByteBuf buf = Unpooled.buffer(BASE_LENGTH + protocol.getContent().length);
        buf.writeShort(protocol.getStx());
        buf.writeShort(protocol.getLength());
        buf.writeBytes(Arrays.copyOfRange(protocol.getContent(), 0, contentLength - BASE_LENGTH));
        List<Integer> byteList = new ArrayList<>();
        for (byte value : buf.array()) {
            byteList.add((value & 0xff));
        }

        int calculateSum = byteList.stream().mapToInt(ele -> ele).sum();
        return receivedSum == calculateSum;
    }
}
