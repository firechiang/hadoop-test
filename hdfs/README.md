### Hadoop HDFS学习记录
- **存储模型**
> 文件线性切割成块（Block=128M），每个块有偏移量（Offset：块的起始位置在整个文件所在的位置），整个文件就是一个Byte数组
> Block分散存储在集群节点当中
> 单一文件Block大小一致，文件与文件可以不一致
> Block可以设置副本数，副本无序分散在不同节点之中 （副本数不要超过节点数）
> 文件上传可以设置Block大小和副本数（如果计算程序较多，可以考虑增加副本数，以减少单机的资源占用。因为计算程序会在副本所在的机器上开盘计算进程）
> 已上传的文件Block副本数可以调整，大小不变
> 只支持一次写入多次读取，同一时刻只有一个写入者
> 可以append追加数据
##### [一、单节点搭建][1]

[1]: https://github.com/firechiang/hadoop-test/tree/master/hdfs/docs/1-setup-single-node.md


















