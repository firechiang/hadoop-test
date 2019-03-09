### Hadoop-3.x 高可用集群搭建
#### 理论说明
```bash
主备两个（或多个）NameNode
解决单点故障：
    主NameNode对外提供服务，备NameNode同步主NameNode元数据，以待切换。
    所有DataNode同时向两个NameNode汇报数据块信息。  
    
    两种切换：
        手动切换：通过命令实现主备之间切换，可以用于HDFS升级等场合。
        自动切换：基于Zookeeper实现。
        
    基于Zookeeper自动方案
   Zookeeper Failover Controller：监控NameNode健康状态，并向Zookeeper注册NameNode。
   NameNode挂掉后，ZKFC为NameNode竞争锁，获得ZKFC锁的NameNode变为active。
  
HDFS Federation（多个NameNode并行提供服务）
    多个NameNode并行提供服务，多个NameNode不能相互访问（相互隔离，像mysql的库一样，一个mysql有多个库）。


机器任务分配如下：

              NameNode     DataNode    ZK    ZKFC    JNN
server-001      是                                                                                      是                 是
server-002      是                                    是                        是                是
server-003                    是                       是                                      是
server-004                    是                       是                                      是
```
#### 一、预先准备环境
```bash
$ wget http://mirror.bit.edu.cn/apache/hadoop/common/hadoop-3.1.2/hadoop-3.1.2.tar.gz     # 下载安装包
```

#### 二、修改hosts文件信息
##### 2.1 修改 [vi /etc/hosts] 在空白处添加如下内容
```bash
192.168.78.132 server-001
192.168.78.129 server-002
192.168.78.130 server-003
192.168.78.131 server-004

$ scp /etc/hosts root@192.168.78.129:/etc/                                                # 分发到各个机器
```

#### 三、修改配置文件

##### 3.1 修改 [vi hadoop-env.sh]
```bash
export JAVA_HOME=/usr/lib/jvm/jdk1.8.0_171                                                # 修改 JAVA_HOME
export HDFS_DATANODE_USER=root                                                            # DataNode所使用的角色
export HDFS_NAMENODE_USER=root                                                            # NameNode所使用的角色
export HDFS_ZKFC_USER=root                                                                # ZKFC所使用的角色
export HDFS_JOURNALNODE_USER=root                                                         # JournalNode所使用的角色
export HDFS_SECONDARYNAMENODE_USER=root                                                   # SecondaryNameNode所使用的角色（高可用可以架构可以不配）
export HADOOP_SECURE_DN_USER=root                                                         # DataNode数据安全传输所使用的角色（建议不要输用root，这个角色非安全（https）协议可以不配）
```
##### 3.2 修改 [vi core-site.xml]
```bash
<!-- HDFS浏览器访问地址，这里使用是NameNode集群逻辑名称（看下面的配置我们自定义了 mycluster） -->
<property>
    <name>fs.defaultFS</name>
    <value>hdfs://mycluster</value>
</property>

<!-- 指定hadoop运行时产生临时文件的存储目录（注意创建该目录） -->
<property>
    <name>hadoop.tmp.dir</name>
    <value>/home/hadoop-3.1.2/data</value>                                                
</property>

<!-- HDFSWebUI浏览器所使用的用户名  -->
<property>
    <name>hadoop.http.staticuser.user</name>
    <value>root</value>                                                
</property>

<!-- Zookeeper集群配置  -->
<property>
    <name>ha.zookeeper.quorum</name>
    <value>server-002:2181,server-003:2181,server-004:2181</value>
</property>
```
##### 3.3 修改 [vi hdfs-site.xml]
```bash
<!-- 指定HDFS副本的数量 -->
<property>
    <name>dfs.replication</name>
    <value>3</value>
</property>

<!-- NameNode集群逻辑名称（这个名称可以随便起）  -->
<property>
    <name>dfs.nameservices</name>
    <value>mycluster</value>
</property>

<!-- NameNode集群逻辑名称（mycluster）所包含的NameNode节点信息（这里是包含了两个NameNode（myNameNode1和myNameNode2））  -->
<property>
    <name>dfs.ha.namenodes.mycluster</name>
    <value>myNameNode1,myNameNode2</value>
</property>

<!-- NameNode集群逻辑名称（mycluster）所包含的myNameNode1的详细信息（rpc协议） -->
<property>
    <name>dfs.namenode.rpc-address.mycluster.myNameNode1</name>
    <value>server-001:8020</value>
</property>

<!-- NameNode集群逻辑名称（mycluster）所包含的myNameNode2的详细信息（rpc协议） -->
<property>
    <name>dfs.namenode.rpc-address.mycluster.myNameNode2</name>
    <value>server-002:8020</value>
</property>

<!-- NameNode集群逻辑名称（mycluster）所包含的 myNameNode1的详细信息（http协议） -->
<property>
    <name>dfs.namenode.http-address.mycluster.myNameNode1</name>
    <value>server-001:9870</value>
</property>

<!-- NameNode集群逻辑名称（mycluster）所包含的 myNameNode2的详细信息（http协议） -->
<property>
    <name>dfs.namenode.http-address.mycluster.myNameNode2</name>
    <value>server-002:9870</value>
</property>

<!-- JournalNode集群配置（最后的名称最好和NameNode集群逻辑名称对应）  -->
<property>
    <name>dfs.namenode.shared.edits.dir</name>
    <value>qjournal://server-001:8485;server-003:8485;server-004:8485/mycluster</value>
</property>

<!-- JournalNode存放文件的目录 （注意创建该目录）-->
<property>
    <name>dfs.journalnode.edits.dir</name>
    <value>/home/hadoop-3.1.2/journalNode/data</value>
</property>

<!--故障转移的代理类，HDFS找Active NameNode的代理类，如果没配则找不到Active NameNode会报错（最后的名称最好和NameNode集群逻辑名称对应）  -->
<property>
    <name>dfs.client.failover.proxy.provider.mycluster</name>
    <value>org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider</value>
</property>

<!-- 隔离配置函数-->
<property>
    <name>dfs.ha.fencing.methods</name>
    <value>sshfence</value>
</property>
<!-- （和上面的隔离配置函数一起使用）-->
<!-- 一台NameNode通过密钥强制登录另一台NameNode将其状态改为Standby，防止同一NameNode集群两各NameNode同时提供服务（下面配的就是我们生成的密钥地址）-->
<property>
    <name>dfs.ha.fencing.ssh.private-key-files</name>
    <value>/root/.ssh/id_rsa</value>
</property>

<!-- 自动故障转移（NameNode挂了自动切换）-->
<property>
    <name>dfs.ha.automatic-failover.enabled</name>
    <value>true</value>
</property>

<!-- SecondaryNameNode地址，这个是做文件合并工作的（高可用不需要这个节点） -->
<!-- <property>
    <name>dfs.namenode.secondary.http-address</name>
    <value>server-001:50090</value>
</property> -->

```

