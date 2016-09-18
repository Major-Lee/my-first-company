package com.bhu.vas.business.asyn.spring.model;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

@SuppressWarnings("serial")
public class WifiDeviceBatchModifyDTO  extends ActionDTO {
	private String macs;
	private String opt;
	private String subopt;
	private String channel;
	private String channel_taskid;
	private String extparams;
	
	@Override
	public String getActionType() {
		return ActionMessageType.WifiDevicesBatchModify.getPrefix();
	}

	
	
	public String getExtparams() {
		return extparams;
	}
	public void setExtparams(String extparams) {
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
	public String getMacs() {
		return macs;
	}
	public void setMacs(String mac) {
		this.macs = mac;
	}

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
	
}
