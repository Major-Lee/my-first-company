package com.bhu.vas.business.subject.dao;

import org.springframework.stereotype.Repository;

import com.bhu.vas.api.subject.model.UrlViewClick;
import com.smartwork.msip.business.abstractmsd.click.multifieldclick.dao.AbstractPKMultiFieldClickDao;

@Repository
public class UrlViewClickDao extends AbstractPKMultiFieldClickDao<String,UrlViewClick>{
	/*public UrlViewClickDao() {
		super(UrlViewClick.class);
	}*/
}
