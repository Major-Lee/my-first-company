package com.bhu.vas.api.dto.commdity.internal.pay;

import java.io.Serializable;
import java.util.List;

import com.bhu.vas.api.rpc.payment.vto.PaymentChannelStatVTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


/**
 * 支付系统接口返回数据基类DTO
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class ResponsePaymentChannelSatDTO implements Serializable{
	//支付是否成功
	private boolean success;
	//失败错误码
	@JsonInclude(Include.NON_NULL)
	private String errorcode;
	//失败原因
	@JsonInclude(Include.NON_NULL)
	private String msg;
	
	@JsonInclude(Include.NON_EMPTY)
	private  List<PaymentChannelStatVTO> result;
	
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getErrorcode() {
		return errorcode;
	}
	public void setErrorcode(String errorcode) {
		this.errorcode = errorcode;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public List<PaymentChannelStatVTO> getResult() {
		return result;
	}
	public void setResult(List<PaymentChannelStatVTO> result) {
		this.result = result;
	}
}

