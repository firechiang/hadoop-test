#### 一、sqoop import命令参数说明
```bash
--connect                                        # MySql连接地址
--driver                                         # 驱动类
--username                                       # MySql用户名
--password                                       # MySql密码
--table                                          # MySql表
--where                                          # sql语句过滤条件
--target-dir                                     # 导入到HDFS目录
--num-mappers                                    # MapReduce Map的个数（强烈建议指定Map的个数）

--incremental                                    # 增量导入模式有 append 和 lastmodify
--check-column                                   # 增量导入验证的字段（用哪个字段做增量验证，注意：这个字段不要用字符串类型，应用数字类型）
--last-value                                     # 增量导入--check-column那个字段的最后一个值（这个值说的是MySql里面的最后一个值，之前的就不会再导入了）
```

#### 二、将下面的命令粘到命令行执行（将MySql单表数据导入到HDFS），参数说明请看上面，反斜杠表示换行
```bash
$ sqoop import \
  --connect jdbc:mysql://server004:3306/sqoop_remote \
  --driver com.mysql.cj.jdbc.Driver \
  --username root \
  --password Jiang@123 \
  --table test \
  --where "id > 0" \
  --target-dir /sqoop1/testmysql1 \
  --num-mappers 1 \
  --incremental append \
  --check-column id \
  --last-value 2
```