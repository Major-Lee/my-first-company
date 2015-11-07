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
		static List<DeviceUnitTypeVTO> allDeviceUnitTypeVTO;
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
			allDeviceUnitTypeVTO = new ArrayList<>();
			DeviceUnitType[] types = values();//new ImageType[] {JPG, BMP, GIF, PNG, TIFF};
			for (DeviceUnitType type : types){
				allDeviceUnitTypes.put(type.getIndex(), type);
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
		}

		public static List<DeviceUnitTypeVTO> getAllDeviceUnitTypeVTO() {
			return allDeviceUnitTypeVTO;
		}
		public static void setAllDeviceUnitTypeVTO(
				List<DeviceUnitTypeVTO> allDeviceUnitTypeVTO) {
			DeviceUnitType.allDeviceUnitTypeVTO = allDeviceUnitTypeVTO;
		}
		
	}
}
