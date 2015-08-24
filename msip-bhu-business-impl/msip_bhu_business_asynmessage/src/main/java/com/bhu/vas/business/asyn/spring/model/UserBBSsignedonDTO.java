package com.bhu.vas.business.asyn.spring.model;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class UserBBSsignedonDTO extends ActionDTO {
	private String dt;//用户移动设备token
	private String d;//用户移动设备类型
	private String pt;//push send type
	private int countrycode;
	private String acc;
	private String secretkey;//bbs sk

	public String getDt() {
		return dt;
	}


	public void setDt(String dt) {
		this.dt = dt;
	}


	public int getCountrycode() {
		return countrycode;
	}


	public void setCountrycode(int countrycode) {
		this.countrycode = countrycode;
	}


	public String getAcc() {
		return acc;
	}


	public void setAcc(String acc) {
		this.acc = acc;
	}


	public String getSecretkey() {
		return secretkey;
	}


	public void setSecretkey(String secretkey) {
		this.secretkey = secretkey;
	}


	public String getD() {
		return d;
	}


	public void setD(String d) {
		this.d = d;
	}

	
	public String getPt() {
		return pt;
	}


	public void setPt(String pt) {
		this.pt = pt;
	}


	@Override
	public String getActionType() {
		return ActionMessageType.USERBBSSIGNEDON.getPrefix();
	}
}
