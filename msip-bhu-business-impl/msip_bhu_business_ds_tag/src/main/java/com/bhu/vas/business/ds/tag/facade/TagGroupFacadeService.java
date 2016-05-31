package com.bhu.vas.business.ds.tag.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.tag.model.TagGroup;
import com.bhu.vas.api.rpc.tag.model.TagGroupRelation;
import com.bhu.vas.business.ds.tag.service.TagGroupRelationService;
import com.bhu.vas.business.ds.tag.service.TagGroupService;

@Service
public class TagGroupFacadeService {
	private final Logger logger = LoggerFactory.getLogger(TagGroupFacadeService.class);
	
	@Resource
	private TagGroupService tagGroupService;
	
	@Resource
	private TagGroupRelationService tagGroupRelationService;
	
	public List<String> findGroupNamesByMacs(List<String> macs){
		if(macs == null || macs.isEmpty()) return Collections.emptyList();
		
		List<TagGroupRelation> tagGroupRelations = tagGroupRelationService.findByIds(macs, true, true);
		List<Integer> gids = new ArrayList<Integer>();
		for(TagGroupRelation tagGroupRelation : tagGroupRelations){
			if(tagGroupRelation != null){
				gids.add(tagGroupRelation.getGid());
			}else{
				gids.add(-1);
			}
		}
		
		List<TagGroup> tagGroups = tagGroupService.findByIds(gids, true, true);
		List<String> tagGroupNames = new ArrayList<String>();
		for(TagGroup tagGroup : tagGroups){
			if(tagGroup != null){
				tagGroupNames.add(tagGroup.getName());
			}else{
				tagGroupNames.add(TagGroup.DefaultGroupName);
			}
		}
		
		return tagGroupNames;
	}
}
