package com.bhu.vas.api.vto.config;

import java.io.Serializable;
import java.util.List;

import com.bhu.vas.api.vto.URouterInfoVTO;
import com.bhu.vas.api.vto.URouterModeVTO;

@SuppressWarnings("serial")
public class URouterDeviceConfigMutilVTO implements Serializable{
	
	private List<URouterDeviceConfigVapVTO> vaps;
	
	private List<URouterDeviceConfigRadioVTO> radios;
	//双频合一
	private String rf_2in1;
	//admin管理密码
	private String admin_pwd;
//	//黑名单列表macs
	private List<String> block_macs;
	//黑名单中带对应的主机名
	private List<URouterDeviceConfigNVTO> block_with_names;
	//终端限速列表
	private List<URouterDeviceConfigRateControlVTO> rcs;
	//终端别名列表
	private List<URouterDeviceConfigMMVTO> mms;
	//上网方式
	private URouterModeVTO linkmode;
	
	//设备基本信息
	private URouterInfoVTO info;
	
	public String getRf_2in1() {
		return rf_2in1;
	}
	public void setRf_2in1(String rf_2in1) {
		this.rf_2in1 = rf_2in1;
	}
	public List<URouterDeviceConfigVapVTO> getVaps() {
		return vaps;
	}
	public void setVaps(List<URouterDeviceConfigVapVTO> vaps) {
		this.vaps = vaps;
	}
	public List<URouterDeviceConfigRadioVTO> getRadios() {
		return radios;
	}
	public void setRadios(List<URouterDeviceConfigRadioVTO> radios) {
		this.radios = radios;
	}
	public String getAdmin_pwd() {
		return admin_pwd;
	}
	public void setAdmin_pwd(String admin_pwd) {
		this.admin_pwd = admin_pwd;
	}


//	public List<URouterDeviceConfigNVTO> getBlock_macs() {
//		return block_macs;
//	}
//
//	public void setBlock_macs(List<URouterDeviceConfigNVTO> block_macs) {
//		this.block_macs = block_macs;
//	}

	public List<String> getBlock_macs() {
		return block_macs;
	}
	public List<URouterDeviceConfigNVTO> getBlock_with_names() {
		return block_with_names;
	}
	public void setBlock_with_names(List<URouterDeviceConfigNVTO> block_with_names) {
		this.block_with_names = block_with_names;
	}
	public void setBlock_macs(List<String> block_macs) {
		this.block_macs = block_macs;
	}
	public List<URouterDeviceConfigRateControlVTO> getRcs() {
		return rcs;
	}
	public void setRcs(List<URouterDeviceConfigRateControlVTO> rcs) {
		this.rcs = rcs;
	}
	public List<URouterDeviceConfigMMVTO> getMms() {
		return mms;
	}
	public void setMms(List<URouterDeviceConfigMMVTO> mms) {
		this.mms = mms;
	}
	public URouterModeVTO getLinkmode() {
		return linkmode;
	}
	public void setLinkmode(URouterModeVTO linkmode) {
		this.linkmode = linkmode;
	}
	public URouterInfoVTO getInfo() {
		return info;
	}
	public void setInfo(URouterInfoVTO info) {
		this.info = info;
	}
}
