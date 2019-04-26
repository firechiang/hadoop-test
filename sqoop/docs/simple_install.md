#### 一、下载安装（注意：运行Sqoop需要有HADOOP_HOME环境变量，因为它要加载Hadoop依赖，默认会从这个环境变量里面去找）
```bash
$ wget https://mirrors.tuna.tsinghua.edu.cn/apache/sqoop/1.99.7/sqoop-1.99.7-bin-hadoop200.tar.gz # 下载安装包
$ tar -zxvf sqoop-1.99.7-bin-hadoop200.tar.gz -C ../                                              # 解压到上层目录
```

#### 二、配置环境变量[vi ~/.bashrc]
```bash
export SQOOP_HOME=/home/sqoop-1.99.7-bin-hadoop200
PATH=$PATH:$SQOOP_HOME/bin                                                                        # linux以 : 号隔开，windows以 ; 号隔开

$ source ~/.bashrc                                                                                # （系统重读配置）在各个机器上执行使配置文件生效（实验：敲个beel然后按Tab键，如果补全了说明配置成功了）
$ echo $SQOOP_HOME                                                                                # 查看是否能获取到环境变量的值
```

#### 三、修改Hadoop HDFS集群配置文件[vi core-site.xml]
```bash
<property>
    <name>hadoop.proxyuser.sqoop2.hosts</name>
    <value>*</value>
</property>
<property>
    <name>hadoop.proxyuser.sqoop2.groups</name>
    <value>*</value>
</property>
```
#### 四、修改配置[vi sqoop-1.99.7-bin-hadoop200/conf/sqoop.properties]（以下配置项在文件里面都有，只需要修改值即可）
```bash
# 配置Hadoop配置文件目录
org.apache.sqoop.submission.engine.mapreduce.configuration.directory=/home/hadoop-3.2.0/etc/hadoop

# 配置Mysql来存储元数据（如果不使用Mysql存储元数据，可以不配置）
org.apache.sqoop.repository.jdbc.handler=org.apache.sqoop.repository.mysql.MySqlRepositoryHandler
org.apache.sqoop.repository.jdbc.url=jdbc:mysql://server004:3306/sqoop_remote?createDatabaseIfNotExist=true
org.apache.sqoop.repository.jdbc.driver=com.mysql.cj.jdbc.Driver
org.apache.sqoop.repository.jdbc.user=root
org.apache.sqoop.repository.jdbc.password=Jiang@123
```

#### 五、如果使用Mysql来存储元数据，修改Mysql sql_mode（如果不使用Mysql存储元数据，可以不配置）
```bash
$ select @@sql_mode;                 # 查询Mysql sql_mode看看是否有 ANSI_QUOTES

# 如果没有 ANSI_QUOTES，将原有的sql_mode查出来再加上ANSI_QUOTES，将其修改到Mysql配置文件，示例如下：
$ vi /etc/my.cnf
  sql_mode=ANSI_QUOTES,ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION
  
$ service mysqld restart             # 重启Mysql  
$ select @@sql_mode;                 # 再查询Mysql sql_mode看看是否有 ANSI_QUOTES
```

#### 六、如果使用Mysql来存储元数据，下载Mysql驱动包到 sqoop-1.99.7-bin-hadoop200/server/lib 目录（如果不使用Mysql存储元数据，可以不下载）
```bash
$ wget http://central.maven.org/maven2/mysql/mysql-connector-java/8.0.15/mysql-connector-java-8.0.15.jar
```
#### 七、验证Sqoop配置安装是否有问题（如果验证失败会有：Verification has failed, please check Server logs for further details 提示信息）
```bash
$ sqoop2-tool verify   # 检查配置，如果失败报fail，在这个命令执行的目录会生成一个@LOGDIR@目录，里面有个错误信息文件sqoop.log，打开看看是哪里错了。如果成功会有Verification was successful提示，且会自动创建名为SQOOP的数据库
```

#### 八、简单使用
```bash
$ sqoop2-server start                      # 启动 Sqoop 服务端（要停止的话使用：sqoop2-server stop）
$ sqoop2-shell                             # 进入命令行客户端（也可以使用：sqoop.sh client 命令）
$ show version                             # 显示 Sqoop 服务端版本
$ show version --all                       # 显示 Sqoop 服务端和客户端版本
$ show server --all                        # 显示服务器相关信息
$ show connector                           # 显示 Sqoop 所支持的连接类型
$ show link                                # 显示所有连接
$ show job                                 # 显示所有任务

$ set option --name verbose --value true   # 开启verbose，方便在出错时及时查错

$ :exit                                    # 退出客户端命令行
```
