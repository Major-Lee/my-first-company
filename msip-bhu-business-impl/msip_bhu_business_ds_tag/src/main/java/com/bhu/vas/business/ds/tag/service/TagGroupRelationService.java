package com.bhu.vas.business.ds.tag.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.tag.model.TagGroupRelation;
import com.bhu.vas.business.ds.tag.dao.TagGroupRelationDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractTagService;

@Service
@Transactional("tagTransactionManager")
public class TagGroupRelationService extends AbstractTagService<String, TagGroupRelation, TagGroupRelationDao>{

    @Resource
    @Override
    public void setEntityDao(TagGroupRelationDao tagGroupRelationDao) {
        super.setEntityDao(tagGroupRelationDao);
    }
	
    public int cleanDeviceGroupRel(String mac){
    	int count = this.deleteById(mac);
    	return count;
    }
    
    public void cleanDeviceGroupRels(List<String> macs){
    	this.deleteByIds(macs);
    }
    
	public String fetchPathWithMac(String mac){
		TagGroupRelation relation = this.getById(mac);
		if(relation != null)
			return relation.getPath2ES();
		return null;
	}
}
