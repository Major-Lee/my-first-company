package com.bhu.vas.business.feedback.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.feedback.model.HandsetFeedback;
import com.bhu.vas.business.feedback.dao.HandsetFeedbackDao;
import com.smartwork.msip.cores.orm.service.EntityService;

@Service
@Transactional("coreTransactionManager")
public class HandsetFeedbackService extends EntityService<Integer,HandsetFeedback, HandsetFeedbackDao>{
	@Resource
	@Override
	public void setEntityDao(HandsetFeedbackDao handsetFeedbackDao) {
		super.setEntityDao(handsetFeedbackDao);
	}
}
