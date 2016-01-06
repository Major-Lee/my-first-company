package com.bhu.vas.api.dto.ret.setting;

public interface DeviceSettingBuilderDTO {
	public static final String Removed = "1";
	
	public Object[] builderProperties();
	
	public Object[] builderProperties(int type);
	
	public boolean beRemoved();
}
