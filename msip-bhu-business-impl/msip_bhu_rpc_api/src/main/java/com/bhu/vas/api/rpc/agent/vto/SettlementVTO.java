package com.bhu.vas.api.rpc.agent.vto;

@SuppressWarnings("serial")
public class SettlementVTO implements java.io.Serializable{
	private int index;
	private int uid;
	private String org;
	//Total Revenue 除去当月之外的所有被结算金额
	private String tr;
	//unsettle Revenue 除去当月之外的未结算金额 
	private String ur;
	//settled Revenue of lastmonth
	//private String lsr;
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getOrg() {
		return org;
	}
	public void setOrg(String org) {
		this.org = org;
	}
	public String getTr() {
		return tr;
	}
	public void setTr(String tr) {
		this.tr = tr;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getUr() {
		return ur;
	}
	public void setUr(String ur) {
		this.ur = ur;
	}
	/*public String getLsr() {
		return lsr;
	}
	public void setLsr(String lsr) {
		this.lsr = lsr;
	}*/
	
	
}
