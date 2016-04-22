package com.bhu.vas.push.common.context;

import com.smartwork.msip.cores.helper.StringHelper;

/**
 * 打赏分成通知 push上下文
 * @author tangzichao
 *
 */
public class SharedealNofityContext {
	//终端厂商
	private String umac_mf = StringHelper.EMPTY_STRING_GAP;
	//分成金额
	private String cash;
	//终端类型名称
	private String umac_type_desc;
	//支付方式名称
	private String payment_type_name;
	
	public String getUmac_mf() {
		return umac_mf;
	}
	public void setUmac_mf(String umac_mf) {
		this.umac_mf = umac_mf;
	}
	public String getCash() {
		return cash;
	}
	public void setCash(String cash) {
		this.cash = cash;
	}
	public String getUmac_type_desc() {
		return umac_type_desc;
	}
	public void setUmac_type_desc(String umac_type_desc) {
		this.umac_type_desc = umac_type_desc;
	}
	public String getPayment_type_name() {
		return payment_type_name;
	}
	public void setPayment_type_name(String payment_type_name) {
		this.payment_type_name = payment_type_name;
	}
}
