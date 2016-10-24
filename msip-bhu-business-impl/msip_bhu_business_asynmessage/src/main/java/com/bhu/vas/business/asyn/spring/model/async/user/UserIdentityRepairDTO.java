package com.bhu.vas.business.asyn.spring.model.async.user;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.async.AsyncDTO;
import com.bhu.vas.business.asyn.spring.builder.async.AsyncMessageType;

public class UserIdentityRepairDTO extends AsyncDTO{

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
	public String getAsyncType() {
		return AsyncMessageType.BatchUserIdentityRepair.getPrefix();
	}
}
