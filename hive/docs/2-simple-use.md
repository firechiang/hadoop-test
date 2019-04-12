#### 一、Hive表数据类型
```bash
# 基础类型
tinyint
smallint
int
bigint
boolean
float
double
string
binary
timestamp
decimal
date
varchar
char

# 扩展类型  
array
map
struct
union
```

#### 二、数据库的基础操作
```bash
$ create database test;            # 创建数据库（test）
$ use test;                        # 进入某个数据（test）
$ drop database test               # 删除数据库（test）
```

#### 三、内部表，需要指定数据库，删除时会删除数据（执行建表语句时请将注释删除）
```bash
create table person(
    id int,                        # int类型
    name string,                   # string 类型
    links array<string>,           # 数组类型
    address map<string,string>     # map类型
)
row format delimited               # 因为我们要导入特定文件的数据，所以我们要定义格式规则，用来匹配解析文件数据，用于导入数据（具体规则如下，数据文件为：person.txt）
fields terminated by ','           # 属性通过什么隔开（就是id,name,links,address字段的数据用什么隔开）
collection items terminated by '-' # 数组用什么隔开（就是links的数据用什么隔开）
map keys terminated by ':'         # map用什么隔开（就是address的数据用什么隔开）
lines terminated by '\n'           # 每一行数据用什么隔开，默认也是\n，所以可以不写
;
```

#### 四、数据表基本操作
```bash
$ desc person                      # 查看表基础信息（信息量较少）
$ desc formatted person            # 查看表详细信息（信息量较多）
```

#### 五、导入文件数据到Hive（注意要进入具体的数据库）
```bash
$ load data LOCAL inpath '/home/hive-test-data/person.txt' INTO TABLE person;   # LOCAL 表示本地文件
```

#### 六、查询数据

```bash
$ select * from person;            # 查询数据
```

#### 七、外部表，外部表可以不指定数据库，且删除表时不删除数据（执行建表语句时请将注释删除）
```bash
create external table person(          # 加入 external 关键字标识外部表
    id int,                            # int类型
    name string,                       # string 类型
    links array<string>,               # 数组类型
    address map<string,string>         # map类型
)
row format delimited                   # 因为我们要导入特定文件的数据，所以我们要定义格式规则，用来匹配解析文件数据，用于导入数据（具体规则如下，数据文件为：person.txt）
fields terminated by ','               # 属性通过什么隔开（就是id,name,links,address字段的数据用什么隔开）
collection items terminated by '-'     # 数组用什么隔开（就是links的数据用什么隔开）
map keys terminated by ':'             # map用什么隔开（就是address的数据用什么隔开）
lines terminated by '\n'               # 每一行数据用什么隔开，默认也是\n，所以可以不写
location '/user/hive_remote/external'; # 该表文件存储所在目录（目录会自动创建）
```

#### 八、Hive表分区，必须在表定义时指定对应的partition字段（分区就是目录）（可以用多个字段分区）（分区字段不能在表的字段当中）
```bash
create table person1(
    id int,                        # int类型
    name string,                   # string 类型
    links array<string>,           # 数组类型
    address map<string,string>     # map类型
)
partitioned by (age int)           # 指定分区字段为age（注意：分区字段不能在表的字段当中）
row format delimited               # 因为我们要导入特定文件的数据，所以我们要定义格式规则，用来匹配解析文件数据，用于导入数据（具体规则如下，数据文件为：person.txt）
fields terminated by ','           # 属性通过什么隔开（就是id,name,links,address字段的数据用什么隔开）
collection items terminated by '-' # 数组用什么隔开（就是links的数据用什么隔开）
map keys terminated by ':'         # map用什么隔开（就是address的数据用什么隔开）
lines terminated by '\n'           # 每一行数据用什么隔开，默认也是\n，所以可以不写
;
```

#### 九、导入分区表数据到Hive（将数据导入到age=10的分区）（注意要进入具体的数据库）
```bash
$ load data LOCAL inpath '/home/hive-test-data/person.txt' INTO TABLE person1 partition (age =10);   # LOCAL 表示本地文件
```

#### 十、添加分区（注意：只能在已有的分区字段上添加，就是在建表的时候指定了哪些分区字段，就用哪些字段）
```bash
$ alter table person1 add partition(age=12);   # 添加age=12的分区
```

#### 十一、删除分区（删除分区的同时也会删除分区里面的数据）
```bash
$ alter table person1 drop partition(age=12);  # 删除age=12的分区
```

#### 十二、复制表数据，自动转成MapReduce操作（单表插入）
```bash
from person insert overwrite table person2 select id,name,links,address;   # 将person表id,name,links,address字段的数据全部插入到person2表
```

#### 十三、复制表数据，自动转成MapReduce操作（多表插入，同时进行）
```bash
from person 
insert overwrite table person2 select id,name,links,address;   # 将person表id,name,links,address字段的数据全部插入到person2表
insert overwrite table person3 select id,name,links,address;   # 将person表id,name,links,address字段的数据全部插入到person3表
```

