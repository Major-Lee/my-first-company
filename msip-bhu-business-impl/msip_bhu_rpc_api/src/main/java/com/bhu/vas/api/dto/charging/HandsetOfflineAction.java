package com.bhu.vas.api.dto.charging;

public class HandsetOfflineAction extends ChargingAction{
	private String hmac;
	private long tx_bytes;
	private long rx_bytes;
	public String getHmac() {
		return hmac;
	}
	public void setHmac(String hmac) {
		this.hmac = hmac;
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

	@Override
	public String getAct() {
		return ActionBuilder.ActionMode.HandsetOffline.getPrefix();
	}
	
}
