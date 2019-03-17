##### 机器任务分配（在HDFS集群之上）如下：
```bash

              NameNode     DataNode      ZooKeeper     ZKFC     JournalNode     ReourceManager      NodeManager
server001        是                                     是           是               是
server002        是           是            是          是                                               是
server003                     是            是                       是               是                 是
server004                     是            是                       是                                  是
```

##### 修改[vi yarn-env.sh]
```bash
YARN_RESOURCEMANAGER_USER=root         # YARN_RESOURCEMANAGER_USER所使用的角色
YARN_NODEMANAGER_USER=root             # YARN_NODEMANAGER_USER所使用的角色
HADOOP_SECURE_DN_USER=root             # HADOOP_SECURE_DN_USER所使用的角色（一般不需要配置）
```

##### 修改[vi mapred-site.xml]
```bash
<!-- 使用YARN资源管理器  -->
<property>
    <name>mapreduce.framework.name</name>
    <value>yarn</value>
</property>

<!-- Hadoop资源目录，YARN执行MapReduce程序需要  -->
<property>  
    <name>mapreduce.application.classpath</name>  
    <value>
	    /home/hadoop-3.2.0/etc/hadoop,  
	    /home/hadoop-3.2.0/share/hadoop/common/*,  
	    /home/hadoop-3.2.0/share/hadoop/common/lib/*,  
	    /home/hadoop-3.2.0/share/hadoop/hdfs/*,  
	    /home/hadoop-3.2.0/share/hadoop/hdfs/lib/*,  
	    /home/hadoop-3.2.0/share/hadoop/mapreduce/*,  
	    /home/hadoop-3.2.0/share/hadoop/mapreduce/lib/*,  
	    /home/hadoop-3.2.0/share/hadoop/yarn/*,  
	    /home/hadoop-3.2.0/share/hadoop/yarn/lib/*  
    </value>  
</property>
```
##### 修改[vi yarn-site.xml]
```bash
<!-- 集成Shuffle功能  -->
<property>
    <name>yarn.nodemanager.aux-services</name>
    <value>mapreduce_shuffle</value>
</property>

<!-- Shuffle处理类，现在配的这个是默认处理类（可以不配）  -->
<!-- <property>  
    <name>yarn.nodemanager.aux-services.mapreduce.shuffle.class</name>  
    <value>org.apache.hadoop.mapred.ShuffleHandle</value>  
</property> -->

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

##### 分发[yarn-env.sh]和[mapred-site.xml]和[yarn-site.xml]文件到其他机器的当前目录
```bash
scp yarn-env.sh mapred-site.xml yarn-site.xml server002:`pwd`
scp yarn-env.sh mapred-site.xml yarn-site.xml server003:`pwd`
scp yarn-env.sh mapred-site.xml yarn-site.xml server004:`pwd`
```
##### 启动yarn（启动之前最好把ResourceManager能免密码登录到各个NodeManager机器之上）（浏览器访问使用：http://ResourceManager机器:8088，如果访问非Active节点一般会默认跳Active节点）
```bash
$ start-yarn.sh                           # 在能免密码登录到各个节点上的机器上执行
$ jps                                     # 到各个节点上查看进程启动信息
$ yarn-daemon.sh start resourcemanager    # 如果ResourceManager没有启动，才到ResourceManager所在的节点执行该命令
$ yarn-daemon.sh stop resourcemanager     # 测试ResourceManager是否自动故障切换（在Active ResourceManager机器上执行）

$ stop-yarn.sh                            # 停止yarn，在能免密码登录到各个节点上的机器上执行
```

##### 简单使用
```bash
$ hadoop jar hadoop-mapreduce-examples-3.2.0.jar wordcount /user/test/test.txt /data/wc/output  # 执行MapReduce执行程序
# hadoop-mapreduce-examples-3.2.0.jar           # 要执行的jar包（linux文件地址）
# wordcount                                     # 要执行的程序名（一个jar包可能包含多个程序）
# /user/test/test.txt                           # 需要分析的文件地址（HDFS地址）
# /data/wc/output                               # 文件分析完成结果的输出地址，该目录必须为空或不存在，否则程序立即停止（HDFS地址）



$ hadoop jar wordcount.jar com.firecode.hadooptest.mapreduce.wordcount.WordCountMain    # 执行自定义计算
# wordcount.jar                                                 # 自己打的jar包名称
# com.firecode.hadooptest.mapreduce.wordcount.WordCountMain     # Main函数所在类名

$ hdfs dfs -get /test_txt/result/wordcount/part-r-00000 ./      # 下载刚刚计算完成的结果文件
```

