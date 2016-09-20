package com.bhu.vas.api.dto.commdity;
/**
 * 短信认证订单dto
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class OrderSMSVTO extends OrderVTO{
	//用户uid
	private Integer uid;
	//设备mac
	private String mac;
	//设备业务线
	private String mac_dut;
	//用户mac
	private String umac;
	//用户终端厂商
	private String umac_mf;
	//用户终端类型
	private Integer umactype;
	//短信验证的手机号
	//private String context;
	//虚拟币
	private long vcurrency;
	
	public Integer getUid() {
		return uid;
	}
	public void setUid(Integer uid) {
		this.uid = uid;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getMac_dut() {
		return mac_dut;
	}
	public void setMac_dut(String mac_dut) {
		this.mac_dut = mac_dut;
	}
	public String getUmac() {
		return umac;
	}
	public void setUmac(String umac) {
		this.umac = umac;
	}
	public String getUmac_mf() {
		return umac_mf;
	}
	public void setUmac_mf(String umac_mf) {
		this.umac_mf = umac_mf;
	}
	public Integer getUmactype() {
		return umactype;
	}
	public void setUmactype(Integer umactype) {
		this.umactype = umactype;
	}
	public long getVcurrency() {
		return vcurrency;
	}
	public void setVcurrency(long vcurrency) {
		this.vcurrency = vcurrency;
	}
}
