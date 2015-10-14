package com.bhu.vas.api.dto.ret.setting;

import com.bhu.vas.api.dto.VapModeDefined;
import com.bhu.vas.api.dto.VapModeDefined.HtmlInject404;
import com.bhu.vas.api.dto.ret.param.ParamVapHttp404DTO;




/**
 * 设备配置信息的ad
 * 广告信息配置
 * @author edmond
 *
 */
public class WifiDeviceSettingVapHttp404DTO implements DeviceSettingBuilderDTO{
	private String enable;
	private String url;
	private String codes;// = "40*,50*";
	private String version;
	//private String style;
	
	@Override
	public Object[] builderProperties() {
		Object[] properties = new Object[4];
		properties[0] = enable;
		properties[1] = url;
		properties[2] = codes;
		properties[3] = version;
		/*HtmlInject404 adv = VapModeDefined.HtmlInject404.getByStyle(style);
		properties[1] = adv.getPackurl();
		properties[2] = adv.toIndentify();*/
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	
	public static WifiDeviceSettingVapHttp404DTO fromParamVapAdDTO(ParamVapHttp404DTO dto){
		WifiDeviceSettingVapHttp404DTO hdto = new WifiDeviceSettingVapHttp404DTO();
		HtmlInject404 adv = VapModeDefined.HtmlInject404.getByStyle(dto.getStyle());
		hdto.setUrl(adv.getDynaurl());//.setBhu_id(adv.getBid());
		hdto.setEnable(dto.getEnable());
		hdto.setCodes(adv.getCodes());
		hdto.setVersion(adv.toIndentify());;
		return hdto;
	}
	

	public String getCodes() {
		return codes;
	}

	public void setCodes(String codes) {
		this.codes = codes;
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
