package com.bhu.vas.api.rpc.charging.dto;

import com.smartwork.msip.cores.helper.ArithHelper;

/**
 * 
 * @author Edmond
 *
 */
public class SharedealInfo {
	// 设备mac地址
	private String mac;
	private String orderid;
	// 总金额
	private double cash;
	private boolean belong = true;
	private int owner;
	// 绑定用户分成金额
	private double owner_cash;
	// 代理商用户分成金额
	// private double agent_cash;
	private int manufacturer;
	// 厂商用户分成金额
	private double manufacturer_cash;

	private int distributor;
	private double distributor_cash;

	private String umac;

	public SharedealInfo(String mac, String umac, String orderid, double cash) {
		super();
		this.mac = mac;
		this.orderid = orderid;
		this.cash = cash;
		this.umac = umac;
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

	/*
	 * public double getAgent_cash() { return agent_cash; } public void
	 * setAgent_cash(double agent_cash) { this.agent_cash = agent_cash; }
	 */
	public double getManufacturer_cash() {
		return manufacturer_cash;
	}

	public void setManufacturer_cash(double manufacturer_cash) {
		this.manufacturer_cash = manufacturer_cash;
	}

	public static SharedealInfo calculate(String mac, String umac, String orderid, 
			                              double cash, double owner_percent,
			                              double manufacturer_percent, double distributor_percent) {
		SharedealInfo info = new SharedealInfo(mac, umac, orderid, cash);
		if (cash > 0) {
			double owner_cash = ArithHelper.round(ArithHelper.mul(cash, owner_percent), 4);
			double manufacturer_cash = ArithHelper.round
			    (ArithHelper.mul(cash, manufacturer_percent), 4);
			info.setOwner_cash(owner_cash);
			info.setManufacturer_cash(manufacturer_cash);
			info.setDistributor_cash
			    (ArithHelper.sub(cash, ArithHelper.add(owner_cash, manufacturer_cash)));
		}
		return info;
	}

	public static void main(String[] args) {
		// System.out.println(ArithHelper.round(ArithHelper.mul(2.1212, 2), 2));
		double ss = ArithHelper.add(2.12, 2.1355);
		System.out.println(ss);
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

	public int getDistributor() {
		return distributor;
	}

	public void setDistributor(int distributor) {
		this.distributor = distributor;
	}

	public double getDistributor_cash() {
		return distributor_cash;
	}

	public void setDistributor_cash(double distributor_cash) {
		this.distributor_cash = distributor_cash;
	}

	public String getUmac() {
		return umac;
	}

	public void setUmac(String umac) {
		this.umac = umac;
	}
	
}
