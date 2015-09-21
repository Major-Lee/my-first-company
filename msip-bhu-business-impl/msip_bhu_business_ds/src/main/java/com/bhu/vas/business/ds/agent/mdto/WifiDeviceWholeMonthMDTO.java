package com.bhu.vas.business.ds.agent.mdto;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.smartwork.msip.cores.helper.StringHelper;

/**
 * wifi设备每月统计数据
 * @author Edmond
 *
 */
@Document(collection = "t_wifi_device_wholemonth")
public class WifiDeviceWholeMonthMDTO {
	/**
	 * 规则为 yyyy-MM_mac
	 */
	@Id
	private String id;
	//yyyyMMdd
	private String date;
	private String mac;
	//当月在线时长
	private long onlinetime;
	//当月连接次数
	private int connecttimes;
	//当月在线时长
	private long onlineduration;
	//当月连接终端数
	private int handsets;
	private long tx_bytes;
	private long rx_bytes;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public long getOnlinetime() {
		return onlinetime;
	}
	public void setOnlinetime(long onlinetime) {
		this.onlinetime = onlinetime;
	}
	public int getConnecttimes() {
		return connecttimes;
	}
	public void setConnecttimes(int connecttimes) {
		this.connecttimes = connecttimes;
	}
	
	public long getOnlineduration() {
		return onlineduration;
	}
	public void setOnlineduration(long onlineduration) {
		this.onlineduration = onlineduration;
	}
	public int getHandsets() {
		return handsets;
	}
	public void setHandsets(int handsets) {
		this.handsets = handsets;
	}
	public long getTx_bytes() {
		return tx_bytes;
	}
	public void setTx_bytes(long tx_bytes) {
		this.tx_bytes = tx_bytes;
	}
	public long getRx_bytes() {
		return rx_bytes;
	}
	public void setRx_bytes(long rx_bytes) {
		this.rx_bytes = rx_bytes;
	}
	public static String generateId(String date, String mac){
		StringBuffer idstring = new StringBuffer();
		idstring.append(date).append(StringHelper.UNDERLINE_STRING_GAP).append(mac);
		return idstring.toString();
	}
}
