package com.bhu.vas.business.ds.agent.mdto;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.smartwork.msip.cores.helper.StringHelper;

/**
 * 代理商每日汇总数据
 * 代理商代理的所有设备的每日数据汇总
 * @author Edmond
 *
 */
@Document(collection = "t_agent_wholeday")
public class AgentWholeDayMDTO {
	@Id
	private String id;
	//yyyyMMdd
	private String date;
	private int user;
	//当天在线时长
	private long onlineduration;
	//当天连接次数
	private int connecttimes;
	//终端数
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
	public int getUser() {
		return user;
	}
	public void setUser(int user) {
		this.user = user;
	}
	public long getOnlineduration() {
		return onlineduration;
	}
	public void setOnlineduration(long onlineduration) {
		this.onlineduration = onlineduration;
	}
	public int getConnecttimes() {
		return connecttimes;
	}
	public void setConnecttimes(int connecttimes) {
		this.connecttimes = connecttimes;
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
	
	public static String generateId(String date, int user){
		StringBuffer idstring = new StringBuffer();
		idstring.append(date).append(StringHelper.UNDERLINE_STRING_GAP).append(user);
		return idstring.toString();
	}
}
