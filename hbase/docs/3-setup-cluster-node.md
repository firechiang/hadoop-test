### HBase2.1.4 完全分布式搭建（建立在HDFS集群之上）
```bash
----------|------------|------------|------------|------------|-------------|----------------|-------------|-------------|-------------------|
          |  NameNode  |  DataNode  |    ZKFC    |  ZooKeeper | JournalNode | ReourceManager | NodeManager | HBaseMaster | HBaseRegionServer |
----------|------------|------------|------------|------------|-------------|----------------|-------------|-------------|-------------------|
server001 |      Y     |            |      Y     |            |      Y      |        Y       |             |      Y      |         Y         |
----------|------------|------------|------------|------------|-------------|----------------|-------------|-------------|-------------------|
server002 |      Y     |      Y     |      Y     |      Y     |             |                |      Y      |             |                   |
----------|------------|------------|------------|------------|-------------|----------------|-------------|-------------|-------------------|
server003 |            |      Y     |            |      Y     |      Y      |        Y       |      Y      |             |         Y         |
----------|------------|------------|------------|------------|-------------|----------------|-------------|-------------|-------------------|
server004 |            |      Y     |            |      Y     |      Y      |                |      Y      |      Y      |         Y         |
----------|------------|------------|------------|------------|-------------|----------------|-------------|-------------|-------------------|
```
#### 一、安装
```bash
$ wget http://mirrors.tuna.tsinghua.edu.cn/apache/hbase/2.1.4/hbase-2.1.4-bin.tar.gz    # 下载服务端安装包
$ tar -zxvf hbase-2.1.4-bin.tar.gz -C ../                                               # 解压安装包到上级目录
```

#### 二、HBaseMaster节点免密码登陆所有HBaseRegionServer节点（在A机器生成一对公钥私钥，将公钥拷贝到想要登录的主机）
```bash
$ ssh-keygen                                                                              # 生成私钥和公钥（如果已经有了就不需要执行了）
$ ssh-copy-id -i ~/.ssh/id_rsa.pub server001                                              # 将公钥拷贝到 server001上（这样我们就可以直接免密码登录server001了）
$ ssh server001                                                                           # 测试面密码登陆
$ exit                                                                                    # 退出登录
```

#### 三、安装时间同步软件（集群的每台机器都要安装）
```bash
$ yum install -y ntp                                                                    # 安装时间同步器
$ rpm -qa|grep ntp                                                                      # 检查是否安装成功
$ ntpdate ntp1.aliyun.com                                                               # 同步时间（每台都要同步，这里同步的是"阿里云"时间服务器）
$ date                                                                                  # 查看本机时间
```

#### 四、配置环境变量[vi ~/.bashrc]
```bash
export HBASE_HOME=/home/hbase-2.1.4
PATH=$PATH:$HBASE_HOME/bin                                                              # linux以 : 号隔开，windows以 ; 号隔开

$ source ~/.bashrc                                                                      # （系统重读配置）在各个机器上执行使配置文件生效（实验：敲个beel然后按Tab键，如果补全了说明配置成功了）
```

#### 五、修改配置[vi conf/hbase-env.sh]（以下配置在文件里面都有，把注释打开，结果改一下即可）
```bash
export JAVA_HOME=/usr/lib/jvm/jdk1.8.0_171                                              # 配置JDK
export HADOOP_HOME=/home/hadoop-3.1.2                                                   # 配置HADOOP_HOME
export HBASE_MANAGES_ZK=false                                                           # 是否启用HBase自带的Zookeeper（我们要使用我们自己的集群所以把这个改为false）    
```

#### 六、修改配置[vi conf/hbase-site.xml]
```bash
<!-- 开启集群模式 -->
<property>
    <name>hbase.cluster.distributed</name>
    <value>true</value>
</property>

<!-- HDFS数据目录（目录会自动创建，我们这里指定的是NameNode集群，因为我们下面会拷贝HDFS集群配置文件，HBase会根据拷贝的配置文件来识别HDFS NameNode集群） -->
<property>
    <name>hbase.rootdir</name>
    <value>hdfs://mycluster/hbase</value>
</property>

<!-- zookeeper集群 -->
<property>
    <name>hbase.zookeeper.quorum</name>
    <value>server002:2181,server003:2181,server004:2181</value>
</property>
```

#### 七、修改配置[vi conf/regionservers]（HBaseRegionServer所在机器）
```bash
server001
server003
server004
```
#### 八、修改配置[vi conf/backup-masters]（备用HBaseMaster所在机器，这个文件在配置目录里面没有需要自己手动创建）
```bash
server004
```
#### 九、拷贝HDFS集群配置文件 hdfs-site.xml 到 HBase 的配置目录 conf 目录下
```bash
$ cp /home/hadoop-3.1.2/etc/hadoop/hdfs-site.xml ./                                     # 拷贝文件（因为我们就在HBase conf目录下，所以直接用 ./） 
```

#### 十、解决 hbase-2.1.4 使用的  htrace-core4-4.2.0-incubating.jar 里面没有 org/apache/htrace/SamplerBuilder 类问题
```bash
$ cd /home/hbase-2.1.4/lib                                                              # 到HBase依赖目录
$ 
```

#### 十一、分发HBase安装文件到各个机器
```bash
$ scp -r ./hbase-2.1.4 root@server003:/home
```

#### 十二、启动HBase
```bash
$ start-hbase.sh                                      
```
