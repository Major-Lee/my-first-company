package com.bhu.vas.api.helper;

import org.springframework.util.StringUtils;

import com.bhu.vas.api.dto.VapModeDefined;
import com.bhu.vas.api.dto.VapModeDefined.HtmlPortal;
import com.bhu.vas.api.dto.ret.param.ParamCmdWifiTimerStartDTO;
import com.bhu.vas.api.dto.ret.param.ParamVapHttpPortalDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceUpgradeDTO;
import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.cores.helper.ArrayHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;

/**
 *  上端下发消息：
	1001. 任务下发：消息体格式12字节mac地址，后接10字节任务id。(10字节任务id 前两位为任务内部编号类型，后八位为taskid)
                    设备报文主类型(4字节)，子类型(8字节)
                    后接xml格式的任务字符串.
                    任务id为0，表明任务不需要回复。
 * <param>开头的那个报文，这两个数字是0000和00000006；// 但先改成0000和00000000，为了调试
 * @author Edmond
 *
 */
public class CMDBuilder {
	
	//1. 查询当前在线终端，下发此命令后触发设备主动上报一次,不走任务号机制  下发管理参数触发设备自动上报用户通知并同步终端
	//private static final String Device_Query_Teminals_Param_template = "00001001%s0000000000"+"000000000006"+"<param><ITEM wlan_user_notify=\"enable\" trap=\"disable\" wlan_user_sync=\"1\" /></param>";
	/*
	sta_sniffer	enable/disable	终端探测的开关
	sta_sniffer_url	STRING	终端探测信息上报的url
	sta_sniffer_batch_num 到达多少量上报
	sta_sniffer_delay 延迟多少秒*/
	//2. 设备终端探测开启关闭指令
	//private static final String Device_Wifi_Sniffer_Param_template = "00001001%s0000000000"+"000000000006"+"<param><ITEM sta_sniffer=\"%s\" sta_sniffer_batch_num=\"%s\" sta_sniffer_delay=\"%s\" sta_sniffer_url=\"%s\"/></param>";
	
	//任务id format为七位，前面补零
	public static final String SuffixTemplete = "%07d";
	
	//指令头长度
	public static final int Cmd_Header_Length = 42;
	
	public static String builderDeviceOnlineTeminalQuery(String wifi_mac){
		return String.format(OperationCMD.ParamQueryTeminals.getCmdtpl(), StringHelper.unformatMacAddress(wifi_mac));
	}
	
	public static String builderDeviceWifiSnifferSetting(String wifi_mac,String sta_sniffer){
		return String.format(OperationCMD.ParamWifiSinffer.getCmdtpl(), StringHelper.unformatMacAddress(wifi_mac),sta_sniffer,RuntimeConfiguration.Vap_Wifistasniffer_Batch_Num,RuntimeConfiguration.Vap_Wifistasniffer_Delay,RuntimeConfiguration.Vap_Wifistasniffer_Url);
	}
	
	public static String builderDeviceUsedStatusQuery(String wifi_mac){
		//return String.format(OperationCMD.QueryDeviceUsedStatus.getCmdtpl(), 
		//		StringHelper.unformatMacAddress(wifi_mac),OperationCMD.QueryDeviceUsedStatus.getNo(),String.format(SuffixTemplete, query_device_used_status.getNextSequence()));
		return builderDeviceUsedStatusQuery(wifi_mac,auto_taskid_fragment.getNextSequence());
	}
	public static String builderDeviceUsedStatusQuery(String wifi_mac,int taskid){
		return String.format(OperationCMD.QueryDeviceUsedStatus.getCmdtpl(), 
				StringHelper.unformatMacAddress(wifi_mac),OperationCMD.QueryDeviceUsedStatus.getNo(),String.format(SuffixTemplete, taskid));
	}
	public static String builderDeviceStatusQuery(String wifi_mac,int taskid){
		return String.format(OperationCMD.QueryDeviceStatus.getCmdtpl(), 
				StringHelper.unformatMacAddress(wifi_mac),OperationCMD.QueryDeviceStatus.getNo(),String.format(SuffixTemplete, taskid));
	}
	
