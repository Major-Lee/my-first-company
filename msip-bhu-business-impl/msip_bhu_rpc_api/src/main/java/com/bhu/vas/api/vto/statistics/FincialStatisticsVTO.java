package com.bhu.vas.api.vto.statistics;

@SuppressWarnings("serial")
public class FincialStatisticsVTO implements java.io.Serializable{
	private String time;
	private double ctm;
	private double cpm;
	private double cta;
	private double cpa;
	private double ctw;
	private double cpw;
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public double getCtm() {
		return ctm;
	}
	public void setCtm(double ctm) {
		this.ctm = ctm;
	}
	public double getCpm() {
		return cpm;
	}
	public void setCpm(double cpm) {
		this.cpm = cpm;
	}
	public double getCta() {
		return cta;
	}
	public void setCta(double cta) {
		this.cta = cta;
	}
	public double getCpa() {
		return cpa;
	}
	public void setCpa(double cpa) {
		this.cpa = cpa;
	}
	public double getCtw() {
		return ctw;
	}
	public void setCtw(double ctw) {
		this.ctw = ctw;
	}
	public double getCpw() {
		return cpw;
	}
	public void setCpw(double cpw) {
		this.cpw = cpw;
	} 
	
	
}
