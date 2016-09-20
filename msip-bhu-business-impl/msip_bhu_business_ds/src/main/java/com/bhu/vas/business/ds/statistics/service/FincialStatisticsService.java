package com.bhu.vas.business.ds.statistics.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.statistics.model.FincialStatistics;
import com.bhu.vas.business.ds.statistics.dao.FincialStatisticsDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCommdityService;
@Service
@Transactional("coreTransactionManager")
public class FincialStatisticsService extends AbstractCommdityService<String, FincialStatistics, FincialStatisticsDao>{
	@Resource
	@Override
	public void setEntityDao(FincialStatisticsDao fincialStatisticsDao) {
		super.setEntityDao(fincialStatisticsDao);
	}
	
	@Override
	public FincialStatistics insert(FincialStatistics entity) {
		//如果订单id为空 会默认生成订单id 并且扩展占位为零
		FincialStatistics fincialStatistics=super.getById(entity.getId());
		if(fincialStatistics==null){
			return super.insert(entity);
		}else{
			entity.setId(fincialStatistics.getId());
			return super.update(entity);
		}
	}
}
