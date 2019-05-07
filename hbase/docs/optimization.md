#### 一、表的设计
##### 1.1 Pre-Creating Regions（预分区）
```bash
默认情况下，在创建HBase表的时候会自动创建一个region分区，当导入数据的时候，所有的HBase客户端都向这一个region写数据，直到这个region足够大了才进行切分。
要想加快批量写入速度可以预先创建一些空的regions，这样当数据写入HBase时，会按照region分区情况，在集群内做数据的负载均衡。
```
##### 1.2 Row Key可以是任意字符串，最大长度64KB，实际应用中一般为10~100bytes，存为byte[]字节数组，一般设计成定长的，越小越好
##### 1.3 Row Key按照字典序存储，设计Row Key时，要充分利用排序特点，将经常一起读取的数据存储到一块，将最近可能会被访问的数据放到一块。
```bash
举例：如果最近写入HBase表中的数据是最可能被访问的，可以考虑将时间戳作为row key的一部分，由于是字典序排序，所以可以使用Long.MAX_VALUE - timestamp作为row key，这样能保证新写入的数据在读取时可以被快速命中。
```

##### 1.4 Column Family（列族）
```bash
不要在一张表里定义太多的column family。目前Hbase并不能很好的处理超过2~3个column family的表。因为某个column family在flush的时候，它邻近的column family也会因关联效应被触发flush，最终导致系统产生更多的I/O。感兴趣的同学可以对自己的HBase集群进行实际测试，从得到的测试结果数据验证一下。
```
##### 1.5 In Memory
```bash
创建表的时候，可以通过HColumnDescriptor.setInMemory(true)将表放到RegionServer的缓存中，保证在读取的时候被cache命中。
```
##### 1.6 Max Version
```bash
创建表的时候，可以通过HColumnDescriptor.setMaxVersions(int maxVersions)设置表中数据的最大版本，如果只需要保存最新版本的数据，那么可以设置setMaxVersions(1)。
```
##### 1.7 Time To Live
```bash
创建表的时候，可以通过HColumnDescriptor.setTimeToLive(int timeToLive)设置表中数据的存储生命期，过期数据将自动被删除，例如如果只需要存储最近两天的数据，那么可以设置setTimeToLive(2 * 24 * 60 * 60)。
```
##### 1.8 Compact & Split
```bash
在HBase中，数据在更新时首先写入WAL 日志(HLog)和内存(MemStore)中，MemStore中的数据是排序的，当MemStore累计到一定阈值时，就会创建一个新的MemStore，并且将老的MemStore添加到flush队列，由单独的线程flush到磁盘上，成为一个StoreFile。于此同时，系统会在zookeeper中记录一个redo point，表示这个时刻之前的变更已经持久化了(minor compact)。

StoreFile是只读的，一旦创建后就不可以再修改。因此Hbase的更新其实是不断追加的操作。当一个Store中的StoreFile达到一定的阈值后，就会进行一次合并(major compact)，将对同一个key的修改合并到一起，形成一个大的StoreFile，当StoreFile的大小达到一定阈值后，又会对 StoreFile进行分割(split)，等分为两个StoreFile。

由于对表的更新是不断追加的，处理读请求时，需要访问Store中全部的StoreFile和MemStore，将它们按照row key进行合并，由于StoreFile和MemStore都是经过排序的，并且StoreFile带有内存中索引，通常合并过程还是比较快的。

实际应用中，可以考虑必要时手动进行major compact，将同一个row key的修改进行合并形成一个大的StoreFile。同时，可以将StoreFile设置大些，减少split的发生。

hbase为了防止小文件（被刷到磁盘的menstore）过多，以保证保证查询效率，hbase需要在必要的时候将这些小的store file合并成相对较大的store file，这个过程就称之为compaction。在hbase中，主要存在两种类型的compaction：minor  compaction和major compaction。
minor compaction:的是较小、很少文件的合并。
major compaction 的功能是将所有的store file合并成一个，触发major compaction的可能条件有：major_compact 命令、majorCompact() API、region server自动运行（相关参数：hbase.hregion.majoucompaction 默认为24 小时、hbase.hregion.majorcompaction.jetter 默认值为0.2 防止region server 在同一时间进行major compaction）。
hbase.hregion.majorcompaction.jetter参数的作用是：对参数hbase.hregion.majoucompaction 规定的值起到浮动的作用，假如两个参数都为默认值24和0,2，那么major compact最终使用的数值为：19.2~28.8 这个范围。

1、	关闭自动major compaction
2、	手动编程major compaction
Timer类，contab
minor compaction的运行机制要复杂一些，它由一下几个参数共同决定：
hbase.hstore.compaction.min :默认值为 3，表示至少需要三个满足条件的store file时，minor compaction才会启动
hbase.hstore.compaction.max 默认值为10，表示一次minor compaction中最多选取10个store file
hbase.hstore.compaction.min.size 表示文件大小小于该值的store file 一定会加入到minor compaction的store file中
hbase.hstore.compaction.max.size 表示文件大小大于该值的store file 一定会被minor compaction排除
hbase.hstore.compaction.ratio 将store file 按照文件年龄排序（older to younger），minor compaction总是从older store file开始选择
```

