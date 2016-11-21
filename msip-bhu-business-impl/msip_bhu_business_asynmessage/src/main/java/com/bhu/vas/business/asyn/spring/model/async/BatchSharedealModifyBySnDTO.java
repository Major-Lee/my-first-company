package com.bhu.vas.business.asyn.spring.model.async;

import com.bhu.vas.business.asyn.spring.builder.async.AsyncDTO;
import com.bhu.vas.business.asyn.spring.builder.async.AsyncMessageType;

public class BatchSharedealModifyBySnDTO extends AsyncDTO {
	private String macs;
	private String owner_percent;
	private String manufacturer_percent;
	private String distributor_percent;
	private String distributor_l2_percent;


	public String getMacs() {
		return macs;
	}


	public void setMacs(String macs) {
		this.macs = macs;
	}


	public String getOwner_percent() {
		return owner_percent;
	}


	public void setOwner_percent(String owner_percent) {
		this.owner_percent = owner_percent;
	}


	public String getManufacturer_percent() {
		return manufacturer_percent;
	}


	public void setManufacturer_percent(String manufacturer_percent) {
		this.manufacturer_percent = manufacturer_percent;
	}


	public String getDistributor_percent() {
		return distributor_percent;
	}


	public void setDistributor_percent(String distributor_percent) {
		this.distributor_percent = distributor_percent;
	}


	public String getDistributor_l2_percent() {
		return distributor_l2_percent;
	}


	public void setDistributor_l2_percent(String distributor_l2_percent) {
		this.distributor_l2_percent = distributor_l2_percent;
	}


	@Override
	public String getAsyncType() {
		return AsyncMessageType.BatchSharedealModifyBySn.getPrefix();
	}
}
