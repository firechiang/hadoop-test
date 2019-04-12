#### [一、单用户搭建和基本操作][1]
#### [一、多用户搭建和基本操作][3]
#### [二、基本使用][2]
#### [三、权限控制][4]

#### Hive（1，数据仓库）（2，解析器，编译器，优化器）（3，运行时，元数据存储在关系型数据库里面）
![image](https://github.com/firechiang/hadoop-test/blob/master/hive/image/hive-framework.jpg)
```bash
1，编译器将一个Hive SQL转换成操作符
2，操作符是Hive的最小的处理单元
3，每个操作符代表HDFS的一个操作或一个MapReduce作业
```
#### Operator（操作符），是Hive定义的一个处理过程
```bash
1，Select（查询）
2，TableScan（区间扫描表数据）
3，Limit（限制输出记录）
4，File Output（文件输出）
```
#### Hive使用Antlr词法语法分析工具解析Hive SQL
```bash
1，Parser                  将Hive SQL转换成抽象语法树
2，Semantic Analyzer       将抽象语法树转成查询块
3，Logic Plan Generator    将查询块转换成逻辑查询计划
4，Logical Optimizer       重写逻辑查询计划
5，Physical Plan Generator 将逻辑计划转成物理计划
6，Physical Optimizer      选择最佳策略
```
[1]: https://github.com/firechiang/hadoop-test/tree/master/hive/docs/1-setup-single.md
[2]: https://github.com/firechiang/hadoop-test/tree/master/hive/docs/2-simple-use.md
[3]: https://github.com/firechiang/hadoop-test/tree/master/hive/docs/2-setup-multi.md
[4]: https://github.com/firechiang/hadoop-test/tree/master/hive/docs/3-setup-authorization.md
