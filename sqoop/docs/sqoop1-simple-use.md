#### [一、安装][1]
##### 1.1、下载安装（注意：运行Sqoop需要有HADOOP_HOME环境变量，因为它要加载Hadoop依赖，默认会从这个环境变量里面去找）
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

##### 1.5、如果使用MySql来存储元数据，下载Mysql驱动包到 sqoop/lib 目录
```bash
$ wget http://central.maven.org/maven2/mysql/mysql-connector-java/8.0.15/mysql-connector-java-8.0.15.jar
```
#### 二、验证Sqoop是否安装成功和帮助命令使用
```bash
$ sqoop-version                                  # 查看Sqoop版本
$ sqoop help                                     # 查看sqoop所有帮助
$ sqoop help import                              # 查看sqoop import帮助
```
#### [三、sqoop import命令使用，将MySql单表数据导入到HDFS][2]
#### [四、sqoop import-all-tables命令使用，将MySql所有表数据导入到HDFS][3]
#### [五、sqoop export命令使用，将HDFS数据导出到MySql表][4]


[1]: https://github.com/firechiang/hadoop-test/tree/master/sqoop/docs/sqoop1-simple-install.md
[2]: https://github.com/firechiang/hadoop-test/tree/master/sqoop/docs/sqoop-import-use.md
[3]: https://github.com/firechiang/hadoop-test/tree/master/sqoop/docs/sqoop-import-all-tables-use.md
[4]: https://github.com/firechiang/hadoop-test/tree/master/sqoop/docs/sqoop-export-use.md