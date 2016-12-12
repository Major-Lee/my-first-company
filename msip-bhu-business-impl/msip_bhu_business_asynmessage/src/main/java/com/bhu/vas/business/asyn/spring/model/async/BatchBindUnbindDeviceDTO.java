package com.bhu.vas.business.asyn.spring.model.async;

import com.bhu.vas.business.asyn.spring.builder.async.AsyncDTO;
import com.bhu.vas.business.asyn.spring.builder.async.AsyncMessageType;
import com.bhu.vas.business.asyn.spring.model.IDTO;

public class BatchBindUnbindDeviceDTO extends AsyncDTO  implements IDTO {
	private String macs;
	private int cc;
	private String mobileno;
	private char dtoType;

	
	public char getDtoType() {
		return dtoType;
	}


	public void setDtoType(char dtoType) {
		this.dtoType = dtoType;
	}


	public String getMacs() {
		return macs;
	}


	public void setMacs(String macs) {
		this.macs = macs;
	}


	public int getCc() {
		return cc;
	}


	public void setCc(int cc) {
		this.cc = cc;
	}


	public String getMobileno() {
		return mobileno;
	}


	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}


	@Override
	public String getAsyncType() {
		return AsyncMessageType.BatchBindUnbindDevice.getPrefix();
	}
}
