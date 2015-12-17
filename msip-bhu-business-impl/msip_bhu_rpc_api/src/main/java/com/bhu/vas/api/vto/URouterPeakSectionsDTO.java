package com.bhu.vas.api.vto;

import java.io.Serializable;
import java.util.List;

import com.bhu.vas.api.dto.ret.WifiDeviceRxPeakSectionDTO;
import com.bhu.vas.api.dto.ret.WifiDeviceTxPeakSectionDTO;

/**
 * urouter设备测速分段数据列表vto
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class URouterPeakSectionsDTO implements Serializable{
	//任务号
	private String serial;
	//设备网速下行速率分段数据列表
	private List<WifiDeviceRxPeakSectionDTO> rxs;
	//设备网速上行速率分段数据列表
	private List<WifiDeviceTxPeakSectionDTO> txs;
	
	public String getSerial() {
		return serial;
	}
	public void setSerial(String serial) {
		this.serial = serial;
	}
	public List<WifiDeviceRxPeakSectionDTO> getRxs() {
		return rxs;
	}
	public void setRxs(List<WifiDeviceRxPeakSectionDTO> rxs) {
		this.rxs = rxs;
	}
	public List<WifiDeviceTxPeakSectionDTO> getTxs() {
		return txs;
	}
	public void setTxs(List<WifiDeviceTxPeakSectionDTO> txs) {
		this.txs = txs;
	}
	
}
