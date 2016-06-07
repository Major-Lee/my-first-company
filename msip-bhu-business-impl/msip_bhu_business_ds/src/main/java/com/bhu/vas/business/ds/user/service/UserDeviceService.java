//package com.bhu.vas.business.ds.user.service;
//
//import com.bhu.vas.api.rpc.user.model.UserDevice;
//import com.bhu.vas.api.rpc.user.model.pk.UserDevicePK;
//import com.bhu.vas.business.ds.user.dao.UserDeviceDao;
//import com.smartwork.msip.cores.orm.service.EntityService;
//import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
//
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.annotation.Resource;
//
//import java.util.List;
//
///**
// * Created by bluesand on 15/4/10.
// */
//@Service
//@Transactional("coreTransactionManager")
//public class UserDeviceService extends EntityService<UserDevicePK, UserDevice, UserDeviceDao> {
//    @Resource
//    @Override
//    public void setEntityDao(UserDeviceDao entityDao) {
//        super.setEntityDao(entityDao);
//    }
//
//    public List<UserDevice> fetchBindDevicesWithLimit(int uid, int limit) {
//        ModelCriteria mc = new ModelCriteria();
//        mc.createCriteria().andColumnEqualTo("uid", uid);
//        mc.setPageNumber(1);
//        mc.setPageSize(limit);
//        return findModelByModelCriteria(mc);
//    }
//
//    public List<UserDevice> fetchBindDevicesUsers(String mac) {
//        ModelCriteria mc = new ModelCriteria();
//        mc.createCriteria().andColumnEqualTo("mac", mac);
//        return findModelByModelCriteria(mc);
//    }
//
//    public Integer fetchBindUid(String mac){
//        ModelCriteria mc = new ModelCriteria();
//        mc.createCriteria().andColumnEqualTo("mac", mac);
//        List<UserDevicePK> pks = super.findIdsByModelCriteria(mc);
//        if(pks != null && !pks.isEmpty()){
//        	return pks.get(0).getUid();
//        }
//        return null;
//    }
//    
//    public UserDevice fetchBindByMac(String mac){
//        ModelCriteria mc = new ModelCriteria();
//        mc.createCriteria().andColumnEqualTo("mac", mac);
//        List<UserDevice> models = super.findModelByModelCriteria(mc);
//        if(models != null && !models.isEmpty()){
//        	return models.get(0);
//        }
//        return null;
//    }
//    /**
//     * 清除用户所有的绑定设备
//     * @param uid
//     * @author Edmond
//     */
//    public void clearBindedDevices(int uid){
//    	ModelCriteria mc = new ModelCriteria();
//        mc.createCriteria().andColumnEqualTo("uid", uid);
//        this.deleteByCommonCriteria(mc);
//    }
//    
//    /**
//     * 清除设备mac的绑定信息
//     * @param mac
//     */
//    public void clearDeviceBinded(String mac){
//    	ModelCriteria mc = new ModelCriteria();
//        mc.createCriteria().andColumnEqualTo("mac", mac);
//        this.deleteByCommonCriteria(mc);
//    }
//
//
//    public int countBindDevices(int uid) {
//        ModelCriteria mc = new ModelCriteria();
//        mc.createCriteria().andColumnEqualTo("uid", uid);
//        return countByModelCriteria(mc);
//    }
//}
