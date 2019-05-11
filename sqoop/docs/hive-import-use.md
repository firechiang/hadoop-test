#### 一、sqoop import命令参数说明
```bash
--connect                                        # MySql连接地址
--driver                                         # 驱动类
--username                                       # MySql用户名
--password                                       # MySql密码
--table                                          # MySql表
--num-mappers                                    # MapReduce Map的个数（强烈建议指定Map的个数）

--hive-table                                     # Hive 表名
--hive-import                                    # 将数据导入到Hive
--hive-overwrite                                 # 表存在就覆盖
--create-hive-table                              # 自动创建Hive表
--hive-database                                  # 指定Hive库
```

#### 二、将下面的命令粘到命令行执行（将MySql数据导入到Hive表），参数说明请看上面，反斜杠表示换行（注意：该命令依赖Hive客户端）
```bash
$ sqoop import \
  --connect jdbc:mysql://server004:3306/sqoop_remote \
  --driver com.mysql.cj.jdbc.Driver \
  --username root \
  --password Jiang@123 \
  --table test \
  --hive-import \
  --hive-overwrite \
  --hive-database test \
  --hive-table sqoop_user \
  --create-hive-table \
  --num-mappers 1
```
