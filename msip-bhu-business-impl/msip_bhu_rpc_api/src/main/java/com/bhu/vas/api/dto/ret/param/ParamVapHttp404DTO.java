package com.bhu.vas.api.dto.ret.param;



/**
 * 设备配置信息的ad
 * 广告信息配置
 * @author edmond
 *
 */
public class ParamVapHttp404DTO{
	private String enable;
	private String style;
	
	/*@Override
	public Object[] builderProperties() {
		Object[] properties = new Object[3];
		properties[0] = enable;
		HtmlInject404 adv = VapModeDefined.HtmlInject404.getByStyle(style);
		properties[1] = adv.getPackurl();
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
	}*/
	
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

	/*public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}*/
//{"enable":"enable","url":"http://auth.wi2o.cn/ad/ad.zip"}
	public static void main(String[] argv){
		/*WifiDeviceSettingVapHttp404DTO dto = new WifiDeviceSettingVapHttp404DTO();
		//dto.setAd_interface(ad_interface);
		//dto.setAd_url(ad_url);
		dto.setUrl("http://auth.wi2o.cn/ad/ad.js");
		dto.setEnable("enable");
		System.out.println(JsonHelper.getJSONString(dto));*/
	}
}
