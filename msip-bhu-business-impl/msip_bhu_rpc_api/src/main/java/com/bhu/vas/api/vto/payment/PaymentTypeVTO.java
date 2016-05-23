package com.bhu.vas.api.vto.payment;

/**
 * 返回支付信息
 * @author Pengyu
 *
 */
@SuppressWarnings("serial")
public class PaymentTypeVTO implements java.io.Serializable{
	
	private String url;
	private String type;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}