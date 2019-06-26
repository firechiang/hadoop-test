package com.firecode.hadooptest.flink.data_stream_api.source.function;

import java.util.concurrent.TimeUnit;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

/**
 * 自定义数据源（注意：继承 RichParallelSourceFunction 类的数据源是并行处理的，而且还可自定义open和clode逻辑）
 * 输出 0-999 的数据到 Flink
 */
public class CustomRichParallelSourceFunction extends  RichParallelSourceFunction<Long> {
	
	private static final long serialVersionUID = 1L;
	private long count = 0L;
	private volatile boolean isRunning = true;

	@Override
	public void run(SourceContext<Long> ctx) throws InterruptedException {
		while (isRunning && count < 1000) {
			// 每2秒输出一次
			TimeUnit.SECONDS.sleep(1);
			// this synchronized block ensures that state checkpointing,
			// internal state updates and emission of elements are an atomic
			// operation
			synchronized (ctx.getCheckpointLock()) {
				// 写出数据
				ctx.collect(count);
				count++;
			}
		}
	}
	
	@Override
	public void cancel() {
		isRunning = false;
	}

	@Override
	public void open(Configuration parameters) throws Exception {
		super.open(parameters);
	}

	@Override
	public void close() throws Exception {
		super.close();
	}
	
	
}
