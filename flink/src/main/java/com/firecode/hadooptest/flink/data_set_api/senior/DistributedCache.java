package com.firecode.hadooptest.flink.data_set_api.senior;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.MapOperator;
import org.apache.flink.configuration.Configuration;

/**
 * 分布式缓存文件简单使用（大文件不建议使用，以免造成内存溢出）
 * @author JIANG
 */
public class DistributedCache {
	
	public static void main(String[] args) throws Exception {
		ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
		String cacheName = "word_count-cache-file";
		// 缓存一个文件数据，缓存Key是 word_count-cache-file
		env.registerCachedFile(System.getProperty("user.dir")+File.separator+"data"+File.separator+"word_count.txt",cacheName);
		DataSource<String> dataSource = env.fromElements("maomao","tiantian","wen","juan");
		// 设置并行度
		DataSource<String> setParallelism = dataSource.setParallelism(2);
		MapOperator<String, String> map = setParallelism.map(new RichMapFunction<String, String>() {
			private static final long serialVersionUID = 1L;
			/**
			 * 任务开始之前调用
			 */
			@Override
			public void open(Configuration parameters) throws Exception {
				// 获取缓存文件
				File file = getRuntimeContext().getDistributedCache().getFile(cacheName);
				List<String> readAllLines = Files.readAllLines(file.toPath());
				System.err.println("获取到缓存文件数据："+readAllLines);
			}
			@Override
			public String map(String value) throws Exception {
				return value;
			}
		});
		map.print();
	}

}
