package com.bhu.vas.api.dto.ret;

/**
 * 根据vap名称获取终端列表的返回头信息
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class QueryTerminalSerialReturnDTO extends QuerySerialReturnDTO{
	private String mode;//vap的模式
	//设备的ssid
	private String ssid;
	//设备的bssid
	private String ap;
	
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public String getAp() {
		return ap;
	}
	public void setAp(String ap) {
		this.ap = ap;
	}
	public String getSsid() {
		return ssid;
	}
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}
}
