package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchsnk;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.helper.VapEnumType.SharedNetworkType;
import com.bhu.vas.api.helper.WifiDeviceDocumentEnumType.SnkTurnStateEnum;
import com.bhu.vas.business.asyn.spring.model.IDTO;
import com.bhu.vas.business.asyn.spring.model.async.snk.BatchDeviceSnkApplyDTO;
import com.bhu.vas.business.backendonline.asyncprocessor.service.iservice.IMsgHandlerService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.bhu.vas.business.search.service.increment.WifiDeviceIndexIncrementService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;

@Service
public class BatchDeviceSnkApplyServiceHandler implements IMsgHandlerService {
	private final Logger logger = LoggerFactory.getLogger(BatchDeviceSnkApplyServiceHandler.class);
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private BatchSnkApplyService batchSnkApplyService;
	
	//@Resource
	//private SharedNetworksFacadeService sharedNetworksFacadeService;
	
	@Resource
	private WifiDeviceIndexIncrementService wifiDeviceIndexIncrementService;
	
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	
/*	@Resource
	private DeviceCMDGenFacadeService deviceCMDGenFacadeService;
	
	@Resource
	private IDaemonRpcService daemonRpcService;*/

	@Override
	public void process(String message) {
		logger.info(String.format("process message[%s]", message));
		try{
			BatchDeviceSnkApplyDTO applyDto = JsonHelper.getDTO(message, BatchDeviceSnkApplyDTO.class);
			final int userid = applyDto.getUid();
			final List<String> dmacs = applyDto.getMacs()== null?new ArrayList<String>():applyDto.getMacs();
			String snk_type = applyDto.getSnk_type();
			SharedNetworkType sharedNetwork = VapEnumType.SharedNetworkType.fromKey(snk_type);
			if(sharedNetwork == null){
				sharedNetwork = SharedNetworkType.SafeSecure;
			}
			final String template = applyDto.getTemplate();
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
						//移除设备的所属类型不清空sharedNetwork
						wifiDeviceIndexIncrementService.sharedNetworkMultiUpdIncrement(dmacs, sharedNetwork.getKey(),template,SnkTurnStateEnum.Off.getType());
						break;
					default:
						wifiDeviceIndexIncrementService.sharedNetworkMultiUpdIncrement(dmacs, sharedNetwork.getKey(),template,SnkTurnStateEnum.Off.getType());
						break;
				}
				return;
			}
			
			
			//final List<DownCmds> downCmds = new ArrayList<DownCmds>();
			if(!dmacs.isEmpty()){//应用指令下发，取值从设备t_wifi_devices_sharednetwork中获取，更新索引生成指令下发
				;
			}else{//给此用户所有的sharedNetwork的设备变更配置并下发指令更新索引，
				wifiDeviceDataSearchService.iteratorWithSharedNetwork(userid, sharedNetwork.getKey(),template, null, 200, new IteratorNotify<Page<WifiDeviceDocument>>() {
				    @Override
				    public void notifyComming(Page<WifiDeviceDocument> pages) {
				    	for (WifiDeviceDocument doc : pages) {
				    		String mac = doc.getD_mac();
				    		dmacs.add(mac);
				    	}
				    }
				});
			}
			batchSnkApplyService.apply(applyDto.getUid(), applyDto.getDtoType(), dmacs, sharedNetwork, applyDto.getTemplate());
			/*if(!dmacs.isEmpty()){
				logger.info(String.format("prepare apply sharednetwork conf uid[%s] dtoType[%s] dmacs[%s]",userid,applyDto.getDtoType(), dmacs));
				if(IDTO.ACT_ADD == applyDto.getDtoType() || IDTO.ACT_UPDATE == applyDto.getDtoType()){//开启
					sharedNetworksFacadeService.addDevices2SharedNetwork(userid,sharedNetwork,template,false,dmacs,
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
										//current.switchWorkMode(WifiDeviceHelper.isWorkModeRouter(wifiDevice.getWork_mode()));
										//生成下发指令
										String cmd = CMDBuilder.autoBuilderCMD4Opt(OperationCMD.ModifyDeviceSetting,OperationDS.DS_SharedNetworkWifi_Start, mac, -1,JsonHelper.getJSONString(current),
												DeviceStatusExchangeDTO.build(wifiDevice.getWork_mode(), wifiDevice.getOrig_swver()),deviceCMDGenFacadeService);
										downCmds.add(DownCmds.builderDownCmds(mac, cmd));
									}
									wifiDeviceIndexIncrementService.sharedNetworkMultiUpdIncrement(rdmacs, current.getNtype(),current.getTemplate());
								}
							});
				}else{//关闭
					sharedNetworksFacadeService.closeAndApplyDevicesFromSharedNetwork(userid,sharedNetwork,applyDto.getTemplate(),dmacs,new ISharedNetworkNotifyCallback(){
						@Override
						public void notify(ParamSharedNetworkDTO current,List<String> rdmacs) {
							logger.info(String.format("notify callback uid[%s] rdmacs[%s] sharednetwork conf[%s]", userid,rdmacs,JsonHelper.getJSONString(current)));
							if(rdmacs == null || rdmacs.isEmpty()){
								return;
							}
							for(String mac:rdmacs){
								WifiDevice wifiDevice = wifiDeviceService.getById(mac);
								if(wifiDevice == null) continue;
								//生成下发指令
								String cmd = CMDBuilder.autoBuilderCMD4Opt(OperationCMD.ModifyDeviceSetting,OperationDS.DS_SharedNetworkWifi_Stop, mac, -1,null,
										DeviceStatusExchangeDTO.build(wifiDevice.getWork_mode(), wifiDevice.getOrig_swver()),deviceCMDGenFacadeService);
								downCmds.add(DownCmds.builderDownCmds(mac, cmd));
							}
							wifiDeviceIndexIncrementService.sharedNetworkMultiUpdIncrement(rdmacs, current.getNtype(),current.getTemplate());
						}
					});
				}
				if(!downCmds.isEmpty()){
					daemonRpcService.wifiMultiDevicesCmdsDown(downCmds.toArray(new DownCmds[0]));
					downCmds.clear();
				}
			}*/
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
