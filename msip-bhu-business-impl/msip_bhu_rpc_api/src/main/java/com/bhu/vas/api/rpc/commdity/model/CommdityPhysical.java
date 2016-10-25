package com.bhu.vas.api.rpc.commdity.model;

import com.bhu.vas.api.dto.commdity.CommdityPhysicalDTO;
import com.smartwork.msip.cores.orm.model.extjson.DtoJsonExtPKModel;

@SuppressWarnings("serial")
public class CommdityPhysical extends DtoJsonExtPKModel<String,CommdityPhysicalDTO>{

	private String id;
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public Class<CommdityPhysicalDTO> getJsonParserModel() {
		return CommdityPhysicalDTO.class;
	}

	@Override
	protected Class<String> getPKClass() {
		return String.class;
	}
	
	public CommdityPhysicalDTO getInnerModel(){
		CommdityPhysicalDTO dto = super.getInnerModel();
		if(dto == null)
			return null;
		return dto;
	}
}
