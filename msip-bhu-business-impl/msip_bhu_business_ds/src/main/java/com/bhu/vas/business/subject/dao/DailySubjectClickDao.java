package com.bhu.vas.business.subject.dao;

import org.springframework.stereotype.Repository;

import com.bhu.vas.api.subject.model.DailySubjectClick;
import com.bhu.vas.api.subject.model.TimeFragmentPK;
import com.smartwork.msip.business.abstractmsd.click.multifieldclick.dao.AbstractPKMultiFieldClickDao;

@Repository
public class DailySubjectClickDao extends AbstractPKMultiFieldClickDao<TimeFragmentPK,DailySubjectClick>{
	/*public CurrentDaySubjectClickDao() {
		super(SubjectMClick.class);
	}*/
}
