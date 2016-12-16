package com.bhu.vas.api.rpc.advertise.model;

import java.math.BigDecimal;
import java.util.Date;

import com.smartwork.msip.cores.helper.ArithHelper;
import com.smartwork.msip.cores.orm.model.extjson.ListJsonExtIntModel;


@SuppressWarnings("serial")
public class AdvertiseDetails extends ListJsonExtIntModel<String>{

	private String advertiseid;
	private String publish_time;
	private int publish_count;
	private long actual_count;
	private int state;
	private String cash;
	private int pv;
	private int uv;
	private Date created_at;
	private Date updated_at;
	
	public String getAdvertiseid() {
		return advertiseid;
	}

	public void setAdvertiseid(String advertiseid) {
		this.advertiseid = advertiseid;
	}
	
	public String getPublish_time() {
		return publish_time;
	}

	public void setPublish_time(String publish_time) {
		this.publish_time = publish_time;
	}

	public int getPublish_count() {
		return publish_count;
	}

	public void setPublish_count(int publish_count) {
		this.publish_count = publish_count;
	}

	public long getActual_count() {
		return actual_count;
	}

	public void setActual_count(long actual_count) {
		this.actual_count = actual_count;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getCash() {
		return cash;
	}

	public void setCash(double cash) {
		this.cash = ArithHelper.getCuttedCurrency(cash+"");
	}

	public int getPv() {
		return pv;
	}

	public void setPv(int pv) {
		this.pv = pv;
	}

	public int getUv() {
		return uv;
	}

	public void setUv(int uv) {
		this.uv = uv;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public Date getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(Date updated_at) {
		this.updated_at = updated_at;
	}

	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}
	
	@Override
	public void preUpdate() {
		this.updated_at = new Date();
		super.preUpdate();
	}
	@Override
	public Class<String> getJsonParserModel() {
		return String.class;
	}
	
	private float cutDecimal (float f){
		 BigDecimal   b  =   new BigDecimal(f);  
		 return b.setScale(2, BigDecimal.ROUND_DOWN).floatValue();  
	}
}
