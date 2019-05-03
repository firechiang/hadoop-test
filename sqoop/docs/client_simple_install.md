#### 一、下载安装（注意：运行Sqoop需要有HADOOP_HOME环境变量，因为它要加载Hadoop依赖，默认会从这个环境变量里面去找）
```bash
$ wget https://mirrors.tuna.tsinghua.edu.cn/apache/sqoop/1.99.7/sqoop-1.99.7-bin-hadoop200.tar.gz # 下载安装包
$ tar -zxvf sqoop-1.99.7-bin-hadoop200.tar.gz -C ../                                              # 解压到上层目录
```

#### 二、配置环境变量[vi ~/.bashrc]
```bash
export SQOOP_HOME=/home/sqoop-1.99.7-bin-hadoop200
PATH=$PATH:$SQOOP_HOME/bin                                                                        # linux以 : 号隔开，windows以 ; 号隔开

$ source ~/.bashrc                                                                                # （系统重读配置）在各个机器上执行使配置文件生效（实验：敲个beel然后按Tab键，如果补全了说明配置成功了）
$ echo $SQOOP_HOME                                                                                # 查看是否能获取到环境变量的值
```
#### 三、启动客户端
```bash
$ sqoop2-shell                                             # 进入命令行客户端（也可以使用：sqoop.sh client 命令）
$ set server --host 127.0.0.1 --port 12000 --webapp sqoop  # 连接到Server
$ set option --name verbose --value true                   # 开启verbose，方便在出错时及时查错
$ :exit                                                    # 退出客户端命令行
```

#### 四、简单使用
```bash
$ sqoop2-shell                                             # 进入命令行客户端（也可以使用：sqoop.sh client 命令）
$ show version                                             # 显示 Sqoop 服务端版本
$ show version --all                                       # 显示 Sqoop 服务端和客户端版本
$ show server --all                                        # 显示服务器相关信息
$ show link                                                # 显示所有连接
$ show job                                                 # 显示所有任务

$ show connector                                           # 显示 Sqoop 所支持的连接器类型

$ show link                                                # 查看所有的link（连接器）
$ show link -all                                           # 查看所有的link（连接器）的详细信息
$ update link --name hdfs                                  # 修改名字叫hdfs的link（连接器）
$ delete link --name mysql                                 # 删除名字叫mysql的link

$ show job                                                 # 查看所有的job（任务）
$ show job -all                                            # 查看所有的job（任务）的详细信息
$ update job --name mysql2hdfs                             # 修改名字叫mysql2hdfs的job（任务）
$ delete job --name  mysql2hdfs                            # 删除名字叫mysql2hdfs的job（任务）
```
