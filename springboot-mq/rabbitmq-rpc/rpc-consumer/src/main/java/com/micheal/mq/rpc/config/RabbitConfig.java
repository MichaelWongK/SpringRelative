package com.micheal.mq.rpc.config;

import com.micheal.mq.common.QueueConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2020/9/18 22:48
 * @Description rabbitmq配置
 */
@Slf4j
@Configuration
public class RabbitConfig {

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

        // 在这里设置返回队列
        rabbitTemplate.setReplyAddress(QueueConstants.RPC_QUEUE2);
        rabbitTemplate.setReplyTimeout(60000);
        return rabbitTemplate;
    }

    // 设置队列监听器
    @Bean
    public SimpleMessageListenerContainer createReplyListenerContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer listenerContainer = new SimpleMessageListenerContainer();
        listenerContainer.setConnectionFactory(connectionFactory);
        listenerContainer.setQueueNames(QueueConstants.RPC_QUEUE2);
        listenerContainer.setMessageListener(rabbitTemplate(connectionFactory));
        return listenerContainer;
    }


    /**
     * 声明同步RPC队列
     * 参数说明：
     * durable 是否持久化，默认是false（持久化队列则数据会被存储在磁盘上，当消息代理重启时数据不会丢失；暂存队列只对当前连接有效）
     * exclusive 默认是false，只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable
     * autoDelete 默认是false，是否自动删除，当没有生产者或者消费者使用此队列，该队列会自动删除。
     * 一般设置一下队列的持久化就好，其余两个就是默认false
     * @return Queue
     */
    @Bean
    public Queue syncRPCQueue() {
        return new Queue(QueueConstants.RPC_QUEUE1);
    }

    @Bean
    public Queue replyRPCQueue() {
        return new Queue(QueueConstants.RPC_QUEUE2);
    }

    /**
     * 设置交换机，类型为 topic
     * @return DirectExchange
     */
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(QueueConstants.RPC_EXCHANGE);
    }

    /**
     * 绑定：将交换机和请求队列绑定，并设置路由匹配键
     * @return Binding
     */
    @Bean
    public Binding syncBinding() {
        return BindingBuilder.bind(syncRPCQueue()).to(exchange()).with(QueueConstants.RPC_QUEUE1);
    }

    /**
     * 绑定：将交换机和返回队列绑定，并设置路由匹配键
     * @return Binding
     */
    @Bean
    public Binding replyBinding() {
        return BindingBuilder.bind(replyRPCQueue()).to(exchange()).with(QueueConstants.RPC_QUEUE2);
    }
}
