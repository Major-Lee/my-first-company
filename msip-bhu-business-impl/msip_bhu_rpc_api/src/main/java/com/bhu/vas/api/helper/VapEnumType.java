package com.bhu.vas.api.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.helper.DeviceRadio;
import com.bhu.vas.api.rpc.devices.dto.DeviceVersion;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.SharedNetworkVTO;
import com.bhu.vas.api.vto.device.DeviceUnitTypeVTO;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;



public class VapEnumType {
	/**
	 * sns 类别定义
	 * @author lawliet
	 *
	 */
	public enum GrayLevel{
		First(	1,"一级灰度","一级灰度",true,true),
		Second(	2,"二级灰度","二级灰度",true,true),
		Third(	3,"三级灰度","三级灰度",true,true),
		Fourth(	4,"四级灰度","四级灰度",false,false),
		Fifth(	5,"五级灰度","五级灰度",false,false),
		Special(90,"特殊灰度","指定的部分设备，无需进行增值运营操作，例如广告配置等",true,true),
		Other(	100,"其它","除去所有之外的设备属于Unknow",true,true),
		;
		private int index;//序号
		private String name;//灰度名称
		private String desc;//描述
		private boolean enable;//当前系统运行中是否有效
		private boolean visible;
		static Map<Integer, GrayLevel> allGrayLevels;
		
		private GrayLevel(int index,String name, String desc,boolean enable,boolean visible){
			this.index = index;
			this.name = name;
			this.desc = desc;
			this.enable = enable;
			this.visible = visible;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public String getDesc() {
			return desc;
		}
		public void setDesc(String desc) {
			this.desc = desc;
		}
		public boolean isEnable() {
			return enable;
		}
		public void setEnable(boolean enable) {
			this.enable = enable;
		}
		
		public boolean isVisible() {
			return visible;
		}
		public void setVisible(boolean visible) {
			this.visible = visible;
		}

		static {
			allGrayLevels = new HashMap<Integer,GrayLevel>();
			GrayLevel[] levels = values();//new ImageType[] {JPG, BMP, GIF, PNG, TIFF};
			for (GrayLevel level : levels){
				allGrayLevels.put(level.getIndex(), level);
			}
		}
		public static GrayLevel fromIndex(int index){
			GrayLevel dType = allGrayLevels.get(index); 
			return dType;
		}
	}
	
	// U(家用版本，urouter), C(商业wifi版本), S(soc版本)
	public static final String DUT_uRouter = "TU";
	public static final String DUT_CWifi = "TC";
	public static final String DUT_soc = "TS";
	
	public static final String IMAGE_CLOUD = "http://devicecapability.bhuwifi.com/";

	/**
	 * 采用正式的设备的hdtype中的数字作为子类型的index
	 * @author Edmond
	 *  init sql
	 * 	insert into t_wifi_devices_grayversions values ('TC_H104',1,0,'-','-',now(),NOW());
		insert into t_wifi_devices_grayversions values ('TC_H104',2,0,'-','-',now(),NOW());
		insert into t_wifi_devices_grayversions values ('TC_H104',90,0,'-','-',now(),NOW());
		insert into t_wifi_devices_grayversions values ('TC_H104',100,0,'-','-',now(),NOW());
	 *
	 */
	public enum DeviceUnitType{
		uRouterRoot(DUT_uRouter, StringHelper.MINUS_STRING_GAP,"uRouter"),
		SOCRoot(DUT_soc, StringHelper.MINUS_STRING_GAP,"SOC"),
		CWifiRoot(DUT_CWifi, StringHelper.MINUS_STRING_GAP,"商业WiFi"),

