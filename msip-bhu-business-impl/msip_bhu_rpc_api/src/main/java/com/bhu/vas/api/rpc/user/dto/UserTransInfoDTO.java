package com.bhu.vas.api.rpc.user.dto;

/**
 * 用户交易信息
 * @author Jason
 *
 */
@SuppressWarnings("serial")
public class UserTransInfoDTO implements java.io.Serializable {
	//用户Id
	private int uid;
	//交易时间
	private String transTime;
	//交易类型
	private String transType;
	//交易单号
	private String transNo;
	//终端Mac
	private String terminalMac;
	//打赏设备Mac
	private String deciveMac;
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getTransTime() {
		return transTime;
	}
	public void setTransTime(String transTime) {
		this.transTime = transTime;
	}
	public String getTransType() {
		return transType;
	}
	public void setTransType(String transType) {
		this.transType = transType;
	}
	public String getTransNo() {
		return transNo;
	}
	public void setTransNo(String transNo) {
		this.transNo = transNo;
	}
	public String getTerminalMac() {
		return terminalMac;
	}
	public void setTerminalMac(String terminalMac) {
		this.terminalMac = terminalMac;
	}
	public String getDeciveMac() {
		return deciveMac;
	}
	public void setDeciveMac(String deciveMac) {
		this.deciveMac = deciveMac;
	}
}
