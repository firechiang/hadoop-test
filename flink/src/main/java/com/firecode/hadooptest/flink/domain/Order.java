package com.firecode.hadooptest.flink.domain;

import java.util.Date;

public class Order {
	
	private String name;
	
	private Date createTime;
	

	public Order() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Order(String name, Date createTime) {
		super();
		this.name = name;
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "Order [name=" + name + ", createTime=" + createTime + "]";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	public boolean hasWatermarkMarker(){
		
		return true;
	}
}
