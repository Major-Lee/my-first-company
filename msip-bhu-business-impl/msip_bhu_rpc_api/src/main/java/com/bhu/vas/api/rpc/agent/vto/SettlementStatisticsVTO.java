package com.bhu.vas.api.rpc.agent.vto;

@SuppressWarnings("serial")
public class SettlementStatisticsVTO implements java.io.Serializable{
	private int u;
	//total settle num 所有的
	private long ts;
	//settled num 已经结清
	private long sd;
	//un settled  未结清
	private long us;
	//数据生成时间cached_at
	private String c_at;
	public int getU() {
		return u;
	}
	public void setU(int u) {
		this.u = u;
	}
	public long getTs() {
		return ts;
	}
	public void setTs(long ts) {
		this.ts = ts;
	}
	public long getSd() {
		return sd;
	}
	public void setSd(long sd) {
		this.sd = sd;
	}
	public long getUs() {
		return us;
	}
	public void setUs(long us) {
		this.us = us;
	}
	public String getC_at() {
		return c_at;
	}
	public void setC_at(String c_at) {
		this.c_at = c_at;
	}
}
