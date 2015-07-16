package com.bhu.vas.api.rpc.devices.dto;

import com.bhu.vas.api.helper.WifiDeviceHelper;

public class PersistenceCMDDTO {
	private String opt;
	private String subopt;
	private String extparams;
	
	public PersistenceCMDDTO() {
	}
	
	public PersistenceCMDDTO(String opt, String subopt, String extparams) {
		this.opt = opt;
		this.subopt = subopt;
		this.extparams = extparams;
	}
	public String getOpt() {
		return opt;
	}
	public void setOpt(String opt) {
		this.opt = opt;
	}
	public String getSubopt() {
		return subopt;
	}
	public void setSubopt(String subopt) {
		this.subopt = subopt;
	}
	public String getExtparams() {
		return extparams;
	}
	public void setExtparams(String extparams) {
		this.extparams = extparams;
	}
	
	public String toKey(){
		return WifiDeviceHelper.builderKey(opt, subopt);
		//return opt.concat(subopt);
	}
}
