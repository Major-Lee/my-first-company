package com.bhu.vas.api.rpc.charging.model;

import com.smartwork.msip.cores.orm.model.BaseStringModel;

/**
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class WifiDeviceSharedealConfigs extends BaseStringModel{
	public static final String Default_ConfigsWifiID = "-1";
	//设备批次号（导入批次号-一般库房进行编号）
	private String batchno;
	//绑定用户 <=0 代表未绑定的设备
	private int owner;
	//分销商用户 <=0 代表 设备不存在分销商
	private int agent;
	//厂商用户  必须>零
	private int manufacturer;
	
	//约定的收益分成比例 最多小数点保留后两位 60%-10%-30%开，总值为1；
	private double owner_percent = 0.60d;
	private double agent_percent = 0.10d;
	private double manufacturer_percent = 0.30d;
	public int getOwner() {
		return owner;
	}
	public void setOwner(int owner) {
		this.owner = owner;
	}

	public int getAgent() {
		return agent;
	}
	public void setAgent(int agent) {
		this.agent = agent;
	}
	public int getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(int manufacturer) {
		this.manufacturer = manufacturer;
	}
	public double getOwner_percent() {
		return owner_percent;
	}
	public void setOwner_percent(double owner_percent) {
		this.owner_percent = owner_percent;
	}
	public double getAgent_percent() {
		return agent_percent;
	}
	public void setAgent_percent(double agent_percent) {
		this.agent_percent = agent_percent;
	}
	public double getManufacturer_percent() {
		return manufacturer_percent;
	}
	public void setManufacturer_percent(double manufacturer_percent) {
		this.manufacturer_percent = manufacturer_percent;
	}
/*	//扣除20%的税，3%的交易费用
	private double withdraw_tax_percent = 0.20d;
	//3%的交易费用
	private double withdraw_trancost_percent = 0.03d;
*/
	public String getBatchno() {
		return batchno;
	}
	public void setBatchno(String batchno) {
		this.batchno = batchno;
	}	
}
