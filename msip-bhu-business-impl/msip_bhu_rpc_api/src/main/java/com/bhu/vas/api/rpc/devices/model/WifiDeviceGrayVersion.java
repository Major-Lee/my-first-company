package com.bhu.vas.api.rpc.devices.model;

import java.util.Date;

import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.helper.VapEnumType.GrayLevel;
import com.bhu.vas.api.vto.device.GrayUsageVTO;
import com.smartwork.msip.cores.orm.model.BaseIntModel;


/**
 * 灰度关联固件版本及增值组件版本表
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class WifiDeviceGrayVersion extends BaseIntModel{
	
	//冗余字段关联使用灰度的设备数量，定时30分钟更新一次
	private int devices;
	//固件版本号id
	private String d_fwid;
	//增值模块 版本id
	private String d_omid;
	private Date created_at;
	
	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}

	public int getDevices() {
		return devices;
	}

	public void setDevices(int devices) {
		this.devices = devices;
	}

	public String getD_fwid() {
		return d_fwid;
	}

	public void setD_fwid(String d_fwid) {
		this.d_fwid = d_fwid;
	}

	public String getD_omid() {
		return d_omid;
	}

	public void setD_omid(String d_omid) {
		this.d_omid = d_omid;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	
	public GrayUsageVTO toGrayUsageVTO(){
		GrayUsageVTO vto = new GrayUsageVTO();
		vto.setIndex(id);
		vto.setFwid(d_fwid);
		vto.setOmid(d_omid);
		vto.setDevices(devices);
		GrayLevel fromIndex = VapEnumType.GrayLevel.fromIndex(id);
		vto.setN(fromIndex!=null?fromIndex.getName():VapEnumType.GrayLevel.Unknow.getName());
		return vto;
	}
}
