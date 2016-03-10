package com.bhu.vas.api.dto.commdity.internal.pay;


/**
 * 支付系统接口返回支付urlDTO
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class ResponseCreatePaymentUrlDTO extends ResponsePaymentDTO{
	//支付url具体信息
	private String params;

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}
	
	@Override
	public String toString(){
		return String.format("ResponseCreatePaymentUrlDTO: success[%s] errorcode[%s] msg[%s] params[%s]", 
				super.isSuccess(), super.getErrorcode(), super.getMsg(), params);
	}
}

