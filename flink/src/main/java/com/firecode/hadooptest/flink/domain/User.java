package com.firecode.hadooptest.flink.domain;

import java.util.Date;

public class User {
	
	private String name;
	
	private Integer age;
	
	private String job;
	
	private Date createTime;
	
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

	public User(String name, Integer age, String job) {
		super();
		this.name = name;
		this.age = age;
		this.job = job;
	}
	
	public User(String name, Integer age, String job, Date createTime) {
		super();
		this.name = name;
		this.age = age;
		this.job = job;
		this.createTime = createTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}
	
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "User [name=" + name + ", age=" + age + ", job=" + job + ", createTime=" + createTime + "]";
	}
}
