package com.bhu.vas.api.dto.ret.param;

import com.smartwork.msip.cores.helper.JsonHelper;



/**
 * 设备配置信息的ad
 * 广告信息配置
 * @author edmond
 *
 */
public class ParamVapHttpPortalDTO{
	private String style;
	private String ssid_guest;
	//private String redirect_url;
	//private String open_resource;
	private int idle_timeout;
	private int force_timeout;
	private String question;
	private String answer;
	//开放的资源链接
	
/*	@Override
	public Object[] builderProperties() {
		Object[] properties = new Object[3];
		properties[0] = enable;
		HtmlPortal adv = VapModeDefined.HtmlPortal.getByStyle(style);
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
	
	public String getStyle() {
		return style;
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

	public void setStyle(String style) {
		this.style = style;
	}
	
	public String getSsid_guest() {
		return ssid_guest;
	}

	public void setSsid_guest(String ssid_guest) {
		this.ssid_guest = ssid_guest;
	}

	public static void main(String[] argv){
		ParamVapHttpPortalDTO dto = new ParamVapHttpPortalDTO();
		dto.setStyle("style001");
		dto.setSsid_guest("uRouter_guest");
		dto.setIdle_timeout(1200);
		dto.setForce_timeout(21600);
		dto.setQuestion("从哪里来？");
		dto.setAnswer("到哪里去");
		System.out.println(JsonHelper.getJSONString(dto));
	}
}
