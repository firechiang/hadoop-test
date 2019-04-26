#### 一、预先准备环境，DataStax多数据中心配置建议：https://docs.datastax.com/en/cassandra/3.0/cassandra/initialize/initMultipleDS.html
```bash
$ wget http://mirror.bit.edu.cn/apache/cassandra/3.11.4/apache-cassandra-3.11.4-bin.tar.gz    # 下载安装包
$ tar -zxvf apache-cassandra-3.11.4-bin.tar.gz -C ../                                         # 解压到上层目录
```

#### 二、配置环境变量[vi ~/.bashrc]
```bash
export CASSANDRA_HOME=/home/apache-cassandra-3.11.4
PATH=$PATH:$CASSANDRA_HOME/bin                                                                # linux以 : 号隔开，windows以 ; 号隔开

$ source ~/.bashrc                                                                            # （系统重读配置）在各个机器上执行使配置文件生效（实验：敲个beel然后按Tab键，如果补全了说明配置成功了）
$ echo $CASSANDRA_HOME                                                                        # 查看是否能获取到环境变量的值
```

#### 三、修改[vi /conf/cassandra.yaml]（注意：以下配置项文件里面都有，只需修改值即可）
```bash
# 集群的名称
cluster_name: 'myCassandraCluster'

# 配置用户名密码才能登陆
authenticator: PasswordAuthenticator

# SSTable文件在磁盘中的存储位置,可以有多个地址（注意创建目录，和读写权限）
data_file_directories:
  - /home/apache-cassandra-3.11.4/data
  
# commitlog文件在磁盘中的存储位置（注意创建目录，和读写权限）
commitlog_directory: /home/apache-cassandra-3.11.4/commitlog

# 缓存数据存储目录（注意创建目录，和读写权限）
saved_caches_directory: /home/apache-cassandra-3.11.4/saved_caches

# 集群所有节点的主机或IP
seed_provider:
  - class_name: org.apache.cassandra.locator.SimpleSeedProvider
    parameters:
      - seeds: "server001,server002,server003"
 
# 是否开始thrift rpc服务器 
start_rpc: false
      
# 集群中服务器与服务器之间相互通信的地址，也可以配置listen_interface，指定使用哪个网卡接口。两者选一配置即可，不要同时配置（这里配置集群的所有主机或IP，为了安全考虑建议配置防火墙）   
listen_address: server001,server002,server003

# 服务器对外提供服务的地址（注意：这里配置集群的所有主机或IP，为了安全考虑建议配置防火墙）
rpc_address: server001,server002,server003   

# 服务器对外提供服务的端口
rpc_port: 9160

# 这个端口用于接收命令和数据（为了安全考虑建议配置防火墙））
storage_port: 7000

# 客户端和服务端的通信端口（注意配置防火墙）
native_transport_port: 9042

# 数据中心机架，配置文件感知策略（我们配的这个策略是：dc(数据中心)和rack(机架)通过显式的定义在cassandra-topology.properties文件里面）
endpoint_snitch: PropertyFileSnitch
```

#### 三、修改[vi /conf/cassandra-topology.properties]数据中心和机架的配置，先将原有的配置都注释掉，再配置（注意：集群中每个节点，该文件内容需一致）
```bash
# Cassandra Node IP(节点主机或IP)=Data Center(数据中心):Rack(机架)。名字可以顺便起
server001=DC1:RAC1
server002=DC1:RAC2
server003=DC1:RAC2
```

#### 四、修改[vi /conf/cassandra-env.sh]Cassandra默认只提供本地主机访问JMX，如果要启用远程JMX连接，注释下面的代码（vi进入命令模式，用/加要搜索的内容进行搜索）
```bash
if [ "x$LOCAL_JMX" = "x" ]; then
    LOCAL_JMX=yes
fi
```

#### 五、启动，关闭，查看集群
```bash
$ cassandra -f -R                                      # 启动，-f表示前台启动，-R表示以管理员身份启动（测试使用）
$ cassandra -R                                         # 启动，-R表示以管理员身份启动（生产可以使用）
$ pkill -f cassandra                                   # 停止

$ nodetool status                                      # 查看集群的状态
```

#### 六、远程连接Cassandra初始化账号和密码
```bash
$ cqlsh 192.168.83.137 9042 -ucassandra -pcassandra    # 连接（远程Cassandra的IP：127.0.0.1，端口：9042，用户名(-u)：cassandra，密码(-p)：cassandra）
$ create user jiang with password 'jiang' superuser;   # 创建超级管理员jiang，密码：jiang
$ exit;                                                # 退出当前登录账号
$ cqlsh 192.168.83.137 9042 -ujiang -pjiang            # 登录我们刚刚创建的账号
$ drop user cassandra;                                 # 删除默认管理员账号
```

#### 七、命令行简单使用
```bash
$ describe keyspaces;                                  # 查看所有的键空间（相当于Mysql的库）
# 创建test_test键空间（keyspace），简单的副本放置策略，数据副本1个
$ create keyspace if not exists test_test with durable_writes = true and replication = {'class':'SimpleStrategy','replication_factor':1};
$ use test_test;                                           # 进入 test_test 键空间

# 创建通话记录表
$ create table call_record(
      id int PRIMARY KEY,
      dnum_phone text,
      length int,
      type int,
      create_time TIMESTAMP);
      
$ describe tables;                                     # 显示所有表
$ describe table call_record;                          # 查看表  call_record 的详细信息 
$ insert into call_record(id,dnum_phone,length,type,create_time) values(1,'13143457381',45,1,'2019-02-05 20:20:20');
$ select * from call_record;
```