		uRouterTU_106("TU_H106",	"AP106","BN207,BN027","Z01",DUT_uRouter,"uRouter","uRouter","2.4GHz 家用AP","64M内存、TF卡版本、9341芯片",
				"{\"icon\":\"" + IMAGE_CLOUD + "urouter\", \"radio\":[{\"name\":\"wifi0\",\"mode\":\"g\",\"power_range\":\"0-27\"}]}"),
		uRouterPlusTU_112("TU_H112","AP112","BN209","Z03",DUT_uRouter,"uRouter Plus","uRouter","2.4GHz 家用AP","64M内存、TF卡版本、9341芯片",
				"{\"icon\":\"" + IMAGE_CLOUD + "urouterplus\", \"radio\":[{\"name\":\"wifi0\",\"mode\":\"g\",\"power_range\":\"0-27\"}]}"),
		uRouterAcTU_401("TU_H401",	"AP401","BN210","Z02",DUT_uRouter,"uRouter AC","uRouter","2.4GHz 5GHz 家用AP","64M内存、TF卡版本、9531+9887芯片",
				"{\"icon\":\"" + IMAGE_CLOUD + "urouterac\", \"radio\":[{\"name\":\"wifi0\",\"mode\":\"g\",\"power_range\":\"0-27\"},{\"name\":\"wifi1\",\"mode\":\"a\",\"power_range\":\"0-27\"}]}"),
		uRouterAcPlusTU_403("TU_H403",	"AP403","BN403","Z05",DUT_uRouter,"uRouter AC Plus","uRouter","2.4GHz 5GHz 家用AP","128M内存、TF卡版本、9531+9887芯片",
				"{\"icon\":\"" + IMAGE_CLOUD + "urouteracplus\", \"radio\":[{\"name\":\"wifi0\",\"mode\":\"g\",\"power_range\":\"0-27\"},{\"name\":\"wifi1\",\"mode\":\"a\",\"power_range\":\"0-27\"}]}"),
		uRouterMiniTU_901("TU_H901","AP901","BN211","Z04",DUT_uRouter,"uRouter mini","uRouter","2.4GHz 家用AP","64M内存、TF卡版本、9341芯片",
				"{\"icon\":\"" + IMAGE_CLOUD + "uroutermini\", \"radio\":[{\"name\":\"wifi0\",\"mode\":\"g\",\"power_range\":\"0-20\"}]}"),
		uRouterMiniTU_NSL_901("TU_NSL_H901","AP901","BN211","Z04",DUT_uRouter,"uRouter mini(SL)","uRouter","2.4GHz 家用AP(赛龙)","64M内存、TF卡版本、9341芯片",
				"{\"icon\":\"" + IMAGE_CLOUD + "uroutermini\", \"radio\":[{\"name\":\"wifi0\",\"mode\":\"g\",\"power_range\":\"0-20\"}]}"),

		uRouterTU_801("TU_H801","AP801","*","M01",DUT_uRouter,"Juwan","uRouter 801","2.4GHz 家用AP","64M内存、TF卡版本、9341芯片",
				"{\"icon\":\"" + IMAGE_CLOUD + "undefined\", \"radio\":[{\"name\":\"wifi0\",\"mode\":\"g\",\"power_range\":\"0-27\"}]}"),
		uRouterTU_802("TU_H802","AP802","*","M02",DUT_uRouter,"Juwan pro","uRouter 802","聚玩9344双频网关","聚玩9344双频网关", //暂时改为单频
				"{\"icon\":\"" + IMAGE_CLOUD + "undefined\", \"radio\":[{\"name\":\"wifi0\",\"mode\":\"g\",\"power_range\":\"0-27\"}]}"),
		uRouterTU_810("TU_H810","AP810","*","M10",DUT_uRouter,"Maixu pro","uRouter 810","深圳迈旭9344双频AP（支持3g拨号）","深圳迈旭9344双频AP（支持3g拨号）",
				"{\"icon\":\"" + IMAGE_CLOUD + "undefined\", \"radio\":[{\"name\":\"wifi0\",\"mode\":\"g\",\"power_range\":\"0-27\"},{\"name\":\"wifi1\",\"mode\":\"a\",\"power_range\":\"0-27\"}]}"),
		uRouterTU_820("TU_H820","AP820","*","M20",DUT_uRouter,"Haier mini","uRouter 820","海尔MTK7628单频AP","海尔MTK7628单频AP",
				"{\"icon\":\"" + IMAGE_CLOUD + "undefined\", \"radio\":[{\"name\":\"wifi0\",\"mode\":\"g\",\"power_range\":\"0-18\"}]}"),
		uRouterTU_830("TU_H830","AP830","*","M30",DUT_uRouter,"Fuqiao mini","uRouter 830","MTK7620单频AP","MTK7620单频AP",
				"{\"icon\":\"" + IMAGE_CLOUD + "undefined\", \"radio\":[{\"name\":\"wifi0\",\"mode\":\"g\",\"power_range\":\"0-20\"}]}"),

		
		
