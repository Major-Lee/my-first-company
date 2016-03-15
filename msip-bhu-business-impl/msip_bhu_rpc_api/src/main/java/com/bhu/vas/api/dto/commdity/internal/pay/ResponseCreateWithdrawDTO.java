package com.bhu.vas.api.dto.commdity.internal.pay;


/**
 * 支付系统接口返回支付urlDTO
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class ResponseCreateWithdrawDTO extends ResponsePaymentDTO{
	private String withdraw_type;
	//支付url具体信息
	private String url;

	public String getWithdraw_type() {
		return withdraw_type;
	}
	public void setWithdraw_type(String withdraw_type) {
		this.withdraw_type = withdraw_type;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Override
	public String toString(){
		return String.format("ResponseCreateWithdrawDTO: success[%s] errorcode[%s] msg[%s] withdraw_type [%s] url[%s]", 
				super.isSuccess(), super.getErrorcode(), super.getMsg(),withdraw_type, url);
	}
}

