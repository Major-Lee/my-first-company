package com.bhu.vas.business.statistics.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.statistics.model.SubjectStatisticsDaily;
import com.bhu.vas.api.subject.model.TimeFragmentPK;
import com.bhu.vas.business.statistics.dao.SubjectStatisticsDailyDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

@Service
@Transactional("coreTransactionManager")
public class SubjectStatisticsDailyService extends AbstractCoreService<TimeFragmentPK,SubjectStatisticsDaily, SubjectStatisticsDailyDao>{
	@Resource
	@Override
	public void setEntityDao(SubjectStatisticsDailyDao subjectStatisticsDailyDao) {
		super.setEntityDao(subjectStatisticsDailyDao);
	}
	
}