		MassAP_2_103("TS_H103",		"AP103", null, null, DUT_soc,"MassAP 2 H103","MassAP 2","2.4GHz 室内单频AP","8M Flash、64M内存、9341芯片",
				"{\"icon\":\"" + IMAGE_CLOUD + "massap\", \"radio\":[{\"name\":\"wifi0\",\"mode\":\"g\",\"power_range\":\"0-27\"}]}"),
		MassAP_2_110("TS_H110",		"AP110", null, null, DUT_soc,"MassAP 2 H110","MassAP 2","2.4GHz 室内单频AP","16M Flash、64M内存、9341芯片",
				"{\"icon\":\"" + IMAGE_CLOUD + "massap\", \"radio\":[{\"name\":\"wifi0\",\"mode\":\"g\",\"power_range\":\"0-27\"}]}"),
		MassAP_Pro_201("TS_H201",	"AP201", null, null, DUT_soc,"MassAP Pro H201","MassAP Pro","双频室外AP","16M flash、128M 内存、9350+9592芯片",
				"{\"icon\":\"" + IMAGE_CLOUD + "massap\", \"radio\":[{\"name\":\"wifi0\",\"mode\":\"g\",\"power_range\":\"0-27\"},{\"name\":\"wifi1\",\"mode\":\"a\",\"power_range\":\"0-27\"}]}"),
		MassAP_Pro_303("TS_H303",	"AP303", null, null, DUT_soc,"MassAP Pro H303","MassAP Pro","双频室外APv2","16M Flash、128M内存、9344+9380芯片",
				"{\"icon\":\"" + IMAGE_CLOUD + "massap\", \"radio\":[{\"name\":\"wifi0\",\"mode\":\"g\",\"power_range\":\"0-27\"},{\"name\":\"wifi1\",\"mode\":\"a\",\"power_range\":\"0-27\"}]}"),
		MassAP_AC_Pro_305("TS_H305","AP305", null, null, DUT_soc,"MassAP AC Pro H305","MassAP AC Pro","双频室外11ac AP","16M Flash、128M内存、9344+9882芯片",
				"{\"icon\":\"" + IMAGE_CLOUD + "massap\", \"radio\":[{\"name\":\"wifi0\",\"mode\":\"g\",\"power_range\":\"0-27\"},{\"name\":\"wifi1\",\"mode\":\"a\",\"power_range\":\"0-27\"}]}"),
		
		MicroStation_2_104("TS_H104","AP104", null, null, DUT_soc,"MicroStation 2 H104","MicroStation 2","2.4GHz室外单频AP","8M Flash、64M内存、9341芯片",
				"{\"icon\":\"" + IMAGE_CLOUD + "micsta\", \"radio\":[{\"name\":\"wifi0\",\"mode\":\"g\",\"power_range\":\"0-27\"}]}"),
		MicroStation_2_109("TS_H109","AP109", null, null, DUT_soc,"MicroStation 2 H109","MicroStation 2","2.4GHz室外单频AP","16M Flash、64M内存、9341芯片",
				"{\"icon\":\"" + IMAGE_CLOUD + "micsta\", \"radio\":[{\"name\":\"wifi0\",\"mode\":\"g\",\"power_range\":\"0-27\"}]}"),
		MicroStation_2U_108("TS_H108","AP108", null, null, DUT_soc,"MicroStation 2U H108","MicroStation 2U","2.4GHz 室外单频AP(带USB口）","8M Flash、64M内存、9344芯片",
				"{\"icon\":\"" + IMAGE_CLOUD + "micsta\", \"radio\":[{\"name\":\"wifi0\",\"mode\":\"g\",\"power_range\":\"0-27\"}]}"),
		