	public static String builderDeviceFlowQuery(String wifi_mac,int taskid){
		return String.format(OperationCMD.QueryDeviceFlow.getCmdtpl(),//query_device_flow_cmd_template, 
				StringHelper.unformatMacAddress(wifi_mac),OperationCMD.QueryDeviceFlow.getNo(),String.format(SuffixTemplete,taskid));
	}
	
	public static String builderDeviceSettingQuery(String wifi_mac,int taskid){
		return String.format(OperationCMD.QueryDeviceSetting.getCmdtpl(),//query_device_flow_cmd_template, 
				StringHelper.unformatMacAddress(wifi_mac),OperationCMD.QueryDeviceSetting.getNo(),String.format(SuffixTemplete,taskid));
	}
	
	public static String builderDeviceSettingModify(String wifi_mac,int taskid, String payload){
		return String.format(OperationCMD.ModifyDeviceSetting.getCmdtpl(),//query_device_flow_cmd_template, 
				StringHelper.unformatMacAddress(wifi_mac),OperationCMD.ModifyDeviceSetting.getNo(),String.format(SuffixTemplete,taskid), payload);
	}
	
	public static String builderDeviceSpeedNotifyQuery(String wifi_mac,int taskid, String download_url, String upload_url){
		String opt = OperationCMD.QueryDeviceSpeedNotify.getNo();
		String taskid_format = String.format(SuffixTemplete,taskid);
		return String.format(OperationCMD.QueryDeviceSpeedNotify.getCmdtpl(),//query_device_flow_cmd_template, 
				StringHelper.unformatMacAddress(wifi_mac), opt, taskid_format, download_url, upload_url, builderCMDSerial(opt, taskid_format));
	}

	public static String builderDeviceUpgrade(String wifi_mac, int taskid, String upgrade_begin, String upgrade_end, String url) {
		String opt = OperationCMD.DeviceUpgrade.getNo();
		String taskid_format = String.format(SuffixTemplete, taskid);
//		return String.format(OperationCMD.DeviceUpgrade.getCmdtpl(),
//				StringHelper.unformatMacAddress(wifi_mac), opt, taskid_format, url, builderCMDSerial(opt, taskid_format));
		return String.format(OperationCMD.DeviceUpgrade.getCmdtpl(),
				StringHelper.unformatMacAddress(wifi_mac), opt, taskid_format, url,upgrade_begin, upgrade_end, builderCMDSerial(opt, taskid_format));
	}
	
	public static String builderDhcpcStatusQuery(String wifi_mac,int taskid,String interface_name){
		return String.format(OperationCMD.QueryDhcpcStatus.getCmdtpl(),//query_device_flow_cmd_template, 
				StringHelper.unformatMacAddress(wifi_mac),OperationCMD.QueryDhcpcStatus.getNo(),
				String.format(SuffixTemplete,taskid), interface_name);
	}
	
	/**
	 * 查询设备实时速率指令
	 * @param wifi_mac
	 * @param taskid
	 * @param interface_name 接口名称，不填表示查询所有接口，wan表示仅查询设备wan口，其他eth0，wlan0等表示查询指定接口；
	 * @param period 上报周期，不填或填0表示仅查询一次；秒
	 * @param duration 持续上报时间；秒
	 * @return
	 */
	public static String builderDeviceRateNotifyQuery(String wifi_mac,int taskid,String interface_name,
			int period,int duration){
		String opt = OperationCMD.QueryDeviceRateNotify.getNo();
		String taskid_format = String.format(SuffixTemplete,taskid);
		return String.format(OperationCMD.QueryDeviceRateNotify.getCmdtpl(),//query_device_flow_cmd_template, 
				StringHelper.unformatMacAddress(wifi_mac), opt, taskid_format, interface_name,
				period, duration, builderCMDSerial(opt, taskid_format));
	}
	
