#### [一、安装][1]
#### [二、简单使用][2]
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

[1]: https://github.com/firechiang/hadoop-test/tree/master/flume/docs/simple_install.md
[2]: https://github.com/firechiang/hadoop-test/tree/master/flume/docs/simple_use.md