#### 十四、以正则表达式的方式读取文件数据（如果与表达式不匹配的数据，我们在查询的时候会直接返回NULL）
```bash
# 建表
create table log (
    host string,
    identity string,
    t_user string,
    time_str string,
    request string,
    referer string,
    agent string
)
row format serde 'org.apache.hadoop.hive.serde2.RegexSerDe'
with serdeproperties (
    "input.regex" = "([^ ]*) ([^ ]*) ([^ ]*) \\[(.*)\\] \"(.*)\" (-|[0-9]*) (-|[0-9]*)"
)
stored as textfile;


$ load data LOCAL inpath '/home/hive-test-data/log.txt' INTO TABLE log;   # 导入数据
$ select * from log;                                                      # 查询数据

```

#### 十五、struct 数据类型表示例
```bash
create table student(
    id int,
    info struct<name:string,age:int>
)
row format delimited
fields terminated by ','
collection items terminated by ':';

# 导入数据
$ load data LOCAL inpath '/home/hive-test-data/student.txt' INTO TABLE student;
```

#### 十六、基站掉话率示例
```bash
# 数据表
create table call_monitor(
    record_time string,
    imei string,
    cell string,
    ph_num string,
    call_num string,
    drop_num int,
    duration int,
    drop_rate double,
    net_type string,
    erl string
)
row format delimited
fields terminated by ',';


# 结果表
create table call_monitor_value(
    imei string,
    drop_num int,
    duration int,
    drop_rate double
)
row format delimited
fields terminated by ',';


# 导入数据
$ load data LOCAL inpath '/home/hive-test-data/call_monitor.csv' INTO TABLE call_monitor;


# 查询数据，并将查询结果插入 call_monitor_value 表
from call_monitor cm
insert into call_monitor_value
select cm.imei,sum(cm.drop_num),sum(cm.duration),(sum(cm.drop_num) /sum(cm.duration)) a3 group by cm.imei order by a3 desc;

# 查询前十条结果数据
select * from call_monitor_value limit 10;
```

#### 十七、Word Count示例
```bash
# 数据表
create table wc(
    word string
);

# 结果表
create table wc_value(
    word string,
    count int
);

# 导入数据
$ load data LOCAL inpath '/home/hive-test-data/wc.txt' INTO TABLE wc;

# 查询数据，并将查询结果插入 wc_value 表（这里用到了子查询）
from (select explode(split(word,' ')) ww from wc) aa
insert into wc_value
select ww,count(ww) group by ww;

# 查询结果数据
select * from wc_value;
```

#### 十八、复制表结构
```bash
$ create table person5 like person;                                       # 新建表 person5并将person表结构复制过来（就是新表person5和旧表person一模一样，这个不复制表数据）
```

#### 十九、Hive参数使用
##### 19.1，修改hive-site.xml文件
##### 19.2，hive cli（客户端）启动时，通过--hiveconf key=value的方式设置
##### 19.3，进入客户端之后，通过set命令
##### 19.4，在当前用户目录（比如: /tmp/root）下创建 .hiverc 文件，然后在里面写配置即可，hive启动时会自动加载该配置
```bash
# 参数设置测试（使用查询语句时显示数据表头）
$ set hive.cli.print.header = true                                        # set命令，这个时临时设置，退出后自动还原
$ hive --hiveconf hive.cli.print.header=true                              # hive客户端启动时设置，这个设置是永久的（等于号两边不能有空格）
$ set hive.exec.dynamic.partition                                         # 查看hive.exec.dynamic.partition参数的值
```

#### 二十、Hive表动态分区（注意查看插入数据的HSQL，主要时最后那句（按 age 分区）：distribute by age）
```bash
# 设置参数（这些参数一般可以不配置，看情况而定）
set hive.exec.dynamic.partition=true                                      # 开启动态分区（默认true）
set hive.exec.dynamic.partition.mode=nostrict                             # 默认值：strict（至少有一个分区是静态分区（就是至少有一个已存在的分区目录）），严格和非严格模式
set hive.exec.max.dynamic.partitions=1000                                 # 所有的mr执行节点上，共允许创建的动态分区最大数（默认：1000）
set hive.exec.max.dynamic.partitions.pernode=100                          # 每一个mr执行节点上，允许创建的动态分区最大数（默认：100）
set hive.exec.max.created.files=100000                                    # 所有mr job允许创建的文件的最大数（默认：100000）

# 创建表
create table person3(
    id int,
    name string
)
partitioned by (age int) 
row format delimited      
fields terminated by ',';

# 查询数据，将其插入到 person3 表
from person1 
insert into person3 
select id,name,age distribute by age;
```

