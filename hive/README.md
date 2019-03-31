#### Hive（1，数据仓库）（2，解析器，编译器，优化器）（3，运行时，元数据存储在关系型数据库里面）
#### Hive架构
![image](https://github.com/firechiang/hadoop-test/blob/master/hive/image/hive-framework.jpg)
```bash
1，编译器将一个Hive SQL转换成操作符
2，操作符是Hive的最小的处理单元
3，每个操作符代表HDFS的一个操作或一个MapReduce作业
```
