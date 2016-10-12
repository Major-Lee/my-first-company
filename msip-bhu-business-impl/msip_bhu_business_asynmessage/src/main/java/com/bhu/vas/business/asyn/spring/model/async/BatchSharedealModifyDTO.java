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
	private String fait;
//	private String channel_lv1;
//	private String channel_lv2;
	
	
	private boolean needCheckBinding;
	public String getFait() {
		return fait;
	}

	public void setFait(String fait) {
		this.fait = fait;
	}

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
	public boolean isNeedCheckBinding() {
		return needCheckBinding;
	}

	public void setNeedCheckBinding(boolean needCheckBinding) {
		this.needCheckBinding = needCheckBinding;
	}

	
//	public String getChannel_lv1() {
//		return channel_lv1;
//	}
//
//	public void setChannel_lv1(String channel_lv1) {
//		this.channel_lv1 = channel_lv1;
//	}
//
//	public String getChannel_lv2() {
//		return channel_lv2;
//	}
//
//	public void setChannel_lv2(String channel_lv2) {
//		this.channel_lv2 = channel_lv2;
//	}
//
	@Override
	public String getAsyncType() {
		return AsyncMessageType.BatchSharedealModify.getPrefix();
	}
}
