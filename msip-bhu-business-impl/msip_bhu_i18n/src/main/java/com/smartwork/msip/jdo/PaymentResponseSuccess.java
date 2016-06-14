package com.smartwork.msip.jdo;

import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.i18n.LocalI18NMessageSource;

public class PaymentResponseSuccess extends Response{
	public static final Response BUSINESS_SUCCESS = new PaymentResponseSuccess("操作成功",ResponseSuccessCode.COMMON_BUSINESS_SUCCESS);
	private String code;
    private String codemsg;
    @JsonInclude(Include.NON_NULL)
    Object result;
	public PaymentResponseSuccess() {
		super();
		this.setSuccess(true);
	}
	/*public ResponseSuccess(Object result) {
		this();
		this.setResult(result);
	}*/
    public PaymentResponseSuccess(String message, ResponseSuccessCode responseSuccessCode) {
    	this(message, responseSuccessCode, null, null);
    }
    
    public PaymentResponseSuccess(String message, ResponseSuccessCode responseSuccessCode, Object result) {
    	this(message, responseSuccessCode, result, null);
    }

    public PaymentResponseSuccess(String message, ResponseSuccessCode responseSuccessCode, Object result, Object[] txts) {
    	this();
    	this.setMsg(message);
        this.code = responseSuccessCode.code();
        Assert.notNull(this.code, "msgcode must be set!");
        this.codemsg = LocalI18NMessageSource.getInstance().getMessage(responseSuccessCode.i18n(),txts);
        this.result = result;
    }
	
	public Object getresult() {
		return result;
	}
	public void setresult(Object result) {
		this.result = result;
	}
	public static PaymentResponseSuccess embed(Object result){
		PaymentResponseSuccess re = new PaymentResponseSuccess(PaymentResponseSuccess.SUCCESS.getMsg(),
				ResponseSuccessCode.COMMON_BUSINESS_SUCCESS,result);
		return re;
	}
	
	public static PaymentResponseSuccess embed(ResponseSuccessCode code){
		PaymentResponseSuccess re = new PaymentResponseSuccess(PaymentResponseSuccess.SUCCESS.getMsg(),code);
		return re;
	}
	
	public static PaymentResponseSuccess embed(ResponseSuccessCode code, Object result){
		PaymentResponseSuccess re = new PaymentResponseSuccess(PaymentResponseSuccess.SUCCESS.getMsg(),code, result);
		return re;
	}
	public static PaymentResponseSuccess embed(ResponseSuccessCode code, Object result, String[] txts){
		PaymentResponseSuccess re = new PaymentResponseSuccess(PaymentResponseSuccess.SUCCESS.getMsg(),code, result, txts);
		return re;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCodemsg() {
		return codemsg;
	}

	public void setCodemsg(String codemsg) {
		this.codemsg = codemsg;
	}

	public static void main(String[] argv){
		System.out.println(JsonHelper.getJSONString(new PaymentResponseSuccess()));
    }
}
