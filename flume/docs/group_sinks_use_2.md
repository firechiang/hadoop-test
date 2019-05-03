### 一、多个Sink组合起来，使用Sink选择器来做容错，容灾（只有一个能被使用，其它待命）
#### 1.1，编辑[vi sink-group-use-2.properties]配置文件
```bash
# common
a1.sources = r1
a1.channels = c1
a1.sinks = s1 s2 s3

# source
a1.sources.r1.type = netcat
a1.sources.r1.bind = 0.0.0.0
a1.sources.r1.port = 9191
  
# channel
a1.channels.c1.type = memory
  
# sink
a1.sinks.s1.type = logger
a1.sinks.s2.type = logger
a1.sinks.s3.type = logger

# sink group
a1.sinkgroups = g1
a1.sinkgroups.g1.sinks = s1 s2 s3
# 容错类型
a1.sinkgroups.g1.processor.type = failover
# 优先级越高，最先被使用
a1.sinkgroups.g1.processor.priority.s1 = 5
a1.sinkgroups.g1.processor.priority.s2 = 10
a1.sinkgroups.g1.processor.priority.s3 = 10
a1.sinkgroups.g1.processor.maxpenalty = 10000
  
# bin
a1.sources.r1.channels = c1
a1.sinks.s1.channel = c1
a1.sinks.s2.channel = c1
a1.sinks.s3.channel = c1
```

#### 1.2，启动Flume
```bash
$ flume-ng agent --conf conf --conf-file /home/apache-flume-1.9.0-bin/conf/sink-group-use-2.properties --name a1 -Dflume.root.logger=INFO,console  # linux使用
$ flume-ng  agent -conf ../conf  -conf-file ../conf/sink-group-use-2.properties -name a1 -property flume.root.logger=INFO,console                  # windows使用
```

#### 1.3，测试我们上面监听在netcat的Flume（如果没有nc命令，请安装：yum -y install nmap-ncat.x86_64）
```bash
$ nc 127.0.0.1 9191              # 另起一个xshell窗口，连接Flume，然后随便输入数据
```