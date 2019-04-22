#### 一、安装
```bash
$ wget http://mirrors.tuna.tsinghua.edu.cn/apache/flume/1.9.0/apache-flume-1.9.0-bin.tar.gz # 下载安装包
$ tar -zxvf apache-flume-1.9.0-bin.tar.gz -C ../                                            # 解压到上层目录
```

#### 二、配置环境变量[vi ~/.bashrc]
```bash
export FLUME_HOME=/home/apache-flume-1.9.0-bin
PATH=$PATH:$FLUME_HOME/bin                                                                  # linux以 : 号隔开，windows以 ; 号隔开

$ source ~/.bashrc                                                                          # （系统重读配置）在各个机器上执行使配置文件生效（实验：敲个beel然后按Tab键，如果补全了说明配置成功了）
$ echo $FLUME_HOME                                                                          # 查看是否能获取到环境变量的值
```

#### 三、修改conf目录flume-conf.properties.template文件名为flume-conf.properties（这里面是定义Source，Channel，Sink的地方）
```bash
$ cp flume-conf.properties.template flume-conf.properties                                   # 复制文件flume-conf.properties.template生成新的文件叫flume-conf.properties
```

#### 四、修改conf目录flume-env.sh.template文件（配置JDK和JVM参数的地方）
```bash
$ cp flume-env.sh.template flume-env.sh                                                     # 复制文件flume-env.sh.template生成新的文件叫flume-env.sh.template
$ vi flume-env.sh                                                                           
export JAVA_HOME=/usr/lib/jvm/jdk1.8.0_171                                                  # 配置JDK目录（这个配置项，文件里面有只要放开注释，修改值即可）
```

#### 五、测试是否安装成功
```bash
$ flume-ng version                                                                          # 查看Flume的安装版本
```