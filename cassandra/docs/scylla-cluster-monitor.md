#### 一、预先准备环境
```bash
$ wget https://github.com/scylladb/scylla-grafana-monitoring/archive/scylla-monitoring-2.3.tar.gz
$ wget https://github.com/prometheus/alertmanager/releases/download/v0.17.0/alertmanager-0.17.0.linux-amd64.tar.gz
$ wget https://github.com/prometheus/prometheus/releases/download/v2.9.2/prometheus-2.9.2.linux-amd64.tar.gz
$ tar -zxvf scylla-monitoring-2.3.tar.gz -C ../              # 解压 scylla-monitoring-2.3.tar.gz 到上层目录
$ tar -zxvf alertmanager-0.17.0.linux-amd64.tar.gz -C ../    # 解压 alertmanager-0.17.0.linux-amd64.tar.gz 到上层目录
$ tar -zxvf prometheus-2.9.2.linux-amd64.tar.gz -C ../       # 解压 prometheus-2.9.2.linux-amd64.tar.gz 到上层目录
```

#### 二、移动配置文件
```bash
# 将scylla-monitoring的rule_config.yml文件内容替换掉alertmanager的alertmanager.yml的文件内容(注意：它会提示是否替换，请填写 yes)
$ cp -p /home/scylla-grafana-monitoring-scylla-monitoring-2.3/prometheus/rule_config.yml /home/alertmanager-0.17.0.linux-amd64/alertmanager.yml

# 复制scylla-monitoring的prometheus.yml.template文件生成新的prometheus.yml文件
$ cp /home/scylla-grafana-monitoring-scylla-monitoring-2.3/prometheus/prometheus.yml.template /home/scylla-grafana-monitoring-scylla-monitoring-2.3/prometheus/prometheus.yml

# 复制scylla-monitoring的prometheus目录下所有yml配置文件到prometheus安装目录(注意：它会提示是否替换，请填写 yes)
$ cp -p /home/scylla-grafana-monitoring-scylla-monitoring-2.3/prometheus/*.yml /home/prometheus-2.9.2.linux-amd64
```

#### 三、修改[vi /home/prometheus-2.9.2.linux-amd64/scylla_servers.yml]，(targets(节点地址)，labels(集群的名字和数据中心的名字))
```bash
- targets:
       - server06:9180
       - server07:9180
       - server08:9180
  labels:
       cluster: myScyllaCluster
       dc: datacenter1
```

#### 四、修改[vi /home/prometheus-2.9.2.linux-amd64/node_exporter_servers.yml]，这个文件需手动创建(targets(节点地址)，labels(集群的名字和数据中心的名字)，多个集群和数据中心可使用多个targets和labels配置)
```bash
- targets:
       - server06:9100
       - server07:9100
       - server08:9100
  labels:
       cluster: myScyllaCluster
       dc: datacenter1
```

#### 五、修改[vi /home/prometheus-2.9.2.linux-amd64/scylla_manager_servers.yml]，(这个文件需手动创建)
```bash
- targets:
  - 127.0.0.1:56090
```

#### 六、修改[vi /home/prometheus-2.9.2.linux-amd64/prometheus.yml]
```bash
# 修改alertmanager(报警器)服务所在地址
alerting:
  alertmanagers:
  - static_configs:
    - targets:
        - 127.0.0.1:9093
        
# 配置scylla集群信息配置文件地址    
scrape_configs:
- job_name: scylla
  honor_labels: false
  file_sd_configs:
    - files:
      - /home/prometheus-2.9.2.linux-amd64/scylla_servers.yml  
      
# 配置scylla集群节点监控服务信息配置文件地址    
- job_name: node_exporter
  honor_labels: false
  file_sd_configs:
    - files:
      - /home/prometheus-2.9.2.linux-amd64/node_exporter_servers.yml  
      
# 配置scylla_manager集群信息配置文件地址
- job_name: scylla_manager
  honor_labels: false
  file_sd_configs:
    - files:
      - /home/prometheus-2.9.2.linux-amd64/scylla_manager_servers.yml            
```

#### 七、安装Grafana(需要5.0.0或更高版本)
```bash
$ cd /home/tools
$ wget https://dl.grafana.com/oss/release/grafana-6.1.6-1.x86_64.rpm
$ sudo yum localinstall grafana-6.1.6-1.x86_64.rpm
```

#### 八、配置Scylla-Grafana
```bash
$ cd /home/scylla-grafana-monitoring-scylla-monitoring-2.3
$ sudo cp -r grafana/plugins /var/lib/grafana/                   # 拷贝插件到 Grafana
$ ./generate-dashboards.sh -v 3.0 -M 1.3                         # 生成仪表板配置(scylla 3.0和scylla manager 1.3)
$ sudo cp grafana/provisioning/dashboards/load.* /etc/grafana/provisioning/dashboards/
$ sudo mkdir -p /var/lib/grafana/dashboards
$ sudo cp -r grafana/build/* /var/lib/grafana/dashboards
$ sudo cp grafana/datasource.yml /etc/grafana/provisioning/datasources/
```

#### 九、修改[vi /etc/grafana/provisioning/datasources/datasource.yml]设置alertmanager(报警服务地址地址)和prometheus(监控服务服务)
```bash
apiVersion: 1
datasources:
- name: prometheus
  type: prometheus
  url: http://server08:9090
  access: proxy
  basicAuth: false

- name: alertmanager
  type: camptocamp-prometheus-alertmanager-datasource
  orgId: 1
  typeLogoUrl: public/img/icn-datasource.svg
  access: proxy
  url: http://server08:9093
  password: 
  user: 
  database: 
  basicAuth: 
  isDefault: 
  jsonData:
    severity_critical: '4'
    severity_high: '3'
    severity_warning: '2'
    severity_info: '1'
```

#### 十、创建[sudo mkdir /home/prometheus-2.9.2.linux-amd64/data]，(监控数据目录)
#### 十一、启动alertmanager(报警)服务（& 表示后台启动，http://192.168.83.145:9093）
```bash
$ cd /home/alertmanager-0.17.0.linux-amd64
$ ./alertmanager &
```

#### 十二、启动prometheus(监控)服务（& 表示后台启动，http://192.168.83.145:9090）
```bash
$ cd /home/prometheus-2.9.2.linux-amd64
$ sudo ./prometheus --config.file=prometheus.yml --storage.tsdb.path /home/prometheus-2.9.2.linux-amd64/data &
```

#### 十三、启动grafana(仪表板)服务（http://192.168.83.145:3000）
```bash
$ sudo service grafana-server start
```