	/*public static String builderDeviceLocationStep1Query(String wifi_mac,int taskid){
		return String.format(OperationCMD.QueryDeviceLocationS1.getCmdtpl(),//query_device_location_step1_cmd_template, 
				StringHelper.unformatMacAddress(wifi_mac),OperationCMD.QueryDeviceLocationS1.getNo(),String.format(SuffixTemplete,taskid));//location_taskid_fragment.getNextSequence()));
	}*/
	
	public static String builderDeviceLocationStep2Query(String wifi_mac,int taskid,String serialno){
		return String.format(OperationCMD.QueryDeviceLocationS2.getCmdtpl(),//query_device_location_step2_cmd_template,
				StringHelper.unformatMacAddress(wifi_mac),OperationCMD.QueryDeviceLocationS2.getNo(),String.format(SuffixTemplete, taskid),serialno);
	}
	
	public static String builderDeviceLocationNotifyQuery(String wifi_mac,int taskid){
		String opt = OperationCMD.QueryDeviceLocationNotify.getNo();
		String taskid_format = String.format(SuffixTemplete,taskid);
		
		return String.format(OperationCMD.QueryDeviceLocationNotify.getCmdtpl(),//query_device_location_step2_cmd_template,
				StringHelper.unformatMacAddress(wifi_mac), opt, taskid_format, builderCMDSerial(opt, taskid_format));
	}
	
//	public static List<String> builderDeviceTerminalsQueryWithAutoTaskid(String wifi_mac,List<String> interfaces){
//		if(interfaces == null || interfaces.isEmpty()){
//			return Collections.emptyList();
//		}
//		List<String> result = new ArrayList<String>();
//		for(String inter_face:interfaces){
//			result.add(String.format(OperationCMD.QueryDeviceTerminals.getCmdtpl(),
//					StringHelper.unformatMacAddress(wifi_mac),OperationCMD.QueryDeviceTerminals.getNo(),
//					String.format(SuffixTemplete,device_terminals_taskid_fragment.getNextSequence()),inter_face));
//		}
//		return result;
//	}
	public static String builderDeviceTerminalsQuery(String wifi_mac, int taskid, 
			int period,int duration){
		String opt = OperationCMD.QueryDeviceTerminals.getNo();
		String taskid_format = String.format(SuffixTemplete,taskid);
		
		return String.format(OperationCMD.QueryDeviceTerminals.getCmdtpl(),
				StringHelper.unformatMacAddress(wifi_mac), opt, taskid_format, period, duration, 
				builderCMDSerial(opt, taskid_format));
	}
	
	/**
	 * 生成Http404ResourceUpdate 指令
	 * @param wifi_mac
	 * @param taskid
	 * @param extparams
	 * @return
	 *//*
	public static String builderCMD4Http404ResourceUpdate(String wifi_mac, int taskid,String extparams){
		String opt = OperationCMD.TriggerHttp404ResourceUpdate.getNo();
		String taskid_format = String.format(SuffixTemplete,taskid);
		//WifiDeviceSettingVapHttp404DTO http404_dto = JsonHelper.getDTO(extparams, WifiDeviceSettingVapHttp404DTO.class);
		ParamVapHttp404DTO http404_dto = JsonHelper.getDTO(extparams, ParamVapHttp404DTO.class);
		//Object[] array = http404_dto
		HtmlInject404 adv = VapModeDefined.HtmlInject404.getByStyle(http404_dto.getStyle());
		return String.format(OperationCMD.TriggerHttp404ResourceUpdate.getCmdtpl(),
				StringHelper.unformatMacAddress(wifi_mac), opt, taskid_format, adv.getPackurl(),adv.toIndentify(),builderCMDSerial(opt, taskid_format));
	}*/
	
