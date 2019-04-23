package com.firecode.hadooptest.flume.helloword;

import java.util.HashMap;
import java.util.Map;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.FlumeException;
import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.event.SimpleEvent;
import org.apache.flume.source.AbstractEventDrivenSource;

/**
 * 自定义数据源
 * 
 * @author JIANG
 */
public class MySource extends AbstractEventDrivenSource {

	@Override
	public synchronized void doStart() {
		ChannelProcessor cp = this.getChannelProcessor();
		Map<String, String> map = new HashMap<>();
		Event e = null;
		map.put("owner", "xupc");
		map.put("date", "1016-02-12");
		for (int i = 0; i < 100; i++) {
			e = new SimpleEvent();
			e.setBody(("1" + i).getBytes());
			e.setHeaders(map);
			cp.processEvent(e);
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
