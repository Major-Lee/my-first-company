package com.bhu.vas.api.rpc.devices.dto.sharednetwork;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.dto.DistributorType;
import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.helper.VapEnumType.SharedNetworkType;
import com.bhu.vas.api.helper.WifiDeviceHelper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;

/**
 * 设备配置信息的ad
 * 广告信息配置
 * @author edmond
 *
 */
@SuppressWarnings("serial")
public class ParamSharedNetworkDTO implements java.io.Serializable{
	
	@JsonIgnore
	public static final String Default_City_Range_Cash_PC = "1.5-3.5";
	@JsonIgnore
	public static final String Default_City_Range_Cash_Mobile = "0.1-0.9";
	@JsonIgnore
	public static final String Default_Channel_Range_Cash_PC = "1.5-3.5";
	@JsonIgnore
	public static final String Default_Channel_Range_Cash_Mobile = "0.1-0.9";
	@JsonIgnore
	public static final String Default_AIT = "14400";
	@JsonIgnore
	public static final String Default_Free_AIT = "14400";

	
	private String ntype;
	private String template;
	private String template_name;
	private long ts;
	//通用字段
	private String ssid;
	private int users_tx_rate;
	private int users_rx_rate;
	private int idle_timeout;
	private int force_timeout;
	private String open_resource;

//	@JsonInclude(Include.NON_NULL)
	private List<OpenMacDTO> open_macs;

	@JsonInclude(Include.NON_NULL)
	private String open_resource_ad; //全城热播所使用的广告所需要放行的白名单
	
