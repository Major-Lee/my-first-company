package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.SharedNetworksHelper;
import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.helper.WifiDeviceDocumentEnumType;
import com.bhu.vas.api.helper.VapEnumType.SharedNetworkType;
import com.bhu.vas.api.helper.WifiDeviceHelper;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.SharedNetworkDeviceDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.SharedNetworkSettingDTO;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSharedNetwork;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.vto.device.DeviceProfileVTO;
import com.bhu.vas.api.vto.device.SnkPortalVTO;
import com.bhu.vas.api.vto.device.UserSnkPortalVTO;
import com.bhu.vas.business.asyn.spring.activemq.service.async.AsyncDeliverMessageService;
import com.bhu.vas.business.asyn.spring.model.IDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.ds.charging.facade.ChargingFacadeService;
import com.bhu.vas.business.ds.device.facade.SharedNetworksFacadeService;
import com.bhu.vas.business.ds.user.facade.UserValidateServiceHelper;
import com.bhu.vas.business.ds.user.facade.UserWifiDeviceFacadeService;
import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.model.WifiDeviceDocumentHelper;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;
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
	
/*	@Resource
	private UserDeviceService userDeviceService;*/
	
	@Resource
	private UserWifiDeviceFacadeService userWifiDeviceFacadeService;
	
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	
	@Resource
	private ChargingFacadeService chargingFacadeService;
	//@Resource
	//private DeliverMessageService deliverMessageService;
	
	@Resource
	private AsyncDeliverMessageService asyncDeliverMessageService;
	/**
	 * 通过dmac获取绑定用户id、nick、mobileno、avatar、设备访客相关限速
	 * @param mac
	 * @return
	 */
	public RpcResponseDTO<DeviceProfileVTO> fetchDeviceSnks4Portal(String mac) {
		try{
			WifiDevice wifiDevice = sharedNetworksFacadeService.getWifiDeviceService().getById(mac);
			if(wifiDevice == null){
				throw new BusinessI18nCodeException(ResponseErrorCode.DEVICE_DATA_NOT_EXIST,new String[]{"mac"});
			}
			User user = null;
			//Integer bindUid = sharedNetworksFacadeService.getUserDeviceService().fetchBindUid(mac);
			Integer bindUid = userWifiDeviceFacadeService.findUidById(mac);
			if(bindUid != null){
				user = sharedNetworksFacadeService.getUserService().getById(bindUid);
			}
			DeviceProfileVTO vto = new DeviceProfileVTO();
			vto.setMac(mac);
			if(user != null){
				vto.setId(user.getId());
				vto.setNick(user.getNick());
				vto.setMobileno(user.getMobileno());
				vto.setAvatar(user.getAvatar());
			}else{
				vto.setId(-1);
				vto.setNick(StringHelper.MINUS_STRING_GAP);
				vto.setMobileno(StringHelper.MINUS_STRING_GAP);
				vto.setAvatar(StringHelper.MINUS_STRING_GAP);
			}
			SharedNetworkSettingDTO sharedNetworkConf = sharedNetworksFacadeService.fetchDeviceSharedNetworkConf(mac);
			if(sharedNetworkConf != null && sharedNetworkConf.isOn() && sharedNetworkConf.getPsn() != null){
				vto.setUsers_rate(sharedNetworkConf.getPsn().getUsers_tx_rate());
			}
			/*UserSettingState settingState = userSettingStateService.getById(mac);
			if(settingState != null){
				UserVistorWifiSettingDTO vistorWifi = settingState.getUserSetting(UserVistorWifiSettingDTO.Setting_Key, UserVistorWifiSettingDTO.class);
				if(vistorWifi != null && vistorWifi.isOn() && vistorWifi.getVw() != null){//访客网络是开启
					vto.setUsers_rate(vistorWifi.getVw().getUsers_tx_rate());
				}
			}*/
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
		}catch(BusinessI18nCodeException i18nex){
			i18nex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode(),i18nex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	public RpcResponseDTO<UserSnkPortalVTO> fetchUserSnks4Portal(int uid,String sharenetwork_type) {
		User user = UserValidateServiceHelper.validateUser(uid,sharedNetworksFacadeService.getUserService());
		try{
			SharedNetworkType sharedNetwork = VapEnumType.SharedNetworkType.fromKey(sharenetwork_type);
			if(sharedNetwork == null){
				sharedNetwork = SharedNetworkType.SafeSecure;
			}
			List<ParamSharedNetworkDTO> snks = sharedNetworksFacadeService.fetchAllUserSharedNetworkConf(uid, sharedNetwork);
			UserSnkPortalVTO vto = new UserSnkPortalVTO();
			vto.setId(user.getId());
			vto.setNick(user.getNick());
			vto.setAvatar(user.getAvatar());
			vto.setMobileno(user.getMobileno());
			vto.setSnks(new ArrayList<SnkPortalVTO>());
			for(ParamSharedNetworkDTO snk:snks){
				SnkPortalVTO inner = new SnkPortalVTO();
				inner.setTpl(snk.getTemplate());
				inner.setUsers_rate(snk.getUsers_tx_rate());
				vto.getSnks().add(inner);
			}
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
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
			//UserValidateServiceHelper.validateUserDevice(uid, dmac, userDeviceService)
			SharedNetworkType sharedNetwork = VapEnumType.SharedNetworkType.fromKey(sharenetwork_type);
			if(sharedNetwork == null){
				sharedNetwork = SharedNetworkType.SafeSecure;
			}
			//template 不为空并且 是无效的template格式,如果为空或者是有效的格式 则传递后续处理
			if(StringUtils.isNotEmpty(template) && !SharedNetworksHelper.validTemplateFormat(template)){
				template = SharedNetworksHelper.DefaultTemplate;
			}
			
			ParamSharedNetworkDTO sharednetwork_dto = JsonHelper.getDTO(extparams, ParamSharedNetworkDTO.class);
			sharednetwork_dto.setNtype(sharedNetwork.getKey());
			sharednetwork_dto.setTemplate(template);
			//ParamSharedNetworkDTO.fufillWithDefault(sharednetwork_dto);
			boolean configChanged = sharedNetworksFacadeService.doApplySharedNetworksConfig(uid, sharednetwork_dto);
			if(configChanged){
				//异步消息执行用户的所有设备应用此配置并发送指令
				asyncDeliverMessageService.sendBatchDeviceSnkApplyActionMessage(uid,sharedNetwork.getKey(),sharednetwork_dto.getTemplate(), null,false,IDTO.ACT_UPDATE);
				//deliverMessageService.sendPortalUpdateRateChangedActionMessage(uid, sharedNetwork.getKey(), sharednetwork_dto.getTemplate(), sharednetwork_dto.getUsers_tx_rate());
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
	 * 	条件1：用户只能删除自己创见的模板。
		条件2：此模板未关联设备（开启状态）。
		如果认证网络是关闭状态，可以删除模板，模板删除后对应的设备会自动关联相同类型的默认模板（原来是打赏的就关联打赏默认，原来是短信的就关联短信默认）。
		如果认证网络是开启状态？则需提示需要解除模板关联的所有设备
	 * @param uid
	 * @param sharenetwork_type
	 * @param template
	 * @return
	 */
	public RpcResponseDTO<Boolean> clearNetworkConf(int uid, String sharenetwork_type,String template) {
		try{
			SharedNetworkType sharedNetwork = VapEnumType.SharedNetworkType.fromKey(sharenetwork_type);
			if(sharedNetwork == null || SharedNetworksHelper.wasDefaultTemplate(template) || !SharedNetworksHelper.validTemplateFormat(template)){
				throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_NOTEXIST,
						new String[]{sharenetwork_type,template});
			}
			UserValidateServiceHelper.validateUser(uid, sharedNetworksFacadeService.getUserService());
			//验证此模板是否有设备使用  搜索引擎count 有则返回错误，没有异步消息进行处理并返回成功
			long count = wifiDeviceDataSearchService.searchCountBySnkType(uid,sharedNetwork.getKey(),template,
					 WifiDeviceDocumentEnumType.SnkTurnStateEnum.On.getType());
			if(count >0){
				throw new BusinessI18nCodeException(ResponseErrorCode.USER_DEVICE_SHAREDNETWORK_TEMPLATE_CLEAR_NOTALLOW,
						new String[]{sharenetwork_type,template,String.valueOf(count)});
			}else{
				//异步消息进行处理
				asyncDeliverMessageService.sendBatchDeviceSnkTemplateClearActionMessage(uid, sharenetwork_type, template);
				return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
			}
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	
	/**
	 * 指定的设备应用用户的具体指定配置
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
			//关闭 false 并且 安全共享网络 并且 所有的设备中有不可以关闭共享网络的设备
			/*if(!on && sharedNetwork==SharedNetworkType.SafeSecure  && !chargingFacadeService.canAllBeTurnoff(dmacs)){
				throw new BusinessI18nCodeException(ResponseErrorCode.USER_DEVICE_SHAREDNETWORK_SAFESECURE_CANNOT_BETURNOFF);
			}*/
			//对于设备allowturnoff标记 如果为false，则只允许开启，不允许关闭或切换为别的网络类型
			/*if(on){
				if(sharedNetwork !=SharedNetworkType.SafeSecure && chargingFacadeService.canAllBeTurnoff(dmacs)){
					throw new BusinessI18nCodeException(ResponseErrorCode.USER_DEVICE_SHAREDNETWORK_SAFESECURE_CANNOT_BETURNOFF);
				}
			}else{
				
			}*/
			if(on && sharedNetwork ==SharedNetworkType.SafeSecure){//此种情况为开启SafeSecure网络 ，可以不做任何验证
				;
			}else{
				if(!chargingFacadeService.canAllBeTurnoff(dmacs)){
					if(on  && sharedNetwork ==SharedNetworkType.Uplink){//不允许开启Uplink网络
						throw new BusinessI18nCodeException(ResponseErrorCode.USER_DEVICE_SHAREDNETWORK_SAFESECURE_CANNOT_BETURNOFF);
					}
					if(!on && sharedNetwork ==SharedNetworkType.SafeSecure){//不允许关闭SafeSecure网络
						throw new BusinessI18nCodeException(ResponseErrorCode.USER_DEVICE_SHAREDNETWORK_SAFESECURE_CANNOT_BETURNOFF);
					}
				}
			}
			UserValidateServiceHelper.validateUserDevices(uid, dmacs, userWifiDeviceFacadeService);
			//template 不为空并且 是无效的template格式,如果为空或者是有效的格式 则传递后续处理
			if(StringUtils.isNotEmpty(template) && !SharedNetworksHelper.validTemplateFormat(template)){
				template = SharedNetworksHelper.DefaultTemplate;
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
			asyncDeliverMessageService.sendBatchDeviceSnkApplyActionMessage(uid,sharedNetwork.getKey(),template, dmacs,false,on?IDTO.ACT_UPDATE:IDTO.ACT_DELETE);
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
			if(!sharedNetworkConfs.isEmpty()){
				Collections.sort(sharedNetworkConfs, new Comparator<ParamSharedNetworkDTO>() {
		            public int compare(ParamSharedNetworkDTO a, ParamSharedNetworkDTO b) {
		            	ParamSharedNetworkDTO d1=(ParamSharedNetworkDTO)a;
		            	ParamSharedNetworkDTO d2=(ParamSharedNetworkDTO)b;
		            	if(d1.getTs() == d2.getTs()) return 0;
		            	else if(d1.getTs() < d2.getTs()) return 1;
		            	else return -1;
		            }
		        });
			}
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(sharedNetworkConfs);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	public RpcResponseDTO<Map<String, List<ParamSharedNetworkDTO>>> fetchAllUserNetworksConf(int uid) {
		try{
			Map<String, List<ParamSharedNetworkDTO>> allUserSharedNetworksConf = sharedNetworksFacadeService.fetchAllUserSharedNetworksConf(uid);
			/*if(!sharedNetworkConfs.isEmpty()){
				Collections.sort(sharedNetworkConfs, new Comparator<ParamSharedNetworkDTO>() {
		            public int compare(ParamSharedNetworkDTO a, ParamSharedNetworkDTO b) {
		            	ParamSharedNetworkDTO d1=(ParamSharedNetworkDTO)a;
		            	ParamSharedNetworkDTO d2=(ParamSharedNetworkDTO)b;
		            	if(d1.getTs() == d2.getTs()) return 0;
		            	else if(d1.getTs() < d2.getTs()) return 1;
		            	else return -1;
		            }
		        });
			}*/
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(allUserSharedNetworksConf);
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
			if(StringUtils.isNotEmpty(template) && !SharedNetworksHelper.validTemplateFormat(template)){
				template = SharedNetworksHelper.DefaultTemplate;
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
			UserValidateServiceHelper.validateUserDevice(uid, mac, userWifiDeviceFacadeService);
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
	
	public RpcResponseDTO<TailPage<SharedNetworkDeviceDTO>> pages(int uid, String sharedNetwork_type, String template, String d_dut,
			int pageNo, int pageSize){
		try{
			List<SharedNetworkDeviceDTO> vtos = null;
			
			int searchPageNo = pageNo>=1?(pageNo-1):pageNo;
			Page<WifiDeviceDocument> search_result = wifiDeviceDataSearchService.searchPageBySharedNetwork(uid, 
					sharedNetwork_type, template, d_dut, searchPageNo, pageSize);
			
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
							if(wifiDeviceDocument.getD_snk_allowturnoff() != null){
								vto.setD_snk_allowturnoff(Integer.parseInt(wifiDeviceDocument.getD_snk_allowturnoff()));
							}else{
								vto.setD_snk_allowturnoff(1);
							}
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
							vto.setD_online(wifiDeviceDocument.getD_online());
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
