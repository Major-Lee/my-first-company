package com.bhu.vas.api.dto.ret.setting;

import java.util.List;

/**
 * 设备配置信息的acllist
 * @author tangzichao
 *
 */
public class WifiDeviceSettingAclDTO {
	//acl名称
	private String name;
	//acl对应的mac列表
	private List<String> macs;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getMacs() {
		return macs;
	}
	public void setMacs(List<String> macs) {
		this.macs = macs;
	}
}
