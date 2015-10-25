package com.bhu.vas.api.helper;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.dto.DeviceVersion;
import com.smartwork.msip.cores.helper.StringHelper;


public class WifiDeviceHelper {
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
	
	
	public final static String WIFI_URouter_DEVICE_ORIGIN_MODEL = "uRouter";
	public static boolean isURouterDevice(String orig_model) {
		/*DeviceVersion ver = DeviceVersion.parser(orig_swver);
		if(ver == null) return false;
		return ver.wasDstURouter();*/
		return WIFI_URouter_DEVICE_ORIGIN_MODEL.equalsIgnoreCase(orig_model);
	}
	
	/**
	 * 比较两个设备的软件版本号
	 * 前置条件：版本号不包括
	 * 备注：只针对大版本号不一致或者 大版本一致情况下的小版本都存在Build情况
	 * 返回 1 表示 device_orig_swver 大于 gray_orig_swver
	 * 返回 0 表示 device_orig_swver 等于 gray_orig_swver
	 * 返回 -1 表示 device_orig_swver 小于 gray_orig_swver 可以升级
	 * 小版本号的前缀不一致的情况下都返回 0 eg：build或r
	 * @param orig_swver1
	 * @param orig_swver2
	 * @return
	 */
	public static int compareDeviceVersions(String device_orig_swver, String gray_defined_orig_swver){
		if(StringUtils.isEmpty(device_orig_swver) || StringUtils.isEmpty(gray_defined_orig_swver)) 
			throw new RuntimeException("param validate empty");
		DeviceVersion ver1 = DeviceVersion.parser(device_orig_swver);
		if(ver1 == null || !ver1.canExecuteUpgrade()) return 0;
		String[] orig_swver1_versions = ver1.parseDeviceSwverVersion();
		if(orig_swver1_versions == null) return -1;
		DeviceVersion ver2 = DeviceVersion.parser(gray_defined_orig_swver);
		String[] orig_swver2_versions = ver2.parseDeviceSwverVersion();
		if(orig_swver2_versions == null) return 1;
		//判断大版本号
		int top_ret = StringHelper.compareVersion(orig_swver1_versions[0], orig_swver2_versions[0]);
		//System.out.println("top ret " + top_ret);
		if(top_ret != 0) return top_ret;
		if(orig_swver1_versions[1] != null &&  orig_swver2_versions[1] != null){
			if(orig_swver1_versions[1].startsWith(DeviceVersion.Build_Normal_Prefix) && orig_swver2_versions[1].startsWith(DeviceVersion.Build_Normal_Prefix)){
				int bottom_ret = StringHelper.compareVersion(
						orig_swver1_versions[1].substring(DeviceVersion.Build_Normal_Prefix.length()), orig_swver2_versions[1].substring(DeviceVersion.Build_Normal_Prefix.length()));
				return bottom_ret;
			}
		}
		return 0;
		/*//判断小版本号
		int bottom_ret = StringHelper.compareVersion(orig_swver1_versions[1], orig_swver2_versions[1]);
		//System.out.println("bottom ret " + bottom_ret);
		return bottom_ret;*/
	}
	
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
					|| OperationDS.DS_VistorWifi_Start == subopt || OperationDS.DS_VistorWifi_Stop == subopt
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
	
	
	/**
	 * 有些指令发送下去后设备端没有响应结果，此类指令下发后会自动进入完成任务表中
	 * @param opt
	 * @return
	 */
	/*public static boolean isAutoCompletedTask(OperationCMD opt){
		return OperationCMD.DeviceModuleUpgrade == opt;
	}*/
	public static boolean isAutoCompletedTask(String optno){
		return OperationCMD.DeviceModuleUpgrade.getNo().equals(optno);
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
	public static PersistenceAction needPersistenceAction(OperationCMD opt,OperationDS subopt){
		PersistenceAction result = null;
		if(opt == null) return result;
		switch(opt){
			case ModifyDeviceSetting:
				if(subopt == null) return result;
				switch(subopt){
					case DS_Http_Ad_Start:
						result = builderPersistenceAction(opt,subopt,PersistenceAction.Oper_Update);
						break;	
					/*case DS_Http_404_Start:
						result = builderPersistenceAction(opt,subopt,PersistenceAction.Oper_Update);
						break;	
					case DS_Http_Redirect_Start:
						result = builderPersistenceAction(opt,subopt,PersistenceAction.Oper_Update);
						break;	*/
					case DS_Http_Ad_Stop:
						result = builderPersistenceAction(opt,OperationDS.DS_Http_Ad_Start,PersistenceAction.Oper_Remove);
						break;	
					/*case DS_Http_404_Stop:
						result = builderPersistenceAction(opt,OperationDS.DS_Http_404_Start,PersistenceAction.Oper_Remove);
						break;	
					case DS_Http_Redirect_Stop:
						result = builderPersistenceAction(opt,OperationDS.DS_Http_Redirect_Start,PersistenceAction.Oper_Remove);
						break;	*/
						
					case DS_Http_VapModuleCMD_Start:
						result = builderPersistenceAction(opt,subopt,PersistenceAction.Oper_Update);
						break;	
					case DS_Http_VapModuleCMD_Stop:
						result = builderPersistenceAction(opt,OperationDS.DS_Http_VapModuleCMD_Start,PersistenceAction.Oper_Remove);
						break;	
					default:
						break;	
				}
				break;
			case TurnOnDeviceDPINotify:
				result = builderPersistenceAction(opt,subopt,PersistenceAction.Oper_Update);
				break;
			case TurnOffDeviceDPINotify:
				result = builderPersistenceAction(OperationCMD.TurnOnDeviceDPINotify,subopt,PersistenceAction.Oper_Remove);
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
	
	public static void main(String[] argv){
		String current = "AP106P06V1.3.2Build8606_TU";
		String[] array = {"AP106P06V1.3.2Build8606","AP106P06V1.3.2Build8677","AP106P06V1.3.2Build8600","AP106P07V1.3.3r1_TU","AP106P07V1.3.1r1_TU","AP106P06V1.3.0Build8606_TU","AP109P06V1.3.0_TU_NGT","AP109P06V1.3.0_TC_NGT","CPE302P07V1.2.16r1","AP106P06V1.2.16Buildwaip_oldsytle"};
		for(String orig:array){
			int compareDeviceVersions = compareDeviceVersions(current, orig);
			System.out.println(compareDeviceVersions);
		}
		
	}
}
