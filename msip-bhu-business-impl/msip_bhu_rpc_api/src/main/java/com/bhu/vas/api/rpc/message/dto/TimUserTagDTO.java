package com.bhu.vas.api.rpc.message.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("serial")
public class TimUserTagDTO implements java.io.Serializable{
	@JsonProperty("UserTags")
	private List<TimUserTagSubDTO> userTags;

	public List<TimUserTagSubDTO> getUserTags() {
		return userTags;
	}

	public void setUserTags(List<TimUserTagSubDTO> userTags) {
		this.userTags = userTags;
	}

	public static TimUserTagDTO buildTimUserTagDTO(String accounts, String tags) {
		if (StringUtils.isEmpty(accounts) || StringUtils.isEmpty(tags)){
			return null;
		}
		List<TimUserTagSubDTO> uTagList = new ArrayList<TimUserTagSubDTO>();
		String[] accArr = accounts.split(",");
		for (String acc : accArr){
			TimUserTagSubDTO subDto = new TimUserTagSubDTO();
			subDto.setTo_Account(acc);
			subDto.setTags(Arrays.asList(tags.split(",")));
			uTagList.add(subDto);
		}
		TimUserTagDTO utagDto = new TimUserTagDTO();
		utagDto.setUserTags(uTagList);
		return utagDto;
	}
}
