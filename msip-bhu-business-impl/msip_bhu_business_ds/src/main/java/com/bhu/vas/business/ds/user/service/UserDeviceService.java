package com.bhu.vas.business.ds.user.service;

import com.bhu.vas.api.rpc.user.model.UserDevice;
import com.bhu.vas.api.rpc.user.model.pk.UserDevicePK;
import com.bhu.vas.business.ds.user.dao.UserDeviceDao;
import com.smartwork.msip.cores.orm.service.EntityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by bluesand on 15/4/10.
 */
@Service
@Transactional("coreTransactionManager")
public class UserDeviceService extends EntityService<UserDevicePK, UserDevice, UserDeviceDao> {
    @Resource
    @Override
    public void setEntityDao(UserDeviceDao entityDao) {
        super.setEntityDao(entityDao);
    }
}
