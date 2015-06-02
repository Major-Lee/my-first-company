package com.bhu.vas.api.dto.ret.setting;

import com.bhu.vas.api.dto.VapModeDefined;
import com.bhu.vas.api.dto.VapModeDefined.HtmlRedirect;
import com.bhu.vas.api.dto.ret.param.ParamVapHttpRedirectDTO;
import com.smartwork.msip.cores.helper.StringHelper;




/**
 * 设备配置信息的ad
 * 广告信息配置
 * @author edmond
 *
 */
public class WifiDeviceSettingVapHttpRedirectDTO implements DeviceSettingBuilderDTO{
	private String enable;
	private String rule;
	private String version;
	//private String style;
	
	@Override
	public Object[] builderProperties() {
		Object[] properties = new Object[3];
		properties[0] = enable;
		properties[1] = rule;
		properties[2] = version;
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


	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public static WifiDeviceSettingVapHttpRedirectDTO fromParamVapAdDTO(ParamVapHttpRedirectDTO dto){
		WifiDeviceSettingVapHttpRedirectDTO hdto = new WifiDeviceSettingVapHttpRedirectDTO();
		hdto.setEnable(dto.getEnable());
		HtmlRedirect adv = VapModeDefined.HtmlRedirect.getByStyle(dto.getStyle());
		
		StringBuilder sb = new StringBuilder();
		sb.append(dto.getTimes()).append(StringHelper.COMMA_STRING_GAP)
			.append(dto.getStart_time()).append(StringHelper.COMMA_STRING_GAP)
			.append(dto.getEnd_time()).append(StringHelper.COMMA_STRING_GAP)
			.append(adv.getUrls());
		hdto.setRule(sb.toString());
		hdto.setVersion(adv.toIndentify());
		//HtmlInject404 adv = VapModeDefined..getByStyle(dto.getStyle());
		//hdto.setUrl(adv.getPackurl());//.setBhu_id(adv.getBid());
		//hdto.setEnable(dto.getEnable());
		//hdto.setVersion(adv.toIndentify());;
		return hdto;
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
