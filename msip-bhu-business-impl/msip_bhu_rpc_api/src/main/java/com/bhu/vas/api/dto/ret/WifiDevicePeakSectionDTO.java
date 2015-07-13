package com.bhu.vas.api.dto.ret;

import java.io.Serializable;

/**
 * wifi设备测速的分段数据dto
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class WifiDevicePeakSectionDTO implements Serializable{
	//设备测速下行速率分段数据
	private WifiDeviceRxPeakSectionDTO rx_dto;
	//设备测速上行速率分段数据
	private WifiDeviceTxPeakSectionDTO tx_dto;
	
	public WifiDeviceRxPeakSectionDTO getRx_dto() {
		return rx_dto;
	}
	public void setRx_dto(WifiDeviceRxPeakSectionDTO rx_dto) {
		this.rx_dto = rx_dto;
	}
	public WifiDeviceTxPeakSectionDTO getTx_dto() {
		return tx_dto;
	}
	public void setTx_dto(WifiDeviceTxPeakSectionDTO tx_dto) {
		this.tx_dto = tx_dto;
	}
}