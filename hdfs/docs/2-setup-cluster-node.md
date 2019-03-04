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
export HADOOP_SECURE_DN_USER=root                                                         # DataNode数据安全传输所使用的角色（建议不要输用root，这个角色非安全（https）协议可以不配）
```
##### 2.2 修改 [vi core-site.xml]
```bash
<property>
    <name>fs.defaultFS</name>
    <value>hdfs://server-001:9820</value>
</property>
<!-- 指定hadoop运行时产生临时文件的存储目录（注意创建该目录） -->
<property>
    <name>hadoop.tmp.dir</name>
    <value>/home/hadoop-3.1.2/tem</value>                                                
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
    <value>server-001:50090</value>
</property>
```

#### 三、设置免密码登陆（在A机器生成一对公钥私钥，将公钥拷贝到想要登录的主机）
```bash
$ ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa                                                # 生成私钥和公钥
$ cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys                                         # 复制公钥到authorized_keys文件
$ chmod 0600 ~/.ssh/authorized_keys                                                       # 修改权限
$ scp ~/.ssh/authorized_keys root@192.168.83.135:~/.ssh/authorized_keys                   # 将公钥拷贝到想要登录的主机（如果想要登录的主机.ssh目录不存在，就执行生成《公私钥的命令》）
$ ssh 192.168.83.135                                                                      # 测试登陆
```

#### 四、修改hosts文件信息
##### 4.1 修改 [vi /etc/hosts] 在空白处添加如下内容
```bash
192.168.78.132 server-001
192.168.78.129 server-002
192.168.78.130 server-003
192.168.78.131 server-004

$ scp /etc/hosts root@192.168.78.129:/etc/                                                # 分发到各个机器
```

#### 五、修改从节点信息
##### 4.1 修改 [vi workers] （从节点信息建议使用主机名，使用IP效率低而且还容易导致 DataNode Block初始化失败）
```bash
server-002
server-003
server-004
```

#### 六、分发文件
```bash
scp -r hadoop-3.1.2 root@192.168.78.129:/home                                             # -r 是目录下所有文件和文件夹
scp -r hadoop-3.1.2 root@192.168.78.130:/home                                             # -r 是目录下所有文件和文件夹
scp -r hadoop-3.1.2 root@192.168.78.131:/home                                             # -r 是目录下所有文件和文件夹
```

#### 七、配置Hadoop环境变量[vi /etc/profile]在末尾添加如下内容
```bash
export HADOOP_HOME=/home/hadoop-3.1.2
PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin                                             # linux以 : 号隔开，windows以 ; 号隔开

$ scp /etc/profile root@192.168.78.131:/etc                                               # 配置完成后将文件分发到各个机器上
$ source /etc/profile                                                                     # （系统重读配置）在各个机器上执行使配置文件生效（实验：敲个hdf然后按Tab键，如果补全了说明配置成功了）
```

#### 八、格式化文件系统
```bash
$ bin/hdfs namenode -format                                                               # 我们配置了环境变量可以在任意目录执行 hdfs namenode -format 即可
# 格式化成功在倒数第几行会打印：common.Storage: Storage directory /home/hadoop-3.1.2/tem/dfs/name has been successfully formatted.
# 也可以去看 tem 目录所生成的文件
```


#### 九、启动集群
```bash
$ sbin/start-dfs.sh                                             # 配置了环境变量可以在任意目录执行 start-dfs.sh
$ jps                                                           # 查看三个节点是否都启动了，如果都启动了可以到浏览器访问：http://192.168.78.128:9870
```

#### 十、简单使用
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