	/**
	 * 生成HttpPortalResourceUpdate 指令
	 * @param wifi_mac
	 * @param taskid
	 * @param extparams
	 * @return
	 */
	public static String builderCMD4HttpPortalResourceUpdate(String wifi_mac, int taskid,String extparams){
		String opt = OperationCMD.TriggerHttpPortalResourceUpdate.getNo();
		String taskid_format = String.format(SuffixTemplete,taskid);
		ParamVapHttpPortalDTO httpportal_dto = JsonHelper.getDTO(extparams, ParamVapHttpPortalDTO.class);
		//Object[] array = http404_dto
		HtmlPortal adv = VapModeDefined.HtmlPortal.getByStyle(httpportal_dto.getStyle());
		return String.format(OperationCMD.TriggerHttpPortalResourceUpdate.getCmdtpl(),
				StringHelper.unformatMacAddress(wifi_mac), opt, taskid_format, adv.getPackurl(),adv.toIndentify(),builderCMDSerial(opt, taskid_format));
		/*String opt = OperationCMD.TriggerPortalResourceUpdate.getNo();
		String taskid_format = String.format(SuffixTemplete,taskid);
		WifiDeviceSettingVapHttpPortalDTO portal_dto = JsonHelper.getDTO(extparams, WifiDeviceSettingVapHttpPortalDTO.class);
		//Object[] array = http404_dto
		HtmlInject404 adv = VapModeDefined.Portal.getByStyle(portal_dto.getStyle());
		return String.format(OperationCMD.TriggerHttp404ResourceUpdate.getCmdtpl(),
				StringHelper.unformatMacAddress(wifi_mac), opt, taskid_format, adv.getUrl(),adv.getVer());*/
	}
	
//	public static String builderDevHTMLInjectionNotify(String wifi_mac,int taskid,String enable,String adUrl,String adid){
//		String opt = OperationCMD.DevHTMLInjectionNotify.getNo();
//		String taskid_format = String.format(SuffixTemplete,taskid);
//		
//		return String.format(OperationCMD.DevHTMLInjectionNotify.getCmdtpl(),//query_device_location_step2_cmd_template,
//				StringHelper.unformatMacAddress(wifi_mac), opt, taskid_format, enable,adUrl,adid);
//	}
	
	
	/*public static String builderCMD4Opt(String opt, String subopt,String wifi_mac,int taskid,String extparams){
		String resultCmd = null;
		OperationCMD operationCMDFromNo = OperationCMD.getOperationCMDFromNo(opt);
		if(operationCMDFromNo != null){
			switch(operationCMDFromNo){
				case ModifyDeviceSetting:
					if(extparams != null){
						resultCmd = builderDeviceSettingModify(wifi_mac, taskid, extparams);
					}
					break;
				case TurnOnDeviceDPINotify:
					String dpiServerIp = extparams;
					resultCmd = String.format(operationCMDFromNo.getCmdtpl(), 
							StringHelper.unformatMacAddress(wifi_mac),opt,String.format(SuffixTemplete,taskid),dpiServerIp);
					break;
				case DeviceUpgrade:
					WifiDeviceUpgradeDTO upgradeDto = JsonHelper.getDTO(extparams, WifiDeviceUpgradeDTO.class);
					resultCmd = builderDeviceUpgrade(wifi_mac, taskid, upgradeDto.getUpgrade_begin(),upgradeDto.getUpgrade_end(), upgradeDto.getUrl());
					break;
				case DeviceWifiTimerStart:
					ParamCmdWifiTimerStartDTO timerDto = JsonHelper.getDTO(extparams, ParamCmdWifiTimerStartDTO.class);
					String[] timeSlot = ParamCmdWifiTimerStartDTO.fetchSlot(timerDto.getTimeslot());
					resultCmd = String.format(operationCMDFromNo.getCmdtpl(), 
							StringHelper.unformatMacAddress(wifi_mac),opt,String.format(SuffixTemplete,taskid),timeSlot[0],timeSlot[1]);
					break;
				default://extparams = null 不需要参数构建的cmd
					//String[] params = genParserParams(wifi_mac,opt,taskid,extparams);
					//resultCmd = String.format(operationCMDFromNo.getCmdtpl(),params);
					resultCmd = String.format(operationCMDFromNo.getCmdtpl(), 
							StringHelper.unformatMacAddress(wifi_mac),opt,String.format(SuffixTemplete,taskid));
					break;
			}
		}
		return resultCmd;
	}*/
	
	
	public static String autoBuilderCMD4Opt(OperationCMD opt,String wifi_mac,int taskid,String extparams){
		return autoBuilderCMD4Opt(opt,null,wifi_mac,taskid,extparams,null);
	}
	
