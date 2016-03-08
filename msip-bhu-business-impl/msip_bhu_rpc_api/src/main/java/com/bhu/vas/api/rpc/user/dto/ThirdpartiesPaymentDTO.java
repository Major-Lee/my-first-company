package com.bhu.vas.api.rpc.user.dto;

import java.io.Serializable;

import com.bhu.vas.api.helper.BusinessEnumType.ThirdpartiesPaymentType;

@SuppressWarnings("serial")
public class ThirdpartiesPaymentDTO implements Serializable{
	private String type;
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
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public static ThirdpartiesPaymentDTO build(ThirdpartiesPaymentType mode,String id,String name){
		ThirdpartiesPaymentDTO dto = new ThirdpartiesPaymentDTO();
		dto.setType(mode.getType());
		dto.setId(id);
		dto.setName(name);
		return dto;
	}
	
	public String toString(){
		return String.format("ThirdpartiesPayment type[%s] id[%s] name[%s]", type,id,name);
	}
}