#### 二、写表操作
##### 2.1，多Table并发写，可以打开多个连接，创建多个Table客户端用于并发写操作，提高写数据的吞吐量
##### 2.2 HTable参数设置
###### 2.2.1，Write Buffer（Table客户端的写buffer大小，如果新设置的buffer小于当前写buffer中的数据时，buffer将会被flush到服务端）
```bash
hbase.client.write.buffer：默认为2M，写缓存大小，推荐设置为5M，单位是字节，当然越大占用的内存越多。
```
###### 2.2.3，WAL Flag
```bash
在HBae中，客户端向集群中的RegionServer提交数据时（Put/Delete操作），首先会先写WAL（Write Ahead Log）日志（即HLog，一个RegionServer上的所有Region共享一个HLog），只有当WAL日志写成功后，再接着写MemStore，然后客户端被通知提交数据成功；如果写WAL日志失败，客户端则被通知提交失败。这样做的好处是可以做到RegionServer宕机后的数据恢复。
因此，对于相对不太重要的数据，可以在Put/Delete操作时，通过调用Put.setDurability(Durability.SKIP_WAL)或Delete.setDurability(Durability.SKIP_WAL)函数，放弃写WAL日志，从而提高数据写入的性能。
值得注意的是：谨慎选择关闭WAL日志，因为这样的话，一旦RegionServer宕机，Put/Delete的数据将会无法根据WAL日志进行恢复。
```

##### 2.3 批量写
```bash
通过调用Table.put(Put)方法可以将一个指定的row key记录写入HBase，同样HBase提供了另一个方法：通过调用HTable.put(List<Put>)方法可以将指定的row key列表，批量写入多行记录，这样做的好处是批量执行，只需要一次网络I/O开销，这对于对数据实时性要求高，网络传输RTT高的情景下可能带来明显的性能提升。
```
#### 三、读表操作
##### 3.1 多HTable并发读，创建多个HTable客户端，每个读线程负责通过HTable对象进行get操作，提高读数据的吞吐量
##### 3.2 HTable参数设置
###### 3.2.1 Scanner Caching
```bash
hbase.client.scanner.caching配置项可以设置HBase scanner一次从服务端抓取的数据条数，默认情况下一次一条。通过将其设置成一个合理的值，可以减少scan过程中next()的时间开销，代价是scanner需要通过客户端的内存来维持这些被cache的行记录。
有两个地方可以进行配置：
  1，在HBase的conf配置文件中进行配置；
  3，通过调用Scan.setCaching(int caching)进行配置
```

###### 3.2.2 Scan Attribute Selection（scan时指定需要的Column Family，可以减少网络传输数据量，否则默认scan操作会返回整行所有Column Family的数据。）
###### 3.2.3 Close ResultScanner（通过scan取完数据后，记得要关闭ResultScanner，否则RegionServer可能会出现问题（对应的Server资源无法释放））

