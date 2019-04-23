### 一、Flume数据流程图
![image](https://github.com/firechiang/hadoop-test/blob/master/flume/image/2_flume_framework.png)
```bash
# 源码流程
1，Source生成Event，调用ChannelProcessor的方法，将Event，put到Channel去。Source调用流程：Source --> Event --> ChannelProcessor --> InterceptorChain(e) --> Channel(s);

2，Sink连接到Channel，消费里面Event，将其发送到desctination(Sink或Agent)，有很多相应的Sink类型。
   Sink可以根据SinkGroup和SinkProcessor进行分组，通过Processor由SinkRunner轮询出来。
   Sink的Processor()方法只能由一个线程访问。

3，Channel连接Source(Event Procuder)和Sink(Event Consumer)，本质上Channel就是Buffer缓冲区，支持事物处理，保证原子性(put + take)，Channel是线程安全的，方法有put()，take()，getTransaction()
```
### 二、Flume配置步骤说明（1：命名Agent组件，2：描述配置Source，3：描述配置Channel，4：描述配置Sink，5：绑定Source和Sink到Channel）。示例如下：
```bash
# 1，命名Agent组件（名字可以顺便取）
a1.sources = r1                            # a1是Agent的名字，它的Source（数据源）有 s1（可以配置多个用逗号隔开，比如：r1,r2）
a1.sinks = s1                              # a1是Agent的名字，它的Sink（沉槽）有 s1（可以配置多个用逗号隔开，比如：s1,s2）
a1.channels = c1                           # a1是Agent的名字，它的Channel（通道）有 c1（可以配置多个用逗号隔开，比如：c1,c2）

# 2，描述配置Source
a1.sources.r1.type = netcat                # 名字叫a1的Agent的Source（数据源）r1的type（类型）是netcat（数据源类型有很多具体看官网）
a1.sources.r1.bind = localhost             # 名字叫a1的Agent的Source（数据源）r1的 netcat 绑定的可连接IP，最好配置0.0.0.0就是所有的IP都可以访问（这个一般只有Source类型是netcat才有）
a1.sources.r1.port = 44444                 # 名字叫a1的Agent的Source（数据源）r1绑定的port（这个一般只有Source类型是netcat才有）

# 3，描述配置Channel
a1.channels.c1.type = memory               # 名字叫a1的Agent的Channel（通道）的type（类型）是memory（内存通道）（通道类型有很多具体看官网）
a1.channels.c1.capacity = 1000
a1.channels.c1.transactionCapacity = 100

# 4，描述配置Sink
a1.sinks.s1.type = logger                  # 名字叫a1的Agent的Sink（沉槽）的type（类型）是logger（日志沉槽）（沉槽类型有很多具体看官网）

# 5，绑定Source和Sink到Channel（注意：Source可以配置多个Channel，Sink只能配置一个Channel）
a1.sources.r1.channels = c1                # 将名字叫a1的Agent的Source（数据源）r1绑定到Channel（通道）c1
a1.sinks.s1.channel = c1                   # 将名字叫a1的Agent的Sink（沉槽）s1绑定到Channel（通道）c1
```

### 三、简单使用（flume-ng 命令参数说明： --conf（配置文件目录），--conf-file（配置文件地址），--name（Agent的名字），-D（指定额外的参数））
#### 3.1 测试使用 netcat 数据源
```bash
# 将上面的示例配置复制到 flume-conf.properties 配置文件，再执行以下命令启动Flume
$ flume-ng agent --conf conf --conf-file /home/apache-flume-1.9.0-bin/conf/flume-conf.properties --name a1 -DFlume.root.logger=INFO,console   # linux 使用
$ flume-ng  agent -conf ../conf  -conf-file ../conf/flume-conf.properties  -name a1  -property flume.root.logger=INFO,console                 # windows使用
# 测试我们上面监听在netcat的Flume（如果没有nc命令，请安装：yum -y install nmap-ncat.x86_64）
$ nc localhost 44444                       # 另起一个xshell窗口，连接Flume，然后顺便输入数据，看看有没有发送过去（接收端我们配的是console，数据会打印到控制台）
$ telnet localhost 44444                   # windows下连接测试，windows telnet如果没有开启到 卸载程序下 找到 打开或关闭windows下开启
```

#### 3.2 测试使用 avro 数据源（这种源一般用于文件发送和文件数据收集）
```bash
$ vi avro-conf.properties                  # 创建配置文件，内容如下：

  a1.sources = r1
  a1.channels = c1
  a1.sinks = s1

  a1.sources.r1.type = avro
  a1.sources.r1.bind = 0.0.0.0
  a1.sources.r1.port = 9999
  
  a1.channels.c1.type = memory
  
  a1.sinks.s1.type = logger
  
  a1.sources.r1.channels = c1
  a1.sinks.s1.channel = c1

$ flume-ng agent --conf conf --conf-file /home/apache-flume-1.9.0-bin/conf/avro-conf.properties --name a1 -Dflume.root.logger=INFO,console    # linux使用
$ flume-ng  agent -conf ../conf  -conf-file ../conf/avro-conf.properties  -name a1  -property flume.root.logger=INFO,console                  # windows使用
$ flume-ng avro-client -H localhost -p 9999 -F ./test.txt          # 测试发送当前目录下 test.txt到Flume avro源，内容相同的文件只能发送一次（另起一个xshell窗口）  
```

#### 3.3 测试Flume从Zookeeper读取配置文件来使用，[上传配置文件到Zookeeper的源码，先执行这个][1]（这里测试的是avro 数据源配置文件，所以配置文件内容和上面的一样）
```bash
$ flume-ng agent -z 192.168.174.1:2181 -p /flume --name a1 -Dflume.root.logger=INFO,console   # 从zookeeper中读取配置文件并启动，多个zk用逗号隔开， -p是指定文件所在zk的目录 ，--name一般是Agent名也是文件名
$ flume-ng avro-client -H localhost -p 9999 -F ./test.txt          # 测试发送当前目录下 test.txt到Flume avro源，内容相同的文件只能发送一次（另起一个xshell窗口） 
```

#### 3.4 测试使用 exec 数据源（一般用于类似于 tail -111f test.log 这样的日志跟随命令）
```bash
$ vi exec-conf.properties                  # 创建配置文件，内容如下：

  a1.sources = r1
  a1.channels = c1
  a1.sinks = s1

  a1.sources.r1.type = exec
  # 要执行的命令
  a1.sources.r1.command = tail -F /var/log/secure
  
  
  a1.channels.c1.type = memory
  
  a1.sinks.s1.type = logger
  
  a1.sources.r1.channels = c1
  a1.sinks.s1.channel = c1
  
$ flume-ng agent --conf conf --conf-file /home/apache-flume-1.9.0-bin/conf/exec-conf.properties --name a1 -Dflume.root.logger=INFO,console    # linux使用
$ flume-ng  agent -conf ../conf  -conf-file ../conf/exec-conf.properties  -name a1  -property flume.root.logger=INFO,console                  # windows使用
$ vi /var/log/secure                       # 测试往/var/log/secure文件添加内容，看看Flume是否能接收到（另起一个xshell窗口执行）
```

#### 3.5 测试使用 spooldir 数据源（一般用于监听某个文件夹下的新增文件，可用于收集日志）
```bash
$ vi spooling-conf.properties              # 创建配置文件，内容如下：

  a1.sources = r1
  a1.channels = c1
  a1.sinks = s1

  a1.sources.r1.type = spooldir
  # 要监控的文件目录（这个目录一定要存在）
  a1.sources.r1.spoolDir = /home/logs
  a1.sources.r1.fileHeader = true
  
  a1.channels.c1.type = memory
  
  a1.sinks.s1.type = logger
  
  a1.sources.r1.channels = c1
  a1.sinks.s1.channel = c1
  
$ flume-ng agent --conf conf --conf-file /home/apache-flume-1.9.0-bin/conf/spooling-conf.properties --name a1 -Dflume.root.logger=INFO,console # linux使用
$ flume-ng  agent -conf ../conf  -conf-file ../conf/spooling-conf.properties  -name a1  -property flume.root.logger=INFO,console               # windows使用 
$ vi 1.log                                 # 测试往/home/logs目录添加文件，看看Flume是否能接收到（另起一个xshell窗口执行）
```

#### 3.6 测试使用 Syslog TCP 数据源（可用于 tcp 数据收集）
```bash
$ vi syslog-tcp-conf.properties            # 创建配置文件，内容如下：  

   a1.sources = r1
   a1.channels = c1
   a1.sinks = s1

   a1.sources.r1.type = syslogtcp
   a1.sources.r1.port = 9898
   a1.sources.r1.host = localhost
   
   a1.channels.c1.type = memory
   
   a1.sinks.s1.type = logger
    
   a1.sources.r1.channels = c1
   a1.sinks.s1.channel = c1
   
$ flume-ng agent --conf conf --conf-file /home/apache-flume-1.9.0-bin/conf/syslog-tcp-conf.properties --name a1 -Dflume.root.logger=INFO,console # linux使用
$ flume-ng  agent -conf ../conf  -conf-file ../conf/syslog-tcp-conf.properties  -name a1  -property flume.root.logger=INFO,console               # windows使用 
$ nc localhost 9898                        # 另起一个xshell窗口，连接Flume，然后顺便输入数据，看看有没有发送过去（接收端我们配的是console，数据会打印到控制台）
$ telnet localhost 9898                    # windows下连接测试，windows telnet如果没有开启到 卸载程序下 找到 打开或关闭windows下开启
```

#### 3.7 测试使用 Syslog UDP 数据源（可用于 udp 数据收集）[数据发送的测试代码][2]
```bash
$ vi syslog-udp-conf.properties            # 创建配置文件，内容如下：

   a1.sources = r1
   a1.channels = c1
   a1.sinks = s1

   a1.sources.r1.type = syslogudp
   a1.sources.r1.port = 9899
   # 这个是同网段可发送数据，建议配置成 0.0.0.0 就是全部IP可发送数据
   a1.sources.r1.host = 192.168.83.137
   
   a1.channels.c1.type = memory
   
   a1.sinks.s1.type = logger
    
   a1.sources.r1.channels = c1
   a1.sinks.s1.channel = c1
   
$ flume-ng agent --conf conf --conf-file /home/apache-flume-1.9.0-bin/conf/syslog-udp-conf.properties --name a1 -Dflume.root.logger=INFO,console # linux使用
$ flume-ng  agent -conf ../conf  -conf-file ../conf/syslog-udp-conf.properties  -name a1  -property flume.root.logger=INFO,console               # windows使用
```

#### 3.8 测试使用 http 数据源（用于前端用户行为数据收集）[数据发送的测试代码][3]
```bash
$ vi http-conf.properties                  # 创建配置文件，内容如下：  

   a1.sources = r1
   a1.channels = c1
   a1.sinks = s1

   a1.sources.r1.type = http
   a1.sources.r1.port = 8888
   a1.sources.r1.bind = 0.0.0.0
   #a1.sources.r1.handler = org.example.rest.RestHandler
   #a1.sources.r1.handler.nickname = random props
   #a1.sources.r1.HttpConfiguration.sendServerVersion = false
   #a1.sources.r1.ServerConnector.idleTimeout = 3000
   
   a1.channels.c1.type = memory
   
   a1.sinks.s1.type = logger
    
   a1.sources.r1.channels = c1
   a1.sinks.s1.channel = c1
   
$ flume-ng agent --conf conf --conf-file /home/apache-flume-1.9.0-bin/conf/http-conf.properties --name a1 -Dflume.root.logger=INFO,console       # linux使用
$ flume-ng  agent -conf ../conf  -conf-file ../conf/http-conf.properties  -name a1  -property flume.root.logger=INFO,console                     # windows使用
```

#### 3.9 测试使用 Custom （自定义）数据源，将[自定义源的源码][4]打包，再将jar包放到 Flume lib目录
```bash
$ vi custom-conf.properties                # 创建配置文件，内容如下：

   a1.sources = r1
   a1.channels = c1
   a1.sinks = s1
   # 自定义源的类的完整包路径
   a1.sources.r1.type = com.firecode.hadooptest.flume.helloword.MySource
   
   a1.channels.c1.type = memory
   
   a1.sinks.s1.type = logger
    
   a1.sources.r1.channels = c1
   a1.sinks.s1.channel = c1

$ flume-ng agent --conf conf --conf-file /home/apache-flume-1.9.0-bin/conf/custom-conf.properties --name a1 -Dflume.root.logger=INFO,console     # linux使用
$ flume-ng  agent -conf ../conf  -conf-file ../conf/custom-conf.properties  -name a1  -property flume.root.logger=INFO,console                   # windows使用
```
#### 4.0 测试使用 sequence generator 数据源（Flume启动自动生成 字符序列 （主要用于测试））
```bash
$ vi sequence-conf.properties              # 创建配置文件，内容如下：  

   a1.sources = r1
   a1.channels = c1
   a1.sinks = s1

   a1.sources.r1.type = seq
   
   a1.channels.c1.type = memory
   
   a1.sinks.s1.type = logger
    
   a1.sources.r1.channels = c1
   a1.sinks.s1.channel = c1
   
$ flume-ng agent --conf conf --conf-file /home/apache-flume-1.9.0-bin/conf/sequence-conf.properties --name a1 -Dflume.root.logger=INFO,console # linux使用
$ flume-ng  agent -conf ../conf  -conf-file ../conf/sequence-conf.properties  -name a1  -property flume.root.logger=INFO,console               # windows使用 
```

#### 4.1 测试使用 Stress 数据源（有点像序列生成器，Flume启动就生成大量事件，主要用于压力测试）
```bash
$ vi stress-conf.properties                # 创建配置文件，内容如下：  

   a1.sources = r1
   a1.channels = c1
   a1.sinks = s1
   
   a1.sources.r1.type = org.apache.flume.source.StressSource
   a1.sources.r1.size = 10240
   a1.sources.r1.maxTotalEvents = 1000000
   
   a1.channels.c1.type = memory
   
   a1.sinks.s1.type = logger
   
   a1.sources.r1.channels = c1
   a1.sinks.s1.channel = c1
   
$ flume-ng agent --conf conf --conf-file /home/apache-flume-1.9.0-bin/conf/stress-conf.properties --name a1 -Dflume.root.logger=INFO,console   # linux使用
$ flume-ng  agent -conf ../conf  -conf-file ../conf/stress-conf.properties  -name a1  -property flume.root.logger=INFO,console                 # windows使用
```

[1]: https://github.com/firechiang/hadoop-test/tree/master/flume/src/main/java/com/firecode/hadooptest/flume/helloword/ZookeeperFileUpload.java
[2]: https://github.com/firechiang/hadoop-test/tree/master/flume/src/main/java/com/firecode/hadooptest/flume/helloword/SyslogUDPTest.java
[3]: https://github.com/firechiang/hadoop-test/tree/master/flume/src/main/java/com/firecode/hadooptest/flume/helloword/HttpTest.java
[4]: https://github.com/firechiang/hadoop-test/tree/master/flume/src/main/java/com/firecode/hadooptest/flume/helloword/MySource.java