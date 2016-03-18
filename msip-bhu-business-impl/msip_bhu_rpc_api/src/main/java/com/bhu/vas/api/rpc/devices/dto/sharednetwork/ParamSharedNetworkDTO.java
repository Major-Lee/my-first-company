package com.bhu.vas.api.rpc.devices.dto.sharednetwork;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.helper.VapEnumType.SharedNetworkType;
import com.bhu.vas.api.helper.WifiDeviceHelper;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.smartwork.msip.cores.helper.HtmlHelper;

/**
 * 设备配置信息的ad
 * 广告信息配置
 * @author edmond
 *
 */
@SuppressWarnings("serial")
public class ParamSharedNetworkDTO implements java.io.Serializable{
	private String ntype;
	//通用字段
	private String ssid;
	private int users_tx_rate;
	private int users_rx_rate;
	private int idle_timeout;
	private int force_timeout;
	private String open_resource;
	private int signal_limit;
	private int max_clients;
	//users_tx_rate users_rx_rate signal_limit(-30) redirect_url("www.bhuwifi.com") idle_timeout(1200) force_timeout(21600) open_resource("") ssid("BhuWIFI-访客")
	
	//此值不体现在参数传递中，是根据设备当前的工作模式来决定是什么值,就是参数在过程中进行初始化
	@JsonInclude(Include.NON_NULL)
	private String block_mode;
	//此值不体现在参数传递中，是根据设备当前的工作模式来决定是什么值,就是参数在过程中进行初始化
	@JsonInclude(Include.NON_NULL)
	private String complete_isolate_ports;

	//uplink 特殊字段
	private String redirect_url;
	
	//securenetwork 特殊字段
	private String remote_auth_url;
	private String portal_server_url;
	private String dns_default_ip;
	
	
	public ParamSharedNetworkDTO() {
		this.ntype = VapEnumType.SharedNetworkType.Uplink.getKey();
	}
	public ParamSharedNetworkDTO(String ntype) {
		this.ntype = ntype;
	}

	public Object[] builderProperties() {
		Object[] properties = null;
		if(VapEnumType.SharedNetworkType.Uplink.getKey().equals(ntype)){
			properties = new Object[11];
			properties[0] = HtmlHelper.htmlEncode(ssid);
			properties[1] = users_tx_rate * 8;//转成大B-》小b的单位
			properties[2] = users_rx_rate * 8;//转成大B-》小b的单位
			properties[3] = complete_isolate_ports;
			properties[4] = block_mode;
			properties[5] = signal_limit;
			properties[6] = max_clients;
			properties[7] = idle_timeout;
			properties[8] = force_timeout;
			properties[9] = open_resource;
			properties[10] = redirect_url;
		}else{
			properties = new Object[13];
			properties[0] = HtmlHelper.htmlEncode(ssid);
			properties[1] = users_tx_rate * 8;//转成大B-》小b的单位
			properties[2] = users_rx_rate * 8;//转成大B-》小b的单位
			properties[3] = complete_isolate_ports;
			properties[4] = block_mode;
			properties[5] = signal_limit;
			properties[6] = max_clients;
			properties[7] = idle_timeout;
			properties[8] = force_timeout;
			properties[9] = open_resource;
			properties[10] = remote_auth_url;
			properties[11] = portal_server_url;
			properties[12] = dns_default_ip;
		}
		
		
		return properties;
	}

	public int getMax_clients() {
		return max_clients;
	}

	public void setMax_clients(int max_clients) {
		this.max_clients = max_clients;
	}

	public String getNtype() {
		return ntype;
	}