##### 3.3 批量读
```bash
通过调用HTable.get(Get)方法可以根据一个指定的row key获取一行记录，同样HBase提供了另一个方法：通过调用HTable.get(List<Get>)方法可以根据一个指定的row key列表，批量获取多行记录，这样做的好处是批量执行，只需要一次网络I/O开销，这对于对数据实时性要求高而且网络传输RTT高的情景下可能带来明显的性能提升。
```
##### 3.4 缓存查询结果
```bash
对于频繁查询HBase的应用场景，可以考虑在应用程序中做缓存，当有新的查询请求时，首先在缓存中查找，如果存在则直接返回，不再查询HBase；否则对HBase发起读请求查询，然后在应用程序中将查询结果缓存起来。至于缓存的替换策略，可以考虑LRU等常用的策略。
```

##### 3.5 Blockcache
```bash
HBase上Regionserver的内存分为两个部分，一部分作为Memstore，主要用来写；另外一部分作为BlockCache，主要用于读。
写请求会先写入Memstore，Regionserver会给每个region提供一个Memstore，当Memstore满64MB以后，会启动 flush刷新到磁盘。当Memstore的总大小超过限制时（heapsize * hbase.regionserver.global.memstore.upperLimit * 0.9），会强行启动flush进程，从最大的Memstore开始flush直到低于限制。
读请求先到Memstore中查数据，查不到就到BlockCache中查，再查不到就会到磁盘上读，并把读的结果放入BlockCache。由于BlockCache采用的是LRU策略，因此BlockCache达到上限(heapsize * hfile.block.cache.size * 0.85)后，会启动淘汰机制，淘汰掉最老的一批数据。
一个Regionserver上有一个BlockCache和N个Memstore，它们的大小之和不能大于等于heapsize * 0.8，否则HBase不能启动。默认BlockCache为0.2，而Memstore为0.4。对于注重读响应时间的系统，可以将 BlockCache设大些，比如设置BlockCache=0.4，Memstore=0.39，以加大缓存的命中率。
有关BlockCache机制，请参考这里：HBase的Block cache，HBase的blockcache机制，hbase中的缓存的计算与使用。
```

#### 3.4 HTable和HTablePool使用注意事项
##### 3.4.1 规避HTable对象的创建开销
```bash
因为客户端创建HTable对象后，需要进行一系列的操作：检查.META.表确认指定名称的HBase表是否存在，表是否有效等等，整个时间开销比较重，
可能会耗时几秒钟之长，因此最好在程序启动时一次性创建完成需要的HTable对象，如果使用Java API，一般来说是在构造函数中进行创建，程序启动后直接重用
```
##### 3.4.2 HTable对象不是线程安全的
```bash
HTable对象对于客户端读写数据来说不是线程安全的，因此多线程时，要为每个线程单独创建复用一个HTable对象，不同对象间不要共享HTable对象使用，特别是在客户端auto flash被置为false时，由于存在本地write buffer，可能导致数据不一致。
```

##### 3.4.3 HTable对象之间共享Configuration好处在于：
```bash
1，共享ZooKeeper的连接：每个客户端需要与ZooKeeper建立连接，查询用户的table regions位置，这些信息可以在连接建立后缓存起来共享使用。
2，共享公共的资源：客户端需要通过ZooKeeper查找-ROOT-和.META.表，这个需要网络传输开销，客户端缓存这些公共资源后能够减少后续的网络传输开销，加快查找过程速度。
备注：即使是高负载的多线程程序，也并没有发现因为共享Configuration而导致的性能问题；如果你的实际情况中不是如此，那么可以尝试不共享Configuration。
```
##### 3.4.4 HTablePool可以解决HTable存在的线程不安全问题，同时通过维护固定数量的HTable对象，能够在程序运行期间复用这些HTable资源对象
```bash
1，HTablePool可以自动创建HTable对象，而且对客户端来说使用上是完全透明的，可以避免多线程间数据并发修改问题。
2，HTablePool中的HTable对象之间是公用Configuration连接的，能够可以减少网络开销。
```
