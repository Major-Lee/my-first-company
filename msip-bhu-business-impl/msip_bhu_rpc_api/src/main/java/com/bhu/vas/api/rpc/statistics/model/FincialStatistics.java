package com.bhu.vas.api.rpc.statistics.model;

import com.smartwork.msip.cores.orm.model.BaseStringModel;

@SuppressWarnings("serial")
public class FincialStatistics extends BaseStringModel{
	//private String time;
	private float ctm;
	private float cpm;
	private float cta;
	private float cpa;
	private float ctw;
	private float cpw;
	/*public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}*/
	public float getCtm() {
		return ctm;
	}
	public void setCtm(float ctm) {
		this.ctm = ctm;
	}
	public float getCpm() {
		return cpm;
	}
	public void setCpm(float cpm) {
		this.cpm = cpm;
	}
	public float getCta() {
		return cta;
	}
	public void setCta(float cta) {
		this.cta = cta;
	}
	public float getCpa() {
		return cpa;
	}
	public void setCpa(float cpa) {
		this.cpa = cpa;
	}
	public float getCtw() {
		return ctw;
	}
	public void setCtw(float ctw) {
		this.ctw = ctw;
	}
	public float getCpw() {
		return cpw;
	}
	public void setCpw(float cpw) {
		this.cpw = cpw;
	}
	
}
