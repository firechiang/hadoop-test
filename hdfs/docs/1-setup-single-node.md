### Hadoop-3.x 单节点搭建
#### 一、预先准备环境
```bash
$ wget http://mirror.bit.edu.cn/apache/hadoop/common/hadoop-3.1.2/hadoop-3.1.2.tar.gz     # 下载安装包
```
#### 二、修改配置文件

##### 2.1 修改 [vi hadoop-env.sh]
```bash
JAVA_HOME=/usr/lib/jvm/jdk1.8.0_171                                                       # 修改 JAVA_HOME
```
##### 2.2 修改 [vi core-site.xml]
```bash
<property>
    <name>fs.defaultFS</name>
    <value>hdfs://localhost:9000</value>
</property>
<!-- 指定hadoop运行时产生临时文件的存储目录 -->
<property>
    <name>hadoop.tmp.dir</name>
    <value>/home/hadoop-3.1.2/tem</value>                                                 # 注意创建该目录
</property>
```
##### 2.3 修改 [vi hdfs-site.xml]
```bash
<!-- 指定HDFS副本的数量 -->
<property>
    <name>dfs.replication</name>
    <value>1</value>
</property>
```

#### 三、设置免密码登陆
```bash
$ ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa
$ cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
$ chmod 0600 ~/.ssh/authorized_keys
```

#### 四、添加 HDFS 执行用户
##### 4.1 修改 [vi sbin/start-dfs.sh] 在顶部空白处添加如下内容
```bash
HDFS_DATANODE_USER=root
HADOOP_SECURE_DN_USER=hdfs
HDFS_NAMENODE_USER=root
HDFS_SECONDARYNAMENODE_USER=root
```
##### 4.2 修改 [vi sbin/stop-dfs.sh] 在顶部空白处添加如下内容
```bash
HDFS_DATANODE_USER=root               # DataNode所使用的角色
HADOOP_SECURE_DN_USER=hdfs            # HADOOP_SECURE_DN_USER所使用的角色（这个角色单节点可以不配）
HDFS_NAMENODE_USER=root               # NameNode所使用的角色
HDFS_SECONDARYNAMENODE_USER=root      # SecondaryNameNode所使用的角色
```

#### 五、格式化文件系统
```bash
$ bin/hdfs namenode -format
```


#### 六、启动NameNode 和 DataNode
```bash
$ sbin/start-dfs.sh
```


#### 其它、添加 Yarn 执行用户
##### 修改 [vi sbin/start-yarn.sh] 在顶部空白处添加如下内容
```bash
YARN_RESOURCEMANAGER_USER=root    # YARN_RESOURCEMANAGER_USER所使用的角色
YARN_NODEMANAGER_USER=root        # YARN_NODEMANAGER_USER所使用的角色
HADOOP_SECURE_DN_USER=yarn        # HADOOP_SECURE_DN_USER所使用的角色
```
##### 修改 [vi sbin/stop-yarn.sh] 在顶部空白处添加如下内容
```bash
YARN_RESOURCEMANAGER_USER=root
YARN_NODEMANAGER_USER=root
HADOOP_SECURE_DN_USER=yarn
```
