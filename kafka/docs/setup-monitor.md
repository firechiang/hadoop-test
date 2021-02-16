#### 一、环境准备
```bash
# 下载雅虎开源的Kafka管理监控项目并将其解压
$ wget -P /home/tools https://github.com/yahoo/CMAK/releases/download/3.0.0.5/cmak-3.0.0.5.zip
```

#### 二、修改[vi /home/chiangfire/data-dev/kafka/cmak-3.0.0.5/conf/application.conf]监控程序配置文件
```bash
# 管理kafka的Zookeeper地址
kafka-manager.zkhosts="127.0.0.1:2181"

# 监控程序要连接的Zookeeper
cmak.zkhosts="127.0.0.1:2181"
```

#### 三、创建Kafka-Manager启动脚本[vi /home/chiangfire/data-dev/kafka/cmak-3.0.0.5/bin/kafka-manager.sh]
```bash
#!/bin/sh
#
# 监控程序所在目录
KAFKA_MANAGER_HOME=/home/chiangfire/data-dev/kafka/cmak-3.0.0.5
EXEC=${KAFKA_MANAGER_HOME}/bin/cmak
P_COUNT=`ps -ef | grep cmak | grep -v grep | wc -l`
# 服务监听端口
PORT="-Dhttp.port=9093"

case "$1" in
    start)
        if [ $P_COUNT -gt 0 ]
        then
            echo "$P_COUNT Kafka-Manager exists, process is already running or crashed"
        else
            echo "Starting Kafka-Manager server..."
            nohup $KAFKA_MANAGER_HOME $PORT 1>/dev/null 2>&1 &
        fi
         ;;
    stop)
        if [ ! $P_COUNT -gt 0 ]
        then
            echo "Kafka-Manager does not exist, process is not running"
        else
            ${KAFKA_MANAGER_HOME}/bin/kafka-server-stop.sh
            echo "Kafka-Manager stopped"
        fi
        ;;
    *)
        echo "Please use start or stop as first argument"
        ;;
esac
```

#### 四、启动和停止Kafka-Manager监控
```bash
# 启动Kafka-Manager监控
$ kafka-manager.sh start
```

#### 五、添加Kafka集群到Kafka-Manager监控
![image](https://github.com/firechiang/hadoop-test/blob/master/kafka/images/monitor-add-001.png)
![image](https://github.com/firechiang/hadoop-test/blob/master/kafka/images/monitor-add-002.png)
![image](https://github.com/firechiang/hadoop-test/blob/master/kafka/images/monitor-add-003.png)
![image](https://github.com/firechiang/hadoop-test/blob/master/kafka/images/monitor-add-004.png)
![image](https://github.com/firechiang/hadoop-test/blob/master/kafka/images/monitor-add-005.png)

