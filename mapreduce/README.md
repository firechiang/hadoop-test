### 基础理论
##### MapReduce执行顺序  
![image](https://github.com/firechiang/hadoop-test/blob/master/mapreduce/image/1-map-reduce.png)
```bash
先Split（切片），再Map计算，再排序（Sort），再分组（partition）（相同的key为一组），再Reduce
```
##### MapReduce语义（原语：相同的key为一组，调用一次reduce方法，方法内迭代这一组数据进行计算）
```bash
1，HDFS是Map的输入，Map是Reduce的输入，Reduce再输出到HDFS
2，一个Split（切片：默认是HDFS的Block块，但是可以自定义再切块以增加Map加快执行效率）对应一个Map程序
3，Map的数量由切片来决定，切片由我们的程序来决定，切片包含：文件，起始位置，结束位置，Block所在Host，缓存所在Host 等信息
4，一组（分组）数据对应一个Reduce（如果一组数据对多个Reduce也只会有一个Reduce执行，其它的Reduce放空），只有多组数据时才会有多个Reduce（比如：按性别分组统计男女各个总数量就会有两个Reduce同时并行）
（一组数据不能对应多个Reduce，这违背了原语）     
```

##### Shuffler<洗牌>  内部执行流程
![image](https://github.com/firechiang/hadoop-test/blob/master/mapreduce/image/2-map-reduce.jpeg)
###### 一、Map阶段
```bash
1.1，首先一个Split（切片）对应一个Map，每一条记录调用一次Map，Map的输出映射成Key和Value
1.2，Map映射成Key和Value之后，再经过我们自己的算法，经过算法之后映射成Key，Value，Partition（分区）
1.3，经过算法之后每一条记录都会有Partition（所在分区），然后将数据放到内存（Buffer In Memory）
1.4，当数据量到达一定大小之后，会将数据先按Partition（分区）做一个（Sort）排序（同一分区放到一起），再按Key排序，最后将这些数据Flash到磁盘成一个小文件
1.5，Map一直输出会生成很多小文件， 这些个小文件都有一个特征就是：内部有序，外部无序。然后再进行归并，成为一个文件
1.6，归并的过程会将同一分区的数据放到一起，而且Key有序。（如果有多个Map就会有多个归并文件）
```
###### 二、Reduce阶段
```bash
2.1，Reduce拉取所有属于自己分区的文件，再进行一次归并，再进行Reduce（计算），最后输出
```
##### Shuffler典型案列
![image](https://github.com/firechiang/hadoop-test/blob/master/mapreduce/image/3-map-reduce.jpg)