		MicroStation_5_304("TS_H304","AP304", null, null, DUT_soc,"MicroStation 5 H304","MicroStation 5","5GHz室外单频AP","8M Flash、64M内存、9344芯片",
				"{\"icon\":\"" + IMAGE_CLOUD + "micsta\", \"radio\":[{\"name\":\"wifi0\",\"mode\":\"a\",\"power_range\":\"0-27\"}]}"),
		MicroStation_5_306("TS_H306","AP306", null, null, DUT_soc,"MicroStation 5 H306","MicroStation 5","5GHz室外单频AP","16M Flash、64M内存、9344芯片",
				"{\"icon\":\"" + IMAGE_CLOUD + "micsta\", \"radio\":[{\"name\":\"wifi0\",\"mode\":\"a\",\"power_range\":\"0-27\"}]}"),
		uRouterTS_106("TS_H106",	 "AP106", null, null, DUT_soc,"uRouter","uRouter","商用","---",
				"{\"icon\":\"" + IMAGE_CLOUD + "urouter\", \"radio\":[{\"name\":\"wifi0\",\"mode\":\"g\",\"power_range\":\"0-27\"}]}"),
		uRouterPlusTS_112("TS_H112","AP112", null, null, DUT_soc,"uRouter Plus","uRouter","商用","64M内存、TF卡版本、9341芯片",
				"{\"icon\":\"" + IMAGE_CLOUD + "\", \"radio\":[{\"name\":\"wifi0\",\"mode\":\"g\",\"power_range\":\"0-27\"}]}"),
		
		BXO2000n2SLite_104("TC_H104","AP104", null, null, DUT_CWifi,"BXO2000n(2S-Lite) H104","BXO2000n(2S-Lite)","","",
				"{\"icon\":\"" + IMAGE_CLOUD + "micsta\", \"radio\":[{\"name\":\"wifi0\",\"mode\":\"g\",\"power_range\":\"0-27\"}]}"),
		BXO2000n2SLiteU_108("TC_H108","AP108", null, null, DUT_CWifi,"BXO2000n(2S-Lite-U) H108","BXO2000n(2S-Lite-U)","","",
				"{\"icon\":\"" + IMAGE_CLOUD + "micsta\", \"radio\":[{\"name\":\"wifi0\",\"mode\":\"g\",\"power_range\":\"0-27\"}]}"),
		BXO2000n2SLite_109("TC_H109","AP109", null, null, DUT_CWifi,"BXO2000n(2S-Lite) H109","BXO2000n(2S-Lite)","","",
				"{\"icon\":\"" + IMAGE_CLOUD + "micsta\", \"radio\":[{\"name\":\"wifi0\",\"mode\":\"g\",\"power_range\":\"0-27\"}]}"),
		BXI2050n2_201("TC_H201","AP201", null, null, DUT_CWifi,"BXI2050n(2) H201","BXI2050n(2)","","",
				"{\"icon\":\"" + IMAGE_CLOUD + "massap\", \"radio\":[{\"name\":\"wifi0\",\"mode\":\"g\",\"power_range\":\"0-27\"},{\"name\":\"wifi1\",\"mode\":\"a\",\"power_range\":\"0-27\"}]}"),
		BXI2050n2v2_112("TC_H303","AP303", null, null, DUT_CWifi,"BXI2050n(2) v2 H303","BXI2050n(2) v2","","",
				"{\"icon\":\"" + IMAGE_CLOUD + "massap\", \"radio\":[{\"name\":\"wifi0\",\"mode\":\"g\",\"power_range\":\"0-27\"},{\"name\":\"wifi1\",\"mode\":\"a\",\"power_range\":\"0-27\"}]}"),
		
		//H104, H108, H109,H201, H110, H303
		;
		//key index,value DeviceUnitType
		static Map<String, DeviceUnitType> allDeviceUnitTypes;
		//key parent_prefix ,value DeviceUnitType
		static Map<String, DeviceUnitType> allDeviceUnitPrefixTypes;
		static Map<String, DeviceUnitType> allDeviceUnitSNPrefixTypes;
		static Map<String, List<DeviceUnitType>> allRootDeviceUnitTypes;
		static List<DeviceUnitTypeVTO> allDeviceUnitTypeVTO;
		
		static List<String> allMassAPHdTypes;// = new ArrayList<String>();
		//业务类型_HDTYPE
		private String index;
		//orig_swver 前缀
		private String prefix;
		//对应于SN号的前缀范围
		private String snprefix_range;
		private String hdver;//orig_swver;
		private String name;
		private String sname;
		private String parent;
		private String fname;
		private String desc;
		private String capability;
		private DeviceCapability deviceCapability;
		
		public static final DeviceCapability unknownDeviceCapability = JsonHelper.getDTO("{\"icon\":\"" + IMAGE_CLOUD + "undefined\"}", DeviceCapability.class);;
		
