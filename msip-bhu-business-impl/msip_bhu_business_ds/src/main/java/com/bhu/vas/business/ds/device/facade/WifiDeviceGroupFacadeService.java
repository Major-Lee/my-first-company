package com.bhu.vas.business.ds.device.facade;

import com.bhu.vas.api.rpc.devices.model.WifiDeviceGroup;
import com.bhu.vas.business.ds.device.service.WifiDeviceGroupRelationService;
import com.bhu.vas.business.ds.device.service.WifiDeviceGroupService;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by bluesand on 8/4/15.
 *
 * 避免 wifiDeviceGroupService 与 wifiDeviceGroupRelationService 同级service相互调用
 */
@Service
public class WifiDeviceGroupFacadeService {

    @Resource
    WifiDeviceGroupService wifiDeviceGroupService;

    @Resource
    WifiDeviceGroupRelationService wifiDeviceGroupRelationService;

    /**
     * 还需要删除gid及其所有的子节点
     * @param uid
     * @param gids
     */
    public void cleanUpByIds(Integer uid,String gids){
        String[] arrayresids = gids.split(StringHelper.COMMA_STRING_GAP);
        for(String residstr:arrayresids){
            Integer resid = new Integer(residstr);
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
}
