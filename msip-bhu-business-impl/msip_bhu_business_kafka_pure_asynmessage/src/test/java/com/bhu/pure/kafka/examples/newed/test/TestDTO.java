package com.bhu.pure.kafka.examples.newed.test;

@SuppressWarnings("serial")
public class TestDTO implements java.io.Serializable{
	private String name;
	private String desc;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
}
