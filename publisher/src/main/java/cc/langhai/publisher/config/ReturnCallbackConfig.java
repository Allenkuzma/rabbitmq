package cc.langhai.publisher.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

/**
 * ReturnCallbackConfig
 * 消息投递到MQ成功，但是消息路由失败。
 *
 * @author langhai.cc
 * @since 2025-01-20
 */
@Slf4j
@Configuration
public class ReturnCallbackConfig implements ApplicationContextAware {
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // 配置回调
        RabbitTemplate rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
        rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {
            @Override
            public void returnedMessage(ReturnedMessage returnedMessage) {
                log.error("消息路由失败：exchange：{}，routingKey: {}，message：{}, replyCode：{}, replyText：{}",
                        returnedMessage.getExchange(), returnedMessage.getRoutingKey(),
                        returnedMessage.getMessage(), returnedMessage.getReplyCode(), returnedMessage.getReplyText());
            }
        });
    }
}
