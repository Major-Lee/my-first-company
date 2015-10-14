package com.bhu.vas.api.dto.ret;

import java.io.Serializable;

/**
 * wifi设备测速的下行速率dto
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class WifiDeviceRxPeakSectionDTO implements Serializable{
	//从测速开始到目前的时间 毫秒
	private String time_cost;
	//测速下载的总数据长度 单位字节
	private String rx_bytes;
	//是否为最后一条
	private boolean last;
	
	public String getTime_cost() {
		return time_cost;
	}
	public void setTime_cost(String time_cost) {
		this.time_cost = time_cost;
	}
	public String getRx_bytes() {
		return rx_bytes;
	}
	public void setRx_bytes(String rx_bytes) {
		this.rx_bytes = rx_bytes;
	}
	public boolean isLast() {
		return last;
	}
	public void setLast(boolean last) {
		this.last = last;
	}
}