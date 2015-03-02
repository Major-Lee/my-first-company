package com.bhu.vas.business.subject.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.subject.model.DomainUrlClick;
import com.bhu.vas.business.subject.dao.DomainUrlClickDao;
import com.smartwork.msip.business.abstractmsd.click.multifieldclick.service.AbstractPKMultiFieldClickService;
import com.smartwork.msip.cores.orm.support.criteria.CommonCriteria;

@Service
@Transactional("coreTransactionManager")
public class DomainUrlClickService extends AbstractPKMultiFieldClickService<String,DomainUrlClick, DomainUrlClickDao>{
	@Resource
	@Override
	public void setEntityDao(DomainUrlClickDao domainUrlClickDao) {
		super.setEntityDao(domainUrlClickDao);
	}

	@Override
	protected DomainUrlClick createClick(String id) {
		return new DomainUrlClick(id);
	}
	
	@Override
	public int entityExpiredSec() {
		return 60;
	}
	
	@Override
	public int getPersistCountDelta() {
		return 1;
	}
	
	public List<DomainUrlClick> findOrderByShare(int start, int size){
		CommonCriteria mc = new CommonCriteria();
		mc.setStart(start);
		mc.setSize(size);
		mc.setOrderByClause(DomainUrlClick.Field_Share.concat(" desc"));
		return super.findModelByCommonCriteria(mc);
	}
	
	public int count(){
		CommonCriteria mc = new CommonCriteria();
		return super.countByCommonCriteria(mc);
	}
}
