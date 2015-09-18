package com.bhu.vas.api.rpc.agent.vto;

@SuppressWarnings("serial")
public class SettlementStatisticsVTO implements java.io.Serializable{
	private int u;
	//total settle num 所有的
	private int ts;
	//settled num 已经结清
	private int sd;
	//un settled  未结清
	private int us;
	//数据生成时间cached_at
	private String c_at;
	public int getU() {
		return u;
	}
	public void setU(int u) {
		this.u = u;
	}
	public int getTs() {
		return ts;
	}
	public void setTs(int ts) {
		this.ts = ts;
	}
	public int getSd() {
		return sd;
	}
	public void setSd(int sd) {
		this.sd = sd;
	}
	public int getUs() {
		return us;
	}
	public void setUs(int us) {
		this.us = us;
	}
	public String getC_at() {
		return c_at;
	}
	public void setC_at(String c_at) {
		this.c_at = c_at;
	}
}
