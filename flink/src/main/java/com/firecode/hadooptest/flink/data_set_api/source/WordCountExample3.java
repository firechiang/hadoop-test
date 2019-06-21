package com.firecode.hadooptest.flink.data_set_api.source;

import java.io.File;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.AggregateOperator;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.FlatMapOperator;
import org.apache.flink.api.java.operators.UnsortedGrouping;
import org.apache.flink.api.java.tuple.Tuple2;
import org.junit.Test;

import com.firecode.hadooptest.flink.LineSplitter;
/**
 * 加载文件数据，统计单词数量
 * @author JIANG
 */
public class WordCountExample3  {
	
	@Test
	public void test() throws Exception {
		// 获取DataSet执行上下文
		ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
		// 加载数据（注意：这个函数可以读指定文件，也可以读整合文件夹）
		DataSource<String> dataSource = env.readTextFile(System.getProperty("user.dir")+File.separator+"data"+File.separator+"word_count.txt");
		// 转换数据
		FlatMapOperator<String, Tuple2<String, Integer>> flatMap = dataSource.flatMap(new LineSplitter(" "));
		// 按照 Tuple2<String, Integer> 的Key分组
		UnsortedGrouping<Tuple2<String, Integer>> groupBy = flatMap.groupBy(0);
		// 按照 Tuple2<String, Integer> 的Value求和
		AggregateOperator<Tuple2<String, Integer>> sum = groupBy.sum(1);
		// 打印数据
		sum.print();
	}
}
