package com.bhu.vas.business.backendonline.asyncprocessor.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.DownCmds;
import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.OperationDS;
import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.helper.VapEnumType.SharedNetworkType;
import com.bhu.vas.api.helper.WifiDeviceHelper;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.notify.ISharedNetworkNotifyCallback;
import com.bhu.vas.business.asyn.spring.model.IDTO;
import com.bhu.vas.business.asyn.spring.model.UserDeviceSharedNetworkApplyDTO;
import com.bhu.vas.business.backendonline.asyncprocessor.service.iservice.IMsgHandlerService;
import com.bhu.vas.business.ds.device.facade.DeviceCMDGenFacadeService;
import com.bhu.vas.business.ds.device.facade.SharedNetworkFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.bhu.vas.business.search.service.increment.WifiDeviceIndexIncrementService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;

@Service
public class UserDeviceSharedNetworkApplyServiceHandler implements IMsgHandlerService {
	private final Logger logger = LoggerFactory.getLogger(UserDeviceSharedNetworkApplyServiceHandler.class);
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private SharedNetworkFacadeService sharedNetworkFacadeService;
	
	@Resource
	private WifiDeviceIndexIncrementService wifiDeviceIndexIncrementService;
	
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	
	@Resource
	private DeviceCMDGenFacadeService deviceCMDGenFacadeService;
	
	@Resource
	private IDaemonRpcService daemonRpcService;

	@Override
	public void process(String message) {
		logger.info(String.format("process message[%s]", message));
		try{
			UserDeviceSharedNetworkApplyDTO applyDto = JsonHelper.getDTO(message, UserDeviceSharedNetworkApplyDTO.class);
			final int userid = applyDto.getUid();
			final List<String> dmacs = applyDto.getMacs()== null?new ArrayList<String>():applyDto.getMacs();
			String snk_type = applyDto.getSnk_type();
			SharedNetworkType sharedNetwork = VapEnumType.SharedNetworkType.fromKey(snk_type);
			if(sharedNetwork == null){
				sharedNetwork = SharedNetworkType.SafeSecure;
			}
			boolean onlyindexupdate = applyDto.isOnlyindexupdate();
			char dtoType = applyDto.getDtoType();
			
			if(userid <= 0 && dmacs.isEmpty()){
				logger.info("UserDeviceSharedNetworkApplyServiceHandler 条件不符");
				return;
			}
			if(onlyindexupdate){//只进行索引更新
				logger.info(String.format("process dmacs[%s] sharedNetwork[%s] onlyindexupdate[%s] dtoType[%s]", dmacs,sharedNetwork, onlyindexupdate,dtoType));
				if(dmacs.isEmpty()) return;
				switch(dtoType){
					case IDTO.ACT_DELETE:
						wifiDeviceIndexIncrementService.sharedNetworkMultiUpdIncrement(dmacs, null);
						break;
					default:
						wifiDeviceIndexIncrementService.sharedNetworkMultiUpdIncrement(dmacs, sharedNetwork.getKey());
						break;
				}
				return;
			}
			
			
			final List<DownCmds> downCmds = new ArrayList<DownCmds>();
			if(!dmacs.isEmpty()){//应用指令下发，取值从设备t_wifi_devices_sharednetwork中获取，更新索引生成指令下发
			}else{//给此用户所有的sharedNetwork的设备变更配置并下发指令更新索引，
				wifiDeviceDataSearchService.iteratorWithSharedNetwork(userid, sharedNetwork.getKey(),200, new IteratorNotify<Page<WifiDeviceDocument>>() {
				    @Override
				    public void notifyComming(Page<WifiDeviceDocument> pages) {
				    	for (WifiDeviceDocument doc : pages) {
				    		String mac = doc.getD_mac();
				    		dmacs.add(mac);
				    	}
				    }
				});
			}
			if(!dmacs.isEmpty()){
				logger.info(String.format("prepare apply sharednetwork conf uid[%s] dmacs[%s]",userid, dmacs));
				sharedNetworkFacadeService.addDevices2SharedNetwork(userid,sharedNetwork,false,dmacs,
						new ISharedNetworkNotifyCallback(){
							@Override
							public void notify(ParamSharedNetworkDTO current,List<String> rdmacs) {
								logger.info(String.format("notify callback uid[%s] rdmacs[%s] sharednetwork conf[%s]", userid,rdmacs,JsonHelper.getJSONString(current)));
								if(rdmacs == null || rdmacs.isEmpty()){
									return;
								}
								for(String mac:rdmacs){
									WifiDevice wifiDevice = wifiDeviceService.getById(mac);
									if(wifiDevice == null) continue;
									current.switchWorkMode(WifiDeviceHelper.isWorkModeRouter(wifiDevice.getWork_mode()));
									//生成下发指令
									String cmd = CMDBuilder.autoBuilderCMD4Opt(OperationCMD.ModifyDeviceSetting,OperationDS.DS_SharedNetworkWifi_Start, mac, -1,JsonHelper.getJSONString(current),deviceCMDGenFacadeService);
									downCmds.add(DownCmds.builderDownCmds(mac, cmd));
								}
								if(!downCmds.isEmpty()){
									daemonRpcService.wifiMultiDevicesCmdsDown(downCmds.toArray(new DownCmds[0]));
									downCmds.clear();
								}
								wifiDeviceIndexIncrementService.sharedNetworkMultiUpdIncrement(rdmacs, current.getNtype());
							}
						});
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
