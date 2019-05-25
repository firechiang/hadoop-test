#### 一、环境准备
```bash
# 节点分布
-----------|------------|------------|
server001  | server002  |  server003 |
-----------|------------|------------|
     Y     |      Y     |      Y     |
-----------|------------|------------|


$ wget -P /home/tools http://mirror.bit.edu.cn/apache/kafka/2.2.0/kafka_2.12-2.2.0.tgz
$ cd /home/tools
$ sudo tar -zxvf kafka_2.12-2.2.0.tgz -C ../                    # 解压到上层目录
```

#### 二、修改[vi server.properties]
```bash
# 配置kafka日志目录(注意：手动创建该目录)
log.dirs=/home/kafka_2.12-2.2.0/logs
# 配置ZooKeeper集群
zookeeper.connect=server002:2181,server003:2181,server004:2181
```

#### 三、分发安装包到各个节点
```bash
$ scp -r /home/kafka_2.12-2.2.0 root@server002:/home/kafka_2.12-2.2.0
$ scp -r /home/kafka_2.12-2.2.0 root@server003:/home/kafka_2.12-2.2.0
```

#### 四、修改[vi server.properties]每个节点 broker.id(注意：broker.id代表群节点的唯一标识，不能有相同的)
```bash
broker.id=1
```

#### 五、配置各个节点的环境变量[vi ~/.bashrc]在末尾添加如下内容
```bash
export KAFKA_HOME=/home/kafka_2.12-2.2.0
PATH=$PATH:$KAFKA_HOME/bin                                      # linux以 : 号隔开，windows以 ; 号隔开

$ source ~/.bashrc                                              # （系统重读配置）在各个机器上执行使配置文件生效（实验：敲个hdf然后按Tab键，如果补全了说明配置成功了）
$ echo $KAFKA_HOME
```

#### 六、到各个节点上指定配置启动kafka
```bash
$ kafka-server-start.sh -daemon /home/kafka_2.12-2.2.0/config/server.properties
$ kafka-server-stop.sh                                          # 停止kafka
```

#### 七、简单使用
```bash
# 查看kafka-topics.sh命令使用帮助
$ kafka-topics.sh --help
# 创建组题 --bootstrap-server(kafka服务地址)，--replication-factor(副本数)，--partitions(分区数)，--topic(主题名称)
$ kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 2 --partitions 2 --topic test-test-1
# 修改主题的分区数(注意：副本数不能更改，只能在创建主题时指定)
$ kafka-topics.sh --alter --bootstrap-server localhost:9092 --partitions 2 --topic test-test-1
# 查看主题列表
$ kafka-topics.sh --list --bootstrap-server localhost:9092      
# 查看 test_test 主题详细信息(包含分区副本放置策略)
$ kafka-topics.sh --describe --bootstrap-server localhost:9092 --topic test-test-1

# 创建主题时指定分区副本所在机器（参数 --replica-assignment 说明：逗号区分分区(每一个逗号分开的区域代表一个分区，从0开始)，冒号区分broker.id（副本所在的机器id））
# 创建名字叫test-test-100的topic，副本放置策略是：0号分区放1号机器和2号机器各一个副本，1号分区放1号机器和3号机器各一个副本，2号分区放2号机器和3号机器各一个副本，
$ kafka-topics.sh --create --bootstrap-server localhost:9092 --topic test-test-100 --replica-assignment 1:2,1:3,2:3
# 查看 test-test-100 主题详细信息(包含分区副本放置策略)
$ kafka-topics.sh --describe --bootstrap-server localhost:9092 --topic test-test-100

# 修改主题的分区副本所在机器（注意：只可以增加分区）
$ kafka-topics.sh --alter --bootstrap-server localhost:9092 --topic test-test-100 --partitions 4 --replica-assignment 1:2,2:3,1:2,2:3

# 创建记录时间戳的topic(--config message.timestamp.type=LogAppendTime（表示broker接收到这条消息的时间），CreateTime表示（producer创建这条消息的时间）)
# 如果想要延迟消费消息，一定要记录时间戳
$ kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 2 --partitions 2 --topic test-test-1 --config message.timestamp.type=LogAppendTime
```

#### 八、发送消息到主题(连接成功后顺便填写数据发送即可)
```bash
$ kafka-console-producer.sh --broker-list localhost:9092 --topic test-test-1
```

#### 九、启动消费者(另开一个xshell窗口)，然后在生产者端填写数据发送，看看消费者会不会打印
```bash
$ kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test-test-1 --from-beginning
```