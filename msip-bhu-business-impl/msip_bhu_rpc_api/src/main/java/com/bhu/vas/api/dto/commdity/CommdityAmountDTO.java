package com.bhu.vas.api.dto.commdity;


/**
 * 针对应用请求生成商品的金额DTO
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class CommdityAmountDTO implements java.io.Serializable{
	//随机金额
	private String amount;
	//设备的上网时间 add by fengshibo
	private String forceTime;
	//本周用户数
	private String user7d;
	//ssid
	private String ssid;
	//用户限速
	private int users_tx_rate;
	private int users_rx_rate;
	public String getSsid() {
		return ssid;
	}
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}
	public int getUsers_tx_rate() {
		return users_tx_rate;
	}
	public void setUsers_tx_rate(int users_tx_rate) {
		this.users_tx_rate = users_tx_rate;
	}
	public int getUsers_rx_rate() {
		return users_rx_rate;
	}
	public void setUsers_rx_rate(int users_rx_rate) {
		this.users_rx_rate = users_rx_rate;
	}
	
	public String getUser7d() {
		return user7d;
	}
	public void setUser7d(String user7d) {
		this.user7d = user7d;
	}
	public String getForceTime() {
		return forceTime;
	}
	public void setForceTime(String forceTime) {
		this.forceTime = forceTime;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
}

