package com.bhu.vas.api.dto.ret.setting;

import java.util.List;

import com.smartwork.msip.cores.helper.StringHelper;

/**
 * 设备配置信息的acllist
 * @author tangzichao
 *
 */
public class WifiDeviceSettingAclDTO implements DeviceSettingBuilderDTO{
	//acl名称
	private String name;
	//acl对应的mac列表
	private List<String> macs;
	
	public WifiDeviceSettingAclDTO(){
		
	}
	
	public WifiDeviceSettingAclDTO(String name){
		this.name = name;
	}
	
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
	
	@Override
	public boolean equals(Object o) {
		if(o==null)return false;
		if(o instanceof WifiDeviceSettingAclDTO){
			WifiDeviceSettingAclDTO oo = (WifiDeviceSettingAclDTO)o;
			return this.getName().equals(oo.getName());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.getName().toString().hashCode();
	}

	@Override
	public Object[] builderProperties() {
		Object[] properties = new Object[2];
		properties[0] = name;
		
		StringBuffer macs_split_comma = new StringBuffer();
		if(macs != null && !macs.isEmpty()){
			for(String mac : macs){
				if(macs_split_comma.length() > 0)
					macs_split_comma.append(StringHelper.COMMA_STRING_GAP);
				macs_split_comma.append(mac);
			}
		}
		properties[1] = macs_split_comma.toString();
		return properties;
	}
}
