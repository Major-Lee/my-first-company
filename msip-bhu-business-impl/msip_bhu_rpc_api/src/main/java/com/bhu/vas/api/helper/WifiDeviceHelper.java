package com.bhu.vas.api.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.helper.VapEnumType.DeviceUnitType;
import com.bhu.vas.api.rpc.devices.dto.DeviceVersion;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.HtmlHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;


public class WifiDeviceHelper {
	//配置通用开关可用
	public static final String Enable = "enable";
	//配置通用开关不可用
	public static final String Disable = "disable";
	
	public static final String WifiSniffer_Start_Sta_Sniffer = "enable";
	public static final String WifiSniffer_Stop_Sta_Sniffer  = "disable";
	
	public static final String WifiTimer_Timer_On = "on";
	public static final String WifiTimer_Timer_Off = "off";
	public static final String WifiTimer_Days = "days";
	public static final String WifiTimer_Default_Days = "1234567";
	public static final String WifiTimer_Default_Timeslot = "00:00:00-24:00:00";
	public static final String[] WifiTimer_Default_Timeslot_Array = {"00:00:00","24:00:00"};

	public static final String Upgrade_Default_BeginTime = "02:00:00";
	public static final String Upgrade_Default_EndTime = "04:00:00";
	
	public static final int Upgrade_Module_Default_Retry_Count = 1;
	public static final int Upgrade_Module_Default_RetryInterval = 60;
	
	//public final static String WIFI_URouter_DEVICE_ORIGIN_MODEL = "uRouter";
	public final static boolean WIFI_DEVICE_UPGRADE_FW = true;
	public final static boolean WIFI_DEVICE_UPGRADE_OM = false;
	
	public final static String WorkMode_Router = "router-ap";
	public final static String WorkMode_Bridge = "bridge-ap";
	public final static String Default_BlockMode_Router = "route";
	public final static String Default_BlockMode_Bridge = "bridge";
	public final static String Default_CompleteIsolatePorts_Router = "wlan3";
	public final static String Default_CompleteIsolatePorts_Bridge = "";
	public final static int SwitchMode_NoAction = -1;
	public final static int SwitchMode_Router2Bridge_Act = 1;
	public final static int SwitchMode_Bridge2Router_Act = 0;
	
	
	public static final int Boot_On_Reset_Happen = 1;
	public static final int Boot_On_Reset_NotHappen = 0;
	//private static Set<String> URouter_HdTypes = new HashSet<String>();
	//private static Set<String> Soc_HdTypes = new HashSet<String>();
	private static Set<String> vapExceptDevices = new HashSet<String>();
	
	
	public final static String VistorWifi_Default_Redirect_url = "www.bhuwifi.com";
	public final static String VistorWifi_Default_Open_resource = "bhuwifi.com,bhunetworks.com";
	//public final static String VistorWifi_Default_SSID = "BhuWiFi-访客";
	
	
	public final static int VistorWifi_Default_Users_tx_rate= 256;//单位 KB/s
	public final static int VistorWifi_Default_Users_rx_rate= 256;//单位 KB/s
	public final static int VistorWifi_Default_Signal_limit= -30;
	public final static int VistorWifi_Default_Idle_timeout= 3*60*60;
	public final static int VistorWifi_Default_Force_timeout= 12*60*60;

	//public final static String VistorWifi_Default_SSID = "BhuWiFi-访客";
	
	//KB/s 
	public final static int SharedNetworkWifi_Default_Users_tx_rate= 256;//2Mb/s
	public final static int SharedNetworkWifi_Default_Users_rx_rate= 256;//2Mb/s
	public final static int SharedNetworkWifi_Default_Signal_limit= -30;
	public final static int SharedNetworkWifi_Default_Idle_timeout= 20*60;//20分钟
	public final static int SharedNetworkWifi_Default_Force_timeout= 6*60*60;//6小时
	public final static int SharedNetworkWifi_Default_Maxclients= 128;
	//public final static String VistorWifi_Default_SSID_WhenEmpty = "BhuWiFi-访客";
	
	static{
		/*URouter_HdTypes.add("H106");
		//Mass AP H103 H110
		Soc_HdTypes.add("H103");
		Soc_HdTypes.add("H110");
		//Mass AP Pro H201 H303
		Soc_HdTypes.add("H201");
		Soc_HdTypes.add("H303");*/
		//vapExceptDevices.add("84:82:f4:23:06:68");
	}
	
