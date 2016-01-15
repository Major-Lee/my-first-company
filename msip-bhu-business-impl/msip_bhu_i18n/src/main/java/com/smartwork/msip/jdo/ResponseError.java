package com.smartwork.msip.jdo;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.smartwork.msip.cores.i18n.LocalI18NMessageSource;
import com.smartwork.msip.exception.BusinessI18nCodeException;


public class ResponseError extends Response{
	public static final Response ERROR = new Response(false, "操作失败");
	public static final Response BUSINESS_ERROR = new ResponseError("操作失败",ResponseErrorCode.COMMON_BUSINESS_ERROR);
	public static final Response SYSTEM_ERROR = new ResponseError("操作失败",ResponseErrorCode.COMMON_SYSTEM_UNKOWN_ERROR);
	private String code;
    private String codemsg;
    @JsonInclude(Include.NON_NULL)
    private String stack;
    @JsonInclude(Include.NON_NULL)
    private Object result;
    public ResponseError() {
    	this.setSuccess(false);
    }

    public ResponseError(String message, ResponseErrorCode responseErrorCode) {
    	this();
    	this.setMsg(message);
        this.code = responseErrorCode.code();
        Assert.notNull(this.code, "msgcode must be set!");
        this.codemsg = LocalI18NMessageSource.getInstance().getMessage(responseErrorCode.i18n());
    }

    public ResponseError(String message, ResponseErrorCode responseErrorCode, Object[] txts) {
    	this();
    	this.setMsg(message);
        this.code = responseErrorCode.code();
        Assert.notNull(this.code, "msgcode must be set!");
        this.codemsg = LocalI18NMessageSource.getInstance().getMessage(responseErrorCode.i18n(),txts);
    }
    /*public ResponseError(String message, ResponseErrorCode code) {
        this(message, code.code());
    }*/

    public ResponseError(Throwable t, ResponseErrorCode responseErrorCode, boolean debug) {
        while (t.getCause() != null) {
            t = t.getCause();
        }
        this.setSuccess(false);
    	this.setMsg(t.getMessage());
        this.code = responseErrorCode.code();
        Assert.notNull(this.code, "msgcode must be set!");
        this.codemsg = LocalI18NMessageSource.getInstance().getMessage(responseErrorCode.i18n());

        if (debug){
                StringWriter s = new StringWriter();
                PrintWriter w = new PrintWriter(s);
                t.printStackTrace(w);
                w.flush();
                this.stack = s.toString();
        }
    }
    
    public ResponseError(String message, ResponseErrorCode responseErrorCode, Object result) {
    	this(message, responseErrorCode, result, null);
    }

    public ResponseError(String message, ResponseErrorCode responseErrorCode, Object result, Object[] txts) {
    	this();
    	this.setMsg(message);
        this.code = responseErrorCode.code();
        Assert.notNull(this.code, "msgcode must be set!");
        this.codemsg = LocalI18NMessageSource.getInstance().getMessage(responseErrorCode.i18n(),txts);
        this.result = result;
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

	public String getStack() {
		return stack;
	}
	public void setStack(String stack) {
		this.stack = stack;
	}
	
	/*public static ResponseError embedSystemError(){
		ResponseError re = new ResponseError(Response.ERROR.getMsg(),ResponseErrorCode.COMMON_SYSTEM_UNKOWN_ERROR);
		return re;
	}*/
	
	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public static ResponseError embed(String msg,ResponseErrorCode code){
		ResponseError re = new ResponseError(msg,code);
		return re;
	}
	
	public static ResponseError embed(ResponseErrorCode code){
		ResponseError re = new ResponseError(ResponseError.ERROR.getMsg(),code);
		return re;
	}
	public static ResponseError embed(ResponseErrorCode code,String[] txts){
		ResponseError re = new ResponseError(ResponseError.ERROR.getMsg(),code,txts);
		return re;
	}
	
	public static ResponseError embed(ResponseErrorCode code, Object result){
		ResponseError re = new ResponseError(ResponseError.ERROR.getMsg(),code, result);
		return re;
	}
	public static ResponseError embed(ResponseErrorCode code, Object result, String[] txts){
		ResponseError re = new ResponseError(ResponseError.ERROR.getMsg(),code, result, txts);
		return re;
	}
	
	public static ResponseError embed(BusinessI18nCodeException ex){
		ResponseError re = new ResponseError(ResponseError.ERROR.getMsg(),ex.getErrorCode(),ex.getPayload());
		return re;
	}
	
	public static ResponseError embed(IResponseDTO res){
		ResponseError re = new ResponseError(ResponseError.ERROR.getMsg(),res.getErrorCode(),res.getErrorCodeAttach());
		return re;
	}
	public static ResponseError embed(IResponseDTO res, Object result){
		ResponseError re = new ResponseError(ResponseError.ERROR.getMsg(),res.getErrorCode(), result, res.getErrorCodeAttach());
		return re;
	}
}
