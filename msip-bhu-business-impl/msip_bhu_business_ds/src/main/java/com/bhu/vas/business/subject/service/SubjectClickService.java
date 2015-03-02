package com.bhu.vas.business.subject.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.subject.model.SubjectClick;
import com.bhu.vas.business.subject.dao.SubjectClickDao;
import com.smartwork.msip.business.abstractmsd.click.multifieldclick.service.AbstractPKMultiFieldClickService;

@Service
@Transactional("coreTransactionManager")
public class SubjectClickService extends AbstractPKMultiFieldClickService<Integer,SubjectClick, SubjectClickDao>{
	@Resource
	@Override
	public void setEntityDao(SubjectClickDao subjectClickDao) {
		super.setEntityDao(subjectClickDao);
	}

	@Override
	protected SubjectClick createClick(Integer id) {
		return new SubjectClick(id);
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
