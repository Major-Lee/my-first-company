package com.bhu.vas.business.asyn.spring.model;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class WifiDeviceAsynCmdGenerateDTO extends ActionDTO {
	
	//private int user;
	private long gid;
	private boolean dependency;
	private String mac;
	String opt;
	String subopt; 
	String extparams;
	String channel;
	String channel_taskid;
	
	@Override
	public String getActionType() {
		return ActionMessageType.WifiDeviceAsyncCMDGen.getPrefix();
	}

	/*public int getUser() {
		return user;
	}

	public void setUser(int user) {
		this.user = user;
	}*/

	public long getGid() {
		return gid;
	}

	public void setGid(long gid) {
		this.gid = gid;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
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

	public boolean isDependency() {
		return dependency;
	}

	public void setDependency(boolean dependency) {
		this.dependency = dependency;
	}

}
