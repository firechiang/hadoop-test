### HDFS基础理论
##### 存储模型
```bash
1. 文件线性切割成块（Block=128M），每个块有偏移量（Offset：块的起始位置在整个文件所在的位置），整个文件就是一个Byte数组
2. Block分散存储在集群节点当中
3. 单一文件Block大小一致（不包括最后一个块），文件与文件可以不一致
4. Block可以设置副本数，副本无序分散在不同节点之中 （副本数不要超过节点数）
5. 文件上传可以设置Block大小（最小1M，最大和默认都是128M）和副本数（如果计算程序较多，可以考虑增加副本数，以减少单机的资源占用。因为计算程序会在副本所在的机器上开盘计算进程）
6. 已上传的文件Block副本数可以调整，大小不变
7. 只支持一次写入多次读取（就是不可以修改），同一时刻只有一个写入者
8. 可以append追加数据
```

##### 副本放置策略
```bash
第一个副本：如果在集群之内（就是当前客户端上也有DataNode）就放置在当前的DataNode之上，如果是集群外提交，则随机挑选一台磁盘不太满，CPU不太忙的节点
第二个副本：放置在于第一个副本不同的机架的节点上
第三个副本：与第二个副本相同机架但不同的节点
更多副本：随机节点
```

##### 架构模型（主从模型）
```bash
1. 文件元数据MetaData（主：NameNode节点保存文件元数据），文件数据（从：DataNode节点保存文件Block数据）
       1.1 （主）元数据（数据大小，创建时间，所在位置等等信息）
       1.2 （从）数据本身
2. DataNode与NameNode保持心跳，向NameNode提交Block列表信息（当前有几个DataNode可用）  
3. HdfsClient与NameNode交换元数据信息
4. HdfsClient与DataNode交换文件Block数据
5. DataNode利用服务器本地文件系统存储数据块
```

### HDFS-3.X新特性
```bash
1. ClassPath isolation（防止不同版本jar包冲突（开发依赖jar可以重名））
2. 支持HDFS中的擦除编码（Erasure Encoding）
       2.1 默认的EC编码可以节约50%的存储空间，同时还可以承受更多的存储故障
3. DataNode内部添加了负载均衡 Disk Balancer（磁盘之间的负载均衡=如果机器添加了新的硬盘，Disk Balancer会自动将数据均衡到新硬盘上来） 
4. MapReduce任务级本地优化
5. MapReduce内存参数自动推断
       5.1 mapreduce.{map,reduce}.memory.mb
       5.2 mapreduce.{map,reduce}.java.opts
6. 基于cGroup的内存隔离和IO Disk隔离
7. 支持更改分配容器的资源Container resizing                 
             
```
##### [一、单节点搭建][1]
##### [一、高可用搭建][2]

[1]: https://github.com/firechiang/hadoop-test/tree/master/hdfs/docs/1-setup-single-node.md
[2]: https://github.com/firechiang/hadoop-test/tree/master/hdfs/docs/2-setup-cluster-node.md


















