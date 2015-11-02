package com.bhu.vas.api.helper;

import java.util.HashMap;
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
	}
	
	
	public enum UserType{
		NormalUser("普通用户", 0),
		SystemRobotUser("系统机器人用户", 1),
		SystemNotifyUser("系统消息用户", 2),
		SystemArtificialUser("系统人工用户", 3),
		StarUser("明星用户", 8),
		;
		static Map<Integer, UserType> allUserTypes;
		private String name;
		private int type;
		private UserType(String name, int type){
			this.name = name;
			this.type = type;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}
		
		public static UserType fromType(int type){
			UserType userType = allUserTypes.get(type); 
			if(userType == null){
				userType = NormalUser;
			}
			return userType;
		}
		
		public static boolean isSystemArtificialUser(int type) {
			if(SystemArtificialUser.getType() == type) return true;
			return false;
		}
		
		static {
			allUserTypes = new HashMap<Integer,UserType>();
			UserType[] types = values();//new ImageType[] {JPG, BMP, GIF, PNG, TIFF};
			for (UserType type : types){
				allUserTypes.put(type.getType(), type);
			}
		}
	}
	
	public enum UserSex{
		Male("男"),
		Female("女"),
		Neutral("中性"),
		;
		static Map<String, UserSex> allUserSexs;
		private String name;
		private UserSex(String name){
			this.name = name;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public static UserSex fromType(String name){
			UserSex userSex = allUserSexs.get(name); 
			if(userSex == null){
				userSex = Female;
			}
			return userSex;
		}
		
		public static boolean isMale(String name){
			UserSex userSex = fromType(name);
			if(userSex == Male){
				return true;
			}
			return false;
		}
		
		static {
			allUserSexs = new HashMap<String, UserSex>();
			UserSex[] types = values();//new ImageType[] {JPG, BMP, GIF, PNG, TIFF};
			for (UserSex type : types){
				allUserSexs.put(type.getName(), type);
			}
		}
	}
}
