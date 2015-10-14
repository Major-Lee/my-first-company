package com.bhu.vas.api.vto;




/**
 * 设备周边探测的设置数据
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class WifiSinfferSettingVTO implements java.io.Serializable{
	//Wifi Timer开关
	private boolean on = false;
	//最近12小时出现的终端数量
	private long recent_c;
	
	public boolean isOn() {
		return on;
	}
	public void setOn(boolean on) {
		this.on = on;
	}
	public long getRecent_c() {
		return recent_c;
	}
	public void setRecent_c(long recent_c) {
		this.recent_c = recent_c;
	}
}
