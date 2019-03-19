### Windows开发搭建
##### 修改 \etc\hadoop\mapred-site.xml 文件
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
```

##### 启动yarn（浏览器访问使用：http://ResourceManager机器:8088）
```bash
$ start-yarn.sh                           # 启动yarn
$ jps                                     # 查看进程进程启动信息
$ stop-yarn.sh                            # 停止yarn
```