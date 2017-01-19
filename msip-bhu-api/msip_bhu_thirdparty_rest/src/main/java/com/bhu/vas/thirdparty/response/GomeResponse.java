package com.bhu.vas.thirdparty.response;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.i18n.LocalI18NMessageSource;
import com.smartwork.msip.jdo.IResponseDTO;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;

public class GomeResponse {
	public static final String SUCCESS_DESC = "success";

	private int code;
	private String desc;
	
    @JsonInclude(Include.NON_NULL)
	private String result;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	public static GomeResponse fromSuccessRpcResponse(Object o){
		GomeResponse ret = new GomeResponse();
		ret.setCode(0);
		ret.setDesc(GomeResponse.SUCCESS_DESC);
		if(!(o instanceof Boolean)){
			ret.setResult(JsonHelper.getJSONString(o));
		}
		return ret;
	}

	public static GomeResponse fromFailRpcResponse(IResponseDTO res){
		GomeResponse ret = new GomeResponse();
		ret.setCode(Integer.parseInt(res.getErrorCode().code()));
		ResponseError re = new ResponseError(ResponseError.ERROR.getMsg(),res.getErrorCode(),res.getErrorCodeAttach(), Locale.CHINA);
		ret.setDesc(re.getCodemsg());
		return ret;
	}
	
	public static GomeResponse fromFailErrorCode(ResponseErrorCode code){
		GomeResponse ret = new GomeResponse();
		ret.setCode(Integer.parseInt(code.code()));
		ret.setDesc(LocalI18NMessageSource.getInstance().getMessage(code.i18n(), Locale.CHINA));
		return ret;
	}
	
}
