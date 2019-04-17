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
	    /home/hadoop-3.1.2/etc/hadoop,  
	    /home/hadoop-3.1.2/share/hadoop/common/*,  
	    /home/hadoop-3.1.2/share/hadoop/common/lib/*,  
	    /home/hadoop-3.1.2/share/hadoop/hdfs/*,  
	    /home/hadoop-3.1.2/share/hadoop/hdfs/lib/*,  
	    /home/hadoop-3.1.2/share/hadoop/mapreduce/*,  
	    /home/hadoop-3.1.2/share/hadoop/mapreduce/lib/*,  
	    /home/hadoop-3.1.2/share/hadoop/yarn/*,  
	    /home/hadoop-3.1.2/share/hadoop/yarn/lib/*  
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
```

##### 启动yarn（浏览器访问使用：http://ResourceManager机器:8088）
```bash
$ start-yarn.sh                           # 在能免密码登录到各个节点上的机器上执行
$ jps                                     # 查看进程进程启动信息
$ stop-yarn.sh                            # 停止yarn
```

##### 简单使用
```bash
$ hadoop jar hadoop-mapreduce-examples-3.1.2.jar wordcount /user/test/test.txt /data/wc/output  # 执行MapReduce执行程序
# hadoop-mapreduce-examples-3.1.2.jar           # 要执行的jar包（linux文件地址）
# wordcount                                     # 要执行的程序名（一个jar包可能包含多个程序）
# /user/test/test.txt                           # 需要分析的文件地址（HDFS地址）
# /data/wc/output                               # 文件分析完成结果的输出地址，该目录必须为空或不存在，否则程序立即停止（HDFS地址）



$ hadoop jar wordcount.jar com.firecode.hadooptest.mapreduce.wordcount.WordCountMain    # 执行自定义计算
# wordcount.jar                                                                         # 自己打的jar包名称
# com.firecode.hadooptest.mapreduce.wordcount.WordCountMain                             # Main函数所在类名

$ hdfs dfs -get /test_txt/result/wordcount/part-r-00000 ./                              # 下载刚刚计算完成的结果文件
```