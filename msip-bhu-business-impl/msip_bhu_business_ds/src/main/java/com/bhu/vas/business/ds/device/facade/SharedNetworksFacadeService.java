package com.bhu.vas.business.ds.device.facade;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.NumberValidateHelper;
import com.bhu.vas.api.helper.SharedNetworkChangeType;
import com.bhu.vas.api.helper.SharedNetworksHelper;
import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.helper.VapEnumType.SharedNetworkType;
import com.bhu.vas.api.helper.WifiDeviceHelper;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.SharedNetworkSettingDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.SharedNetworkVTO;
import com.bhu.vas.api.rpc.devices.model.UserDevicesSharedNetworks;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSharedNetwork;
import com.bhu.vas.api.rpc.devices.notify.ISharedNetworkNotifyCallback;
import com.bhu.vas.business.ds.device.service.UserDevicesSharedNetworksService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.device.service.WifiDeviceSharedNetworkService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class SharedNetworksFacadeService {
	
	public final static int SHARE_NETWORK_NOT_CHANGED = 0;
	public final static int SHARE_NETWORK_DEVICE_PART_CHANGED = 1;
	public final static int SHARE_NETWORK_CHANGED = 2;
	
	@Resource
	private UserService userService;

	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private WifiDeviceSharedNetworkService wifiDeviceSharedNetworkService;

	@Resource
	private UserDevicesSharedNetworksService userDevicesSharedNetworksService;

/*    @Resource
    private UserDeviceService userDeviceService;*/
	
	/**
	 * 接受页面传递的参数，
	 * 1、比对配置是否变化，如变化则更新用户的配置
	 * 2、配置变化了在其它调用程序发送异步消息到后台，批量更新其绑定的属于ntype的设备
	 * 3、需要注意的是 paramDto中的template字段
	 * 		如果不存在configs 则新建template 为 0 的配置并录入数据库
	 * 		如果存在configs，则判定template是否存在 存在的话判定是否配置变更了，更新数据库 ，如果不存在则获取一个有效的template并add进去一个
	 * 4、处理完后 paramDto中的template值可能会改变，
	 * @param uid
	 * @param paramDto
	 * @return 配置是否变化了
	 */
	public SharedNetworkChangeType doApplySharedNetworksConfig(int uid,ParamSharedNetworkDTO paramDto){
		boolean configChanged = false;
		boolean devicePartChanged = false;
		SharedNetworkType sharedNetwork = VapEnumType.SharedNetworkType.fromKey(paramDto.getNtype());
		paramDto = ParamSharedNetworkDTO.fufillWithDefault(paramDto);
		if(StringUtils.isEmpty(paramDto.getTemplate())){
			paramDto.setTemplate(SharedNetworksHelper.DefaultTemplate);
		}
		SharedNetworksHelper.validAmountRange(paramDto.getRange_cash_mobile(), "rcm", NumberValidateHelper.Range_Amount_Min,NumberValidateHelper.Range_Amount_Max);
		SharedNetworksHelper.validAmountRange(paramDto.getRange_cash_pc(), "rcp", NumberValidateHelper.Range_Amount_Min,NumberValidateHelper.Range_Amount_Max);
		SharedNetworksHelper.validAitRange(paramDto.getAit_mobile(), "ait_m", NumberValidateHelper.Range_Ait_Min,NumberValidateHelper.Range_Ait_Max);
		SharedNetworksHelper.validAitRange(paramDto.getAit_pc(), "ait_p", NumberValidateHelper.Range_Ait_Min,NumberValidateHelper.Range_Ait_Max);
		SharedNetworksHelper.validAitRange(paramDto.getFree_ait_mobile(), "fait_m", NumberValidateHelper.Range_Ait_Min,NumberValidateHelper.Range_Ait_Max);
		SharedNetworksHelper.validAitRange(paramDto.getFree_ait_pc(), "fait_p", NumberValidateHelper.Range_Ait_Min,NumberValidateHelper.Range_Ait_Max);
		
		/*if(StringUtils.isEmpty(paramDto.getTemplate_name())){
			paramDto.setTemplate_name(sharedNetwork.getName().concat(paramDto.getTemplate()));
		}*/
		
		UserDevicesSharedNetworks configs = userDevicesSharedNetworksService.getById(uid);
		//SharedNetworkType sharedNetwork = VapEnumType.SharedNetworkType.fromKey(paramDto.getNtype());
		//if(StringUtils.isNotEmpty(sharednetwork_dto.getTemplate_name())){
		//	sharednetwork_dto.setTemplate_name(SharedNetworkType.SafeSecure.getName().concat(sharednetwork_dto.getTemplate()));
		//}
		if(configs == null){
			configs = SharedNetworksHelper.buildDefaultUserDevicesSharedNetworks(uid, paramDto);
			/*configs = new UserDevicesSharedNetworks();
			configs.setId(uid);
			List<ParamSharedNetworkDTO> models = new ArrayList<ParamSharedNetworkDTO>();
			paramDto.setTemplate(DefaultTemplate);
			models.add(paramDto);
			configs.put(paramDto.getNtype(), models);*/
			userDevicesSharedNetworksService.insert(configs);
			devicePartChanged = true;
		}else{
			List<ParamSharedNetworkDTO> models_fromdb = configs.get(paramDto.getNtype(),new ArrayList<ParamSharedNetworkDTO>(),true);
			
			if(VapEnumType.SharedNetworkType.Uplink.getKey().equals(paramDto.getNtype())){
				paramDto.setTemplate(SharedNetworksHelper.DefaultTemplate);
				if(StringUtils.isEmpty(paramDto.getTemplate_name()))
					paramDto.setTemplate_name(SharedNetworksHelper.buildTemplateName(sharedNetwork, SharedNetworksHelper.DefaultTemplate));//sharedNetwork.getName().concat(DefaultTemplate));
				paramDto.setTs(System.currentTimeMillis());
				models_fromdb.clear();
				models_fromdb.add(paramDto);
				userDevicesSharedNetworksService.update(configs);
				configChanged = true;
			}else{//SafeSecure & SmsSecure
				//验证models_fromdb 是否存在 template编号,如果存在则替换，否则增加
				int index = models_fromdb.indexOf(paramDto);
				if(index != -1){
					ParamSharedNetworkDTO dto_fromdb = models_fromdb.get(index);
					if(StringUtils.isEmpty(paramDto.getTemplate_name()))
						paramDto.setTemplate_name(sharedNetwork.getName().concat(paramDto.getTemplate()));
					paramDto.setTs(System.currentTimeMillis());
					if(ParamSharedNetworkDTO.wasDeviceRelatedConfigChanged( paramDto,dto_fromdb) /* || ParamSharedNetworkDTO.wasTemplateNameChanged(paramDto,dto_fromdb) */){
						devicePartChanged = true;
					} else {
						configChanged = ParamSharedNetworkDTO.wasServerRelatedConfigChanged( paramDto,dto_fromdb);
					}
					models_fromdb.set(index, paramDto);
					userDevicesSharedNetworksService.update(configs);
				}else{
					//SafeSecure网络需要限制模板数量
					if(models_fromdb.size() >= BusinessRuntimeConfiguration.SharedNetworksTemplateMaxLimit){
						throw new BusinessI18nCodeException(ResponseErrorCode.USER_DEVICE_SHAREDNETWORK_TEMPLATES_MAXLIMIT,new String[]{String.valueOf(BusinessRuntimeConfiguration.SharedNetworksTemplateMaxLimit)});
					}
					String template = SharedNetworksHelper.fetchValidTemplate(models_fromdb);
					paramDto.setTemplate(template);
					paramDto.setTs(System.currentTimeMillis());
					if(StringUtils.isEmpty(paramDto.getTemplate_name()))
						paramDto.setTemplate_name(SharedNetworksHelper.buildTemplateName(sharedNetwork, template));//sharedNetwork.getName().concat(template));
					models_fromdb.add(paramDto);
					userDevicesSharedNetworksService.update(configs);
					//当前不可能有新设备应用新模板，所以返回false
					configChanged = false;
				}
			}
			
			/*if(VapEnumType.SharedNetworkType.SafeSecure.getKey().equals(paramDto.getNtype())){
				//验证models_fromdb 是否存在 template编号,如果存在则替换，否则增加
				int index = models_fromdb.indexOf(paramDto);
				if(index != -1){
					ParamSharedNetworkDTO dto_fromdb = models_fromdb.get(index);
					if(StringUtils.isEmpty(paramDto.getTemplate_name()))
						paramDto.setTemplate_name(sharedNetwork.getName().concat(paramDto.getTemplate()));
					paramDto.setTs(System.currentTimeMillis());
					if(ParamSharedNetworkDTO.wasConfigChanged( paramDto,dto_fromdb) || ParamSharedNetworkDTO.wasTemplateNameChanged(paramDto,dto_fromdb)){
						configChanged = true;
					}
					models_fromdb.set(index, paramDto);
					userDevicesSharedNetworksService.update(configs);
				}else{
					//SafeSecure网络需要限制模板数量
					if(models_fromdb.size() >= BusinessRuntimeConfiguration.SharedNetworksTemplateMaxLimit){
						throw new BusinessI18nCodeException(ResponseErrorCode.USER_DEVICE_SHAREDNETWORK_TEMPLATES_MAXLIMIT,new String[]{String.valueOf(BusinessRuntimeConfiguration.SharedNetworksTemplateMaxLimit)});
					}
					String template = SharedNetworksHelper.fetchValidTemplate(models_fromdb);
					paramDto.setTemplate(template);
					paramDto.setTs(System.currentTimeMillis());
					if(StringUtils.isEmpty(paramDto.getTemplate_name()))
						paramDto.setTemplate_name(sharedNetwork.getName().concat(template));
					models_fromdb.add(paramDto);
					userDevicesSharedNetworksService.update(configs);
					//当前不可能有新设备应用新模板，所以返回false
					configChanged = false;
				}
			}else{
				paramDto.setTemplate(SharedNetworksHelper.DefaultTemplate);
				if(StringUtils.isEmpty(paramDto.getTemplate_name()))
					paramDto.setTemplate_name(SharedNetworksHelper.buildTemplateName(sharedNetwork, SharedNetworksHelper.DefaultTemplate));//sharedNetwork.getName().concat(DefaultTemplate));
				paramDto.setTs(System.currentTimeMillis());
				models_fromdb.clear();
				models_fromdb.add(paramDto);
				userDevicesSharedNetworksService.update(configs);
				configChanged = true;
			}*/
		}
		if(devicePartChanged)
			return SharedNetworkChangeType.SHARE_NETWORK_DEVICE_PART_CHANGED;
		return (configChanged)?SharedNetworkChangeType.SHARE_NETWORK_CHANGED:SharedNetworkChangeType.SHARE_NETWORK_NOT_CHANGED;
	}
	
	
	/**
	 * 获取用户关于特定的共享网络的配置内容
	 * 如果为空则采用缺省值构建
	 * @param uid
	 * @param sharednetwork_type
	 */
	public List<ParamSharedNetworkDTO> fetchAllUserSharedNetworkConf(int uid,VapEnumType.SharedNetworkType sharedNetwork){
		UserDevicesSharedNetworks configs = userDevicesSharedNetworksService.getById(uid);
		//paramDto = ParamSharedNetworkDTO.fufillWithDefault(paramDto);
		if(configs == null){
			configs = SharedNetworksHelper.buildDefaultUserDevicesSharedNetworks(uid, sharedNetwork, SharedNetworksHelper.DefaultTemplate);
			/*configs = new UserDevicesSharedNetworks();
			configs.setId(uid);
			List<ParamSharedNetworkDTO> sharedNetworkType_models = new ArrayList<ParamSharedNetworkDTO>();
			sharedNetworkType_models.add(ParamSharedNetworkDTO.builderDefault(sharedNetwork.getKey()));
			configs.put(sharedNetwork.getKey(), sharedNetworkType_models);*/
			userDevicesSharedNetworksService.insert(configs);
		}else{
			List<ParamSharedNetworkDTO> models = configs.get(sharedNetwork.getKey(),new ArrayList<ParamSharedNetworkDTO>(),true);
			if(models.isEmpty()){
				ParamSharedNetworkDTO dto = ParamSharedNetworkDTO.builderDefault(sharedNetwork.getKey());
				dto.setTemplate(SharedNetworksHelper.DefaultTemplate);
				dto.setTemplate_name(SharedNetworksHelper.buildTemplateName(sharedNetwork, SharedNetworksHelper.DefaultTemplate));//sharedNetwork.getName().concat(DefaultTemplate));
				dto.setTs(System.currentTimeMillis());
				models.add(dto);
				userDevicesSharedNetworksService.update(configs);
			}
		}
		return configs.get(sharedNetwork.getKey());
	}
	
	public Map<String,List<ParamSharedNetworkDTO>> fetchAllUserSharedNetworksConf(int uid){
		Map<String,List<ParamSharedNetworkDTO>> result = new LinkedHashMap<>();
		
		UserDevicesSharedNetworks configs = userDevicesSharedNetworksService.getById(uid);
		boolean needInsert = false;
		boolean changed = false;
		if(configs == null){
			configs = new UserDevicesSharedNetworks();
			configs.setId(uid);
			needInsert = true;
			changed = true;
		}
		SharedNetworkType[] snks = SharedNetworkType.values();
		for(SharedNetworkType snk:snks){
			List<ParamSharedNetworkDTO> sharedNetworkType_models = configs.get(snk.getKey(),new ArrayList<ParamSharedNetworkDTO>(),true);
			if(sharedNetworkType_models.isEmpty()){
				ParamSharedNetworkDTO dto = ParamSharedNetworkDTO.builderDefault(snk.getKey());
				dto.setTs(System.currentTimeMillis());
				dto.setTemplate(SharedNetworksHelper.DefaultTemplate);
				dto.setTemplate_name(SharedNetworksHelper.buildTemplateName(snk,SharedNetworksHelper.DefaultTemplate));//sharedNetwork.getName().concat(template));
				//List<ParamSharedNetworkDTO> sharedNetworkType_models = new ArrayList<ParamSharedNetworkDTO>();
				sharedNetworkType_models.add(dto);
				configs.put(snk.getKey(), sharedNetworkType_models);
				changed = true;
			}
			result.put(snk.getKey(), sharedNetworkType_models);
		}
		if(needInsert){
			userDevicesSharedNetworksService.insert(configs);
		}else{
			if(changed){
				userDevicesSharedNetworksService.update(configs);
			}
		}
		return result;
		/*//paramDto = ParamSharedNetworkDTO.fufillWithDefault(paramDto);
		if(configs == null){
			//configs = SharedNetworksHelper.buildDefaultUserDevicesSharedNetworks(uid, SharedNetworkType.SafeSecure, SharedNetworksHelper.DefaultTemplate);
			configs = SharedNetworksHelper.buildDefaultUserDevicesSharedNetworksWhenIsNullOrEmpty(uid,configs);
			userDevicesSharedNetworksService.insert(configs);
		}else{
			
			userDevicesSharedNetworksService.update(configs);
		}*/
		/*Map<String,List<ParamSharedNetworkDTO>> result = new LinkedHashMap<>();
		SharedNetworkType[] snks = SharedNetworkType.values();
		for(SharedNetworkType snk:snks){
			List<ParamSharedNetworkDTO> list_snk = configs.get(snk.getKey());
			if(list_snk == null)
				list_snk = new ArrayList<>();
			result.put(snk.getKey(), list_snk);
		}
		return result;*/
		//return configs.get(sharedNetwork.getKey());
	}
	
	/**
	 * 获取用户的指定的共享网络配置
	 * 如果不存在则建立缺省值或者返回index=0值
	 * @param uid
	 * @param sharedNetwork
	 * @return
	 */
	public ParamSharedNetworkDTO fetchUserSharedNetworkConf(int uid,VapEnumType.SharedNetworkType sharedNetwork,String template){
		if(StringUtils.isEmpty(template)) template = SharedNetworksHelper.DefaultTemplate;
		UserDevicesSharedNetworks configs = userDevicesSharedNetworksService.getById(uid);
		ParamSharedNetworkDTO dto = null;
		//paramDto = ParamSharedNetworkDTO.fufillWithDefault(paramDto);
		if(configs == null){
			configs = SharedNetworksHelper.buildDefaultUserDevicesSharedNetworks(uid, sharedNetwork, SharedNetworksHelper.DefaultTemplate);//.buildDefault(uid, sharedNetwork, DefaultTemplate);
			userDevicesSharedNetworksService.insert(configs);
			dto = configs.get(sharedNetwork.getKey()).get(0);
			/*configs = new UserDevicesSharedNetworks();
			configs.setId(uid);
			List<ParamSharedNetworkDTO> sharedNetworkType_models = new ArrayList<ParamSharedNetworkDTO>();
			dto = ParamSharedNetworkDTO.builderDefault(sharedNetwork.getKey());
			sharedNetworkType_models.add(dto);
			configs.put(sharedNetwork.getKey(), sharedNetworkType_models);
			userDevicesSharedNetworksService.insert(configs);*/
		}else{
			List<ParamSharedNetworkDTO> models = configs.get(sharedNetwork.getKey(),new ArrayList<ParamSharedNetworkDTO>(),true);
			if(models.isEmpty()){
				dto = ParamSharedNetworkDTO.builderDefault(sharedNetwork.getKey());
				dto.setTemplate(SharedNetworksHelper.DefaultTemplate);
				dto.setTemplate_name(SharedNetworksHelper.buildTemplateName(sharedNetwork, SharedNetworksHelper.DefaultTemplate));//sharedNetwork.getName().concat(DefaultTemplate));
				dto.setTs(System.currentTimeMillis());
				models.add(dto);
				userDevicesSharedNetworksService.update(configs);
			}else{
				ParamSharedNetworkDTO temp = new ParamSharedNetworkDTO();
				temp.setTemplate(template);
				int index = models.indexOf(temp);
				if(index != -1){
					dto = models.get(index);
				}else{
					dto = models.get(0);
				}
			}
		}
		return dto;
	}
	
	public ParamSharedNetworkDTO fetchUserSharedNetworkConf(int uid,VapEnumType.SharedNetworkType sharedNetwork){
		return fetchUserSharedNetworkConf(uid,sharedNetwork,SharedNetworksHelper.DefaultTemplate);
	}
	public WifiDeviceSharedNetwork fetchDeviceSharedNetwork(String mac){
		return wifiDeviceSharedNetworkService.getById(mac);
	}
	
	public SharedNetworkSettingDTO fetchDeviceSharedNetworkConf(String mac){
		WifiDeviceSharedNetwork configs = fetchDeviceSharedNetwork(mac);
		if(configs != null){
			return configs.getInnerModel();
		}else{
			return null;
		}
	}
	
	/**
	 * 如果此设备未开启共享网络则开启指定用户的共享网络存储配置，不过是关闭的，并存储到设备配置中
	 * @param uid
	 * @param mac
	 * @return
	 */
	public SharedNetworkSettingDTO fetchDeviceSharedNetworkConfIfEmptyThenCreate(int uid,String mac){
		WifiDeviceSharedNetwork configs = wifiDeviceSharedNetworkService.getById(mac);
		if(configs != null){
			return configs.getInnerModel();
		}else{
			ParamSharedNetworkDTO sharedNetworkConf = this.fetchUserSharedNetworkConf(uid, SharedNetworkType.SafeSecure);
			configs  = new WifiDeviceSharedNetwork();
			configs.setId(mac);
			configs.setSharednetwork_type(sharedNetworkConf.getNtype());
			configs.setTemplate(sharedNetworkConf.getTemplate());
			SharedNetworkSettingDTO sharedNetworkSettingDTO = new SharedNetworkSettingDTO();
			sharedNetworkSettingDTO.turnOff(sharedNetworkConf);
			configs.putInnerModel(sharedNetworkSettingDTO);
			wifiDeviceSharedNetworkService.insert(configs);
			return sharedNetworkSettingDTO;
		}
	}
	/*public ParamSharedNetworkDTO fetchDeviceSharedNetworkConf(String mac,int switchAct){
		ParamSharedNetworkDTO vw_dto = null;
		SharedNetworkSettingDTO sharedNetworkConf = fetchDeviceSharedNetworkConf(mac);
		if(sharedNetworkConf != null && sharedNetworkConf.isOn() && sharedNetworkConf.getPsn() != null){
			vw_dto = sharedNetworkConf.getPsn();
			vw_dto.switchWorkMode(switchAct);
		}
		return vw_dto;
	}*/
	/*public ParamSharedNetworkDTO fetchDeviceSharedNetworkConfAndSwitchWorkmode(String mac,int switchAct){
		ParamSharedNetworkDTO vw_dto = null;
		SharedNetworkSettingDTO sharedNetworkConf = fetchDeviceSharedNetworkConf(mac);
		if(sharedNetworkConf != null && sharedNetworkConf.isOn() && sharedNetworkConf.getPsn() != null){
			vw_dto = sharedNetworkConf.getPsn();
			vw_dto.switchWorkMode(switchAct);
		}
		return vw_dto;
	}*/
	
	/**
	 * 获取设备当前的配置，如果不存在则创建新的缺省关闭配置
	 * 为了考虑效率，如果此设备有绑定用户的话，不会以绑定用户的个人共享网络配置为准，少操作一次数据库
	 * 只以设备配置的数据为准
	 * 目前只为设备上线需要调用时处理
	 * @param mac
	 * @param notExistThenOn true 不存在 则创建开启的共享网络  false 不存在 则创建关闭的共享网络
	 * @param existIfOnFalseAndDsFalseThenOn true 存在并且on=false ds=false 则开启的共享网络  false 忽略
	 * @return
	 */
	public SharedNetworkSettingDTO fetchDeviceSharedNetworkConfWhenEmptyThenCreate(String mac,boolean notExistThenOn,boolean existIfOnFalseAndDsFalseThenOn){
		String mac_lowercase = mac.toLowerCase();
		WifiDeviceSharedNetwork sharednetwork = wifiDeviceSharedNetworkService.getById(mac_lowercase);
		if(sharednetwork != null){
			SharedNetworkSettingDTO settingDto = sharednetwork.getInnerModel();
			if(existIfOnFalseAndDsFalseThenOn ){
				if(!settingDto.isOn() && !settingDto.isDs() && settingDto.getPsn() != null){//这种情况一般是代表提取设备的共享网络不存在建立的关闭的共享网络
					settingDto.turnOn(settingDto.getPsn());
					sharednetwork.replaceInnerModel(settingDto);
					wifiDeviceSharedNetworkService.update(sharednetwork);
				}
			}
			return settingDto;
		}else{
			sharednetwork = new WifiDeviceSharedNetwork();
			sharednetwork.setId(mac_lowercase);
			ParamSharedNetworkDTO configDto = ParamSharedNetworkDTO.builderDefault(SharedNetworkType.SafeSecure.getKey());
			configDto.setTemplate(SharedNetworksHelper.DefaultTemplate);
			configDto.setTemplate_name(SharedNetworksHelper.buildTemplateName(SharedNetworkType.SafeSecure, SharedNetworksHelper.DefaultTemplate));//SharedNetworkType.SafeSecure.getName().concat(DefaultTemplate));
			configDto.setTs(System.currentTimeMillis());
			sharednetwork.setSharednetwork_type(configDto.getNtype());
			sharednetwork.setTemplate(SharedNetworksHelper.DefaultTemplate);
			SharedNetworkSettingDTO sharedNetworkSettingDTO = new SharedNetworkSettingDTO();
			if(notExistThenOn)
				sharedNetworkSettingDTO.turnOn(configDto);
			else
				sharedNetworkSettingDTO.turnOff(configDto);
			sharednetwork.putInnerModel(sharedNetworkSettingDTO);
			wifiDeviceSharedNetworkService.insert(sharednetwork);
			return sharedNetworkSettingDTO;
		}
	}
	
	/**
	 * 添加设备应用指定的配置
	 * 如果用户不存在此配置，需要建立新的用户配置
	 * 如果配置不存在则需要新建缺省的进去
	 * 此接口在后台backend执行,主要配合doApplySharedNetworksConfig 和 修改一个或多个设备的共享网络配置的后续执行
	 * 如果相关设备关闭了，则需要重新开启
	 * @param uid
	 * @param sharednetwork_type
	 * @param sharednetworkMatched true 进行比对，只有sharednetwork_type相同的配置才会更新 false 都更新
	 * @param macs
	 * @return 配置变更了的具体设备地址集合
	 */
	public void addDevices2SharedNetwork(int uid,
			VapEnumType.SharedNetworkType sharednetwork_type,String template,
			boolean sharednetworkMatched,
			List<String> macs,ISharedNetworkNotifyCallback callback){
		if(macs == null || macs.isEmpty()){
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_VALIDATE_EMPTY,new String[]{"macs"});
		}
		if(sharednetwork_type == null){
			sharednetwork_type = SharedNetworkType.SafeSecure;
		}
		List<String> result = new ArrayList<String>();
		//TODO：验证设备是否真实绑定,假定设备macs param 里面的数据真实存在
		//TODO：等设备版本升级上来后可以去掉此条件约束
		if(SharedNetworkType.SafeSecure.getKey().equals(sharednetwork_type.getKey())){
			List<WifiDevice> wifiDevices = wifiDeviceService.findByIds(macs);
			for(WifiDevice device :wifiDevices){
				if(!WifiDeviceHelper.suppertedDeviceSecureSharedNetwork(device.getOrig_swver())){
					macs.remove(device.getId());
					//throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_VERSION_TOO_LOWER,new String[]{BusinessRuntimeConfiguration.Device_SharedNetwork_Top_Version});
				}
			}
		}
		ParamSharedNetworkDTO configDto = fetchUserSharedNetworkConf(uid,sharednetwork_type,template);
		//如果template不存在则返回的dto中是列表的第一个值
		template = configDto.getTemplate();
		for(String mac:macs){
			String mac_lowercase = mac.toLowerCase();
			WifiDeviceSharedNetwork sharednetwork = wifiDeviceSharedNetworkService.getById(mac_lowercase);
			if(sharednetwork == null){
				sharednetwork = new WifiDeviceSharedNetwork();
				sharednetwork.setId(mac_lowercase);
				sharednetwork.setSharednetwork_type(configDto.getNtype());
				sharednetwork.setTemplate(template);
				SharedNetworkSettingDTO sharedNetworkSettingDTO = new SharedNetworkSettingDTO();
				sharedNetworkSettingDTO.turnOn(configDto);
				sharednetwork.putInnerModel(sharedNetworkSettingDTO);
				wifiDeviceSharedNetworkService.insert(sharednetwork);
				result.add(mac_lowercase);
			}else{
				if(sharednetworkMatched){
					if(!sharednetwork_type.getKey().equals(sharednetwork.getSharednetwork_type())){
						continue;
					}
				}
				sharednetwork.setSharednetwork_type(configDto.getNtype());
				sharednetwork.setTemplate(template);
				SharedNetworkSettingDTO sharedNetworkSettingDTO = sharednetwork.getInnerModel();
				
				sharedNetworkSettingDTO.turnOn(configDto);
				sharednetwork.replaceInnerModel(sharedNetworkSettingDTO);
				wifiDeviceSharedNetworkService.update(sharednetwork);
				result.add(mac_lowercase);
				/*
				 	ParamSharedNetworkDTO dbDto = sharedNetworkSettingDTO.getPsn();
					if(dbDto == null || ParamSharedNetworkDTO.wasChanged(configDto, dbDto)){
					sharedNetworkSettingDTO.turnOn(configDto);
					sharednetwork.replaceInnerModel(sharedNetworkSettingDTO);
					wifiDeviceSharedNetworkService.update(sharednetwork);
					result.add(mac_lowercase);
				}else{
					;
				}*/
			}
		}
		if(callback != null){
			callback.notify(configDto, result);
		}
		//return result;
	}
	
	/**
	 * 单个设备设置共享网络，不更新用户共享网络配置
	 * 目前是有taskfacade 老接口调用
	 * @param mac
	 * @param configDto
	 * @return
	 */
	public boolean updateDevices2SharedNetwork(String mac,ParamSharedNetworkDTO configDto){
		boolean wasUpdated = false;
		String mac_lowercase = mac.toLowerCase();
		WifiDeviceSharedNetwork sharednetwork = wifiDeviceSharedNetworkService.getById(mac_lowercase);
		if(sharednetwork == null){
			sharednetwork = new WifiDeviceSharedNetwork();
			sharednetwork.setId(mac_lowercase);
			sharednetwork.setSharednetwork_type(configDto.getNtype());
			sharednetwork.setTemplate(configDto.getTemplate());
			SharedNetworkSettingDTO sharedNetworkSettingDTO = new SharedNetworkSettingDTO();
			sharedNetworkSettingDTO.turnOn(configDto);
			sharednetwork.putInnerModel(sharedNetworkSettingDTO);
			wifiDeviceSharedNetworkService.insert(sharednetwork);
			wasUpdated = true;
		}else{
			sharednetwork.setSharednetwork_type(configDto.getNtype());
			sharednetwork.setTemplate(configDto.getTemplate());
			SharedNetworkSettingDTO sharedNetworkSettingDTO = sharednetwork.getInnerModel();
			sharedNetworkSettingDTO.turnOn(configDto);
			sharednetwork.replaceInnerModel(sharedNetworkSettingDTO);
			wifiDeviceSharedNetworkService.update(sharednetwork);
			wasUpdated = true;
		}
		return wasUpdated;
	}
	
	
	/**
	 * 更新指定设备的打赏portal金额范围和时长
	 * @param mac
	 * @param rang_pc
	 * @param rang_m
	 * @param ait_pc
	 * @param ait_m
	 * @param fait_pc
	 * @param fait_m
	 */
	public void updateDevicesSharedNetworkTimeMoneyControl(String mac, String range_pc, String range_m, String ait_pc, String ait_m, String fait_pc, String fait_m){
		String mac_lowercase = mac.toLowerCase();
		WifiDeviceSharedNetwork sharednetwork = wifiDeviceSharedNetworkService.getById(mac_lowercase);
		//需要先有配置才更新。目前暂时这样。后续如果有从unicorn更新此数据的需求，再和产品部讨论具体逻辑
		if(sharednetwork == null)
			return;
		SharedNetworkSettingDTO sharedNetworkSettingDTO = sharednetwork.getInnerModel();
		ParamSharedNetworkDTO psn = sharedNetworkSettingDTO.getPsn();
		psn.setRange_cash_mobile(range_m);
		psn.setRange_cash_pc(range_pc);
		psn.setAit_mobile(ait_m);
		psn.setAit_pc(ait_pc);
		psn.setFree_ait_mobile(fait_m);
		psn.setFree_ait_pc(fait_pc);
		sharednetwork.replaceInnerModel(sharedNetworkSettingDTO);
		wifiDeviceSharedNetworkService.update(sharednetwork);
	}
	
	
	/**
	 * 移除设备从指定的配置
	 * 不需要置Sharednetwork_type null
	 * 需要置turnOff
	 * 用于用户关闭共享网络
	 * @param macs
	 * @return 配置变更了的具体设备地址集合
	 */
	public List<String> removeDevicesFromSharedNetwork(String... macs){
		List<String> result = new ArrayList<String>();
		for(String mac:macs){
			String mac_lowercase = mac.toLowerCase();
			WifiDeviceSharedNetwork sharednetwork = wifiDeviceSharedNetworkService.getById(mac_lowercase);
			if(sharednetwork != null){
				//sharednetwork.setSharednetwork_type(null);
				SharedNetworkSettingDTO sharedNetworkSettingDTO = sharednetwork.getInnerModel();
				sharedNetworkSettingDTO.turnOff();
				sharednetwork.putInnerModel(sharedNetworkSettingDTO);
				wifiDeviceSharedNetworkService.update(sharednetwork);
				result.add(mac_lowercase);
			}
		}
		return result;
	}
	
	public void closeAndApplyDevicesFromSharedNetwork(int uid,
			VapEnumType.SharedNetworkType sharednetwork_type,String template,List<String> macs,
			ISharedNetworkNotifyCallback callback){
		List<String> result = new ArrayList<String>();
		ParamSharedNetworkDTO configDto = fetchUserSharedNetworkConf(uid,sharednetwork_type,template);
		for(String mac:macs){
			String mac_lowercase = mac.toLowerCase();
			WifiDeviceSharedNetwork sharednetwork = wifiDeviceSharedNetworkService.getById(mac_lowercase);
			sharednetwork.setSharednetwork_type(configDto.getNtype());
			sharednetwork.setTemplate(configDto.getTemplate());
			if(sharednetwork != null){
				//sharednetwork.setSharednetwork_type(null);
				SharedNetworkSettingDTO sharedNetworkSettingDTO = sharednetwork.getInnerModel();
				sharedNetworkSettingDTO.setPsn(configDto);
				sharedNetworkSettingDTO.turnOff();
				sharednetwork.putInnerModel(sharedNetworkSettingDTO);
				//sharednetwork.putInnerModel(configDto);
				wifiDeviceSharedNetworkService.update(sharednetwork);
				result.add(mac_lowercase);
			}
		}
		if(callback != null){
			callback.notify(configDto, result);
		}
	}
	
	public void remoteResponseNotifyFromDevice(String mac){
		WifiDeviceSharedNetwork sharedNetwork = wifiDeviceSharedNetworkService.getById(mac);
		if(sharedNetwork != null){
			SharedNetworkSettingDTO settingDto = sharedNetwork.getInnerModel();
			if(!settingDto.isDs()){
				settingDto.remoteNotify();
				sharedNetwork.putInnerModel(settingDto);
				wifiDeviceSharedNetworkService.update(sharedNetwork);
			}
		}
	}
	
	
	public void deivceUnbinded(String mac){
		wifiDeviceSharedNetworkService.deleteById(mac);
	}
	
	public void deivceReset(String mac){
		wifiDeviceSharedNetworkService.deleteById(mac);
	}
	
	public List<SharedNetworkVTO> fetchSupportedSharedNetwork(){
		return SharedNetworkType.getSharedNetworkVtos();
	}

	public UserService getUserService() {
		return userService;
	}

	public WifiDeviceService getWifiDeviceService() {
		return wifiDeviceService;
	}

	public WifiDeviceSharedNetworkService getWifiDeviceSharedNetworkService() {
		return wifiDeviceSharedNetworkService;
	}

	public UserDevicesSharedNetworksService getUserDevicesSharedNetworksService() {
		return userDevicesSharedNetworksService;
	}

/*	public UserDeviceService getUserDeviceService() {
		return userDeviceService;
	}*/
	
	
}
