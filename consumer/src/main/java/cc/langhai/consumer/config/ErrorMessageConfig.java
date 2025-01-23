package cc.langhai.consumer.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.ImmediateRequeueMessageRecoverer;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消费者消费失败重试处理机制
 * 自定义重试次数耗尽后的消息处理策略
 *
 * @author langhai.cc
 * @since 2025-01-21
 */
@Configuration
@ConditionalOnProperty(name = "spring.rabbitmq.listener.simple.retry.enabled", havingValue = "true")
public class ErrorMessageConfig {


    /**
     * 声明交换机 消息失败处理的交换机
     *
     * @return DirectExchange
     */
    @Bean
    public DirectExchange errorMessageExchange() {
        return new DirectExchange("error.direct");
    }

    /**
     * 声明队列 消息失败处理的队列
     *
     * @return Queue
     */
    @Bean
    public Queue errorMessageQueue() {
        return new Queue("error.queue", true);
    }

    /**
     * 绑定队列和交换机
     *
     * @param errorMessageQueue 消息失败处理的队列
     * @param errorMessageExchange 消息失败处理的交换机
     * @return 绑定关系
     */
    @Bean
    public Binding errorMessageBinding(Queue errorMessageQueue, DirectExchange errorMessageExchange) {
        return BindingBuilder.bind(errorMessageQueue).to(errorMessageExchange).with("error");
    }

    /**
     * 创建消息失败处理的策略
     *
     * @param rabbitTemplate 消息发送模板
     * @return MessageRecoverer
     */
    @Bean
    public MessageRecoverer republishMessageRecoverer(RabbitTemplate rabbitTemplate) {
        // 重试耗尽之后，直接拒绝消息，丢弃信息。默认实现方式。返回reject。
        // return new RejectAndDontRequeueRecoverer();
        // 重试耗尽之后，消息重新入队。返回nack。
        // return new ImmediateRequeueMessageRecoverer();
        // 重试耗尽之后，消息重新发送到指定交换机。
        return new RepublishMessageRecoverer(rabbitTemplate, "error.direct", "error");
    }

}
