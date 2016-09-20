package com.bhu.vas.api.vto.statistics;

import java.io.Serializable;

@SuppressWarnings("serial")
public class RankingCardInfoVTO implements Serializable{
	private int rank;
	private String income;
	private int age;
	//头像
	private String avatar;
	//用户签名
	private String memo;
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public String getIncome() {
		return income;
	}
	public void setIncome(String income) {
		this.income = income;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	
}
