### Windows开发搭建
#### 一、预先准备环境
```bash
http://mirrors.shu.edu.cn/apache/hadoop/common/hadoop-3.2.0/hadoop-3.2.0.tar.gz      # 下载安装包
$ start winrar x -y hadoop-3.2.0.tar.gz ./                                           # 使用Winrar将文件解压到当前目录（用管理员身份打开命令行）
```

#### 二、添加 hadoop.dll 和 winutils.exe到bin目录（注意使用相应版本），下载地址：https://github.com/steveloughran/winutils

#### 三、修改配置文件

##### 3.1 修改 \etc\hadoop\hadoop-env.cmd 文件，配置JAVA_HOME，如果我们的电脑配置了 JAVA_HOME 环境变量就不需要改了
```bash
set JAVA_HOME=%JAVA_HOME%                                                            # %JAVA_HOME%（直接取环境变量JAVA_HOME的值）
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
    <value>/E:/hadoop-3.2.0/tmp</value>                                            
</property>
```

##### 3.3 修改 \etc\hadoop\hdfs-site.xml 文件
```bash
<!-- 指定HDFS副本的数量 -->
<property>
    <name>dfs.replication</name>
    <value>1</value>
</property>
<!-- HDFS数据存储目录（注意创建该目录） -->
<property>
    <name>dfs.data.dir</name>
    <value>/E:/hadoop-3.2.0/data</value>
</property>
```

#### 四、配置Hadoop环境变量 HADOOP_HOME 并将 %HADOOP_HOME%\bin和%HADOOP_HOME%\sbin加入到Path

