package com.firecode.hadooptest.protobuf.callrecord;

import com.firecode.hadooptest.protobuf.callrecord.CallRecordDetail.CallRecord;
import com.firecode.hadooptest.protobuf.callrecord.CallRecordDetail.DayCallRecord;

public class CallRecordDetailTest {
	
	public static void main(String[] args) {
		DayCallRecord.Builder listBuilder = CallRecordDetail.DayCallRecord.newBuilder();
		CallRecord.Builder entityBuilder = CallRecordDetail.CallRecord.newBuilder();
		entityBuilder.setDate("2019-01-12 12:12:12");
		entityBuilder.setLength("41");
		entityBuilder.setType("1");
		entityBuilder.setDnumPhone("121");
		listBuilder.addDayCallRecord(entityBuilder);
		DayCallRecord dayCallRecord = listBuilder.build();
		System.err.println(dayCallRecord);
	}
	
}
