### 一、先扔一张图

![img](http://upload-images.jianshu.io/upload_images/4931997-1fa8ff913b8a898f.png)

**说明:**

本文涵盖了关于RabbitMQ很多方面的知识点, 如:

- 消息发送确认机制
- 消费确认机制
- 消息的重新投递
- 消费幂等性, 等等

这些都是围绕上面那张整体流程图展开的, 所以有必要先贴出来, 见图知意

### 二、实现思路

1. 简略介绍 163 邮箱授权码的获取
2. 编写发送邮件工具类
3. 编写 RabbitMQ 配置文件
4. 生产者发起调用
5. 消费者发送邮件
6. 定时任务定时拉取投递失败的消息, 重新投递
7. 各种异常情况的测试验证
8. 拓展: 使用动态代理实现消费端幂等性验证和消息确认 (ack)

### 三、项目介绍

1. `springboot`版本`2.1.5.RELEASE`, 旧版本可能有些配置属性不能使用, 需要以代码形式进行配置
2. `RabbitMQ`版本`3.7.15`
3. `MailUtil`: 发送邮件工具类
4. `RabbitConfig`: rabbitmq 相关配置
5. `TestServiceImpl`: 生产者, 发送消息
6. `MailConsumer`: 消费者, 消费消息, 发送邮件
7. `ResendMsg`: 定时任务, 重新投递发送失败的消息

### 四、代码实现

1. 163 邮箱授权码的获取, 如图:

![img](http://upload-images.jianshu.io/upload_images/4931997-551a1bf2c619d09e.png)

该授权码就是配置文件`spring.mail.password`需要的密码

1. `pom`

```
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-mail</artifactId>
        </dependency>
```

1. `rabbitmq`、邮箱配置

```
# rabbitmq
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
# 开启confirms回调 P -> Exchange
spring.rabbitmq.publisher-confirms=true
# 开启returnedMessage回调 Exchange -> Queue
spring.rabbitmq.publisher-returns=true
# 设置手动确认(ack) Queue -> C
spring.rabbitmq.listener.simple.acknowledge-mode=manual
spring.rabbitmq.listener.simple.prefetch=100

# mail
spring.mail.host=smtp.163.com
spring.mail.username=186****2249@163.com
spring.mail.password=***
spring.mail.from=186****2249@163.com
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
```

> 说明: `password`即授权码, `username`和`from`要一致

1. 表结构

```
CREATE TABLE `msg_log` (
  `msg_id` varchar(255) NOT NULL DEFAULT '' COMMENT '消息唯一标识',
  `msg` text COMMENT '消息体, json格式化',
  `exchange` varchar(255) NOT NULL DEFAULT '' COMMENT '交换机',
  `routing_key` varchar(255) NOT NULL DEFAULT '' COMMENT '路由键',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '状态: 0投递中 1投递成功 2投递失败 3已消费',
  `try_count` int(11) NOT NULL DEFAULT '0' COMMENT '重试次数',
  `next_try_time` datetime DEFAULT NULL COMMENT '下一次重试时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`msg_id`),
  UNIQUE KEY `unq_msg_id` (`msg_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息投递日志';
```

> 说明: `exchange routing_key`字段是在定时任务重新投递消息时需要用到的

1. `MailUtil`

```
@Component
@Slf4j
public class MailUtil {

    @Value("${spring.mail.from}")
    private String from;

    @Autowired
    private JavaMailSender mailSender;

    /**
     * 发送简单邮件
     *
     * @param mail
     */
    public boolean send(Mail mail) {
        String to = mail.getTo();// 目标邮箱
        String title = mail.getTitle();// 邮件标题
        String content = mail.getContent();// 邮件正文

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(title);
        message.setText(content);

        try {
            mailSender.send(message);
            log.info("邮件发送成功");
            return true;
        } catch (MailException e) {
            log.error("邮件发送失败, to: {}, title: {}", to, title, e);
            return false;
        }
    }

}
```

1. `RabbitConfig`

```
@Configuration
@Slf4j
public class RabbitConfig {

    @Autowired
    private CachingConnectionFactory connectionFactory;

    @Autowired
    private MsgLogService msgLogService;

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());

        // 消息是否成功发送到Exchange
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("消息成功发送到Exchange");
                String msgId = correlationData.getId();
                msgLogService.updateStatus(msgId, Constant.MsgLogStatus.DELIVER_SUCCESS);
            } else {
                log.info("消息发送到Exchange失败, {}, cause: {}", correlationData, cause);
            }
        });

        // 触发setReturnCallback回调必须设置mandatory=true, 否则Exchange没有找到Queue就会丢弃掉消息, 而不会触发回调
        rabbitTemplate.setMandatory(true);
        // 消息是否从Exchange路由到Queue, 注意: 这是一个失败回调, 只有消息从Exchange路由到Queue失败才会回调这个方法
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            log.info("消息从Exchange路由到Queue失败: exchange: {}, route: {}, replyCode: {}, replyText: {}, message: {}", exchange, routingKey, replyCode, replyText, message);
        });

        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    // 发送邮件
    public static final String MAIL_QUEUE_NAME = "mail.queue";
    public static final String MAIL_EXCHANGE_NAME = "mail.exchange";
    public static final String MAIL_ROUTING_KEY_NAME = "mail.routing.key";

    @Bean
    public Queue mailQueue() {
        return new Queue(MAIL_QUEUE_NAME, true);
    }

    @Bean
    public DirectExchange mailExchange() {
        return new DirectExchange(MAIL_EXCHANGE_NAME, true, false);
    }

    @Bean
    public Binding mailBinding() {
        return BindingBuilder.bind(mailQueue()).to(mailExchange()).with(MAIL_ROUTING_KEY_NAME);
    }

}
```

1. `TestServiceImpl`生产消息

```
@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private MsgLogMapper msgLogMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public ServerResponse send(Mail mail) {
        String msgId = RandomUtil.UUID32();
        mail.setMsgId(msgId);

        MsgLog msgLog = new MsgLog(msgId, mail, RabbitConfig.MAIL_EXCHANGE_NAME, RabbitConfig.MAIL_ROUTING_KEY_NAME);
        msgLogMapper.insert(msgLog);// 消息入库

        CorrelationData correlationData = new CorrelationData(msgId);
        rabbitTemplate.convertAndSend(RabbitConfig.MAIL_EXCHANGE_NAME, RabbitConfig.MAIL_ROUTING_KEY_NAME, MessageHelper.objToMsg(mail), correlationData);// 发送消息

        return ServerResponse.success(ResponseCode.MAIL_SEND_SUCCESS.getMsg());
    }

}
```

1. `MailConsumer`消费消息, 发送邮件

```
@Component
@Slf4j
public class MailConsumer {

