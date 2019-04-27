package com.firecode.hadooptest.protobuf;

import com.firecode.hadooptest.protobuf.CallRecordBuilder.CallRecord;
import com.google.protobuf.InvalidProtocolBufferException;

public class CallRecordTest {
	
	public static void main(String[] args) throws InvalidProtocolBufferException {
		CallRecordBuilder.CallRecord.Builder builder = CallRecordBuilder.CallRecord.newBuilder();
		builder.setDate("2019-01-02");
		builder.setDnum("123456789");
		builder.setLength("42");
		builder.setType("1");
		CallRecord cr = builder.build();
		System.err.println(cr.toString());
		System.out.println(cr.getDnum());
		System.err.println("对象转字节数组："+cr.toByteArray());
		//字节数组序列化为对象
		//CallRecord parseFrom = CallRecordBuilder.CallRecord.parseFrom(cr.toByteArray());
	}
}
