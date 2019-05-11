#### 简单使用
```bash
$ sqoop job --help                 # 查看Sqoop job帮助

# 创建job,名字叫mysql2hdfs，-- import表示导入，其它都是正常导入参数（就是从命令的第二行开始的参数），反斜杠表示换行
$ sqoop job --create mysql2hdfs -- import \
  --connect jdbc:mysql://server004:3306/sqoop_remote \
  --driver com.mysql.cj.jdbc.Driver \
  --username root \
  --password Jiang@123 \
  --table test \
  --where "id > 0" \
  --target-dir /sqoop1/testmysql2 \
  --num-mappers 1 \
  --incremental append \
  --check-column id \
  --last-value 2
  
$ sqoop job --list                 # 查看所有job
$ sqoop job --show "job名称"       # 查看某个job详细信息（默认之后要输入Mysql密码，不想输入密码可自行百度：sqoop job指定密码）
$ sqoop job --delete "job名称"     # 删除某个job   
$ sqoop job --exec "job名称"       # 执行某个job（默认之后要输入Mysql密码，不想输入密码可自行百度：sqoop job指定密码）
```

