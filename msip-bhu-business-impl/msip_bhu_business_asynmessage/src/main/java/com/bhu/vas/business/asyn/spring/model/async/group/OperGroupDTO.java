package com.bhu.vas.business.asyn.spring.model.async.group;

import com.bhu.vas.business.asyn.spring.builder.async.AsyncDTO;
import com.bhu.vas.business.asyn.spring.builder.async.AsyncMessageType;

public class OperGroupDTO extends AsyncDTO{
	
	private int uid;
	private String message;
	private String opt;
	private String subopt;
	private String extparams;
	private String channel;
	private String channel_taskid;
	
	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getChannel_taskid() {
		return channel_taskid;
	}

	public void setChannel_taskid(String channel_taskid) {
		this.channel_taskid = channel_taskid;
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
