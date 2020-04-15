package com.dili.glasses.utils;

import com.dili.glasses.consts.NettyConstant;
import com.dili.ss.redis.service.RedisUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.*;

/**
 * @author Ren HongWei
 * @date 2019-11-04 15:11
 * @description 消息延时发送器
 **/
@Component
@Slf4j
public class MessageDelaySender {

    private final RedisUtil redisUtil;

    /**
     * 定时计划任务
     * 线程数为Cpu核心数+1
     */
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() + 1);


    /**
     * 消息间隔 ms
     */
    private static final long MESSAGE_INTERVAL = 1000;

    @Autowired
    public MessageDelaySender(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    @SuppressWarnings("unchecked")
    public void sendMsg(Integer terminalId, byte[] content) {
        if (!NettyConstant.TERMINAL_CHANNEL_ID_MAP.containsKey(terminalId)) {
            return;
        }
        ChannelId channelId = NettyConstant.TERMINAL_CHANNEL_ID_MAP.get(terminalId);
        synchronized (channelId) {
            //判断不久前是否对该终端发送过该消息，发送过，未到过期时间，则不允许再次发送
            String key = NettyConstant.DELAY_TERMINAL_KEY + terminalId;
            Object o = redisUtil.get(key);
            if (o != null) {
                Long expire = redisUtil.getRedisTemplate().getExpire(key, TimeUnit.MILLISECONDS);
                if (expire != null) {
                    //延迟执行
                    scheduledExecutorService.schedule(() -> channelWrite(channelId, content), expire, TimeUnit.MILLISECONDS);
                    //退后过期时间
                    redisUtil.expire(key, expire + MESSAGE_INTERVAL, TimeUnit.MILLISECONDS);
                }
                return;
            }

            //发送消息
            channelWrite(channelId, content);

            //缓存发送过的终端id，缓存过期前不允许再次发送
            redisUtil.set(key, new Date(), MESSAGE_INTERVAL, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 服务端给客户端发送消息
     *
     * @param msg       需要发送的消息内容
     * @param channelId 连接通道唯一id
     */
    private void channelWrite(ChannelId channelId, byte[] msg) {
        ChannelHandlerContext ctx = NettyConstant.CHANNEL_MAP.get(channelId);
        log.info("-------写数据---------");
        if (ctx == null) {
            log.info("通道【" + channelId + "】不存在");
            return;
        }
        if (msg == null) {
            log.warn("服务端响应空的消息");
            return;
        }
        //将客户端的信息直接返回写入ctx
        ctx.writeAndFlush(msg);
    }

}
