### 一、使用说明：
#### 1.1，安装设置好以后需要重启Eclipse，设置项才会生效
#### 1.2，保存proto文件一般会自动编译成JAVA文件，如果没有自动编译，刷新项目就会自动编译
### 二、安装（注意：一定要看到最后）
```bash
安装时填的地址：https://google.github.io/protobuf-dt/updates/latest/
```
![image](https://github.com/firechiang/hadoop-test/blob/master/protobuf/image/1.png)
![image](https://github.com/firechiang/hadoop-test/blob/master/protobuf/image/2.png)
![image](https://github.com/firechiang/hadoop-test/blob/master/protobuf/image/3.png)
![image](https://github.com/firechiang/hadoop-test/blob/master/protobuf/image/4.png)
![image](https://github.com/firechiang/hadoop-test/blob/master/protobuf/image/5.png)
![image](https://github.com/firechiang/hadoop-test/blob/master/protobuf/image/6.png)

### 三、关闭 Eclipse（注意：一定要关闭 Eclipse，才执行下面的操作，否则Eclipse将无法使用）
### 四、替换 Protobuf-dt 插件依赖的Guava jar包
#### 4.1，[下载com.google.guava_21.0.0.v20170206-1425.jar包][1]（注：这个jar包实际就是guava-19.0.jar只是该了名字而已）
#### 4.2，将我们下载的jar包替换掉Eclipse插件安装目录下的com.google.guava_21.0.0.v20170206-1425.jar包（注：Windows Eclipse插件安装目录一般在：C:\Users\用户名\.p2\pool\plugins）

[1]: https://github.com/firechiang/hadoop-test/blob/master/protobuf/plug/com.google.guava_21.0.0.v20170206-1425.jar
