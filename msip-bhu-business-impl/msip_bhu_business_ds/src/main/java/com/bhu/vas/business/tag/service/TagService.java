package com.bhu.vas.business.tag.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.tag.model.Tag;
import com.bhu.vas.business.tag.dao.TagDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.support.criteria.CommonCriteria;

@Service
@Transactional("coreTransactionManager")
public class TagService extends AbstractCoreService<Integer,Tag, TagDao>{
	@Resource
	@Override
	public void setEntityDao(TagDao tagDao) {
		super.setEntityDao(tagDao);
	}
	
	public void createTag(int uid, String name, int pid){
		if(pid > 0){
			Tag parent_entity = super.getById(pid);
			if(parent_entity != null){
				this.createTag(uid, name, pid, parent_entity.getPath());
			}
		}
	}
	
	public void createTag(int uid, String name, int pid, String parent_category_path){
		Tag entity = new Tag();
		entity.setName(name);
		entity.setUid(uid);
		entity.setPid(pid);
		if(pid > 0){
			StringBuffer path_sb = new StringBuffer();
			if(StringHelper.isNotEmpty(parent_category_path)){
				path_sb.append(parent_category_path);
			}
			path_sb.append(StringHelper.MINUS_STRING_GAP);
			path_sb.append(pid);
			entity.setPath(path_sb.toString());
		}
		super.insert(entity);
	}
	
	public int countByPid(int pid){
		CommonCriteria mc = new CommonCriteria();
		mc.createCriteria().andColumnEqualTo("pid", pid);
		return super.countByCommonCriteria(mc);
	}
	
	public List<Tag> findModelByPid(int pid, int start, int size){
		CommonCriteria mc = new CommonCriteria();
		mc.createCriteria().andColumnEqualTo("pid", pid);
		mc.setStart(start);
		mc.setSize(size);
		return super.findModelByCommonCriteria(mc);
	}
}
