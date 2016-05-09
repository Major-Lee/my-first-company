package com.bhu.vas.api.rpc.charging.model;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.smartwork.msip.cores.orm.model.BaseStringModel;

/**
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class WifiDeviceBatchDetail extends BaseStringModel{
	//ID dmac
	private int last_importor;
	private String last_batchno;
	private String sellor;
	private String partner;
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public int getLast_importor() {
		return last_importor;
	}

	public void setLast_importor(int last_importor) {
		this.last_importor = last_importor;
	}

	public String getSellor() {
		return sellor;
	}

	public void setSellor(String sellor) {
		this.sellor = sellor;
	}

	public String getPartner() {
		return partner;
	}

	public void setPartner(String partner) {
		this.partner = partner;
	}

	public String getLast_batchno() {
		return last_batchno;
	}

	public void setLast_batchno(String last_batchno) {
		this.last_batchno = last_batchno;
	}
}
