## 关于 auto-offset-reset
auto.offset.reset 配置有3个值可以设置，分别如下：

earliest：当各分区下有已提交的 offset 时，从提交的 offset 开始消费；无提交的 offset时，从头开始消费；
latest：当各分区下有已提交的 offset 时，从提交的 offset 开始消费；无提交的 offset 时，消费新产生的该分区下的数据；
none: topic各分区都存在已提交的 offset 时，从 offset 后开始消费；只要有一个分区不存在已提交的 offset，则抛出异常;

默认建议用 earliest, 设置该参数后 kafka出错后重启，找到未消费的offset可以继续消费。
而 latest 这个设置容易丢失消息，假如 kafka 出现问题，还有数据往topic中写，这个时候重启kafka，这个设置会从最新的offset开始消费, 中间出问题的哪些就不管了。
none 这个设置没有用过，兼容性太差，经常出问题。

## 验证 Kafka 的topic 列表
看 xiaoha 这个topic 是否正常被创建, 执行 bin 目录下查看 topic 列表的 kafka-topics.sh 脚本：

```
bin/kafka-topics.sh --list --zookeeper localhost:2181
```



### Partition

每一个**topic**对应有多个**分区(Partition),实际上partition会**分布在不同的broker中

![image-20210701092751277](http://micheal.wang:10020/mongo/read/60dd255f29eb83032d5a169e)



由此得知：**Kafka是天然分布式的**



生产者往topic里边丢数据，实际上这些数据会分到不同的partition上，这些partition存在不同的broker上。

### 分布式肯定会带来问题：“万一其中一台broker(Kafka服务器)出现网络抖动或者挂了，怎么办？”

Kafka是这样做的：我们数据存在不同的partition上，那kafka就把这些partition做**备份**。

比如，现在我们有三个partition，分别存在三台broker上。每个partition都会备份，这些备份散落在**不同**的broker上。

红色块的partition代表的是**主**分区，紫色的partition块代表的是**备份**分区。生产者往topic丢数据，是与**主**分区交互，消费者消费topic的数据，也是与主分区交互。

**备份分区仅仅用作于备份，不做读写。\**如果某个Broker挂了，那就会选举出其他Broker的partition来作为主分区，这就实现了\**高可用**。

Kafka是将partition的数据写在**磁盘**的(消息日志)，不过Kafka只允许**追加写入**(顺序访问)，避免缓慢的随机 I/O 操作。

- Kafka也不是partition一有数据就立马将数据写到磁盘上，它会先**缓存**一部分，等到足够多数据量或等待一定的时间再批量写入(flush)。

生产者可以有多个，消费者也可以有多个。像上面图的情况，是一个消费者消费三个分区的数据。多个消费者可以组成一个**消费者组**。本来是一个消费者消费三个分区的，现在我们有消费者组，就可以**每个消费者去消费一个分区**（也是为了提高吞吐量）



![消费者组的每个消费者会去对应partition拿数据](http://micheal.wang:10020/mongo/read/60dd260829eb83032d5a16a3)



按图上所示的情况，这里想要说明的是：

- 如果消费者组中的某个消费者挂了，那么其中一个消费者可能就要消费两个partition了
- 如果只有三个partition，而消费者组有4个消费者，那么一个消费者会空闲
- 如果多加入一个**消费者组**，无论是新增的消费者组还是原本的消费者组，都能消费topic的全部数据。（消费者组之间从逻辑上它们是**独立**的）







