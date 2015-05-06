package com.bhu.vas.api.dto.ret.setting;

public interface DeviceSettingBuilderDTO {
	//配置通用开关可用
	public static final String Enable = "enable";
	//配置通用开关不可用
	public static final String Disable = "disable";
	
	public static final String Removed = "1";
	
	public Object[] builderProperties();
	
	public Object[] builderProperties(int type);
	
	public boolean isRemoved();
}
