package com.bhu.vas.business.subject.dao;

import org.springframework.stereotype.Repository;

import com.bhu.vas.api.subject.model.SubjectClick;
import com.smartwork.msip.business.abstractmsd.click.multifieldclick.dao.AbstractPKMultiFieldClickDao;

@Repository
public class SubjectClickDao extends AbstractPKMultiFieldClickDao<Integer,SubjectClick>{
	/*public SubjectClickDao() {
		super(SubjectClick.class);
	}*/
}
