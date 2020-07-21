# RabbitMQ几种队列模式



路由模式

[1.什么是路由模式](#direct)

[2. 代码部分](https://www.cnblogs.com/niceyoo/p/11448093.html#h2)[2.1 日志生产者](https://www.cnblogs.com/niceyoo/p/11448093.html#h21)[2.2 info消费者](https://www.cnblogs.com/niceyoo/p/11448093.html#h22info)[2.3 error消费者](https://www.cnblogs.com/niceyoo/p/11448093.html#h23error)[2.4 运行截图](https://www.cnblogs.com/niceyoo/p/11448093.html#h24)[3. 路由模式总结](https://www.cnblogs.com/niceyoo/p/11448093.html#h3)

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

##  什么是发布订阅模式

简单解释就是，可以将消息发送给不同类型的消费者。做到**发布一次，消费多个**。下图取自于官方网站（RabbitMQ）的发布/订阅模式的图例：

![img](https:////upload-images.jianshu.io/upload_images/8574472-ec8a48ed71a49374.png?imageMogr2/auto-orient/strip|imageView2/2/w/459/format/webp)


 P 表示为生产者、 X 表示交换机、C1C2 表示为消费者，红色表示队列。



## 总结

首先相对于工作模式，发布订阅模式引入了交换机的概念，相对其类型上更加灵活广泛一些。通过上文我们可以总结如下：

- 1.生产者不是直接操作队列，而是将数据发送给交换机，由交换机将数据发送给与之绑定的队列。从不加特定参数的运行结果中可以看到，两种类型的消费者（`email`，`sms`）都收到相同数量的消息。
- 2.必须声明交换机，并且设置模式：`channel.exchangeDeclare(EXCHANGE_NAME, "fanout")`，其中 `fanout` 指分发模式（将每一条消息都发送到与交换机绑定的队列）。
- 3.队列必须绑定交换机：`channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");`

生产者发送消息到交换机，多个消费者声明多个队列，与交换机进行绑定，队列中的消息可以被所有消费者消费，类似于QQ群消息



## 路由模式

### <span id="direct">什么是路由模式</span>























