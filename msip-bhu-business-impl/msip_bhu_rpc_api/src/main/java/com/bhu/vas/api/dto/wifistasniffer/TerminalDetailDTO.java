package com.bhu.vas.api.dto.wifistasniffer;

import java.io.Serializable;
/**
 * 周边探测终端流水细节dto
 * @author tangzichao
 *snifftime$#state$#duration
 */
@SuppressWarnings("serial")
public class TerminalDetailDTO implements Serializable{
	//时长 单位秒
	private int duration;
	//探测状态
	private int state;
	//探测上线时间
	private long snifftime;
	
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
}
