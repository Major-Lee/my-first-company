package com.bhu.vas.business.ds.tag.service;

import javax.annotation.Resource;

import com.bhu.vas.api.rpc.tag.model.TagGroupSortMessage;
import com.bhu.vas.business.ds.tag.dao.TagGroupSortMessageDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractTagService;

public class TagGroupSortMessageService extends AbstractTagService<Integer, TagGroupSortMessage, TagGroupSortMessageDao> {
	
    @Resource
    public void setEntityDao(TagGroupSortMessageDao tagGroupSortMessage) {
        super.setEntityDao(tagGroupSortMessage);
    }
}
