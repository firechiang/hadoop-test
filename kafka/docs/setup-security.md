#### 一、创建密钥仓库文件，用于存储证书文件
```bash
# 定位到Kafka目录生成ssl文件夹并进入到ssl文件夹（注意：ssl文件夹在哪里都可以）
$ cd /home/chiangfire/data-dev/kafka && mkdir ssl && cd ssl

# 密钥仓库文件（注意：kafka.keystore.jks是密钥仓库文件名称，security-kafka是密钥仓库的别名）
$ keytool -keystore kafka.keystore.jks -alias security-kafka -validity 100000 -genkey
# 注意：密码的长度的是6位，而且要记住因为下面要用到
输入密钥库口令:  
再次输入新口令: 
您的名字与姓氏是什么?
  [Unknown]:  jiang
您的组织单位名称是什么?
  [Unknown]:  ji
您的组织名称是什么?
  [Unknown]:  ji
您所在的城市或区域名称是什么?
  [Unknown]:  shenzhen
您所在的省/市/自治区名称是什么?
  [Unknown]:  shenzhen
该单位的双字母国家/地区代码是什么?
  [Unknown]:  cn
CN=jiang, OU=ji, O=ji, L=shenzhen, ST=shenzhen, C=cn是否正确?
  [否]:  y
  
# 完成以后在该目录下会生成一个kafka.keystore.jks文件
```
#### 二、创建CA证书
```bash
# 创建CA
$ openssl req -new -x509 -keyout ca-key -out ca-cert -days 100000
Generating a RSA private key
........................................................................................................................................................+++++
...............................................+++++
writing new private key to 'ca-key'
# 输入密码，建议和上面的一致
Enter PEM pass phrase:
# 确认密码
Verifying - Enter PEM pass phrase:
-----
You are about to be asked to enter information that will be incorporated
into your certificate request.
What you are about to enter is what is called a Distinguished Name or a DN.
There are quite a few fields but you can leave some blank
For some fields there will be a default value,
If you enter '.', the field will be left blank.
-----
Country Name (2 letter code) [AU]:cn
State or Province Name (full name) [Some-State]:shenzhen
Locality Name (eg, city) []:nanshan
Organization Name (eg, company) [Internet Widgits Pty Ltd]:ji   
Organizational Unit Name (eg, section) []:jiang
Common Name (e.g. server FQDN or YOUR name) []:jiang
Email Address []:chiang-fire@outlook.com
```

#### 三、将CA证书添加到客户端信任库文件当中，并生产客户端信任库文件
```bash
# client.truststore.jks是信任库文件名，CARoot是信任库别名
$ keytool -keystore client.truststore.jks -alias CARoot -import -file ca-cert
# 输入密码，建议和上面的一致
输入密钥库口令:  
再次输入新口令: 
所有者: EMAILADDRESS=chiang-fire@outlook.com, CN=jiang, OU=jiang, O=ji, L=nanshan, ST=shenzhen, C=cn
发布者: EMAILADDRESS=chiang-fire@outlook.com, CN=jiang, OU=jiang, O=ji, L=nanshan, ST=shenzhen, C=cn
序列号: 3192ec3d42eb0f1924d5c4baafb266263caa99ca
生效时间: Tue Feb 16 15:20:34 CST 2021, 失效时间: Sun Dec 02 15:20:34 CST 2294
证书指纹:
         SHA1: 8D:F5:98:C6:31:BD:73:72:28:10:EB:08:E4:41:F9:90:95:8A:9F:6C
         SHA256: 72:3A:5E:B0:BE:49:1A:9E:99:63:22:4F:91:65:53:DE:03:B3:20:C9:D7:76:60:DE:02:FD:75:A7:C5:47:BE:90
签名算法名称: SHA256withRSA
主体公共密钥算法: 2048 位 RSA 密钥
版本: 3

扩展: 

#1: ObjectId: 2.5.29.35 Criticality=false
AuthorityKeyIdentifier [
KeyIdentifier [
0000: 70 CE 0F 4C 42 18 07 D0   81 C8 14 2B 31 BF 21 B4  p..LB......+1.!.
0010: AF E6 77 17                                        ..w.
]
]

#2: ObjectId: 2.5.29.19 Criticality=true
BasicConstraints:[
  CA:true
  PathLen:2147483647
]

#3: ObjectId: 2.5.29.14 Criticality=false
SubjectKeyIdentifier [
KeyIdentifier [
0000: 70 CE 0F 4C 42 18 07 D0   81 C8 14 2B 31 BF 21 B4  p..LB......+1.!.
0010: AF E6 77 17                                        ..w.
]
]

是否信任此证书? [否]:  y
证书已添加到密钥库中

# 完成以后在该目录下会生成一个client.truststore.jks文件
```

