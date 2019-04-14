### HBase2.1.4 伪分布式搭建（建立在HDFS集群之上）
#### 一、安装
```bash
$ wget http://mirrors.tuna.tsinghua.edu.cn/apache/hbase/2.1.4/hbase-2.1.4-bin.tar.gz    # 下载服务端安装包
$ tar -zxvf hbase-2.1.4-bin.tar.gz -C ../                                               # 解压安装包到上级目录
```

#### 二、配置环境变量[vi ~/.bashrc]
```bash
export HBASE_HOME=/home/hbase-2.1.4
PATH=$PATH:$HBASE_HOME/bin                                                              # linux以 : 号隔开，windows以 ; 号隔开

$ source ~/.bashrc                                                                      # （系统重读配置）在各个机器上执行使配置文件生效（实验：敲个beel然后按Tab键，如果补全了说明配置成功了）
```

#### 三、修改配置[vi conf/hbase-env.sh]（以下配置在文件里面都有，把注释打开，结果改一下即可）
```bash
export JAVA_HOME=/usr/lib/jvm/jdk1.8.0_171                                              # 配置JDK                                
export HBASE_MANAGES_ZK=true                                                            # 启用自带的Zookeeper
```

#### 四、修改配置[vi conf/hbase-site.xml]
```bash
<!-- hbase数据文件目录（目录会自动创建） -->
<property>
    <name>hbase.rootdir</name>
    <value>file:///home/HBaseData/hbase</value>
</property>

<!-- 存储zookeeper相关信息的目录（目录会自动创建） -->
<property>
    <name>hbase.zookeeper.property.dataDir</name>
    <value>/home/HBaseData/zookeeper</value>
</property>

<!-- 伪分布式本地文件模式禁用hflush/hsync -->
<property>
    <name>hbase.unsafe.stream.capability.enforce</name>
    <value>false</value>
</property>
```

#### 五、启动HBase和简单使用（注意：hbase-2.1.4/lib/client-facing-thirdparty/slf4j-log4j12-1.7.25.jar和hadoop-3.2.0里面的jar有冲突，如果我们使用的是hadoop-3.2.0就要删除hbase里面的这个jar包）
```bash
$ start-hbase.sh                                  # 启动HBase（启动完成后可访问HBase Master：http://192.168.229.133:16010，RegionServer：http://192.168.229.133:16030）

$ hbase shell                                     # 进入hbase命令行管理界面
$ status                                          # 查看hbase状态信息（1 active master, 0 backup masters, 1 servers, 0 dead, 2.0000 average load Took 0.4755 seconds）
$ whoami                                          # 查看当前登录用户 
$ list_namespace                                  # 查看所有的命名空间 （相当于mysql里面的库）
$ create_namespace 'test'                         # 创建test命名空间（创建库）
$ describe_namespace 'test'                       # 查看test命名空间的详细信息（库的详细信息）
$ drop_namespace 'test'                           # 删除test命名空间  （删除库）

$ create 'person','cf'                            # 创建表名为person,一个列族名为 cf   
$ create 'person1','cf1','cf2'                    # 创建表名为person1,两个列族，分别是 cf1和cf2 
$ list                                            # 查看所有表
$ describe '表名'                                 # 查看表详细信息以及配置信息（注意加引号）
$ disable '表名'                                  # 禁用表（注意加引号）
$ drop '表名'                                     # 删除表（注意：删除表之前要先禁用，否则无法删除）  （注意加引号）

$ put 'person','roekay001','cf:name','mamomao'   # 往person表添加数据，RowKey是roekay001，列族是cf，列名是name，值是mamomao
$ get 'person','roekay001','cf:name'             # 获取表名为person，RowKey为roekay001，列族为cf，列名为name的数据
$ delete 'person','roekay001','cf:name'          # 删除表名为person，RowKey为roekay001，列族为cf，列名为name的数据
$ flush 'person'                                 # 将表数据（内存里面）刷到磁盘（数据在/home/HBaseData/hbase/data/default）
$ truncate 'person'                              # 清空person表的所有数据
```

