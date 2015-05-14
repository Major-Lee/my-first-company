package com.bhu.vas.api.rpc.user.model;

import java.util.HashMap;
import java.util.Map;

public enum DeviceEnum {
	
	IPhone("IPhone","O",DeviceEnum.HandSet_IOS_Type,DeviceEnum.Display_IOS_Name,true,51,new String[]{"iphone_newly","iphone_callback"}),
/*	IPhone_4("IPhone4","O_4",DeviceEnum.HandSet_IOS_Type,DeviceEnum.Display_IOS_Name,true,51,new String[]{"iphone_newly","iphone_callback"}),
	IPhone_4s("IPhone4s","O_4s",DeviceEnum.HandSet_IOS_Type,DeviceEnum.Display_IOS_Name,true,51,new String[]{"iphone_newly","iphone_callback"}),
	IPhone_5("IPhone5","O_5",DeviceEnum.HandSet_IOS_Type,DeviceEnum.Display_IOS_Name,true,51,new String[]{"iphone_newly","iphone_callback"}),
	IPhone_5s("IPhone5s","O_5s",DeviceEnum.HandSet_IOS_Type,DeviceEnum.Display_IOS_Name,true,51,new String[]{"iphone_newly","iphone_callback"}),
*/	
	IPad("IPad","D",DeviceEnum.HandSet_IOS_Type,DeviceEnum.Display_IOS_Name,true,52,new String[]{"ipad_newly","ipad_callback"}),
//	IPad_Retina("IPad_retina","D_r",DeviceEnum.HandSet_IOS_Type,DeviceEnum.Display_IOS_Name,true,52,new String[]{"ipad_newly","ipad_callback"}),
	
	ITouch("ITouch","T",DeviceEnum.HandSet_IOS_Type,DeviceEnum.Display_IOS_Name,true,53,new String[]{"itouch_newly","itouch_callback"}),
	Android("Android","R",DeviceEnum.HandSet_ANDROID_Type,DeviceEnum.Display_ANDROID_Name,false,54,new String[]{"android_newly","android_callback"}),
	PC("PC","P",DeviceEnum.Desktop_WEB_Type,DeviceEnum.Display_WEB_Name,false,55,new String[]{"web_newly","web_callback"}),
	
	//APP360("APP360","A3",DeviceEnum.Desktop_360_WEB_Type,DeviceEnum.Display_360_WEB_Name,false,0,new String[]{"app360_newly","app360_callback"}),
	//APPAndroid("APPAndroid","AR",DeviceEnum.HandSet_ANDROID_Type,DeviceEnum.Display_ANDROID_Name,false,1,new String[]{"a_android_newly","a_android_callback"}),
	//APPIPhone("APPIPhone","AO",DeviceEnum.HandSet_IOS_Type,DeviceEnum.Display_IOS_Name,false,2,new String[]{"a_iphone_newly","a_iphone_callback"}),
	//APPIPad("APPIPad","AD",DeviceEnum.HandSet_IOS_Type,DeviceEnum.Display_IOS_Name,false,3,new String[]{"a_ipad_newly","a_ipad_callback"}),
	//APPITouch("APPITouch","AT",DeviceEnum.HandSet_IOS_Type,DeviceEnum.Display_IOS_Name,false,4,new String[]{"a_itouch_newly","a_itouch_callback"}),
	Unkown("Unkown","U","unkown","unkown",false,1000,null),
	;
	public static final String HandSet_IOS_Type = "ios";
	public static final String HandSet_ANDROID_Type = "adr";
	
	public static final String Desktop_WEB_Type = "web";
	public static final String Desktop_APP_Type = "app";
	public static final String Desktop_360_WEB_Type = "360app";
	
	public static final String Display_WEB_Name = "Web App";
	public static final String Display_360_WEB_Name = "360 Web App";
	public static final String Display_IOS_Name = "iPhone客户端";
	public static final String Display_ANDROID_Name = "android客户端";
	
	public static final String Prefix_Guest_App_Name = "APP";
	
