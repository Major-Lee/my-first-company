package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.helper.VapEnumType.SharedNetworkType;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.SharedNetworkDeviceDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.SharedNetworkSettingDTO;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSharedNetwork;
import com.bhu.vas.business.asyn.spring.activemq.service.DeliverMessageService;
import com.bhu.vas.business.asyn.spring.model.IDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.ds.device.facade.SharedNetworkFacadeService;
import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.model.WifiDeviceDocumentHelper;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * device Sharednetwork RPC组件的业务service
 * @author Edmond Lee
 *
 */
@Service
public class DeviceSharedNetworkUnitFacadeService {
	
	@Resource
	private SharedNetworkFacadeService sharedNetworkFacadeService;
	
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	
	@Resource
	private DeliverMessageService deliverMessageService;
	
	
	/**
	 * 修改用户的sharenetwork_type共享网络的配置，得出配置是否变更了
	 * 如果配置变更了则通过异步消息重新应用到此用户的sharenetwork_type设备（修改设备配置及下发指令）
	 * @param uid
	 * @param sharenetwork_type
	 * @param extparams
	 * @return
	 */
	public RpcResponseDTO<ParamSharedNetworkDTO> applyNetworkConf(int uid, String sharenetwork_type, String extparams) {
		try{
			SharedNetworkType sharedNetwork = VapEnumType.SharedNetworkType.fromKey(sharenetwork_type);
			if(sharedNetwork == null){
				sharedNetwork = SharedNetworkType.SafeSecure;
			}
			
			ParamSharedNetworkDTO sharednetwork_dto = JsonHelper.getDTO(extparams, ParamSharedNetworkDTO.class);
			sharednetwork_dto.setNtype(sharedNetwork.getKey());
			ParamSharedNetworkDTO.fufillWithDefault(sharednetwork_dto);
			boolean configChanged = sharedNetworkFacadeService.doApplySharedNetworksConfig(uid, sharednetwork_dto);
			if(configChanged){
				//异步消息执行用户的所有设备应用此配置并发送指令
				deliverMessageService.sendUserDeviceSharedNetworkApplyActionMessage(uid,sharedNetwork.getKey(), null,false,IDTO.ACT_UPDATE);
			}
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(sharednetwork_dto);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	/**
	 * 指定的设备应用用户的具体指定配置
	 * 
	 * 异步消息修改数据库,并发送指令并且更新索引
	 * @param uid
	 * @param on 开启或关闭
	 * @param sharenetwork_type
	 * @param mac
	 * @return
	 */
	public RpcResponseDTO<Boolean> takeEffectNetworkConf(int uid,boolean on,String sharenetwork_type,List<String> dmacs){
		try{
			SharedNetworkType sharedNetwork = VapEnumType.SharedNetworkType.fromKey(sharenetwork_type);
			if(sharedNetwork == null){
				sharedNetwork = SharedNetworkType.SafeSecure;
			}
			//异步消息执行用户的 addDevices2SharedNetwork 设备应用此配置并发送指令
			deliverMessageService.sendUserDeviceSharedNetworkApplyActionMessage(uid,sharedNetwork.getKey(), dmacs,false,on?IDTO.ACT_UPDATE:IDTO.ACT_DELETE);
			/*List<String> addDevices2SharedNetwork = sharedNetworkFacadeService.addDevices2SharedNetwork(uid,sharedNetwork,false,dmacs);
			if(!addDevices2SharedNetwork.isEmpty()){
				//异步消息执行用户的 addDevices2SharedNetwork 设备应用此配置并发送指令
				deliverMessageService.sendUserDeviceSharedNetworkApplyActionMessage(uid,sharedNetwork.getKey(), addDevices2SharedNetwork,IDTO.ACT_UPDATE);
			}*/
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	public RpcResponseDTO<ParamSharedNetworkDTO> fetchUserNetworkConf(int uid, String sharenetwork_type) {
		try{
			SharedNetworkType sharedNetwork = VapEnumType.SharedNetworkType.fromKey(sharenetwork_type);
			if(sharedNetwork == null){
				sharedNetwork = SharedNetworkType.SafeSecure;
			}
			ParamSharedNetworkDTO sharedNetworkConf = sharedNetworkFacadeService.fetchUserSharedNetworkConf(uid, sharedNetwork);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(sharedNetworkConf);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	public RpcResponseDTO<SharedNetworkSettingDTO> fetchDeviceNetworkConf(int uid, String mac) {
		try{
			/*SharedNetworkType sharedNetwork = VapEnumType.SharedNetworkType.fromKey(sharenetwork_type);
			if(sharedNetwork == null){
				sharedNetwork = SharedNetworkType.SafeSecure;
			}*/
			SharedNetworkSettingDTO sharedNetworkSetting = sharedNetworkFacadeService.fetchDeviceSharedNetworkConf(mac);
			if(sharedNetworkSetting == null){
				sharedNetworkSetting = SharedNetworkSettingDTO.buildOffSetting();
			}
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(sharedNetworkSetting);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	public RpcResponseDTO<TailPage<SharedNetworkDeviceDTO>> pages(int uid, String sharedNetwork_type, int pageNo, int pageSize){
		try{
			List<SharedNetworkDeviceDTO> vtos = null;
			
			int searchPageNo = pageNo>=1?(pageNo-1):pageNo;
			Page<WifiDeviceDocument> search_result = wifiDeviceDataSearchService.searchPageBySharedNetwork(uid, 
					sharedNetwork_type, searchPageNo, pageSize);
			
			int total = 0;
			if(search_result != null){
				total = (int)search_result.getTotalElements();//.getTotal();
				if(total == 0){
					vtos = Collections.emptyList();
				}else{
					List<WifiDeviceDocument> searchDocuments = search_result.getContent();//.getResult();
					if(searchDocuments.isEmpty()) {
						vtos = Collections.emptyList();
					}else{
						List<String> macs = WifiDeviceDocumentHelper.generateDocumentIds(searchDocuments);
						List<WifiDeviceSharedNetwork> deviceConfs = sharedNetworkFacadeService.getWifiDeviceSharedNetworkService().findByIds(macs, true, true);
						List<Object> ohd_counts = WifiDeviceHandsetPresentSortedSetService.getInstance().presentOnlineSizes(macs);
						
						vtos = new ArrayList<SharedNetworkDeviceDTO>();
						SharedNetworkDeviceDTO vto = null;
						int cursor = 0;
						for(WifiDeviceDocument wifiDeviceDocument : searchDocuments){
							vto = new SharedNetworkDeviceDTO();
							vto.setMac(wifiDeviceDocument.getId());
							vto.setD_address(wifiDeviceDocument.getD_address());
							vto.setD_workmodel(wifiDeviceDocument.getD_workmodel());
							if(ohd_counts != null){
								Object ohd_count_obj = ohd_counts.get(cursor);
								if(ohd_count_obj != null){
									vto.setOhd_count((Long)ohd_count_obj);
								}
							}
							vto.setSnk_type(sharedNetwork_type);
							if(deviceConfs != null && !deviceConfs.isEmpty()){
								WifiDeviceSharedNetwork deviceConf = deviceConfs.get(cursor);
								if(deviceConf != null){
									if(sharedNetwork_type.equals(deviceConf.getSharednetwork_type())){
										vto.setMatched(true);
									}
									vto.setOn(deviceConf.getInnerModel().isOn());
								}
							}
							vtos.add(vto);
							cursor++;
						}
					}
				}
			}else{
				vtos = Collections.emptyList();
			}
			TailPage<SharedNetworkDeviceDTO> returnRet = new CommonPage<SharedNetworkDeviceDTO>(pageNo, pageSize, total, vtos);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(returnRet);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
}
