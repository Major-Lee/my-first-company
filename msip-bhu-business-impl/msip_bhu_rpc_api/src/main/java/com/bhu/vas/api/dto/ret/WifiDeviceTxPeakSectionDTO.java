package com.bhu.vas.api.dto.ret;

import java.io.Serializable;

/**
 * wifi设备测速的上行速率dto
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class WifiDeviceTxPeakSectionDTO implements Serializable{
	//从测速开始到目前的时间 毫秒
	private String time_cost;
	//测速上传的总数据长度 单位字节
	private String tx_bytes;
	//是否为最后一条
	private boolean last;
	
	public String getTime_cost() {
		return time_cost;
	}
	public void setTime_cost(String time_cost) {
		this.time_cost = time_cost;
	}
	public String getTx_bytes() {
		return tx_bytes;
	}
	public void setTx_bytes(String tx_bytes) {
		this.tx_bytes = tx_bytes;
	}
	public boolean isLast() {
		return last;
	}
	public void setLast(boolean last) {
		this.last = last;
	}
}