	/**
	 * 共享网络支持的设备的判定
	 * 标准的版本号 需要valid
	 * 大版本号 >= 1.5.6 并且为uRouter设备
	 * @param orig_swver
	 * @return
	 */
	public static boolean deviceSharedNetworkStrategy(String orig_swver){
		DeviceVersion ver = DeviceVersion.parser(orig_swver);
		if(ver == null || !ver.valid()) return false;
		if(BusinessRuntimeConfiguration.Device_SharedNetwork_DUT.equals(ver.getDut())){
			String[] orig_swver1_versions = ver.parseDeviceSwverVersion();
			int top_ret = StringHelper.compareVersion(orig_swver1_versions[0], BusinessRuntimeConfiguration.Device_SharedNetwork_Top_Version);
			if(top_ret>=0){
				return true;
			}
		}
		return false;
	}
	
	public static String dutDevice(String orig_swver){
		DeviceVersion parser = DeviceVersion.parser(orig_swver);
		if(parser != null){
			return parser.getDut();
		}
		return null;
	}
	
	public static boolean isURouterDevice(String orig_swver) {
		DeviceVersion ver = DeviceVersion.parser(orig_swver);
		if(ver == null || !ver.valid()) return false;
		return ver.wasDutURouter();
	}
	
	public static boolean isCWifiDevice(String orig_swver) {
		return DeviceUnitType.isCWifi(orig_swver);
		/*DeviceVersion ver = DeviceVersion.parser(orig_swver);
		if(ver == null || !ver.valid()) return false;
		return ver.wasDutURouter();*/
	}
	public static boolean isWorkModeRouter(String work_mode){
		//如果为空的情况下，则缺省返回true
		if(StringUtils.isEmpty(work_mode)) return true;
		return WorkMode_Router.equals(work_mode);
	}
	//act 0代表bridge to router 1反之
	public static void deviceWorkModeNeedChanged(String d_work_mode,int act, String linkmode_value){
		if(act <0 || act >1)
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_ERROR,new String[]{"mode:"+act});
//		String paramWorkMode = SwitchMode_Router2Bridge_Act==act?WorkMode_Bridge:WorkMode_Router;
		String paramWorkMode = null;
		if(SwitchMode_Router2Bridge_Act==act){
			paramWorkMode = WorkMode_Bridge;
			if(!DeviceHelper.isDhcpcLinkMode(linkmode_value)){
				throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_SETTING_WORKMODE_ROUTER2BRIDGE_DHCPC,new String[]{linkmode_value});
			}
		}else{
			paramWorkMode = WorkMode_Router;
		}
		if(paramWorkMode.equals(d_work_mode)) {
			throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_SETTING_WORKMODE_NOCHANGED,new String[]{d_work_mode});
		}


	}
	
	/*public static boolean isURouterDevice(String orig_model) {
		DeviceVersion ver = DeviceVersion.parser(orig_swver);
		if(ver == null) return false;
		return ver.wasDstURouter();
		
		ssss
		return WIFI_URouter_DEVICE_ORIGIN_MODEL.equalsIgnoreCase(orig_model);
	}*/
	
	/*public static boolean isURouterHdType(String hd_type) {
		return DeviceUnitType.isURouterHdType(hd_type);
		//return URouter_HdTypes.contains(hd_type);
	}
	public static boolean isSocHdType(String hd_type) {
		return DeviceUnitType.isSocHdType(hd_type);
		//return Soc_HdTypes.contains(hd_type);
	}*/
	/**
	 * uRouter 设备 或者SOC设备
	 * 开启设备终端自动上报（uRouter( TU  TS TC)和 SOC（ TS TC ） ）支持
	 * @param orig_model
	 * @return
	 */
	/*public static boolean isDeviceNeedOnlineTeminalQuery(String orig_model,String orig_swver){
		if(isURouterDevice(orig_model)){
			return true;
		}else{
			DeviceVersion parser = DeviceVersion.parser(orig_swver);
			if(parser.wasDutURouter() || parser.wasDutSoc()){
				return true;
			}
		}
		return false;
	}*/
	
	public static boolean isLocationCMDSupported(){
		return false;
	}
	
	/*public static boolean isVapModuleSupported(String moduleBuild){
		return StringUtils.isNotEmpty(moduleBuild);
	}*/
	
	//private static final String VapModuleVersion = "1.3.0";
	
	/**
	 * 新版本的设备支持运营组件，此组件支持新的增值指令：404 redirect
	 * @param deviceVersionBuild
	 * @return
	 */
	/*public static boolean isVapModuleSupported(String deviceVersionBuild){
		if(StringUtils.isEmpty(deviceVersionBuild)) return false;
		String[] orig_swver1_versions = DeviceHelper.parseDeviceSwverVersion(deviceVersionBuild);
		if(orig_swver1_versions == null) return false;
		try{
			int ret = VersionHelper.compareVersion(orig_swver1_versions[0], VapModuleVersion);
			return (ret >= 0);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return false;
		}
	}*/

	/*public static boolean isVapModuleSupported(WifiDevice wifiDevice){
		if(StringUtils.isEmpty(deviceVersionBuild)) return false;
		String[] orig_swver1_versions = DeviceHelper.parseDeviceSwverVersion(deviceVersionBuild);
		if(orig_swver1_versions == null) return false;
		try{
			int ret = VersionHelper.compareVersion(orig_swver1_versions[0], VapModuleVersion);
			return (ret >= 0);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return false;
		}
	}*/
	
	public static boolean isDeviceVapModuleSupported(String moduleBuild){
		return StringUtils.isNotEmpty(moduleBuild);
	}
	
	public static boolean isDeviceVapModuleOnline(boolean online,boolean vapmoduleonline){
		return (online && vapmoduleonline);
	}
	
	public static boolean isDeviceSpecialCmd(OperationCMD opt, OperationDS subopt){
		if(OperationCMD.ModifyDeviceSetting == opt){
			if(subopt == null) return false;
			return (/*OperationDS.DS_Http_404_Start == subopt || OperationDS.DS_Http_404_Stop == subopt
					|| 	OperationDS.DS_Http_Ad_Start == subopt || OperationDS.DS_Http_Ad_Stop == subopt
					|| OperationDS.DS_Http_Redirect_Start == subopt || OperationDS.DS_Http_Redirect_Stop == subopt*/
					//|| OperationDS.DS_Http_Portal_Start == subopt || OperationDS.DS_Http_Portal_Stop == subopt
					OperationDS.DS_Http_Ad_Start == subopt || OperationDS.DS_Http_Ad_Stop == subopt
					|| OperationDS.DS_SharedNetworkWifi_Start == subopt || OperationDS.DS_SharedNetworkWifi_Limit == subopt || OperationDS.DS_SharedNetworkWifi_Stop == subopt
					|| OperationDS.DS_Http_VapModuleCMD_Start == subopt || OperationDS.DS_Http_VapModuleCMD_Stop == subopt
					);
		}else{
			return false;
		}
	}
	
	//增值模块组件支持的增值指令
	public static boolean isVapCmdModuleSupported(OperationCMD opt, OperationDS subopt){
		if(OperationCMD.ModifyDeviceSetting == opt){
			if(subopt == null) return false;
			return (/*OperationDS.DS_Http_404_Start == subopt || OperationDS.DS_Http_404_Stop == subopt
					|| OperationDS.DS_Http_Redirect_Start == subopt || OperationDS.DS_Http_Redirect_Stop == subopt
					||*/ 
					OperationDS.DS_Http_VapModuleCMD_Start == subopt || OperationDS.DS_Http_VapModuleCMD_Stop == subopt
					);
		}else{
			return false;
		}
	}
	
	
	public static boolean isVapCmdExceptDevice(String mac){
		return vapExceptDevices.contains(mac);
	}
	
	/**
	 * 有些指令发送下去后设备端没有响应结果，此类指令下发后会自动进入完成任务表中
	 * @param opt
	 * @return
	 */
	/*public static boolean isAutoCompletedTask(OperationCMD opt){
		return OperationCMD.DeviceModuleUpgrade == opt;
	}*/
	public static boolean isAutoCompletedTask(String optno){
		return OperationCMD.DeviceModuleUpgrade.getNo().equals(optno) || OperationCMD.RemoteDeviceControlTransfer.getNo().equals(optno);
	}
	/**
	 * opt = OperationCMD.ModifyDeviceSetting
	 * 		subopt = OperationDS.
	 * 			DS_Http_Ad_Start("01","开启广告注入"),
				DS_Http_404_Start("15","开启404错误页面"),
				DS_Http_Redirect_Start("16","开启http redirect"),
				//DS_Http_Portal_Start("17","开启http portal"),
				//DS_Http_Portal_Stop("18","关闭http portal"),
				DS_Http_Ad_Stop("19","关闭广告注入"),
				DS_Http_404_Stop("20","关闭404错误页面"),
				DS_Http_Redirect_Stop("21","关闭http redirect"),
	 * opt = OperationCMD.TurnOnDeviceDPINotify
	 * opt = OperationCMD.TurnOffDeviceDPINotify
	 * @param opt
	 * @param subopt
	 * @return
	 */
	public static List<PersistenceAction> needPersistenceAction(OperationCMD opt,OperationDS subopt){
		List<PersistenceAction> result = null;
		if(opt == null) return result;
		result = new ArrayList<>();
		switch(opt){
			case ModifyDeviceSetting:
				if(subopt == null) return result;
				switch(subopt){
					case DS_Http_Ad_Start:
						//result = builderPersistenceAction(opt,subopt,PersistenceAction.Oper_Update);
						result.add(builderPersistenceAction(opt,subopt,PersistenceAction.Oper_Update));
						break;	
					/*case DS_Http_404_Start:
						result = builderPersistenceAction(opt,subopt,PersistenceAction.Oper_Update);
						break;	
					case DS_Http_Redirect_Start:
						result = builderPersistenceAction(opt,subopt,PersistenceAction.Oper_Update);
						break;	*/
					case DS_Http_Ad_Stop:
						//result = builderPersistenceAction(opt,OperationDS.DS_Http_Ad_Start,PersistenceAction.Oper_Remove);
						result.add(builderPersistenceAction(opt,OperationDS.DS_Http_Ad_Start,PersistenceAction.Oper_Remove));
						break;	
					/*case DS_Http_404_Stop:
						result = builderPersistenceAction(opt,OperationDS.DS_Http_404_Start,PersistenceAction.Oper_Remove);
						break;	
					case DS_Http_Redirect_Stop:
						result = builderPersistenceAction(opt,OperationDS.DS_Http_Redirect_Start,PersistenceAction.Oper_Remove);
						break;	*/
					case DS_Http_VapModuleCMD_Start:
						//result = builderPersistenceAction(opt,subopt,PersistenceAction.Oper_Update);
						//移除掉删除指令并增加开启指令
						result.add(builderPersistenceAction(opt,OperationDS.DS_Http_VapModuleCMD_Stop,PersistenceAction.Oper_Remove));
						result.add(builderPersistenceAction(opt,OperationDS.DS_Http_VapModuleCMD_Start,PersistenceAction.Oper_Update));
						break;	
					case DS_Http_VapModuleCMD_Stop:
						//result = builderPersistenceAction(opt,OperationDS.DS_Http_VapModuleCMD_Start,PersistenceAction.Oper_Remove);
						//移除掉开启指令并增加删除指令
						result.add(builderPersistenceAction(opt,OperationDS.DS_Http_VapModuleCMD_Start,PersistenceAction.Oper_Remove));
						result.add(builderPersistenceAction(opt,OperationDS.DS_Http_VapModuleCMD_Stop,PersistenceAction.Oper_Update));
						break;	
					default:
						break;	
				}
				break;
			case TurnOnDeviceDPINotify:
				//result = builderPersistenceAction(opt,subopt,PersistenceAction.Oper_Update);
				result.add(builderPersistenceAction(opt,subopt,PersistenceAction.Oper_Update));
				break;
			case TurnOffDeviceDPINotify:
				//result = builderPersistenceAction(OperationCMD.TurnOnDeviceDPINotify,subopt,PersistenceAction.Oper_Remove);
				result.add(builderPersistenceAction(OperationCMD.TurnOnDeviceDPINotify,subopt,PersistenceAction.Oper_Remove));
				break;
			default:
				break;	
		}
		return result;
	}
	
	public static PersistenceAction builderPersistenceAction(OperationCMD opt,OperationDS subopt,String operation){
		PersistenceAction action = new PersistenceAction();
		action.setOperation(operation);
		//action.setKey(opt.getNo().concat(StringHelper.MINUS_STRING_GAP).concat(subopt!=null?subopt.getNo():OperationDS.Empty_OperationDS));
		action.setKey(builderPersistenceKey(opt.getNo(),subopt!=null?subopt.getNo():null));
		return action;
	}
	
	public static String builderPersistenceKey(String opt,String subopt){
		return opt.concat(StringHelper.MINUS_STRING_GAP).concat(subopt!=null?subopt:OperationDS.Empty_OperationDS);
	}
	
	
	public static String[] parserPersistenceKey(String persistenceKey){
		return persistenceKey.split(StringHelper.MINUS_STRING_GAP);
	}
	
	
	public static String xmlContentEncoder(String content){
		if(StringUtils.isEmpty(content)) return content;
		return HtmlHelper.htmlEncode(content);
	}
	
	
	public static void main(String[] argv){
		System.out.println(WifiDeviceHelper.deviceSharedNetworkStrategy("AP106P07V1.5.7r1_TC_UGX"));
	}
}
