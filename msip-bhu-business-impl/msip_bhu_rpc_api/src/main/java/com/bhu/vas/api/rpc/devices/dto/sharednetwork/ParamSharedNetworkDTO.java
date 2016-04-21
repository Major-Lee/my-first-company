package com.bhu.vas.api.rpc.devices.dto.sharednetwork;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.helper.VapEnumType.SharedNetworkType;
import com.bhu.vas.api.helper.WifiDeviceHelper;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.JsonHelper;

/**
 * 设备配置信息的ad
 * 广告信息配置
 * @author edmond
 *
 */
@SuppressWarnings("serial")
public class ParamSharedNetworkDTO implements java.io.Serializable{
	private String ntype;
	private String template;
	private String template_name;
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
	
/*	//此值不体现在参数传递中，是根据设备当前的工作模式来决定是什么值,就是参数在过程中进行初始化
	@JsonInclude(Include.NON_NULL)
	private String block_mode;
	//此值不体现在参数传递中，是根据设备当前的工作模式来决定是什么值,就是参数在过程中进行初始化
	@JsonInclude(Include.NON_NULL)
	private String complete_isolate_ports;
*/
	//uplink 特殊字段
	@JsonInclude(Include.NON_NULL)
	private String redirect_url;
	
	//securenetwork 特殊字段
	@JsonInclude(Include.NON_NULL)
	private String remote_auth_url;
	@JsonInclude(Include.NON_NULL)
	private String portal_server_url;
	@JsonInclude(Include.NON_NULL)
	private String dns_default_ip;
	
	
	public ParamSharedNetworkDTO() {
		this.ntype = VapEnumType.SharedNetworkType.Uplink.getKey();
	}
	public ParamSharedNetworkDTO(String ntype) {
		this.ntype = ntype;
	}

	public Object[] builderProperties(DeviceStatusExchangeDTO device_status) {
		Object[] properties = null;
		if(VapEnumType.SharedNetworkType.Uplink.getKey().equals(ntype)){
			if(VapEnumType.DeviceUnitType.isDualBandByOrigSwver(device_status.getOrig_swver())){//双频
				properties = new Object[14];
				properties[0] = WifiDeviceHelper.xmlContentEncoder(ssid);
				properties[1] = WifiDeviceHelper.xmlContentEncoder(ssid).concat(WifiDeviceHelper.Default_Dual_Suffix);
				properties[2] = users_tx_rate * 8;//转成大B-》小b的单位
				properties[3] = users_rx_rate * 8;//转成大B-》小b的单位
				properties[4] = users_tx_rate * 8;//转成大B-》小b的单位
				properties[5] = users_rx_rate * 8;//转成大B-》小b的单位
				if(WifiDeviceHelper.isWorkModeRouter(device_status.getWorkmode())){
					properties[6] = WifiDeviceHelper.Default_CompleteIsolatePorts_Router_Dual;
					properties[7] = WifiDeviceHelper.Default_BlockMode_Router;
				}else{
					properties[6] = WifiDeviceHelper.Default_CompleteIsolatePorts_Bridge;
					properties[7] = WifiDeviceHelper.Default_BlockMode_Bridge;
				}
				properties[8] = signal_limit;
				properties[9] = max_clients;
				properties[10] = idle_timeout;
				properties[11] = force_timeout;
				properties[12] = open_resource;
				properties[13] = redirect_url;
			}else{//单频
				properties = new Object[11];
				properties[0] = WifiDeviceHelper.xmlContentEncoder(ssid);
				properties[1] = users_tx_rate * 8;//转成大B-》小b的单位
				properties[2] = users_rx_rate * 8;//转成大B-》小b的单位
				if(WifiDeviceHelper.isWorkModeRouter(device_status.getWorkmode())){
					properties[3] = WifiDeviceHelper.Default_CompleteIsolatePorts_Router_Single;
					properties[4] = WifiDeviceHelper.Default_BlockMode_Router;
				}else{
					properties[3] = WifiDeviceHelper.Default_CompleteIsolatePorts_Bridge;
					properties[4] = WifiDeviceHelper.Default_BlockMode_Bridge;
				}
				properties[5] = signal_limit;
				properties[6] = max_clients;
				properties[7] = idle_timeout;
				properties[8] = force_timeout;
				properties[9] = open_resource;
				properties[10] = redirect_url;
			}
		}else{
			if(VapEnumType.DeviceUnitType.isDualBandByOrigSwver(device_status.getOrig_swver())){//双频
				properties = new Object[16];
				properties[0] = WifiDeviceHelper.xmlContentEncoder(ssid);
				properties[1] = WifiDeviceHelper.xmlContentEncoder(ssid).concat(WifiDeviceHelper.Default_Dual_Suffix);
				properties[2] = users_tx_rate * 8;//转成大B-》小b的单位
				properties[3] = users_rx_rate * 8;//转成大B-》小b的单位
				properties[4] = users_tx_rate * 8;//转成大B-》小b的单位
				properties[5] = users_rx_rate * 8;//转成大B-》小b的单位
				if(WifiDeviceHelper.isWorkModeRouter(device_status.getWorkmode())){
					properties[6] = WifiDeviceHelper.Default_CompleteIsolatePorts_Router_Dual;
					properties[7] = WifiDeviceHelper.Default_BlockMode_Router;
				}else{
					properties[6] = WifiDeviceHelper.Default_CompleteIsolatePorts_Bridge;
					properties[7] = WifiDeviceHelper.Default_BlockMode_Bridge;
				}
				properties[8] = signal_limit;
				properties[9] = max_clients;
				properties[10] = idle_timeout;
				properties[11] = force_timeout;
				properties[12] = open_resource;
				properties[13] = remote_auth_url;
				properties[14] = portal_server_url;
				properties[15] = dns_default_ip;
			}else{//单频
				properties = new Object[13];
				properties[0] = WifiDeviceHelper.xmlContentEncoder(ssid);
				properties[1] = users_tx_rate * 8;//转成大B-》小b的单位
				properties[2] = users_rx_rate * 8;//转成大B-》小b的单位
				if(WifiDeviceHelper.isWorkModeRouter(device_status.getWorkmode())){
					properties[3] = WifiDeviceHelper.Default_CompleteIsolatePorts_Router_Single;
					properties[4] = WifiDeviceHelper.Default_BlockMode_Router;
				}else{
					properties[3] = WifiDeviceHelper.Default_CompleteIsolatePorts_Bridge;
					properties[4] = WifiDeviceHelper.Default_BlockMode_Bridge;
				}
				properties[5] = signal_limit;
				properties[6] = max_clients;
				properties[7] = idle_timeout;
				properties[8] = force_timeout;
				properties[9] = open_resource;
				properties[10] = remote_auth_url;
				properties[11] = portal_server_url;
				properties[12] = dns_default_ip;
			}
		}
		
		
		return properties;
	}

