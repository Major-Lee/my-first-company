package com.bhu.vas.api.rpc.portrait.model;

import com.smartwork.msip.cores.orm.model.BaseStringModel;
@SuppressWarnings("serial")
public class HandsetPortrait extends BaseStringModel{// implements ISequenceGenable,TableSplitable<Integer>{
	
	private String mobileno;
	private String os;
	public String getMobileno() {
		return mobileno;
	}
	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}
	public String getOs() {
		return os;
	}
	public void setOs(String os) {
		this.os = os;
	}

}
