#### 一、预先准备环境，下载安装包：http://mirror.bit.edu.cn/apache/cassandra/3.11.4/apache-cassandra-3.11.4-bin.tar.gz
```bash
$ start winrar x -y apache-cassandra-3.11.4-bin.tar.gz ./  # 使用Winrar将文件解压到当前目录（用管理员身份打开命令行）
```

#### 二、配置 CASSANDRA_HOME 环境变量

#### 三、修改[apache-cassandra-3.11.4/conf/cassandra.yaml]配置用户名密码才能登陆
```bash
authenticator: PasswordAuthenticator
```

#### 四、下载安装Python(环境变量会自动配置)：https://www.python.org/ftp/python/2.7.16/python-2.7.16.amd64.msi
```bash
$ python                                                   # 进入Python命令行，看看Python是否安装成功
$ quit()                                                   # 退出Python命令行
```

#### 五、删除默认账号和初始化自定义账号密码
```bash
$ cassandra                                                # 启动Cassandra

# 另起一个命令行窗口，进入Cassandra客服端（远程Cassandra的IP：127.0.0.1，端口：9042，用户名(-u)：cassandra，密码(-p)：cassandra）
$ cqlsh 127.0.0.1 9042 -ucassandra -pcassandra
$ create user jiang with password 'jiang' superuser;       # 创建超级管理员jiang，密码：jiang
$ exit;                                                    # 退出当前登录账号
$ cqlsh 127.0.0.1 9042 -ujiang -pjiang                     # 登录我们刚刚创建的账号
$ drop user cassandra;                                     # 删除默认管理员账号
```

#### 六、命令行简单使用
```bash
$ describe keyspaces;                                      # 查看所有的键空间（相当于Mysql的库）
# 创建test_test键空间（keyspace），简单的副本放置策略，数据副本1个
$ create keyspace if not exists test_test with durable_writes = true and replication = {'class':'SimpleStrategy','replication_factor':1};
$ use test_test;                                           # 进入 test_test 键空间

# 创建通话记录表
$ create table call_record(
      id int PRIMARY KEY,
      dnum_phone text,
      length int,
      type int,
      create_time TIMESTAMP);
      
$ describe tables;                                         # 显示所有表
$ describe table call_record;                              # 查看表  call_record 的详细信息 
$ insert into call_record(id,dnum_phone,length,type,create_time) values(1,'13143457381',45,1,'2019-02-05 20:20:20');
$ select * from call_record;
```
