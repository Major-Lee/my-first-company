package com.bhu.vas.api.dto.ret.param;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.helper.WifiDeviceHelper;
import com.smartwork.msip.cores.helper.JsonHelper;



/**
 * 设备配置信息的ad
 * 广告信息配置
 * @author edmond
 *
 */
@SuppressWarnings("serial")
public class ParamVapVistorWifiDTO implements java.io.Serializable{
	//users_tx_rate users_rx_rate signal_limit(-30) redirect_url("www.bhuwifi.com") idle_timeout(1200) force_timeout(21600) open_resource("") ssid("BhuWIFI-访客")
	private int users_tx_rate;
	private int users_rx_rate;
	private int signal_limit;
	private String redirect_url;
	private int idle_timeout;
	private int force_timeout;
	private String open_resource;
	private String block_mode;
	private String ssid;
	
	
	public Object[] builderProperties() {
		Object[] properties = new Object[9];
		properties[0] = users_tx_rate * 8;
		properties[1] = users_rx_rate * 8;
		properties[2] = signal_limit;
		properties[3] = redirect_url;
		properties[4] = idle_timeout;
		properties[5] = force_timeout;
		properties[6] = open_resource;
		properties[7] = block_mode;
		properties[8] = ssid;
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

	public String getBlock_mode() {
		return block_mode;
	}

	public void setBlock_mode(String block_mode) {
		this.block_mode = block_mode;
	}
	
	public void switchWorkMode(int switchAct){
		switch(switchAct){
			case WifiDeviceHelper.SwitchMode_Router2Bridge_Act:
				this.block_mode = WifiDeviceHelper.Default_BlockMode_Bridge;
			break;
			case WifiDeviceHelper.SwitchMode_Bridge2Router_Act:
				this.block_mode = WifiDeviceHelper.Default_BlockMode_Router;
				break;
			default:
				break;
		}
	}

	private final static String Default_Redirect_url = "www.bhuwifi.com";
	private final static String Default_Open_resource = "bhuwifi.com,bhunetworks.com";
	private final static String Default_SSID = "BhuWiFi-访客";
	
	private final static int Default_Users_tx_rate= 128;
	private final static int Default_Users_rx_rate= 128;
	private final static int Default_Signal_limit= -30;
	private final static int Default_Idle_timeout= 3*60*60;
	private final static int Default_Force_timeout= 12*60*60;
	////users_tx_rate users_rx_rate signal_limit(-30) redirect_url("www.bhuwifi.com") idle_timeout(1200) force_timeout(21600) open_resource("") ssid("BhuWIFI-访客")
	public static ParamVapVistorWifiDTO builderDefault(boolean router){
		ParamVapVistorWifiDTO dto = new ParamVapVistorWifiDTO();
		dto.setUsers_tx_rate(Default_Users_tx_rate);
		dto.setUsers_rx_rate(Default_Users_rx_rate);
		dto.setSignal_limit(Default_Signal_limit);
		dto.setRedirect_url(Default_Redirect_url);
		dto.setIdle_timeout(Default_Idle_timeout);//
		dto.setForce_timeout(Default_Force_timeout);
		dto.setOpen_resource(Default_Open_resource);
		dto.setBlock_mode(router?WifiDeviceHelper.Default_BlockMode_Router:WifiDeviceHelper.Default_BlockMode_Bridge);
		dto.setSsid(Default_SSID);
		return dto;
	}
	
	public static ParamVapVistorWifiDTO fufillWithDefault(ParamVapVistorWifiDTO param,boolean router){
		if(param == null) return builderDefault(router);
		//if(param.getUsers_tx_rate() == 0) param.setUsers_tx_rate(Default_Users_tx_rate);
		//if(param.getUsers_rx_rate() == 0) param.setUsers_rx_rate(Default_Users_rx_rate);
		
		if(param.getSignal_limit() == 0) param.setSignal_limit(Default_Signal_limit);
		if(StringUtils.isEmpty(param.getRedirect_url())) param.setRedirect_url(Default_Redirect_url);
		if(param.getIdle_timeout() == 0) param.setIdle_timeout(Default_Idle_timeout);
		if(param.getForce_timeout() == 0) param.setForce_timeout(Default_Force_timeout);
		if(StringUtils.isEmpty(param.getOpen_resource()) || param.getOpen_resource().indexOf(Default_Open_resource) == -1) {
			param.setOpen_resource(Default_Open_resource);
		}
		param.setBlock_mode(router?WifiDeviceHelper.Default_BlockMode_Router:WifiDeviceHelper.Default_BlockMode_Bridge);
		if(StringUtils.isEmpty(param.getSsid())) param.setSsid(Default_SSID);
		return param;
	}
	
	public static void main(String[] argv){
		System.out.println(JsonHelper.getJSONString(fufillWithDefault(null,false)));
		
		ParamVapVistorWifiDTO param = new ParamVapVistorWifiDTO();
		param.setSsid("GOOO理论");
		System.out.println(JsonHelper.getJSONString(fufillWithDefault(param,true)));
	}
}
