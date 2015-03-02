package com.smartwork.msip.jdo;

import org.springframework.util.Assert;

import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.i18n.LocalI18NMessageSource;

public class ResponseSuccess extends Response{
	public static final Response BUSINESS_SUCCESS = new ResponseSuccess("操作成功",ResponseSuccessCode.COMMON_BUSINESS_SUCCESS);
	private String code;
    private String codemsg;
    Object result;
	public ResponseSuccess() {
		super();
		this.setSuccess(true);
	}
	/*public ResponseSuccess(Object result) {
		this();
		this.setResult(result);
	}*/
    public ResponseSuccess(String message, ResponseSuccessCode responseSuccessCode) {
    	this(message, responseSuccessCode, null, null);
    }
    
    public ResponseSuccess(String message, ResponseSuccessCode responseSuccessCode, Object result) {
    	this(message, responseSuccessCode, result, null);
    }

    public ResponseSuccess(String message, ResponseSuccessCode responseSuccessCode, Object result, Object[] txts) {
    	this();
    	this.setMsg(message);
        this.code = responseSuccessCode.code();
        Assert.notNull(this.code, "msgcode must be set!");
        this.codemsg = LocalI18NMessageSource.getInstance().getMessage(responseSuccessCode.i18n(),txts);
        this.result = result;
    }
	
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}

	public static ResponseSuccess embed(Object result){
		ResponseSuccess re = new ResponseSuccess(ResponseSuccess.SUCCESS.getMsg(),
				ResponseSuccessCode.COMMON_BUSINESS_SUCCESS,result);
		return re;
	}
	
	public static ResponseSuccess embed(ResponseSuccessCode code){
		ResponseSuccess re = new ResponseSuccess(ResponseSuccess.SUCCESS.getMsg(),code);
		return re;
	}
	
	public static ResponseSuccess embed(ResponseSuccessCode code, Object result){
		ResponseSuccess re = new ResponseSuccess(ResponseSuccess.SUCCESS.getMsg(),code, result);
		return re;
	}
	public static ResponseSuccess embed(ResponseSuccessCode code, Object result, String[] txts){
		ResponseSuccess re = new ResponseSuccess(ResponseSuccess.SUCCESS.getMsg(),code, result, txts);
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
		System.out.println(JsonHelper.getJSONString(new ResponseSuccess()));
    }
}
