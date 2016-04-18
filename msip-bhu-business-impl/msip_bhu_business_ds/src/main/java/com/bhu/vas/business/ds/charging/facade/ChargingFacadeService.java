package com.bhu.vas.business.ds.charging.facade;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.charging.dto.SharedealInfo;
import com.bhu.vas.api.rpc.charging.dto.WithdrawCostInfo;
import com.bhu.vas.api.rpc.charging.model.WifiDeviceSharedealConfigs;
import com.bhu.vas.business.ds.charging.service.UserWithdrawCostConfigsService;
import com.bhu.vas.business.ds.charging.service.WifiDeviceBatchImportService;
import com.bhu.vas.business.ds.charging.service.WifiDeviceSharedealConfigsService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.user.service.UserDeviceService;
import com.bhu.vas.business.ds.user.service.UserService;

@Service
public class ChargingFacadeService {
	@Resource
	private UserService userService;

	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private UserDeviceService userDeviceService;
	
	@Resource
	private WifiDeviceBatchImportService wifiDeviceBatchImportService;

	@Resource
	private UserWithdrawCostConfigsService userWithdrawCostConfigsService;

    @Resource
    private WifiDeviceSharedealConfigsService wifiDeviceSharedealConfigsService;

	public UserService getUserService() {
		return userService;
	}

	public WifiDeviceService getWifiDeviceService() {
		return wifiDeviceService;
	}

	public WifiDeviceBatchImportService getWifiDeviceBatchImportService() {
		return wifiDeviceBatchImportService;
	}

	public UserWithdrawCostConfigsService getUserWithdrawCostConfigsService() {
		return userWithdrawCostConfigsService;
	}

	public WifiDeviceSharedealConfigsService getWifiDeviceSharedealConfigsService() {
		return wifiDeviceSharedealConfigsService;
	}
	
	
	/**
	 * 获取当前对此用户生效的钱包配置
	 * 目前直接取Default_ConfigsID对应的数据
	 * @return
	 */
	public WifiDeviceSharedealConfigs userfulWifiDeviceSharedealConfigs(String dmac){
		WifiDeviceSharedealConfigs configs = wifiDeviceSharedealConfigsService.getById(dmac);
		if(configs == null){
			configs = wifiDeviceSharedealConfigsService.getById(WifiDeviceSharedealConfigs.Default_ConfigsWifiID);
			Integer bindUid = userDeviceService.fetchBindUid(dmac);
			configs.doRuntimeInit(dmac,bindUid);
			configs = wifiDeviceSharedealConfigsService.insert(configs);
		}
		return configs;
	}
	
	/**
	 * 计算收益分成
	 * @param uid
	 * @param cash
	 * @return
	 */
	public SharedealInfo calculateSharedeal(String dmac,String orderid,double cash){
		WifiDeviceSharedealConfigs configs = this.userfulWifiDeviceSharedealConfigs(dmac);
		SharedealInfo result =  SharedealInfo.calculate(dmac, orderid, cash, configs.getOwner_percent(), configs.getManufacturer_percent());
		if(configs.getOwner()>0){
			result.setOwner(configs.getOwner());
			result.setBelong(true);
		}else{
			result.setOwner(WifiDeviceSharedealConfigs.Default_Owner);
			result.setBelong(false);
		}
		result.setManufacturer(configs.getManufacturer());
		return result;
	}
	
	
	public WithdrawCostInfo calculateWithdrawCost(int uid,String applyid,double cash){
		return userWithdrawCostConfigsService.calculateWithdrawCost(uid,applyid,cash);
	}
}
