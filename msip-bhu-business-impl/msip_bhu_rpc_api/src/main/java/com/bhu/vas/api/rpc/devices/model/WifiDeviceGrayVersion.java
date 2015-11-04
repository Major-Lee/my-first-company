package com.bhu.vas.api.rpc.devices.model;

import java.util.Date;

import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.helper.VapEnumType.DeviceUnitType;
import com.bhu.vas.api.helper.VapEnumType.GrayLevel;
import com.bhu.vas.api.rpc.devices.model.pk.WifiDeviceGrayVersionPK;
import com.bhu.vas.api.vto.device.GrayUsageVTO;
import com.smartwork.msip.cores.orm.model.BasePKModel;


/**
 * 灰度关联固件版本及增值组件版本表
 * 主键为	  产品类型编号-灰度编号
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class WifiDeviceGrayVersion extends BasePKModel<WifiDeviceGrayVersionPK>{
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

	
    public int getDut() {
    	if (this.getId() == null) {
            return 0;
        }
        return this.getId().getDut();
	}

	public void setDut(int dut) {
	 	if (this.getId() == null) {
            this.setId(new WifiDeviceGrayVersionPK());
        }
	    this.getId().setDut(dut);
	}

	public int getGl() {
		if (this.getId() == null) {
            return 0;
        }
        return this.getId().getGl();
	}

	public void setGl(int gl) {
		if (this.getId() == null) {
            this.setId(new WifiDeviceGrayVersionPK());
        }
	    this.getId().setGl(gl);
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
		vto.setDut(id.getDut());
		vto.setGl(getGl());
		vto.setFwid(d_fwid);
		vto.setOmid(d_omid);
		vto.setDevices(devices);
		GrayLevel fromIndex = VapEnumType.GrayLevel.fromIndex(vto.getGl());
		vto.setGn(fromIndex!=null?fromIndex.getName():VapEnumType.GrayLevel.Unknow.getName());
		DeviceUnitType dut = VapEnumType.DeviceUnitType.fromIndex(vto.getDut());
		vto.setDun(dut!=null?dut.getName():"");
		return vto;
	}

	@Override
	protected Class<WifiDeviceGrayVersionPK> getPKClass() {
		return WifiDeviceGrayVersionPK.class;
	}
}
