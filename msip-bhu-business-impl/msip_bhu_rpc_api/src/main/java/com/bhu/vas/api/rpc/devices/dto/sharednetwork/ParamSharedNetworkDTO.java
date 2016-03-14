package com.bhu.vas.api.rpc.devices.dto.sharednetwork;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.helper.VapEnumType.SharedNetworkType;
import com.bhu.vas.api.helper.WifiDeviceHelper;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.smartwork.msip.cores.helper.JsonHelper;



/**
 * 设备配置信息的ad
 * 广告信息配置
 * @author edmond
 *
 */
@SuppressWarnings("serial")
public class ParamSharedNetworkDTO implements java.io.Serializable{
	//users_tx_rate users_rx_rate signal_limit(-30) redirect_url("www.bhuwifi.com") idle_timeout(1200) force_timeout(21600) open_resource("") ssid("BhuWIFI-访客")
	private int users_tx_rate;
	private int users_rx_rate;
	private int signal_limit;
	private String redirect_url;
	private int idle_timeout;
	private int force_timeout;
	private String open_resource;
	//此值不体现在参数传递中，是根据设备当前的工作模式来决定是什么值,就是参数在过程中进行初始化
	@JsonInclude(Include.NON_NULL)
	private String block_mode;
	//此值不体现在参数传递中，是根据设备当前的工作模式来决定是什么值,就是参数在过程中进行初始化
	@JsonInclude(Include.NON_NULL)
	private String complete_isolate_ports;
	private String ssid;
	
	
	public Object[] builderProperties() {
		Object[] properties = new Object[10];
		properties[0] = users_tx_rate * 8;
		properties[1] = users_rx_rate * 8;
		properties[2] = signal_limit;
		properties[3] = redirect_url;
		properties[4] = idle_timeout;
		properties[5] = force_timeout;
		properties[6] = open_resource;
		properties[7] = block_mode;
		properties[8] = complete_isolate_ports;
		properties[9] = ssid;
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
	
	public String getComplete_isolate_ports() {
		return complete_isolate_ports;
	}

	public void setComplete_isolate_ports(String complete_isolate_ports) {
		this.complete_isolate_ports = complete_isolate_ports;
	}

	public void switchWorkMode(int switchAct){
		switch(switchAct){
			case WifiDeviceHelper.SwitchMode_Router2Bridge_Act:
				this.block_mode = WifiDeviceHelper.Default_BlockMode_Bridge;
				this.complete_isolate_ports = WifiDeviceHelper.Default_CompleteIsolatePorts_Bridge;
			break;
			case WifiDeviceHelper.SwitchMode_Bridge2Router_Act:
				this.block_mode = WifiDeviceHelper.Default_BlockMode_Router;
				this.complete_isolate_ports = WifiDeviceHelper.Default_CompleteIsolatePorts_Router;
				break;
			default:
				break;
		}
	}

	
	////users_tx_rate users_rx_rate signal_limit(-30) redirect_url("www.bhuwifi.com") idle_timeout(1200) force_timeout(21600) open_resource("") ssid("BhuWIFI-访客")
	public static ParamSharedNetworkDTO builderDefault(String type,boolean router){
		ParamSharedNetworkDTO dto = new ParamSharedNetworkDTO();
		dto.setUsers_tx_rate(WifiDeviceHelper.VistorWifi_Default_Users_tx_rate);
		dto.setUsers_rx_rate(WifiDeviceHelper.VistorWifi_Default_Users_rx_rate);
		dto.setSignal_limit(WifiDeviceHelper.VistorWifi_Default_Signal_limit);
		dto.setRedirect_url(WifiDeviceHelper.VistorWifi_Default_Redirect_url);
		dto.setIdle_timeout(WifiDeviceHelper.VistorWifi_Default_Idle_timeout);//
		dto.setForce_timeout(WifiDeviceHelper.VistorWifi_Default_Force_timeout);
		dto.setOpen_resource(WifiDeviceHelper.VistorWifi_Default_Open_resource);
		dto.setBlock_mode(router?WifiDeviceHelper.Default_BlockMode_Router:WifiDeviceHelper.Default_BlockMode_Bridge);
		dto.setComplete_isolate_ports(router?WifiDeviceHelper.Default_CompleteIsolatePorts_Router:WifiDeviceHelper.Default_CompleteIsolatePorts_Bridge);
		dto.setSsid(
				SharedNetworkType.Uplink.getKey().equals(type)?
						SharedNetworkType.Uplink.getDefalutSsid():
							SharedNetworkType.SafeSecure.getDefalutSsid());//WifiDeviceHelper.VistorWifi_Default_SSID);
		return dto;
	}
	
	public static ParamSharedNetworkDTO fufillWithDefault(ParamSharedNetworkDTO param,String type,boolean router){
		if(param == null) return builderDefault(type,router);
		
		if(param.getSignal_limit() == 0) param.setSignal_limit(WifiDeviceHelper.VistorWifi_Default_Signal_limit);
		if(StringUtils.isEmpty(param.getRedirect_url())) param.setRedirect_url(WifiDeviceHelper.VistorWifi_Default_Redirect_url);
		if(param.getIdle_timeout() == 0) param.setIdle_timeout(WifiDeviceHelper.VistorWifi_Default_Idle_timeout);
		if(param.getForce_timeout() == 0) param.setForce_timeout(WifiDeviceHelper.VistorWifi_Default_Force_timeout);
		if(StringUtils.isEmpty(param.getOpen_resource()) || param.getOpen_resource().indexOf(WifiDeviceHelper.VistorWifi_Default_Open_resource) == -1) {
			param.setOpen_resource(WifiDeviceHelper.VistorWifi_Default_Open_resource);
		}
		param.setBlock_mode(router?WifiDeviceHelper.Default_BlockMode_Router:WifiDeviceHelper.Default_BlockMode_Bridge);
		param.setComplete_isolate_ports(router?WifiDeviceHelper.Default_CompleteIsolatePorts_Router:WifiDeviceHelper.Default_CompleteIsolatePorts_Bridge);
		//if(StringUtils.isEmpty(param.getSsid())) param.setSsid(WifiDeviceHelper.VistorWifi_Default_SSID);
		if(StringUtils.isEmpty(param.getSsid())){
			param.setSsid(SharedNetworkType.Uplink.getKey().equals(type)?
					SharedNetworkType.Uplink.getDefalutSsid():
						SharedNetworkType.SafeSecure.getDefalutSsid());
		}
		return param;
	}
	
	public static void main(String[] argv){
		System.out.println(JsonHelper.getJSONString(fufillWithDefault(null,SharedNetworkType.Uplink.getKey(),false)));
		
		ParamSharedNetworkDTO param = new ParamSharedNetworkDTO();
		param.setSsid("GOOO理论");
		System.out.println(JsonHelper.getJSONString(fufillWithDefault(param,SharedNetworkType.Uplink.getKey(),true)));
	}
}
