package com.bhu.vas.business.ds.device.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.ret.param.ParamVasModuleDTO;
import com.bhu.vas.api.dto.ret.param.ParamVasSwitchWorkmodeDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingDTO;
import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.api.helper.DeviceHelper;
import com.bhu.vas.api.helper.IGenerateDeviceSetting;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.OperationDS;
import com.bhu.vas.api.helper.WifiDeviceHelper;
import com.bhu.vas.api.rpc.devices.dto.PersistenceCMDDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.model.WifiDevicePersistenceCMDState;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSetting;
import com.bhu.vas.api.rpc.task.model.VasModuleCmdDefined;
import com.bhu.vas.api.rpc.task.model.pk.VasModuleCmdPK;
import com.bhu.vas.business.ds.device.service.WifiDevicePersistenceCMDStateService;
import com.bhu.vas.business.ds.device.service.WifiDeviceSettingService;
import com.bhu.vas.business.ds.task.service.VasModuleCmdDefinedService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class DeviceCMDGenFacadeService implements IGenerateDeviceSetting{
	//private final Logger logger = LoggerFactory.getLogger(DeviceCMDGenFacadeService.class);
	
	@Resource
	private WifiDeviceSettingService wifiDeviceSettingService;
	
	@Resource
	private WifiDevicePersistenceCMDStateService wifiDevicePersistenceCMDStateService;
	
	@Resource
	private VasModuleCmdDefinedService vasModuleCmdDefinedService;
	
	@Resource
	private SharedNetworksFacadeService sharedNetworksFacadeService;
	
	@Resource
	private DeviceFacadeService deviceFacadeService;
	/**************************  具体业务修改配置数据 封装 **********************************/

	
	/**
	 * 生成设备配置的广告配置数据
	 * @param mac
	 * @param ods 修改设备配置的ds_opt
	 * @param extparams 修改配置具体的参数
	 * @return
	 * @throws Exception 
	 */
	public String generateDeviceSetting(String mac, OperationDS ods, String extparams) throws Exception {
		if(ods == null)
			throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
		String config_sequence = DeviceHelper.Common_Config_Sequence;
		switch(ods){
			case DS_Http_Ad_Start:
				return DeviceHelper.builderDSHttpAdStartOuter(config_sequence, extparams);
			case DS_Http_Ad_Stop:
				return DeviceHelper.builderDSHttpAdStopOuter(config_sequence);	
			case DS_SharedNetworkWifi_Start:
				return DeviceHelper.builderDSStartSharedNetworkWifiOuter(extparams);
			case DS_SharedNetworkWifi_Stop:
				return DeviceHelper.builderDSStopSharedNetworkWifiOuter();
			case DS_Plugins:
				return DeviceHelper.builderDSPluginOuter(extparams);
			case DS_Switch_WorkMode:
				return generateDeviceSettingWithSwitchWorkMode(mac, extparams);
			case DS_Power:
				return DeviceHelper.builderDSPowerOuter(config_sequence, extparams, validateDeviceSettingReturnDTO(mac));
			case DS_RealChannel:
				return DeviceHelper.builderDSRealChannelOuter(config_sequence, extparams, validateDeviceSettingReturnDTO(mac));
			case DS_VapPassword:
				return DeviceHelper.builderDSVapPasswordOuter(config_sequence, extparams, validateDeviceSettingReturnDTO(mac));
			case DS_AclMacs:
				return DeviceHelper.builderDSAclMacsOuter(config_sequence, extparams, validateDeviceSettingReturnDTO(mac));
			case DS_RateControl:
				return DeviceHelper.builderDSRateControlOuter(config_sequence, extparams, validateDeviceSettingReturnDTO(mac));
			case DS_AdminPassword:
				return DeviceHelper.builderDSAdminPasswordOuter(config_sequence, extparams);
			case DS_LinkMode:
				return DeviceHelper.builderDSLinkModeOuter(config_sequence, extparams);
			case DS_MM:
				return DeviceHelper.builderDSHDAliasOuter(config_sequence, extparams, validateDeviceSettingReturnDTO(mac));
			
//			case DS_VapGuest:
//				return DeviceHelper.builderDSVapGuestOuter(config_sequence, extparams, ds_dto);
			default:
				throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
		}
	}
	
	//ParamSharedNetworkDTO
	public String generateDeviceSettingWithSwitchWorkMode(String mac, String extparams){
		ParamVasSwitchWorkmodeDTO wk_dto = JsonHelper.getDTO(extparams, ParamVasSwitchWorkmodeDTO.class);
		int switchAct = wk_dto.getWmode();
		if(switchAct == WifiDeviceHelper.SwitchMode_Router2Bridge_Act || switchAct == WifiDeviceHelper.SwitchMode_Bridge2Router_Act){
			ParamSharedNetworkDTO vw_dto = sharedNetworksFacadeService.fetchDeviceSharedNetworkConfAndSwitchWorkmode(mac, switchAct);
			/*SharedNetworkSettingDTO sharedNetworkConf = sharedNetworkFacadeService.fetchDeviceSharedNetworkConf(mac);
			if(sharedNetworkConf != null && sharedNetworkConf.isOn() && sharedNetworkConf.getPsn() != null){
				vw_dto = sharedNetworkConf.getPsn();
				vw_dto.switchWorkMode(switchAct);
			}*/
			/*ParamVapVistorWifiDTO vw_dto = null;
			UserSettingState settingState = userSettingStateService.getById(mac);
			if(settingState != null){
				UserVistorWifiSettingDTO vistorWifi = settingState.getUserSetting(UserVistorWifiSettingDTO.Setting_Key, UserVistorWifiSettingDTO.class);
				if(vistorWifi != null && vistorWifi.isOn()){
					vw_dto = vistorWifi.getVw();
					//TODO:ParamVapVistorWifiDTO block_mode变更并且更新配置 或者数据库中就不存ParamVapVistorWifiDTO字段block_mode
					vw_dto.switchWorkMode(switchAct);
					//更新操作应该在设备切换工作模式后上线后，如果模式变更了，再更新状态
					//userSettingStateService.update(settingState);
				}
			}*/
			return DeviceHelper.builderDSWorkModeSwitchOuter(mac, switchAct, validateDeviceSettingReturnDTO(mac), vw_dto);
		}
		return null;
	}
	
	/**
	 * 验证设备是否加载正确配置
	 * @param mac
	 * @return
	 */
	public WifiDeviceSettingDTO validateDeviceSettingReturnDTO(String mac){
		WifiDeviceSetting entity = wifiDeviceSettingService.getById(mac);
		if(entity == null) {
			throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_SETTING_NOTEXIST);
		}
		WifiDeviceSettingDTO dto = entity.getInnerModel();
		if(dto == null) {
			throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_SETTING_ERROR);
		}
		return dto;
	}
	
	
	public List<String> fetchWifiDevicePersistenceVapModuleCMD(String mac){
		WifiDevicePersistenceCMDState cmdState = wifiDevicePersistenceCMDStateService.getById(mac);
		if(cmdState == null || cmdState.getExtension().isEmpty()) return null;
		List<String> payloads = null;
		List<OperationDS> vap_module_ds = null;
		List<String> vap_module_ds_extparams = null;
		try{
			payloads = new ArrayList<>();
			Set<Entry<String, PersistenceCMDDTO>> entrySet = cmdState.getExtension().entrySet();
			for(Entry<String, PersistenceCMDDTO> entry : entrySet){
				PersistenceCMDDTO dto = entry.getValue();
				OperationCMD opt_cmd = OperationCMD.getOperationCMDFromNo(dto.getOpt());
				if(opt_cmd == null){// || StringUtils.isEmpty(dto.getExtparams())){
					continue;
				}
				OperationDS ods_cmd = OperationDS.getOperationDSFromNo(dto.getSubopt());
				if(OperationCMD.ModifyDeviceSetting == opt_cmd){
					if(ods_cmd == null) continue;
					if(WifiDeviceHelper.isVapCmdModuleSupported(opt_cmd,ods_cmd)){// && WifiDeviceHelper.isVapModuleSupported(wifiDevice.getOrig_swver())){
						if(vap_module_ds == null) vap_module_ds = new ArrayList<>();
						if(vap_module_ds_extparams == null) vap_module_ds_extparams = new ArrayList<>();
						if(OperationDS.DS_Http_VapModuleCMD_Start == ods_cmd){
							ParamVasModuleDTO param_dto = JsonHelper.getDTO(dto.getExtparams(), ParamVasModuleDTO.class);
							if(param_dto == null || StringUtils.isEmpty(param_dto.getStyle()))
								continue;
							VasModuleCmdDefined cmdDefined = vasModuleCmdDefinedService.getById(new VasModuleCmdPK(ods_cmd.getRef(),param_dto.getStyle()));
							if(cmdDefined == null || StringUtils.isEmpty(cmdDefined.getTemplate())){
								continue;
							}
							payloads.add(CMDBuilder.autoBuilderVapFullCMD4Opt(mac, CMDBuilder.auto_taskid_vapstart_fragment.getNextSequence(),cmdDefined.getTemplate()));
						}else if(OperationDS.DS_Http_VapModuleCMD_Stop == ods_cmd){
							String templated = vasModuleCmdDefinedService.fetchCommonStopTemplate();
							payloads.add(CMDBuilder.autoBuilderVapFullCMD4Opt(mac, CMDBuilder.auto_taskid_vapstop_fragment.getNextSequence(), templated));
						}else{
							vap_module_ds.add(ods_cmd);
							vap_module_ds_extparams.add(dto.getExtparams());
						}
					}
				}
			}
			/*//取消老的增值指令下发数据 20151023
			if(vap_module_ds != null && !vap_module_ds.isEmpty()){
				String cmd = CMDBuilder.autoBuilderVapCMD4Opt(OperationCMD.ModifyDeviceSetting,vap_module_ds.toArray(new OperationDS[0]),mac,
						CMDBuilder.auto_taskid_fragment.getNextSequence(),vap_module_ds_extparams.toArray(new String[0]));
				if(StringUtils.isNotEmpty(cmd))
					payloads.add(cmd);
			}*/
			return payloads;
		}finally{
			if(vap_module_ds != null){
				vap_module_ds.clear();
				vap_module_ds = null;
			}
			if(vap_module_ds_extparams != null){
				vap_module_ds_extparams.clear();
				vap_module_ds_extparams = null;
			}
		}
	}
	
	/**
	 * 获取持久化指令中除vapmodule支持的增值指令的其他指令
	 * @param mac
	 * @return
	 */
	public List<String> fetchWifiDevicePersistenceExceptVapModuleCMD(String mac){
		WifiDevicePersistenceCMDState cmdState = wifiDevicePersistenceCMDStateService.getById(mac);
		if(cmdState == null || cmdState.getExtension().isEmpty()) return null;
		List<String> payloads = null;
		try{
			payloads = new ArrayList<>();
			Set<Entry<String, PersistenceCMDDTO>> entrySet = cmdState.getExtension().entrySet();
			StringBuilder sb_setting_inner = new StringBuilder();
			for(Entry<String, PersistenceCMDDTO> entry : entrySet){
				PersistenceCMDDTO dto = entry.getValue();
				OperationCMD opt_cmd = OperationCMD.getOperationCMDFromNo(dto.getOpt());
				if(opt_cmd == null || StringUtils.isEmpty(dto.getExtparams())){
					continue;
				}
				OperationDS ods_cmd = OperationDS.getOperationDSFromNo(dto.getSubopt());
				if(OperationCMD.ModifyDeviceSetting == opt_cmd){
					if(ods_cmd == null) continue;
					switch(ods_cmd){
						case DS_Http_Ad_Start:
							sb_setting_inner.append(DeviceHelper.builderDSHttpAdStartFragmentOuter(dto.getExtparams()));
							break;	
						/*case DS_Http_404_Start:
							sb_setting_inner.append(DeviceHelper.builderDSHttp404StartFragmentOuter(dto.getExtparams()));
							break;	
						case DS_Http_Redirect_Start:
							sb_setting_inner.append(DeviceHelper.builderDSHttpRedirectStartFragmentOuter(dto.getExtparams()));
							break;*/	
						default:
							break;
					}
					//}
				}else{
					payloads.add(CMDBuilder.autoBuilderCMD4Opt(opt_cmd, ods_cmd, mac,0, dto.getExtparams(), this));
				}
			}
			if(sb_setting_inner.length() > 0){
				WifiDeviceSetting entity = deviceFacadeService.validateDeviceSetting(mac);
				WifiDeviceSettingDTO ds_dto = entity.getInnerModel();
				String config_sequence = DeviceHelper.getConfigSequence(ds_dto);
				if(StringUtils.isEmpty(config_sequence))
					throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_SETTING_SEQUENCE_NOTEXIST);
				payloads.add(
						CMDBuilder.builderDeviceSettingModify(
								mac, 
								CMDBuilder.auto_taskid_fragment.getNextSequence(), 
								DeviceHelper.builderDSHttpVapSettinStartOuter(config_sequence,sb_setting_inner.toString())));
			}
			return payloads;
		}finally{
		}
	}
}
