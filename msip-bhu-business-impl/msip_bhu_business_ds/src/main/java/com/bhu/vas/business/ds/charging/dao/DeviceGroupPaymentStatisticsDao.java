package com.bhu.vas.business.ds.charging.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.bhu.vas.api.rpc.charging.model.DeviceGroupPaymentStatistics;
import com.smartwork.msip.business.abstractmsd.dao.AbstractCoreDao;

@Repository
public class DeviceGroupPaymentStatisticsDao extends AbstractCoreDao<String, DeviceGroupPaymentStatistics>{

	public List<DeviceGroupPaymentStatistics> rankingList() {
		return super.getSqlSessionMasterTemplate().selectList(DeviceGroupPaymentStatistics.class.getName()+".rankingList");
	}
}
