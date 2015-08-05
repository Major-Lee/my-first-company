package com.bhu.vas.business.ds.device.facade;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;


import com.bhu.vas.api.rpc.devices.model.WifiDeviceGroup;
import com.bhu.vas.business.ds.device.service.WifiDeviceGroupRelationService;
import com.bhu.vas.business.ds.device.service.WifiDeviceGroupService;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import java.util.List;

/**
 * Created by bluesand on 8/4/15.
 *
 * 避免 wifiDeviceGroupService 与 wifiDeviceGroupRelationService 同级service相互调用
 */
@Service
public class WifiDeviceGroupFacadeService {

    /**
     * 定义<=10000的群组都为灰度群组
     */
    private final static Integer GRAY_GROUP_ID_PARENT = 10000;
    private final static Integer GRAY_GROUP_ID_ONE = 9999;
    private final static Integer GRAY_GROUP_ID_TWO = 9998;
    private final static Integer GRAY_GROUP_ID_THREE = 9997;

    @Resource
    WifiDeviceGroupService wifiDeviceGroupService;

    @Resource
    WifiDeviceGroupRelationService wifiDeviceGroupRelationService;

    /**
     * 还需要删除gid及其所有的子节点
     * @param uid
     * @param gids
     */
    public void cleanUpByIds(Integer uid, String gids){
        String[] arrayresids = gids.split(StringHelper.COMMA_STRING_GAP);
        for(String residstr : arrayresids){
            Integer resid = new Integer(residstr);

            if (isInGrayGroup(resid)){
                continue; //灰度群组不删除
            }

            WifiDeviceGroup group = wifiDeviceGroupService.getById(resid);
            if(group != null){
                //int gid = group.getId().intValue();
                int pid = group.getPid();
                wifiDeviceGroupService.removeAllByPath(group.getPath(), true);
                //removeAllByPathStepByStep(group.getPath(),true);
                //判定每个gid的parentid是否为hanchild
                if(pid != 0){
					/*try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}*/
                    WifiDeviceGroup parent_group = wifiDeviceGroupService.getById(pid);
                    parent_group.setChildren(parent_group.getChildren()-1);
                    wifiDeviceGroupService.update(parent_group);
					/*if(parent_group.getChildren() > 1){

					}*/
					/*int count  = countAllByPath(parent_group.getPath(),false);
					System.out.println("~~~~~~~~~~~~count:"+count);
					if(count == 0 && parent_group.getChildren() > 0){
						parent_group.setChildren(0);
						this.update(parent_group);
					}*/

                }else{//pid == 0 本身是根节点，被删除后，无需动作
                    ;
                }

                //删除绑定的设备
                ModelCriteria mc = new ModelCriteria();
                mc.createCriteria().andColumnEqualTo("gid", resid);
                wifiDeviceGroupRelationService.deleteByCommonCriteria(mc);

            }
        }
    }


    /**
     * 是否是灰度测试群组
     *
     * @param gid
     * @return
     */
    public boolean isInGrayGroup(int gid) {
//        if (gid == GRAY_GROUP_ID_PARENT || gid == GRAY_GROUP_ID_ONE || gid == GRAY_GROUP_ID_TWO || gid == GRAY_GROUP_ID_THREE) {
//            return true;
//        }
//        WifiDeviceGroup wifiDeviceGroup = wifiDeviceGroupService.getById(gid);
//        if (wifiDeviceGroup != null) {
//            String path =  wifiDeviceGroup.getPath();
//            String[] pids = path.split("/");
//            for (String pid : pids) {
//                if (String.valueOf(GRAY_GROUP_ID_PARENT).equals(pid) || String.valueOf(GRAY_GROUP_ID_ONE).equals(pid) ||
//                String.valueOf(GRAY_GROUP_ID_TWO).equals(pid) || String.valueOf(GRAY_GROUP_ID_THREE).equals(pid)) {
//                    return true;
//                }
//            }
//        }
//        return false;

        if (gid <= GRAY_GROUP_ID_PARENT) {
            return true;
        }
        WifiDeviceGroup wifiDeviceGroup = wifiDeviceGroupService.getById(gid);
        if (wifiDeviceGroup != null) {
            String path =  wifiDeviceGroup.getPath();
            String[] pids = path.split("/");
            for (String pid : pids) {
                if (Integer.parseInt(pid) <= GRAY_GROUP_ID_PARENT) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否设备在灰度测试组里面
     *
     * @param mac
     * @return
     */
    public boolean isDeviceInGrayGroup(String mac) {

        List<Integer> groupIds = wifiDeviceGroupRelationService.getDeviceGroupIds(mac);

//        return groupIds.contains(GRAY_GROUP_ID_PARENT) || groupIds.contains(GRAY_GROUP_ID_ONE)  ||
//                groupIds.contains(GRAY_GROUP_ID_TWO) || groupIds.contains(GRAY_GROUP_ID_THREE);

        for (Integer gid : groupIds) {
            if (gid <= GRAY_GROUP_ID_PARENT) {
                return true;
            }
        }
        return false;



    }
}
