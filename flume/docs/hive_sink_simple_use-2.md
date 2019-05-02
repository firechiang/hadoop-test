### 一、测试使用 Hive Sink（将数据存储到Hive）

#### 1.1，创建表（使用Metastore客户端，就是直接使用hive命令进入的客户端）
```bash
# Flume插入Hive数据时默认使用事务，所以建表时需指明支持事务，要支持事务需设置如下Hive参数
$ set hive.support.concurrency = true                                    # 开启并发支持
$ set hive.enforce.bucketing = true                                      # 开启分桶支持
$ set hive.exec.dynamic.partition.mode = nonstrict                       # 设置动态分区非严格模式
$ set hive.txn.manager = org.apache.hadoop.hive.ql.lockmgr.DbTxnManager  # 开启事务支持

# 建表
create table hive_user_2
(
    id int,
    name string,
    age int
)
clustered by (id) into 2 buckets               # 根据id分桶，个数为2
row format delimited
fields terminated by ','                       # 数据根据逗号分隔
stored as orc
tblproperties ('transactional'='true');        # 支持事务
```
#### 1.2，编辑[vi hive-sink-conf_2.properties]配置文件
```bash
# common
a1.sources=r1
a1.channels=c1
a1.sinks=s1

a1.sources.r1.type=netcat
a1.sources.r1.bind=0.0.0.0
a1.sources.r1.port=9191

# sink
a1.sinks.s1.type=hive
a1.sinks.s1.hive.metastore=thrift://server003:9083
a1.sinks.s1.hive.database=test
a1.sinks.s1.hive.table=hive_user_2
a1.sinks.s1.serializer=DELIMITED
a1.sinks.s1.serializer.delimiter="\t"
a1.sinks.s1.serializer.serdeSeparator='\t'
# 要获取的数据字段（注意分区字段不要写到这里）
a1.sinks.s1.serializer.fieldnames=id,name,age

# channel
a1.channels.c1.type = memory
a1.channels.c1.capacity = 1000000	
a1.channels.c1.transactionCapacity = 20000	
a1.channels.c1.keep-alive = 30

# bin
a1.sources.r1.channels=c1
a1.sinks.s1.channel=c1
```

#### 1.3，下载 hive-hcatalog-streaming-3.1.1.jar 到Flume lib目录
```bash
wget http://central.maven.org/maven2/org/apache/hive/hcatalog/hive-hcatalog-streaming/3.1.1/hive-hcatalog-streaming-3.1.1.jar
```
#### 1.4，简单测试使用
```bash
$ flume-ng agent --conf conf --conf-file /home/apache-flume-1.9.0-bin/conf/hive-sink-conf_2.properties --name a1 -Dflume.root.logger=INFO,console  # linux使用
$ flume-ng  agent -conf ../conf  -conf-file ../conf/hive-sink-conf_2.properties -name a1 -property flume.root.logger=INFO,console                  # windows使用

# 测试我们上面监听在netcat的Flume（如果没有nc命令，请安装：yum -y install nmap-ncat.x86_64）
$ nc 127.0.0.1 9191              # 另起一个xshell窗口，连接Flume
$ 1	jiang	34                   # 输入数据（注意：数据以（按Tab键）隔开）
```