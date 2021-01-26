package com.micheal.mq.rpc.config;

import com.micheal.mq.common.QueueConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.MessageFormat;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2020/9/18 22:48
 * @Description rabbitmq配置
 */
@Slf4j
@Configuration
public class RabbitConfig {

    @Autowired
    private CachingConnectionFactory cachingConnectionFactory;

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(cachingConnectionFactory);

        /**
         * 消息只要被 rabbitmq broker 接收到就会执行 confirmCallback
         * 如果是 cluster 模式，需要所有 broker 接收到才会调用 confirmCallback
         * 被 broker 接收到只能表示 message 已经到达服务器，并不能保证消息一定会被投递到目标 queue 里
         */
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            log.info("消息发送ack：{}", ack);
            if (ack) {
                // 修改消息状态为成功
                String msgId = correlationData.getId();

                log.info("消息成功发送到Exchange: {}", msgId);
            } else {
                log.info("消息发送到Exchange失败: correlationData: {}, cause: {}", correlationData, cause);
            }
        });

        /**
         * confirm 模式只能保证消息到达 broker，不能保证消息准确投递到目标 queue 里。
         * 在有些业务场景下，我们需要保证消息一定要投递到目标 queue 里，此时就需要用到 return 退回模式
         * 这样如果未能投递到目标 queue 里将调用 returnCallback，可以记录下详细到投递数据，
         * 定期的巡检或者自动纠错都需要这些数据
         **/
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            log.info(MessageFormat.format("消息发送失败，ReturnCallback:{0},{1},{2},{3},{4},{5}", message, replyCode,
                    replyText, exchange, routingKey));
            // TODO 做消息发送失败时的处理逻辑
        });

        return rabbitTemplate;
    }


    /**
     * 声明队列
     * 参数说明：
     * durable 是否持久化，默认是false（持久化队列则数据会被存储在磁盘上，当消息代理重启时数据不会丢失；暂存队列只对当前连接有效）
     * exclusive 默认是false，只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable
     * autoDelete 默认是false，是否自动删除，当没有生产者或者消费者使用此队列，该队列会自动删除。
     * 一般设置一下队列的持久化就好，其余两个就是默认false
     * @return Queue
     */
    @Bean
    public Queue myQueue() {
        return new Queue(QueueConstants.RPC_QUEUE1, true);
    }

    /**
     * 设置交换机，类型为 direct
     * @return DirectExchange
     */
    @Bean
    public DirectExchange myExchange() {
        return new DirectExchange(QueueConstants.RPC_EXCHANGE, true, false);
    }

    /**
     * 绑定：将交换机和队列绑定，并设置路由匹配键
     * @return Binding
     */
    @Bean
    public Binding myBinding() {
        return BindingBuilder.bind(myQueue()).to(myExchange()).with(QueueConstants.QUEUE_ROUTING_KEY_NAME);
    }
}
