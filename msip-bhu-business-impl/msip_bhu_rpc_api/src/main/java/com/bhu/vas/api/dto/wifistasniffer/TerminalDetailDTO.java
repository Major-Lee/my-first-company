package com.bhu.vas.api.dto.wifistasniffer;

import java.io.Serializable;
/**
 * 周边探测终端流水细节dto
 * @author tangzichao
 */
@SuppressWarnings("serial")
public class TerminalDetailDTO implements Serializable{
	//时长 单位秒
	private int duration;
	//探测状态
	private int state;
	//探测上线时间 毫秒
	private long snifftime;
	//探测上线时间 毫秒
	//private long onlinetime;
	//探测下线时间 毫秒
	//private long offlinetime;
	
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public long getSnifftime() {
		return snifftime;
	}
	public void setSnifftime(long snifftime) {
		this.snifftime = snifftime;
	}
//	public long getOnlinetime() {
//		return onlinetime;
//	}
//	public void setOnlinetime(long onlinetime) {
//		this.onlinetime = onlinetime;
//	}
//	public long getOfflinetime() {
//		return offlinetime;
//	}
//	public void setOfflinetime(long offlinetime) {
//		this.offlinetime = offlinetime;
//	}
	
}
