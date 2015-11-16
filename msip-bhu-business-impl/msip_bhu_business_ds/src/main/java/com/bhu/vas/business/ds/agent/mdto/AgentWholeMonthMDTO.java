package com.bhu.vas.business.ds.agent.mdto;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.smartwork.msip.cores.helper.StringHelper;

/**
 * 代理商每月汇总数据 
 * 月汇总里面只进行
 * 		收益记录
 * 		此月代理的所有设备在线时长
 * 		此月代理的所有设备总连接次数
 *      此月代理的所有设备数量
 *      此月代理的所有设备上下行流量
 * 
 * 代理商代理的所有设备的月数据汇总
 * @author Edmond
 *
 */
@Document(collection = "t_agent_wholemonth")
public class AgentWholeMonthMDTO {
	@Id
	private String id;//2015-09_user
	//yyyyMMdd
	private String date;
	private int user;
	
	//当月在线时长
	private double dod;
	//当月连接次数
	private int dct;
	//在线设备数
	private int devices;
	private double dtx_bytes;
	private double drx_bytes;
	
	//在线终端数 此数只是所有的设备的连接终端数的和，可能会有重复滴，eg，一个终端连一个设备后又连另外一个设备，则计数为2
	private int handsets;
	//当日终端连接次数 
	private int  hct;
	//handset onlineduration
	private double hod;
	//handsets 上行流量
	private double htx_bytes;
	//handsets 下行流量
	private double hrx_bytes;
	
	private int newdevices;
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
	public int getUser() {
		return user;
	}
	public void setUser(int user) {
		this.user = user;
	}
	
	public int getDct() {
		return dct;
	}
	public void setDct(int dct) {
		this.dct = dct;
	}
	public int getDevices() {
		return devices;
	}
	public void setDevices(int devices) {
		this.devices = devices;
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
	public static String generateId(String date, int user){
		StringBuffer idstring = new StringBuffer();
		idstring.append(date).append(StringHelper.UNDERLINE_STRING_GAP).append(user);
		return idstring.toString();
	}
	public String getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}
	public int getNewdevices() {
		return newdevices;
	}
	public void setNewdevices(int newdevices) {
		this.newdevices = newdevices;
	}
}
