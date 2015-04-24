package com.bhu.vas.api.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.util.StringUtils;

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
	private static final String query_device_teminals_cmd_template = "00001001%s0000000000"+"000000000006"+"<param><ITEM wlan_user_notify=\"enable\" trap=\"disable\" wlan_user_sync=\"1\" /></param>";
	/*
	//1. 查询cpu,内存利用率
	private static final String query_device_status_cmd_template =   "00001001%s%s%s"+"000100000001"+"<cmd><ITEM index=\"1\" cmd=\"sysperf\"/></cmd>";
	//1. 查询设备流量
	private static final String query_device_flow_cmd_template 	 =   "00001001%s%s%s"+"000100000001"+"<cmd><ITEM index=\"1\" cmd=\"if_stat\"/></cmd>";
	//1. 查询wifi地理位置命令第一步
	private static final String query_device_location_step1_cmd_template = "00001001%s%s%s"+"000100000001"+"<cmd><ITEM cmd=\"sysdebug\" supercmd=\"wifiloc -a\" /></cmd>";
	//1. 查询wifi地理位置命令第二步
	private static final String query_device_location_step2_cmd_template = "00001001%s%s%s"+"000100000001"+"<report><ITEM cmd=\"sysdebug\" serial=\"%s\" op=\"get\"/></report>";
*/	
	
	//任务id format为七位，前面补零
	public static final String SuffixTemplete = "%07d";
	
	public static String builderDeviceOnlineTeminalQuery(String wifi_mac){
		return String.format(query_device_teminals_cmd_template, StringHelper.unformatMacAddress(wifi_mac));
	}
	
	public static String builderDeviceStatusQuery(String wifi_mac,int taskid){
		return String.format(OperationCMD.QueryDeviceStatus.getCmdtpl(), 
				StringHelper.unformatMacAddress(wifi_mac),OperationCMD.QueryDeviceStatus.getNo(),String.format(SuffixTemplete,taskid));
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
	
	public static String builderDeviceSpeedNotifyQuery(String wifi_mac,int taskid,int max_test_time){
		String opt = OperationCMD.QueryDeviceSpeedNotify.getNo();
		String taskid_format = String.format(SuffixTemplete,taskid);
		return String.format(OperationCMD.QueryDeviceSpeedNotify.getCmdtpl(),//query_device_flow_cmd_template, 
				StringHelper.unformatMacAddress(wifi_mac), opt, taskid_format, max_test_time, builderCMDSerial(opt, taskid_format));
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
	}
	
	public static String builderDeviceLocationStep2Query(String wifi_mac,int taskid,String serialno){
		return String.format(OperationCMD.QueryDeviceLocationS2.getCmdtpl(),//query_device_location_step2_cmd_template,
				StringHelper.unformatMacAddress(wifi_mac),OperationCMD.QueryDeviceLocationS2.getNo(),String.format(SuffixTemplete,taskid),serialno);
	}*/
	
	public static String builderDeviceLocationNotifyQuery(String wifi_mac,int taskid){
		String opt = OperationCMD.QueryDeviceLocationNotify.getNo();
		String taskid_format = String.format(SuffixTemplete,taskid);
		
		return String.format(OperationCMD.QueryDeviceLocationNotify.getCmdtpl(),//query_device_location_step2_cmd_template,
				StringHelper.unformatMacAddress(wifi_mac), opt, taskid_format, builderCMDSerial(opt, taskid_format));
	}
	
	public static List<String> builderDeviceTerminalsQueryWithAutoTaskid(String wifi_mac,List<String> interfaces){
		if(interfaces == null || interfaces.isEmpty()){
			return Collections.emptyList();
		}
		List<String> result = new ArrayList<String>();
		for(String inter_face:interfaces){
			result.add(String.format(OperationCMD.QueryDeviceTerminals.getCmdtpl(),
					StringHelper.unformatMacAddress(wifi_mac),OperationCMD.QueryDeviceTerminals.getNo(),String.format(SuffixTemplete,device_terminals_taskid_fragment.getNextSequence()),inter_face));
		}
		return result;
	}
	
	public static String builderDevHTMLInjectionNotify(String wifi_mac,int taskid,String enable,String adUrl,String adid){
		String opt = OperationCMD.DevHTMLInjectionNotify.getNo();
		String taskid_format = String.format(SuffixTemplete,taskid);
		
		return String.format(OperationCMD.DevHTMLInjectionNotify.getCmdtpl(),//query_device_location_step2_cmd_template,
				StringHelper.unformatMacAddress(wifi_mac), opt, taskid_format, enable,adUrl,adid);
	}
	
	
	public static String builderCMD4Opt(String opt,String wifi_mac,int taskid,String extparams){
		String resultCmd = null;
		OperationCMD operationCMDFromNo = OperationCMD.getOperationCMDFromNo(opt);
		if(operationCMDFromNo != null){
			switch(operationCMDFromNo){
				case DevHTMLInjectionNotify:
					//extparams 三个值 enable，adurl，adid
					String[] split = parserExtParams(extparams);
					if(split == null || split.length ==0)
						resultCmd = builderDevHTMLInjectionNotify(wifi_mac,taskid,"true","null","null");
					else
						resultCmd = builderDevHTMLInjectionNotify(wifi_mac,taskid,split[0],split[1],split[2]);
					break;
				default:
					resultCmd = String.format(operationCMDFromNo.getCmdtpl(), 
							StringHelper.unformatMacAddress(wifi_mac),opt,String.format(SuffixTemplete,taskid));
					break;
			}
		}
		return resultCmd;
	}
	
	public static String[] parserExtParams(String extparams){
		if(StringUtils.isEmpty(extparams)) return null;
		return extparams.split(StringHelper.OR_STRING_GAP_4SPLIT);
	}
	
	public static String builderCMDSerial(String opt, String taskid_format){
		StringBuffer serial = new StringBuffer();
		serial.append(opt).append(taskid_format);
		return serial.toString();
	}
	
	//任务号分段：
	//对于查询wifi地理位置任务 区间段未1~2000
	public static TaskSequenceFragment location_taskid_fragment = new TaskSequenceFragment(1,2000);
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
	
	//其他taskid区间，此部分区间数据是在数据库中有相应的taskid
	public static TaskSequenceFragment normal_taskid_fragment = new TaskSequenceFragment(100000,-1);
	public static boolean wasLocationQueryTaskid(int taskid){
		return location_taskid_fragment.wasInFragment(taskid);
	}
	
	public static boolean wasNormalTaskid(int taskid){
		return location_taskid_fragment.wasInFragment(taskid);
	}
}
