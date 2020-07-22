# RabbitMQ几种队列模式

# 工作队列模式

### 1. 什么是工作队列

工作队列：用来将耗时的任务分发给多个消费者（工作者）

主要解决问题：处理资源密集型任务，并且还要等他完成。有了工作队列，我们就可以将具体的工作放到后面去做，将工作封装为一个消息，发送到队列中，一个工作进程就可以取出消息并完成工作。如果启动了多个工作进程，那么工作就可以在多个进程间共享。

工作队列也称为**公平性队列模式**，怎么个说法呢？

循环分发，假如我们拥有两个消费者，默认情况下，RabbitMQ 将按顺序将每条消息发送给下一个消费者，平均而言，每个消费者将获得相同数量的消息，这种分发消息的方式称为轮询。

### 2代码部分

#### 2.1 生产者

创建50个消息

```
public class Producer2 {

    private final static String QUEUENAME = "test_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 创建连接
        Connection conn = MQConnectionUtils.connect();
        // 创建通道
        Channel channel = conn.createChannel();
        // 绑定的交换机 参数1交互机名称 参数2 exchange类型
        channel.queueDeclare(QUEUENAME, false, false, false, null);
        // 保证一次只分发一次 限制发送给同一个消费者 不得超过一条消息
        channel.basicQos(1);
        for (int i=1; i<=50; i++) {
            String msg = "生产者消息_" + i;
            System.out.println("生产者发送消息_" + msg);
            // 发送消息
            channel.basicPublish("", QUEUENAME, null, msg.getBytes());
        }
        // 关闭通道、连接
        channel.close();
        conn.close();
        // 注意：如果消费没有绑定交换机和队列，则消息会丢失
    }

}
```

#### 2.2 消费者

```
public class Consumer1 {
    private final static String QUEUENAME = "test_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 创建连接和channel
        Connection conn = MQConnectionUtils.connect();
        Channel channel = conn.createChannel();
        channel.queueDeclare(QUEUENAME, false, false, false, null);
        // 保证一次只分发一次 限制发送给同一个消费者 不得超过一条消息
        channel.basicQos(1);
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body, "UTF-8");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    /** 手动回执消息 */
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
                System.out.println("X 消费者获取生产者消息：" + msg);
            }
        };
        // 消费者监听队列消息
        channel.basicConsume(QUEUENAME, false, consumer);
    }
}
```

### 3. 循环分发

#### 3.1 启动生产者

![image-20200722222414526](..\..\files\image-20200722222414526.png)

#### 3.2 启动两个消费者

![image-20200722222254960](..\..\files\image-20200722222254960.png)

![image-20200722222353396](..\..\files\image-20200722222353396.png)

在生产者中我们发送了50条消息进入队列，而上方消费者启动图里很明显的看到轮询的效果，就是每个消费者会分到相同的队列任务。

#### 3.3 公平分发

由于上方模拟的是非常简单的消息队列的消费，假如有一些非常耗时的任务，某个消费者在缓慢地进行处理，而另一个消费者则空闲，显然是非常消耗资源的。

再举一个例子，一个1年的程序员，跟一个3年的程序员，分配相同的任务量，明显3年的程序员处理起来更加得心应手，很快就无所事事了，但是3年的程序员拿着非常高的薪资！显然3年的程序员应该承担更多的责任，那怎么办呢？

公平分发。

其实发生上述问题的原因是 RabbitMQ 收到消息后就立即分发出去，而没有确认各个工作者未返回确认的消息数量，类似于TCP/UDP中的UDP，面向无连接。

因此我们可以使用 basicQos 方法，并将参数 prefetchCount 设为1，告诉 RabbitMQ 我每次值处理一条消息，你要等我处理完了再分给我下一个。这样 RabbitMQ 就不会轮流分发了，而是寻找空闲的工作者进行分发。

关键性代码：

```
/** 2.获取通道 */
final Channel channel = newConnection.createChannel();
channel.queueDeclare(QUEUE_NAME, false, false, false, null);
/** 保证一次只分发一次 限制发送给同一个消费者 不得超过一条消息 */
channel.basicQos(1);
```

### 4. 消息持久化

#### 4.1 问题背景

上边我们提到的公平分发是由消费者收取消息时确认解决的，但是这里面又会出现被 kill 的情况。

当有多个消费者同时收取消息，且每个消费者在接收消息的同时，还要处理其它的事情，且会消耗很长的时间。在此过程中可能会出现一些意外，比如消息接收到一半的时候，一个消费者死掉了。

