### 一、测试使用 File_Roll Sink（将数据存储到本地）
#### 1.1，编辑[vi file-roll-sink-conf.properties]配置文件
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
a1.sinks.s1.type = file_roll
# 文件存储目录（注意要手动创建目录）
a1.sinks.s1.sink.directory = /home/log/flume
  
# bin
a1.sources.r1.channels = c1
a1.sinks.s1.channel = c1
```

#### 1.2，启动Flume
```bash
$ flume-ng agent --conf conf --conf-file /home/apache-flume-1.9.0-bin/conf/file-roll-sink-conf.properties --name a1 -Dflume.root.logger=INFO,console  # linux使用
$ flume-ng  agent -conf ../conf  -conf-file ../conf/file-roll-sink-conf.properties -name a1 -property flume.root.logger=INFO,console                  # windows使用
```

#### 1.3，测试我们上面监听在netcat的Flume（如果没有nc命令，请安装：yum -y install nmap-ncat.x86_64）
```bash
$ nc 127.0.0.1 9191              # 另起一个xshell窗口，连接Flume，然后随便输入数据
```