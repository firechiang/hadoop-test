### Windows开发搭建
#### 一、预先准备环境，下载安装包：http://mirrors.shu.edu.cn/apache/hadoop/common/hadoop-3.2.0/hadoop-3.2.0.tar.gz
```bash
$ start winrar x -y hadoop-3.2.0.tar.gz ./                    # 使用Winrar将文件解压到当前目录（用管理员身份打开命令行）
```

#### 二、添加 hadoop.dll和winutils.exe到bin目录，下载地址：https://github.com/firechiang/apache-hadoop-3.1.0-winutils

#### 三、修改配置文件

##### 3.1 修改 \etc\hadoop\hadoop-env.cmd 文件
```bash
set JAVA_HOME="C:\Program Files"\Java\jdk1.8.0_171            # 修改 JAVA_HOME（因为Program Files文件夹中存在空格所以要添加双引号）
```

##### 3.2 修改 \etc\hadoop\core-site.xml 文件
```bash
<property>
    <name>fs.defaultFS</name>
    <value>hdfs://localhost:9820</value>
</property>
<!-- 指定hadoop运行时产生临时文件的存储目录（注意创建该目录） -->
<property>
    <name>hadoop.tmp.dir</name>
    <value>/E:/hadoop-3.2.0/tem</value>                                            
</property>
```

##### 3.3 修改 \etc\hadoop\hdfs-site.xml 文件
```bash
<!-- 指定HDFS副本的数量 -->
<property>
    <name>dfs.replication</name>
    <value>1</value>
</property>
```

#### 四、配置Hadoop环境变量 HADOOP_HOME 并将 %HADOOP_HOME%\bin和%HADOOP_HOME%\sbin加入到Path（最后记得重启机器才能生效）


#### 五、格式化NameNode
```bash
$ hdfs namenode -format
```

#### 六、启动NameNode，DataNode（使用管理员模式打开命令行）
```bash
$ start-dfs                                                   # 启动
$ jps                                                         # 看看NameNode，DataNode进程是否启动
$ stop-dfs                                                    # 停止
```

#### 七、简单使用，所有命令可能会报：（'C:\Program' 不是内部或外部命令，也不是可运行的程序或批处理文件。），这个错误可以忽略

```bash
$ hdfs dfs --help                                             # 查看 hdfs dfs 命令基础使用
$ hdfs dfs -mkdir /tools                                      # 在根目录下创建 tools 目录
$ hdfs dfs -put ./jdk-8u171-linux-x64.tar.gz /tools           #上传文件至HDFS /tools目录
$ hdfs dfs -ls /tools                                         # 查看文件是否存在
```
