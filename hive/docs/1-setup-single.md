#### 一、安装Mysql
```bash
$ yum install -y mysql-server             # 安装Mysql服务
$ service mysqld start                    # 启动Mysql服务
$ chkconfig mysqld on                     # 开机启动Mysql服务
$ mysql                                   # 进入Mysql服务
$ select user,host,password from user;    # 查看Mysql授权用户信息（字段 host允许远程访问的ip）
$ GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'jiang' WITH GRANT OPTION    # 添加用户root允许所有IP访问，密码是jiang
$ delete from user where host != '%'      # 删除授权用户信息
$ select user,host,password from user;    # 现在应该只有一个用户信息了就是我刚刚加的
$ flush privileges                        # 刷新权限
```