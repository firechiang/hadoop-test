#### 一、sqoop import-all-tables命令参数说明
```bash
--connect                                        # MySql连接地址
--driver                                         # 驱动类
--username                                       # MySql用户名
--password                                       # MySql密码
```

#### 二、测试sqoop import-all-tables命令，将MySql所有表数据导入到HDFS，默认用表名做HDFS目录，将下面的命令粘到命令行执行，参数说明请看上面，反斜杠表示换行
```bash
# 注意：MySql8不能用，因为里面没有默认的SQ_CONFIG表，会报java.sql.SQLSyntaxErrorException: Table 'sqoop_remote.SQ_CONFIG' doesn't exist错误
$ sqoop import-all-tables \
  --connect jdbc:mysql://server004:3306/sqoop_remote \
  --driver com.mysql.cj.jdbc.Driver \
  --username root \
  --password Jiang@123
```