package com.firecode.hadooptest.flink.data_set_api.senior;

import java.io.File;

import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.accumulators.LongCounter;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.MapOperator;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.FileSystem.WriteMode;

/**
 * 自定义参数 和 计数器简单使用
 * @author JIANG
 */
public class Counter {
	
	public static void main(String[] args) throws Exception {
		String counterName = "test-counter";
		ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
		DataSource<String> dataSource = env.fromElements("maomao","tiantian","wen","juan");
		// 设置并行度
		DataSource<String> setParallelism = dataSource.setParallelism(2);
		MapOperator<String, String> map = setParallelism.map(new RichMapFunction<String, String>() {
			private static final long serialVersionUID = 1L;
			// 计数器
			private LongCounter counter = new LongCounter();
			/**
			 * 任务开始之前调用
			 */
			@Override
			public void open(Configuration parameters) throws Exception {
				// 注册自定义参数（这里我们设置的是计数器）
				getRuntimeContext().addAccumulator(counterName, counter);
			}
			
			@Override
			public String map(String value) throws Exception {
				// 累加计数器
				counter.add(1);
				return value;
			}
		});
		// 数据要先写出去，否则无法得到 JobExecutionResult（任务执行结果）数据
		map.writeAsText(System.getProperty("user.dir")+File.separator+"data",WriteMode.OVERWRITE);
		JobExecutionResult execute = env.execute("Counter");
		Long counter = execute.getAccumulatorResult(counterName);
		// 打印计数
		System.err.println(counter);
	}
}