	@JsonInclude(Include.NON_NULL)
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
	
	
	private String range_cash_pc;
	private String range_cash_mobile;
	private String ait_pc;
	private String ait_mobile;
	//pc免费上网时长
	private String free_ait_pc;
	//手机免费上网时长
	private String free_ait_mobile;
	//是否开启免费上网
	private int isfree = 0;
	//是否开启首次认证
	private int firstLogin = 0;

	
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
				properties = new Object[15];
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
				properties[12] = combineOpenResource();
				properties[13] = getOpen_mac_string();
				properties[14] = redirect_url;
			}else{//单频
				properties = new Object[12];
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
				properties[9] = combineOpenResource();
				properties[10] = getOpen_mac_string();
				properties[11] = redirect_url;
			}
		}else{
			if(VapEnumType.DeviceUnitType.isDualBandByOrigSwver(device_status.getOrig_swver())){//双频
				properties = new Object[17];
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
				properties[12] = combineOpenResource();
				properties[13] = getOpen_mac_string();
				properties[14] = remote_auth_url;
				properties[15] = portal_server_url;
				properties[16] = dns_default_ip;
			}else{//单频
				properties = new Object[14];
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
				properties[9] = combineOpenResource();
				properties[10] = getOpen_mac_string();
				properties[11] = remote_auth_url;
				properties[12] = portal_server_url;
				properties[13] = dns_default_ip;
			}
		}
		
		
		return properties;
	}

	public long getTs() {
		return ts;
	}
	public void setTs(long ts) {
		this.ts = ts;
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
	public static ParamSharedNetworkDTO builderDefault(String dtype){
		return builderDefault(SharedNetworkType.SafeSecure.getKey(), dtype);
	}
	
	private String combineOpenResource(){
		StringBuilder sb = new StringBuilder(this.open_resource);
		if(!StringUtils.isEmpty(this.open_resource_ad)){
			sb.append(StringHelper.COMMA_STRING_GAP).append(open_resource_ad);
		}
		return sb.toString();	
	}
	
	////users_tx_rate users_rx_rate signal_limit(-30) redirect_url("www.bhuwifi.com") idle_timeout(1200) force_timeout(21600) open_resource("") ssid("BhuWIFI-访客")
	public static ParamSharedNetworkDTO builderDefault(String ntype, String distributor_type){
		ParamSharedNetworkDTO param = new ParamSharedNetworkDTO();
		if(StringUtils.isEmpty(ntype)) ntype = (SharedNetworkType.SafeSecure.getKey());
		param.setNtype(ntype);
		param.setUsers_tx_rate(WifiDeviceHelper.SharedNetworkWifi_Default_Users_tx_rate);
		param.setUsers_rx_rate(WifiDeviceHelper.SharedNetworkWifi_Default_Users_rx_rate);
		param.setSignal_limit(WifiDeviceHelper.SharedNetworkWifi_Default_Signal_limit);
		param.setIdle_timeout(WifiDeviceHelper.SharedNetworkWifi_Default_Idle_timeout);//
		param.setForce_timeout(WifiDeviceHelper.SharedNetworkWifi_Default_Force_timeout);
		//param.setOpen_resource(BusinessRuntimeConfiguration.SharedNetworkWifi_Default_Open_resource);
		param.setMax_clients(WifiDeviceHelper.SharedNetworkWifi_Default_Maxclients);
		if(DistributorType.City.getType().equals(distributor_type)){
			param.setRange_cash_mobile(ParamSharedNetworkDTO.Default_City_Range_Cash_Mobile);
			param.setRange_cash_pc(ParamSharedNetworkDTO.Default_City_Range_Cash_PC);
		} else {
			param.setRange_cash_mobile(ParamSharedNetworkDTO.Default_Channel_Range_Cash_Mobile);
			param.setRange_cash_pc(ParamSharedNetworkDTO.Default_Channel_Range_Cash_PC);
		}
		param.setAit_mobile(ParamSharedNetworkDTO.Default_AIT);
		param.setAit_pc(ParamSharedNetworkDTO.Default_AIT);
		param.setFree_ait_mobile(ParamSharedNetworkDTO.Default_Free_AIT);
		param.setFree_ait_pc(ParamSharedNetworkDTO.Default_Free_AIT);
		
		//param.setBlock_mode(router?WifiDeviceHelper.Default_BlockMode_Router:WifiDeviceHelper.Default_BlockMode_Bridge);
		//param.setComplete_isolate_ports(router?WifiDeviceHelper.Default_CompleteIsolatePorts_Router:WifiDeviceHelper.Default_CompleteIsolatePorts_Bridge);
		if(SharedNetworkType.Uplink.getKey().equals(param.getNtype())){
			param.setSsid(SharedNetworkType.Uplink.getDefaultSsid());
			param.setRedirect_url(BusinessRuntimeConfiguration.SharedNetworkWifi_Default_Redirect_url);
			param.setRemote_auth_url(null);
			param.setPortal_server_url(null);
			param.setDns_default_ip(null);
			param.setOpen_resource(BusinessRuntimeConfiguration.SharedNetworkWifi_Default_Uplink_Open_resource);
		}else{
			if(SharedNetworkType.SafeSecure.getKey().equals(param.getNtype())){
				param.setSsid(SharedNetworkType.SafeSecure.getDefaultSsid());
				param.setRemote_auth_url(BusinessRuntimeConfiguration.SharedNetworkWifi_Default_SafeSecure_Remote_auth_url);
			}else{
				param.setSsid(SharedNetworkType.SmsSecure.getDefaultSsid());
				param.setRemote_auth_url(BusinessRuntimeConfiguration.SharedNetworkWifi_Default_SmsSecure_Remote_auth_url);
			}
			param.setPortal_server_url(BusinessRuntimeConfiguration.SharedNetworkWifi_Default_Remote_portal_server_url);
			param.setDns_default_ip(BusinessRuntimeConfiguration.SharedNetworkWifi_Default_Remote_Dns_default_ip);
			param.setRedirect_url(null);
			param.setOpen_resource(BusinessRuntimeConfiguration.SharedNetworkWifi_Default_SafeSecure_Open_resource);
		}
		return param;
	}
	
	public static ParamSharedNetworkDTO fufillWithDefault(ParamSharedNetworkDTO param, String distributor_type){
		if(param == null) return builderDefault(SharedNetworkType.SafeSecure.getKey(), distributor_type);
		if(StringUtils.isEmpty(param.getNtype())) param.setNtype(SharedNetworkType.SafeSecure.getKey());
		
		if(param.getSignal_limit() == 0) param.setSignal_limit(WifiDeviceHelper.SharedNetworkWifi_Default_Signal_limit);
		//if(StringUtils.isEmpty(param.getRedirect_url())) param.setRedirect_url(WifiDeviceHelper.SharedNetworkWifi_Default_Redirect_url);
		if(param.getIdle_timeout() == 0) param.setIdle_timeout(WifiDeviceHelper.SharedNetworkWifi_Default_Idle_timeout);
		if(param.getForce_timeout() == 0) param.setForce_timeout(WifiDeviceHelper.SharedNetworkWifi_Default_Force_timeout);
		if(param.getMax_clients() == 0){
			param.setMax_clients(WifiDeviceHelper.SharedNetworkWifi_Default_Maxclients);
		}
		/*if(StringUtils.isEmpty(param.getOpen_resource())) {
			param.setOpen_resource(BusinessRuntimeConfiguration.SharedNetworkWifi_Default_Open_resource);
		}*/
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
			if(StringUtils.isEmpty(param.getOpen_resource())) {
				param.setOpen_resource(BusinessRuntimeConfiguration.SharedNetworkWifi_Default_Uplink_Open_resource);
			}
//			param.setOpen_mac(null);
		}else{
			if(SharedNetworkType.SafeSecure.getKey().equals(param.getNtype())){
				if(StringUtils.isEmpty(param.getSsid())){
					param.setSsid(SharedNetworkType.SafeSecure.getDefaultSsid());
				}
				if(StringUtils.isEmpty(param.getRemote_auth_url())){
					param.setRemote_auth_url(BusinessRuntimeConfiguration.SharedNetworkWifi_Default_SafeSecure_Remote_auth_url);
				}
				if(StringUtils.isEmpty(param.getRange_cash_mobile())){
					param.setRange_cash_mobile((DistributorType.City.getType().equals(distributor_type))?ParamSharedNetworkDTO.Default_City_Range_Cash_Mobile:ParamSharedNetworkDTO.Default_Channel_Range_Cash_Mobile);
				}
				if(StringUtils.isEmpty(param.getRange_cash_pc())){
					param.setRange_cash_pc((DistributorType.City.getType().equals(distributor_type))?ParamSharedNetworkDTO.Default_City_Range_Cash_PC:ParamSharedNetworkDTO.Default_Channel_Range_Cash_PC);
				}
				if(StringUtils.isEmpty(param.getAit_mobile()))
					param.setAit_mobile(ParamSharedNetworkDTO.Default_AIT);
				if(StringUtils.isEmpty(param.getAit_pc()))
					param.setAit_pc(ParamSharedNetworkDTO.Default_AIT);
				if(StringUtils.isEmpty(param.getFree_ait_mobile()))
					param.setFree_ait_mobile(ParamSharedNetworkDTO.Default_Free_AIT);
				if(StringUtils.isEmpty(param.getFree_ait_pc()))
					param.setFree_ait_pc(ParamSharedNetworkDTO.Default_Free_AIT);
			}else{
				if(StringUtils.isEmpty(param.getSsid())){
					param.setSsid(SharedNetworkType.SmsSecure.getDefaultSsid());
				}
				if(StringUtils.isEmpty(param.getRemote_auth_url())){
					param.setRemote_auth_url(BusinessRuntimeConfiguration.SharedNetworkWifi_Default_SmsSecure_Remote_auth_url);
				}
			}
			
			if(StringUtils.isEmpty(param.getPortal_server_url())){
				param.setPortal_server_url(BusinessRuntimeConfiguration.SharedNetworkWifi_Default_Remote_portal_server_url);
			}
			if(StringUtils.isEmpty(param.getDns_default_ip())){
				param.setDns_default_ip(BusinessRuntimeConfiguration.SharedNetworkWifi_Default_Remote_Dns_default_ip);
			}
			param.setRedirect_url(null);
			if(StringUtils.isEmpty(param.getOpen_resource())) {
				param.setOpen_resource(BusinessRuntimeConfiguration.SharedNetworkWifi_Default_SafeSecure_Open_resource);
			}
//			param.setOpen_mac(null);
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

	public static boolean wasServerRelatedConfigChanged(ParamSharedNetworkDTO paramDTO,ParamSharedNetworkDTO dbDTO){
		if(dbDTO == null) return true;
		if(paramDTO == null) return false;
		
		if(!paramDTO.getTemplate_name().equals(dbDTO.getTemplate_name())) return true;
		if(!paramDTO.getRange_cash_mobile().equals(dbDTO.getRange_cash_mobile())) return true;
		if(!paramDTO.getRange_cash_pc().equals(dbDTO.getRange_cash_pc())) return true;
		if(!paramDTO.getAit_mobile().equals(dbDTO.getAit_mobile())) return true;
		if(!paramDTO.getAit_pc().equals(dbDTO.getAit_pc())) return true;
		if(!paramDTO.getFree_ait_mobile().equals(dbDTO.getFree_ait_mobile())) return true;
		if(!paramDTO.getFree_ait_pc().equals(dbDTO.getFree_ait_pc())) return true;
		if(paramDTO.getIsfree() != dbDTO.getIsfree()) return true;
		if(paramDTO.getFirstLogin() != dbDTO.getFirstLogin()) return true;
		
		return false;
	}

	public static boolean wasDeviceRelatedConfigChanged(ParamSharedNetworkDTO paramDTO,ParamSharedNetworkDTO dbDTO){
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
		
		String paramStr = paramDTO.getOpen_mac_string();
		String dbdtoStr = dbDTO.getOpen_mac_string();
	
		if(StringUtils.isEmpty(paramStr)){
			if(StringUtils.isNotEmpty(dbdtoStr))
				return true;
		} else if(!paramStr.equals(dbdtoStr)){
			return true;
		}
		
		if(SharedNetworkType.Uplink.getKey().equals(paramDTO.getNtype())){
			if(!paramDTO.getRedirect_url().equals(dbDTO.getRedirect_url())) return true;
		}else{
			if(!paramDTO.getRemote_auth_url().equals(dbDTO.getRemote_auth_url())) return true;
			if(!paramDTO.getPortal_server_url().equals(dbDTO.getPortal_server_url())) return true;
			if(!paramDTO.getDns_default_ip().equals(dbDTO.getDns_default_ip())) return true;
		}
		return false;
	}
	
	public String getFree_ait_pc() {
		return free_ait_pc;
	}
	public void setFree_ait_pc(String free_ait_pc) {
		this.free_ait_pc = free_ait_pc;
	}
	public String getFree_ait_mobile() {
		return free_ait_mobile;
	}
	public void setFree_ait_mobile(String free_ait_mobile) {
		this.free_ait_mobile = free_ait_mobile;
	}
	public String getRange_cash_pc() {
		return range_cash_pc;
	}
	public void setRange_cash_pc(String range_cash_pc) {
		this.range_cash_pc = range_cash_pc;
	}
	public String getRange_cash_mobile() {
		return range_cash_mobile;
	}
	public void setRange_cash_mobile(String range_cash_mobile) {
		this.range_cash_mobile = range_cash_mobile;
	}
	public String getAit_pc() {
		return ait_pc;
	}
	public void setAit_pc(String ait_pc) {
		this.ait_pc = ait_pc;
	}
	public String getAit_mobile() {
		return ait_mobile;
	}
	public void setAit_mobile(String ait_mobile) {
		this.ait_mobile = ait_mobile;
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
	
	public int getIsfree() {
		return isfree;
	}
	public void setIsfree(int isfree) {
		this.isfree = isfree;
	}
	public int getFirstLogin() {
		return firstLogin;
	}
	public void setFirstLogin(int firstLogin) {
		this.firstLogin = firstLogin;
	}
	
	public String getOpen_resource_ad() {
		return open_resource_ad;
	}
	public void setOpen_resource_ad(String open_resource_ad) {
		this.open_resource_ad = open_resource_ad;
	}
	
	public List<OpenMacDTO> getOpen_macs() {
		return open_macs;
	}
	public void setOpen_macs(List<OpenMacDTO> open_mac) {
		this.open_macs = open_mac;
	}
	
	private String getOpen_mac_string(){
		if(open_macs == null || open_macs.isEmpty())
			return null;
		StringBuffer sb = new StringBuffer();
		int count = 0;
		for(OpenMacDTO dto:open_macs){
			if(!StringHelper.isValidMac(dto.getMac()))
				continue;
			if(count != 0)
				sb.append(StringHelper.COMMA_STRING_GAP);
			sb.append(dto.getMac());
			count ++;
		}
		return sb.toString();
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
		System.out.println(JsonHelper.getJSONString(ParamSharedNetworkDTO.builderDefault(null)));
	}
}
