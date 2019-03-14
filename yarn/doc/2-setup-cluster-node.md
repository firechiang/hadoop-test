##### 机器任务分配（在HDFS集群之上）如下：
```bash

              NameNode     DataNode    ZK    ZKFC   JournalNode     ReourceManager      NodeManager
server001        是                                                                                   是                        是                                                是
server002        是                                 是                        是                是                                                                                                                                    是
server003                     是                       是                                             是                                                是                                                  是
server004                     是                       是                                             是                                                                                                       是
```
##### 修改[mapred-site.xml]
```bash
<property>
    <name>mapreduce.framework.name</name>
    <value>yarn</value>
</property>
```
##### 修改[yarn-site.xml]
```bash
<!-- 集成Shuffle功能  -->
<property>
    <name>yarn.nodemanager.aux-services</name>
    <value>mapreduce_shuffle</value>
</property>

<!-- 开启HA -->
<property>
    <name>yarn.resourcemanager.ha.enabled</name>
    <value>true</value>
</property>

<!-- 配置Zookeeper集群  -->
<property>
    <name>hadoop.zk.address</name>
    <value>server002:2181,server003:2181,server004:2181</value>
</property>

<!-- 配置集群ID -->
<property>
    <name>yarn.resourcemanager.cluster-id</name>
    <value>mycluster</value>
</property>

<!-- 配置集群所包含的ReourceManager机器 -->
<property>
    <name>yarn.resourcemanager.ha.rm-ids</name>
    <value>rm1,rm2</value>
</property>

<!-- 配置rm1所在机器  -->
<property>
    <name>yarn.resourcemanager.hostname.rm1</name>
    <value>server001</value>
</property>

<!-- 配置rm2所在机器  -->
<property>
    <name>yarn.resourcemanager.hostname.rm2</name>
    <value>server003</value>
</property>

<!-- 配置rm1 web访问地址  -->
<property>
    <name>yarn.resourcemanager.webapp.address.rm1</name>
    <value>server001:8088</value>
</property>

<!-- 配置rm2 web访问地址  -->
<property>
  <name>yarn.resourcemanager.webapp.address.rm2</name>
  <value>server003:8088</value>
</property>
```

##### 分发[mapred-site.xml]和[yarn-site.xml]文件到其他机器的当前目录
```bash
scp mapred-site.xml yarn-site.xml server002:`pwd`
scp mapred-site.xml yarn-site.xml server003:`pwd`
scp mapred-site.xml yarn-site.xml server004:`pwd`
```
##### 启动yarn
```bash
$ start-yarn.sh
$ jps                                     # 到各个节点上查看进程启动信息
$ yarn-daemon.sh start resourcemanager    # 如果ResourceManager没有启动，才到ResourceManager所在的节点执行该命令
$ cat slaves                              # 查看从节点信息
```

