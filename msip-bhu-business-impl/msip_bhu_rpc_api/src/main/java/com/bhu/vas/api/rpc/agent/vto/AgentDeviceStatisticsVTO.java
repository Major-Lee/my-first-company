package com.bhu.vas.api.rpc.agent.vto;

@SuppressWarnings("serial")
public class AgentDeviceStatisticsVTO implements java.io.Serializable{
	private int u;
	//total devices
	private int td;
	//online devices
	private int od;
	//offline devices；
	private int fd;
	//数据生成时间cached_at
	private String c_at;
	public int getTd() {
		return td;
	}
	public void setTd(int td) {
		this.td = td;
	}
	public int getOd() {
		return od;
	}
	public void setOd(int od) {
		this.od = od;
	}
	public int getFd() {
		return fd;
	}
	public void setFd(int fd) {
		this.fd = fd;
	}
	public String getC_at() {
		return c_at;
	}
	public void setC_at(String c_at) {
		this.c_at = c_at;
	}
	public int getU() {
		return u;
	}
	public void setU(int u) {
		this.u = u;
	}
	
}
