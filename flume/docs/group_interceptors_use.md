### 一、多个拦截器组合使用，就是头里面会自动添加多个属性（其它拦截器请看官方文档）
#### 1.1，编辑[vi group_interceptors_use.properties]配置文件
```bash
# common
a1.sources = r1
a1.channels = c1
a1.sinks = s1 s2 s3

# source
a1.sources.r1.type = netcat
a1.sources.r1.bind = 0.0.0.0
a1.sources.r1.port = 9191

a1.sources.r1.interceptors = i1 i2
# Host拦截器（它会自动在信息头里面加个IP）
a1.sources.r1.interceptors.i1.type = host
# 消息头key的名称
a1.sources.r1.interceptors.i1.hostHeader = hostname
# 时间戳拦截器（它会自动在信息头里面加个时间戳）
a1.sources.r1.interceptors.i2.type = timestamp
  
# channel
a1.channels.c1.type = memory
  
# sink
a1.sinks.s1.type = logger
a1.sinks.s2.type = logger
a1.sinks.s3.type = logger

# sink group
a1.sinkgroups = g1
a1.sinkgroups.g1.sinks = s1 s2 s3
# 负载均衡的类型
a1.sinkgroups.g1.processor.type = load_balance
a1.sinkgroups.g1.processor.backoff = true
a1.sinkgroups.g1.processor.selector = random
  
# bin
a1.sources.r1.channels = c1
a1.sinks.s1.channel = c1
a1.sinks.s2.channel = c1
a1.sinks.s3.channel = c1
```

#### 1.2，启动Flume
```bash
$ flume-ng agent --conf conf --conf-file /home/apache-flume-1.9.0-bin/conf/group_interceptors_use.properties --name a1 -Dflume.root.logger=INFO,console  # linux使用
$ flume-ng  agent -conf ../conf  -conf-file ../conf/group_interceptors_use.properties -name a1 -property flume.root.logger=INFO,console                  # windows使用
```

#### 1.3，测试我们上面监听在netcat的Flume（如果没有nc命令，请安装：yum -y install nmap-ncat.x86_64）
```bash
$ nc 127.0.0.1 9191              # 另起一个xshell窗口，连接Flume，然后随便输入数据，看看打印的消息头里面是否有hostname和时间戳属性
```