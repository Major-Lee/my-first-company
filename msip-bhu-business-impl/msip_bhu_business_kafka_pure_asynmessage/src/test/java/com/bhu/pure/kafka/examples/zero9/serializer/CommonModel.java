package com.bhu.pure.kafka.examples.zero9.serializer;

import com.smartwork.msip.cores.helper.JsonHelper;

public class CommonModel {
	private String p;

	public String getP() {
		return p;
	}

	public void setP(String payload) {
		this.p = payload;
	}
	
	public static CommonModel build(String payload){
		CommonModel model = new CommonModel();
		model.setP(payload);
		return model;
	}
	public static CommonModel invoke(Object object){
		CommonModel model = new CommonModel();
		model.setP(JsonHelper.getJSONString(object));
		return model;
	}
	
	public <T> T fetch(Class<T> classz){
		return JsonHelper.getDTO(getP(), classz);
	}
}
