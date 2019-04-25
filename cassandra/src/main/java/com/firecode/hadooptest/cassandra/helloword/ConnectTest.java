package com.firecode.hadooptest.cassandra.helloword;

import org.junit.Before;
import org.junit.Test;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

/**
 * 简单测试
 * @author JIANG
 */
public class ConnectTest {
	
	Session connect;
	
	@Before
	public void init(){
		Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1").withPort(9042).withCredentials("jiang", "jiang").build();
		//键空间的名称
		this.connect = cluster.connect("test_test");
	}
	
	@Test
	public void test(){
		ResultSet rs = connect.execute("select * from call_record");
		for(Row r:rs){
			System.out.println(r.getLong("id")+"  "+r.getString("dnum_phone") + "  "+r.getInt("length")+"  "+r.getInt("type")/*+"  "+r.getDate("create_time")*/);
		}
	}

}
