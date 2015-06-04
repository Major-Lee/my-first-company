package com.bhu.vas.business.ds.dict.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.dict.model.DictMacPrefix;
import com.bhu.vas.business.ds.dict.dao.DictMacPrefixDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

@Service
@Transactional("coreTransactionManager")
public class DictMacPrefixService extends AbstractCoreService<String,DictMacPrefix, DictMacPrefixDao>{
	@Resource
	@Override
	public void setEntityDao(DictMacPrefixDao dictMacPrefixDao) {
		super.setEntityDao(dictMacPrefixDao);
	}
	
	/*public String generateAncestryRelativePath(MlEtmaTag tag){//,boolean hasParent){
		if(tag == null) return null;
		boolean hasParent = (tag.getPid()!=null && tag.getPid().intValue()!= 0);
		if(!hasParent){
			//return null;
			StringBuilder sb = new StringBuilder();
			sb.append(tag.getId()).append('/');
			return sb.toString();
		}else{
			MlEtmaTag parentTag = this.getById(tag.getPid());
			StringBuilder sb = new StringBuilder();
			sb.append(parentTag.getAncestry()).append(parentTag.getId()).append('/');
			return sb.toString();
		}
	}
	
	@Override
	public MlEtmaTag insert(MlEtmaTag entity) {
		//if(entity.getId() == null)
		//	sequenceService.onCreateSequenceKey(entity, false);
		entity.setAncestry(generateAncestryRelativePath(entity));
		return super.insert(entity);
	}

	public MlEtmaTag update(MlEtmaTag entity,boolean pidChanged) {
		if(pidChanged)
			entity.setAncestry(generateAncestryRelativePath(entity));
		return super.update(entity);
	}*/
}