    @Autowired
    private MsgLogService msgLogService;

    @Autowired
    private MailUtil mailUtil;

    @RabbitListener(queues = RabbitConfig.MAIL_QUEUE_NAME)
    public void consume(Message message, Channel channel) throws IOException {
        Mail mail = MessageHelper.msgToObj(message, Mail.class);
        log.info("收到消息: {}", mail.toString());

        String msgId = mail.getMsgId();

        MsgLog msgLog = msgLogService.selectByMsgId(msgId);
        if (null == msgLog || msgLog.getStatus().equals(Constant.MsgLogStatus.CONSUMED_SUCCESS)) {
            log.info("重复消费, msgId: {}", msgId);
            return;
        }

        MessageProperties properties = message.getMessageProperties();
        long tag = properties.getDeliveryTag();

        boolean success = mailUtil.send(mail);
        if (success) {
            msgLogService.updateStatus(msgId, Constant.MsgLogStatus.CONSUMED_SUCCESS);
            channel.basicAck(tag, false);
        } else {
            channel.basicNack(tag, false, true);
        }
    }

}
```

> 说明: 其实就完成了 3 件事: 1. 保证消费幂等性, 2. 发送邮件, 3. 更新消息状态, 手动 ack

1. `ResendMsg`定时任务重新投递发送失败的消息

```
@Component
@Slf4j
public class ResendMsg {

