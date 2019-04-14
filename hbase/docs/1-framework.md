#### HBase架构
![image](https://github.com/firechiang/hadoop-test/blob/master/hbase/image/hbase-001.png)
##### Client
```bash
1，包含访问HBase的接口并维护cache来加快对HBase的访问
```
##### Zookeeper
```bash
1，保证任何时候，集群中只有一个master
2，存贮所有Region的寻址入口。
3，实时监控Region server的上线和下线信息。并实时通知Master
4，存储HBase的schema和table元数据
```
##### Master
```bash
1，为Region server分配region
2，负责Region server的负载均衡
3，发现失效的Region server并重新分配其上的region
4，管理用户对table的增删改操作
```
##### RegionServer
![image](https://github.com/firechiang/hadoop-test/blob/master/hbase/image/3.region-server.png)

```bash
1，Region server维护region，处理对这些region的IO请求
2，Region server负责切分在运行过程中变得过大的region
```

##### HRegion（HRegion是HBase中分布式存储和负载均衡的最小单元。最小单元就表示不同的HRegion可以分布在不同的 HRegion server上）
![image](https://github.com/firechiang/hadoop-test/blob/master/hbase/image/2-region.jpg)

```bash
1，HRegion由一个或者多个Store组成，每个Store保存一个Columns Family（列族）。
2，每个Strore又由一个memStore和0至多个StoreFile组成。如图：StoreFile以HFile格式保存在HDFS上。
3，HBase自动把表水平划分成多个区域(region)，每个region会保存一个表里面某段连续的数据
4，每个表一开始只有一个region，随着数据不断插入表，region不断增大，当增大到一个阀值的时候，region就会等分会两个新的region（裂变）
5，当table中的行数据不断增多，就会有越来越多的region。这样一张完整的表被保存在多个Regionserver 上。
```
##### Memstore 与 StoreFile
```bash
1，一个region由多个store组成，一个store对应一个CF（列族）
2，store包括位于内存中的memstore和位于磁盘的storefile写操作先写入memstore，当memstore中的数据达到某个阈值，hregionserver会启动flashcache进程写入storefile，每次写入形成单独的一个storefile
3，当storefile文件的数量增长到一定阈值后，系统会进行合并（minor、major compaction），在合并过程中会进行版本合并和删除工作（majar），形成更大的storefile
4，当一个region所有storefile的大小和数量超过一定阈值后，会把当前的region分割为两个，并由hmaster分配到相应的regionserver服务器，实现负载均衡
5，客户端检索数据，先在memstore找，找不到再找storefile
```