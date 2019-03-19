### Windows开发搭建
#### 一、预先准备环境，下载安装包：http://mirrors.shu.edu.cn/apache/hadoop/common/hadoop-3.2.0/hadoop-3.2.0.tar.gz
```bash
$ start winrar x -y hadoop-3.2.0.tar.gz ./                                           # 使用Winrar将文件解压到当前目录（用管理员身份打开命令行）
```

#### 二、添加 hadoop.dll和winutils.exe到bin目录（注意对应版本），下载地址：https://github.com/steveloughran/winutils

#### 三、修改配置文件

##### 3.1 修改 \etc\hadoop\hadoop-env.cmd 文件
```bash
set JAVA_HOME="C:\Program Files"\Java\jdk1.8.0_171                                   # 修改 JAVA_HOME（因为Program Files文件夹中存在空格所以要添加双引号）
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

#### 四、配置Hadoop环境变量 HADOOP_HOME 并将 %HADOOP_HOME%\bin和%HADOOP_HOME%\sbin加入到Path


#### 五、格式化NameNode
```bash
$ hdfs namenode -format
```

#### 五、启动NameNode，DataNode
```bash
$ start-dfs
```
