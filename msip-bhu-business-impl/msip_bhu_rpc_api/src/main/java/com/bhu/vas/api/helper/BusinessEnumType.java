package com.bhu.vas.api.helper;

import java.util.HashMap;
import java.util.Map;

public class BusinessEnumType {
	/**
	 * sns 类别定义
	 * @author lawliet
	 *
	 */
	public enum OAuthType{
		Local("本地", "local"),
		Weichat("腾讯微信", "weichat"),
		QQ("腾讯QQ", "qq"),
		Weibo("新浪微博", "weibo"),
		;
		private String name;
		private String type;
		static Map<String, OAuthType> allOAuthTypeTypes;
		
		private OAuthType(String name, String type){
			this.name = name;
			this.type = type;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public static OAuthType fromType(String type){
			return allOAuthTypeTypes.get(type);
		}
		
		public static boolean supported(String type){
			return allOAuthTypeTypes.containsKey(type);
		}
		
		static {
			allOAuthTypeTypes = new HashMap<String, OAuthType>();
			OAuthType[] types = values();//new ImageType[] {JPG, BMP, GIF, PNG, TIFF};
			for (OAuthType type : types){
				allOAuthTypeTypes.put(type.getType(), type);
			}
		}
	}
	
	/*
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
	}*/
	
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
