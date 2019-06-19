package com.firecode.hadooptest.flink.helloword.word_count_file;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.AggregateOperator;
import org.apache.flink.api.java.operators.FlatMapOperator;
import org.apache.flink.api.java.operators.UnsortedGrouping;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.util.Collector;
/**
 * 统计文件里面单词出现次数（计算文件数据统称：批处理）
 * @author JIANG
 */
public class WordCountMain {
	
	
	public static void main(String[] args) throws Exception {
		final ParameterTool params = ParameterTool.fromArgs(args);
		// 设置执行环境（上下文执行环境）
		final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
		// 设置全局参数
		env.getConfig().setGlobalJobParameters(params);
		// 获取到要分析的数据集（HDFS文件）
		//DataSource<String> textDataSet = env.readTextFile("hdfs://data/word_count.txt");
		// 获取到要分析的数据集（本地文件）
		//DataSource<String> textDataSet = env.readTextFile(System.getProperty("user.dir")+File.separator+"data"+File.separator+"word_count.txt");
		// 获取到要分析的数据集
		DataSet<String> textDataSet = WordCountData.getDefaultTextLineDataSet(env);
		// 以单条数据为Key，单词和次数为Value
		FlatMapOperator<String, Tuple2<String, Integer>> flatMap = textDataSet.flatMap(new Tokenizer());
		// 按照单条数据分组（0就是按照Tuple2<String, Integer> Key分组）
		UnsortedGrouping<Tuple2<String, Integer>> groupBy = flatMap.groupBy(0);
		// 求和（1就是按照Tuple2<String, Integer> Value求和）
		AggregateOperator<Tuple2<String, Integer>> sum = groupBy.sum(1);
		// 打印数据
		sum.print();
		// 数据写入磁盘
		//sum.writeAsCsv("");
		// 执行任务
		env.execute("WordCountMain");
	}
	
	
	/**
	 * 单条数据分割写入单词出现次数
	 * @author JIANG
	 */
	private static class Tokenizer implements FlatMapFunction<String, Tuple2<String, Integer>> {
		private static final long serialVersionUID = 1L;

		@Override
		public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
			// 分割单条数据
			String[] tokens = value.toLowerCase().split("\\W+");
			for (String token : tokens) {
				if (token.length() > 0) {
					out.collect(new Tuple2<>(token, 1));
				}
			}
		}
	}

}
