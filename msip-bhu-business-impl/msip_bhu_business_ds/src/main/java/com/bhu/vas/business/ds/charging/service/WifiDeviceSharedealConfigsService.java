package com.bhu.vas.business.ds.charging.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.charging.model.WifiDeviceSharedealConfigs;
import com.bhu.vas.business.ds.charging.dao.WifiDeviceSharedealConfigsDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

@Service
@Transactional("coreTransactionManager")
public class WifiDeviceSharedealConfigsService extends AbstractCoreService<String,WifiDeviceSharedealConfigs,WifiDeviceSharedealConfigsDao>{
	
	@Resource
	@Override
	public void setEntityDao(WifiDeviceSharedealConfigsDao wifiDeviceSharedealConfigsDao) {
		super.setEntityDao(wifiDeviceSharedealConfigsDao);
	}
	
	public WifiDeviceSharedealConfigs addDefault(){
		WifiDeviceSharedealConfigs configs = new WifiDeviceSharedealConfigs();
		configs.setId(WifiDeviceSharedealConfigs.Default_ConfigsWifiID);
		configs.setBatchno(WifiDeviceSharedealConfigs.Default_ConfigsBatchno);
		configs.setAit_mobile(WifiDeviceSharedealConfigs.Default_AIT);
		configs.setAit_pc(WifiDeviceSharedealConfigs.Default_AIT);
		configs.setRange_cash_mobile(WifiDeviceSharedealConfigs.Default_Range_Cash_Mobile);
		configs.setRange_cash_pc(WifiDeviceSharedealConfigs.Default_Range_Cash_PC);
		configs.setCanbe_turnoff(true);
		configs.setRuntime_applydefault(true);
		configs.setOwner(WifiDeviceSharedealConfigs.Default_Owner);
		configs.setManufacturer(WifiDeviceSharedealConfigs.Default_Manufacturer);
		configs.setOwner_percent(WifiDeviceSharedealConfigs.Default_Owner_Percent);
		configs.setManufacturer_percent(WifiDeviceSharedealConfigs.Default_Manufacturer_Percent);
		this.insert(configs);
		return configs;
	}
}
