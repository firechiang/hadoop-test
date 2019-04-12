#### 一、Hive 权限管理，有三种授权模型
```bash
1、基于存储的授权 - 可以对Metastore中的元数据进行保护，但是没有提供更加细粒度的访问控制（例如：列级别、行级别）。
2、基于SQL标准的Hive授权 - 完全兼容SQL的授权模型，推荐使用该模式。
3、Hive默认授权 - 设计目的仅仅只是为了防止用户产生误操作，而不是防止恶意用户访问未经授权的数据
```

#### 二、修改服务端配置[vi hive-site.xml]（注意配置了权限的Hive，服务端需要使用 hiveserver2 启动，客户端需要用 beeline 连接）
```bash
<!-- 开启权限控制 -->
<property>
    <name>hive.security.authorization.enabled</name>
    <value>true</value>
</property>

<!-- 如果控制了权限，建议使用false，如果为true，Hive Server会以提交用户的身份去执行语句，如果为false会以hive server的用户来执行语句  -->
<property>
    <name>hive.server2.enable.doAs</name>
    <value>false</value>
</property>

<!-- 给root用户分配管理员的角色 -->
<property>
    <name>hive.users.in.admin.role</name>
    <value>root</value>
</property>

<!-- 权限认证规则实现类（基于sql级别） -->
<property>
    <name>hive.security.authorization.manager</name>
    <value>org.apache.hadoop.hive.ql.security.authorization.plugin.sqlstd.SQLStdHiveAuthorizerFactory</value>
</property>

<!-- 权限认证规则实现类（基于session（会话）级别） -->
<property>
    <name>hive.security.authenticator.manager</name>
    <value>org.apache.hadoop.hive.ql.security.SessionStateUserAuthenticator</value>
</property>
```

#### 二、角色管理（角色添加，删除，查看，设置）
```bash
$ show current roles;           # 查看当前具有的角色
$ set role admin;               # 给当前用户添加管理员角色（要当前系统用户是root才能执行，因为我们上面配置的是root可以有admin角色）
$ show roles;                   # 查看所有角色（命令是admin角色才能使用）
$ create role test;             # 创建角色（命令是admin角色才能使用）
$ show roles;                   # 看看是不有我们建立的test角色（命令是admin角色才能使用）
$ drop role test;               # 删除角色（命令是admin角色才能使用）
$ show roles;                   # 看看我们建立的test角色是不是没有了（命令是admin角色才能使用）
```

#### 二、权限管理（注意要到对应的DataBase里面授权,否则的话需要指定DataBase，比如： grant select on database default to user aaa
```bash
# 授权命令说明，'权限' 有（insert | select | update | delete | all）
$ grant '权限' on [table|view] '表名|视图名' to [role|user|group] ['用户名|角色名''组名']
 
# 授权命令示例1（为test角色添加对person表的查询权限）
$ grant select on table person to role test;

# 授权命令示例2（为用户aaa添加对person表的查询权限）
$ grant select on table person to user aaa;
            
# 查看权限说明（'授权方式'有 （grant | option | for），看你授权命令的第一个单词）
$ show '授权方式' [role|user|'group'] on ['角色名'|'用户名'|'组名']  
   
# 查看权限示例1（查看角色test的所有权限）
$ show grant role test;

# 查看权限示例2（查看用户aaa的所有权限）
$ show grant user aaa;
          
# 移除权限说明
$ revoke '权限' on [table|view] ['表名|视图名'] from [role|user|group] ['用户名|角色名'|'组名']

# 移除权限示例1（移除角色test对person表的查询权限）
$ revoke select on table person from role test;

# 移除权限示例2（移除用户aaa对person表的查询权限）
$ revoke select on table person from user aaa;
```

