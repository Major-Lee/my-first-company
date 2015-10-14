package com.bhu.vas.business.device;

import com.bhu.vas.api.rpc.devices.model.WifiDeviceGroupRelation;
import com.bhu.vas.api.rpc.devices.model.pk.WifiDeviceGroupRelationPK;
import com.bhu.vas.business.ds.device.service.WifiDeviceGroupRelationService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.localunit.BaseTest;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created by bluesand on 7/16/15.
 */
public class WifiDeviceGroupRelationTest extends BaseTest {

    @Resource
    WifiDeviceGroupRelationService wifiDeviceGroupRelationService;


    //@Test
    public void insert() {
        WifiDeviceGroupRelation wifiDeviceGroupRelation = new WifiDeviceGroupRelation();
        wifiDeviceGroupRelation.setId(new WifiDeviceGroupRelationPK(1, "84:82:f4:19:01:10"));
        wifiDeviceGroupRelation.setCreated_at(new Date());
        wifiDeviceGroupRelationService.insert(wifiDeviceGroupRelation);
    }

//    @Test
    public void delete() {
        wifiDeviceGroupRelationService.deleteById(new WifiDeviceGroupRelationPK(1, "62:68:75:f1:10:80"));
    }

    @Test
    public void getList() {
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andColumnEqualTo("gid", 1);
        List<WifiDeviceGroupRelation> list = wifiDeviceGroupRelationService.findModelByModelCriteria(mc);
        System.out.println(list);
    }

    @Test
    public void getDeviceGroupIds() {
        System.out.println(wifiDeviceGroupRelationService.getDeviceGroupIds("62:68:75:00:10:80"));
    }
}
