package com.firecode.hadooptest.protobuf.helloword;

import com.firecode.hadooptest.protobuf.helloword.HelloWrodBuilder.HelloWord;
import com.firecode.hadooptest.protobuf.helloword.HelloWrodBuilder.HelloWord.Builder;

public class HelloWordTest {
	
	public static void main(String[] args) {
		Builder builder = HelloWrodBuilder.HelloWord.newBuilder();
		builder.setId(1);
		builder.setOpt(2);
		builder.setStr("测试名称");
		HelloWord hw = builder.build();
		System.out.println(hw);
	    System.err.println(hw.getId());
	    System.err.println(hw.getStr());
	    System.err.println(hw.getOpt());
	}

}
