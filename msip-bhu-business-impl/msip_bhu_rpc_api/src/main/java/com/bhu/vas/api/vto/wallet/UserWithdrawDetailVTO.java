package com.bhu.vas.api.vto.wallet;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserWithdrawDetailVTO implements Serializable{
	private UserWithdrawApplyVTO withDrawApplydetail;
	private String describe;
	
	public UserWithdrawApplyVTO getWithDrawApplydetail() {
		return withDrawApplydetail;
	}
	public void setWithDrawApplydetail(UserWithdrawApplyVTO withDrawApplydetail) {
		this.withDrawApplydetail = withDrawApplydetail;
	}
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	
	
}
