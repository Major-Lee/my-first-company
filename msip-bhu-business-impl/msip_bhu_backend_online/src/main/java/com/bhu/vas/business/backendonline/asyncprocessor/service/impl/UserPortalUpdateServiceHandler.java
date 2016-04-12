package com.bhu.vas.business.backendonline.asyncprocessor.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.VapEnumType.SharedNetworkType;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.asyn.spring.model.UserPortalUpdateDTO;
import com.bhu.vas.business.backendonline.asyncprocessor.service.iservice.IMsgHandlerService;
import com.bhu.vas.business.ds.device.facade.SharedNetworksFacadeService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.cores.helper.JsonHelper;

@Service
public class UserPortalUpdateServiceHandler implements IMsgHandlerService {
	private final Logger logger = LoggerFactory.getLogger(UserPortalUpdateServiceHandler.class);
	
	@Resource
	private UserService userService;
	
	@Resource
	private SharedNetworksFacadeService sharedNetworksFacadeService;
	
	@Override
	public void process(String message) {
		logger.info(String.format("process message[%s]", message));
		try{
			UserPortalUpdateDTO dto = JsonHelper.getDTO(message, UserPortalUpdateDTO.class);
			int uid = dto.getUid();
			User user = userService.getById(uid);
			if(user != null){
				ParamSharedNetworkDTO sharedNetworkConf = sharedNetworksFacadeService.fetchUserSharedNetworkConf(uid, SharedNetworkType.fromKey(dto.getSnk()), dto.getTpl());
				if(sharedNetworkConf != null && sharedNetworkConf.isOn() && sharedNetworkConf.getPsn() != null){
					vto.setUsers_rate(sharedNetworkConf.getPsn().getUsers_tx_rate());
				}
			}
		}finally{
		}
		logger.info(String.format("process message[%s] successful", message));
	}


/*	@Override
	public void createDeviceGroupIndex(String message) {

		logger.info(String.format("WifiDeviceGroupServiceHandler createDeviceGroupIndex message[%s]", message))
		WifiDeviceGroupAsynCreateIndexDTO dto = JsonHelper.getDTO(message, WifiDeviceGroupAsynCreateIndexDTO.class);
		String wifiIdsStr = dto.getWifiIds();
		String type = dto.getType();
		Long gid = dto.getGid();

		List<String> wifiIds = new ArrayList<>();
		List<List<Long>> groupIdList = new ArrayList<List<Long>>();
		String[] wifiIdArray = wifiIdsStr.split(StringHelper.COMMA_STRING_GAP);
		for (String wifiId : wifiIdArray) {
			wifiIds.add(wifiId);

			List<Long> gids = wifiDeviceGroupRelationService.getDeviceGroupIds(wifiId);
			if (type.equals(WifiDeviceGroupAsynCreateIndexDTO.GROUP_INDEX_GRANT)) {
				gids.add(gid);
			} else if (type.equals(WifiDeviceGroupAsynCreateIndexDTO.GROUP_INDEX_UNGRANT)) {
				gids.remove(gid);
			}
			groupIdList.add(gids);
		}
		List<WifiDevice> wifiDeviceList = wifiDeviceService.findByIds(wifiIds);
		wifiDeviceIndexIncrementService.wifiDeviceIndexBlukIncrement(wifiDeviceList, groupIdList);

	}*/
}
