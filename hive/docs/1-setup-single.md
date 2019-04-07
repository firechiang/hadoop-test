#### 一、安装
```bash
$ wget https://mirrors.tuna.tsinghua.edu.cn/apache/hive/hive-3.1.1/apache-hive-3.1.1-bin.tar.gz   # 下载文件包
$ tar -zxvf apache-hive-3.1.1-bin.tar.gz                                                          # 解压安装包
```

#### 二、配置环境变量[vi ~/.bashrc]
```bash
export HIVE_HOME=/home/apache-hive-3.1.1-bin
PATH=$PATH:$HIVE_HOME/bin                                                                         # linux以 : 号隔开，windows以 ; 号隔开

$ source ~/.bashrc                                                                                # （系统重读配置）在各个机器上执行使配置文件生效（实验：敲个beel然后按Tab键，如果补全了说明配置成功了）
```
#### 三、修改conf/hive-default.xml.template文件
```bash
$ mv hive-default.xml.template hive-site.xml                                                      # 将conf目录下配置文件hive-default.xml.template 修改为 hive-site.xml
$ vi hive-site.xml                                                                                # 修改配置内容
$ :.,$-1d                                                                                         # 删除文件里面除configuration标签外，其它所有内容（建议删除之前做备份）
$ :wq                                                                                             # 保存修改
```

#### 四、修改[vi hive-site.xml]
```bash
<!-- Hive数据上传到HDFS所在目录（这个目录是HDFS上面的） -->
<property>  
    <name>hive.metastore.warehouse.dir</name>  
    <value>/user/hive_remote/warehouse</value>  
</property>  
   
<!-- Hive是否本地模式（我们这里是单用户模式） -->
<property>  
    <name>hive.metastore.local</name>  
    <value>false</value>  
</property>  
   
<!-- 连接关系数库的地址（注意创建库名 hive_remote） -->
<property>  
    <name>javax.jdo.option.ConnectionURL</name>  
    <value>jdbc:mysql://server004:3306/hive_remote?createDatabaseIfNotExist=true&amp;serverTimezone=UTC</value>  
</property>  
   
<!-- JDBC驱动 -->
<property>  
    <name>javax.jdo.option.ConnectionDriverName</name>  
    <value>com.mysql.cj.jdbc.Driver</value>  
</property>  
   
<!-- 连接关系数库用户名（我们使用的是MySQL用户名） -->   
<property>  
    <name>javax.jdo.option.ConnectionUserName</name>  
    <value>root</value>  
</property>  
   
<!-- 连接关系数据库密码（我们使用的是MySQL密码） -->   
<property>  
    <name>javax.jdo.option.ConnectionPassword</name>  
    <value>Jiang@123</value>  
</property> 

<!-- 如果为true，Hive Server会以提交用户的身份去执行语句，如果为false会以hive server daemon的admin user来执行语句  -->
<property>
    <name>hive.server2.enable.doAs</name>
    <value>true</value>
</property>

<!-- 绑定hiveserver2服务的端口，默认是10000 -->
<property>
    <name>hive.server2.thrift.port</name>
    <value>10001</value>
</property>
```

#### 五、将MySQL驱动包拷贝到Hive程序lib目录下
```bash
$ scp mysql-connector-java-8.0.15.jar /home/apache-hive-3.1.1-bin/lib
```

#### 六、修改conf/hive-env.sh.template文件
```bash
$ mv hive-env.sh.template hive-env.sh
```

#### 七、修改[vi hive-env.sh]
```bash
HADOOP_HOME=/home/hadoop-3.2.0                                   # Hadoop目录
export HIVE_CONF_DIR=/home/apache-hive-3.1.1-bin/conf            # HIve配置文件目录
```

#### 八、初始化元数据
```bash
$ schematool -dbType mysql -initSchema
```

#### 九、简单使用
```bash
$ hive                                                           # 进入Hive（注意日志打印是否有：Class path contains multiple SLF4J bindings错误，看看是不是有jar包冲突，如果有，删除Hive lib目录相应jar包即可）
```

