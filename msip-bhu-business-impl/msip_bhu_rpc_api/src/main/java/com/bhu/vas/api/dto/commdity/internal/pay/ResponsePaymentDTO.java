package com.bhu.vas.api.dto.commdity.internal.pay;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


/**
 * 支付系统接口返回数据基类DTO
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class ResponsePaymentDTO implements java.io.Serializable{
	//支付是否成功
	private boolean success;
	//失败错误码
	@JsonInclude(Include.NON_NULL)
	private String errorcode;
	//失败原因
	@JsonInclude(Include.NON_NULL)
	private String msg;
	
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
}

