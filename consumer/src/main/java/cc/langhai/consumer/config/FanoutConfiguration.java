package cc.langhai.consumer.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Fanout 配置类
 *
 * @author langhai.cc
 * @since 2025-01-19
 */
@Configuration
public class FanoutConfiguration {

    /**
     * 创建一个 fanout 交换机
     *
     * @return FanoutExchange
     */
    @Bean
    public FanoutExchange fanoutExchange() {
        // 可以使用构造器创建，可以通过new方式创建。
        // ExchangeBuilder.fanoutExchange("hmall.fanout2").build();
        return new FanoutExchange("hmall.fanout2");
    }

    /**
     * 创建一个队列
     *
     * @return Queue
     */
    @Bean
    public Queue fanoutQueue3() {
        // 可以使用构造器创建，可以通过new方式创建。
        // QueueBuilder.durable("fanout.queue3").build();
        return new Queue("fanout.queue3", true);
    }

    /**
     * 绑定队列到交换机
     *
     * @param fanoutQueue3 队列
     * @param fanoutExchange 交换机
     * @return Binding
     */
    @Bean
    public Binding bindingFanoutQueue3(Queue fanoutQueue3, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(fanoutQueue3).to(fanoutExchange);
    }

    /**
     * 创建一个队列
     *
     * @return Queue
     */
    @Bean
    public Queue fanoutQueue4() {
        return new Queue("fanout.queue4", true);
    }

    /**
     * 绑定队列到交换机
     *
     * @return Binding
     */
    @Bean
    public Binding bindingFanoutQueue4() {
        return BindingBuilder.bind(fanoutQueue4()).to(fanoutExchange());
    }

}
