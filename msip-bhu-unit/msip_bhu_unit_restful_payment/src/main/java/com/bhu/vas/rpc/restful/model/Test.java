package com.bhu.vas.rpc.restful.model;

@SuppressWarnings("serial")
public class Test implements java.io.Serializable{
	private String param1;
	private String param2;
	public String getParam1() {
		return param1;
	}
	public void setParam1(String param1) {
		this.param1 = param1;
	}
	public String getParam2() {
		return param2;
	}
	public void setParam2(String param2) {
		this.param2 = param2;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("param1:").append(param1).append("\n param2:").append(param2);
		return sb.toString();
	}
}
