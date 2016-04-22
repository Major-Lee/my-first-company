package com.bhu.vas.business.ds.device.facade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

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
import com.bhu.vas.business.ds.user.service.UserDeviceService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class SharedNetworksFacadeService {
	@Resource
	private UserService userService;

	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private WifiDeviceSharedNetworkService wifiDeviceSharedNetworkService;

	@Resource
	private UserDevicesSharedNetworksService userDevicesSharedNetworksService;

    @Resource
    private UserDeviceService userDeviceService;
	
    private static final String FormatTemplete = "%04d";
    //如果为DefaultCreateTemplate 则代表新建一个模板
    public static final String DefaultCreateTemplate = "0000";
    public static final String DefaultTemplate = "0001";
    private static final List<String> TemplateSequences = new ArrayList<>();
    static{
    	for(int i=1;i<BusinessRuntimeConfiguration.SharedNetworksTemplateMaxLimit+1;i++){
    		TemplateSequences.add(String.format(FormatTemplete, i));
    	}
    }
    
    public static boolean validDefaultCreateTemplateFormat(String template){
    	return DefaultCreateTemplate.equals(template);
    }
    
    public static boolean validTemplateFormat(String template){
    	if(StringUtils.isEmpty(template)) return false;
    	if(template.length() != 4) return false;
    	try{
    		Integer.parseInt(template);
    	}catch(NumberFormatException ex){
    		return false;
    	}
    	return true;
    }
    
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
	public boolean doApplySharedNetworksConfig(int uid,ParamSharedNetworkDTO paramDto){
		boolean configChanged = false;
		SharedNetworkType sharedNetwork = VapEnumType.SharedNetworkType.fromKey(paramDto.getNtype());
		paramDto = ParamSharedNetworkDTO.fufillWithDefault(paramDto);
		if(StringUtils.isEmpty(paramDto.getTemplate())){
			paramDto.setTemplate(DefaultTemplate);
		}
		/*if(StringUtils.isEmpty(paramDto.getTemplate_name())){
			paramDto.setTemplate_name(sharedNetwork.getName().concat(paramDto.getTemplate()));
		}*/
		
		UserDevicesSharedNetworks configs = userDevicesSharedNetworksService.getById(uid);
		//SharedNetworkType sharedNetwork = VapEnumType.SharedNetworkType.fromKey(paramDto.getNtype());
		//if(StringUtils.isNotEmpty(sharednetwork_dto.getTemplate_name())){
		//	sharednetwork_dto.setTemplate_name(SharedNetworkType.SafeSecure.getName().concat(sharednetwork_dto.getTemplate()));
		//}
		if(configs == null){
			configs = UserDevicesSharedNetworks.buildDefault(uid, paramDto);
			/*configs = new UserDevicesSharedNetworks();
			configs.setId(uid);
			List<ParamSharedNetworkDTO> models = new ArrayList<ParamSharedNetworkDTO>();
			paramDto.setTemplate(DefaultTemplate);
			models.add(paramDto);
			configs.put(paramDto.getNtype(), models);*/
			userDevicesSharedNetworksService.insert(configs);
			configChanged = true;
		}else{
			List<ParamSharedNetworkDTO> models_fromdb = configs.get(paramDto.getNtype(),new ArrayList<ParamSharedNetworkDTO>(),true);
			if(VapEnumType.SharedNetworkType.SafeSecure.getKey().equals(paramDto.getNtype())){
				//验证models_fromdb 是否存在 template编号,如果存在则替换，否则增加
				int index = models_fromdb.indexOf(paramDto);
				if(index != -1){
					ParamSharedNetworkDTO dto_fromdb = models_fromdb.get(index);
					
					if(ParamSharedNetworkDTO.wasConfigChanged(dto_fromdb, paramDto) || ParamSharedNetworkDTO.wasTemplateNameChanged(dto_fromdb, paramDto)){
						configChanged = true;
					}
					models_fromdb.set(index, paramDto);
					userDevicesSharedNetworksService.update(configs);
					/*if(ParamSharedNetworkDTO.wasConfigChanged(dto_fromdb, paramDto)){
						configChanged = true;
						models_fromdb.set(index, paramDto);
						userDevicesSharedNetworksService.update(configs);
					}else if(ParamSharedNetworkDTO.wasTemplateNameChanged(dto_fromdb, paramDto)){
						models_fromdb.set(index, paramDto);
						userDevicesSharedNetworksService.update(configs);
					}*/
					/*if(dto_fromdb == null || ParamSharedNetworkDTO.wasChanged(dto_fromdb, paramDto)){
						configChanged = true;
						models_fromdb.set(index, paramDto);
						userDevicesSharedNetworksService.update(configs);
					}else if(){
						//System.out.println("0hhhh:"+paramDto.getTemplate());
					}*/
				}else{
					//SafeSecure网络需要限制模板数量
					if(models_fromdb.size() >= BusinessRuntimeConfiguration.SharedNetworksTemplateMaxLimit){
						throw new BusinessI18nCodeException(ResponseErrorCode.USER_DEVICE_SHAREDNETWORK_TEMPLATES_MAXLIMIT,new String[]{String.valueOf(BusinessRuntimeConfiguration.SharedNetworksTemplateMaxLimit)});
					}
					String template = fetchValidTemplate(models_fromdb);
					paramDto.setTemplate(template);
					if(StringUtils.isEmpty(paramDto.getTemplate_name()))
						paramDto.setTemplate_name(sharedNetwork.getName().concat(template));
					models_fromdb.add(paramDto);
					userDevicesSharedNetworksService.update(configs);
					//当前不可能有新设备应用新模板，所以返回false
					configChanged = false;
				}
			}else{
				paramDto.setTemplate(DefaultTemplate);
				models_fromdb.clear();
				models_fromdb.add(paramDto);
				userDevicesSharedNetworksService.update(configs);
				configChanged = true;
			}
		}
		return configChanged;
	}
	
	@SuppressWarnings("unchecked")
	public String fetchValidTemplate(List<ParamSharedNetworkDTO> models_fromdb){
		Set<String> templates_fromdb = null;
		Collection<String> subtract = null;
		List<String> tmp = null;
		try{
			if(!models_fromdb.isEmpty()){
				templates_fromdb = new HashSet<String>();
				for(ParamSharedNetworkDTO dto:models_fromdb){
					templates_fromdb.add(dto.getTemplate());
				}
				subtract = CollectionUtils.subtract(TemplateSequences, templates_fromdb);
				System.out.println("valid templates:"+subtract);
				if(!subtract.isEmpty()){
					tmp = new ArrayList<String>(subtract);
					return tmp.get(0);
				}
			}else{
				return DefaultTemplate;
			}
		}finally{
			if(templates_fromdb != null){
				templates_fromdb.clear();
				templates_fromdb = null;
			}
			if(tmp != null){
				tmp.clear();
				tmp = null;
			}
			if(subtract != null){
				subtract.clear();
				subtract = null;
			}
		}
		return null;
	}
	
	/**
	 * 获取用户关于共享网络的配置
	 * 如果为空则采用缺省值构建
	 * @param uid
	 * @param sharednetwork_type
	 */
	public List<ParamSharedNetworkDTO> fetchAllUserSharedNetworkConf(int uid,VapEnumType.SharedNetworkType sharedNetwork){
		UserDevicesSharedNetworks configs = userDevicesSharedNetworksService.getById(uid);
		//paramDto = ParamSharedNetworkDTO.fufillWithDefault(paramDto);
		if(configs == null){
			configs = UserDevicesSharedNetworks.buildDefault(uid, sharedNetwork, DefaultTemplate);
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
				dto.setTemplate(DefaultTemplate);
				dto.setTemplate_name(sharedNetwork.getName().concat(DefaultTemplate));
				models.add(dto);
				userDevicesSharedNetworksService.update(configs);
			}
		}
		return configs.get(sharedNetwork.getKey());
	}
	
	/**
	 * 获取用户的指定的共享网络配置
	 * 如果不存在则建立缺省值或者返回index=0值
	 * @param uid
	 * @param sharedNetwork
	 * @return
	 */
	public ParamSharedNetworkDTO fetchUserSharedNetworkConf(int uid,VapEnumType.SharedNetworkType sharedNetwork,String template){
		if(StringUtils.isEmpty(template)) template = DefaultTemplate;
		UserDevicesSharedNetworks configs = userDevicesSharedNetworksService.getById(uid);
		ParamSharedNetworkDTO dto = null;
		//paramDto = ParamSharedNetworkDTO.fufillWithDefault(paramDto);
		if(configs == null){
			configs = UserDevicesSharedNetworks.buildDefault(uid, sharedNetwork, DefaultTemplate);
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
				dto.setTemplate(DefaultTemplate);
				dto.setTemplate_name(sharedNetwork.getName().concat(DefaultTemplate));
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
		return fetchUserSharedNetworkConf(uid,sharedNetwork,DefaultTemplate);
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
			configDto.setTemplate(DefaultTemplate);
			configDto.setTemplate_name(SharedNetworkType.SafeSecure.getName().concat(DefaultTemplate));
			sharednetwork.setSharednetwork_type(configDto.getNtype());
			sharednetwork.setTemplate(DefaultTemplate);
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
		if(sharednetwork_type == null){
			sharednetwork_type = SharedNetworkType.SafeSecure;
		}
		if(macs == null || macs.isEmpty()){
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_VALIDATE_EMPTY,new String[]{"macs"});
		}
		List<String> result = new ArrayList<String>();
		ParamSharedNetworkDTO configDto = fetchUserSharedNetworkConf(uid,sharednetwork_type,template);
		//如果template不存在则返回的dto中是列表的第一个值
		template = configDto.getTemplate();
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

	public UserDeviceService getUserDeviceService() {
		return userDeviceService;
	}
	
	
}
