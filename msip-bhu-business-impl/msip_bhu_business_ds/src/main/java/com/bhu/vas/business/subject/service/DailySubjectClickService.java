package com.bhu.vas.business.subject.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.subject.model.DailySubjectClick;
import com.bhu.vas.api.subject.model.TimeFragmentPK;
import com.bhu.vas.business.subject.dao.DailySubjectClickDao;
import com.smartwork.msip.business.abstractmsd.click.multifieldclick.service.AbstractPKMultiFieldClickService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.support.criteria.CommonCriteria;

@Service
@Transactional("coreTransactionManager")
public class DailySubjectClickService extends AbstractPKMultiFieldClickService<TimeFragmentPK,DailySubjectClick, DailySubjectClickDao>{
	@Resource
	@Override
	public void setEntityDao(DailySubjectClickDao dailySubjectClickDao) {
		super.setEntityDao(dailySubjectClickDao);
	}

	@Override
	protected DailySubjectClick createClick(TimeFragmentPK id) {
		return new DailySubjectClick(id);
	}

	@Override
	public int entityExpiredSec() {
		return 60;
	}
	
	public List<TimeFragmentPK> findIdsOrderByEvaluate(Date date, int start, int size){
		String currentid = generateDailyFormatId(date);
		return this.findIdsOrderByEvaluate(currentid, start, size);
	}
	
	public List<TimeFragmentPK> findIdsOrderByEvaluate(int start, int size){
		String currentid = generateDailyFormatId(new Date());
		return this.findIdsOrderByEvaluate(currentid, start, size);
	}
	
	public List<TimeFragmentPK> findIdsOrderByEvaluate(String currentid, int start, int size){
		CommonCriteria mc = new CommonCriteria();
		mc.createCriteria().andColumnEqualTo("currentid", currentid);
		mc.setStart(start);
		mc.setSize(size);
		mc.setOrderByClause(DailySubjectClick.Field_Up.
				concat(StringHelper.MINUS_STRING_GAP).concat(DailySubjectClick.Field_Down).concat(" desc"));
		return super.findIdsByCommonCriteria(mc);
	}
	
	public int count(){
		CommonCriteria mc = new CommonCriteria();
		mc.createCriteria().andColumnEqualTo("currentid", generateDailyFormatId(new Date()));
		return super.countByCommonCriteria(mc);
	}
	
	public String generateDailyFormatId(Date date){
		return DateTimeHelper.formatDate(date, DateTimeHelper.FormatPattern5);
	}
	
	@Override
	public int getPersistCountDelta() {
		return 1;
	}
}
