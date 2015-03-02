package com.bhu.vas.business.subject.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.subject.model.SubjectAbstractClick;
import com.bhu.vas.business.subject.dao.SubjectAbstractClickDao;
import com.smartwork.msip.business.abstractmsd.click.multifieldclick.service.AbstractPKMultiFieldClickService;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.support.criteria.CommonCriteria;

@Service
@Transactional("coreTransactionManager")
public class SubjectAbstractClickService extends AbstractPKMultiFieldClickService<Integer,SubjectAbstractClick, SubjectAbstractClickDao>{
	@Resource
	@Override
	public void setEntityDao(SubjectAbstractClickDao subjectAbstractClickDao) {
		super.setEntityDao(subjectAbstractClickDao);
	}

	@Override
	protected SubjectAbstractClick createClick(Integer id) {
		return new SubjectAbstractClick(id);
	}

	/*@Override
	public String generateKey(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}*/
	/**
	 * 根据subject_id进行查询点击数据 以up-down进行排序
	 * @param subject_id
	 * @param start
	 * @param size
	 * @return
	 */
	public List<SubjectAbstractClick> findBySubjectIdOrderEvaluate(int subject_id, int start, int size){
		CommonCriteria mc = new CommonCriteria();
		mc.createCriteria().andColumnEqualTo("subjectid", subject_id);
		mc.setStart(start);
		mc.setSize(size);
		mc.setOrderByClause(SubjectAbstractClick.Field_Up.
				concat(StringHelper.MINUS_STRING_GAP).concat(SubjectAbstractClick.Field_Down).concat(" desc"));
		return super.findModelByCommonCriteria(mc);
	}
	
}
