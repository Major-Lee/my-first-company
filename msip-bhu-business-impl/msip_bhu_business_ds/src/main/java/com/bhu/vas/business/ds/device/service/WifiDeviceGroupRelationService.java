package com.bhu.vas.business.ds.device.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.devices.model.WifiDeviceGroupRelation;
import com.bhu.vas.api.rpc.devices.model.pk.WifiDeviceGroupRelationPK;
import com.bhu.vas.business.ds.device.dao.WifiDeviceGroupRelationDao;
import com.smartwork.msip.cores.orm.service.EntityService;

/**
 * Created by bluesand on 7/16/15.
 */
@Service
@Transactional("coreTransactionManager")
public class WifiDeviceGroupRelationService extends EntityService<WifiDeviceGroupRelationPK, WifiDeviceGroupRelation, WifiDeviceGroupRelationDao>{

    @Resource
    @Override
    public void setEntityDao(WifiDeviceGroupRelationDao entityDao) {
        super.setEntityDao(entityDao);
    }



}
