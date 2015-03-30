package com.bhu.vas.api.dto.redis;

/**
 * 曲线数据每个点的描述dto
 * @author Edmond
 *
 */
public class PointDTO {
	private String po;
	private int sq;
	private String va;
	public PointDTO() {
	}
	public PointDTO(String po, String va,int sq) {
		super();
		this.po = po;
		this.va = va;
		this.sq = sq;
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
	
	public int getSq() {
		return sq;
	}
	public void setSq(int sq) {
		this.sq = sq;
	}
	public String toString(){
		StringBuilder sb= new StringBuilder();
		sb.append(this.getPo()).append('~').append(this.va);
		return sb.toString();
	}
	
}
