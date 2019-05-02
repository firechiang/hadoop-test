package com.firecode.hadooptest.flume.sink;

import org.apache.flume.Channel;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.Transaction;
import org.apache.flume.sink.AbstractSink;

/**
 * 自定义Sink
 * @author JIANG
 *
 */
public class MySink extends AbstractSink {

	@Override
	public Status process() throws EventDeliveryException {
		Channel channel = getChannel();
		//获取事务
		Transaction transaction = channel.getTransaction();
		try{
			//开启事务
			transaction.begin();
			Event event = channel.take();
			if(null != event){
				String str = new String(event.getBody());
				System.err.println("我收到了数据: "+str);
			}
			//提交事务
			transaction.commit();
			return Status.READY;
		}catch(Exception e){
			transaction.rollback();
		}finally{
			transaction.close();
		}
		return Status.BACKOFF;
	}

}
