### 一、测试使用 File_Roll Sink（将数据存储到HBase）
#### 1.1，编辑[vi hbase-sink-conf.properties]配置文件
```bash
# common
a1.sources = r1
a1.channels = c1
a1.sinks = s1

# source
a1.sources.r1.type = netcat
a1.sources.r1.bind = 0.0.0.0
a1.sources.r1.port = 9191
  
# channel
a1.channels.c1.type = memory
  
# sink
a1.sinks.s1.type = hbase2
# hbase表名
a1.sinks.s1.table = foo_table
# 列族
a1.sinks.s1.columnFamily = bar_cf
a1.sinks.s1.serializer = org.apache.flume.sink.hbase2.RegexHBase2EventSerializer
# hbase zookeeper集群
a1.sinks.s1.zookeeperQuorum = server002:2181,server003:2181,server004:2181
  
# bin
a1.sources.r1.channels = c1
a1.sinks.s1.channel = c1
```

#### 1.2，启动Flume
```bash
$ flume-ng agent --conf conf --conf-file /home/apache-flume-1.9.0-bin/conf/hbase-sink-conf.properties --name a1 -Dflume.root.logger=INFO,console  # linux使用
$ flume-ng  agent -conf ../conf  -conf-file ../conf/hbase-sink-conf.properties -name a1 -property flume.root.logger=INFO,console                  # windows使用
```

#### 1.3，测试我们上面监听在netcat的Flume（如果没有nc命令，请安装：yum -y install nmap-ncat.x86_64）
```bash
$ nc 127.0.0.1 9191              # 另起一个xshell窗口，连接Flume，然后随便输入数据。它默认列名是payload，值就是比输入的那个（最后去HBase上看foo_table表是否有数据）
```