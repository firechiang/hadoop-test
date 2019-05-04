#### 一、sqoop export命令参数说明
```bash
--connect                                        # MySql连接地址
--driver                                         # 驱动类
--username                                       # MySql用户名
--password                                       # MySql密码
--table                                          # MySql表
--columns                                        # MySql的列，顺序要和HDFS文件数据格式一致
--export-dir                                     # 导出的HDFS目录
--num-mappers                                    # MapReduce Map的个数（强烈建议指定Map的个数）
```

#### 二、将下面的命令粘到命令行执行（将HDFS数据导出到MySql表），参数说明请看上面，反斜杠表示换行
```bash
$ sqoop export \
  --connect jdbc:mysql://server004:3306/sqoop_remote \
  --driver com.mysql.cj.jdbc.Driver \
  --username root \
  --password Jiang@123 \
  --table test \
  --export-dir /sqoop1/testmysql1 \
  --columns id,msg \
  --num-mappers 1
```