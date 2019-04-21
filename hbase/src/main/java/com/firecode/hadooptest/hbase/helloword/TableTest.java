package com.firecode.hadooptest.hbase.helloword;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptor;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;

/**
 * HBase表相关操作
 * @author JIANG
 */
public class TableTest extends HBaseConnectionTest {
	
	/**
	 * 创建test_test表
	 * @throws IOException
	 */
	@Test
	public void create() throws IOException {
		TableName tableName = TableName.valueOf("test_test");
		//表是否存在
		if(admin.tableExists(tableName)) {
			//先禁用表才能删除
			admin.disableTable(tableName);
			//删除表
			admin.deleteTable(tableName);
		}
		//列族
		ColumnFamilyDescriptor family = ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes("cf")).build();
		//builder 表
		TableDescriptorBuilder tableBuilder = TableDescriptorBuilder.newBuilder(tableName);
		tableBuilder.setColumnFamily(family);
		TableDescriptor desc = tableBuilder.build();
		admin.createTable(desc);
	}
	
	/**
	 * 插入数据
	 * @throws IOException
	 */
	@Test
	public void insert() throws IOException {
		TableName tableName = TableName.valueOf("test_test");
		Table table = connection.getTable(tableName);
		byte[] rowKey = Bytes.toBytes("1234567890");
		Put data = new Put(rowKey);
		/**
		 * cf=列族
		 * id=列
		 * 1=列值
		 */
		data.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("id"), Bytes.toBytes(1));
		data.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("name"), Bytes.toBytes("猫猫e"));
		data.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("age"), Bytes.toBytes(22));
		//添加数据
		table.put(data);
	}
	
	/**
	 * 根据RowKey获取数据
	 * @throws IOException 
	 */
	@Test
	public void getCellDataByLast() throws IOException {
		TableName tableName = TableName.valueOf("test_test");
		Table table = connection.getTable(tableName);
		byte[] rowKey = Bytes.toBytes("1234567890");
		Get get = new Get(rowKey);
		Result rs = table.get(get);
		//获取列族为cf，列名为name的值
		Cell cell = rs.getColumnLatestCell(Bytes.toBytes("cf"), Bytes.toBytes("name"));
		System.err.println(Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
		
		List<Cell> columnCells = rs.getColumnCells(Bytes.toBytes("cf"), Bytes.toBytes("name"));
		for(Cell c:columnCells) {
			System.out.println(Bytes.toString(c.getValueArray(), c.getValueOffset(), c.getValueLength()));
		}
	}
	
    /**
     * 根据RowKey,列族,列名获取数据（一般都是这样用的）
     * @throws IOException 
     */
	@Test
    public void getCellDataByVersion() throws IOException {
		TableName tableName = TableName.valueOf("test_test");
		Table table = connection.getTable(tableName);
		byte[] rowKey = Bytes.toBytes("1234567890");
		Get get = new Get(rowKey);
    	//返回数据
        List<String> result = new ArrayList<>();

        get.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("name"));
        //读取多少个版本
        //get.readVersions(2);
        Result hTableResult = table.get(get);
        if (hTableResult != null && !hTableResult.isEmpty()) {
            for (Cell cell : hTableResult.listCells()) {
                result.add(Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
            }
        }
        System.err.println(result);
    }
	
	
    /**
     * 用scan获取某一个列族的所有数据数据
     * @throws IOException 
     */
	@Test
    public void getCellDataByScan() throws IOException {
		TableName tableName = TableName.valueOf("test_test");
		Table table = connection.getTable(tableName);
		Scan scan = new Scan();
		byte[] family = Bytes.toBytes("cf");
		//开始行（指的是Row Key）
		scan.withStartRow(Bytes.toBytes("1234567890"));
		//结束行（指的是Row Key）
		scan.withStopRow(Bytes.toBytes("1234567891"));
		scan.addFamily(family);
        ResultScanner scanner = table.getScanner(scan);
        for(Iterator<Result> it=scanner.iterator();it.hasNext();) {
        	Result rs = it.next();
    		List<Cell> listCells = rs.listCells();
    		for(Cell cell:listCells) {
    			System.err.println(Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
    		}
        }
    }
}
