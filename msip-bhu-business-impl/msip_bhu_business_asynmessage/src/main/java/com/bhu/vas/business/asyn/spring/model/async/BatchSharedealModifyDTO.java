package com.bhu.vas.business.asyn.spring.model.async;

import com.bhu.vas.business.asyn.spring.builder.async.AsyncDTO;
import com.bhu.vas.business.asyn.spring.builder.async.AsyncMessageType;

public class BatchSharedealModifyDTO extends AsyncDTO {
	private String message;
	private Boolean cbto;
	private Boolean el;
	private boolean customized;
	private String owner_percent;
	private String manufacturer_percent;
	private String distributor_percent;
	private String rcm;
	private String rcp;
	private String ait;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getOwner_percent() {
		return owner_percent;
	}

	public void setOwner_percent(String owner_percent) {
		this.owner_percent = owner_percent;
	}

	public String getRcm() {
		return rcm;
	}

	public void setRcm(String rcm) {
		this.rcm = rcm;
	}

	public String getRcp() {
		return rcp;
	}

	public void setRcp(String rcp) {
		this.rcp = rcp;
	}

	public String getAit() {
		return ait;
	}

	public void setAit(String ait) {
		this.ait = ait;
	}

	public Boolean getCbto() {
		return cbto;
	}

	public void setCbto(Boolean cbto) {
		this.cbto = cbto;
	}

	public Boolean getEl() {
		return el;
	}

	public void setEl(Boolean el) {
		this.el = el;
	}

	public boolean isCustomized() {
		return customized;
	}

	public void setCustomized(boolean customized) {
		this.customized = customized;
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

	@Override
	public String getAsyncType() {
		return AsyncMessageType.BatchSharedealModify.getPrefix();
	}
}
