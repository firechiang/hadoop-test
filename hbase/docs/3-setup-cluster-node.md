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

#### 二、所有HBaseMaster免密码登陆所有HBaseRegionServer节点（在A机器生成一对公钥私钥，将公钥拷贝到想要登录的主机）
```bash
$ ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa                                                # 生成私钥和公钥
$ cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys                                         # 复制公钥到authorized_keys文件
$ chmod 0600 ~/.ssh/authorized_keys                                                       # 修改权限
$ scp ~/.ssh/id_rsa.pub root@192.168.83.135:~/.ssh/id_rsa.pub_131                         # 将公钥拷贝到想要登录的主机（如果想要登录的主机.ssh目录不存在，就执行生成《公私钥的命令》）
$ cat ~/.ssh/id_rsa.pub_131 >> ~/.ssh/authorized_keys                                     #（到想要登录的主机上执行）将上一步考过来的公钥 复制到 authorized_keys
$ rm -rf id_rsa.pub_131                                                                   #（到想要登录的主机上执行）删除上一步考过来的公钥

$ ssh 192.168.83.135                                                                      # 测试登陆
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
```

#### 六、修改配置[vi conf/hbase-site.xml]
```bash
<-- 开启集群模式 -->
<property>
    <name>hbase.cluster.distributed</name>
    <value>true</value>
</property>

<!-- HDFS数据目录 -->
<property>
    <name>hbase.rootdir</name>
    <value>hdfs://localhost:8020/hbase</value>
</property>

<!-- zookeeper集群 -->
<property>
    <name>hbase.zookeeper.quorum</name>
    <value>server002:2181,server003:2181,server004:2181</value>
</property>

<!-- 存储zookeeper相关信息的目录（目录会自动创建） -->
<property>
    <name>hbase.zookeeper.property.dataDir</name>
    <value>/usr/local/zookeeper</value>
</property>
```