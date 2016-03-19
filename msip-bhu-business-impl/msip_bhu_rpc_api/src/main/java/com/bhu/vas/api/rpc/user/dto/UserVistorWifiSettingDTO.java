package com.bhu.vas.api.rpc.user.dto;

import com.bhu.vas.api.dto.ret.param.ParamVapVistorWifiDTO;




/**
 * Wifi 设置定时开关
 * 存储的数据
 * @author tangzichao
 *
 */
@Deprecated
@SuppressWarnings("serial")
public class UserVistorWifiSettingDTO extends UserSettingDTO{
	public static final String Setting_Key = "uvw";
	//访客网络Wifi开关
	private boolean on = false;
	//device response ok 时 为true
	private boolean ds = false;
	//此值作为返给客户端需要设置的 在线授权的访客网络终端的数量
	private long c = 0;
	private ParamVapVistorWifiDTO vw;
	//users_tx_rate users_rx_rate signal_limit(-30) redirect_url("www.bhuwifi.com") idle_timeout(1200) force_timeout(21600) open_resource("") ssid("BhuWIFI-访客")
	
	public boolean isOn() {
		return on;
	}
	public void setOn(boolean on) {
		this.on = on;
	}
	@Override
	public String getSettingKey() {
		return Setting_Key;
	}
	public ParamVapVistorWifiDTO getVw() {
		return vw;
	}
	public void setVw(ParamVapVistorWifiDTO vw) {
		this.vw = vw;
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
	
}
