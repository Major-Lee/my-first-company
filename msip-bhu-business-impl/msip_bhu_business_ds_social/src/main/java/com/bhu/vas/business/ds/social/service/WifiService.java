package com.bhu.vas.business.ds.social.service;

import com.bhu.vas.api.rpc.social.model.Wifi;
import com.bhu.vas.business.ds.social.dao.WifiDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractSocialService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by bluesand on 3/7/16.
 */
@Service
@Transactional("socialTransactionManager")
public class WifiService extends AbstractSocialService<String, Wifi, WifiDao> {

    @Resource
    @Override
    public void setEntityDao(WifiDao entityDao) {
        super.setEntityDao(entityDao);
    }
}