#### 四、将CA证书添加到服务端信任库文件当中，并生产服务端信任库文件
```bash
# server.truststore.jks是信任库文件名，CARoot是信任库别名
$ keytool -keystore server.truststore.jks -alias CARoot -import -file ca-cert
输入密钥库口令:  
再次输入新口令: 
所有者: EMAILADDRESS=chiang-fire@outlook.com, CN=jiang, OU=jiang, O=ji, L=nanshan, ST=shenzhen, C=cn
发布者: EMAILADDRESS=chiang-fire@outlook.com, CN=jiang, OU=jiang, O=ji, L=nanshan, ST=shenzhen, C=cn
序列号: 3192ec3d42eb0f1924d5c4baafb266263caa99ca
生效时间: Tue Feb 16 15:20:34 CST 2021, 失效时间: Sun Dec 02 15:20:34 CST 2294
证书指纹:
         SHA1: 8D:F5:98:C6:31:BD:73:72:28:10:EB:08:E4:41:F9:90:95:8A:9F:6C
         SHA256: 72:3A:5E:B0:BE:49:1A:9E:99:63:22:4F:91:65:53:DE:03:B3:20:C9:D7:76:60:DE:02:FD:75:A7:C5:47:BE:90
签名算法名称: SHA256withRSA
主体公共密钥算法: 2048 位 RSA 密钥
版本: 3

扩展: 

#1: ObjectId: 2.5.29.35 Criticality=false
AuthorityKeyIdentifier [
KeyIdentifier [
0000: 70 CE 0F 4C 42 18 07 D0   81 C8 14 2B 31 BF 21 B4  p..LB......+1.!.
0010: AF E6 77 17                                        ..w.
]
]

#2: ObjectId: 2.5.29.19 Criticality=true
BasicConstraints:[
  CA:true
  PathLen:2147483647
]

#3: ObjectId: 2.5.29.14 Criticality=false
SubjectKeyIdentifier [
KeyIdentifier [
0000: 70 CE 0F 4C 42 18 07 D0   81 C8 14 2B 31 BF 21 B4  p..LB......+1.!.
0010: AF E6 77 17                                        ..w.
]
]

是否信任此证书? [否]:  y
证书已添加到密钥库中

# 完成以后在该目录下会生成一个server.truststore.jks文件
```

#### 五、签名证书（用自己生成的CA来签名前面生成的证书）
##### 1，从密钥仓库导出证书
```bash
# 注意：kafka.keystore.jks要和密钥仓库的一致，别名security-kafka也要一致
$ keytool -keystore kafka.keystore.jks -alias security-kafka -certreq -file cert-file
# 输入密码，和上面的一样
输入密钥库口令:
# 完成后会生成一个cert-file文件
```

##### 2，生成CA证书签名文件
```bash
# 注意：jiang123 就是上面指定的密码 
$ openssl x509 -req -CA ca-cert -CAkey ca-key -in cert-file -out cert-signed -days 100000 -CAcreateserial -passin pass:jiang123
Signature ok
subject=C = cn, ST = shenchen, L = shenxhen, O = ji, OU = ji, CN = jiang
Getting CA Private Key
# 完成以后会生成一个cert-signed签名文件
```

##### 3，导入CA证书和已签名信息到密钥仓库
```bash
# 导入CA证书到密钥仓库（注意：ca-cert就是CA证书）
$ keytool -keystore kafka.keystore.jks -alias CARoot -import -file ca-cert
# 输入密码，和上面的一致
输入密钥库口令:  
所有者: EMAILADDRESS=chiang-fire@outlook.com, CN=jiang, OU=jiang, O=ji, L=nanshan, ST=shenzhen, C=cn
发布者: EMAILADDRESS=chiang-fire@outlook.com, CN=jiang, OU=jiang, O=ji, L=nanshan, ST=shenzhen, C=cn
序列号: 3192ec3d42eb0f1924d5c4baafb266263caa99ca
生效时间: Tue Feb 16 15:20:34 CST 2021, 失效时间: Sun Dec 02 15:20:34 CST 2294
证书指纹:
         SHA1: 8D:F5:98:C6:31:BD:73:72:28:10:EB:08:E4:41:F9:90:95:8A:9F:6C
         SHA256: 72:3A:5E:B0:BE:49:1A:9E:99:63:22:4F:91:65:53:DE:03:B3:20:C9:D7:76:60:DE:02:FD:75:A7:C5:47:BE:90
签名算法名称: SHA256withRSA
主体公共密钥算法: 2048 位 RSA 密钥
版本: 3

扩展: 

#1: ObjectId: 2.5.29.35 Criticality=false
AuthorityKeyIdentifier [
KeyIdentifier [
0000: 70 CE 0F 4C 42 18 07 D0   81 C8 14 2B 31 BF 21 B4  p..LB......+1.!.
0010: AF E6 77 17                                        ..w.
]
]

#2: ObjectId: 2.5.29.19 Criticality=true
BasicConstraints:[
  CA:true
  PathLen:2147483647
]

#3: ObjectId: 2.5.29.14 Criticality=false
SubjectKeyIdentifier [
KeyIdentifier [
0000: 70 CE 0F 4C 42 18 07 D0   81 C8 14 2B 31 BF 21 B4  p..LB......+1.!.
0010: AF E6 77 17                                        ..w.
]
]

是否信任此证书? [否]:  y
证书已添加到密钥库中


# 导入CA签名信息到密钥仓库（注意：cert-signed就是CA签名）
$ keytool -keystore kafka.keystore.jks -alias security-kafka -import -file cert-signed
# 输入密码，和上面的一致
输入密钥库口令:  
证书回复已安装在密钥库中
```

