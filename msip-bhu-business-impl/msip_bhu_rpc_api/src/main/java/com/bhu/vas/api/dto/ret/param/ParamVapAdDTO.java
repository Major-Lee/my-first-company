package com.bhu.vas.api.dto.ret.param;

import com.bhu.vas.api.dto.VapModeDefined;
import com.smartwork.msip.cores.helper.JsonHelper;


/**
 * 设备配置信息的ad
 * 广告信息配置
 * @author edmond
 *
 */
public class ParamVapAdDTO{
	//private String bid;
	private String style;
	private String enable;
	/*public String getBid() {
		return bid;
	}

	public void setBid(String bid) {
		this.bid = bid;
	}*/

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getEnable() {
		return enable;
	}

	public void setEnable(String enable) {
		this.enable = enable;
	}

	/*public String getBhu_id() {
		return bhu_id;
	}
	public void setBhu_id(String bhu_id) {
		this.bhu_id = bhu_id;
	}
	public String getBhu_ad_url() {
		return bhu_ad_url;
	}
	public void setBhu_ad_url(String bhu_ad_url) {
		this.bhu_ad_url = bhu_ad_url;
	}
	public String getBhu_enable() {
		return bhu_enable;
	}
	public void setBhu_enable(String bhu_enable) {
		this.bhu_enable = bhu_enable;
	}
	//bhu_enable=\"%s\"  bhu_ad_url=\"%s\" id=\"%s\"
	@Override
	public Object[] builderProperties() {
		Object[] properties = new Object[3];
		properties[0] = bhu_id;
		HtmlInjectAdv adv = VapModeDefined.HtmlInjectAdv.getByStyle(bhu_ad_url);
		properties[1] = adv.getUrl();
		properties[2] = bhu_enable;
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
	//http://192.168.66.7/vap/ad/001/js/ad.js
	public static void main(String[] argv){
		ParamVapAdDTO dto = new ParamVapAdDTO();
		//dto.setAd_interface(ad_interface);
		//dto.setAd_url(ad_url);
		dto.setStyle(VapModeDefined.HtmlInjectAdv.STYLE001.getStyle());
		dto.setEnable("enable");
		//dto.setBid("400889");
		System.out.println(JsonHelper.getJSONString(dto));
	}
}
