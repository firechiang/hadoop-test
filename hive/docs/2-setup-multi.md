#### 一、服务端安装配置和[单用户搭建][1]一致（注意：多用户模式，服务端和客户端不能在同一台机器上）
#### 二、客户端安装配置和”单用户搭建“基本一致，只是配置文件[vi hive-site.xml]变更如下（注意：另找一台机器作为客户端）
```bash
<!-- 指向运行Metastore服务的主机，多个以逗号号隔开,这是hive客户端配置，服务端不配置 -->
<property>
    <name>hive.metastore.uris</name>
    <value>thrift://server003:9083</value>
</property>
```
#### 三、使用Metastore服务（& 表示后台启动）
```bash
$ hive --service metastore &                                # 先到服务端启动 Metastore服务（& 表示后台启动）
$ ps -aux| grep metastore                                   # 查看 metastore 进程信息
$ hive                                                      # 再到客户端执行，连接Metastore服务，连上了就可以用命令对Hive进行一系列的操作了
```
#### 四、使用Hiveserver2服务（& 表示后台启动）
```bash
$ hive --service hiveserver2 &                              # 先到服务端启动 Hiveserver2服务，这个启动有点慢，而且会报java.lang.NoClassDefFoundError: org/apache/tez/dag/api/TezConfiguration错误，原因是我们没有使用tez
$ ps -aux| grep hiveserver2                                 # 查看 hiveserver2 进程信息
$ beeline -u jdbc:hive2://server003:10001 -n root -p aaaa   # 再到客户端连接Hiveserver2服务（-u是database url，-n是username，-p是密码（我们没有验证密码，所以密码可以随便写） ）
$ !quit                                                     # 退出 beeline（beeline命令一般再前面加!）
```

[1]: https://github.com/firechiang/hadoop-test/tree/master/hive/docs/1-setup-single.md