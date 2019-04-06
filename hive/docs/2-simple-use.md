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

#### 三、建立人员测试表（执行建表语句时请将注释删除）
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
