package com.bhu.vas.api.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.bhu.vas.api.vto.device.DeviceUnitTypeVTO;



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
	
	
	/**
	 * 采用正式的设备的hdtype中的数字作为子类型的index
	 * @author Edmond
	 *
	 */
	public enum DeviceUnitType{
		uRouterRoot(1000, 0,"uRouter"),
		SOCRoot(2000, 0,"SOC"),
		
		uRouterTU_106(106,		"H106",1000,"uRouter","2.4GHz 家用AP","64M内存、TF卡版本、9341芯片"),
		
		MassAP_2_103(103,		"H103",2000,"MassAP 2 H103","MassAP 2","2.4GHz 室内单频AP","8M Flash、64M内存、9341芯片"),
		MassAP_2_110(110,		"H110",2000,"MassAP 2 H110","MassAP 2","2.4GHz 室内单频AP","16M Flash、64M内存、9341芯片"),
		MassAP_Pro_201(201,		"H201",2000,"MassAP Pro H201","MassAP Pro","双频室外AP","16M flash、128M 内存、9350+9592芯片"),
		MassAP_Pro_303(303,		"H303",2000,"MassAP Pro H303","MassAP Pro","双频室外APv2","16M Flash、128M内存、9344+9380芯片"),
		MassAP_AC_Pro_305(305,	"H305",2000,"MassAP AC Pro H305","MassAP AC Pro","双频室外11ac AP","16M Flash、128M内存、9344+9882芯片"),
		
		MicroStation_2_104(104,	"H104",2000,"MicroStation 2 H104","MicroStation 2","2.4GHz室外单频AP","8M Flash、64M内存、9341芯片"),
		MicroStation_2_109(109,	"H109",2000,"MicroStation 2 H109","MicroStation 2","2.4GHz室外单频AP","16M Flash、64M内存、9341芯片"),
		MicroStation_2U_108(108,"H108",2000,"MicroStation 2U H108","MicroStation 2U","2.4GHz 室外单频AP(带USB口）","8M Flash、64M内存、9344芯片"),
		
		MicroStation_5_304(304,	"H304",2000,"MicroStation 5 H304","MicroStation 5","5GHz室外单频AP","8M Flash、64M内存、9344芯片"),
		MicroStation_5_306(306,	"H306",2000,"MicroStation 5 H306","MicroStation 5","5GHz室外单频AP","16M Flash、64M内存、9344芯片"),
		uRouterTC_401(401,		"H401",2000,"uRouter","uRouter","---","---"),
		;
		static Map<Integer, DeviceUnitType> allDeviceUnitTypes;
		static Map<String, DeviceUnitType> allDeviceHdTypes;
		static Map<Integer, List<DeviceUnitType>> allRootDeviceUnitTypes;
		static List<DeviceUnitTypeVTO> allDeviceUnitTypeVTO;
		
		static List<String> allMassAPHdTypes;// = new ArrayList<String>();
		
		private int index;
		private String hdtype;
		private String name;
		private String sname;
		private int parent;
		private String fname;
		private String desc;
		private DeviceUnitType(int index, int parent,String name){
			this.index = index;
			this.name = name;
			this.parent = parent;
		}
		
		private DeviceUnitType(int index,String hdtype, int parent,String name,String sname,String fname,String desc){
			this.index = index;
			this.hdtype = hdtype;
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
		
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public int getParent() {
			return parent;
		}
		public void setParent(int parent) {
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

		public String getHdtype() {
			return hdtype;
		}

		public void setHdtype(String hdtype) {
			this.hdtype = hdtype;
		}

		public String getSname() {
			return sname;
		}

		public void setSname(String sname) {
			this.sname = sname;
		}

		public static DeviceUnitType fromIndex(int index){
			DeviceUnitType dType = allDeviceUnitTypes.get(index); 
			return dType;
		}
		public static DeviceUnitType fromHdType(String hdtype){
			return allDeviceHdTypes.get(hdtype);
		}
		
		public static boolean isURouterHdType(String hdtype) {
			DeviceUnitType fromHdType = fromHdType(hdtype);
			if(fromHdType != null){
				return fromHdType.getParent() == uRouterRoot.getParent();
			}
			return false;
		}
		
		public static boolean isSocHdType(String hdtype) {
			DeviceUnitType fromHdType = fromHdType(hdtype);
			if(fromHdType != null){
				return fromHdType.getParent() == SOCRoot.getParent();
			}
			return false;
		}
		static {
			allDeviceUnitTypes = new HashMap<Integer,DeviceUnitType>();
			allDeviceHdTypes = new HashMap<String,DeviceUnitType>();
			allRootDeviceUnitTypes = new HashMap<Integer,List<DeviceUnitType>>();
			allDeviceUnitTypeVTO = new ArrayList<>();
			allMassAPHdTypes = new ArrayList<String>();
			DeviceUnitType[] types = values();//new ImageType[] {JPG, BMP, GIF, PNG, TIFF};
			for (DeviceUnitType type : types){
				allDeviceUnitTypes.put(type.getIndex(), type);
				allDeviceHdTypes.put(type.getHdtype(), type);
				if(type.parent == 0){//root
					allRootDeviceUnitTypes.put(type.getIndex(), new ArrayList<DeviceUnitType>());
					//allDeviceUnitTypeVTO.add(new DeviceUnitTypeVTO(type.getIndex(),type.getName()));
				}else{
					List<DeviceUnitType> list = allRootDeviceUnitTypes.get(type.getParent());
					if(list != null)
						list.add(type);
				}
			}
			Iterator<Entry<Integer, List<DeviceUnitType>>> iter = allRootDeviceUnitTypes.entrySet().iterator();
			while(iter.hasNext()){
				Entry<Integer, List<DeviceUnitType>> next = iter.next();
				Integer index = next.getKey();
				List<DeviceUnitType> children = next.getValue();
				DeviceUnitType parent = DeviceUnitType.fromIndex(index);
				DeviceUnitTypeVTO parentVTO = new DeviceUnitTypeVTO(parent.getIndex(),parent.getName());
				for(DeviceUnitType child:children){
					if(parentVTO.getC() == null) parentVTO.setC(new ArrayList<DeviceUnitTypeVTO>());
					parentVTO.getC().add(new DeviceUnitTypeVTO(child.getIndex(),child.getName()));
				}
				allDeviceUnitTypeVTO.add(parentVTO);
			}
			/*MassAP_2_103(103,		"H103",2000,"MassAP 2 H103","2.4GHz 室内单频AP","8M Flash、64M内存、9341芯片"),
			MassAP_2_110(110,		"H110",2000,"MassAP 2 H110","2.4GHz 室内单频AP","16M Flash、64M内存、9341芯片"),
			MassAP_Pro_201(201,		"H201",2000,"MassAP Pro H201","双频室外AP","16M flash、128M 内存、9350+9592芯片"),
			MassAP_Pro_303(303,		"H303",2000,"MassAP Pro H303","双频室外APv2","16M Flash、128M内存、9344+9380芯片"),
			MassAP_AC_Pro_305(305,	"H305",2000,"MassAP AC Pro H305","双频室外11ac AP","16M Flash、128M内存、9344+9882芯片"),
*/
			allMassAPHdTypes.add(MassAP_2_103.getHdtype());
			allMassAPHdTypes.add(MassAP_2_110.getHdtype());
			allMassAPHdTypes.add(MassAP_Pro_201.getHdtype());
			allMassAPHdTypes.add(MassAP_Pro_303.getHdtype());
			allMassAPHdTypes.add(MassAP_AC_Pro_305.getHdtype());
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
}
