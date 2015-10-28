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
	//当月在线时长device onlineduration
	private double dod;//;
	//当月连接次数device connect times
	private int  dct;
	private double dtx_bytes;
	private double drx_bytes;
	//终端数
	private int handsets;
	//当月终端连接次数 
	private int  hct;
	//当月在线设备的连接终端的在线时长总和handset onlineduration
	private double hod;
	//handsets 上行流量
	private double htx_bytes;
	//handsets 下行流量
	private double hrx_bytes;
	
	private int cashbacks;
	private int samedays;
	private String updated_at;
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
	
	public int getDct() {
		return dct;
	}
	public void setDct(int dct) {
		this.dct = dct;
	}
	public int getHandsets() {
		return handsets;
	}
	public void setHandsets(int handsets) {
		this.handsets = handsets;
	}
	public int getHct() {
		return hct;
	}
	public void setHct(int hct) {
		this.hct = hct;
	}
	
	public String getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}
	
	public int getCashbacks() {
		return cashbacks;
	}
	public void setCashbacks(int cashbacks) {
		this.cashbacks = cashbacks;
	}
	public int getSamedays() {
		return samedays;
	}
	public void setSamedays(int samedays) {
		this.samedays = samedays;
	}
	
	public double getDod() {
		return dod;
	}
	public void setDod(double dod) {
		this.dod = dod;
	}
	public double getDtx_bytes() {
		return dtx_bytes;
	}
	public void setDtx_bytes(double dtx_bytes) {
		this.dtx_bytes = dtx_bytes;
	}
	public double getDrx_bytes() {
		return drx_bytes;
	}
	public void setDrx_bytes(double drx_bytes) {
		this.drx_bytes = drx_bytes;
	}
	public double getHod() {
		return hod;
	}
	public void setHod(double hod) {
		this.hod = hod;
	}
	public double getHtx_bytes() {
		return htx_bytes;
	}
	public void setHtx_bytes(double htx_bytes) {
		this.htx_bytes = htx_bytes;
	}
	public double getHrx_bytes() {
		return hrx_bytes;
	}
	public void setHrx_bytes(double hrx_bytes) {
		this.hrx_bytes = hrx_bytes;
	}
	public static String generateId(String date, String mac){
		StringBuffer idstring = new StringBuffer();
		idstring.append(date).append(StringHelper.UNDERLINE_STRING_GAP).append(mac);
		return idstring.toString();
	}
}
