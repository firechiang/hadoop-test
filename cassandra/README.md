#### [一、Windows开发搭建][1]
#### [二、单数据中心集群搭建(推荐使用)][2]
#### [三、多数据中心集群搭建][3]
#### [四、ScyllaDB单节点搭建][4]
#### [五、ScyllaDB单数据中心集群搭建(推荐使用)][5]
#### [六、ScyllaDB监控搭建][6]


#### 种子节点说明(种子节点用于节点启动的时候发现整个集群，可以将任何一个节点设置为种子， 对于种子节点没有什么特殊的，只要你把它写入种子列表里，它就是一个种子节点)
```bash
1，如果你配置了你的节点指向几个节点作为种子，那么你集群中的节点会发送相比非种子节点较多的gossip信息到种子节点。
        换句话说：种子节点相当于gossip网络的集线器，每个节点通过种子节点可以很快知道其他节点的状态。
       
2，在一个节点新加入集群的时候，需要指定种子节点用于发现集群中其它节点，当你增加一个新节点到集群中的时候，你需要至少指定一个活着的种子节点可以连接，
        一旦一个节点加入这个集群，它学习到了其它节点，他在下次启动的时候就不需要种子节点了。

3，如果是往旧的集群里面添加新的节点，不能把自己设置成种子节点(因为它要通过种子节点发现集群以及同步数据)，如果是初始化一个新集群是可以将自己设置成种子节点。 
```


[1]: https://github.com/firechiang/hadoop-test/tree/master/cassandra/docs/windows-single-node.md
[2]: https://github.com/firechiang/hadoop-test/tree/master/cassandra/docs/setup-single-cluster-node.md
[3]: https://github.com/firechiang/hadoop-test/tree/master/cassandra/docs/setup-many-cluster-node.md
[4]: https://github.com/firechiang/hadoop-test/tree/master/cassandra/docs/scylla-single-node.md
[5]: https://github.com/firechiang/hadoop-test/tree/master/cassandra/docs/scylla-single-cluster-node.md
[6]: https://github.com/firechiang/hadoop-test/tree/master/cassandra/docs/scylla-cluster-monitor.md