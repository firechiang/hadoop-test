package com.firecode.hadooptest.hbase.helloword;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.security.authentication.util.KerberosName;
import org.junit.After;
import org.junit.Before;

/**
 * HBase连接相关
 * 
 * @author JIANG
 */
public class HBaseConnectionTest {

	protected Connection connection;
	protected Admin admin;
	
	@Before
	public void before() throws IOException {
		Configuration conf = new Configuration(); 
		/*InputStream coreSite = Thread.currentThread().getContextClassLoader().getResourceAsStream("core-site.xml");
		InputStream hdfsSite = Thread.currentThread().getContextClassLoader().getResourceAsStream("hdfs-site.xml");
		InputStream hbaseSite = Thread.currentThread().getContextClassLoader().getResourceAsStream("hbase-site.xml");
        conf.addResource(coreSite);
        conf.addResource(hdfsSite);
        conf.addResource(hbaseSite);*/
		/**
		 * 我们没有使用 Kerberos 做权限控制，但是HBase默认要验证Kerberos Name，所以我们设置了一个默认值，以防止报错
		 * @see org.apache.hadoop.security.HadoopKerberosName   # 77行
		 */
		KerberosName.setRules("RULE:[1:$1] RULE:[2:$1]");
		/**
		 * 一般配置Zookeeper集群即可，HBase客户端会去Zookeeper集群拿Hbase集群信息，然后直接连接HMaster和HRegionServer写数据
		 */
		conf.set("hbase.zookeeper.quorum", "server002:2181,server003:2181,server004:2181");
		this.connection = ConnectionFactory.createConnection(conf);
		this.admin = this.connection.getAdmin();
	}
	
	@After
	public void after() throws IOException {
		if(this.admin != null) {
			this.admin.close();
		}
		if(this.connection != null) {
			this.connection.close();
		}
	}
	
}
