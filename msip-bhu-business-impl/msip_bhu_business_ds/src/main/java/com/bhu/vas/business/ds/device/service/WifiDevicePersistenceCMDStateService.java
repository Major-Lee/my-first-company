package com.bhu.vas.business.ds.device.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.OperationDS;
import com.bhu.vas.api.helper.PersistenceAction;
import com.bhu.vas.api.helper.WifiDeviceHelper;
import com.bhu.vas.api.rpc.devices.dto.PersistenceCMDDTO;
import com.bhu.vas.api.rpc.devices.model.WifiDevicePersistenceCMDState;
import com.bhu.vas.business.ds.device.dao.WifiDevicePersistenceCMDStateDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

@Service
@Transactional("coreTransactionManager")
public class WifiDevicePersistenceCMDStateService extends AbstractCoreService<String,WifiDevicePersistenceCMDState,WifiDevicePersistenceCMDStateDao>{
	
	@Resource
	@Override
	public void setEntityDao(WifiDevicePersistenceCMDStateDao wifiDevicePersistenceCMDStateDao) {
		super.setEntityDao(wifiDevicePersistenceCMDStateDao);
	}

	public void filterPersistenceCMD(String mac, OperationCMD opt, OperationDS subopt, String extparams){
		System.out.println(String.format("filterPersistenceCMD mac:%s opt:%s subopt:%s extparams:%s", mac,opt,subopt,extparams));
		
		PersistenceAction action = WifiDeviceHelper.needPersistenceAction(opt, subopt);
		if(action != null){
			WifiDevicePersistenceCMDState persist = this.getOrCreateById(mac);
			if(PersistenceAction.Oper_Update.equals(action.getOperation())){
				persist.addOrUpdatePersistence(action.getKey(),new PersistenceCMDDTO(opt.getNo(),subopt!=null?subopt.getNo():OperationDS.Empty_OperationDS,extparams));
			}else if(PersistenceAction.Oper_Remove.equals(action.getOperation())){
				persist.removePersistence(action.getKey());
				//persist.removePersistence(new PersistenceCMDDTO(opt.getNo(),subopt!=null?subopt.getNo():OperationDS.Empty_OperationDS,extparams).toKey());
			}else{
				;
			}
			this.update(persist);
		}
	}
}
