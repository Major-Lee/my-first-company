package com.bhu.vas.business.asyn.spring.model.async;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.async.AsyncMessageType;

public class BatchSharedealModifyDTO extends ActionDTO {
	private String message;
	private Boolean cbto;
	private Boolean el;
	private double owner_percent;
	private String rcm;
	private String rcp;
	private String ait;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public double getOwner_percent() {
		return owner_percent;
	}

	public void setOwner_percent(double owner_percent) {
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

	@Override
	public String getActionType() {
		return AsyncMessageType.BatchSharedealModify.getPrefix();
	}
}
