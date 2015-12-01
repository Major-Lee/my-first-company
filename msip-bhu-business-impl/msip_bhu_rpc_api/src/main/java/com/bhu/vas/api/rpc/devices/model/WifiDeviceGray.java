package com.bhu.vas.api.rpc.devices.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseStringModel;
/*
 * wifi设备 灰度管理表
 * 对于目前的灰度定义，一个设备只能属于一个灰度，录入前需要验证是否mac已经存在这个表中
 */
@SuppressWarnings("serial")
public class WifiDeviceGray extends BaseStringModel{
	//id为设备mac地址
	
	//device unit type
	private String dut;
	//灰度
	private int gl;	
	private Date created_at;
	
	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}

	public String getDut() {
		return dut;
	}
	public void setDut(String dut) {
		this.dut = dut;
	}
	public int getGl() {
		return gl;
	}
	public void setGl(int gl) {
		this.gl = gl;
	}
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	
}