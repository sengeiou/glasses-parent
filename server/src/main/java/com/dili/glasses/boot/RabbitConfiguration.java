package com.dili.glasses.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by asiam on 2019/4/8
 *
 * @author wangmi
 */
@Configuration
@ConditionalOnExpression("'${mq.enable}'=='true'")
public class RabbitConfiguration {

    //服务端下发

    public static final String TOPIC_EXCHANGE = "glasses.topicExchange";
    public static final String CMD_ROUTING_KEY = "glasses.cmd.routingKey";
    public static final String CMD_QUEUE = "glasses.cmd.queue";

    //终端上报

    public static final String REPORT_ROUTING_KEY = "glasses.report.routingKey";
    public static final String REPORT_QUEUE = "glasses.report.queue";

    //发布-消费者 模式

    public static final String FANOUT_EXCHANGE = "glasses.fanoutExchange";


    private final NettyServerConfig nettyServerConfig;

    @Autowired
    public RabbitConfiguration(NettyServerConfig nettyServerConfig) {
        this.nettyServerConfig = nettyServerConfig;
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE, true, false);
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE, true, false);
    }

    public String queueName() {
        //队列的名称，根据服务器的ip和端口号生成
        return CMD_QUEUE + "." + nettyServerConfig.getHost().replace(".", "-") + "." + nettyServerConfig.getPort();
    }

    @Bean
    public Queue cmdQueue() {
        return new Queue(queueName(), true, false, false);
    }

    @Bean
    public Binding cmdBinding() {
        String routeKey = CMD_ROUTING_KEY + "." + nettyServerConfig.getHost().replace(".", "-") + "." + nettyServerConfig.getPort();
        return BindingBuilder.bind(cmdQueue()).to(topicExchange()).with(routeKey);
    }

    @Bean
    public Binding fanoutBinding() {
        return BindingBuilder.bind(cmdQueue()).to(fanoutExchange());
    }

    @Bean
    public Queue reportQueue() {
        return new Queue(REPORT_QUEUE, true, false, false);
    }

    @Bean
    public Binding reportQueueBinding() {
        return BindingBuilder.bind(reportQueue()).to(topicExchange()).with(REPORT_ROUTING_KEY);
    }

    // ======================== 定制一些处理策略 =============================

    /**
     * 定制化amqp模版
     * <p>
     * ConfirmCallback接口用于实现消息发送到RabbitMQ交换器后接收ack回调   即消息发送到exchange  ack
     * ReturnCallback接口用于实现消息发送到RabbitMQ 交换器，但无相应队列与交换器绑定时的回调  即消息发送不到任何一个队列中  ack
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        Logger log = LoggerFactory.getLogger(RabbitTemplate.class);
        // 消息发送失败返回到队列中, yml需要配置 publisher-returns: true
        rabbitTemplate.setMandatory(true);
        // 消息返回, yml需要配置 publisher-returns: true
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String correlationId = message.getMessageProperties().getCorrelationId();
            log.debug("消息：{} 发送失败, 应答码：{} 原因：{} 交换机: {}  路由键: {}", correlationId, replyCode, replyText, exchange, routingKey);
        });
        // 消息确认, yml需要配置 publisher-confirms: true
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                // log.debug("消息发送到exchange成功,id: {}", correlationData.getId());
            } else {
                log.debug("消息发送到exchange失败,原因: {}", cause);
            }
        });
        return rabbitTemplate;
    }
}