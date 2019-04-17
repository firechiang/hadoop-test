### Windows开发搭建
##### 修改 \etc\hadoop\mapred-site.xml 文件
```bash
<!-- 使用YARN资源管理器  -->
<property>
    <name>mapreduce.framework.name</name>
    <value>yarn</value>
</property>

<!-- Hadoop资源目录，YARN执行MapReduce程序需要（注意：配置 HADOOP_HOME 环境变量）  -->
<property>
    <name>mapreduce.application.classpath</name>
    <value>
	    %HADOOP_HOME%/etc/hadoop,
	    %HADOOP_HOME%/share/hadoop/common/*,
	    %HADOOP_HOME%/share/hadoop/common/lib/*,
	    %HADOOP_HOME%/share/hadoop/hdfs/*,
	    %HADOOP_HOME%/share/hadoop/hdfs/lib/*,
	    %HADOOP_HOME%/share/hadoop/mapreduce/*,
	    %HADOOP_HOME%/share/hadoop/mapreduce/lib/*,
	    %HADOOP_HOME%/share/hadoop/yarn/*,
	    %HADOOP_HOME%/share/hadoop/yarn/lib/*
    </value>
</property>
```

##### 修改 \etc\hadoop\yarn-site.xml 文件
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

<property> 
    <name>yarn.resourcemanager.address</name>  
    <value>localhost:8032</value>  
</property>  

<property>  
    <name>yarn.resourcemanager.scheduler.address</name>  
    <value>localhost:8030</value>  
</property>  

<property>  
    <name>yarn.resourcemanager.resource-tracker.address</name>  
    <value>localhost:8031</value>  
</property>
```

##### 启动yarn（使用管理员模式打开命令行）（浏览器访问使用：http://ResourceManager机器:8088）
```bash
$ start-yarn.sh                           # 启动yarn
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