    @Autowired
    private MsgLogService msgLogService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // 最大投递次数
    private static final int MAX_TRY_COUNT = 3;

    /**
     * 每30s拉取投递失败的消息, 重新投递
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void resend() {
        log.info("开始执行定时任务(重新投递消息)");

        List<MsgLog> msgLogs = msgLogService.selectTimeoutMsg();
        msgLogs.forEach(msgLog -> {
            String msgId = msgLog.getMsgId();
            if (msgLog.getTryCount() >= MAX_TRY_COUNT) {
                msgLogService.updateStatus(msgId, Constant.MsgLogStatus.DELIVER_FAIL);
                log.info("超过最大重试次数, 消息投递失败, msgId: {}", msgId);
            } else {
                msgLogService.updateTryCount(msgId, msgLog.getNextTryTime());// 投递次数+1

                CorrelationData correlationData = new CorrelationData(msgId);
                rabbitTemplate.convertAndSend(msgLog.getExchange(), msgLog.getRoutingKey(), MessageHelper.objToMsg(msgLog.getMsg()), correlationData);// 重新投递

                log.info("第 " + (msgLog.getTryCount() + 1) + " 次重新投递消息");
            }
        });

        log.info("定时任务执行结束(重新投递消息)");
    }

}
```

> 说明: 每一条消息都和`exchange routingKey`绑定, 所有消息重投共用这一个定时任务即可

### 五、基本测试

OK, 目前为止, 代码准备就绪, 现在进行正常流程的测试

1. 发送请求:

![img](http://upload-images.jianshu.io/upload_images/4931997-a566358d096ce6df.png)

1. 后台日志:

![img](http://upload-images.jianshu.io/upload_images/4931997-267c25ba0f2ae810.png)

1. 数据库消息记录:

![img](http://upload-images.jianshu.io/upload_images/4931997-c6f69c09e725d793.png)

状态为 3, 表明已消费, 消息重试次数为 0, 表明一次投递就成功了

1. 查看邮箱

![img](http://upload-images.jianshu.io/upload_images/4931997-efbb265f212df62d.png)

发送成功

### 六、各种异常情况测试

步骤一罗列了很多关于 RabbitMQ 的知识点, 很重要, 很核心, 而本文也涉及到了这些知识点的实现, 接下来就通过异常测试进行验证 (这些验证都是围绕本文开头扔的那张流程图展开的, 很重要, 所以, 再贴一遍)

![img](http://upload-images.jianshu.io/upload_images/4931997-1fa8ff913b8a898f.png)

1. 验证消息发送到 Exchange 失败情况下的回调, 对应上图`P -> X`

如何验证? 可以随便指定一个不存在的交换机名称, 请求接口, 看是否会触发回调

![img](http://upload-images.jianshu.io/upload_images/4931997-d7b99bf7702d5686.png)

发送失败, 原因: `reply-code=404, reply-text=NOT_FOUND - no exchange 'mail.exchangeabcd' in vhost '/'`, 该回调能够保证消息正确发送到 Exchange, 测试完成

1. 验证消息从 Exchange 路由到 Queue 失败情况下的回调, 对应上图`X -> Q`

同理, 修改一下路由键为不存在的即可, 路由失败, 触发回调

![img](http://upload-images.jianshu.io/upload_images/4931997-260b3cac074ccc3d.png)

发送失败, 原因: `route: mail.routing.keyabcd, replyCode: 312, replyText: NO_ROUTE`

1. 验证在手动 ack 模式下, 消费端必须进行手动确认 (ack), 否则消息会一直保存在队列中, 直到被消费, 对应上图`Q -> C`

将消费端代码`channel.basicAck(tag, false);// 消费确认`注释掉, 查看控制台和 rabbitmq 管控台

![img](http://upload-images.jianshu.io/upload_images/4931997-96c7fedc023e700a.png)

![img](http://upload-images.jianshu.io/upload_images/4931997-58b3f018cb1f06ca.png)

可以看到, 虽然消息确实被消费了, 但是由于是手动确认模式, 而最后又没手动确认, 所以, 消息仍被 rabbitmq 保存, 所以, 手动 ack 能够保证消息一定被消费, 但一定要记得`basicAck`

1. 验证消费端幂等性

接着上一步, 去掉注释, 重启服务器, 由于有一条未被 ack 的消息, 所以重启后监听到消息, 进行消费, 但是由于消费前会判断该消息的状态是否未被消费, 发现`status=3`, 即已消费, 所以, 直接`return`, 这样就保证了消费端的幂等性, 即使由于网络等原因投递成功而未触发回调, 从而多次投递, 也不会重复消费进而发生业务异常

![img](http://upload-images.jianshu.io/upload_images/4931997-cfcde9d281acff4e.png)

1. 验证消费端发生异常消息也不会丢失

很显然, 消费端代码可能发生异常, 如果不做处理, 业务没正确执行, 消息却不见了, 给我们感觉就是消息丢失了, 由于我们消费端代码做了异常捕获, 业务异常时, 会触发: `channel.basicNack(tag, false, true);`, 这样会告诉 rabbitmq 该消息消费失败, 需要重新入队, 可以重新投递到其他正常的消费端进行消费, 从而保证消息不被丢失

测试: send 方法直接返回 false 即可 (这里跟抛出异常一个意思)

![img](http://upload-images.jianshu.io/upload_images/4931997-7180d326607ed971.png)

可以看到, 由于`channel.basicNack(tag, false, true)`, 未被 ack 的消息 (unacked) 会重新入队并被消费, 这样就保证了消息不会走丢

1. 验证定时任务的消息重投

实际应用场景中, 可能由于网络原因, 或者消息未被持久化 MQ 就宕机了, 使得投递确认的回调方法`ConfirmCallback`没有被执行, 从而导致数据库该消息状态一直是`投递中`的状态, 此时就需要进行消息重投, 即使也许消息已经被消费了

定时任务只是保证消息 100% 投递成功, 而多次投递的消费幂等性需要消费端自己保证

我们可以将回调和消费成功后更新消息状态的代码注释掉, 开启定时任务, 查看是否重投

![img](http://upload-images.jianshu.io/upload_images/4931997-49562e6f4d07a213.png)

![img](http://upload-images.jianshu.io/upload_images/4931997-05e67711c58c390f.png)

可以看到, 消息会重投 3 次, 超过 3 次放弃, 将消息状态置为投递失败状态, 出现这种非正常情况, 就需要人工介入排查原因

### 七、拓展: 使用动态代理实现消费端幂等性验证和消费确认 (ack)

不知道大家发现没有, 在`MailConsumer`中, 真正的业务逻辑其实只是发送邮件`mailUtil.send(mail)`而已, 但我们又不得不在调用`send`方法之前校验消费幂等性, 发送后, 还要更新消息状态为 "已消费" 状态, 并手动 ack, 实际项目中, 可能还有很多生产者 - 消费者的应用场景, 如记录日志, 发送短信等等, 都需要 rabbitmq, 如果每次都写这些重复的公用代码, 没必要, 也难以维护, 所以, 我们可以将公共代码抽离出来, 让核心业务逻辑只关心自己的实现, 而不用做其他操作, 其实就是 AOP

为达到这个目的, 有很多方法, 可以用 spring aop, 可以用拦截器, 可以用静态代理, 也可以用动态代理, 在这里, 我用的是动态代理

目录结构如下:

![img](http://upload-images.jianshu.io/upload_images/4931997-562e2b44e588458a.png)

核心代码就是代理的实现, 这里就不把所有代码贴出来了, 只是提供一个思路, 我们要尽可能地把代码写的更简洁更优雅

### 八、总结

发送邮件其实很简单, 但深究起来其实有很多需要注意和完善的点, 一个看似很小的知识点, 也可以引申出很多问题, 甚至涉及到方方面面, 这些都需要自己踩坑, 当然我这代码肯定还有很多不完善和需要优化的点, 希望小伙伴多多提意见和建议

我的代码都是经过自测验证过的, 图也都是一点一点自己画的或认真截的, 希望小伙伴能学到一点东西, 路过的点个赞或点个关注呗, 谢谢