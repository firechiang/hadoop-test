package com.firecode.hadooptest.flink.domain;

public class User {
	
	private String name;
	
	private Integer age;
	
	private String job;

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

	@Override
	public String toString() {
		return "User [name=" + name + ", age=" + age + ", job=" + job + "]";
	}
}
