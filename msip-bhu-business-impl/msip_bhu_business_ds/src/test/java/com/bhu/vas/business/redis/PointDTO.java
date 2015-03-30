package com.bhu.vas.business.redis;

public class PointDTO {
	private String po;
	private String va;
	public PointDTO() {
	}
	public PointDTO(String po, String va) {
		super();
		this.po = po;
		this.va = va;
	}
	public String getPo() {
		return po;
	}
	public void setPo(String po) {
		this.po = po;
	}
	public String getVa() {
		return va;
	}
	public void setVa(String va) {
		this.va = va;
	}
	
	public String toString(){
		StringBuilder sb= new StringBuilder();
		sb.append(this.getPo()).append('~').append(this.va);
		return sb.toString();
	}
	
}
