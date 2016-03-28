package com.bhu.vas.api.rpc.devices.dto.sharednetwork;






/**
 * Wifi 设置定时开关
 * 存储的数据
 * @author Edmond Lee
 *
 */
@SuppressWarnings("serial")
public class SharedNetworkSettingDTO implements java.io.Serializable{
	public static final String Setting_Key = "sdn";
	//访客网络Wifi开关
	private boolean on = false;
	//device response ok 时 为true
	private boolean ds = false;
	//此值作为返给客户端需要设置的 在线授权的访客网络终端的数量
	private long c = 0;
	private ParamSharedNetworkDTO psn;
	//users_tx_rate users_rx_rate signal_limit(-30) redirect_url("www.bhuwifi.com") idle_timeout(1200) force_timeout(21600) open_resource("") ssid("BhuWIFI-访客")
	
	public boolean isOn() {
		return on;
	}
	public void setOn(boolean on) {
		this.on = on;
	}
	
	public ParamSharedNetworkDTO getPsn() {
		return psn;
	}
	public void setPsn(ParamSharedNetworkDTO psn) {
		this.psn = psn;
	}
	public boolean isDs() {
		return ds;
	}
	public void setDs(boolean ds) {
		this.ds = ds;
	}
	public long getC() {
		return c;
	}
	public void setC(long c) {
		this.c = c;
	}
	
	public void turnOn(ParamSharedNetworkDTO psn){
		this.setOn(true);
		this.setDs(false);
		this.setPsn(psn);
		this.setC(System.currentTimeMillis());
	}
	/**
	 * 关闭的时候不置空psn
	 */
	public void turnOff(){
		this.setOn(false);
		this.setDs(false);
		//this.setPsn(null);
		this.setC(System.currentTimeMillis());
	}
	
	public void turnOff(ParamSharedNetworkDTO psn){
		this.setOn(false);
		this.setDs(false);
		this.setPsn(psn);
		this.setC(System.currentTimeMillis());
	}
	
	public void remoteNotify(){
		this.setDs(true);
		this.setC(System.currentTimeMillis());
	}
	
	public static SharedNetworkSettingDTO buildOffSetting(){
		SharedNetworkSettingDTO result = new SharedNetworkSettingDTO();
		result.setPsn(ParamSharedNetworkDTO.builderDefault());
		result.turnOff();
		return result;
	}
}
