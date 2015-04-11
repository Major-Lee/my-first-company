package com.bhu.vas.api.rpc.devices.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BasePKModel;
/**
 * wifi设备对应的终端标记记录
 * 只有绑定了urouter的设备才会进行记录
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class WifiHandsetDeviceMark extends BasePKModel<WifiHandsetDeviceMarkPK>{
	//上行速率
	private String data_tx_rate;
	//下行速率
	private String data_rx_rate;
	//终端名称
	private String hd_name;
	//上行限速
	private String data_tx_limit;
	//下行限速
	private String data_rx_limit;
	//是否在黑名单
	private boolean block;
	
	private Date created_at;
	
	@Override
	protected Class<WifiHandsetDeviceMarkPK> getPKClass() {
		return WifiHandsetDeviceMarkPK.class;
	}
	
	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}
	
	@Override
	public void preUpdate() {
		super.preUpdate();
	}

	public String getData_tx_rate() {
		return data_tx_rate;
	}

	public void setData_tx_rate(String data_tx_rate) {
		this.data_tx_rate = data_tx_rate;
	}

	public String getData_rx_rate() {
		return data_rx_rate;
	}

	public void setData_rx_rate(String data_rx_rate) {
		this.data_rx_rate = data_rx_rate;
	}

	public String getHd_name() {
		return hd_name;
	}

	public void setHd_name(String hd_name) {
		this.hd_name = hd_name;
	}

	public String getData_tx_limit() {
		return data_tx_limit;
	}

	public void setData_tx_limit(String data_tx_limit) {
		this.data_tx_limit = data_tx_limit;
	}

	public String getData_rx_limit() {
		return data_rx_limit;
	}

	public void setData_rx_limit(String data_rx_limit) {
		this.data_rx_limit = data_rx_limit;
	}

	public boolean isBlock() {
		return block;
	}

	public void setBlock(boolean block) {
		this.block = block;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
}