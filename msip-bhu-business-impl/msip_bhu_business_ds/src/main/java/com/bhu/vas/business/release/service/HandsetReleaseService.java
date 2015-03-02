package com.bhu.vas.business.release.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.release.model.HandsetRelease;
import com.bhu.vas.api.release.model.HandsetReleasePK;
import com.bhu.vas.business.release.dao.HandsetReleaseDao;
import com.smartwork.msip.cores.orm.service.EntityService;

@Service
@Transactional("coreTransactionManager")
public class HandsetReleaseService extends EntityService<HandsetReleasePK,HandsetRelease, HandsetReleaseDao>{
	@Resource
	@Override
	public void setEntityDao(HandsetReleaseDao handsetReleaseDao) {
		super.setEntityDao(handsetReleaseDao);
	}
}
