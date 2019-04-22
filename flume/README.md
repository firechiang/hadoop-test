#### [一、安装][1]
#### [二、使用][2]
#### 三、架构
![image](https://github.com/firechiang/hadoop-test/blob/master/flume/image/1_flume_framework.png)
```bash
1，在数据生成器运行的节点上（一般指的是我们的应用），启动单独的Flume Agent 来收集数据，再推送到存储端（HDFS，ElasticSearch等等）
2，Flume Event事件是Flume的传输单元，主要是byte[]，可以含有一些header信息，在source和desctination之间
3，Flume Agent包含三个组件，是一个独立的java进程，从客户端（其它的Agent）接收数据，然后再转发到下一个desctination（Sink或Agent）
   3.1，Source（源头）从事件生成器接收数据，以Event事件的形式传给一个或多个Channel
   3.2，Channel（通道）从Source中接收Event作为临时存放地，缓存到Buffer中，直到Sink将其消费掉。Channel可以和多个Source或Sink协同
   3.3，Sink（沉槽）存放数据到HDFS或ElasticSearch等等。从Channel中消费Event，并分发给desctination（Sink或Agent）。
        Sink的desctination（Sink或Agent）也可以是另一个Agent或HDFS
4，Flume附加组件有Interceptors（拦截器），Channel Selectors（通道选择器），Sink Processors（沉槽处理器）        
        
注意：一个Flume的Agent可以有多个Source，Channel，Sink
```

#### 四、优点
```bash
1，可以和任意集中式进程集成
2，输入的数据速度大于写入存储目的地的速度，Flume会进行缓冲
3，Flume提供上下文路由（数据流路线）
4，Flume中的事务基于Channe，使用了两种事务模型（sender + receiver），确保消息可靠的被发送
```

#### 五、特点
```bash
1，Flume可以高效的收集Web Server的Log到HDFS
2，可以高效获取输入，用于缓冲再转发
3，可导入大量的数据
4，Flume支持大量的Source和destination（Sink或Agent）类型
5，Flume支持多级跳跃，Source和destination（Sink或Agent）的fan in 和 fan out
6，Flume可以水平伸缩
```

##### 配置 flume-conf.properties
```bash
a1.sources = r1
a1.sinks = s1
a1.channels = c1

#sources-r1
a1.sources.r1.type = netcat
a1.sources.r1.bind = localhost
a1.sources.r1.port = 44444

#sinks-s1
a1.sinks.s1.type = logger

#a1.channels-c1
a1.channels.c1.type = memory
a1.channels.c1.capacity = 1000
a1.channels.c1.transactionCapacity = 100

#绑定channel到source和sink。source可以配置多个channel；sink只能配置一个channel
a1.sources.r1.channels = c1
a1.sinks.s1.channel = c1
```

##### 运行agent（命令说明：--conf 配置目录; --conf-file 配置文件; --name 代理名称; -D 指定额外的参数）
```bash
flume-ng  agent -conf ../conf  -conf-file ../conf/flume-conf.properties  -name a1  -property flume.root.logger=INFO,console   # windows使用
flume-ng agent --conf conf --conf-file flume-conf.properties --name a1 -Dflume.root.logger=INFO,console                       # linux使用

连接测试：windows：telnet localhost 44444    --windows telnet如果没有开启到 卸载程序下 找到 打开或关闭windows下开启
```

##### 核心说明
```bash
Source  --数据来源
     生成Event，调用channelProcessor将Event put到channel中去
  Source --> Event --> ChannelProcessor -> InterceptorChain(e)  --> Channels(e)
  start()
  stop()
	  
Channel  --数据通道
    连接Source(Event Procuder) 和 Sink(Event Consumer)本质上Channel就是Buffer 支持事务处理，保证原子性(put + take)
  Channel是线程安全的
  put()
  take()
  getTransation()//事物
  
Sink  --数据出口
    连接到Channel，消费里面的Event将其发送到desctination,有很多相应的sink类型
  Sink可以根据SinkGroup和SinkProcessor进行分组，通过Processor由SinkRunner轮询出来
  Sink的Process()方法只能由一个线程访问
  setChannel()
  getChannel()
  process()
  Flume的入口点org.apache.flume.node.Application
```

##### 使用avro源，配置文件（自建）avro-conf.properties
```bash
flume-ng  agent -conf ../conf  -conf-file ../conf/avro-conf.properties  -name a1  -property flume.root.logger=INFO,console    # windows启动
flume-ng agent --conf conf --conf-file avro-conf.properties --name a1 -Dflume.root.logger=INFO,console                        # linux启动

通过flume-ng avro-client命令向flume发送avro数据；命令：flume-ng avro-client -H localhost -p 4141                                # 连接flume然后可以发送数据
发送文件命令：flume-ng avro-client -H localhost -p 4141 -F test.txt                                                             # 发送当前目录下 test.txt 文件<这个命令在windows下好像不起作用>
```

##### 将flume配置文件放入zookeeper中
```bash
flume-ng agent -z 127.0.0.1:2181 -p /flume --name a1 -Dflume.root.logger=INFO,console      # 从zookeeper中读取配置文件启动命令 -p /flume<-p 是读取父目录的意思>就是读取zookeeper /flume/a1目录
```

##### 使用exec源 （flume启动时执行命令）配置文件[exec-conf.properties]	
```bash
flume-ng  agent -conf ../conf  -conf-file ../conf/exec-conf.properties  -name a1  -property flume.root.logger=INFO,console                       # windows启动
```

##### 使用spooldir源 （监听某个文件夹下的新增文件，可用于收集日志）配置文件[spooling-conf.properties]
```bash
flume-ng  agent -conf ../conf  -conf-file ../conf/spooling-conf.properties  -name a1  -property flume.root.logger=INFO,console                   # windows启动
```

##### 使用sequence generator源 （自动生成 字符序列 （主要用于测试>） 配置文件[sequence-conf.properties]
```bash
flume-ng  agent -conf ../conf  -conf-file ../conf/sequence-conf.properties  -name a1  -property flume.root.logger=INFO,console                   # windows启动
```

##### 使用Syslog TCP源 （可使用 tcp 传输数据） 配置文件[syslog-tcp-conf.properties]
```bash
flume-ng  agent -conf ../conf  -conf-file ../conf/syslog-tcp-conf.properties  -name a1  -property flume.root.logger=INFO,console                 # windows启动

telnet localhost 44444                       #连接测试  windows telnet如果没有开启到 卸载程序下 找到 打开或关闭windows下开启
```

##### 使用Stress 源（有点像序列生成器，已启动就生成大量事件，主要用于压力测试）配置文件[stress-conf.properties]
```bash
flume-ng  agent -conf ../conf  -conf-file ../conf/stress-conf.properties  -name a1  -property flume.root.logger=INFO,console                     # windows启动
```

##### 使用Syslog UDP源（可使用 udp 传输数据） 配置文件[syslog-udp-conf.properties]
```bash
# 测试代码
public static void main(String[] args) throws IOException {                                       
    DatagramSocket socket = new DatagramSocket();
    byte[] bytes = "jaingjiang".getBytes();
    DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
    packet.setAddress(InetAddress.getByName("127.0.0.1"));
    packet.setPort(5140);
    socket.send(packet);
    socket.close();
    System.err.println("发送完成.");
}

# 打包放入 flume lib目录
flume-ng  agent -conf ../conf  -conf-file ../conf/syslog-udp-conf.properties  -name a1  -property flume.root.logger=INFO,console                 # windows启动
```

##### http 源 （用于前端用户行为数据收集） 配置文件[http-conf.properties]
```bash
# 测试代码
public static void main(String[] args) throws IOException {
    String url = "http://localhost:5140/";
	String param = "[{\"headers\" : {\"timestamp\" : \"434324343\",\"host\" : \"random_host.example.com\" },\"body\" :\"random_body\"},
	                 {\"headers\" : {\"namenode\" : \"namenode.example.com\", \"datanode\" :\"random_datanode.example.com\"},\"body\" : \"really_random_body\"}]";
    URL realUrl = new URL(url);
    URLConnection conn = realUrl.openConnection();
     // POST
    conn.setDoOutput(true);
    conn.setDoInput(true);
    OutputStream out = conn.getOutputStream();
    out.write(param.getBytes());
    out.flush();
    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    while (in.readLine() != null) {
    }
    out.close();
    in.close();
}
# 打包放入 flume lib目录
flume-ng  agent -conf ../conf  -conf-file ../conf/http-conf.properties  -name a1  -property flume.root.logger=INFO,console                       # windows启动
```

##### Custom 源（自定义源）	type 就是包名加类名 完整的路径，配置文件[custom-conf.properties]
```bash
# 测试代码
public class MySource extends AbstractEventDrivenSource{
    @Override
	public synchronized void doStart() {
	    ChannelProcessor cp = this.getChannelProcessor();
		Map<String ,String> map = new HashMap<>();
		Event e = null;
		map.put("owner", "xupc");
		map.put("date", "1016-02-12");
		for (int i = 0; i < 100; i++) {
		    e = new SimpleEvent();
			e.setBody(("1"+i).getBytes());
			e.setHeaders(map);
			cp.processEvent(e);
		}
	    System.err.println("自定义数据源。");
    }
    
    @Override
    protected void doConfigure(Context context) throws FlumeException {
    }
    
    @Override
    protected void doStop() throws FlumeException {
    }
}
# 打包放入 flume lib目录
flume-ng  agent -conf ../conf  -conf-file ../conf/custom-conf.properties  -name a1  -property flume.root.logger=INFO,console                     # windows启动
```
[1]: https://github.com/firechiang/hadoop-test/tree/master/flume/docs/simple_install.md
[2]: https://github.com/firechiang/hadoop-test/tree/master/flume/docs/simple_use.md
