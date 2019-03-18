### YARN（资源管理框架）

##### 基础理论
```bash
1，ResourceManager为主节点（整个YARN的核心，统计整个集群的资源使用情况），NodeManager（从节点）
2，NodeManager采集当前机器资源定时汇报给ResourceManager和管理Container生命周期（每一DataNode机器上都会有一个NodeManager，它们是一比一的关系）
3，Container（任务执行容器），默认NodeManager启动线程监控Container大小（默认1GB），如果超出申请资源额度则kill掉，Container支持Linux内核的cGroup隔离
```
##### 调用流程
![image](https://github.com/firechiang/hadoop-test/blob/master/yarn/image/1-yarn.png)

```bash
1，客户端想跑一个计算作业，先访问ResourceManager，然后ResourceManager会在整个集群中找一台相对不忙的节点
2，在这个不忙的节点上创建一个Application Master（调度程序），然后Application Master会获取当前作业的HDFS切片清单，然后将清单信息发送给ResourceManager
3，ResourceManager收到当前作业的HDFS切片清单后，返回一个或多个Container（数量由HDFS切片信息决定）
4，Container里面包含MapReduce任务代码和归属那个NodeManager管理（具体在那台机上跑），需要多少资源等相关信息
5，Application Master通过Container描述信息来调度Container执行，然后Container向Application Master汇报作业执行情况
```
#### [一、单节点搭建][1]
#### [一、高可用搭建][2]

[1]: https://github.com/firechiang/hadoop-test/blob/master/yarn/doc/1-setup-single-node.md
[2]: https://github.com/firechiang/hadoop-test/blob/master/yarn/doc/2-setup-cluster-node.md
