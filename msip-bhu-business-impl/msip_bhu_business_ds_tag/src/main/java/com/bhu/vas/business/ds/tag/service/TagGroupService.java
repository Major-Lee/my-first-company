package com.bhu.vas.business.ds.tag.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.devicegroup.model.WifiDeviceGroup;
import com.bhu.vas.api.rpc.sequence.helper.IRedisSequenceGenable;
import com.bhu.vas.api.rpc.tag.model.TagGroup;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.SequenceService;
import com.bhu.vas.business.ds.tag.dao.TagGroupDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractTagService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.criteria.PerfectCriteria.Criteria;
import com.smartwork.msip.localunit.RandomData;

/**
 * 
 * @author xiaowei
 *		by 16/05/10
 */

@Service
@Transactional("tagTransactionManager")
public class TagGroupService extends AbstractTagService<Integer, TagGroup, TagGroupDao>{

    @Resource
    @Override
    public void setEntityDao(TagGroupDao tagGroupDao) {
        super.setEntityDao(tagGroupDao);
    }
    
	@Override
	public TagGroup insert(TagGroup entity) {
		if(entity.getId() == null)
			SequenceService.getInstance().onCreateSequenceKey(entity, false);
		entity.setPath(generateRelativePath(entity));
		entity.setChildren(0);
		return super.insert(entity);
	}

	public String generateRelativePath(TagGroup group){
		if(group == null) return null;
		boolean hasParent = group.getPid() != 0;
		if(!hasParent){
			StringBuilder sb = new StringBuilder();
			sb.append(group.getId()).append('/');
			return sb.toString();
		}else{
			TagGroup parentCate = this.getById(group.getPid());
			StringBuilder sb = new StringBuilder();
			sb.append(parentCate.getPath()).append(group.getId()).append('/');
			return sb.toString();
		}
	}
	
	/**
	 * 此种删除方式不会更新缓存，可能存在问题，但效率相对高
	 * @param path
	 * @param withSelf
	 * @return
	 */
	public boolean removeAllByPath(String path,boolean withSelf){
		this.deleteByCommonCriteria(builderModelCriteriaByPath(path,withSelf));
		return true;
		
	}
	
	private static ModelCriteria builderModelCriteriaByPath(String path,boolean withSelf){
		ModelCriteria mc = new ModelCriteria();
		Criteria createCriteria = mc.createCriteria();
		int rand = RandomData.intNumber(10, 10000);
		createCriteria.andSimpleCaulse(rand+"="+rand);
		if(!withSelf){
			createCriteria.andColumnNotEqualTo("path", path);
		}
		createCriteria.andColumnLike("path", path+"%");
		return mc;
	}
	
}