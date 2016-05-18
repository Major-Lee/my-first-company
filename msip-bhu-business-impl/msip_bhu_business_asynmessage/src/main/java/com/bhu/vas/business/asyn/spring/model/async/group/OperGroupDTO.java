package com.bhu.vas.business.asyn.spring.model.async.group;

import com.bhu.vas.business.asyn.spring.builder.async.AsyncDTO;
import com.bhu.vas.business.asyn.spring.builder.async.AsyncMessageType;

public class OperGroupDTO extends AsyncDTO{
	
	private int uid;
	private String message;
	private String cmds;
	
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

	public String getCmds() {
		return cmds;
	}

	public void setCmds(String cmds) {
		this.cmds = cmds;
	}

	@Override
	public String getAsyncType() {
		return AsyncMessageType.BatchGroupDownCmds.getPrefix();
	}
}
