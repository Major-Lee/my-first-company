package com.bhu.vas.api.rpc.tag.model;

import java.util.Date;
import java.util.Set;

import com.smartwork.msip.cores.helper.ArrayHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.model.extjson.SetJsonExtStringModel;

@SuppressWarnings("serial")
public class TagDevices extends SetJsonExtStringModel<String> {
	
	/**
	 * 最后操作用户
	 */
	private int last_operator;
	
	
	private Date created_at;
	private Date update_at;
	
	public String getTag2ES(){
		Set<String> set = fetchTags();
		String d_tags = ArrayHelper.toSplitString(set, StringHelper.WHITESPACE_STRING_GAP);
		return d_tags;
	}
	
	public void setLast_operator(int last_operator) {
		this.last_operator = last_operator;
	}
	
	private Set<String> fetchTags(){
		return this.getInnerModels();
	}
	public void addTag(String tag){
		this.putInnerModel(tag);
	}

	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}
	
	@Override
	public void preUpdate() {
		this.update_at = new Date();
		super.preUpdate();
	}
	@Override
	public Class<String> getJsonParserModel() {
		return String.class;
	}
	
	
}
