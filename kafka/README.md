#### [一、单节点搭建以及简单测试使用][1]
#### [二、集群搭建以及简单测试使用][2]
#### [三、Kafka Connect（连接器）简单使用（不推荐生产使用）][3]
#### [四、Kafka 管理监控部署以及简单使用（推荐生产使用）][4]
#### [五、Kafka 服务端和客户端安全连接配置（不推荐生产使用（有性能损耗），建议生产使用内网连接，Kafka不提供外网访问即可）][5]
#### kafka核心简介
 - Broker（一般只Kafka的部署节点）
 - Follwer（主要用于备份消息数据）
 - Leader（用于处理消息的接收和消费等请求)
 - kafka的load balance(负载均衡)策略允许producer(生产者)动态发现broker(kafka集群节点)
 - producer(生产者)维护了broker(kafka集群节点)的连接池，并通过zk的warcher call(监听)机制实时更新
 - producer可以选择同步或异步的方式向broker(kafka集群节点)发送消息
 - 消息数据都是被即刻写入OS(系统)内核页，并缓存最后flush到磁盘(可以配置)
 - 消息被消费后，还可长时间驻留，如果有必要，可重复消费
 - 对分组消息使用消息set，防止网络过载。生产者和消费者是push-pull模型
 - 使用消费者保存消息元数据。消费者状态默认保存在zk当中，也可保存在其它OLTP当中
 - 消息可压缩，可在消息header(头)中使用compress type标识
 - kafka的分区是相互备份的
 - offset（偏移量）在单个分区里面是唯一的
 - 生产者的消息是批量发送的，不是一条一条发送的
 - producer向Leader发送消息，消息立即写入本地log，每个Follow节点pull（拉取）Leader的消息副本，
写入本地的log后，向leader发送确认回执，leader在收到所有的follow确认回执和本地副本的写入工作均完成后，再向producer发送确认回执

#### kafka Leader选举策略（注意：Leader选举是相对于客户端的，就是给了好几个kafka的服务地址，客户端具体要连哪个） 
```bash
1，首先第一次连接Kafka集群由Controller分配Leader节点给客户端（Controller就是最先注册到Zookeeper上的节点）
2，每个Leader节点都会维护一组ISR节点（这些节点就是Leader的数据备份节点，简单来说就是Leader的Follwer节点）
3，当Leader节点挂了以后，Controller就会从Leader的ISR节点中选一个最快的节点给客户端（所谓最快就是ISR节点中最先注册到Zookeeper上的节点）
4，Controller是来监控和管理Broker的，有挂了的Broker将其提除，还有选举Leader
```

#### 如果Leader和ISR节点都挂了，Kafka可以进行unclean leader（选举）注意：unclean leader需要在配置文件里面配置，生产环境建议禁用unclean leader（选举）
```bash
1，第一种unclean leader（选举）模式是死等Leader和它的ISR节点中有一个节点复活（注意：在此期间客户端将无法使用）
2，第二种unclean leader（选举）模式是从Kafka集群中任选一个存活的Broker节点做为Leader给客户端用（注意：这种可能会丢失数据，因为新的Leader没有客户端以前发送的消息数据）
```

#### kafka服务器优化建议
 - 调整服务器的文件描述符数量（建议在十万以上），公式（number_of_partitions=分区数量）* (partition_size=分区大小默认25G / segment_size=默认1G)
 - 禁用服务器的swap
 - 服务器的pagecache尽量分配的与大多数日志的激活日志段大小一致
 - 调整服务器的最大套接字缓冲区大小
 - 单个节点的分区最好小于2000个（所有的Topic加起来），每个分区的大小不要大于25G
 - 单个个Topic的分区数量建议和最大的消费者组中消费者数据一致（遵循一个分区对应一个消费者的原则）

#### kafka 4大客户端
 - Producers（生产者）
 - Consumers（消费者）
 - Stream processors（流处理）
 - Connectors（用于链接数据库DB）
 
#### kafka客户端API
 - AdmainClient API允许管理Topic，Broker以及其他Kafka对象
 - Producer API发布消息到1个多个Topic
 - Consumer API订阅一个或多个Topic，并处理产生的消息
 - Stream API 高效的将输入流转到输出流
 - Connectors API 从一些源系统或应用程序中拉取数据到Kafka 
 
[1]: https://github.com/firechiang/hadoop-test/tree/master/kafka/docs/setup-single.md
[2]: https://github.com/firechiang/hadoop-test/tree/master/kafka/docs/setup-cluster.md
[3]: https://github.com/firechiang/hadoop-test/tree/master/kafka/docs/connect-simple.md
[4]: https://github.com/firechiang/hadoop-test/tree/master/kafka/docs/setup-monitor.md
[5]: https://github.com/firechiang/hadoop-test/tree/master/kafka/docs/setup-security.md