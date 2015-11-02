package com.bhu.vas.api.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class VapEnumType {
	/**
	 * sns 类别定义
	 * @author lawliet
	 *
	 */
	public enum GrayLevel{
		First(1,"一级灰度","一级灰度",true),
		Second(2,"二级灰度","二级灰度",true),
		Third(3,"三级灰度","三级灰度",true),
		Fourth(4,"四级灰度","四级灰度",false),
		Fifth(5,"五级灰度","五级灰度",false),
		None(90,"无灰度","指定的部分设备，无需进行增值运营操作，例如广告配置等",true),
		Unknow(100,"其他或未知","除去所有之外的设备属于Unknow",true),
		;
		private int index;//序号
		private String name;//灰度名称
		private String desc;//描述
		private boolean enable;//当前系统运行中是否有效
		
		static Map<Integer, GrayLevel> allGrayLevels;
		
		private GrayLevel(int index,String name, String desc,boolean enable){
			this.index = index;
			this.name = name;
			this.desc = desc;
			this.enable = enable;
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
	
	
	public enum DeviceUnitType{
		uRouterRoot(100,"uRouter", 0),
		SOCRoot(200,"SOC", 0),
		
		uRouterTU(101,"uRouter",100),
		
		MassAP(201,"MassAP",200),
		MassAP_Pro(202,"MassAP Pro",200),
		MicroStation_2_2U(203,"MicroStation 2\2U",200),
		MicorStation_5(204,"MicorStation 5",200),
		uRouterTC(205,"uRouter",200),
		;
		static Map<Integer, DeviceUnitType> allDeviceUnitTypes;
		static Map<Integer, List<DeviceUnitType>> allRootDeviceUnitTypes;
		private int index;
		private String name;
		private int parent;
		private DeviceUnitType(int index,String name, int parent){
			this.index = index;
			this.name = name;
			this.parent = parent;
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
		public static DeviceUnitType fromIndex(int index){
			DeviceUnitType dType = allDeviceUnitTypes.get(index); 
			return dType;
		}
		
		static {
			allDeviceUnitTypes = new HashMap<Integer,DeviceUnitType>();
			allRootDeviceUnitTypes = new HashMap<Integer,List<DeviceUnitType>>();
			DeviceUnitType[] types = values();//new ImageType[] {JPG, BMP, GIF, PNG, TIFF};
			for (DeviceUnitType type : types){
				allDeviceUnitTypes.put(type.getIndex(), type);
				if(type.parent == 0){//root
					allRootDeviceUnitTypes.put(type.getIndex(), new ArrayList<DeviceUnitType>());
				}else{
					List<DeviceUnitType> list = allRootDeviceUnitTypes.get(type.getParent());
					if(list != null)
						list.add(type);
				}
			}
		}
	}
}