#### 四、设置免密码登陆（在A机器生成一对公钥私钥，将公钥拷贝到想要登录的主机）
```bash
$ ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa                                                # 生成私钥和公钥
$ cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys                                         # 复制公钥到authorized_keys文件
$ chmod 0600 ~/.ssh/authorized_keys                                                       # 修改权限
$ scp ~/.ssh/id_rsa.pub root@192.168.83.135:~/.ssh/id_rsa.pub_131                         # 将公钥拷贝到想要登录的主机（如果想要登录的主机.ssh目录不存在，就执行生成《公私钥的命令》）
$ cat ~/.ssh/id_rsa.pub_131 >> ~/.ssh/authorized_keys                                     #（到想要登录的主机上执行）将上一步考过来的公钥 复制到 authorized_keys
$ rm -rf id_rsa.pub_131                                                                   #（到想要登录的主机上执行）删除上一步考过来的公钥

$ ssh 192.168.83.135                                                                      # 测试登陆
```

#### 五、修改从节点信息
##### 5.1 修改 [vi workers] （从节点信息建议使用主机名，使用IP效率低而且还容易导致 DataNode Block初始化失败）
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

$ scp /etc/profile root@192.168.78.131:/etc                                               # 配置完成后将文件分发到各个主节点（NameNode）机器上
$ source /etc/profile                                                                     # （系统重读配置）在各个机器上执行使配置文件生效（实验：敲个hdf然后按Tab键，如果补全了说明配置成功了）
```

#### 八、启动journalNode集群
```bash
$ bin/hdfs --daemon start journalnode                                                     # （启动）到所有journalNode所在节点执行，要停止的话将 start 改成 stop 即可
$ jps                                                                                     # 如果有JournalNode进程说明启动成功
```

#### 九、格式化文件系统
```bash
$ bin/hdfs namenode -format                                                               # （多台NameNode任选一台）每次格式化都会产生新集群ID
# 格式化成功在倒数第几行会打印：common.Storage: Storage directory /home/hadoop-3.1.2/data/dfs/name has been successfully formatted.
# 也可以去看 data 目录所生成的文件
```

#### 十、同步NameNode元数据
```bash
$ bin/hdfs --daemon start namenode                                                        # （到已格式化文件系统的NameNode机器上）启动NameNode
$ bin/hdfs namenode -bootstrapStandby                                                     # （到未格式化的NameNode机器上执行）同步NameNode元数据，前提是已格式化文件系统的NameNode要启动
# 同步成功在倒数第几行会打印：common.Storage: Storage directory /home/hadoop-3.1.2/data/dfs/name has been successfully formatted.
# 也可以去看 data 目录所生成的文件
```

#### 十一、将Hadoop集群信息注册到Zookeeper集群（前提是Zookeeper集群已启动）
```bash
$ bin/hdfs zkfc -formatZK                                                                 # 到已启动NameNode的机器上执行
# 是否注册成功在倒数第几行会打印：ha.ActiveStandbyElector: Successfully created /hadoop-ha/mycluster in ZK.
# 也可以到Zookeeper集群去看所生成的目录文件
```


#### 十二、启动集群
```bash
$ sbin/start-dfs.sh                                             # 配置了环境变量可以在任意目录执行 start-dfs.sh
$ jps                                                           # 查看进程情况，浏览器访问：http://NameNode节点IP:9870（看看NameNode情况）
```

#### 十三、测试NameNode是否自动故障切换
```bash
$ bin/hdfs --daemon stop namenode                               # （模拟提供服务的NameNode停止）到Active NameNode上执行
#   浏览器访问：http://NameNode节点IP:9870（看看NameNode情况）
```

#### 十四、停止集群
```bash
$ sbin/stop-dfs.sh                                              # 配置了环境变量可以在任意目录执行 stop-dfs.sh
```

#### 十、简单使用
```bash
$ ./bin/hdfs dfs --help                                         # 查看 hdfs dfs 命令基础使用
$ ./bin/hdfs dfs -mkdir /tools                                  # 在根目录下创建 tools 目录
$ ./bin/hdfs dfs -put /home/tools/hadoop-3.1.2.tar.gz /tools    # 上传文件至HDFS /tools目录
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
