#### 一、集群节点分部情况
```bash
----------|----------------|
          |     种子节点    |
----------|----------------|
server001 |        Y       |   
----------|----------------|
server002 |                |
----------|----------------|
server003 |        Y       |
----------|----------------|
```
#### 二、预先准备环境(root或sudo访问系统)
```bash
$ cat /etc/redhat-release                   # 查看CentOS版本，需CentOS 7.3或更高版本
$ uname -a                                  # 查看linux内核版本，需3.10或更高版本
$ sudo yum remove -y abrt                   # 删除abrt(解决abrt与Scylla coredump配置冲突问题)
$ sudo yum install epel-release
```

#### 三、下载 scylla 源配置文件到 /etc/yum.repos.d 目录
```bash
# 这个是官方文档提供的源文件，但是在下载程序时较慢，可能会有下载安装失败的情况
$ sudo curl -o /etc/yum.repos.d/scylla.repo -L http://repositories.scylladb.com/scylla/repo/centos/scylladb-3.0.repo

# 这个不是官方文档提供的源文件，但也是官方的源，这个在下载程序时较快，基本不会有下载安装失败的情况
$ sudo curl -o /etc/yum.repos.d/scylla.repo -L http://repositories.scylladb.com/scylla/repo/f8bb7a46e42927231ac3445eb3d7fba4/centos/scylladb-3.0.repo
```

#### 四、安装
```bash
$ sudo yum install scylla                   # 安装 scylla
```

#### 四、修改[vi /etc/scylla/scylla.yaml]配置(注意：配置和Cassandra配置一样的。配置项文件里面都有，只需修改值即可)
```bash
# 集群的名称
cluster_name: 'myScyllaCluster'

# 配置用户名密码才能登陆
authenticator: PasswordAuthenticator

# 种子节点的主机或IP
seed_provider:
  - class_name: org.apache.cassandra.locator.SimpleSeedProvider
    parameters:
      - seeds: "server001,server003"
      
api_address: server001
      
# 是否开始thrift rpc服务器
start_rpc: false      
      
# 集群中服务器与服务器之间相互通信的地址，也可以配置listen_interface，指定使用哪个网卡接口。两者选一配置即可，不要同时配置（这里配置当前主机或IP，为了安全考虑建议配置防火墙）   
listen_address: server001

# 服务器对外提供服务的地址（注意：这里配置当前主机或IP，为了安全考虑建议配置防火墙）
rpc_address: server001 

# 服务器对外提供服务的端口
rpc_port: 9160

# 这个端口用于接收命令和数据（为了安全考虑建议配置防火墙）
storage_port: 7000

# 客户端和服务端的通信端口（注意配置防火墙）
native_transport_port: 9042    

# 数据中心机架感知策略
endpoint_snitch: GossipingPropertyFileSnitch
```

#### 四、修改[vi /etc/scylla/cassandra-rackdc.properties]数据中心和机架的配置
##### 4.1，server001机器的配置
```bash
# 数据中心的名称
dc=datacenter1
# 机架的名称
rack=rack43
```
##### 4.2，server002机器的配置
```bash
# 数据中心的名称
dc=datacenter1
# 机架的名称
rack=rack44
```
##### 4.3，server003机器的配置
```bash
# 数据中心的名称
dc=datacenter1
# 机架的名称
rack=rack45
```

