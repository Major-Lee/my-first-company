package com.bhu.vas.business.asyn.spring.model.async.device;

import java.util.List;

import com.bhu.vas.business.asyn.spring.builder.async.AsyncDTO;
import com.bhu.vas.business.asyn.spring.builder.async.AsyncMessageType;

public class BatchUpdateDeviceIndustryDTO extends AsyncDTO{
	
	private List<String> macs;
	private String industry;
	private String merchant_name;
	private char dtoType;

	public List<String> getMacs() {
		return macs;
	}
	public void setMacs(List<String> ids) {
		this.macs = ids;
	}
	public char getDtoType() {
		return dtoType;
	}

	public void setDtoType(char dtoType) {
		this.dtoType = dtoType;
	}

	
	public String getIndustry() {
		return industry;
	}
	public void setIndustry(String industry) {
		this.industry = industry;
	}
	public String getMerchant_name() {
		return merchant_name;
	}
	public void setMerchant_name(String merchant_name) {
		this.merchant_name = merchant_name;
	}
	@Override
	public String getAsyncType() {
		return AsyncMessageType.BatchUpdateDeviceIndustry.getPrefix();
	}
}

