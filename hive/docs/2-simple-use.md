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

#### 十九、函数使用（自定义函数请看代码udf包下；自带函数很多，关系型数据库函数，Hive基本都有）
```bash
$ select explode(links) from person;                                      # explode函数将数据以列的方式输出
```

#### 二十、[自定义函数（UDF）][1]


[1]: https://github.com/firechiang/hadoop-test/blob/master/hive/src/main/java/com/firecode/hadooptest/hive/udf/TuoMin.java
