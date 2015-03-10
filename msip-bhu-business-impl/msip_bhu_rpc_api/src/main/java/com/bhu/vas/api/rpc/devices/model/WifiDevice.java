package com.bhu.vas.api.rpc.devices.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseStringModel;
/*
 * 
 */
@SuppressWarnings("serial")
public class WifiDevice extends BaseStringModel{
	
	private String hdtype;	
	private String orig_vendor;
	private String orig_model;
	private String orig_hdver;
	private String orig_swver;
	private String oem_vendor;
	private String oem_model;
	private String oem_hdver;
	private String oem_swver;
	private String sn;
	private String ip;
	private String wan_ip;
	private String config_sequence;
	private String build_info;
	private String config_model_ver;
	private String config_mode;
	private String work_mode;
	private Date last_reged_at;
	private Date created_at;
	
	
	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}
	
	@Override
	public void preUpdate() {
		super.preUpdate();
	}

	public String getHdtype() {
		return hdtype;
	}

	public void setHdtype(String hdtype) {
		this.hdtype = hdtype;
	}

	public String getOrig_vendor() {
		return orig_vendor;
	}

	public void setOrig_vendor(String orig_vendor) {
		this.orig_vendor = orig_vendor;
	}

	public String getOrig_model() {
		return orig_model;
	}

	public void setOrig_model(String orig_model) {
		this.orig_model = orig_model;
	}

	public String getOrig_hdver() {
		return orig_hdver;
	}

	public void setOrig_hdver(String orig_hdver) {
		this.orig_hdver = orig_hdver;
	}

	public String getOrig_swver() {
		return orig_swver;
	}

	public void setOrig_swver(String orig_swver) {
		this.orig_swver = orig_swver;
	}

	public String getOem_vendor() {
		return oem_vendor;
	}

	public void setOem_vendor(String oem_vendor) {
		this.oem_vendor = oem_vendor;
	}

	public String getOem_model() {
		return oem_model;
	}

	public void setOem_model(String oem_model) {
		this.oem_model = oem_model;
	}

	public String getOem_hdver() {
		return oem_hdver;
	}

	public void setOem_hdver(String oem_hdver) {
		this.oem_hdver = oem_hdver;
	}

	public String getOem_swver() {
		return oem_swver;
	}

	public void setOem_swver(String oem_swver) {
		this.oem_swver = oem_swver;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getWan_ip() {
		return wan_ip;
	}

	public void setWan_ip(String wan_ip) {
		this.wan_ip = wan_ip;
	}

	public String getConfig_sequence() {
		return config_sequence;
	}

	public void setConfig_sequence(String config_sequence) {
		this.config_sequence = config_sequence;
	}

	public String getBuild_info() {
		return build_info;
	}

	public void setBuild_info(String build_info) {
		this.build_info = build_info;
	}

	public String getConfig_model_ver() {
		return config_model_ver;
	}

	public void setConfig_model_ver(String config_model_ver) {
		this.config_model_ver = config_model_ver;
	}

	public String getConfig_mode() {
		return config_mode;
	}

	public void setConfig_mode(String config_mode) {
		this.config_mode = config_mode;
	}

	public String getWork_mode() {
		return work_mode;
	}

	public void setWork_mode(String work_mode) {
		this.work_mode = work_mode;
	}

	public Date getLast_reged_at() {
		return last_reged_at;
	}

	public void setLast_reged_at(Date last_reged_at) {
		this.last_reged_at = last_reged_at;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
}