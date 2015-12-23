package com.bhu.vas.api.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.util.StringUtils;

import com.bhu.vas.api.vto.device.DeviceUnitTypeVTO;
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
		Other(	100,"其他","除去所有之外的设备属于Unknow",true,true),
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
	
	/**
	 * 采用正式的设备的hdtype中的数字作为子类型的index
	 * @author Edmond
	 *
	 */
	public enum DeviceUnitType{
		uRouterRoot(DUT_uRouter, StringHelper.MINUS_STRING_GAP,"uRouter"),
		SOCRoot(DUT_soc, StringHelper.MINUS_STRING_GAP,"SOC"),
		
		uRouterTU_106("TU_H106",	"AP106",DUT_uRouter,"uRouter","uRouter","2.4GHz 家用AP","64M内存、TF卡版本、9341芯片"),
		uRouterPlusTU_112("TU_H112","AP112",DUT_uRouter,"uRouter Plus","uRouter","2.4GHz 家用AP","64M内存、TF卡版本、9341芯片"),
		
		MassAP_2_103("TS_H103",		"AP103",DUT_soc,"MassAP 2 H103","MassAP 2","2.4GHz 室内单频AP","8M Flash、64M内存、9341芯片"),
		MassAP_2_110("TS_H110",		"AP110",DUT_soc,"MassAP 2 H110","MassAP 2","2.4GHz 室内单频AP","16M Flash、64M内存、9341芯片"),
		MassAP_Pro_201("TS_H201",	"AP201",DUT_soc,"MassAP Pro H201","MassAP Pro","双频室外AP","16M flash、128M 内存、9350+9592芯片"),
		MassAP_Pro_303("TS_H303",	"AP303",DUT_soc,"MassAP Pro H303","MassAP Pro","双频室外APv2","16M Flash、128M内存、9344+9380芯片"),
		MassAP_AC_Pro_305("TS_H305","AP305",DUT_soc,"MassAP AC Pro H305","MassAP AC Pro","双频室外11ac AP","16M Flash、128M内存、9344+9882芯片"),
		
		MicroStation_2_104("TS_H104","AP104",DUT_soc,"MicroStation 2 H104","MicroStation 2","2.4GHz室外单频AP","8M Flash、64M内存、9341芯片"),
		MicroStation_2_109("TS_H109","AP109",DUT_soc,"MicroStation 2 H109","MicroStation 2","2.4GHz室外单频AP","16M Flash、64M内存、9341芯片"),
		MicroStation_2U_108("TS_H108","AP108",DUT_soc,"MicroStation 2U H108","MicroStation 2U","2.4GHz 室外单频AP(带USB口）","8M Flash、64M内存、9344芯片"),
		
		MicroStation_5_304("TS_H304","AP304",DUT_soc,"MicroStation 5 H304","MicroStation 5","5GHz室外单频AP","8M Flash、64M内存、9344芯片"),
		MicroStation_5_306("TS_H306","AP306",DUT_soc,"MicroStation 5 H306","MicroStation 5","5GHz室外单频AP","16M Flash、64M内存、9344芯片"),
		uRouterTS_106("TS_H106",	 "AP106",DUT_soc,"uRouter","uRouter","商用","---"),
		uRouterPlusTS_112("TS_H112","AP112",DUT_soc,"uRouter Plus","uRouter","商用","64M内存、TF卡版本、9341芯片"),
		;
		//key index,value DeviceUnitType
		static Map<String, DeviceUnitType> allDeviceUnitHDTypes;
		//key parent_prefix ,value DeviceUnitType
		static Map<String, DeviceUnitType> allDeviceUnitPrefixTypes;
		static Map<String, List<DeviceUnitType>> allRootDeviceUnitTypes;
		static List<DeviceUnitTypeVTO> allDeviceUnitTypeVTO;
		
		static List<String> allMassAPHdTypes;// = new ArrayList<String>();
		//业务类型_HDTYPE
		private String index;
		//orig_swver 前缀
		private String prefix;
		private String name;
		private String sname;
		private String parent;
		private String fname;
		private String desc;
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

		public static DeviceUnitType fromIndex(String index){
			DeviceUnitType dType = allDeviceUnitHDTypes.get(index); 
			return dType;
		}
		
		public static DeviceUnitType fromHdType(String dut,String hdtype){
			StringBuilder sb_key = new StringBuilder();
			sb_key.append(dut).append(StringHelper.UNDERLINE_STRING_GAP).append(hdtype);
			DeviceUnitType dType = allDeviceUnitHDTypes.get(sb_key.toString()); 
			return dType;
		}
		
		public static DeviceUnitType fromVersionPrefix(String dut,String prefix){
			StringBuilder sb_key = new StringBuilder();
			sb_key.append(dut).append(StringHelper.UNDERLINE_STRING_GAP).append(prefix);
			DeviceUnitType dType = allDeviceUnitPrefixTypes.get(sb_key.toString()); 
			return dType;
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
		
		public static boolean isURouter(String prefix,String dut) {
			if(StringUtils.isEmpty(prefix) || StringUtils.isEmpty(dut)) return false;
			if((prefix.equals(uRouterTU_106.getPrefix()) || prefix.equals(uRouterPlusTU_112.getPrefix())) && dut.equals(uRouterRoot.getIndex())){
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
		
		static {
			allDeviceUnitHDTypes = new HashMap<String,DeviceUnitType>();
			allDeviceUnitPrefixTypes = new HashMap<String,DeviceUnitType>();
			allRootDeviceUnitTypes = new LinkedHashMap<String,List<DeviceUnitType>>();
			allDeviceUnitTypeVTO = new ArrayList<>();
			allMassAPHdTypes = new ArrayList<String>();
			DeviceUnitType[] types = values();//new ImageType[] {JPG, BMP, GIF, PNG, TIFF};
			for (DeviceUnitType type : types){
				allDeviceUnitHDTypes.put(type.getIndex(), type);
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
	
	
	public static void main(String[] argv){
		DeviceUnitType unitType = VapEnumType.DeviceUnitType.fromIndex("TU_H106");
		System.out.print(unitType.name);
	}
}