	/**
	 * 自动生成指令，用于设备上线后自动构造指令
	 * @param opt
	 * @param subopt
	 * @param wifi_mac
	 * @param taskid 如果taskid==0 则自动生成任务id
	 * @param extparams 构造指令的参数 可能是字符串 可能是jason参数
	 * 			目前由于DeviceSetting需要构造sequence serial ,所以需要把extparams值为已经构造成payload
	 * 			其余属性直接extparams为相关参数，可能是字符串 可能是jason参数
	 * @return
	 */
	public static String autoBuilderCMD4Opt(OperationCMD opt, OperationDS subopt,String wifi_mac,int taskid,String extparams,IGenerateDeviceSetting generateDeviceSetting){
		String resultCmd = null;
		if(opt != null){
			if(taskid == 0){
				//System.out.println(auto_taskid_fragment);
				taskid = auto_taskid_fragment.getNextSequence();
				//System.out.println("~~~~~~~~~~~~:"+taskid);
			}
			switch(opt){
				case ModifyDeviceSetting:
					try{
						String payload = generateDeviceSetting.generateDeviceSetting(wifi_mac, subopt, extparams);
						resultCmd = builderDeviceSettingModify(wifi_mac, taskid, payload);
					}catch(Exception ex){
						ex.printStackTrace(System.out);
					}
					break;
				case TurnOnDeviceDPINotify:
					String dpiServerIp = extparams;
					resultCmd = String.format(opt.getCmdtpl(), 
							StringHelper.unformatMacAddress(wifi_mac),opt.getNo(),String.format(SuffixTemplete,taskid),dpiServerIp);
					break;
				case DeviceUpgrade:
					WifiDeviceUpgradeDTO upgradeDto = JsonHelper.getDTO(extparams, WifiDeviceUpgradeDTO.class);
					resultCmd = builderDeviceUpgrade(wifi_mac, taskid, upgradeDto.getUpgrade_begin(),upgradeDto.getUpgrade_end(), upgradeDto.getUrl());
					break;
				case DeviceWifiTimerStart:
					ParamCmdWifiTimerStartDTO timerDto = JsonHelper.getDTO(extparams, ParamCmdWifiTimerStartDTO.class);
					String[] timeSlot = ParamCmdWifiTimerStartDTO.fetchSlot(timerDto.getTimeslot());
					resultCmd = String.format(opt.getCmdtpl(), 
							StringHelper.unformatMacAddress(wifi_mac),opt.getNo(),String.format(SuffixTemplete,taskid),timeSlot[0],timeSlot[1]);
					break;
				default://extparams = null 不需要参数构建的cmd
					//String[] params = genParserParams(wifi_mac,opt,taskid,extparams);
					//resultCmd = String.format(operationCMDFromNo.getCmdtpl(),params);
					resultCmd = String.format(opt.getCmdtpl(), 
							StringHelper.unformatMacAddress(wifi_mac),opt.getNo(),String.format(SuffixTemplete,taskid));
					break;
			}
		}
		return resultCmd;
	}
	
	private static String[] genParserParams(String wifi_mac,String opt,int taskid,String extparams){
		String[] params = new String[3];
		params[0] = StringHelper.unformatMacAddress(wifi_mac);
		params[1] = opt;
		params[2] = String.format(SuffixTemplete,taskid);
		String[] split = extparams.split(StringHelper.OR_STRING_GAP_4SPLIT);
		if(split != null && split.length>0)
			return ArrayHelper.join(params, split);
		else{
			return params;
		}
		/*for(int i=3;i<10;i++){
			
		}*/
		/*if(StringUtils.isEmpty(extparams)) {
			params = new String[3];
			params[0] = StringHelper.unformatMacAddress(wifi_mac);
			params[1] = opt;
			params[2] = String.format(SuffixTemplete,taskid);
			return params;
		}else{
			
			params = new String[3+split.length];
			params[0] = StringHelper.unformatMacAddress(wifi_mac);
			params[1] = opt;
			params[2] = String.format(SuffixTemplete,taskid);
			
		}
		return extparams.split(StringHelper.OR_STRING_GAP_4SPLIT);*/
	}
	
