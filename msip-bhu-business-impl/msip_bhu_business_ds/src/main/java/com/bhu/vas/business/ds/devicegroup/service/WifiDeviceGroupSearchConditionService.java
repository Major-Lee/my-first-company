package com.bhu.vas.business.ds.devicegroup.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.devicegroup.model.WifiDeviceGroupSearchCondition;
import com.bhu.vas.business.ds.devicegroup.dao.WifiDeviceGroupSearchConditionDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

/**
 * 
 * @author Edmond
 *
 */
@Service
@Transactional("coreTransactionManager")
public class WifiDeviceGroupSearchConditionService extends AbstractCoreService<Long, WifiDeviceGroupSearchCondition, WifiDeviceGroupSearchConditionDao>{

    @Resource
    @Override
    public void setEntityDao(WifiDeviceGroupSearchConditionDao entityDao) {
        super.setEntityDao(entityDao);
    }

}
