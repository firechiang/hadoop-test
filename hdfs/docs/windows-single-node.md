### Windows开发搭建
#### 一、预先准备环境，下载安装包：http://mirror.bit.edu.cn/apache/hadoop/common/hadoop-3.1.2/hadoop-3.1.2.tar.gz
```bash
$ start winrar x -y hadoop-3.1.2.tar.gz ./                    # 使用Winrar将文件解压到当前目录（用管理员身份打开命令行）
```

#### 二、添加 hadoop.dll和winutils.exe到bin目录，下载地址：https://github.com/firechiang/apache-hadoop-3.1.0-winutils
```bash
2.1 检查系统C:\Windows\System32和C:\Windows\SysWOW64目录是否有MSVCR120.dll文件
2.2 如果没有，请下载相应文件到相应目录，下载地址：https://github.com/firechiang/hadoop-test/blob/master/hdfs/msvcr120dll
2.3 到Hadoop bin目录双击执行一下winutils.exe文件，如果没有弹出任何信息说明环境没有问题，如果有弹出信息，请看具体是缺少什么文件
```

#### 三、修改配置文件

##### 3.1 修改 \etc\hadoop\hadoop-env.cmd 文件
```bash
set JAVA_HOME=C:\PROGRA~1\Java\jdk1.8.0_171            # 修改 JAVA_HOME（(如果路径中有"Program Files"，则将Program Files改为 PROGRA~1）
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
    <value>/E:/hadoop-3.1.2/tem</value>                                            
</property>

<!-- 表示可以通过代理“root”用户操作HDFS，不加代理权限，有些客户端不能操控HDFS文件（比如：Hive hiveserver2服务或浏览器）（下面的root指的是用户，可以改） -->
<property>
    <name>hadoop.proxyuser.root.hosts</name>
    <value>*</value>
</property>

<!-- 表示可以通过代理“root”用户组操作HDFS，不加代理权限，有些客户端不能操控HDFS文件（比如：Hive hiveserver2服务或浏览器）（下面的root指的是用户，可以改） -->
<property>
    <name>hadoop.proxyuser.root.groups</name>
    <value>*</value>
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

#### 七、简单使用

```bash
$ hdfs dfs --help                                             # 查看 hdfs dfs 命令基础使用
$ hdfs dfs -mkdir /tools                                      # 在根目录下创建 tools 目录
$ hdfs dfs -put ./jdk-8u171-linux-x64.tar.gz /tools           #上传文件至HDFS /tools目录
$ hdfs dfs -ls /tools                                         # 查看文件是否存在
```