	public static String builderCMDSerial(String opt, String taskid_format){
		StringBuffer serial = new StringBuffer();
		serial.append(opt).append(taskid_format);
		return serial.toString();
	}

	
	public static String builderCMDWithoutHeader(String cmd){
		if(StringUtils.isEmpty(cmd) || cmd.length() < Cmd_Header_Length) return null;
		return cmd.substring(Cmd_Header_Length);
	}
	
	//任务号分段：
	//对于查询wifi地理位置任务 区间段未1~2000
	/*public static TaskSequenceFragment location_taskid_fragment = new TaskSequenceFragment(1,2000);
	//对于查询查询cpu,内存利用率 区间段未2001~5000
	public static TaskSequenceFragment timer_device_status_taskid_fragment = new TaskSequenceFragment(2001,5000);
	//对于查询设备流量 区间段未5001~8000
	public static TaskSequenceFragment timer_device_flow_taskid_fragment = new TaskSequenceFragment(5001,8000);
	
	//对于查询设备设置 区间段未8001~20000
	public static TaskSequenceFragment device_setting_query_taskid_fragment = new TaskSequenceFragment(8001,20000);
	//对于查询设备终端 区间段未20001~30000
	public static TaskSequenceFragment device_terminals_taskid_fragment = new TaskSequenceFragment(20001,30000);
	//对于查询设备终端 区间段未30001,35000
	public static TaskSequenceFragment device_speed_taskid_fragment = new TaskSequenceFragment(30001,35000);
	//对于查询设备终端 区间段未35001,40000
	public static TaskSequenceFragment device_rate_taskid_fragment = new TaskSequenceFragment(35001,40000);
	//对于修改设备配置 区间段未40001,45000
	public static TaskSequenceFragment device_setting_modify_taskid_fragment = new TaskSequenceFragment(40001,45000);
	//对于升级设备 区间段位45001,50000
	public static TaskSequenceFragment device_upgrade_fragment = new TaskSequenceFragment(45001,50000);
	public static TaskSequenceFragment device_wifitimer_fragment = new TaskSequenceFragment(50001,55000);
	public static TaskSequenceFragment query_device_used_status = new TaskSequenceFragment(55001,65000);

	public static TaskSequenceFragment device_deviceDPI_turnon = new TaskSequenceFragment(65001,65000);
	public static TaskSequenceFragment device_deviceDPI_turnoff = new TaskSequenceFragment(55001,65000);*/
	
	/*//对于设备 开启404
	public static TaskSequenceFragment device_http404_resourceupgrade_fragment = new TaskSequenceFragment(50001,52000);
	
	//对于设备 开启portal
	public static TaskSequenceFragment device_httpportal_resourceupgrade_fragment = new TaskSequenceFragment(52001,54000);*/
	//获取dhcp模式下的状态信息
	//public static TaskSequenceFragment device_dhcpc_status_fragment = new TaskSequenceFragment(54001,56000);
	
	public static TaskSequenceFragment auto_taskid_fragment = new TaskSequenceFragment(1,99999);
	//其他taskid区间，此部分区间数据是在数据库中有相应的taskid
	public static TaskSequenceFragment normal_taskid_fragment = new TaskSequenceFragment(100000,Integer.MAX_VALUE);
	public static boolean wasAutoTaskid(int taskid){
		return auto_taskid_fragment.wasInFragment(taskid);
	}
	
	public static boolean wasNormalTaskid(int taskid){
		return normal_taskid_fragment.wasInFragment(taskid);
	}
	
	/*public static void main(String[] argv){
		String[] params = new String[]{};
		String resultCmd = String.format("",params);
		System.out.println(new Date(1436407520276l));
	}*/
}
