package com.bhu.vas.api.rpc.statistics.model;

import com.bhu.vas.api.rpc.sequence.helper.IRedisSequenceGenable;
import com.smartwork.msip.cores.orm.model.BaseStringModel;

@SuppressWarnings("serial")
public class FincialStatistics extends BaseStringModel implements IRedisSequenceGenable{
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
	@Override
	public void setSequenceKey(Long key) {
		// TODO Auto-generated method stub
		
	}
	
}