#### 五、运行scylla_setup脚本以调整系统设置
```bash
$ sudo scylla_setup

Skip any of the following steps by answering 'no'
Do you want to run check your kernel version?
Yes - runs a  script to verify that the kernel for this instance qualifies to run Scylla. No - skips the kernel check.
[YES/no]yes
INFO  2019-05-14 01:59:08,940 [shard 0] iotune - /var/tmp/mnt passed sanity checks
This is a supported kernel version.
Do you want to verify the ScyllaDB packages are installed?
Yes - runs a script to confirm that ScyllaDB is installed. No - skips the installation check.
[YES/no]no
Do you want the Scylla server service to automatically start when the Scylla node boots?
Yes - Scylla server service automatically starts on Scylla node boot. No - skips this step. Note you will have to start the Scylla Server service manually.
[YES/no]yes
Created symlink from /etc/systemd/system/multi-user.target.wants/scylla-server.service to /usr/lib/systemd/system/scylla-server.service.
Do you want to disable SELinux?
Yes - disables SELinux. Choosing Yes greatly improves performance. No - keeps SELinux activated.
[YES/no]no
Do you want to setup Network Time Protocol(NTP) to auto-synchronize the current time on the node?
Yes - enables time-synchronization. This keeps the correct time on the node. No - skips this step.
[YES/no]no
Do you want to setup RAID0 and XFS?
It is recommended to use RAID0 and XFS for Scylla data. If you select yes, you will be prompted to choose the unmounted disks to use for Scylla data. Selected disks are formatted as part of the process.
Yes - choose a disk/disks to format and setup for RAID0 and XFS. No - skip this step.
[YES/no]yes
Are you sure you want to setup RAID0 and XFS?
If you choose Yes, the selected drive will be reformated, erasing all existing data in the process.
[YES/no]no
Do you want to enable coredumps?
Yes - sets up coredump to allow a post-mortem analysis of the Scylla state just prior to a crash. No - skips this step.
[YES/no]yes
kernel.core_pattern = |/usr/lib/systemd/systemd-coredump %p %u %g %s %t %e"
Do you want to setup a system-wide customized configuration for Scylla?
Yes - setup the sysconfig file. No - skips this step.
[YES/no]yes
Do you want to enable Network Interface Card (NIC) and disk(s) optimization?
Yes - optimize the NIC queue and disks settings. Selecting Yes greatly improves performance. No - skip this step.
[YES/no]yes
Do you want iotune to study your disks IO profile and adapt Scylla to it?
Yes - let iotune study my disk(s). Note that this action will take a few minutes. No - skip this step.
[YES/no]yes
tuning /sys/devices/virtual/block/dm-0
tuning: /sys/devices/virtual/block/dm-0/queue/nomerges 2
tuning /sys/devices/pci0000:00/0000:00:10.0/host2/target2:0:0/2:0:0:0/block/sda/sda2
tuning /sys/devices/pci0000:00/0000:00:10.0/host2/target2:0:0/2:0:0:0/block/sda
tuning: /sys/devices/pci0000:00/0000:00:10.0/host2/target2:0:0/2:0:0:0/block/sda/queue/nomerges 2
INFO  2019-05-14 01:59:42,594 [shard 0] iotune - /var/lib/scylla/data passed sanity checks
WARN  2019-05-14 01:59:42,594 [shard 0] iotune - Scheduler for /sys/devices/pci0000:00/0000:00:10.0/host2/target2:0:0/2:0:0:0/block/sda/queue/scheduler set to deadline. It is recommend to set it to noop before evaluation so as not to skew the results.
Starting Evaluation. This may take a while...
Measuring sequential write bandwidth: 47 MB/s
Measuring sequential read bandwidth: 126 MB/s
Measuring random write IOPS: 285 IOPS
Measuring random read IOPS: 262 IOPS
Recommended --num-io-queues: 1
Writing result to /etc/scylla.d/io_properties.yaml
Writing result to /etc/scylla.d/io.conf
Do you want to install node exporter to export Prometheus data from the node? Note that the Scylla monitoring stack uses this data
Yes - install node exporter. No - skip this  step.
[YES/no]yes
Created symlink from /etc/systemd/system/multi-user.target.wants/node-exporter.service to /usr/lib/systemd/system/node-exporter.service.
node_exporter successfully installed
Do you want to set the CPU scaling governor to Performance level on boot?
Yes - sets the CPU scaling governor to performance level. No - skip this step.
[YES/no]yes
This computer doesn't supported CPU scaling configuration.
Do you want to enable fstrim service?
Yes - runs fstrim on your SSD. No - skip this step.
[YES/no]yes
ScyllaDB setup finished                         
```

#### 六、scylla --help 查看配置帮助，修改[vi /etc/sysconfig/scylla-server]启动脚本，添加到 SCYLLA_ARGS 选项里面
```bash
--memory 1G                                            # 每个CPU使用的最大内存(不配置的话，默认是4G*CPU，机器内存最少3G，如果机器内存不足，将导致无法启动Scylla)
--smp 1                                                # 使用几个CPU(如果配置的话，最好是配合 --memory 一起配置)
```

#### 七、启动Scylla集群，因为种子节点是server001，server003我们将首先启动它们，最后才启动server002(http://192.168.83.143:10000/ui)
```bash
$ sudo systemctl start scylla-server                   # 启动
$ sudo systemctl restart scylla-server                 # 重启
$ sudo systemctl stop scylla-server                    # 关闭
$ sudo service scylla-jmx stop                         # 关闭jmx服务
$ sudo service scylla-jmx start                        # 开启jmx服务(默认已开启)
$ nodetool status                                      # 查看集群的状态
$ nodetool help                                        # 查看 nodetool 命令帮助
```

#### 八、远程连接Cassandra初始化账号和密码(修改一台机器，会自动同步到集群其它机器)
```bash
$ cqlsh 192.168.83.137 9042 -ucassandra -pcassandra    # 连接（远程Cassandra的IP：127.0.0.1，端口：9042，用户名(-u)：cassandra，密码(-p)：cassandra）
$ create user jiang with password 'jiang' superuser;   # 创建超级管理员jiang，密码：jiang
$ exit;                                                # 退出当前登录账号
$ cqlsh 192.168.83.137 9042 -ujiang -pjiang            # 登录我们刚刚创建的账号
$ drop user cassandra;                                 # 删除默认管理员账号
```

