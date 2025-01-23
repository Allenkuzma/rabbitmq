package cc.langhai.publisher;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.UUID;

/**
 * 生产者测试类
 *
 * @author langhai.cc
 * @since 2025-01-18
 */
@Slf4j
@SpringBootTest
public class SpringAmqpTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 直接发送消息到队列，simple.queue队列在控制台创建。
     */
    @Test
    void testSendMessageQueue() {
        String queueName = "simple.queue";
        String message = "hello, rabbitMQ 浪海";
        rabbitTemplate.convertAndSend(queueName, message);
    }

    /**
     * workQueue工作队列模式，发送消息到队列，控制台创建work.queue队列。
     * Work模型总结：
     *          1.多个消费者绑定到一个队列，可以加快消息处理速度。
     *          2.同一条消息只能被一个消费者处理。
     *          3.通过设置 prefetch 这个参数来控制消费者预取消息的数量，处理完这一条消息再处理下一条消息。（可以实现能者多劳）
     *
     * @throws InterruptedException 打断异常
     */
    @Test
    void testWorkQueue() throws InterruptedException {
        String queueName = "work.queue";
        for (int i = 0; i < 50; i++) {
            String message = "hello, rabbitMQ 浪海" + i;
            rabbitTemplate.convertAndSend(queueName, message);
            Thread.sleep(20);
        }
    }

    /**
     * Fanout交换机，fanout.queue1和fanout.queue2队列在控制台创建。
     * 这两个队列绑定在这个交换机上，交换机名是hmall.fanout。
     * Fanout交换机：翻译：善出，也就是广播模式，订阅队列的消费者都可以获取到发布的消息。
     */
    @Test
    void testFanoutExchange() {
        String exchangeName = "hmall.fanout";
        String message = "广播模式";
        rabbitTemplate.convertAndSend(exchangeName, "", message);
    }

    /**
     * Direct交换机，direct.queue1和direct.queue2队列在控制台创建。
     * Direct订阅模式，订阅队列的消费者根据消息的routingKey来获取消息。
     */
    @Test
    void testDirectExchange() {
        String exchangeName = "hmall.direct";
        String message = "红色警告：日本排放核污水";
        rabbitTemplate.convertAndSend(exchangeName, "red", message);

        message = "蓝色警告：注意火灾";
        rabbitTemplate.convertAndSend(exchangeName, "blue", message);

        message = "黄色警告：注意雷击";
        rabbitTemplate.convertAndSend(exchangeName, "yellow", message);
    }

    /**
     * Topic交换机，topic.queue1和topic.queue2队列在控制台创建。
     * Topic订阅模式，订阅队列的消费者根据消息的routingKey来获取消息，可以使用通配符匹配。
     * 通配符匹配规则：# 匹配一个或者多个单词，*匹配一个单词。
     */
    @Test
    void testTopicExchange() {
        String exchangeName = "hmall.topic";
        String message = "中国运动：打乒乓球";
        rabbitTemplate.convertAndSend(exchangeName, "china.sport", message);

        message = "中国新闻：某地方下雨";
        rabbitTemplate.convertAndSend(exchangeName, "china.news", message);

        message = "美国新闻：某地方下雪";
        rabbitTemplate.convertAndSend(exchangeName, "usa.news", message);
    }

    /**
     * 发送对象消息 测试消息转换器
     */
    @Test
    void testSendMap() {
        HashMap<String, Object> message = new HashMap<>();
        message.put("name", "浪海");
        message.put("age", 18);
        rabbitTemplate.convertAndSend("object.queue", message);
    }

    /**
     * 发送消息的回调函数，测试消息确认机制。
     * 消息投递到了MQ，但是路由失败了，会回调ReturnCallbackConfig类中的回调函数，也会返回ACK。
     * 临时信息投递到MQ并且入队成功，会返回ACK。
     * 持久消息投递到MQ，入队成功完成持久化，会返回ACK。
     * 其他情况一般都是NACK。
     *
     * 生产者确认机制需要额外的网络和系统资源开销，一般不需要开启。
     * 在需要使用的情况下，ReturnCallbackConfig一般也不需要开启，因为一般是路由失败引发。
     * 对于NACK消息可以考虑开启有限次数的重试发送，并记录好相关日志信息。
     */
    @Test
    void testConfirmCallback() throws InterruptedException {
        // 异步回调
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        correlationData.getFuture().addCallback(new ListenableFutureCallback<CorrelationData.Confirm>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error("消息回调失败，这里一般不会执行。", ex);
            }

            @Override
            public void onSuccess(CorrelationData.Confirm result) {
                log.error("消息回调成功。");
                if (result.isAck()) {
                    log.info("消息发送成功，收到ACK。{}", result);
                } else {
                    log.error("消息发送失败，收到NACK。{}", result);
                }
            }
        });
        rabbitTemplate.convertAndSend("hmall.direct", "red", "消息回调测试", correlationData);
        Thread.sleep(2000);
        // routingKey 不存在，消息路由失败测试。
        rabbitTemplate.convertAndSend("hmall.direct", "red-888", "消息回调测试", correlationData);
        Thread.sleep(2000);
    }

    /**
     * 发送消息的TTL，消息过期之后，投递到死信队列。
     */
    @Test
    void testSendTTLMessage() {
        rabbitTemplate.convertAndSend("simple.direct", "hi", "hello", new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setExpiration("1000");
                return message;
            }
        });
    }
}
