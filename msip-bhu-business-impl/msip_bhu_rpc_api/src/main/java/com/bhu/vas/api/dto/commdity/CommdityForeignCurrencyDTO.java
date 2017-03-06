package com.bhu.vas.api.dto.commdity;

import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.rpc.commdity.helper.PaymentInternalHelper;

/**
 * 针对应用请求生成商品的外币金额DTO
 * @author fengshibo
 *
 */
@SuppressWarnings("serial")
public class CommdityForeignCurrencyDTO implements java.io.Serializable{
	//新加坡币
	private String sgd_amount;
	private String sgd_monthCardAmount;
	private String sgd_weekCardAmount;
	private String sgd_dayCardAmount;
	//美元
	private String usd_amount;
	private String usd_monthCardAmount;
	private String usd_weekCardAmount;
	private String usd_dayCardAmount;
	public String getSgd_amount() {
		return sgd_amount;
	}
	public void setSgd_amount(String sgd_amount) {
		this.sgd_amount = sgd_amount;
	}
	public String getSgd_monthCardAmount() {
		return sgd_monthCardAmount;
	}
	public void setSgd_monthCardAmount(String sgd_monthCardAmount) {
		this.sgd_monthCardAmount = sgd_monthCardAmount;
	}
	public String getSgd_weekCardAmount() {
		return sgd_weekCardAmount;
	}
	public void setSgd_weekCardAmount(String sgd_weekCardAmount) {
		this.sgd_weekCardAmount = sgd_weekCardAmount;
	}
	public String getSgd_dayCardAmount() {
		return sgd_dayCardAmount;
	}
	public void setSgd_dayCardAmount(String sgd_dayCardAmount) {
		this.sgd_dayCardAmount = sgd_dayCardAmount;
	}
	public String getUsd_amount() {
		return usd_amount;
	}
	public void setUsd_amount(String usd_amount) {
		this.usd_amount = usd_amount;
	}
	public String getUsd_monthCardAmount() {
		return usd_monthCardAmount;
	}
	public void setUsd_monthCardAmount(String usd_monthCardAmount) {
		this.usd_monthCardAmount = usd_monthCardAmount;
	}
	public String getUsd_weekCardAmount() {
		return usd_weekCardAmount;
	}
	public void setUsd_weekCardAmount(String usd_weekCardAmount) {
		this.usd_weekCardAmount = usd_weekCardAmount;
	}
	public String getUsd_dayCardAmount() {
		return usd_dayCardAmount;
	}
	public void setUsd_dayCardAmount(String usd_dayCardAmount) {
		this.usd_dayCardAmount = usd_dayCardAmount;
	}
	
	public static CommdityForeignCurrencyDTO builder(String amount, String dayAmount, String weekAmount, String monthAmount){
		CommdityForeignCurrencyDTO dto = new CommdityForeignCurrencyDTO();
		dto.setSgd_amount(PaymentInternalHelper.
				fetchFinallyAmountByCurrency(BusinessEnumType.
						PaymentFeeType.SGD.getKey(), amount));
		dto.setUsd_amount(PaymentInternalHelper.
				fetchFinallyAmountByCurrency(BusinessEnumType.
						PaymentFeeType.USD.getKey(), amount));
		dto.setSgd_dayCardAmount(PaymentInternalHelper.
				fetchFinallyAmountByCurrency(BusinessEnumType.
						PaymentFeeType.SGD.getKey(), dayAmount));
		dto.setUsd_dayCardAmount(PaymentInternalHelper.
				fetchFinallyAmountByCurrency(BusinessEnumType.
						PaymentFeeType.USD.getKey(), dayAmount));
		dto.setSgd_weekCardAmount(PaymentInternalHelper.
				fetchFinallyAmountByCurrency(BusinessEnumType.
						PaymentFeeType.SGD.getKey(), weekAmount));
		dto.setUsd_weekCardAmount(PaymentInternalHelper.
				fetchFinallyAmountByCurrency(BusinessEnumType.
						PaymentFeeType.USD.getKey(), weekAmount));
		dto.setSgd_monthCardAmount(PaymentInternalHelper.
				fetchFinallyAmountByCurrency(BusinessEnumType.
						PaymentFeeType.SGD.getKey(), monthAmount));
		dto.setUsd_monthCardAmount(PaymentInternalHelper.
				fetchFinallyAmountByCurrency(BusinessEnumType.
						PaymentFeeType.USD.getKey(), monthAmount));
		return dto;
	}
}

