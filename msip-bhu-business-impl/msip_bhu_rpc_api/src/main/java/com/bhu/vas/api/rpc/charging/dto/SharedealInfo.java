package com.bhu.vas.api.rpc.charging.dto;

import com.smartwork.msip.cores.helper.ArithHelper;

/**
 * 
 * @author Edmond
 *
 */
public class SharedealInfo {
	//设备mac地址
	private String mac;
	private String orderid;
	//总金额
	private double cash;
	private boolean belong = true;
	private int owner;
	//绑定用户分成金额
	private double owner_cash;
	//代理商用户分成金额
	//private double agent_cash;
	private int manufacturer;
	//厂商用户分成金额
	private double manufacturer_cash;
	
	public SharedealInfo(String mac,String orderid,double cash) {
		super();
		this.mac = mac;
		this.orderid = orderid;
		this.cash = cash;
	}
	public double getCash() {
		return cash;
	}
	public void setCash(double cash) {
		this.cash = cash;
	}

	
	public String getOrderid() {
		return orderid;
	}
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public double getOwner_cash() {
		return owner_cash;
	}
	public void setOwner_cash(double owner_cash) {
		this.owner_cash = owner_cash;
	}
	/*public double getAgent_cash() {
		return agent_cash;
	}
	public void setAgent_cash(double agent_cash) {
		this.agent_cash = agent_cash;
	}*/
	public double getManufacturer_cash() {
		return manufacturer_cash;
	}
	public void setManufacturer_cash(double manufacturer_cash) {
		this.manufacturer_cash = manufacturer_cash;
	}

	public static SharedealInfo calculate(String mac,String orderid,
			double cash,
			double owner_percent,
			//double agent_percent,
			double manufacturer_percent
			){
		SharedealInfo info = new SharedealInfo(mac,orderid,cash);
		if(cash > 0){
			info.setOwner_cash(ArithHelper.round(ArithHelper.mul(cash, owner_percent),2));
			//info.setAgent_cash(ArithHelper.round(ArithHelper.mul(cash, agent_percent),2));
			info.setManufacturer_cash(ArithHelper.sub(cash,info.getOwner_cash()));
		}
		return info;
	}
	public int getOwner() {
		return owner;
	}
	public void setOwner(int owner) {
		this.owner = owner;
	}
	public int getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(int manufacturer) {
		this.manufacturer = manufacturer;
	}
	public boolean isBelong() {
		return belong;
	}
	public void setBelong(boolean belong) {
		this.belong = belong;
	}
	
}
