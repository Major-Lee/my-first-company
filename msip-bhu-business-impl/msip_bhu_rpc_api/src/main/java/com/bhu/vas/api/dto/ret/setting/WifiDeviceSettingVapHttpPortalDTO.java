package com.bhu.vas.api.dto.ret.setting;

import com.bhu.vas.api.dto.VapModeDefined;
import com.bhu.vas.api.dto.VapModeDefined.HtmlPortal;
import com.bhu.vas.api.dto.ret.setting.param.ParamVapHttpPortalDTO;




/**
 * 设备配置信息的ad
 * 广告信息配置
 * @author edmond
 *
 */
public class WifiDeviceSettingVapHttpPortalDTO implements DeviceSettingBuilderDTO{
	private String enable;
	//private String url;
	private String redirect_url;
	private String open_resource;
	private String ssid_guest;
	private int idle_timeout;
	private int force_timeout;
	private String question;
	private String answer;
	private String version;
	//private String style;
	
	@Override
	public Object[] builderProperties() {
		Object[] properties = new Object[7];
		properties[0] = redirect_url;
		properties[1] = idle_timeout;
		properties[2] = force_timeout;
		properties[3] = open_resource;
		
		properties[4] = question;
		properties[5] = answer;
		properties[6] = ssid_guest;
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

	
	public String getRedirect_url() {
		return redirect_url;
	}

	public void setRedirect_url(String redirect_url) {
		this.redirect_url = redirect_url;
	}

	public String getOpen_resource() {
		return open_resource;
	}

	public void setOpen_resource(String open_resource) {
		this.open_resource = open_resource;
	}

	public int getIdle_timeout() {
		return idle_timeout;
	}

	public void setIdle_timeout(int idle_timeout) {
		this.idle_timeout = idle_timeout;
	}

	public int getForce_timeout() {
		return force_timeout;
	}

	public void setForce_timeout(int force_timeout) {
		this.force_timeout = force_timeout;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getSsid_guest() {
		return ssid_guest;
	}

	public void setSsid_guest(String ssid_guest) {
		this.ssid_guest = ssid_guest;
	}

	public static WifiDeviceSettingVapHttpPortalDTO fromParamVapAdDTO(ParamVapHttpPortalDTO dto){
		WifiDeviceSettingVapHttpPortalDTO hdto = new WifiDeviceSettingVapHttpPortalDTO();
		HtmlPortal adv = VapModeDefined.HtmlPortal.getByStyle(dto.getStyle());
		hdto.setRedirect_url(adv.getRedirect_url());
		hdto.setSsid_guest(dto.getSsid_guest());
		hdto.setOpen_resource(adv.getOpen_resource());
		hdto.setIdle_timeout(dto.getIdle_timeout());
		hdto.setForce_timeout(dto.getForce_timeout());
		hdto.setQuestion(dto.getQuestion());
		hdto.setAnswer(dto.getAnswer());
		hdto.setVersion(adv.toIndentify());
		
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
