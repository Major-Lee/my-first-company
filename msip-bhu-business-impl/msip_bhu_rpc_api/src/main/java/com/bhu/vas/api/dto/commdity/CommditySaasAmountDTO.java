package com.bhu.vas.api.dto.commdity;


/**
 * 针对应用请求生成商品的金额DTO
 * @author fengshibo
 *
 */
@SuppressWarnings("serial")
public class CommditySaasAmountDTO implements java.io.Serializable{
	private String noapp;
	private String canbe_trunoff;
	public String getNoapp() {
		return noapp;
	}
	public void setNoapp(String noapp) {
		this.noapp = noapp;
	}
	public String getCanbe_trunoff() {
		return canbe_trunoff;
	}
	public void setCanbe_trunoff(String canbe_trunoff) {
		this.canbe_trunoff = canbe_trunoff;
	}
	
}

