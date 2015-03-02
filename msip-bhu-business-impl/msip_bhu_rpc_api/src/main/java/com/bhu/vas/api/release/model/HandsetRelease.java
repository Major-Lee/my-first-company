package com.bhu.vas.api.release.model;

import java.util.Date;

import com.bhu.vas.api.release.dto.HandsetReleaseDTO;
import com.smartwork.msip.cores.orm.model.extjson.SimpleJsonExtPKModel;


@SuppressWarnings("serial")
public class HandsetRelease extends SimpleJsonExtPKModel<HandsetReleaseDTO, HandsetReleasePK>{

	private Date created_at;
	
	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public String getChannelid(){
		if(this.getId() == null) return null;
		return this.getId().getChannelid();
	}
	
	public void setChannelid(String channelid){
		if(this.getId() == null) this.setId(new HandsetReleasePK());
		this.getId().setChannelid(channelid);
	}
	
	public String getPlatform(){
		if(this.getId() == null) return null;
		return this.getId().getPlatform();
	}
	
	public void setPlatform(String platform){
		if(this.getId() == null) this.setId(new HandsetReleasePK());
		this.getId().setPlatform(platform);
	}
	
	@Override
	protected Class<HandsetReleasePK> getPKClass() {
		return HandsetReleasePK.class;
	}

	@Override
	public Class<HandsetReleaseDTO> getJsonParserModel() {
		return HandsetReleaseDTO.class;
	}
	
	
}
