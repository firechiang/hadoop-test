### Hadoop-3.x 单节点搭建
#### 一、预先准备环境
```bash
$ wget http://mirrors.shu.edu.cn/apache/hadoop/common/hadoop-3.2.0/hadoop-3.2.0.tar.gz    # 下载安装包
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
    <value>hdfs://server-001:9820</value>
</property>
<!-- 指定hadoop运行时产生临时文件的存储目录（注意创建该目录） -->
<property>
    <name>hadoop.tmp.dir</name>
    <value>/home/hadoop-3.2.0/tem</value>                                                
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
$ ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa                                                # 生成私钥和公钥
$ cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys                                         # 复制公钥到authorized_keys文件
$ chmod 0600 ~/.ssh/authorized_keys                                                       # 修改权限
```

#### 四、修改hosts文件信息
##### 4.1 修改 [vi /etc/hosts] 在空白处添加如下内容
```bash
192.168.78.128 server-001
```

#### 五、修改从节点信息
##### 4.1 修改 [vi workers] 修改为当前机器名称或IP（有多个的话写多个一行一个）
```bash
server-001
```

#### 六、配置Hadoop环境变量[vi ~/.bashrc]在末尾添加如下内容
```bash
export HADOOP_HOME=/home/hadoop-3.2.0
PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin                                             # linux以 : 号隔开，windows以 ; 号隔开
$ source ~/.bashrc                                                                        # （系统重读配置）在各个机器上执行使配置文件生效（实验：敲个hdf然后按Tab键，如果补全了说明配置成功了）
```

#### 七、格式化文件系统
```bash
$ bin/hdfs namenode -format
```


#### 八、启动NameNode，DataNode，SecondaryNameNode
```bash
$ sbin/start-dfs.sh
$ jps                                                           # 查看上面那三个进程是否都启动了，如果都启动了可以到浏览器访问：http://server-001:9870
```

#### 九、简单使用
```bash
$ ./bin/hdfs dfs --help                                         # 查看 hdfs dfs 命令基础使用
$ ./bin/hdfs dfs -mkdir /tools                                  # 在根目录下创建 tools 目录
$ ./bin/hdfs dfs -put /home/tools/hadoop-3.1.2.tar.gz /tools    #上传文件至HDFS /tools目录
$ ./bin/hdfs dfs -ls /tools                                     # 查看文件是否存在
```
