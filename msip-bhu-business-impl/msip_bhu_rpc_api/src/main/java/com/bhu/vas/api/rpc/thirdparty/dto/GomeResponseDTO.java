package com.bhu.vas.api.rpc.thirdparty.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@SuppressWarnings("serial")
public class GomeResponseDTO implements Serializable {

	@JsonInclude(Include.NON_NULL)
	private Integer code;
	@JsonInclude(Include.NON_NULL)
	private String desc;
	
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
}
