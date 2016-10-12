package com.bhu.vas.business.ds.charging.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.charging.model.WifiDeviceSharedealConfigsOld;
import com.bhu.vas.business.ds.charging.dao.WifiDeviceSharedealConfigsOldDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

@Service
@Transactional("coreTransactionManager")
public class WifiDeviceSharedealConfigsOldService extends AbstractCoreService<String,WifiDeviceSharedealConfigsOld,WifiDeviceSharedealConfigsOldDao>{
	
	@Resource
	@Override
	public void setEntityDao(WifiDeviceSharedealConfigsOldDao wifiDeviceSharedealConfigsOldDao) {
		super.setEntityDao(wifiDeviceSharedealConfigsOldDao);
	}
	
	public WifiDeviceSharedealConfigsOld addDefault(){
		WifiDeviceSharedealConfigsOld configs = new WifiDeviceSharedealConfigsOld();
		configs.setId(WifiDeviceSharedealConfigsOld.Default_ConfigsWifiID);
		configs.setBatchno(WifiDeviceSharedealConfigsOld.Default_ConfigsBatchno);
		configs.setAit_mobile(WifiDeviceSharedealConfigsOld.Default_AIT);
		configs.setAit_pc(WifiDeviceSharedealConfigsOld.Default_AIT);
		configs.setRange_cash_mobile(WifiDeviceSharedealConfigsOld.Default_Range_Cash_Mobile);
		configs.setRange_cash_pc(WifiDeviceSharedealConfigsOld.Default_Range_Cash_PC);
		configs.setCanbe_turnoff(true);
		configs.setRuntime_applydefault(true);
		configs.setOwner(WifiDeviceSharedealConfigsOld.Default_Owner);
		configs.setManufacturer(WifiDeviceSharedealConfigsOld.Default_Manufacturer);
		configs.setOwner_percent(WifiDeviceSharedealConfigsOld.Default_Owner_Percent);
		configs.setManufacturer_percent(WifiDeviceSharedealConfigsOld.Default_Manufacturer_Percent);
		this.insert(configs);
		return configs;
	}
}
