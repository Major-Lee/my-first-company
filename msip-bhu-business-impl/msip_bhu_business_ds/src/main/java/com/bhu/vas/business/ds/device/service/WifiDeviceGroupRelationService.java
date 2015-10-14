package com.bhu.vas.business.ds.device.service;

import java.util.ArrayList;
import java.util.List;

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


    /**
     * 获取设备所在的所有设备组
     * @param mac
     * @return
     */
    public List<Long> getDeviceGroupIds(String mac) {
        List<WifiDeviceGroupRelationPK> pks = this.findIds("mac", mac);

        List<Long> groupIds = new ArrayList<Long>();
        for (WifiDeviceGroupRelationPK pk : pks) {
            groupIds.add(pk.getGid());
        }
        return groupIds;
    }

    /**
     * 获取群组下所有设备mac地址
     *
     * @param gid
     * @return
     */
    public List<String> getDeviceIdsByGroupId(long gid) {
        List<WifiDeviceGroupRelationPK> pks = this.findIds("gid", gid);

        List<String> deviceIds = new ArrayList<>();
        for (WifiDeviceGroupRelationPK pk : pks) {
            deviceIds.add(pk.getMac());
        }
        return deviceIds;
    }

}
