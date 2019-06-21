package com.firecode.hadooptest.flink.data_set_api.source;

import java.io.File;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.junit.Test;
/**
 * 加载压缩文件数据（支持的格式：https://ci.apache.org/projects/flink/flink-docs-release-1.8/dev/batch/#read-compressed-files）
 * @author JIANG
 */
public class CompressLoadFileExample  {
	
	@Test
	public void test() throws Exception {
		// 获取DataSet执行上下文
		ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
		// 加载数据
		DataSource<String> dataSource = env.readTextFile(System.getProperty("user.dir")+File.separator+"data"+File.separator+"word_count.gz");
		dataSource.print();
	}
}
