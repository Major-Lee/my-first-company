package com.bhu.vas.business.ds.agent.mdto;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.smartwork.msip.cores.helper.StringHelper;

/**
 * wifi设备每日全天统计数据，包括此天的上下线日志
 * 此表数据每月汇总生成月统计数据，并清除汇总后的月数据（考虑数据量大，定时任务清除数据）
 * 此表数据理论上可以查询当前自然月的设备上下线日志
 * @author Edmond
 *
 */
@Document(collection = "t_wifi_device_wholeday")
public class WifiDeviceWholeDayMDTO {
	/**
	 * 规则为 yyyy-MM-dd_mac
	 */
	@Id
	private String id;
	//yyyyMMdd
	private String date;
	private String mac;
	//当天在线时长device onlineduration
	private long dod;//;
	//当天连接次数device connect times
	private int  dct;
	private long dtx_bytes;
	private long drx_bytes;
	//终端数 对单设备排重
	private int handsets;
	//当日终端连接次数 
	private int  hct;
	//当日在线设备的连接终端的在线时长总和handset onlineduration
	private long hod;
	//handsets 上行流量
	private long htx_bytes;
	//handsets 下行流量
	private long hrx_bytes;
	private boolean cashback;
	private boolean sameday;
	//上下线连接记录
	private List<LineRecord> records;
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
	
	public List<LineRecord> getRecords() {
		return records;
	}
	public void setRecords(List<LineRecord> records) {
		this.records = records;
	}
	
	public static String generateId(String date, String mac){
		StringBuffer idstring = new StringBuffer();
		idstring.append(date).append(StringHelper.UNDERLINE_STRING_GAP).append(mac);
		return idstring.toString();
	}
	public int getHandsets() {
		return handsets;
	}
	public void setHandsets(int handsets) {
		this.handsets = handsets;
	}
	public long getDod() {
		return dod;
	}
	public void setDod(long dod) {
		this.dod = dod;
	}
	public int getDct() {
		return dct;
	}
	public void setDct(int dct) {
		this.dct = dct;
	}
	public long getDtx_bytes() {
		return dtx_bytes;
	}
	public void setDtx_bytes(long dtx_bytes) {
		this.dtx_bytes = dtx_bytes;
	}
	public long getDrx_bytes() {
		return drx_bytes;
	}
	public void setDrx_bytes(long drx_bytes) {
		this.drx_bytes = drx_bytes;
	}
	public int getHct() {
		return hct;
	}
	public void setHct(int hct) {
		this.hct = hct;
	}
	public long getHod() {
		return hod;
	}
	public void setHod(long hod) {
		this.hod = hod;
	}
	public long getHtx_bytes() {
		return htx_bytes;
	}
	public void setHtx_bytes(long htx_bytes) {
		this.htx_bytes = htx_bytes;
	}
	public long getHrx_bytes() {
		return hrx_bytes;
	}
	public void setHrx_bytes(long hrx_bytes) {
		this.hrx_bytes = hrx_bytes;
	}
	public boolean isCashback() {
		return cashback;
	}
	public void setCashback(boolean cashback) {
		this.cashback = cashback;
	}
	public boolean isSameday() {
		return sameday;
	}
	public void setSameday(boolean sameday) {
		this.sameday = sameday;
	}
	
}
