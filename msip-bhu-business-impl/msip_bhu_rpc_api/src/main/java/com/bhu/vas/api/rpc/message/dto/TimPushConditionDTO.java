package com.bhu.vas.api.rpc.message.dto;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("serial")
public class TimPushConditionDTO implements java.io.Serializable{
	@JsonProperty("TagsOr")
	@JsonInclude(Include.NON_NULL)
	private List<String> tagsOr;
	
	@JsonProperty("TagsAnd")
	@JsonInclude(Include.NON_NULL)
	private List<String> tagsAnd;

	public List<String> getTagsOr() {
		return tagsOr;
	}

	public void setTagsOr(List<String> tagsOr) {
		this.tagsOr = tagsOr;
	}

	public List<String> getTagsAnd() {
		return tagsAnd;
	}

	public void setTagsAnd(List<String> tagsAnd) {
		this.tagsAnd = tagsAnd;
	}
	
	public static TimPushConditionDTO builder(String tags){
		if (StringUtils.isEmpty(tags))
			return null;
		TimPushConditionDTO dto = new TimPushConditionDTO();
		dto.setTagsOr(Arrays.asList(tags.split(",")));
		return dto;
	}
	
}
