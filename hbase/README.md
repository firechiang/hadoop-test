#### [伪分布式搭建以及简单使用][2]
#### HBase简介，[架构][1]
```bash
1，Hadoop Database，是一个高可靠性、高性能、面向列、可伸缩、实时读写的分布式数据库
2，利用Hadoop HDFS作为其文件存储系统,利用Hadoop MapReduce来处理HBase中的海量数据,利用Zookeeper作为其分布式协同服务
3，主要用来存储非结构化和半结构化的松散数据（列存 NoSQL 数据库）
```

#### HBase数据模型
```bash
----------------------------------------------------------------------------------------------------------
|  Row Key  |  Time Stamp（时间戳）  |  Column Family（CF1）|  Column Family（CF2）|  Column Family（CF2） |
|-----------|---------------------- |--------------------- |----------------------|----------------------|
|           |           t8          |                      |   CF2:q1=value1      |    CF3:q3=value3     |
|           |-----------------------|----------------------|----------------------|----------------------|
| 11269339  |           t4          |                      |                      |                      |
|           |-----------------------|----------------------|----------------------|----------------------|
|           |           t2          |     CF1:q2=value1    |                      |                      |
|-----------|-----------------------|----------------------|----------------------|----------------------|
```
###### 一、Row Key
```bash
1,决定一行数据。
2,按照字典顺序排序的。
3,Row Key最大只能存储64k的字节数据（Row Key的长度）。
```
###### 二、Column Family列族  & qualifier列
```bash
1，HBase表中的每个列都归属于某个列族，列族必须作为表模式(schema)定义的一部分预先给出。如 create ‘test’, ‘course’（创建表时可以不给列，但一定要给列族）。
2，列名以列族作为前缀，每个“列族”都可以有多个列成员(column)；如course:math, course:english, 新的列族成员（列）可以随后按需、动态加入。
3，权限控制、存储以及调优都是在列族层面进行的。
4，HBase把同一列族里面的数据存储在同一目录下，由几个文件保存。
```

###### 三、Timestamp时间戳
```bash
1，在HBase每个cell存储单元对同一份数据有多个版本，根据唯一的时间戳来区分每个版本之间的差异，不同版本的数据按照时间倒序排序，最新的数据版本排在最前面。
2，时间戳的类型是 64位整型。
3，时间戳可以由HBase(在数据写入时自动)赋值，此时时间戳是精确到毫秒的当前系统时间。
4，时间戳也可以由客户显式赋值，如果应用程序要避免数据版本冲突，就必须自己生成具有唯一性的时间戳。
```

###### 四、Cell单元格
```bash
1，由行和列的坐标交叉决定。
2，单元格是有版本的。
3，单元格的内容是未解析的字节数组。
   3.1，由{row key， column( =<family> +<qualifier>)， version} 唯一确定的单元。
   3.2，cell中的数据是没有类型的，全部是字节码形式存贮。
```

###### 五、HLog(WAL log)数据
```bash
1，HLog文件就是一个普通的Hadoop Sequence File，Sequence File 的Key是HLogKey对象，HLogKey中记录了写入数据的归属信息，
       除了table和region名字外，同时还包括 sequence number和timestamp，timestamp是” 写入时间”，sequence number的起始值为0，
       或者是最近一次存入文件系统中sequence number。
2，HLog SequeceFile的Value是HBase的KeyValue对象，即对应HFile中的KeyValue
```

[1]: https://github.com/firechiang/hadoop-test/tree/master/hbase/docs/1-framework.md
[2]: https://github.com/firechiang/hadoop-test/tree/master/hbase/docs/2-setup-single-node.md
