package com.bhu.vas.business.asyn.spring.model.async.group;

import com.bhu.vas.business.asyn.spring.builder.async.AsyncDTO;
import com.bhu.vas.business.asyn.spring.builder.async.AsyncMessageType;
import com.bhu.vas.business.asyn.spring.model.IDTO;

public class OperGroupDTO extends AsyncDTO implements IDTO{
	
	private int uid;
	private String message;
	private String cmds;
	private char dtoType;
	
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
	
	@Override
	public char getDtoType() {
		return dtoType;
	}
	public void setDtoType(char dtoType) {
		this.dtoType = dtoType;
	}
}
