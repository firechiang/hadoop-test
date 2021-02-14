#### [一、单节点搭建以及简单测试使用][1]
#### [二、集群搭建以及简单测试使用][2]
#### kafka核心简介
```bash
1，kafka没有主从模型，所有broker地位相同，broker信息数据均在zk中维护，并在producer(生产者)和consumer(消费者)之间共享
2，kafka的load balance(负载均衡)策略允许producer(生产者)动态发现broker(kafka集群节点)
3，producer(生产者)维护了broker(kafka集群节点)的连接池，并通过zk的warcher call(监听)机制实时更新
4，producer可以选择同步或异步的方式向broker(kafka集群节点)发送消息
5，消息数据都是被即刻写入OS(系统)内核页，并缓存最后flush到磁盘(可以配置)
6，消息被消费后，还可长时间驻留，如果有必要，可重复消费
7，对分组消息使用消息set，防止网络过载。生产者和消费者是push-pull模型
8，使用消费者保存消息元数据。消费者状态默认保存在zk当中，也可保存在其它OLTP当中
9，消息可压缩，可在消息header(头)中使用compress type标识
10，offset（偏移量）在单个分区里面是唯一的
```

#### kafka速度快的原因
 - 生产者的消息是批量发送的，不是一条一条发送的
 
#### kafka优化建议
 - 一个分区对应一个消费者 

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
 
#### kafka消息副本简介
```bash
1，每个分区有n个副本，可以承受n-1节点故障，每个副本都有自己的leader，其余都是follow
2，leader故障时，消息写入本地log或在producer收到ack消息前，producer将消息发送给新的分区新的leader
```
#### kafka消息副本模型
##### 同步复制
```bash
producer从zk中找到副本的leader，并发送message，消息立即写入本地log，而且follow开pull(拉取leader的消息)，
每个follow将消息写入各自本地的log后，向leader发送确认回执，leader在收到所有的follow确认回执和本地副本的写入工作均完成后，再向producer发送确认回执
```
##### 异步复制
```bash
leader的本地log写入完成，立即向producer发送确认回执
```
[1]: https://github.com/firechiang/hadoop-test/tree/master/kafka/docs/setup-single.md
[2]: https://github.com/firechiang/hadoop-test/tree/master/kafka/docs/setup-cluster.md
