#### 一、安装
##### 1.1、下载解压安装包
```bash
$ wget http://mirror.bit.edu.cn/apache/sqoop/1.4.7/sqoop-1.4.7.bin__hadoop-2.6.0.tar.gz    # 下载安装包
$ tar -zxvf sqoop-1.4.7.bin__hadoop-2.6.0.tar.gz -C ../                                    # 解压到上层目录
```

##### 1.2、配置环境变量[vi ~/.bashrc]
```bash
export SQOOP_HOME=/home/sqoop-1.4.7.bin__hadoop-2.6.0
PATH=$PATH:$SQOOP_HOME/bin                                                                  # linux以 : 号隔开，windows以 ; 号隔开

$ source ~/.bashrc                                                                          # （系统重读配置）在各个机器上执行使配置文件生效（实验：敲个beel然后按Tab键，如果补全了说明配置成功了）
$ echo $SQOOP_HOME                                                                          # 查看是否能获取到环境变量的值
```


##### 1.3、生成sqoop-env.sh配置文件（到sqoop/conf目录）
```bash
$ cp sqoop-env-template.sh sqoop-env.sh          # 复制sqoop-env-template.sh文件，生成新的sqoop-env.sh文件
```

##### 1.4、修改配置文件[vi conf/sqoop-env.sh]
```bash
export HADOOP_COMMON_HOME=/home/hadoop-3.1.2     # 配置Hadoop所在目录
export HADOOP_MAPRED_HOME=/home/hadoop-3.1.2     # 配置Hadoop所在目录
```

##### 1.5、下载Mysql驱动包到 sqoop/lib 目录
```bash
$ wget http://central.maven.org/maven2/mysql/mysql-connector-java/8.0.15/mysql-connector-java-8.0.15.jar
```

##### 1.6、解决创建job报java.lang.NoClassDefFoundError: org/json/JSONObject错误（解决方案：复制json-20180813.jar包到Sqoop lib目录）
```bash
$ wget http://central.maven.org/maven2/org/json/json/20180813/json-20180813.jar
```

##### 1.7、解决Hive导入报java.lang.ClassNotFoundException: org.apache.hadoop.hive.conf.HiveConf错误（解决方案：复制hive-common-3.1.1.jar包到Sqoop lib目录）
```bash
cp /home/apache-hive-3.1.1-bin/lib/hive-common-3.1.1.jar /home/sqoop-1.4.7.bin__hadoop-2.6.0/lib
```

##### 1.8、解决Sqoop-1.4.7不支持HBase-2.x问题（解决方案：下载hbase-1.4.9，将其所有依赖拷贝到sqoop-1.4.7 lib目录）
```bash
$ cd /home/sqoop-1.4.7.bin__hadoop-2.6.0/lib     # 到Sqoop lib目录
$ mkdir hbase                                    # 创建hbase目录
$ cd hbase                                       # 进入hbase目录
$ wget http://mirrors.tuna.tsinghua.edu.cn/apache/hbase/1.4.9/hbase-1.4.9-bin.tar.gz
$ tar -zxvf hbase-1.4.9-bin.tar.gz               # 解压hbase-1.4.9到当前目录
$ cd hbase-1.4.9/lib                             # 进入hbase-1.4.9 lib目录
$ cp *.* /home/sqoop-1.4.7.bin__hadoop-2.6.0/lib # 复制hbase所有依赖包到sqoop-1.4.7 lib目录（已存在的包就替换）
```

#### 二、验证Sqoop是否安装成功和帮助命令使用
```bash
$ sqoop-version                                  # 查看Sqoop版本
$ sqoop help                                     # 查看sqoop所有帮助
$ sqoop help import                              # 查看sqoop import(导入)帮助
$ sqoop help export                              # 查看sqoop import(导出)帮助
$ sqoop help job                                 # 查看sqoop job(任务)帮助
```
#### [三、sqoop import命令使用，将MySql单表数据导入到HDFS(包含增量导入)][2]
#### [四、sqoop import-all-tables命令使用，将MySql所有表数据导入到HDFS][3]
#### [五、sqoop export命令使用，将HDFS数据导出到MySql表][4]
#### [六、sqoop import --hive-import命令使用，将MySql数据导入到Hive表][5]
#### [七、sqoop import 命令使用，将MySql数据导入到HBase表][6]
#### [八、sqoop job 命令使用，定义导入导出任务][7]

[2]: https://github.com/firechiang/hadoop-test/tree/master/sqoop/docs/sqoop-import-use.md
[3]: https://github.com/firechiang/hadoop-test/tree/master/sqoop/docs/sqoop-import-all-tables-use.md
[4]: https://github.com/firechiang/hadoop-test/tree/master/sqoop/docs/sqoop-export-use.md
[5]: https://github.com/firechiang/hadoop-test/tree/master/sqoop/docs/hive-import-use.md
[6]: https://github.com/firechiang/hadoop-test/tree/master/sqoop/docs/hbase-import-use.md
[7]: https://github.com/firechiang/hadoop-test/tree/master/sqoop/docs/sqoop-job-use.md