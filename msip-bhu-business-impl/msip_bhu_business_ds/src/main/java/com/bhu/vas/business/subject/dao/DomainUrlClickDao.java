package com.bhu.vas.business.subject.dao;

import org.springframework.stereotype.Repository;

import com.bhu.vas.api.subject.model.DomainUrlClick;
import com.smartwork.msip.business.abstractmsd.click.multifieldclick.dao.AbstractPKMultiFieldClickDao;

@Repository
public class DomainUrlClickDao extends AbstractPKMultiFieldClickDao<String,DomainUrlClick>{
	/*public UrlViewClickDao() {
		super(UrlViewClick.class);
	}*/
}