这种情况要使用消息接收确认机制，可以执行上次宕机的消费者没有完成的事情。

但是在默认情况下，我们程序创建的消息队列以及存放在队列里面的消息，都是非持久化的。当RabbitMQ死掉了或者重启了，上次创建的队列、消息都不会保存。

怎么办呢？

#### 4.2 参数配置

**参数配置一：生产者创建队列声明时，修改第二个参数为 true**

```
/**3.创建队列声明 */
channel.queueDeclare(QUEUE_NAME, true, false, false, null);
```

**参数配置二：生产者发送消息时，修改第三个参数为MessageProperties.PERSISTENT_TEXT_PLAIN**

```
for (int i = 1; i <= 50; i++) {
    String msg = "生产者消息_" + i;
    System.out.println("生产者发送消息:" + msg);
    /**4.发送消息 */
    channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes());
}
```

### 5. 工作队列总结

1、循环分发：消费者端在信道上打开消息应答机制，并确保能返回接收消息的确认信息，这样可以保证消费者发生故障也不会丢失消息。

2、消息持久化：服务器端和客户端都要指定队列的持久化和消息的持久化，这样可以保证RabbitMQ重启，队列和消息也不会丢失。

3、公平分发：指定消费者接收的消息个数，避免出现消息均匀推送出现的资源不合理利用的问题。



## 发布订阅模式(fanout)

举个用户注册的列子

> 门户网站，用户在注册完后一般都会发送消息通知用户注册成功（失败）。
>
> 如果在一个系统中，用户注册信息有邮箱、手机号，那么在注册完后会向邮箱和手机号都发送注册完成信息（假设都发送）。
>
> 利用 MQ 实现业务异步处理，如果是用工作队列的话，就会声明一个注册信息队列。注册完成之后生产者会向队列提交一条注册数据，消费者取出数据同时向邮箱以及手机号发送两条消息。但是实际上邮箱和手机号信息发送实际上是不同的业务逻辑，不应该放在一块处理。(应该放到不同的队列中，由不同的消费者消费)
>
> 这个时候就可以利用发布/订阅模式将消息发送到转换机（EXCHANGE），声明两个不同的队列（邮箱、手机），并绑定到交换机。这样生产者只需要发布一次消息，两个队列都会接收到消息发给对应的消费者，大致如下图所示。

![image-20200721232040465](..\..\files\image-20200721232040465.png)

###  什么是发布订阅模式

简单解释就是，可以将消息发送给不同类型的消费者。做到**发布一次，消费多个**。下图取自于官方网站（RabbitMQ）的发布/订阅模式的图例：

<img src="..\..\files\image-20200722142130944.png" alt="image-20200722142130944" style="zoom:80%;" />


 P 表示为生产者、 X 表示交换机、C1C2 表示为消费者，红色表示队列。



### 总结

首先相对于工作模式，发布订阅模式引入了交换机的概念，相对其类型上更加灵活广泛一些。通过上文我们可以总结如下：

- 1.生产者不是直接操作队列，而是将数据发送给交换机，由交换机将数据发送给与之绑定的队列。从不加特定参数的运行结果中可以看到，两种类型的消费者（`email`，`sms`）都收到相同数量的消息。
- 2.必须声明交换机，并且设置模式：`channel.exchangeDeclare(EXCHANGE_NAME, "fanout")`，其中 `fanout` 指分发模式（将每一条消息都发送到与交换机绑定的队列）。
- 3.队列必须绑定交换机：`channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");`

生产者发送消息到交换机，多个消费者声明多个队列，与交换机进行绑定，队列中的消息可以被所有消费者消费，类似于QQ群消息



## 路由模式（direct）

### 什么是路由模式

官网链接：https://msd.misuland.com/pd/2884250137616455578

路由模式跟发布订阅模式类似，然后在订阅模式的基础上加上了类型，订阅模式是分发到所有绑定到交换机的队列，路由模式只分发到绑定在交换机上面指定路由键的队列，我们可以看一下下面这张图：

![img](..\..\files\1104426-20190902194402779-1665800428.png)

