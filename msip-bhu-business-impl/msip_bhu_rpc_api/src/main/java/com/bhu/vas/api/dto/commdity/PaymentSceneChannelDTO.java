package com.bhu.vas.api.dto.commdity;

import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.helper.BusinessEnumType.OrderPaymentType;
import com.bhu.vas.api.helper.BusinessEnumType.PaymentChannelType;

@SuppressWarnings("serial")
public class PaymentSceneChannelDTO implements java.io.Serializable{
	private String channel;
	private String payment_type;
	
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getPayment_type() {
		return payment_type;
	}
	public void setPayment_type(String payment_type) {
		this.payment_type = payment_type;
	}
	
	public static PaymentSceneChannelDTO builder(String payment_type, Integer channel){
		PaymentSceneChannelDTO dto = new PaymentSceneChannelDTO();
		PaymentChannelType channelType = BusinessEnumType.PaymentChannelType.fromKey(channel);
		OrderPaymentType paymentType = BusinessEnumType.OrderPaymentType.fromKey(payment_type);
		dto.setPayment_type(payment_type);
		switch (paymentType) {
		case PcWeixin:
		case PcAlipay:
			break;
		case WapWeixin:
		case WapAlipay:
		case AppWeixin:
		case AppAlipay:
		case WapPayPal:
			switch (channelType) {
			case BHUWIFIAPP:
				dto.setChannel(BusinessEnumType.PaymentSceneChannelType.APPMANAGER.getName());
				break;
			case UTOOL:
				dto.setChannel(BusinessEnumType.PaymentSceneChannelType.APPHELPER.getName());
				break;
			case CARDREWARD:
			case OTHERS:
				dto.setChannel(BusinessEnumType.PaymentSceneChannelType.WAPQR.getName());
				break;
			case BHUWIFIWEB:
			case BHUWIFIWEBF2P:
			case OPS:
			default:
				dto.setChannel(BusinessEnumType.PaymentSceneChannelType.WAPH5.getName());
				break;
			}
			break;
		default:
			System.out.println("payment type not supported "+payment_type);
			break;
		}
		return dto;
	}
}
