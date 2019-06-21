package com.firecode.hadooptest.flink.data_set_api.sink;

import java.io.File;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.AggregateOperator;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.FlatMapOperator;
import org.apache.flink.api.java.operators.UnsortedGrouping;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.core.fs.FileSystem.WriteMode;

import com.firecode.hadooptest.flink.LineSplitter;

/**
 * 将结果集写出到文件
 * @author JIANG
 */
public class writeAsTextOrCvs {
	
	
	public static void main(String[] args) throws Exception {
		// 获取DataSet执行上下文
		ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
		// 加载数据
		DataSource<String> dataSource = env.fromElements("Who's there?","I think I hear them. Stand, ho! Who's there?");
		// 转换数据
		FlatMapOperator<String, Tuple2<String, Integer>> flatMap = dataSource.flatMap(new LineSplitter(" "));
		// 按照 Tuple2<String, Integer> 的Key分组
		UnsortedGrouping<Tuple2<String, Integer>> groupBy = flatMap.groupBy(0);
		// 按照 Tuple2<String, Integer> 的Value求和
		AggregateOperator<Tuple2<String, Integer>> sum = groupBy.sum(1);
		// 写出txt格式数据（WriteMode.OVERWRITE 就是覆盖原有文件）
		//sum.writeAsText(System.getProperty("user.dir")+File.separator+"data",WriteMode.OVERWRITE);
		// 写出csv格式数据（WriteMode.OVERWRITE 就是覆盖原有文件）
		sum.writeAsCsv(System.getProperty("user.dir")+File.separator+"data",WriteMode.OVERWRITE);
		env.execute("writeAsTextOrCvs");
	}
}
