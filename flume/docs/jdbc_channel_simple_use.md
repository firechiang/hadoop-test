### 一、自定义 JDBC Channel简单使用(将数据缓冲数据存储到Mysql数据库)
#### 1.1，编辑[vi jdbc-channel-conf.properties]配置文件
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
a1.channels.c1.type = com.firecode.hadooptest.flume.channel.MyChannel

# sink
a1.sinks.s1.type = logger
  
# bin
a1.sources.r1.channels = c1
a1.sinks.s1.channel = c1
```
#### 1.3，创建Mysql数据库flume_remote和表test（到MySql客户端下执行）
```bash
$ create database flume_remote;
$ create table test(msg varchar(64));
```
#### 1.4，下载mysql-connector-java-8.0.15.jar到Flume lib目录
```bash
wget http://central.maven.org/maven2/mysql/mysql-connector-java/8.0.15/mysql-connector-java-8.0.15.jar
```
#### 1.5，将当前项目模块打成jar包，放到Flume lib目录下

#### 1.6，启动Flume
```bash
$ flume-ng agent --conf conf --conf-file /home/apache-flume-1.9.0-bin/conf/jdbc-channel-conf.properties --name a1 -Dflume.root.logger=INFO,console  # linux使用
$ flume-ng  agent -conf ../conf  -conf-file ../conf/jdbc-channel-conf.properties -name a1 -property flume.root.logger=INFO,console                  # windows使用
```

#### 1.7，测试我们上面监听在netcat的Flume（如果没有nc命令，请安装：yum -y install nmap-ncat.x86_64）
```bash
$ nc 127.0.0.1 9191              # 另起一个xshell窗口，连接Flume，然后随便输入数据
```