	String name;
	String sname;
	String displayname;
	boolean pushsupport;
	String type;
	int index;//目前应用于 redis heartbeat dbindex 因为 redis 配置缺省支持16个db，所有 dbindex在 0-15，
	//用于统计Guest App用户在线状态，其中360用户由于没有长连接，不管事guest还是正式用户都用此种方式统计在线
	String[] fields;//0-newly 1-callback;
	DeviceEnum(String name,String sname,String type,String displayname,boolean pushsupport,int index,String[] fields){
		this.name = name;
		this.sname = sname;
		this.pushsupport = pushsupport;
		this.type = type;
		this.displayname = displayname;
		this.index = index;
		this.fields = fields;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSname() {
		return sname;
	}
	public void setSname(String sname) {
		this.sname = sname;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isPushsupport() {
		return pushsupport;
	}
	public void setPushsupport(boolean pushsupport) {
		this.pushsupport = pushsupport;
	}
	public String getDisplayname() {
		return displayname;
	}
	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}

	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}

	public String[] getFields() {
		return fields;
	}
	public void setFields(String[] fields) {
		this.fields = fields;
	}
	
	private static Map<String, DeviceEnum> mapSName;
	
	static {
		mapSName = new HashMap<String, DeviceEnum>();
		DeviceEnum[] types = values();//new ThumbType[] {SMALL, MIDDLE, LARGE, ORIGINAL};
		for (DeviceEnum type : types){
			mapSName.put(type.sname, type);
		}
	}
	
	public static DeviceEnum getBySName(String sname) {
		DeviceEnum ret = mapSName.get(sname);
		if(ret == null) return DeviceEnum.PC;
		return ret;
	}
	
	public static boolean isWeb(String sname) {
		DeviceEnum ret = mapSName.get(sname);
		if(ret == null) ret = DeviceEnum.PC;

		if(Desktop_WEB_Type.endsWith(ret.getType())) return true;
		return false;
	}
	
	public static boolean isIos(String sname) {
		DeviceEnum ret = mapSName.get(sname);
		if(ret == null) ret = DeviceEnum.PC;

		if(HandSet_IOS_Type.endsWith(ret.getType())) return true;
		return false;
	}
	
	public static boolean isAndroid(String sname) {
		DeviceEnum ret = mapSName.get(sname);
		if(ret == null) ret = DeviceEnum.PC;

		if(HandSet_ANDROID_Type.endsWith(ret.getType())) return true;
		return false;
	}
	
	public static boolean isHandsetDevice(DeviceEnum device){
		//DeviceEnum ret = mapSName.get(sname);
		//if(ret == null) return false;
		if(HandSet_IOS_Type.equals(device.getType()) || HandSet_ANDROID_Type.equals(device.getType())) return true;
		return false;
	}
	
	public static boolean isHandsetDevice(String sname){
		DeviceEnum device = mapSName.get(sname);
		if(device == null) return false;
		return isHandsetDevice(device);
		/*if(HandSet_IOS_Type.equals(ret.getType()) || HandSet_ANDROID_Type.equals(ret.getType())) return true;
		return false;*/
	}
	
	public static boolean isAppDevice(DeviceEnum device){
		if(device == null) return true;
		if(device.getName().startsWith(Prefix_Guest_App_Name)) return true;
		return false;
	}
	
	public static boolean supported(String sname){
		DeviceEnum device = mapSName.get(sname);
		if(device == null) return false;
		return true;
	}
	
	public static Map<String, DeviceEnum> getMapSName() {
		return mapSName;
	}
	public static void setMapSName(Map<String, DeviceEnum> mapSName) {
		DeviceEnum.mapSName = mapSName;
	}
	public static void main(String[] argv){
		DeviceEnum de = DeviceEnum.getBySName("O");
		System.out.println(DeviceEnum.isHandsetDevice(de));
		System.out.println(DeviceEnum.HandSet_IOS_Type);
		//System.out.println(DeviceEnum.isHandsetDevice("O"));
	}
}
