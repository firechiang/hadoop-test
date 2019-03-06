### Zookeeper-3.x 集群搭建
#### 一、理论说明
#### 二、预先准备环境
```bash
$ wget http://mirror.bit.edu.cn/apache/zookeeper/zookeeper-3.4.13/zookeeper-3.4.13.tar.gz     # 下载安装包
```

#### 三、安装
```bash
$ tar -zxvf zookeeper-3.4.13.tar.gz                           # 解压
$ vi /etc/profile                                             # 修改环境变量，添加如下内容
    export ZOOKEEPER_HOME=/home/zookeeper-3.4.13
    PATH=$PATH:$ZOOKEEPER_HOME/bin
$ :wq                                                         # 保存修改
$ source /etc/profile                                         # 重读配置使环境变量生效

$ mv zoo_sample.cfg zoo.cfg                                   # 到Zookeeper conf目录修改配置文件名称
$ vi zoo.cfg                                                  # 修改配置文件内容如下
    dataDir=/home/zookeeper-3.4.13/data                       # Zookeeper数据目录（注意创建这个目录）
    # 新增内容; 2888是主与从的数据通信端口,3888是当主挂断以后选举新的主节点的通信端口（选举通信端口）
    server.1=server-002:2888:3888
    server.2=server-003:2888:3888
    server.3=server-004:2888:3888
    
$ scp -r ./zookeeper-3.4.13/ root@server-003:/home            # 分发安装包到各个机器  

# 配置集群id信息，要与zoo.cfg配置文件里面的id和机器对应，具体依次执行如下命令
$ echo 1 > /home/zookeeper-3.4.13/data/myid      # （server-002执行）在ZK数据目录（/home/zookeeper-3.4.13/data） 下建立文件myid里面的内容是1
$ echo 2 > /home/zookeeper-3.4.13/data/myid      # （server-003执行）在ZK数据目录（/home/zookeeper-3.4.13/data） 下建立文件myid里面的内容是2
$ echo 3 > /home/zookeeper-3.4.13/data/myid      # （server-004执行）在ZK数据目录（/home/zookeeper-3.4.13/data） 下建立文件myid里面的内容是3
$ cat /home/zookeeper-3.4.13/data/myid           # 查看myid文件是否有id信息

$ zkServer.sh start                              # 到各个节点上启动Zookeeper
$ jps                                            # 查看java进程，如果有 QuorumPeerMain 进程说明启动成功
$ zkServer.sh status                             # 查看当前Zookeeper的状态，如果看到 Mode: follower 或者  Mode: leader 这样的状态说明集群搭建成功

$ zkServer.sh stop                               # 停止Zookeeper                              
```