#### 六、Kafka配置安全连接（配置证书）[vi server.properties]
```bash
# Kfaka集群内部使用绑定的IP和端口（注意：SSL是绑定的安全连接地址和端口）
listeners=PLAINTEXT://127.0.0.1:9092,SSL://127.0.0.1:9091
# Kfaka集群对外暴露服务的IP和端口（注意：这里的默认值就是listeners的值；还有这里可以配置域名，SSL是绑定的安全连接地址和端口）
advertised.listeners=PLAINTEXT://127.0.0.1:9092,SSL://127.0.0.1:9091

# 密钥仓库文件
ssl.keystore.location=/home/chiangfire/data-dev/kafka/ssl/kafka.keystore.jks
# 密钥仓库密码
ssl.keystore.password=jiang123

# 证书密码
ssl.key.password=jiang123

# 服务端信任库文件
ssl.truststore.location=/home/chiangfire/data-dev/kafka/ssl/server.truststore.jks
# 服务端信任库文件密码
ssl.truststore.password=jiang123

# 客户端身份验证是必需的
#ssl.client.auth=required
# 可使用的SSL协议列表
ssl.enabled.protocols=TLSv1.2,TLSv1.1,TLSv1
# 证书秘钥类型
ssl.keystore.type=JKS 
ssl.truststore.type=JKS

# broker内部通讯使用SSL
#security.inter.broker.protocol=SSL
```

#### 七、测试SSL安全（注意：测试前启动Kafka集群）
```bash
# 测试SSL端口
# 这条命令执行完成以后客户端会打印一些连接信息
# 这条命令执行完成以后服务端会打印SSL连接失败的日志如下：
# INFO [SocketServer brokerId=0] Failed authentication with /127.0.0.1 (SSL handshake failed) (org.apache.kafka.common.network.Selector)
$ openssl s_client -debug -connect 127.0.0.1:9091 -tls1
read from 0x55cf6a8749e0 [0x55cf6a7b6870] (8192 bytes => 327 (0x147))
0000 - 00 00 00 00 00 00 00 00-00 00 00 00 00 00 00 00   ................
0010 - 00 00 00 00 00 00 00 00-00 00 00 00 00 00 00 00   ................
0020 - 00 00 00 00 00 00 00 00-00 00 00 00 00 00 00 00   ................
0030 - 00 00 00 00 00 00 00 00-00 00 00 00 00 00 00 00   ................
0040 - 00 00 00 00 00 00 00 00-00 00 00 00 00 00 00 00   ................
0050 - 00 00 00 00 00 00 00 00-00 00 00 00 00 00 00 00   ................
0060 - 00 00 00 00 00 00 00 00-00 00 00 00 00 00 00 00   ................
0070 - 00 00 00 00 00 00 00 00-00 00 00 00 00 00 00 00   ................
0080 - 00 00 00 00 00 00 00 00-00 00 00 00 00 00 00 00   ................
0090 - 00 00 00 00 00 00 00 00-00 00 00 00 00 00 00 00   ................
00a0 - 00 00 00 00 00 00 00 00-00 00 00 00 00 00 00 00   ................
00b0 - 00 00 00 00 00 00 00 00-00 00 00 00 00 00 00 00   ................
00c0 - 00 00 00 00 00 00 00 00-00 00 00 00 00 00 00 00   ................
00d0 - 00 00 00 00 00 00 00 00-00 00 00 00 00 00 00 00   ................
00e0 - 00 00 00 00 00 00 00 00-00 00 00 00 00 00 00 00   ................
00f0 - 00 00 00 00 00 00 00 00-00 00 00 00 00 00 00 00   ................
0100 - 00 00 00 00 00 00 00 00-00 00 00 00 00 00 00 00   ................
0110 - 00 00 00 00 00 00 00 00-00 00 00 00 00 00 00 00   ................
0120 - 00 00 00 00 00 00 00 00-00 00 00 00 00 00 00 00   ................
0130 - 00 00 00 00 00 00 00 00-00 00 00 00 00 00 00 00   ................
0140 - 15 03 03 00 02 02 46                              ......F
read from 0x55cf6a8749e0 [0x55cf6a7b6870] (8192 bytes => 0 (0x0))
```

#### 八、客户端SSL连接Kafka集群配置如下参数即可
```bash
# 使用SSL（安全）连接服务地址
config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, SERVER_NAME_SSL);
config.put("security.protocol", "SSL");
# 使用服务器证书验证服务器主机名的端点标识算法（空字符串表示关闭服务器主机名验证）
# SslConfigs.DEFAULT_SSL_ENDPOINT_IDENTIFICATION_ALGORITHM
config.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "");
config.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "/home/chiangfire/data-dev/kafka/ssl/client.truststore.jks");
config.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "jiang123");
```