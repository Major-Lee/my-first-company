package com.bhu.vas.api.rpc.user.dto;

import java.io.Serializable;

import com.bhu.vas.api.helper.BusinessEnumType.ThirdpartiesPaymentMode;

@SuppressWarnings("serial")
public class ThirdpartiesPaymentDTO implements Serializable{
	private String mode;
	private String id;
	private String name;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public static ThirdpartiesPaymentDTO build(ThirdpartiesPaymentMode mode,String id,String name){
		ThirdpartiesPaymentDTO dto = new ThirdpartiesPaymentDTO();
		dto.setMode(mode.getMode());
		dto.setId(id);
		dto.setName(name);
		return dto;
	}
	
	public String toString(){
		return String.format("ThirdpartiesPayment mode[%s] id[%s] name[%s]", mode,id,name);
	}
}
