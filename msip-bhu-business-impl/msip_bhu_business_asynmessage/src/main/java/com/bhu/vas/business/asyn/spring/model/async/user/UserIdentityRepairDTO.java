package com.bhu.vas.business.asyn.spring.model.async.user;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.async.AsyncMessageType;

public class UserIdentityRepairDTO extends ActionDTO{

	private String hdmac;
	private String mobileno;
	
	public String getMobileno() {
		return mobileno;
	}

	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}

	public String getHdmac() {
		return hdmac;
	}

	public void setHdmac(String hdmac) {
		this.hdmac = hdmac;
	}

	@Override
	public String getActionType() {
		return AsyncMessageType.BatchUserIdentityRepair.getPrefix();
	}
	
}
