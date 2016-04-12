package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.helper.VapEnumType.SharedNetworkType;
import com.bhu.vas.api.helper.WifiDeviceHelper;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.SharedNetworkDeviceDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.SharedNetworkSettingDTO;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSharedNetwork;
import com.bhu.vas.business.asyn.spring.activemq.service.DeliverMessageService;
import com.bhu.vas.business.asyn.spring.model.IDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.ds.device.facade.SharedNetworksFacadeService;
import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.model.WifiDeviceDocumentHelper;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
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
	private SharedNetworksFacadeService sharedNetworksFacadeService;
	
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	
	@Resource
	private DeliverMessageService deliverMessageService;
	
	
	/**
	 * 修改用户的sharenetwork_type共享网络的配置，得出配置是否变更了
	 * 如果配置变更了则通过异步消息重新应用到此用户的sharenetwork_type设备（修改设备配置及下发指令）
	 * @param uid
	 * @param sharenetwork_type
	 * @param template DefaultCreateTemplate= 0000 则代表新建模板
	 * @param extparams
	 * @return
	 */
	public RpcResponseDTO<ParamSharedNetworkDTO> applyNetworkConf(int uid, String sharenetwork_type,String template, String extparams) {
		try{
			SharedNetworkType sharedNetwork = VapEnumType.SharedNetworkType.fromKey(sharenetwork_type);
			if(sharedNetwork == null){
				sharedNetwork = SharedNetworkType.SafeSecure;
			}
			
			//template 不为空并且 是无效的template格式,如果为空或者是有效的格式 则传递后续处理
			if(StringUtils.isNotEmpty(template) && !SharedNetworksFacadeService.validTemplateFormat(template)){
				template = SharedNetworksFacadeService.DefaultTemplate;
			}
			
			ParamSharedNetworkDTO sharednetwork_dto = JsonHelper.getDTO(extparams, ParamSharedNetworkDTO.class);
			sharednetwork_dto.setNtype(sharedNetwork.getKey());
			sharednetwork_dto.setTemplate(template);
			ParamSharedNetworkDTO.fufillWithDefault(sharednetwork_dto);
			boolean configChanged = sharedNetworksFacadeService.doApplySharedNetworksConfig(uid, sharednetwork_dto);
			if(configChanged){
				//异步消息执行用户的所有设备应用此配置并发送指令
				deliverMessageService.sendUserDeviceSharedNetworkApplyActionMessage(uid,sharedNetwork.getKey(),sharednetwork_dto.getTemplate(), null,false,IDTO.ACT_UPDATE);
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
	public RpcResponseDTO<Boolean> takeEffectNetworkConf(int uid,boolean on,String sharenetwork_type,String template,List<String> dmacs){
		try{
			SharedNetworkType sharedNetwork = VapEnumType.SharedNetworkType.fromKey(sharenetwork_type);
			if(sharedNetwork == null){
				sharedNetwork = SharedNetworkType.SafeSecure;
			}
			//template 不为空并且 是无效的template格式,如果为空或者是有效的格式 则传递后续处理
			if(StringUtils.isNotEmpty(template) && !SharedNetworksFacadeService.validTemplateFormat(template)){
				template = SharedNetworksFacadeService.DefaultTemplate;
			}
			//TODO：等设备版本升级上来后可以去掉此条件约束
			if(dmacs!= null && !dmacs.isEmpty() && on && SharedNetworkType.SafeSecure.getKey().equals(sharedNetwork.getKey())){
				List<WifiDevice> wifiDevices = sharedNetworksFacadeService.getWifiDeviceService().findByIds(dmacs);
				for(WifiDevice device :wifiDevices){
					if(!WifiDeviceHelper.suppertedDeviceSecureSharedNetwork(device.getOrig_swver())){
						throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_VERSION_TOO_LOWER,new String[]{BusinessRuntimeConfiguration.Device_SharedNetwork_Top_Version});
					}
				}
			}
			//异步消息执行用户的 addDevices2SharedNetwork 设备应用此配置并发送指令
			deliverMessageService.sendUserDeviceSharedNetworkApplyActionMessage(uid,sharedNetwork.getKey(),template, dmacs,false,on?IDTO.ACT_UPDATE:IDTO.ACT_DELETE);
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
	
	public RpcResponseDTO<List<ParamSharedNetworkDTO>> fetchAllUserNetworkConf(int uid, String sharenetwork_type) {
		try{
			SharedNetworkType sharedNetwork = VapEnumType.SharedNetworkType.fromKey(sharenetwork_type);
			if(sharedNetwork == null){
				sharedNetwork = SharedNetworkType.SafeSecure;
			}
			List<ParamSharedNetworkDTO> sharedNetworkConfs = sharedNetworksFacadeService.fetchAllUserSharedNetworkConf(uid, sharedNetwork);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(sharedNetworkConfs);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	public RpcResponseDTO<ParamSharedNetworkDTO> fetchUserNetworkConf(int uid, String sharenetwork_type,String template) {
		try{
			SharedNetworkType sharedNetwork = VapEnumType.SharedNetworkType.fromKey(sharenetwork_type);
			if(sharedNetwork == null){
				sharedNetwork = SharedNetworkType.SafeSecure;
			}
			//template 不为空并且 是无效的template格式,如果为空或者是有效的格式 则传递后续处理
			if(StringUtils.isNotEmpty(template) && !SharedNetworksFacadeService.validTemplateFormat(template)){
				template = SharedNetworksFacadeService.DefaultTemplate;
			}
			ParamSharedNetworkDTO sharedNetworkConf = sharedNetworksFacadeService.fetchUserSharedNetworkConf(uid, sharedNetwork,template);
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
			SharedNetworkSettingDTO sharedNetworkSetting = sharedNetworksFacadeService.fetchDeviceSharedNetworkConfIfEmptyThenCreate(uid,mac);
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
	
	public RpcResponseDTO<TailPage<SharedNetworkDeviceDTO>> pages(int uid, String sharedNetwork_type,String template, String d_dut, 
			int pageNo, int pageSize){
		try{
			List<SharedNetworkDeviceDTO> vtos = null;
			
			int searchPageNo = pageNo>=1?(pageNo-1):pageNo;
			Page<WifiDeviceDocument> search_result = wifiDeviceDataSearchService.searchPageBySharedNetwork(uid, 
					sharedNetwork_type, d_dut, searchPageNo, pageSize);
			
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
						List<WifiDeviceSharedNetwork> deviceConfs = sharedNetworksFacadeService.getWifiDeviceSharedNetworkService().findByIds(macs, true, true);
						List<Object> ohd_counts = WifiDeviceHandsetPresentSortedSetService.getInstance().presentOnlineSizes(macs);
						
						vtos = new ArrayList<SharedNetworkDeviceDTO>();
						SharedNetworkDeviceDTO vto = null;
						int cursor = 0;
						for(WifiDeviceDocument wifiDeviceDocument : searchDocuments){
							vto = new SharedNetworkDeviceDTO();
							vto.setMac(wifiDeviceDocument.getId());
							vto.setD_sn(wifiDeviceDocument.getD_sn());
							vto.setD_address(wifiDeviceDocument.getD_address());
							vto.setD_workmodel(wifiDeviceDocument.getD_workmodel());
							vto.setD_origmodel(wifiDeviceDocument.getD_origmodel());
							vto.setDevice_name(wifiDeviceDocument.getU_dnick());
							vto.setD_type(wifiDeviceDocument.getD_type());
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
									vto.setTemplate(deviceConf.getTemplate());
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
