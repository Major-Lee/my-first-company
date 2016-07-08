package com.bhu.vas.api.test;

import com.smartwork.msip.cores.helper.JsonHelper;

public class TestAAA {
	
	private String a;
	private String b;
	public String getA() {
		return a;
	}
	public void setA(String a) {
		this.a = a;
	}
	public String getB() {
		return b;
	}
	public void setB(String b) {
		this.b = b;
	}
	public static void main(String[] argv){
		TestAAA aa = new TestAAA();
		aa.setA("abcd&ddaf");
		aa.setB("abcd?/f");
		
		System.out.println(JsonHelper.getJSONString(aa));
		
		//String 
	}
}
