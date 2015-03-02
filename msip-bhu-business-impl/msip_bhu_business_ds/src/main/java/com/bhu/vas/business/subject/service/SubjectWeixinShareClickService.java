package com.bhu.vas.business.subject.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.subject.model.SubjectWeixinShareClick;
import com.bhu.vas.business.subject.dao.SubjectWeixinShareClickDao;
import com.smartwork.msip.business.abstractmsd.click.multifieldclick.service.AbstractPKMultiFieldClickService;
import com.smartwork.msip.cores.orm.support.criteria.CommonCriteria;

@Service
@Transactional("coreTransactionManager")
public class SubjectWeixinShareClickService extends AbstractPKMultiFieldClickService<String, SubjectWeixinShareClick, SubjectWeixinShareClickDao>{
	@Resource
	@Override
	public void setEntityDao(SubjectWeixinShareClickDao subjectWeixinShareClickDao) {
		super.setEntityDao(subjectWeixinShareClickDao);
	}

	@Override
	protected SubjectWeixinShareClick createClick(String id) {
		return new SubjectWeixinShareClick(id);
	}

	public static final String OrderByCreateDesc = "created_at desc";
	
	
	public List<SubjectWeixinShareClick> findModelOrderByCreatedAtDesc(int start, int size){
		CommonCriteria mc = new CommonCriteria();
		mc.setStart(start);
		mc.setSize(size);
		mc.setOrderByClause(OrderByCreateDesc);
		return super.findModelByCommonCriteria(mc);
	}
	
	public int count(){
		return super.countByCommonCriteria(new CommonCriteria());
	}
	
	@Override
	public int entityExpiredSec() {
		return 60;
	}
	@Override
	public int getPersistCountDelta() {
		return 1;
	}
}
