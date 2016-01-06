package com.bhu.vas.api.dto.vap;

import com.bhu.vas.api.helper.WifiDeviceHelper;

public abstract class ModuleDTO {
	
	public static String Type_Http404 = "http404";
	public static String Type_Redirect = "redirect";
	
	/*public static String Enable = "enable";
	public static String Disable = "disable";*/
	
	//type="http404" enable="enable" ver="style001-00.00.03"
	//private String type;
	private String enable;
	private String ver;
	/*public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}*/
	public String getEnable() {
		return enable;
	}
	public void setEnable(String enable) {
		this.enable = enable;
	}
	public String getVer() {
		return ver;
	}
	public void setVer(String ver) {
		this.ver = ver;
	}
	
	public abstract String getType();
	
	public boolean wasEnable(){
		return WifiDeviceHelper.Enable.equals(enable);
	}
}