> P 表示为生产者、 X 表示交换机、C1C2 表示为消费者，红色表示队列。
>
> 上图是一个结合日志消费级别的配图，在路由模式它会把消息路由到那些 binding key 与 routing key 完全匹配的 Queue 中，此模式也就是 Exchange 模式中的 direct 模式。
>
> 以上图的配置为例，我们以 routingKey="error" 发送消息到 Exchange，则消息会路由到Queue1（amqp.gen-S9b…，这是由RabbitMQ自动生成的Queue名称）和Queue2（amqp.gen-Agl…）。如果我们以 routingKey="info" 或 routingKey="warning" 来发送消息，则消息只会路由到 Queue2。如果我们以其他 routingKey 发送消息，则消息不会路由到这两个 Queue 中。



```
相对于发布订阅模式，我们可以看到不再是广播似的接收全部消息，而是有选择性的消费。
注意：exchangeDeclare() 方法 exchange 类型为 direct
先运行两个消费者，再运行生产者。如果没有提前将队列绑定到交换机，那么直接运行生产者的话，消息是不会发到任何队列里的。
```

###  路由模式总结

1、两个队列消费者设置的路由不一样，接收到的消息就不一样。路由模式下，决定消息向队列推送的主要取决于路由，而不是交换机了。　　

2、该模式必须设置交换机，且声明路由模式 **channel.exchangeDeclare(EXCHANGE_NAME, "direct");**

> 生产者发送消息到交换机，同时定义了一个路由 routingKey，多个消费者声明多个队列，与交换机进行绑定，同时定义路由 routingKey，只有路由 routingKey相同的消费者才能消费数据



## 主题模式

​		从前面的几篇我们依次经历了 exchange 模式从 fanout > direct 的转变过程，在 fanout 时，我们只能进行简单的广播，对应类型比较单一，使用 direct 后，消费者则可以进行一定程度的选择，但是，direct 还是有局限性，路由不支持多个条件。

怎么讲呢？

​		direct 不支持匹配 routingKey，一但绑定了就是绑定了，而 topic 主题模式支持规则匹配，只要符合 routingKey 就能发送到绑定的队列上。

### 什么是主题模式

官方链接：http://www.rabbitmq.com/tutorials/tutorial-five-java.html

> topics 主题模式跟 routing 路由模式类似，只不过路由模式是指定固定的路由键 routingKey，而主题模式是可以模糊匹配路由键 routingKey，类似于SQL中 = 和 like 的关系。

![img](https://img2018.cnblogs.com/blog/1104426/201909/1104426-20190902194941919-489860393.png)
P 表示为生产者、 X 表示交换机、C1C2 表示为消费者，红色表示队列。

 

topics 模式与 routing 模式比较相近，topics 模式不能具有任意的 routingKey，必须由
一个英文句点号“.”分隔的字符串（我们将被句点号“.”分隔开的每一段独立的字符串称为一个单词），比如 "lazy.orange.fox"。topics routingKey 中可以存在两种特殊字符"\*" 与“#”，用于做模糊匹配，其中“*”用于匹配一个单词，“#”用于匹配多个单词（可以是零个）。

> "*" 表示任何一个词
> "#" 表示0或1个词

以上图中的配置为例：

如果一个消息的 routingKey 设置为 “xxx.orange.rabbit”，那么该消息会同时路由到 Q1 与 Q2，routingKey="lazy.orange.fox”的消息会路由到Q1与Q2；

routingKey="lazy.brown.fox”的消息会路由到 Q2；

routingKey="lazy.pink.rabbit”的消息会路由到 Q2（只会投递给Q2一次，虽然这个routingKey 与 Q2 的两个 bindingKey 都匹配）；

routingKey="quick.brown.fox”、routingKey="orange”、routingKey="quick.orange.male.rabbit”的消息将会被丢弃，因为它们没有匹配任何bindingKey。

### 总结

> 1、topic 相对于之前几种算是比较复杂了，简单来说，就是每个队列都有其关心的主题，所有的消息都带有一个“标题”(RouteKey)，exchange 会将消息转发到所有关注主题能与 routeKey 模糊匹配的队列。
>
> 2、在进行绑定时，要提供一个该队列关心的主题，如“#.sscai.#”表示该队列关心所有涉及 sscai 的消息(一个 routeKey 为 "club.sscai.tmax”的消息会被转发到该队列)。
>
> 3、"#”表示0个或若干个关键字，“”表示一个关键字。如“club.”能与“club.sscai”匹配，无法与“club.sscai.xxx”匹配；但是“club.#”能与上述两者匹配。
>
> 4、同样，如果 exchange 没有发现能够与 routeKey 匹配的 Queue，则会抛弃此消息。































