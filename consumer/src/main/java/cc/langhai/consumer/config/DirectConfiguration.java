package cc.langhai.consumer.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * direct 配置类
 *
 * @author langhai.cc
 * @since 2025-01-19
 */
// @Configuration
public class DirectConfiguration {

    /**
     * 创建 direct 交换机
     *
     * @return DirectExchange
     */
    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange("hmall.direct");
    }

    /**
     * 创建 direct 队列
     *
     * @return Queue
     */
    @Bean
    public Queue directQueue1(){
        return new Queue("direct.queue1");
    }

    /**
     * 绑定 direct 队列到 direct 交换机
     *
     * @param directQueue1 direct 队列
     * @param directExchange direct 交换机
     * @return Binding
     */
    @Bean
    public Binding bindingDirectQueueBlue(Queue directQueue1, DirectExchange directExchange){
        return BindingBuilder.bind(directQueue1).to(directExchange).with("blue");
    }

    /**
     * 绑定 direct 队列到 direct 交换机
     *
     * @param directQueue1 direct 队列
     * @param directExchange direct 交换机
     * @return Binding
     */
    @Bean
    public Binding bindingDirectQueueRed(Queue directQueue1, DirectExchange directExchange){
        return BindingBuilder.bind(directQueue1).to(directExchange).with("red");
    }

    /**
     * 创建 direct 队列
     *
     * @return Queue
     */
    @Bean
    public Queue directQueue2(){
        return new Queue("direct.queue2");
    }

    /**
     * 绑定 direct 队列到 direct 交换机
     *
     * @param directQueue2 direct 队列
     * @param directExchange direct 交换机
     * @return Binding
     */
    @Bean
    public Binding bindingDirectQueue2Yellow(Queue directQueue2, DirectExchange directExchange){
        return BindingBuilder.bind(directQueue2).to(directExchange).with("yellow");
    }

    /**
     * 绑定 direct 队列到 direct 交换机
     *
     * @param directQueue2 direct 队列
     * @param directExchange direct 交换机
     * @return Binding
     */
    @Bean
    public Binding bindingDirectQueue2Blue(Queue directQueue2, DirectExchange directExchange){
        return BindingBuilder.bind(directQueue2).to(directExchange).with("blue");
    }
}
