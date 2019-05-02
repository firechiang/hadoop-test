### 一、通道的多路复用选择器（当一个源绑定了多个通道，它会根据头信息将数据发送到对应的通道，有负载均衡的意思）
#### 1.1，编辑[vi source-select-conf-2.properties]配置文件
```bash
# common
a1.sources = r1
a1.channels = c1 c2 c3 c4
a1.sinks = s1 s2 s3 s4

# source
# 通道的多路复用选择器（当一个源绑定了多个通道，它会根据头信息将数据发送到对应的通道，有负载均衡的意思）
a1.sources.r1.selector.type = multiplexing
a1.sources.r1.selector.header = state
# 头信息state=CZ映射到c通道
a1.sources.r1.selector.mapping.CZ = c1
# 头信息state=US映射到c2 c3通道
a1.sources.r1.selector.mapping.US = c2 c3
# 默认的发送通道
a1.sources.r1.selector.default = c4

a1.sources.r1.type = avro
a1.sources.r1.bind = 0.0.0.0
a1.sources.r1.port = 9191
  
# channel
a1.channels.c1.type = memory
a1.channels.c2.type = memory
a1.channels.c3.type = memory
a1.channels.c4.type = memory
  
# sink
a1.sinks.s1.type = logger
a1.sinks.s2.type = logger
a1.sinks.s3.type = logger
a1.sinks.s4.type = logger
  
# bin
# r1源绑定到 c1 c2 c3 c4 四个通道
a1.sources.r1.channels = c1 c2 c3 c4
a1.sinks.s1.channel = c1
a1.sinks.s2.channel = c2
a1.sinks.s3.channel = c3
a1.sinks.s4.channel = c4
```

#### 1.2，启动Flume
```bash
$ flume-ng agent --conf conf --conf-file /home/apache-flume-1.9.0-bin/conf/source-select-conf-2.properties --name a1 -Dflume.root.logger=INFO,console  # linux使用
$ flume-ng  agent -conf ../conf  -conf-file ../conf/source-select-conf-2.properties -name a1 -property flume.root.logger=INFO,console                  # windows使用
```

#### 1.3，测试我们上面监听在avro的Flume（另起一个xshell窗口）
```bash
$ echo state=US > header_US.txt                 # 在当前目录建立header_US.txt文件，里面的内容是state=US
$ echo state=CZ > header_CZ.txt                 # 在当前目录建立header_CZ.txt文件，里面的内容是state=CZ
$ echo aaa > f.txt                              # 在当前目录建立f.txt文件，里面的内容是aaa
# 使用avro客户端发送文件数据，到Flume avro源，--headerFile指定的是头信息数据，--filename是要发送的文件（当我们指定header_US.txt头信息的时候，服务端应打印2次，因为state=US指定了2个管道，它会往这两个管道同时发送数据）
$ flume-ng avro-client -H 127.0.0.1 -p 9191 --headerFile header_US.txt --filename f.txt
```