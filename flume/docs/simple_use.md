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
# 将上面的示例配置配到 flume-conf.properties 文件，再执行以下命令启动Flume
$ flume-ng agent --conf conf --conf-file /home/apache-flume-1.9.0-bin/conf/flume-conf.properties --name a1 -DFlume.root.logger=INFO,console   # linux 使用
$ flume-ng  agent -conf ../conf  -conf-file ../conf/flume-conf.properties  -name a1  -property flume.root.logger=INFO,console                 # windows使用
# 测试我们上面监听在netcat的Flume（如果没有nc命令，请安装：yum -y install nmap-ncat.x86_64）
$ nc localhost 44444                       # 另起一个xshell窗口，连接Flume，然后顺便输入数据，看看有没有发送过去（接收端我们配的是console，数据会打印到控制台）
$ telnet localhost 44444                   # windows下连接测试，windows telnet如果没有开启到 卸载程序下 找到 打开或关闭windows下开启
```
