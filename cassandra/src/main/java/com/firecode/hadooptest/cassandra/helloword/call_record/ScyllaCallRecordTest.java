package com.firecode.hadooptest.cassandra.helloword.call_record;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.DataType;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.schemabuilder.Create;
import com.datastax.driver.core.schemabuilder.SchemaBuilder;
import com.datastax.driver.mapping.DefaultNamingStrategy;
import com.datastax.driver.mapping.DefaultPropertyMapper;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingConfiguration;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.NamingConventions;
import com.datastax.driver.mapping.PropertyMapper;

/**
 * 通话记录需求测试
 * 
 * @author JIANG
 *
 */
public class ScyllaCallRecordTest {

	private Mapper<CallRecord> mapper;
	private Session session;

	@Before
	public void init() throws IOException {
		Cluster cluster = Cluster.builder().addContactPoint("192.168.83.143").withPort(9042).withCredentials("jiang", "jiang").build();
		Session session = cluster.connect();
		String keyspace = "test_test";
		// 创建 键空间
		createKeyspace(session, keyspace);
		Session connect = cluster.connect(keyspace);
		// 创建表
		createTable(connect);
		MappingManager mappingManager = mappingManager(connect);
		this.mapper = mappingManager.mapper(CallRecord.class);
		this.session = mappingManager.getSession();
	}
	
	@Test
	public void save() {
		int size = 5000001;
		for(int i=10001;i<size;i++){
			CallRecord callRecord1 = new CallRecord();
			callRecord1.setId(Long.valueOf(i+1));
			callRecord1.setDnumPhone("13143457382"+i);
			callRecord1.setType(i/2);
			callRecord1.setLength(i+1);
			callRecord1.setCreateTime(new Date());
			mapper.save(callRecord1);
		}
    }
	
	/**
	 * 根据ID查询（这里是多主键）
	 */
	@Test
	public void findById() {
		CallRecord callRecord = mapper.get("13143457382",1L);
		System.out.println(callRecord);
    }

	@Test
	public void findAll() {
        ResultSet result = session.execute(QueryBuilder.select().all().from("call_record"));
        List<CallRecord> list = mapper.map(result).all();
        System.out.println(list);
    }
	
	@Test
	public void findByDnumPhone() {
        ResultSet result = session.execute(QueryBuilder.select().all().from("call_record").where(QueryBuilder.eq("dnum_phone", "13143457382")));
        List<CallRecord> list = mapper.map(result).all();
        System.out.println(list);
    }
	
	@Test
	public void delete() {
		CallRecord callRecord = new CallRecord();
		callRecord.setId(1L);
		callRecord.setDnumPhone("13143457382");
		mapper.delete(callRecord);
    }
	
	/**
	 * 创建 键空间
	 * 
	 * @param session
	 * @param keyspace
	 * @throws IOException
	 */
	private void createKeyspace(Session session, String keyspace) throws IOException {
		Map<String, Object> replication = new HashMap<>();
		// 副本放置策略（简单的）
		replication.put("class", "SimpleStrategy");
		// 副本数量
		replication.put("replication_factor", 1);
		session.execute(SchemaBuilder.createKeyspace(keyspace).ifNotExists().with().durableWrites(true).replication(replication));
		//session.execute("USE " + keyspace);
	}

	/**
	 * 创建表
	 * 
	 * @param session
	 */
	private void createTable(Session session) {
		Create schema = SchemaBuilder.createTable("call_record")
				                     .ifNotExists()
				                     .addClusteringColumn("id", DataType.bigint())
				                     .addPartitionKey("dnum_phone", DataType.text())
				                     .addColumn("length",DataType.cint())
				                     .addColumn("type",DataType.cint())
				                     .addColumn("create_time", DataType.timestamp());
		//数据存储根据id字段倒叙排列（排序字段必须是 addClusteringColumn 字段）
		//schema.withOptions().clusteringOrder("length", Direction.ASC);
		session.execute(schema);
	}

	/**
	 * 创建映射
	 * @param session
	 * @return
	 */
	private MappingManager mappingManager(Session session) {
		/**
		 * NamingConventions.LOWER_CAMEL_CASE 下划线转驼峰
		 */
		PropertyMapper propertyMapper = new DefaultPropertyMapper().setNamingStrategy(new DefaultNamingStrategy(NamingConventions.LOWER_CAMEL_CASE, NamingConventions.LOWER_SNAKE_CASE));
		MappingConfiguration configuration = MappingConfiguration.builder().withPropertyMapper(propertyMapper).build();
		return new MappingManager(session, configuration);
	}

}
