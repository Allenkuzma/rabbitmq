package cc.langhai.consumer.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * mq监听器
 *
 * @author langhai.cc
 * @since 2025-01-18
 */
@Slf4j
@Component
public class MqListener {


    /**
     * 监听simple.queue队列
     *
     * @param message 消息内容
     */
    // @RabbitListener(queues = "simple.queue")
    public void listenSimpleQueue(String message) {
        log.info("simple.queue监听到消息：{}", message);
        // 故意抛出异常
        throw new RuntimeException("故意抛出异常");
    }

    /**
     * 监听work.queue队列，消费能力不同。 20ms 消费一次。
     *
     * @param message 消息内容
     * @throws InterruptedException 打断异常
     */
    @RabbitListener(queues = "work.queue")
    public void listenWorkQueue1(String message) throws InterruptedException {
        log.info("work.queue1监听到消息：{}", message);
        Thread.sleep(20);
    }

    /**
     * 监听work.queue队列，消费能力不同。 200ms 消费一次。
     *
     * @param message 消息内容
     * @throws InterruptedException 打断异常
     */
    @RabbitListener(queues = "work.queue")
    public void listenWorkQueue2(String message) throws InterruptedException {
        log.info("work.queue2监听到消息：{}", message);
        Thread.sleep(200);
    }

    /**
     * 监听fanout.queue1队列，测试广播交换机模式。
     *
     * @param message 消息内容
     */
    @RabbitListener(queues = "fanout.queue1")
    public void listenFanoutQueue1(String message) {
        log.info("fanout.queue1监听到消息：{}", message);
    }

    /**
     * 监听fanout.queue2队列，测试广播交换机模式。
     *
     * @param message 消息内容
     */
    @RabbitListener(queues = "fanout.queue2")
    public void listenFanoutQueue2(String message) {
        log.info("fanout.queue2监听到消息：{}", message);
    }

    /**
     * 监听direct.queue1队列，测试路由交换机模式。RoutingKey为 blue red。
     *
     * @param message 消息内容
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.queue1"),
            exchange = @Exchange(name = "hmall.direct", type = ExchangeTypes.DIRECT),
            key = {"blue", "red"}
    ))
    public void listenDirectQueue1(String message) {
        log.info("direct.queue1监听到消息：{}", message);
    }

    /**
     * 监听direct.queue2队列，测试路由交换机模式。RoutingKey为 yellow red。
     *
     * @param message 消息内容
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.queue2"),
            exchange = @Exchange(name = "hmall.direct", type = ExchangeTypes.DIRECT),
            key = {"yellow", "red"}
    ))
    public void listenDirectQueue2(String message) {
        log.info("direct.queue2监听到消息：{}", message);
    }

    /**
     * 监听topic.queue1队列，测试主题交换机模式。RoutingKey为 china.#。
     *
     * @param message 消息内容
     */
    @RabbitListener(queues = "topic.queue1")
    public void listenTopicQueue1(String message) {
        log.info("topic.queue1监听到消息：{}", message);
    }

    /**
     * 监听topic.queue2队列，测试主题交换机模式。RoutingKey为 #.news。
     *
     * @param message 消息内容
     */
    @RabbitListener(queues = "topic.queue2")
    public void listenTopicQueue2(String message) {
        log.info("topic.queue2监听到消息：{}", message);
    }

    /**
     * 监听object.queue队列，测试对象消息。
     *
     * @param user 对象消息
     */
    @RabbitListener(queues = "object.queue")
    public void listenObjectQueue(Map<String, Object> user) {
        log.info("object.queue监听到消息：{}", user);
    }

    /**
     * 监听dlx.queue队列，测试死信队列。
     *
     * @param message 消息内容
     */
    @RabbitListener(queues = "dlx.queue")
    public void listenDlxQueue(String message) {
        log.info("dlx.queue监听到消息：{}", message);
    }
}
