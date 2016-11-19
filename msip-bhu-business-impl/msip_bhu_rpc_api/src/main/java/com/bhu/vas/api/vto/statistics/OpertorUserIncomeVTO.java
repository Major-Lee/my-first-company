package com.bhu.vas.api.vto.statistics;

@SuppressWarnings("serial")
public class OpertorUserIncomeVTO implements java.io.Serializable{
	private int uid;
	private String curMonIncome;
	private String lastMonIncome;
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getCurMonIncome() {
		return curMonIncome;
	}
	public void setCurMonIncome(String curMonIncome) {
		this.curMonIncome = curMonIncome;
	}
	public String getLastMonIncome() {
		return lastMonIncome;
	}
	public void setLastMonIncome(String lastMonIncome) {
		this.lastMonIncome = lastMonIncome;
	}
	
}
