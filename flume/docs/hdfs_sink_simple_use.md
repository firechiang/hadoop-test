### 一、测试使用 HDFS Sink（将数据存储到HDFS）
#### 1.1 将Hadoop集群的hdfs-site.xml、core-site.xml两个配置文件复制到 flume安装目录的conf目录
```bash
$ scp /home/hadoop-3.1.2/etc/hadoop/hdfs-site.xml /home/apache-flume-1.9.0-bin/conf/hdfs-site.xml
$ scp /home/hadoop-3.1.2/etc/hadoop/core-site.xml /home/apache-flume-1.9.0-bin/conf/core-site.xml
```
#### 1.2 将hadoop-hdfs-3.1.2.jar复制到 Flume  lib目录
```bash
$ wget http://central.maven.org/maven2/org/apache/hadoop/hadoop-hdfs/3.1.2/hadoop-hdfs-3.1.2.jar
```

#### 1.3，编辑[vi hdfs-sink-conf.properties]配置文件（注意：实际生成中使用配置还需优化，比如：每次产生记录都会生成一个小文件，我们要尽量避免这个问题，因为我们的Block默认是128M的）
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
a1.sinks.s1.type = hdfs
# hdfs存储目录，这个目录会自动创建
a1.sinks.s1.hdfs.path = hdfs://mycluster/flume/events/%y-%m-%d/%H%M/%S
# 文件前缀
a1.sinks.s1.hdfs.filePrefix = events-
a1.sinks.s1.hdfs.round = true
a1.sinks.s1.hdfs.roundValue = 10
a1.sinks.s1.hdfs.roundUnit = minute
# 使用本地时间戳，不然我们在发送数据的时候需要在Head里面加useLocalTimeStamp
a1.sinks.s1.hdfs.useLocalTimeStamp = true
  
# bin
a1.sources.r1.channels = c1
a1.sinks.s1.channel = c1
```

#### 1.4，简单测试使用
```bash
$ flume-ng agent --conf conf --conf-file /home/apache-flume-1.9.0-bin/conf/hdfs-sink-conf.properties --name a1 -Dflume.root.logger=INFO,console    # linux使用
$ flume-ng  agent -conf ../conf  -conf-file ../conf/hdfs-sink-conf.properties  -name a1  -property flume.root.logger=INFO,console                  # windows使用


# 测试我们上面监听在netcat的Flume（如果没有nc命令，请安装：yum -y install nmap-ncat.x86_64）
$ nc 127.0.0.1 9191   # 另起一个xshell窗口，连接Flume，然后顺便输入数据，看看有没有发送过去（接收端我们配的是console，数据会打印到控制台）
```

#### 1.6 如果报Flume没有HDFS读写权限错误，解决方案如下（到HDFS集群上去执行）
```bash
$ hadoop fs -chmod -R 777 /flume/          # 因为我们上面配的HDFS目录是以 /flume 为根目录，所以放开/flume目录权限即可
```