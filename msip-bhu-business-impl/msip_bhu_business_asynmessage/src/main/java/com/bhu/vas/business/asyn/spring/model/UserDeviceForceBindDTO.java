package com.bhu.vas.business.asyn.spring.model;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class UserDeviceForceBindDTO extends ActionDTO {
	//被替换解绑的用户id
	private Integer old_uid;
	//设备软件版本号
	private String orig_swver;
	
	public Integer getOld_uid() {
		return old_uid;
	}

	public void setOld_uid(Integer old_uid) {
		this.old_uid = old_uid;
	}
	
	public String getOrig_swver() {
		return orig_swver;
	}

	public void setOrig_swver(String orig_swver) {
		this.orig_swver = orig_swver;
	}

	@Override
	public String getActionType() {
		return ActionMessageType.USERDEVICEFORCEBIND.getPrefix();
	}
}
