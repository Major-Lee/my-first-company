package com.bhu.vas.business.asyn.spring.model.async.group;

import com.bhu.vas.business.asyn.spring.builder.async.AsyncDTO;
import com.bhu.vas.business.asyn.spring.builder.async.AsyncMessageType;

public class OperGroupDTO extends AsyncDTO{
	
	private int uid;
	private String message;
	private String opt;
	private String subopt;
	private String extparams;
	
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

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String getAsyncType() {
		return AsyncMessageType.BatchGroupDownCmds.getPrefix();
	}
}
