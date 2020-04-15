package com.dili.glasses.factory.strategy.protocol;

import com.dili.glasses.boot.NettyServerConfig;
import com.dili.glasses.consts.NettyConstant;
import com.dili.glasses.dao.DeviceLongShortNumMapMapper;
import com.dili.glasses.domain.entity.DeviceLongShortNumMap;
import com.dili.glasses.domain.entity.SystemConfig;
import com.dili.glasses.domain.entity.Terminal;
import com.dili.glasses.domain.entity.TerminalConfig;
import com.dili.glasses.domain.receive.LoginReceiveProtocol;
import com.dili.glasses.domain.send.ReadDataSendProtocol;
import com.dili.glasses.domain.send.SimpleSendProtocol;
import com.dili.glasses.service.SystemConfigService;
import com.dili.glasses.service.TerminalConfigService;
import com.dili.glasses.service.TerminalService;
import com.dili.glasses.utils.MessageDelaySender;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.redis.service.RedisUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Ren HongWei
 * @date 2019-09-02 19:40
 * @description 登录执行逻辑
 **/
@Slf4j
@Component
public class LoginStrategy extends BaseStrategy<LoginReceiveProtocol> {


    private final TerminalConfigService terminalConfigService;

    private final SystemConfigService systemConfigService;

    private final RedisUtil redisUtil;

    private final NettyServerConfig nettyServerConfig;

    private final TerminalService terminalService;

    @Resource
    private DeviceLongShortNumMapMapper deviceLongShortNumMapMapper;

    private final MessageDelaySender messageDelaySender;

    @Autowired
    public LoginStrategy(RedisUtil redisUtil, NettyServerConfig nettyServerConfig, SystemConfigService systemConfigService, TerminalConfigService terminalConfigService, TerminalService terminalService, MessageDelaySender messageDelaySender) {
        super(messageDelaySender);
        this.redisUtil = redisUtil;
        this.nettyServerConfig = nettyServerConfig;
        this.systemConfigService = systemConfigService;
        this.terminalConfigService = terminalConfigService;
        this.terminalService = terminalService;
        this.messageDelaySender = messageDelaySender;
    }

    @Override
    public void exec(ChannelHandlerContext context, LoginReceiveProtocol protocol) {
        log.info("设备 {} 登录服务器。", protocol.getTerminalId());

        //检查终端相关信息
        terminalCheck(context, protocol);

        ChannelId channelId = context.channel().id();
        //登录覆盖一次
        NettyConstant.CHANNEL_MAP.put(channelId, context);
        //绑定终端号和channelId的关系
        NettyConstant.TERMINAL_CHANNEL_ID_MAP.put(protocol.getTerminalId(), channelId);
        //绑定channelId和终端号的关系
        NettyConstant.CHANNEL_ID_TERMINAL_MAP.put(channelId, protocol.getTerminalId());
        String key = NettyConstant.TERMINAL_KEY + protocol.getTerminalId();
        //缓存到redis, key为终端号，value为Server端的host:port
        redisUtil.set(key, nettyServerConfig.getHost() + ":" + nettyServerConfig.getPort());

        //更新设备在线状态
        terminalService.updateOnlineStatus(protocol.getTerminalId(), true);

        //响应客户端
        SimpleSendProtocol simpleSendProtocol = new SimpleSendProtocol(protocol.getTerminalId(), protocol.getCmd(), true);
        channelWrite(context.channel().id(), simpleSendProtocol.toByteArray());

        //发送读取终端配置指令
        readTerminalConfig(context, protocol);
    }


    /**
     * 设备信息检查
     *
     * @param context  上下文
     * @param protocol 协议
     */
    private void terminalCheck(ChannelHandlerContext context, LoginReceiveProtocol protocol) {
        Terminal terminal = terminalService.findOne(protocol.getTerminalId());

        //如果不存在，则返回失败
        if (terminal == null) {
            DeviceLongShortNumMap deviceLongShortNumMap = DTOUtils.newInstance(DeviceLongShortNumMap.class);
            deviceLongShortNumMap.setShortNum(protocol.getTerminalId());
            deviceLongShortNumMap.setDeleteFlag((byte) 0);
            DeviceLongShortNumMap deviceNumEntity = deviceLongShortNumMapMapper.selectOne(deviceLongShortNumMap);
            if (deviceNumEntity != null) {
                //注册设备信息
                terminalService.createTerminal(deviceNumEntity.getLongNum(), protocol.getTerminalId(), deviceNumEntity.getBatch());
            } else {
                SimpleSendProtocol simpleSendProtocol = new SimpleSendProtocol(protocol.getTerminalId(), protocol.getCmd(), false);
                //响应客户端
                channelWrite(context.channel().id(), simpleSendProtocol.toByteArray());
                return;
            }
        }

        //判断设备的设置是否存在，如果不存在，则根据默认的配置复制一份。
        List<TerminalConfig> terminalConfigList = terminalConfigService.findAllTerminalConfig(protocol.getTerminalId());
        if (CollectionUtils.isEmpty(terminalConfigList)) {
            //查询系统配置
            List<SystemConfig> systemConfigs = systemConfigService.getAllSystemConfig();

            //添加终端配置
            List<TerminalConfig> newTerminalConfigList = new ArrayList<>();
            systemConfigs.forEach(config -> {
                TerminalConfig newTerminalConfig = DTOUtils.newInstance(TerminalConfig.class);
                BeanUtils.copyProperties(config, newTerminalConfig);
                newTerminalConfig.setTerminalId(protocol.getTerminalId());
                newTerminalConfig.setCreateTime(new Date());
                newTerminalConfig.setModifyTime(new Date());
                newTerminalConfig.setDeleteFlag((byte) 0);
                newTerminalConfig.setSystemConfigId(config.getId());
                newTerminalConfig.setId(null);
                newTerminalConfigList.add(newTerminalConfig);
            });
            terminalConfigService.batchInsert(newTerminalConfigList);
        }
    }

    /**
     * 发送指令读取终端配置
     *
     * @param context  上下文
     * @param protocol 协议
     */
    private void readTerminalConfig(ChannelHandlerContext context, LoginReceiveProtocol protocol) {
        Set<Byte> allSystemConfigType = systemConfigService.getAllSystemConfigByte();
        ReadDataSendProtocol readDataSendProtocol = new ReadDataSendProtocol(protocol.getTerminalId(), (byte) 0x04,
                NettyConstant.SOCKET_READ_DATA_RECORD_NO, allSystemConfigType.toArray(new Byte[]{}));
        channelWrite(context.channel().id(), readDataSendProtocol.toByteArray());
    }

    @Override
    public Class<LoginReceiveProtocol> getRequestProtocolClass() {
        return LoginReceiveProtocol.class;
    }
}