	public void setNtype(String ntype) {
		this.ntype = ntype;
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

	public String getRemote_auth_url() {
		return remote_auth_url;
	}

	public void setRemote_auth_url(String remote_auth_url) {
		this.remote_auth_url = remote_auth_url;
	}

	public String getPortal_server_url() {
		return portal_server_url;
	}

	public void setPortal_server_url(String portal_server_url) {
		this.portal_server_url = portal_server_url;
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



	public String getDns_default_ip() {
		return dns_default_ip;
	}

	public void setDns_default_ip(String dns_default_ip) {
		this.dns_default_ip = dns_default_ip;
	}

	////users_tx_rate users_rx_rate signal_limit(-30) redirect_url("www.bhuwifi.com") idle_timeout(1200) force_timeout(21600) open_resource("") ssid("BhuWIFI-访客")
	public static ParamSharedNetworkDTO builderDefault(String ntype){
		ParamSharedNetworkDTO param = new ParamSharedNetworkDTO();
		if(StringUtils.isEmpty(ntype)) ntype = (SharedNetworkType.Uplink.getKey());
		param.setNtype(ntype);
		param.setUsers_tx_rate(WifiDeviceHelper.SharedNetworkWifi_Default_Users_tx_rate);
		param.setUsers_rx_rate(WifiDeviceHelper.SharedNetworkWifi_Default_Users_rx_rate);
		param.setSignal_limit(WifiDeviceHelper.SharedNetworkWifi_Default_Signal_limit);
		param.setIdle_timeout(WifiDeviceHelper.SharedNetworkWifi_Default_Idle_timeout);//
		param.setForce_timeout(WifiDeviceHelper.SharedNetworkWifi_Default_Force_timeout);
		param.setOpen_resource(WifiDeviceHelper.SharedNetworkWifi_Default_Open_resource);
		param.setMax_clients(WifiDeviceHelper.SharedNetworkWifi_Default_Maxclients);
		//param.setBlock_mode(router?WifiDeviceHelper.Default_BlockMode_Router:WifiDeviceHelper.Default_BlockMode_Bridge);
		//param.setComplete_isolate_ports(router?WifiDeviceHelper.Default_CompleteIsolatePorts_Router:WifiDeviceHelper.Default_CompleteIsolatePorts_Bridge);
		if(SharedNetworkType.Uplink.getKey().equals(param.getNtype())){
			param.setSsid(SharedNetworkType.Uplink.getDefalutSsid());
			param.setRedirect_url(WifiDeviceHelper.SharedNetworkWifi_Default_Redirect_url);
		}else{
			param.setSsid(SharedNetworkType.SafeSecure.getDefalutSsid());
			param.setRemote_auth_url(WifiDeviceHelper.SharedNetworkWifi_Default_Remote_auth_url);
			param.setPortal_server_url(WifiDeviceHelper.SharedNetworkWifi_Default_Remote_portal_server_url);
			param.setDns_default_ip(WifiDeviceHelper.SharedNetworkWifi_Default_Remote_Dns_default_ip);
		}
		return param;
	}
	
	public static ParamSharedNetworkDTO fufillWithDefault(ParamSharedNetworkDTO param){
		if(param == null) return builderDefault(SharedNetworkType.Uplink.getKey());
		if(StringUtils.isEmpty(param.getNtype())) param.setNtype(SharedNetworkType.Uplink.getKey());
		if(param.getSignal_limit() == 0) param.setSignal_limit(WifiDeviceHelper.SharedNetworkWifi_Default_Signal_limit);
		//if(StringUtils.isEmpty(param.getRedirect_url())) param.setRedirect_url(WifiDeviceHelper.SharedNetworkWifi_Default_Redirect_url);
		if(param.getIdle_timeout() == 0) param.setIdle_timeout(WifiDeviceHelper.SharedNetworkWifi_Default_Idle_timeout);
		if(param.getForce_timeout() == 0) param.setForce_timeout(WifiDeviceHelper.SharedNetworkWifi_Default_Force_timeout);
		if(param.getMax_clients() == 0){
			param.setMax_clients(WifiDeviceHelper.SharedNetworkWifi_Default_Maxclients);
		}
		if(StringUtils.isEmpty(param.getOpen_resource())) {
			param.setOpen_resource(WifiDeviceHelper.SharedNetworkWifi_Default_Open_resource);
		}
		//param.setBlock_mode(router?WifiDeviceHelper.Default_BlockMode_Router:WifiDeviceHelper.Default_BlockMode_Bridge);
		//param.setComplete_isolate_ports(router?WifiDeviceHelper.Default_CompleteIsolatePorts_Router:WifiDeviceHelper.Default_CompleteIsolatePorts_Bridge);
		if(SharedNetworkType.Uplink.getKey().equals(param.getNtype())){
			if(StringUtils.isEmpty(param.getSsid())){
				param.setSsid(SharedNetworkType.Uplink.getDefalutSsid());
			}
			if(StringUtils.isEmpty(param.getRedirect_url())){
				param.setRedirect_url(WifiDeviceHelper.SharedNetworkWifi_Default_Redirect_url);
			}
		}else{
			if(StringUtils.isEmpty(param.getSsid())){
				param.setSsid(SharedNetworkType.SafeSecure.getDefalutSsid());
			}
			if(StringUtils.isEmpty(param.getRemote_auth_url())){
				param.setRemote_auth_url(WifiDeviceHelper.SharedNetworkWifi_Default_Remote_auth_url);
			}
			if(StringUtils.isEmpty(param.getPortal_server_url())){
				param.setPortal_server_url(WifiDeviceHelper.SharedNetworkWifi_Default_Remote_portal_server_url);
			}
			if(StringUtils.isEmpty(param.getDns_default_ip())){
				param.setDns_default_ip(WifiDeviceHelper.SharedNetworkWifi_Default_Remote_Dns_default_ip);
			}
		}
		return param;
	}
	
	public static boolean wasChanged(ParamSharedNetworkDTO param1,ParamSharedNetworkDTO param2){
		if(param1 == null || param2 == null) return false;
		if(!param1.getNtype().equals(param2.getNtype())){
			return false;
		}
		if(param1.getUsers_rx_rate() != param2.getUsers_rx_rate()) return false;
		if(param1.getUsers_tx_rate() != param2.getUsers_tx_rate()) return false;
		if(param1.getSignal_limit() != param2.getSignal_limit()) return false;
		if(param1.getIdle_timeout() != param2.getIdle_timeout()) return false;
		if(param1.getForce_timeout() != param2.getForce_timeout()) return false;
		if(param1.getMax_clients() != param2.getMax_clients()) return false;
		if(!param1.getOpen_resource().equals(param2.getOpen_resource())) return false;
		
		if(SharedNetworkType.Uplink.getKey().equals(param1.getNtype())){
			if(!param1.getRedirect_url().equals(param2.getRedirect_url())) return false;
		}else{
			if(!param1.getRemote_auth_url().equals(param2.getRemote_auth_url())) return false;
			if(!param1.getPortal_server_url().equals(param2.getPortal_server_url())) return false;
			if(!param1.getDns_default_ip().equals(param2.getDns_default_ip())) return false;
		}
		return true;
	}
	
	
	public static void main(String[] argv){
/*		System.out.println(String.format(DeviceHelper.DeviceSetting_Start_SharedNetworkWifi_Uplink, ParamSharedNetworkDTO.builderDefault(null, true).builderProperties()));
		System.out.println(String.format(DeviceHelper.DeviceSetting_Start_SharedNetworkWifi_Uplink, ParamSharedNetworkDTO.builderDefault(null, false).builderProperties()));
		
		System.out.println(String.format(DeviceHelper.DeviceSetting_Start_SharedNetworkWifi_SafeSecure, ParamSharedNetworkDTO.builderDefault(SharedNetworkType.SafeSecure.getKey(), true).builderProperties()));
		System.out.println(String.format(DeviceHelper.DeviceSetting_Start_SharedNetworkWifi_SafeSecure, ParamSharedNetworkDTO.builderDefault(SharedNetworkType.SafeSecure.getKey(), false).builderProperties()));
*/	
		//System.out.println(String.format(DeviceHelper.DeviceSetting_Start_SharedNetworkWifi_Uplink, ParamSharedNetworkDTO.fufillWithDefault(new ParamSharedNetworkDTO()).builderProperties()));
		//System.out.println(String.format(DeviceHelper.DeviceSetting_Start_SharedNetworkWifi_SafeSecure, ParamSharedNetworkDTO.fufillWithDefault(new ParamSharedNetworkDTO(SharedNetworkType.SafeSecure.getKey())).builderProperties()));
	}
}
