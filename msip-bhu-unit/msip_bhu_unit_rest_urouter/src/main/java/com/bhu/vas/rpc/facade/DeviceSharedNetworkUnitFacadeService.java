package com.bhu.vas.rpc.facade;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.helper.VapEnumType.SharedNetworkType;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.business.asyn.spring.activemq.service.DeliverMessageService;
import com.bhu.vas.business.asyn.spring.model.IDTO;
import com.bhu.vas.business.ds.device.facade.SharedNetworkFacadeService;
import com.smartwork.msip.cores.helper.JsonHelper;
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
				sharedNetwork = SharedNetworkType.Uplink;
			}
			
			ParamSharedNetworkDTO sharednetwork_dto = JsonHelper.getDTO(extparams, ParamSharedNetworkDTO.class);
			sharednetwork_dto.setNtype(sharedNetwork.getKey());
			ParamSharedNetworkDTO.fufillWithDefault(sharednetwork_dto);
			boolean configChanged = sharedNetworkFacadeService.doApplySharedNetworksConfig(uid, sharednetwork_dto);
			if(configChanged){
				//异步消息执行用户的所有设备应用此配置并发送指令
				deliverMessageService.sendUserDeviceSharedNetworkApplyActionMessage(uid,sharedNetwork.getKey(), null,IDTO.ACT_UPDATE);
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
	 * @param sharenetwork_type
	 * @param mac
	 * @return
	 */
	public RpcResponseDTO<Boolean> takeEffectNetworkConf(int uid,String sharenetwork_type,List<String> dmacs){
		try{
			SharedNetworkType sharedNetwork = VapEnumType.SharedNetworkType.fromKey(sharenetwork_type);
			if(sharedNetwork == null){
				sharedNetwork = SharedNetworkType.Uplink;
			}
			//异步消息执行用户的 addDevices2SharedNetwork 设备应用此配置并发送指令
			deliverMessageService.sendUserDeviceSharedNetworkApplyActionMessage(uid,sharedNetwork.getKey(), dmacs,IDTO.ACT_UPDATE);
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
	
	public RpcResponseDTO<ParamSharedNetworkDTO> fetchNetworkConf(int uid, String sharenetwork_type) {
		try{
			SharedNetworkType sharedNetwork = VapEnumType.SharedNetworkType.fromKey(sharenetwork_type);
			if(sharedNetwork == null){
				sharedNetwork = SharedNetworkType.Uplink;
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
	
	public RpcResponseDTO<Boolean> pages(int uid,String sharenetwork_type){
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
	}
}
