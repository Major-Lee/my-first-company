package com.bhu.vas.business.subject.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.subject.model.Subject;
import com.bhu.vas.business.subject.dao.SubjectDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
import com.smartwork.msip.cores.helper.ArithHelper;
import com.smartwork.msip.cores.orm.support.criteria.CommonCriteria;

@Service
@Transactional("coreTransactionManager")
public class SubjectService extends AbstractCoreService<Integer,Subject, SubjectDao>{
	/*@Resource
	private SequenceService sequenceService;*/
	
	@Resource
	@Override
	public void setEntityDao(SubjectDao subjectDao) {
		super.setEntityDao(subjectDao);
	}
	
	/*@Override
	public Subject insert(Subject entity) {
		if(entity.getId() == null)
			sequenceService.onCreateSequenceKey(entity, false);
		if(StringUtils.isEmpty(entity.getPermalink()))
			entity.setPermalink(String.valueOf(entity.getId()));
		return super.insert(entity);
	}*/
	
	public Subject getByUrlMD5(String urlMD5){
		CommonCriteria mc = new CommonCriteria();
		mc.createCriteria().andColumnEqualTo("url_md5", urlMD5);
		List<Subject> result = super.findModelByCommonCriteria(mc);
		if(!result.isEmpty()){
			return result.get(0);
		}
		return null;
	}
	
	public static final String OrderByCreateDesc = "created_at desc";
	public static final String OrderByEstimateDesc = "estimate desc";
	
//	public List<Subject> getOrderByCreatedAtDesc(int start, int size){
//		CommonCriteria mc = new CommonCriteria();
//		mc.createCriteria().andColumnEqualTo("visiable", true);
//		mc.setOrderByClause("created_at desc");
//		mc.setStart(start);
//		mc.setSize(size);
//		return super.findModelByCommonCriteria(mc);
//	}
	
	public List<Subject> findModelOrderByString(String orderby, int visible_state, int start, int size){
		CommonCriteria mc = new CommonCriteria();
		mc.createCriteria().andColumnEqualTo("visible_state", visible_state);
		mc.setOrderByClause(orderby);
		mc.setStart(start);
		mc.setSize(size);
		return super.findModelByCommonCriteria(mc);
	}
	
	public List<Subject> findModelByDomain(String domain, int start, int size){
		CommonCriteria mc = new CommonCriteria();
		mc.createCriteria()
			.andColumnEqualTo("visible_state", Subject.VisibleState_Normal)
			.andColumnEqualTo("domain", domain);
		mc.setOrderByClause(OrderByCreateDesc);
		mc.setStart(start);
		mc.setSize(size);
		return super.findModelByCommonCriteria(mc);
	}
	
	public List<Integer> findIdsOrderByString(String orderby, int visible_state, int start, int size){
		CommonCriteria mc = new CommonCriteria();
		mc.createCriteria().andColumnEqualTo("visible_state", visible_state);
		mc.setOrderByClause(orderby);
		mc.setStart(start);
		mc.setSize(size);
		return super.findIdsByCommonCriteria(mc);
	}
	
	public int count(int visible_state){
		CommonCriteria mc = new CommonCriteria();
		mc.createCriteria().andColumnEqualTo("visible_state", visible_state);
		return super.countByCommonCriteria(mc);
	}
	
	public int count(String domain){
		CommonCriteria mc = new CommonCriteria();
		mc.createCriteria().andColumnEqualTo("domain", domain);
		return super.countByCommonCriteria(mc);
	}
	
	public String arithEstimate(Integer subject_id, String new_estimate){
		Subject entity = super.getById(subject_id);
		if(entity == null){
			return null;
		}
		return arithEstimate(entity, new_estimate);
	}
	
	public String arithEstimate(Subject entity, String new_estimate){
		if(entity == null) return null;
		
		double arith_estimate = ArithHelper.div((ArithHelper.add(entity.getEstimate(), Double.parseDouble(new_estimate))), 2, 2);
		entity.setEstimate(arith_estimate);
		super.update(entity);
		return String.valueOf(arith_estimate);
	}
}
