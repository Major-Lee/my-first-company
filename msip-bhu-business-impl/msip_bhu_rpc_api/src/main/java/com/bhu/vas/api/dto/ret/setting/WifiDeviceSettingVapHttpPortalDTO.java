package com.bhu.vas.api.dto.ret.setting;

import com.bhu.vas.api.dto.VapModeDefined;
import com.bhu.vas.api.dto.VapModeDefined.HtmlPortal;


/**
 * 设备配置信息的ad
 * 广告信息配置
 * @author edmond
 *
 */
public class WifiDeviceSettingVapHttpPortalDTO implements DeviceSettingBuilderDTO{
	
	private String enable;
	
	private String style;
	
	@Override
	public Object[] builderProperties() {
		Object[] properties = new Object[2];
		properties[0] = enable;
		HtmlPortal adv = VapModeDefined.HtmlPortal.getByStyle(style);
		properties[1] = adv.getUrl();
		properties[2] = adv.toIndentify();
		return properties;
	}
	
	@Override
	public Object[] builderProperties(int type) {
		return builderProperties();
	}
	
	@Override
	public boolean beRemoved() {
		return false;
	}
	
	public String getEnable() {
		return enable;
	}

	public void setEnable(String enable) {
		this.enable = enable;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}
}