#### 二十一、Hive表分桶（就是分文件）（适应场景：数据抽样（sampling），map-join，一般都是新建一张分桶表，将数据倒过来，再抽样）（其它应用场景最好不要用，影响效率）
##### 20.1，分桶是对值取哈希的方式，将不同数据放到不同文件中存储
##### 20.2，对于hive中每一个表的分区都可以进一步进行分桶
##### 20.3，由列的哈希值除以桶的个数来决定每条数据划分在哪个桶中（文件中）
```bash
# 设置参数
# 开启分桶，mr运行时会根据bucket的个数自动分配ReduceTask个数（用户可以通过 mapred.reduce.tasks 自己设置reduce个数，但分桶时不推荐使用）
# 注意：一次作业产生的桶（文件数）和reduce个数一致
set hive.enforce.bucketing=true

# 创建分桶表（执行建表语句时注意删除注释）
create table person4(
    id int,
    name string,
    age int
)
clustered by (id) into 4 buckets        # 以id分桶，桶的总个数为4个（表示会有4个文件）
row format delimited      
fields terminated by ',';


# 查询数据，将其插入到 person4 表
from person3
insert into person4 
select id,name,age;


# 桶表抽样查询说明（x表示从哪个bucket（桶）开始抽样，y必须为该表总bucket（桶）的倍数或因子（能整除总bucket（桶）数））。
# 当表总bucket（桶）数为32（就是32个文件），x=2，y=4，每个文件抽取8（32/y=4）份数据，分别是 2，6，10，14，18，22，26，30  桶里面的数据，从x（x=2）开始，每次递增y（y=4）
select * from person4 tablesample(bucket x out of y)

# 桶表抽样查询
$ select * from person4 tablesample(bucket 1 out of 2);
```

#### 二十二、函数使用（自定义函数请看代码udf包下；自带函数很多，关系型数据库函数，Hive基本都有）
```bash
$ select explode(links) from person;                                      # explode函数将数据以列的方式输出
```

#### 二十三、[自定义函数（UDF）][1]

#### 二十四、Hive Lateral View（用于和UDTF函数（explode，split）结合使用）
```bash
# 具体使用场景
1，首先通过UDTF函数数组数据拆分成多行，再将多行结果组合成一个支持别名的虚拟表
2，主要解决在 select 使用UDTF做查询过程中，查询只能包含单个UDTF，不能包含其它字段，以及多个UDTF的问题

# explode函数使用
select explode(links) from person;                                        # explode函数将数组数据拆分成多行输出（links字段是数组格式的数据）

# Lateral View使用基础如下：（将person表里面的links数据（数组数据）打成lateral view，然后再count里面的总数量）
select count(a1) from person p
lateral view explode(links) p as a1
```

#### 二十五、Hive 视图
```bash
# 特点
1，不支持物化视图
2，只能查询数据，不能修改数据
3，视图的创建，只是保存一份元数据，查询视图时才执行对应的子查询
4，view定义中若包含了order by/limit语句，当查询试图时也进行order by/limit语句操作，view当中定义的优先级更高
5，view支持迭代视图

# 使用
create view person_view as select id,name,links,address from person;      # 创建视图 person_view，查询语句是 select id,name,links,address from person
show tables;                                                              # 查看视图是否创建成功
select * from person_view;                                                # 查询视图数据
drop view person_view;                                                    # 删除视图
```

#### 二十五、Hive 索引
```bash
# 为person表，字段id创建索引名字叫 person_id（as表示指定索引器；in table表示索引的数据放在哪张表里面，若不指定默认在default_ person_person_id__表里面，索引数据表都会自动生成）
create index person_id on table person1(id) as 'org.apache.hadoop.hive.ql.index.compact.CompactIndexHandler' 
with deferred rebuild
in table person_id_index;

# 为person表的person_id索引，建立索引数据，以加快我们的数据查询速度（如果没有这个操作，我们的索引是没有数据的，也相当于没有索引）
alter index person_id on person rebuild;

# 删除person表的person_id索引
drop index person_id on person;
```

#### 二十六、Hive 脚本命令使用（这些命令也可正常使用）
```bash
# 命令使用
$ hive -e 'use test; select * from person;'          # 连接hive，先进入test数据库，再 查询    person 所有数据，然后退出hive
$ hive -e 'use test; select * from person;' > aaa    # 将结果数据保存到当前目录 aaa 文件当中，然后退出hive

# 定义文件aaa，里面的内容是： use test; select * from person;    
$ vi aaa                                             
# 执行上面定义的脚本文件（-f：执行完成以后退出hive客服端，-i：不退出）；-i 不退出客户端之后，还可以使用 source aaa;命令再次执行这个脚本文件
$ hive -f aaa                                    
```


[1]: https://github.com/firechiang/hadoop-test/blob/master/hive/src/main/java/com/firecode/hadooptest/hive/udf/TuoMin.java
