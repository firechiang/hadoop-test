package com.firecode.hadooptest.flink.table_sql_api;

import org.apache.flink.streaming.api.datastream.AllWindowedStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;

import com.firecode.hadooptest.flink.data_stream_api.source.function.CustomRichParallelSourceFunction2;
import com.firecode.hadooptest.flink.domain.User;

/**
 * 使用SQL API进行流处理（实时计算）
 * @author JIANG
 */
public class DataStreamTableApiTest {
	
	public static void main(String[] args) throws Exception {
		// 获取执行上下文
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		// 添加数据源
		DataStreamSource<User> dataSource = env.addSource(new CustomRichParallelSourceFunction2());
		// 创建SQL执行上下文
		StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
		// 基于数据源建立Table
		Table table = tableEnv.fromDataStream(dataSource);
		// 注册表
		tableEnv.registerTable("test_user", table);
		// 查询数据并创建Table
		Table resultTable = tableEnv.sqlQuery("select * from test_user where age > 0");
		// 将Table数据转换为DataStream
		DataStream<Row> dataStream = tableEnv.toAppendStream(resultTable, Row.class);
		// 每5秒计算处理一次
		AllWindowedStream<Row, TimeWindow> timeWindowAll = dataStream.timeWindowAll(Time.seconds(5));
		// 将Row（行）转换成User数据（注意：可以直接在这里将数据输出）
		SingleOutputStreamOperator<User> apply = timeWindowAll.apply(new AllWindowFunction<Row, User, TimeWindow>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void apply(TimeWindow window, Iterable<Row> values, Collector<User> out) throws Exception {
				for(Row r:values){
					out.collect(new User(r.getField(0).toString(),Integer.valueOf(r.getField(1).toString()),r.getField(2).toString()));
				}
			}
		});
		// 打印数据
		apply.print();
		env.execute("DataStreamTableApiTest");
	}
}
