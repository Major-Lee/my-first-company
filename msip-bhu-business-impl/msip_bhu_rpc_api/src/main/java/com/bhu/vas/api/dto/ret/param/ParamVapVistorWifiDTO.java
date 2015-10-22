package com.bhu.vas.api.dto.ret.param;

import org.apache.commons.lang.StringUtils;

import com.smartwork.msip.cores.helper.JsonHelper;



/**
 * 设备配置信息的ad
 * 广告信息配置
 * @author edmond
 *
 */
public class ParamVapVistorWifiDTO{
	//users_tx_rate users_rx_rate signal_limit(-30) redirect_url("www.bhuwifi.com") idle_timeout(1200) force_timeout(21600) open_resource("") ssid("BhuWIFI-访客")
	private int users_tx_rate;
	private int users_rx_rate;
	private int signal_limit;
	private String redirect_url;
	private int idle_timeout;
	private int force_timeout;
	private String open_resource;
	private String ssid;
	
	public Object[] builderProperties() {
		Object[] properties = new Object[8];
		properties[0] = users_tx_rate;
		properties[1] = users_rx_rate;
		properties[2] = signal_limit;
		properties[3] = redirect_url;
		properties[4] = idle_timeout;
		properties[5] = force_timeout;
		properties[6] = open_resource;
		properties[7] = ssid;
		return properties;
	}

	public int getUsers_tx_rate() {
		return users_tx_rate;
	}

	public void setUsers_tx_rate(int users_tx_rate) {
		this.users_tx_rate = users_tx_rate;
	}

	public int getUsers_rx_rate() {
		return users_rx_rate;
	}

	public void setUsers_rx_rate(int users_rx_rate) {
		this.users_rx_rate = users_rx_rate;
	}

	public int getSignal_limit() {
		return signal_limit;
	}

	public void setSignal_limit(int signal_limit) {
		this.signal_limit = signal_limit;
	}

	public String getRedirect_url() {
		return redirect_url;
	}

	public void setRedirect_url(String redirect_url) {
		this.redirect_url = redirect_url;
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

	public String getOpen_resource() {
		return open_resource;
	}

	public void setOpen_resource(String open_resource) {
		this.open_resource = open_resource;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	private final static String Default_Redirect_url = "www.bhuwifi.com";
	private final static String Default_Open_resource = "";
	private final static String Default_SSID = "BhuWIFI-访客";
	////users_tx_rate users_rx_rate signal_limit(-30) redirect_url("www.bhuwifi.com") idle_timeout(1200) force_timeout(21600) open_resource("") ssid("BhuWIFI-访客")
	public static ParamVapVistorWifiDTO builderDefault(){
		ParamVapVistorWifiDTO dto = new ParamVapVistorWifiDTO();
		dto.setUsers_tx_rate(128);
		dto.setUsers_rx_rate(128);
		dto.setSignal_limit(-30);
		dto.setRedirect_url(Default_Redirect_url);
		dto.setIdle_timeout(1200);
		dto.setForce_timeout(21600);
		dto.setOpen_resource(Default_Open_resource);
		dto.setSsid(Default_SSID);
		return dto;
	}
	
	public static ParamVapVistorWifiDTO fufillWithDefault(ParamVapVistorWifiDTO param){
		if(param == null) return builderDefault();
		if(param.getUsers_tx_rate() == 0) param.setUsers_tx_rate(128);
		if(param.getUsers_rx_rate() == 0) param.setUsers_rx_rate(128);
		
		if(param.getSignal_limit() == 0) param.setSignal_limit(-30);
		if(StringUtils.isEmpty(param.getRedirect_url())) param.setRedirect_url(Default_Redirect_url);
		if(param.getIdle_timeout() == 0) param.setIdle_timeout(1200);
		if(param.getForce_timeout() == 0) param.setForce_timeout(21600);
		if(StringUtils.isEmpty(param.getOpen_resource())) param.setOpen_resource(Default_Open_resource);
		if(StringUtils.isEmpty(param.getSsid())) param.setSsid(Default_SSID);
		return param;
	}
	
	public static void main(String[] argv){
		System.out.println(JsonHelper.getJSONString(fufillWithDefault(null)));
		
		ParamVapVistorWifiDTO param = new ParamVapVistorWifiDTO();
		param.setSsid("GOOO理论");
		System.out.println(JsonHelper.getJSONString(fufillWithDefault(param)));
	}
}