	public String getTemplate_name() {
		return template_name;
	}
	public void setTemplate_name(String template_name) {
		this.template_name = template_name;
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

	/*public String getBlock_mode() {
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
	}*/

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
	
	/*public void switchWorkMode(boolean switchAct){
		switchWorkMode(switchAct?WifiDeviceHelper.SwitchMode_Bridge2Router_Act:WifiDeviceHelper.SwitchMode_Router2Bridge_Act);
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
	}*/



	public String getDns_default_ip() {
		return dns_default_ip;
	}

	public void setDns_default_ip(String dns_default_ip) {
		this.dns_default_ip = dns_default_ip;
	}
	public static ParamSharedNetworkDTO builderDefault(){
		return builderDefault(SharedNetworkType.SafeSecure.getKey());
	}
	////users_tx_rate users_rx_rate signal_limit(-30) redirect_url("www.bhuwifi.com") idle_timeout(1200) force_timeout(21600) open_resource("") ssid("BhuWIFI-访客")
	public static ParamSharedNetworkDTO builderDefault(String ntype){
		ParamSharedNetworkDTO param = new ParamSharedNetworkDTO();
		if(StringUtils.isEmpty(ntype)) ntype = (SharedNetworkType.SafeSecure.getKey());
		param.setNtype(ntype);
		param.setUsers_tx_rate(WifiDeviceHelper.SharedNetworkWifi_Default_Users_tx_rate);
		param.setUsers_rx_rate(WifiDeviceHelper.SharedNetworkWifi_Default_Users_rx_rate);
		param.setSignal_limit(WifiDeviceHelper.SharedNetworkWifi_Default_Signal_limit);
		param.setIdle_timeout(WifiDeviceHelper.SharedNetworkWifi_Default_Idle_timeout);//
		param.setForce_timeout(WifiDeviceHelper.SharedNetworkWifi_Default_Force_timeout);
		param.setOpen_resource(BusinessRuntimeConfiguration.SharedNetworkWifi_Default_Open_resource);
		param.setMax_clients(WifiDeviceHelper.SharedNetworkWifi_Default_Maxclients);
		//param.setBlock_mode(router?WifiDeviceHelper.Default_BlockMode_Router:WifiDeviceHelper.Default_BlockMode_Bridge);
		//param.setComplete_isolate_ports(router?WifiDeviceHelper.Default_CompleteIsolatePorts_Router:WifiDeviceHelper.Default_CompleteIsolatePorts_Bridge);
		if(SharedNetworkType.Uplink.getKey().equals(param.getNtype())){
			param.setSsid(SharedNetworkType.Uplink.getDefaultSsid());
			param.setRedirect_url(BusinessRuntimeConfiguration.SharedNetworkWifi_Default_Redirect_url);
			param.setRemote_auth_url(null);
			param.setPortal_server_url(null);
			param.setDns_default_ip(null);
		}else{
			param.setSsid(SharedNetworkType.SafeSecure.getDefaultSsid());
			param.setRemote_auth_url(BusinessRuntimeConfiguration.SharedNetworkWifi_Default_Remote_auth_url);
			param.setPortal_server_url(BusinessRuntimeConfiguration.SharedNetworkWifi_Default_Remote_portal_server_url);
			param.setDns_default_ip(BusinessRuntimeConfiguration.SharedNetworkWifi_Default_Remote_Dns_default_ip);
			param.setRedirect_url(null);
		}
		return param;
	}
	
	public static ParamSharedNetworkDTO fufillWithDefault(ParamSharedNetworkDTO param){
		if(param == null) return builderDefault(SharedNetworkType.SafeSecure.getKey());
		if(StringUtils.isEmpty(param.getNtype())) param.setNtype(SharedNetworkType.SafeSecure.getKey());
		
		if(param.getSignal_limit() == 0) param.setSignal_limit(WifiDeviceHelper.SharedNetworkWifi_Default_Signal_limit);
		//if(StringUtils.isEmpty(param.getRedirect_url())) param.setRedirect_url(WifiDeviceHelper.SharedNetworkWifi_Default_Redirect_url);
		if(param.getIdle_timeout() == 0) param.setIdle_timeout(WifiDeviceHelper.SharedNetworkWifi_Default_Idle_timeout);
		if(param.getForce_timeout() == 0) param.setForce_timeout(WifiDeviceHelper.SharedNetworkWifi_Default_Force_timeout);
		if(param.getMax_clients() == 0){
			param.setMax_clients(WifiDeviceHelper.SharedNetworkWifi_Default_Maxclients);
		}
		if(StringUtils.isEmpty(param.getOpen_resource())) {
			param.setOpen_resource(BusinessRuntimeConfiguration.SharedNetworkWifi_Default_Open_resource);
		}
		//param.setBlock_mode(router?WifiDeviceHelper.Default_BlockMode_Router:WifiDeviceHelper.Default_BlockMode_Bridge);
		//param.setComplete_isolate_ports(router?WifiDeviceHelper.Default_CompleteIsolatePorts_Router:WifiDeviceHelper.Default_CompleteIsolatePorts_Bridge);
		if(SharedNetworkType.Uplink.getKey().equals(param.getNtype())){
			if(StringUtils.isEmpty(param.getSsid())){
				param.setSsid(SharedNetworkType.Uplink.getDefaultSsid());
			}
			if(StringUtils.isEmpty(param.getRedirect_url())){
				param.setRedirect_url(BusinessRuntimeConfiguration.SharedNetworkWifi_Default_Redirect_url);
			}
			param.setRemote_auth_url(null);
			param.setPortal_server_url(null);
			param.setDns_default_ip(null);
		}else{
			if(StringUtils.isEmpty(param.getSsid())){
				param.setSsid(SharedNetworkType.SafeSecure.getDefaultSsid());
			}
			if(StringUtils.isEmpty(param.getRemote_auth_url())){
				param.setRemote_auth_url(BusinessRuntimeConfiguration.SharedNetworkWifi_Default_Remote_auth_url);
			}
			if(StringUtils.isEmpty(param.getPortal_server_url())){
				param.setPortal_server_url(BusinessRuntimeConfiguration.SharedNetworkWifi_Default_Remote_portal_server_url);
			}
			if(StringUtils.isEmpty(param.getDns_default_ip())){
				param.setDns_default_ip(BusinessRuntimeConfiguration.SharedNetworkWifi_Default_Remote_Dns_default_ip);
			}
			param.setRedirect_url(null);
		}
		return param;
	}
	
	public static boolean wasTemplateNameChanged(ParamSharedNetworkDTO paramDTO,ParamSharedNetworkDTO dbDTO){
		if(dbDTO == null) return true;
		if(paramDTO == null) return false;
		if(!paramDTO.getTemplate_name().equals(dbDTO.getTemplate_name())) 
			return true;
		return false;
	}
	public static boolean wasConfigChanged(ParamSharedNetworkDTO paramDTO,ParamSharedNetworkDTO dbDTO){
		if(dbDTO == null) return true;
		if(paramDTO == null) return false;
		if(!paramDTO.getNtype().equals(dbDTO.getNtype())){
			return true;
		}
		
		if(!paramDTO.getSsid().equals(dbDTO.getSsid())) return true;
		
		if(paramDTO.getUsers_rx_rate() != dbDTO.getUsers_rx_rate()) return true;
		if(paramDTO.getUsers_tx_rate() != dbDTO.getUsers_tx_rate()) return true;
		if(paramDTO.getSignal_limit() != dbDTO.getSignal_limit()) return true;
		if(paramDTO.getIdle_timeout() != dbDTO.getIdle_timeout()) return true;
		if(paramDTO.getForce_timeout() != dbDTO.getForce_timeout()) return true;
		if(paramDTO.getMax_clients() != dbDTO.getMax_clients()) return true;
		if(!paramDTO.getOpen_resource().equals(dbDTO.getOpen_resource())) return true;
		
		if(SharedNetworkType.Uplink.getKey().equals(paramDTO.getNtype())){
			if(!paramDTO.getRedirect_url().equals(dbDTO.getRedirect_url())) return true;
		}else{
			if(!paramDTO.getRemote_auth_url().equals(dbDTO.getRemote_auth_url())) return true;
			if(!paramDTO.getPortal_server_url().equals(dbDTO.getPortal_server_url())) return true;
			if(!paramDTO.getDns_default_ip().equals(dbDTO.getDns_default_ip())) return true;
		}
		return false;
	}
	
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	@Override
	public int hashCode() {
		if(this.template == null) 
			return StringUtils.EMPTY.hashCode();
		return this.template.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null) return false;
		if(o instanceof ParamSharedNetworkDTO){
			ParamSharedNetworkDTO oo = (ParamSharedNetworkDTO)o;
			return this.template.equals(oo.template);
		}
		return false;
	}
	public static void main(String[] argv){
/*		System.out.println(String.format(DeviceHelper.DeviceSetting_Start_SharedNetworkWifi_Uplink, ParamSharedNetworkDTO.builderDefault(null, true).builderProperties()));
		System.out.println(String.format(DeviceHelper.DeviceSetting_Start_SharedNetworkWifi_Uplink, ParamSharedNetworkDTO.builderDefault(null, false).builderProperties()));
		
		System.out.println(String.format(DeviceHelper.DeviceSetting_Start_SharedNetworkWifi_SafeSecure, ParamSharedNetworkDTO.builderDefault(SharedNetworkType.SafeSecure.getKey(), true).builderProperties()));
		System.out.println(String.format(DeviceHelper.DeviceSetting_Start_SharedNetworkWifi_SafeSecure, ParamSharedNetworkDTO.builderDefault(SharedNetworkType.SafeSecure.getKey(), false).builderProperties()));
*/	
		//System.out.println(String.format(DeviceHelper.DeviceSetting_Start_SharedNetworkWifi_Uplink, ParamSharedNetworkDTO.fufillWithDefault(new ParamSharedNetworkDTO()).builderProperties()));
		//System.out.println(String.format(DeviceHelper.DeviceSetting_Start_SharedNetworkWifi_SafeSecure, ParamSharedNetworkDTO.fufillWithDefault(new ParamSharedNetworkDTO(SharedNetworkType.SafeSecure.getKey())).builderProperties()));
		System.out.println(JsonHelper.getJSONString(ParamSharedNetworkDTO.builderDefault(SharedNetworkType.Uplink.getKey())));
		System.out.println(JsonHelper.getJSONString(ParamSharedNetworkDTO.builderDefault()));
	}
}