		public String getCapability() {
			return capability;
		}
		public void setCapability(String capability) {
			this.capability = capability;
		}
		private DeviceUnitType(String index, String parent,String name){
			this.index = index;
			this.name = name;
			this.parent = parent;
		}
		private DeviceUnitType(String index,String prefix, String parent,String name,String sname,String fname,String desc){
			this.index = index;
			this.prefix = prefix;
			this.name = name;
			this.sname = sname;
			this.parent = parent;
			this.fname = fname;
			this.desc = desc;
		}
		private DeviceUnitType(String index,String prefix,String snprefix_range,String hdver, String parent,String name,String sname,String fname,String desc){
			this.index = index;
			this.prefix = prefix;
			this.snprefix_range = snprefix_range;
			this.hdver = hdver;
			this.name = name;
			this.sname = sname;
			this.parent = parent;
			this.fname = fname;
			this.desc = desc;
		}
		
		private DeviceUnitType(String index,String prefix,String snprefix_range,String hdver, String parent,String name, 
				String sname,String fname,String desc, String capability){
			this.index = index;
			this.prefix = prefix;
			this.snprefix_range = snprefix_range;
			this.hdver = hdver;
			this.name = name;
			this.sname = sname;
			this.parent = parent;
			this.fname = fname;
			this.desc = desc;
			this.capability = capability;
			this.deviceCapability = JsonHelper.getDTO(capability, DeviceCapability.class);
		}

