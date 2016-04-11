package com.bhu.vas.api.rpc.charging.model;

import com.smartwork.msip.cores.orm.model.BaseStringModel;

/**
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class WifiDeviceSharedealConfigs extends BaseStringModel{
	public static final String Default_ConfigsWifiID = "00:00:00:00:00:00";
	public static final String Default_Range_Cash = "1.5-3.5";
	public static final String Default_Range_AIT = "14400";
	//如果owner字段为<=0则采用此值为缺省分成用户
	public static final String Default_Owner = "1";
	//如果manufacturer<=0字段为空则采用此值为缺省分成用户
	public static final String Default_Manufacturer = "110";
	//设备批次号（导入批次号-一般库房进行编号）
	private String batchno;
	//绑定用户 <=0 代表未绑定的设备（此数据为冗余数据，解绑和绑定、重置操作都需要操作此值）
	private int owner = 0;
	//厂商用户  必须>零 目前值为定值
	private int manufacturer = 0;
	//约定的收益分成比例 最多小数点保留后两位 70%-30%开，总值为1；
	private double owner_percent = 0.70d;
	private double manufacturer_percent = 0.30d;
	
	//pc端和移动端的打赏金额范围及相关时长
	private String range_cash_pc = Default_Range_Cash;
	private String range_cash_mobile = Default_Range_Cash;
	//acessInternettime ait
	private String range_ait_pc = Default_Range_AIT;
	private String range_ait_mobile = Default_Range_AIT;
	//是否可以关闭开关
	private boolean canbe_turnoff;
	//生产环境过程中不存在的设备分成配置 会缺省应用并生效缺省配置时，此值为true，通过导入的设备值为false，后续修改设备配置值为false
	private boolean runtime_applydefault = true;
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
	public double getOwner_percent() {
		return owner_percent;
	}
	public void setOwner_percent(double owner_percent) {
		this.owner_percent = owner_percent;
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
	public boolean isRuntime_applydefault() {
		return runtime_applydefault;
	}
	public void setRuntime_applydefault(boolean runtime_applydefault) {
		this.runtime_applydefault = runtime_applydefault;
	}
	public String getRange_cash_pc() {
		return range_cash_pc;
	}
	public void setRange_cash_pc(String range_cash_pc) {
		this.range_cash_pc = range_cash_pc;
	}
	public String getRange_cash_mobile() {
		return range_cash_mobile;
	}
	public void setRange_cash_mobile(String range_cash_mobile) {
		this.range_cash_mobile = range_cash_mobile;
	}
	public String getRange_ait_pc() {
		return range_ait_pc;
	}
	public void setRange_ait_pc(String range_ait_pc) {
		this.range_ait_pc = range_ait_pc;
	}
	public String getRange_ait_mobile() {
		return range_ait_mobile;
	}
	public void setRange_ait_mobile(String range_ait_mobile) {
		this.range_ait_mobile = range_ait_mobile;
	}
	public boolean isCanbe_turnoff() {
		return canbe_turnoff;
	}
	public void setCanbe_turnoff(boolean canbe_turnoff) {
		this.canbe_turnoff = canbe_turnoff;
	}	
}
