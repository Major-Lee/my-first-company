package com.bhu.vas.business.statistics.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.statistics.model.SubjectStatisticsYearly;
import com.bhu.vas.api.subject.model.SubjectClick;
import com.bhu.vas.api.subject.model.TimeFragmentPK;
import com.bhu.vas.business.statistics.dao.SubjectStatisticsYearlyDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.support.criteria.CommonCriteria;

@Service
@Transactional("coreTransactionManager")
public class SubjectStatisticsYearlyService extends AbstractCoreService<TimeFragmentPK,SubjectStatisticsYearly, SubjectStatisticsYearlyDao>{
	@Resource
	@Override
	public void setEntityDao(SubjectStatisticsYearlyDao subjectStatisticsYearlyDao) {
		super.setEntityDao(subjectStatisticsYearlyDao);
	}
	
	public List<TimeFragmentPK> findIdsOrderByEvaluate(int start, int size){
		CommonCriteria mc = new CommonCriteria();
		mc.createCriteria().andColumnEqualTo("currentid", DateTimeHelper.formatDate(DateTimeHelper.FormatPattern10));
		mc.setStart(start);
		mc.setSize(size);
		mc.setOrderByClause(SubjectClick.Field_Up.
				concat(StringHelper.MINUS_STRING_GAP).concat(SubjectClick.Field_Down).concat(" desc"));
		return super.findIdsByCommonCriteria(mc);
	}
	
	public int count(){
		CommonCriteria mc = new CommonCriteria();
		mc.createCriteria().andColumnEqualTo("currentid", DateTimeHelper.formatDate(DateTimeHelper.FormatPattern10));
		return super.countByCommonCriteria(mc);
	}
}
