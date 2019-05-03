### 一、测试复制通道选择器（当一个源绑定了多个通道，那么一份数据会同时发给多个通道，应用场景：一份数据要被多个集群处理
#### 1.1，编辑[vi source-select-conf.properties]配置文件
```bash
# common
a1.sources = r1
a1.channels = c1 c2 c3
a1.sinks = s1 s2 s3

# source
# 源的复制选择器（当一个源绑定了多个通道，那么一份数据会同时发给多个通道）
a1.sources.r1.selector.type = replicating
a1.sources.r1.selector.optional = c3
a1.sources.r1.type = netcat
a1.sources.r1.bind = 0.0.0.0
a1.sources.r1.port = 9191
  
# channel
a1.channels.c1.type = memory
a1.channels.c2.type = memory
a1.channels.c3.type = memory
  
# sink
a1.sinks.s1.type = logger
a1.sinks.s2.type = logger
a1.sinks.s3.type = logger
  
# bin
# r1源绑定到 c1 c2 c3 三个通道
a1.sources.r1.channels = c1 c2 c3
a1.sinks.s1.channel = c1
a1.sinks.s2.channel = c2
a1.sinks.s3.channel = c3
```

#### 1.2，启动Flume
```bash
$ flume-ng agent --conf conf --conf-file /home/apache-flume-1.9.0-bin/conf/source-select-conf.properties --name a1 -Dflume.root.logger=INFO,console  # linux使用
$ flume-ng  agent -conf ../conf  -conf-file ../conf/source-select-conf.properties -name a1 -property flume.root.logger=INFO,console                  # windows使用
```

#### 1.3，测试我们上面监听在netcat的Flume（如果没有nc命令，请安装：yum -y install nmap-ncat.x86_64）
```bash
$ nc 127.0.0.1 9191              # 另起一个xshell窗口，连接Flume，然后随便输入数据
```