		public DeviceCapability getDeviceCapability() {
			return deviceCapability;
		}
		public void setDeviceCapability(DeviceCapability deviceCapability) {
			this.deviceCapability = deviceCapability;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		public String getIndex() {
			return index;
		}

		public void setIndex(String index) {
			this.index = index;
		}

		public String getParent() {
			return parent;
		}
		public void setParent(String parent) {
			this.parent = parent;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public String getFname() {
			return fname;
		}

		public void setFname(String fname) {
			this.fname = fname;
		}

		public String getPrefix() {
			return prefix;
		}

		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}

		public String getSname() {
			return sname;
		}

		public void setSname(String sname) {
			this.sname = sname;
		}

		public String getSnprefix_range() {
			return snprefix_range;
		}
		public void setSnprefix_range(String snprefix) {
			this.snprefix_range = snprefix;
		}
		
		public String getHdver() {
			return hdver;
		}
		public void setHdver(String hdver) {
			this.hdver = hdver;
		}
		public static DeviceUnitType fromIndex(String index){
			DeviceUnitType dType = allDeviceUnitTypes.get(index); 
			return dType;
		}
/*				
		public static DeviceUnitType fromHdType(String dut,String hdtype){
			StringBuilder sb_key = new StringBuilder();
			sb_key.append(dut).append(StringHelper.UNDERLINE_STRING_GAP).append(hdtype);
			DeviceUnitType dType = allDeviceUnitHDTypes.get(sb_key.toString()); 
			return dType;
		}
*/		
		public static DeviceUnitType fromVersionElements(String dut, String mn, String hdtype){
			StringBuilder sb_key = new StringBuilder();
			sb_key.append(dut).append(StringHelper.UNDERLINE_STRING_GAP);
			if(mn != null)
				sb_key.append(mn).append(StringHelper.UNDERLINE_STRING_GAP);
			sb_key.append(hdtype);
			return fromIndex(sb_key.toString());
		}

		/*
		 *	根据版本号得出DUT 
		 */
		public static DeviceUnitType fromVersion(String version){
			StringBuilder sb_key = new StringBuilder();
			try{
				DeviceVersion parser = DeviceVersion.parser(version);
				sb_key.append(parser.getSt()).append(StringHelper.UNDERLINE_STRING_GAP);
				if(parser.getMn() != null)
					sb_key.append(parser.getMn()).append(StringHelper.UNDERLINE_STRING_GAP);
				sb_key.append(parser.getHdtype());
			}catch(Exception e){
				e.printStackTrace(System.out);
				return null;
			}
			return fromIndex(sb_key.toString());
		}

		public static DeviceCapability getDeviceCapabilityFromVersion(String version){
			DeviceUnitType dut = fromVersion(version);
			if(dut == null)
				return unknownDeviceCapability;
			return dut.getDeviceCapability();
		}
		/*
		 * 目前根据SN并不能准确识别出真正的DUT，（UrouterMini TU版本中，还会有赛龙的版本），
		 * 目前只有再sn导入的时候调用此函数， 先匹配一个。需要等设备上线的时候，上报软件版本后再做修正
		 */
		//BN205CD100121AA 15位
		public static DeviceUnitType fromDeviceSN(String sn){
			if(StringUtils.isNotEmpty(sn) && sn.length() == 15){
				return allDeviceUnitSNPrefixTypes.get(sn.substring(0, 5));
			}
			return null;
		}
		
		
		public static String buildDutIndex4HdType(String st, String mn, String hdtype){
			StringBuilder sb_key = new StringBuilder();
			sb_key.append(st).append(StringHelper.UNDERLINE_STRING_GAP);
			if(mn != null)
				sb_key.append(mn).append(StringHelper.UNDERLINE_STRING_GAP);
			sb_key.append(hdtype);
			return sb_key.toString();
		}
		
		/**
		 * 拆分index数据为数组 TU NSL H106
		 * @param index TU_H106
		 * @return
		 */
		public static String[] parserIndex(String index){
			if(StringUtils.isEmpty(index)) return null;
			String[] spt = index.split(StringHelper.UNDERLINE_STRING_GAP);
			if(spt.length == 3)
				return spt;
			String[] ret = new String[3];
			ret[0] = spt[0];
			ret[1] = null;
			ret[2] = spt[1];
			spt = null;
			return ret;
		}
		
		/*public static DeviceUnitType fromHdType(String hdtype){
			return allDeviceHdTypes.get(hdtype);
		}*/
		
		/**
		 * 以TU结尾 并且包含以AP106开头
		 * @param orig_swver
		 * @return
		 */
		/*public static boolean isURouter(String orig_swver) {
			if(StringUtils.isEmpty(orig_swver)) return false;
			if(orig_swver.endsWith(uRouterTU_106.getPrefix()) && orig_swver.endsWith(uRouterTU_106.getParent())){
				return true;
			}
			return false;
		}*/
		
		public static boolean isURouter(String prefix,String type) {
			if(StringUtils.isEmpty(prefix) || StringUtils.isEmpty(type)) return false;
			if((prefix.equals(uRouterTU_106.getPrefix()) || prefix.equals(uRouterPlusTU_112.getPrefix()) 
					|| prefix.equals(uRouterAcTU_401.getPrefix()) || prefix.equals(uRouterAcPlusTU_403.getPrefix()) 
					|| prefix.equals(uRouterMiniTU_901.getPrefix())
					|| prefix.equals(uRouterTU_801.getPrefix()) 
					|| prefix.equals(uRouterTU_802.getPrefix()) 
					|| prefix.equals(uRouterTU_810.getPrefix()) 
					|| prefix.equals(uRouterTU_820.getPrefix()) 
					|| prefix.equals(uRouterTU_830.getPrefix())
					|| prefix.equals(uRouterMiniTU_NSL_901.getPrefix())) && type.equals(uRouterRoot.getIndex())){
				return true;
			}
			return false;
		}
		
		
		//MassAP_Pro_201("TS_H201",	"AP201",DUT_soc,"MassAP Pro H201","MassAP Pro","双频室外AP","16M flash、128M 内存、9350+9592芯片"),
		//MassAP_Pro_303("TS_H303",	"AP303",DUT_soc,"MassAP Pro H303","MassAP Pro","双频室外APv2","16M Flash、128M内存、9344+9380芯片"),
		//MassAP_AC_Pro_305("TS_H305","AP305",DUT_soc,"MassAP AC Pro H305","MassAP AC Pro","双频室外11ac AP","16M Flash、128M内存、9344+9882芯片"),

		//是否双频设备
		public static boolean isDualBandByVersionPrefix(String prefix){
			if(StringUtils.isEmpty(prefix)) return false;
			if(uRouterAcTU_401.getPrefix().equals(prefix)
					|| uRouterAcPlusTU_403.getPrefix().equals(prefix)
					|| MassAP_Pro_201.getPrefix().equals(prefix)
					|| MassAP_Pro_303.getPrefix().equals(prefix)
					|| MassAP_AC_Pro_305.getPrefix().equals(prefix)
//					|| uRouterTU_802.getPrefix().equals(prefix)
					|| uRouterTU_810.getPrefix().equals(prefix)
					){
				return true;
			}
			return false;
		}
		public static boolean isDualBandByOrigSwver(String orig_swver){
			if(StringUtils.isEmpty(orig_swver)) return false;
			if(orig_swver.startsWith(uRouterAcTU_401.getPrefix())
					|| orig_swver.startsWith(uRouterAcPlusTU_403.getPrefix())
					|| orig_swver.startsWith(MassAP_Pro_201.getPrefix())
					|| orig_swver.startsWith(MassAP_Pro_303.getPrefix())
					|| orig_swver.startsWith(MassAP_AC_Pro_305.getPrefix())
//					|| orig_swver.startsWith(uRouterTU_802.getPrefix())
					|| orig_swver.startsWith(uRouterTU_810.getPrefix())
					){
				return true;
			}
			return false;
		}
		
		
		/**
		 * 以TS结尾的
		 * @param orig_swver
		 * @return
		 */
		public static boolean isSoc(String orig_swver) {
			if(StringUtils.isEmpty(orig_swver)) return false;
			if(orig_swver.endsWith(SOCRoot.getIndex())){
				return true;
			}
			return false;
		}
		
		/**
		 * 以TC结尾的
		 * @param orig_swver
		 * @return
		 */
		public static boolean isCWifi(String orig_swver) {
			if(StringUtils.isEmpty(orig_swver)) return false;
			if(orig_swver.endsWith(CWifiRoot.getIndex())){
				return true;
			}
			return false;
		}
		
		static {
			allDeviceUnitTypes = new HashMap<String,DeviceUnitType>();
			allDeviceUnitPrefixTypes = new HashMap<String,DeviceUnitType>();
			allDeviceUnitSNPrefixTypes = new HashMap<String,DeviceUnitType>();
			allRootDeviceUnitTypes = new LinkedHashMap<String,List<DeviceUnitType>>();
			allDeviceUnitTypeVTO = new ArrayList<>();
			allMassAPHdTypes = new ArrayList<String>();
			DeviceUnitType[] types = values();//new ImageType[] {JPG, BMP, GIF, PNG, TIFF};
			for (DeviceUnitType type : types){
				allDeviceUnitTypes.put(type.getIndex(), type);
				if(StringHelper.MINUS_STRING_GAP.equals(type.parent)){//root
					allRootDeviceUnitTypes.put(type.getIndex(), new ArrayList<DeviceUnitType>());
					//allDeviceUnitTypeVTO.add(new DeviceUnitTypeVTO(type.getIndex(),type.getName()));
				}else{
					String prefixKey = type.getParent().concat(StringHelper.UNDERLINE_STRING_GAP).concat(type.getPrefix());
					allDeviceUnitPrefixTypes.put(prefixKey, type);
					List<DeviceUnitType> list = allRootDeviceUnitTypes.get(type.getParent());
					if(list != null)
						list.add(type);
				}
				if(StringUtils.isNotEmpty(type.getSnprefix_range())){
					String[] ranges = type.getSnprefix_range().split(",");
					for(String range:ranges)
						allDeviceUnitSNPrefixTypes.put(range, type);
				}
			}
			
			Iterator<Entry<String, List<DeviceUnitType>>> iter = allRootDeviceUnitTypes.entrySet().iterator();
			while(iter.hasNext()){
				Entry<String, List<DeviceUnitType>> next = iter.next();
				String index = next.getKey();
				List<DeviceUnitType> children = next.getValue();
				DeviceUnitType parent = DeviceUnitType.fromIndex(index);
				DeviceUnitTypeVTO parentVTO = new DeviceUnitTypeVTO(parent.getIndex(),parent.getName());
				for(DeviceUnitType child:children){
					if(parentVTO.getC() == null) parentVTO.setC(new ArrayList<DeviceUnitTypeVTO>());
					parentVTO.getC().add(new DeviceUnitTypeVTO(child.getIndex(),child.getName()));
				}
				allDeviceUnitTypeVTO.add(parentVTO);
			}
			
			//截取 index中_后面的字符串为hdtype
			allMassAPHdTypes.add(MassAP_2_103.getIndex().substring(3));
			allMassAPHdTypes.add(MassAP_2_110.getIndex().substring(3));
			allMassAPHdTypes.add(MassAP_Pro_201.getIndex().substring(3));
			allMassAPHdTypes.add(MassAP_Pro_303.getIndex().substring(3));
			allMassAPHdTypes.add(MassAP_AC_Pro_305.getIndex().substring(3));
		}

		public static List<DeviceUnitTypeVTO> getAllDeviceUnitTypeVTO() {
			return allDeviceUnitTypeVTO;
		}
		public static void setAllDeviceUnitTypeVTO(
				List<DeviceUnitTypeVTO> allDeviceUnitTypeVTO) {
			DeviceUnitType.allDeviceUnitTypeVTO = allDeviceUnitTypeVTO;
		}

		public static List<String> getAllMassAPHdTypes() {
			return allMassAPHdTypes;
		}
	}
	
	
	public enum SharedNetworkType{
		SafeSecure("SafeSecure","必虎安全共享网络"," 必虎安全共享WiFi"),
		SmsSecure("SmsSecure","必虎短信共享网络"," 必虎短信共享WiFi"),
		Uplink("Uplink","必虎Uplink共享网络"," 必虎Uplink共享WiFi"),
		;
		
