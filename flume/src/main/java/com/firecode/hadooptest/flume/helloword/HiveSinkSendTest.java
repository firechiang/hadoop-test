package com.firecode.hadooptest.flume.helloword;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.FlumeException;
import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.event.SimpleEvent;
import org.apache.flume.source.AbstractEventDrivenSource;

/**
 * 自定义数据源，发送到Hive Sink
 * @author JIANG
 */
public class HiveSinkSendTest extends AbstractEventDrivenSource {
	
	private static final  Log LOG = LogFactory.getLog(HiveSinkSendTest.class);

	@Override
	public synchronized void doStart() {
		ChannelProcessor cp = this.getChannelProcessor();
		Map<String, String> header = new HashMap<>();
		header.put("country", "india");
		header.put("times", "2019-01-02");
		for (int i = 51; i < 100; i++) {
			Event e1 = new SimpleEvent();
			e1.setHeaders(header);
			/**
			 * 数据默认以 制表符 分割（就是Tab键）
			 */
			String data = (i+1)+"	helloword"+i+"	23";
			e1.setBody(data.getBytes());
			LOG.info("正在发送数据："+data);
			cp.processEvent(e1);
		}
		System.err.println("自定义数据源。");
	}

	@Override
	protected void doConfigure(Context context) throws FlumeException {
		
	}

	@Override
	protected void doStop() throws FlumeException {
		
	}

}
