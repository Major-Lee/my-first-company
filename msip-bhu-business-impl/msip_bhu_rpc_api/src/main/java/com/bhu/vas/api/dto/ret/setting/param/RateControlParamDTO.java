package com.bhu.vas.api.dto.ret.setting.param;

/**
 * 配置设备的终端限速 参数dto
 * @author tangzichao
 *
 */
public class RateControlParamDTO {
	//终端mac
	private String mac;
	//终端上行速率 kbps
	private String tm_tx;
	//终端下行速率 kbps
	private String tm_rx;
	
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getTm_tx() {
		return tm_tx;
	}
	public void setTm_tx(String tm_tx) {
		this.tm_tx = tm_tx;
	}
	public String getTm_rx() {
		return tm_rx;
	}
	public void setTm_rx(String tm_rx) {
		this.tm_rx = tm_rx;
	}
}