		private String key;
		private String name;
		private String defaultSsid;
		static Map<String, SharedNetworkType> allSharedNetworkTypes;
		static List<SharedNetworkVTO> sharedNetworkVtos;
		private SharedNetworkType(String key,String name,String defaultSsid){
			this.key = key;
			this.name = name;
			this.defaultSsid = defaultSsid;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		
		public String getDefaultSsid() {
			return defaultSsid;
		}
		public void setDefaultSsid(String defaultSsid) {
			this.defaultSsid = defaultSsid;
		}
		public static SharedNetworkType fromKey(String key){
			return allSharedNetworkTypes.get(key);
		}
		
		public static boolean supported(String key){
			return allSharedNetworkTypes.containsKey(key);
		}
		static {
			allSharedNetworkTypes = new HashMap<String, SharedNetworkType>();
			sharedNetworkVtos = new ArrayList<>();
			SharedNetworkType[] types = values();
			for (SharedNetworkType type : types){
				allSharedNetworkTypes.put(type.getKey(), type);
			}
			//支持的共享网络类型目前只保留打赏网络
			sharedNetworkVtos.add(new SharedNetworkVTO(SafeSecure.getKey(), SafeSecure.getName(), SafeSecure.getDefaultSsid()));
		}
		public static List<SharedNetworkVTO> getSharedNetworkVtos() {
			return sharedNetworkVtos;
		}
	}
	
	
	public static void main(String[] argv){
//		uRouterMiniTU_NSL_901("TU_NSL_H901","AP901","BN211","Z04",DUT_uRouter,"uRouter mini(SL)","uRouter","2.4GHz 家用AP(赛龙)","64M内存、TF卡版本、9341芯片"),
		DeviceUnitType unitType = VapEnumType.DeviceUnitType.fromIndex("TU_H106");
		System.out.println("index:" + unitType.index);
		System.out.println("name:" + unitType.name);

//		DeviceCapability cb = VapEnumType.DeviceUnitType.getDeviceCapabilityFromVersion("AP106P06V1.3.2_TU");
		String content = "{\"icon\":\"" + IMAGE_CLOUD + "urouter\", \"radio\":[{\"name\":\"wifi0\",\"mode\":\"g\",\"power_range\":\"0-27\"}]}";
		System.out.println("content:" + content);
		DeviceCapability exp = new DeviceCapability();
		exp.setIcon("urouter");
		List<DeviceRadio> radio = new ArrayList<DeviceRadio>();
		DeviceRadio ra = new DeviceRadio();
		ra.setMode("g");
		radio.add(ra);
		exp.setRadio(radio);
		
		DeviceCapability cb = JsonHelper.getDTO(content, DeviceCapability.class);
		System.out.println("icons:" + cb.getIcon());
//		String[] parserIndex = VapEnumType.DeviceUnitType.parserIndex("TU_H106");
//		for(String p:parserIndex){
//			System.out.println(p);
//		}
//		System.out.println("BN205CD100121AA".substring(0, 5));
	}
}
