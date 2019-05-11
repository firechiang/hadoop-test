#### 一、sqoop import命令参数说明
```bash
--connect                                        # MySql连接地址
--driver                                         # 驱动类
--username                                       # MySql用户名
--password                                       # MySql密码
--table                                          # MySql表
--num-mappers                                    # MapReduce Map的个数（强烈建议指定Map的个数）

--column-family                                  # 指定HBase列族
--hbase-row-key                                  # 指定MySql数据那个字段作为Row Key（一般用主键）
--hbase-table                                    # Hbase表名
--hbase-create-table                             # 自动创建HBase表
```

#### 二、将下面的命令粘到命令行执行（将MySql数据导入到HBase表），参数说明请看上面，反斜杠表示换行
```bash
$ sqoop import \
  --connect jdbc:mysql://server004:3306/sqoop_remote \
  --driver com.mysql.cj.jdbc.Driver \
  --username root \
  --password Jiang@123 \
  --table test \
  --column-family cf1 \
  --hbase-row-key id \
  --hbase-table sqoop_user1 \
  --hbase-create-table \
  --num-mappers 1
```