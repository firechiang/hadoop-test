#### 一、 创建mysql连接器
```bash
$ create link -connector generic-jdbc-connector            # 创建JDBC连接器（之后填写名称，用户名，密码，Driver等 信息，具体步骤如下）

Name: mysql

Database connection

Driver class: com.mysql.cj.jdbc.Driver
Connection String: jdbc:mysql://server004:3306/sqoop_remote?createDatabaseIfNotExist=true
Username: root
Password: *********
Fetch Size: 50
Connection Properties: 
There are currently 0 values in the map:
entry# protocol=tcp
There are currently 1 values in the map:
protocol = tcp
按回车
entry#

SQL Dialect

Identifier enclose:                                        # 输入空格再按回车，否则start job时会报“GENERIC_JDBC_CONNECTOR_0016:Can't fetch schema -" 类型错误
New link was successfully created with validation status OK and name mysql2

$ show link                                                # 查看我们创建的连接器是否成功
$ show link -all                                           # 查看我们创建的连接的详细信息
```

#### 二、 创建hdfs连接器
```bash
$ create link -connector hdfs-connector                    # 创建JDBC连接器（之后填写名称，用户名，密码，Driver等 信息，具体步骤如下）
Name: hdfs

HDFS cluster

URI: hdfs://mycluster
Conf directory: /home/sqoop-1.99.7-bin-hadoop200/conf      # 这个目录是Sqoop2服务端的配置目录（注意：这个目录一定要存在）
Additional configs:: 
There are currently 0 values in the map:
entry# 
按回车

$ show link                                                # 查看我们创建的连接器是否成功
$ show link -all                                           # 查看我们创建的连接的详细信息
```

#### 三、 创建Job任务（不给值的就直接回车即可）
```bash
$ create job -f "mysql" -t "hdfs"                          # 创建一个任务，流程是从"mysql"连接器（我们上面创建的那个）到"hdfs"连接器（我们上面创建的那个）

Name: mysql2hdfs                                           # 任务名称

Database source

Schema name: sqoop_remote                                  # musql数据库名
Table name: test                                           # mysql表
SQL statement:                                             # sql语句，暂时不写（写的话${CONDITIONS}必须加，如果没有双引号，反斜杠就不用加）
Column names:                                              # 数据列名，暂时不写
There are currently 0 values in the list:                  # 数据元素，暂时不写         
element# 
Partition column:                                          # 分区列，暂时不写
Partition column nullable:                                 # 可以为空的分区列，暂时不写
Boundary query:                                            # 边界查询，暂时不写

Incremental read

Check column:                                              # 检查列，暂时不写
Last value:                                                # 最后值，暂时不写

Target configuration

Override null value:                                       # 重写空值，暂时不写
Null value:                                                # 空值，暂时不写
File format: 
  0 : TEXT_FILE
  1 : SEQUENCE_FILE
  2 : PARQUET_FILE
Choose: 0                                                  # 文件格式，选0（文本格式）
Compression codec: 
  0 : NONE
  1 : DEFAULT
  2 : DEFLATE
  3 : GZIP
  4 : BZIP2
  5 : LZO
  6 : LZ4
  7 : SNAPPY
  8 : CUSTOM
Choose: 0                                                  # 编解码器，选0（没有）
Custom codec:                                              # 自定义编解码器，暂时不写
Output directory: /sqoop/mysql2hdfs                        # 文件输出到HDFS上的路径
Append mode:                                               # 文件追加模型，暂时不写

Throttling resources

Extractors: 1                                              # MapReduce Map的个数，最好指定数量，因为默认会起多个Map，相当耗资源
Loaders: 1                                                 # MapReduce Reduce的个数，，最好指定数量，因为默认会起多个Reduce，相当耗资源

Classpath configuration

Extra mapper jars:                                         # 额外的映射jar，暂时不写
There are currently 0 values in the list:
element# 
New job was successfully created with validation status OK  and name mysql2hdfs

$ show job                                                 # 查看我们刚刚创建的任务
$ show job -all                                            # 查看我们刚刚创建的任务的详细信息
```
#### 四、  到Mysql上去创建表和插入数据
```bash
create table test
(
  id int primary key auto_increment,
  msg varchar(100)
);

insert into test(msg)values('mmm'),('sdfsd'),('对方的'),('try突然'),('342342'),('士大夫'),('5433'),('犯得上发生'),('34'),('fd');
```

#### 五、  启动任务
```bash
# 如果最后报 Exception: Job Failed with status:3 说明资源不够，可以把Map和Reduce的数量调小一点，上面有设置，可以参考
$ start job -name mysql2hdfs -s                            # 启动并执行mysql2hdfs任务，加-s会显示执行进度
```