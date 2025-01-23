package cc.langhai.consumer;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * 消费者启动类
 *
 * @author langhai.cc
 * @since 2025-01-18
 */
@SpringBootApplication
public class ConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

    /**
     * 消息转换器
     *
     * @return MessageConverter
     */
    @Bean
    public MessageConverter messageConverter() {
        // 定义消息转换器
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        // 自动配置创建消息id，用于识别不同的消息，在业务中基于id判断是否重复消费。
        converter.setCreateMessageIds(true);
        return converter;
    }
}
