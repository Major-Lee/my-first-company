package com.smartwork.im.utils;

public class ToDto{
	//user
	private String u;
	//group
	private String g;
	
	public ToDto(){
		
	}
	public ToDto(String u,String g){
		this.u = u;
		this.g = g;
	}
	public String getU() {
		return u;
	}
	public void setU(String u) {
		this.u = u;
	}
	public String getG() {
		return g;
	}
	public void setG(String g) {
		this.g = g;
	}
	
}