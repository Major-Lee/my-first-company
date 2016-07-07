package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchsnk;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.DownCmds;
import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.OperationDS;
import com.bhu.vas.api.helper.UPortalHttpHelper;
import com.bhu.vas.api.helper.VapEnumType.SharedNetworkType;
import com.bhu.vas.api.helper.WifiDeviceDocumentEnumType;
import com.bhu.vas.api.helper.WifiDeviceDocumentEnumType.SnkTurnStateEnum;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.DeviceStatusExchangeDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.notify.ISharedNetworkNotifyCallback;
import com.bhu.vas.business.asyn.spring.model.IDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.marker.SnkChargingMarkerService;
import com.bhu.vas.business.ds.device.facade.DeviceCMDGenFacadeService;
import com.bhu.vas.business.ds.device.facade.SharedNetworksFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.bhu.vas.business.search.service.increment.WifiDeviceIndexIncrementService;
import com.smartwork.msip.cores.helper.JsonHelper;

@Service
public class BatchSnkApplyService {
	private final Logger logger = LoggerFactory.getLogger(BatchSnkApplyService.class);
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private SharedNetworksFacadeService sharedNetworksFacadeService;
	
	@Resource
	private WifiDeviceIndexIncrementService wifiDeviceIndexIncrementService;
	
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	
	@Resource
	private DeviceCMDGenFacadeService deviceCMDGenFacadeService;
	
	@Resource
	private IDaemonRpcService daemonRpcService;

	public void apply(final int userid,char dtoType,List<String> dmacs,SharedNetworkType sharedNetwork,String template) {
		logger.info(String.format("apply sharednetwork conf uid[%s] dtoType[%s] snk[%s] template[%s] dmacs[%s]",userid,dtoType,sharedNetwork.getKey(),template, dmacs));
		if(dmacs == null || dmacs.isEmpty()) return;
		try{
			final List<DownCmds> downCmds = new ArrayList<DownCmds>();
			switch(dtoType){
				case IDTO.ACT_ADD:
				case IDTO.ACT_UPDATE:
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
										//生成下发指令
										String cmd = CMDBuilder.autoBuilderCMD4Opt(OperationCMD.ModifyDeviceSetting,OperationDS.DS_SharedNetworkWifi_Start, mac, -1,JsonHelper.getJSONString(current),
												DeviceStatusExchangeDTO.build(wifiDevice.getWork_mode(), wifiDevice.getOrig_swver()),deviceCMDGenFacadeService);
										downCmds.add(DownCmds.builderDownCmds(mac, cmd));
									}
									wifiDeviceIndexIncrementService.sharedNetworkMultiUpdIncrement(rdmacs, current.getNtype(),current.getTemplate(),SnkTurnStateEnum.On.getType());
								}
							});
					break;
				case IDTO.ACT_DELETE:
					 sharedNetworksFacadeService.closeAndApplyDevicesFromSharedNetwork(userid,sharedNetwork,template,dmacs,new ISharedNetworkNotifyCallback(){
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
								wifiDeviceIndexIncrementService.sharedNetworkMultiUpdIncrement(rdmacs, current.getNtype(),current.getTemplate(),SnkTurnStateEnum.Off.getType());
							}
						});
					 //TODO:如果为SmsSecure 则需要判定此用户id当前是否还存在此类型的网络处于开启状态，如果都关闭了，则需要重置通知开关并通知portal服务器
					 logger.info(String.format("准备开始判定当前信息共享网络状态类型 "));
					 if(SharedNetworkType.SmsSecure == sharedNetwork || SharedNetworkType.WechatSecure == sharedNetwork){
						 logger.info(String.format("准备开始判定当前信息共享网络状态类型【%s】【%s】",userid,sharedNetwork.getKey()));
						 long count = wifiDeviceDataSearchService.searchCountBySnkType(userid,sharedNetwork.getKey(),
								 WifiDeviceDocumentEnumType.SnkTurnStateEnum.On.getType());
						 logger.info(String.format("当前用户信息共享网络状态类型【%s】【%s】 【%s】",userid,sharedNetwork.getKey(),count));
						 if(count <= 0){//不提供sms认证服务
							 	logger.info(String.format("当前用户信息共享网络状态类型【%s】【%s】 【%s】开始清除标记",userid,sharedNetwork.getKey(),count));
							 	//清除标记
								SnkChargingMarkerService.getInstance().clear(userid);
								//通知uportal清除标记位
								UPortalHttpHelper.uPortalChargingStatusNotify(userid,UPortalHttpHelper.NoService);
								logger.info(String.format("当前用户信息共享网络状态类型【%s】【%s】 【%s】开始清除标记成功",userid,sharedNetwork.getKey(),count));
						 }
					 }
					break;
				default:
					throw new UnsupportedOperationException(String.format("snk act type not supported"));
			}
			if(!downCmds.isEmpty()){
				daemonRpcService.wifiMultiDevicesCmdsDown(downCmds.toArray(new DownCmds[0]));
				downCmds.clear();
			}
		}finally{
			/*if(downCmds != null){
				downCmds.clear();
				downCmds = null;
			}*/
		}
		//logger.info(String.format("process message[%s] successful", message));
	}
}
