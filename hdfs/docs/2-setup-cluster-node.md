### Hadoop-3.x 高可用集群搭建
#### 一、预先准备环境
```bash
$ wget http://mirror.bit.edu.cn/apache/hadoop/common/hadoop-3.1.2/hadoop-3.1.2.tar.gz     # 下载安装包
```
#### 二、修改配置文件

##### 2.1 修改 [vi hadoop-env.sh]
```bash
export JAVA_HOME=/usr/lib/jvm/jdk1.8.0_171                                                # 修改 JAVA_HOME
export HDFS_DATANODE_USER=root                                                            # DataNode所使用的角色
export HDFS_NAMENODE_USER=root                                                            # NameNode所使用的角色
export HDFS_SECONDARYNAMENODE_USER=root                                                   # SecondaryNameNode所使用的角色
export HADOOP_SECURE_DN_USER=root                                                         # DataNode数据安全传输所使用的角色（建议不要输用root，这个角色单节点可以不配）
```
##### 2.2 修改 [vi core-site.xml]
```bash
<property>
    <name>fs.defaultFS</name>
    <value>hdfs://192.168.78.128:9820</value>
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
    <value>3</value>
</property>
<!-- SecondaryNameNode地址，这个是做文件合并工作的 -->
<property>
    <name>dfs.namenode.secondary.http-address</name>
    <value>192.168.78.128:50090</value>
</property>
```

#### 三、设置免密码登陆（在A机器生成一对公钥私钥，将公钥拷贝到想要登录的主机）
```bash
$ ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa                                                # 生成私钥和公钥
$ cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys                                         # 复制公钥到authorized_keys文件
$ chmod 0600 ~/.ssh/authorized_keys                                                       # 修改权限
$ scp ~/.ssh/authorized_keys root@192.168.83.135:~/.ssh/authorized_keys                   # 将公钥拷贝到想要登录的主机（如果想要登录的主机.ssh目录不存在，就执行生成《公私钥的命令》）
```

#### 四、修改从节点信息
##### 4.1 修改 [vi workers] 修改为当前机器名称或IP
```bash
192.168.78.129
192.168.78.130
192.168.78.131
```

#### 五、分发文件
```bash
scp -r hadoop-3.1.2 root@192.168.78.129:/home                                             # -r 是目录下所有文件和文件夹
scp -r hadoop-3.1.2 root@192.168.78.130:/home                                             # -r 是目录下所有文件和文件夹
scp -r hadoop-3.1.2 root@192.168.78.131:/home                                             # -r 是目录下所有文件和文件夹
```

#### 五、格式化文件系统
```bash
$ bin/hdfs namenode -format
```


#### 六、启动NameNode，DataNode，SecondaryNameNode
```bash
$ sbin/start-dfs.sh
$ jps                                                           # 查看三个节点是否都启动了，如果都启动了可以到浏览器访问：http://192.168.78.128:9870
```

#### 七、简单使用
```bash
$ ./bin/hdfs dfs --help                                         # 查看 hdfs dfs 命令基础使用
$ ./bin/hdfs dfs -mkdir /tools                                  # 在根目录下创建 tools 目录
$ ./bin/hdfs dfs -put /home/tools/hadoop-3.1.2.tar.gz /tools    #上传文件至HDFS /tools目录
$ ./bin/hdfs dfs -ls /tools                                     # 查看文件是否存在
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
