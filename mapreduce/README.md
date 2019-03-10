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
3，Map的数量由切片来决定，切片由我们的程序来决定
4，一组（分组）数据对应一个Reduce（如果一组数据对多个Reduce也只会有一个Reduce执行，其它的Reduce放空），只有多组数据时才会有多个Reduce（比如：按性别分组统计男女各个总数量就会有两个Reduce同时并行）
（一组数据不能对应多个Reduce，这违背了原语）     
```

#### [一、单节点搭建][1]
#### [一、高可用搭建][2]

[1]: https://github.com/firechiang/hadoop-test/tree/master/mapreduce/docs/1-setup-single-node.md
[2]: https://github.com/firechiang/hadoop-test/tree/master/mapreduce/docs/2-setup-cluster-node.md