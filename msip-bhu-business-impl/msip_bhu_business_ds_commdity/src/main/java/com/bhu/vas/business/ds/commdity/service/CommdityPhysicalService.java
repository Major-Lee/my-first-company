package com.bhu.vas.business.ds.commdity.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.commdity.model.CommdityPhysical;
import com.bhu.vas.business.ds.commdity.dao.CommdityPhysicalDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCommdityService;
@Service
@Transactional("commdityTransactionManager")
public class CommdityPhysicalService extends AbstractCommdityService<String, CommdityPhysical, CommdityPhysicalDao>{
	@Resource
	public void setEntityDao(CommdityPhysicalDao commdityPhysicalDao) {
		super.setEntityDao(commdityPhysicalDao);
	}
}
