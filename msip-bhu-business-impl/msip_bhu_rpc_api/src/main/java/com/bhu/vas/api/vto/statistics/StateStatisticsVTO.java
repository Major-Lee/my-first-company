package com.bhu.vas.api.vto.statistics;

public class StateStatisticsVTO {
	
	//总设备数
	private long d_counts;
	//当前在线数
	private long d_online;
	//今日新增
	private long d_newInc;
	//昨日新增
	private long d_yestInc;
	//7日活跃度
	private long d_weeklive;
	//30日活跃度
	private long d_monthlive;

	//总用户数
	private long u_counts;
	//当前在线数
	private long u_online;
	//今日新增
	private long u_newInc;
	//昨日新增
	private long u_yestInc;
	//当日用户数
	private long u_current;
	
	public long getD_counts() {
		return d_counts;
	}
	public void setD_counts(long d_counts) {
		this.d_counts = d_counts;
	}
	public long getD_online() {
		return d_online;
	}
	public void setD_online(long d_online) {
		this.d_online = d_online;
	}
	public long getD_newInc() {
		return d_newInc;
	}
	public void setD_newInc(long d_newInc) {
		this.d_newInc = d_newInc;
	}
	public long getD_yestInc() {
		return d_yestInc;
	}
	public void setD_yestInc(long d_yestInc) {
		this.d_yestInc = d_yestInc;
	}
	public long getD_weeklive() {
		return d_weeklive;
	}
	public void setD_weeklive(long d_weeklive) {
		this.d_weeklive = d_weeklive;
	}
	public long getD_monthlive() {
		return d_monthlive;
	}
	public void setD_monthlive(long d_monthlive) {
		this.d_monthlive = d_monthlive;
	}
	public long getU_counts() {
		return u_counts;
	}
	public void setU_counts(long u_counts) {
		this.u_counts = u_counts;
	}
	public long getU_online() {
		return u_online;
	}
	public void setU_online(long u_online) {
		this.u_online = u_online;
	}
	public long getU_newInc() {
		return u_newInc;
	}
	public void setU_newInc(long u_newInc) {
		this.u_newInc = u_newInc;
	}
	public long getU_yestInc() {
		return u_yestInc;
	}
	public void setU_yestInc(long u_yestInc) {
		this.u_yestInc = u_yestInc;
	}
	public long getU_current() {
		return u_current;
	}
	public void setU_current(long u_current) {
		this.u_current = u_current;
	}
	
}
