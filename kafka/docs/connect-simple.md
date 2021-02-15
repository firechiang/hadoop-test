### 这个要测试的Connct是将数据加载到Kafka，再写回数据库的另一张表里面
#### 一、从 https://www.confluent.io/hub/ 下载别人已经开发好的Connect插件
![image](https://github.com/firechiang/hadoop-test/blob/master/kafka/images/001.png)
![image](https://github.com/firechiang/hadoop-test/blob/master/kafka/images/002.png)
![image](https://github.com/firechiang/hadoop-test/blob/master/kafka/images/003.png)

#### 二、在Kafka安装同级目录处建立plugins目录，将上面下载好的文件解压至该目录，再下载mysql-connector-java-8.0.23.jar放置lib目录下
![image](https://github.com/firechiang/hadoop-test/blob/master/kafka/images/004.png)

#### 三、修改 [vi connect-distributed.properties] Connect 配置文件
```bash
# Kafka 服务地址
bootstrap.servers=127.0.0.1:9092

# 集群ID（注意：每个节点的配置要相同，一般使用默认即可）
group.id=connect-cluster

# Key Value转换器（一般使用默认即可）
key.converter=org.apache.kafka.connect.json.JsonConverter
value.converter=org.apache.kafka.connect.json.JsonConverter

# 管理端暴露的端口（注意：这个一定要配置）
rest.port=9083

# 插件目录，就是上面下载的Connect插件解压后所在目录（注意：配置的是上一级的目录，这个一定要配置）
plugin.path=/home/chiangfire/data-dev/kafka/plugins
```

#### 四、启动Kafka Connect
```bash
# 指定配置文件启动Kafka Connect
# connect-distributed.sh /home/kafka_2.12-2.2.0/config/connect-distributed.properties
# 注意：-daemon 表示加守护进程启动（也就是后台运行）
$ connect-distributed.sh -daemon /home/kafka_2.12-2.2.0/config/connect-distributed.properties
```

#### 四、测试Kafka Connect
```bash
# 查看Connect插件相关信息（注意：这个请求会返回一些Connect的相关信息）
$ curl http://127.0.0.1:9083/connector-plugins

[{"class":"io.confluent.connect.jdbc.JdbcSinkConnector","type":"sink","version":"10.0.1"},{"class":"io.confluent.connect.jdbc.JdbcSourceConnector","type":"source","version":"10.0.1"},{"class":"org.apache.kafka.connect.file.FileStreamSinkConnector","type":"sink","version":"2.7.0"},{"class":"org.apache.kafka.connect.file.FileStreamSourceConnector","type":"source","version":"2.7.0"},{"class":"org.apache.kafka.connect.mirror.MirrorCheckpointConnector","type":"source","version":"1"},{"class":"org.apache.kafka.connect.mirror.MirrorHeartbeatConnector","type":"source","version":"1"},{"class":"org.apache.kafka.connect.mirror.MirrorSourceConnector","type":"source","version":"1"}]

# 查看Connect连接器的任务（注意：这个时候应该是空的，什么也没有）
$ curl http://127.0.0.1:9083/connectors
[]

# 创建数据加载任务（就是将MySQL里面的数据加载到Kafka） 
# name=任务名称，table.whitelist=加载数据库的哪些表多个逗号隔开
# mode=incrementing（表示增量更新，就是有新的数据就加载）
# incrementing.column.name=增量更新以表里面的哪个字段为标准，topic.prefix=Topic的前缀（如果是加载user表里面的数据，就会生成topic-connect-mysql-user这样的Topic）
$ curl -X POST -H 'Content-Type: application/json' -i 'http://127.0.0.1:9083/connectors'        \
  --data                                                                                        \
  '{"name":"connect-mysql-load-test",                                                             
    "config":{                                                                                      
	    "connector.class":"io.confluent.connect.jdbc.JdbcSourceConnector",                              
	    "connection.url":"jdbc:mysql://127.0.0.1:3306/kafka_connect_test?user=root&password=Jiang@123", 
	    "table.whitelist":"k_user",                                                                     
	    "incrementing.column.name": "uuid",                                                             
	    "mode":"incrementing",                                                                          
	    "topic.prefix": "connect-mysql-"}}'
	    
# 创建成功后返回的消息	    
{"name":"connect-mysql-load-test","config":{"connector.class":"io.confluent.connect.jdbc.JdbcSourceConnector","connection.url":"jdbc:mysql://127.0.0.1:3306/kafka_connect_test?user=root&password=Jiang@123","table.whitelist":"k_user","incrementing.column.name":"uuid","mode":"incrementing","topic.prefix":"connect-mysql-","name":"connect-mysql-load-test"},"tasks":[],"type":"source"}	

# 查看Connect连接器的任务（注意：这个时候应该有一条connect-mysql-load-test任务记录）
$ curl http://127.0.0.1:9083/connectors
["connect-mysql-load-test"] 

# 重开一个窗口监听消息
# 注意：监听起来以后，手动往k_user表里面添加数据。添加完成以后再看监听窗口是否有数据，有数据表示正常OK
$ kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic connect-mysql-k_user --from-beginning

# 创建数据写出任务（就是将Kafka里面的数据写到MySQL）
# name=任务名称，topics=要将哪个Topic里面的数据写到MySQL，auto.create=是否需要自动创建表
# table.name.format=数据要写到哪张表里面.fields=表主键字段名
$ curl -X POST -H 'Content-Type: application/json' -i 'http://127.0.0.1:9083/connectors' \
  --data                                                                                 \
  '{"name":"connect-mysql-write-test",
    "config":{
	    "connector.class":"io.confluent.connect.jdbc.JdbcSinkConnector",
	    "connection.url":"jdbc:mysql://127.0.0.1:3306/kafka_connect_test?user=root&password=Jiang@123",
	    "topics":"connect-mysql-k_user",
	    "auto.create":"false",
	    "insert.mode": "upsert",
	    "pk.mode":"record_value",
	    "pk.fields":"uuid",
	    "table.name.format": "k_user_back"}}'

# 创建成功后返回的消息	  	    
{"name":"connect-mysql-write-test","config":{"connector.class":"io.confluent.connect.jdbc.JdbcSinkConnector","connection.url":"jdbc:mysql://127.0.0.1:3306/kafka_connect_test?user=root&password=Jiang@123","topics":"connect-mysql-k_user","auto.create":"false","insert.mode":"upsert","pk.mode":"record_value","pk.fields":"uuid","table.name.format":"k_user_back","name":"connect-mysql-write-test"},"tasks":[],"type":"sink"}	    
	    
# 查看Connect连接器的任务（注意：这个时候应该有两条connect-mysql-load-test，connect-mysql-write-test任务记录）
$ curl http://127.0.0.1:9083/connectors
["connect-mysql-load-test","connect-mysql-write-test"]	

# 最后手动往k_user表里面添加数据，再去k_user_back表里面看数据是否自动同步过来了

# 删除任务 connect-mysql-load-test
$ curl -X DELETE -i 'http://127.0.0.1:9083/connectors/connect-mysql-load-test'
```


