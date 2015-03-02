package com.bhu.vas.business.subject.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.subject.model.UrlViewClick;
import com.bhu.vas.business.subject.dao.UrlViewClickDao;
import com.smartwork.msip.business.abstractmsd.click.multifieldclick.service.AbstractPKMultiFieldClickService;

@Service
@Transactional("coreTransactionManager")
public class UrlViewClickService extends AbstractPKMultiFieldClickService<String,UrlViewClick, UrlViewClickDao>{
	@Resource
	@Override
	public void setEntityDao(UrlViewClickDao urlViewClickDao) {
		super.setEntityDao(urlViewClickDao);
	}

	@Override
	protected UrlViewClick createClick(String id) {
		return new UrlViewClick(id);
	}
}
