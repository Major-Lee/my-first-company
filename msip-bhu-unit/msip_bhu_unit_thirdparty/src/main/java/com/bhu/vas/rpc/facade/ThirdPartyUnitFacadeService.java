package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.elasticsearch.common.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingAclDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingRadioDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingVapDTO;
import com.bhu.vas.api.helper.DeviceHelper;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.OperationDS;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.charging.model.WifiDeviceSharedealConfigs;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSetting;
import com.bhu.vas.api.rpc.task.dto.TaskResDTO;
import com.bhu.vas.api.rpc.task.iservice.ITaskRpcService;
import com.bhu.vas.api.rpc.task.model.WifiDeviceDownTask;
import com.bhu.vas.api.rpc.thirdparty.dto.GomeConfigDTO;
import com.bhu.vas.api.rpc.thirdparty.dto.GomeDeviceDTO;
import com.bhu.vas.api.rpc.thirdparty.dto.GomeDeviceStaDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetAliasService;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetUnitPresentSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.handset.HandsetStorageFacadeService;
import com.bhu.vas.business.bucache.redis.serviceimpl.thirdparty.ThirdPartyDeviceService;
import com.bhu.vas.business.ds.charging.service.WifiDeviceSharedealConfigsService;
import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.device.service.WifiDeviceSettingService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.helper.encrypt.JNIRsaHelper;
import com.smartwork.msip.cores.plugins.dictparser.impl.mac.MacDictParserFilterHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

import redis.clients.jedis.Tuple;

/**
 * 第三方业务的service
 * @author yetao
 *
 */
@Service
public class ThirdPartyUnitFacadeService {
	private final static Logger logger = LoggerFactory.getLogger(ThirdPartyUnitFacadeService.class);

	@Resource
	private ITaskRpcService taskRpcService;

	@Resource 
	private WifiDeviceSharedealConfigsService wifiDeviceSharedealConfigsService;

	@Resource
	private WifiDeviceSettingService wifiDeviceSettingService;
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private DeviceFacadeService deviceFacadeService;
	
	/**
	 * 绑定国美设备，需要校验设备一级分销商是国美，才允许绑定
	 * @param mac
	 * @return
	 */
	public Boolean gomeBindDevice(String mac){
		if(ThirdPartyDeviceService.isThirdPartyDevice(mac)){
			ThirdPartyDeviceService.bindDevice(mac);
			return Boolean.TRUE;
		} else {
			WifiDeviceSharedealConfigs wifiDeviceSharedeal = wifiDeviceSharedealConfigsService.getById(mac);
			if(wifiDeviceSharedeal == null || wifiDeviceSharedeal.getDistributor() != BusinessRuntimeConfiguration.GomeDistributorId)
				throw new BusinessI18nCodeException(ResponseErrorCode.DEVICE_DATA_NOT_EXIST);
			ThirdPartyDeviceService.bindDevice(mac);
		}
		return Boolean.TRUE;
	}
	
	/**
	 * 解绑国美设备，只有已经再redis中存在的数据，才会解绑
	 * @param mac
	 * @return
	 */
	public Boolean gomeUnBindDevice(String mac){
		if(ThirdPartyDeviceService.isThirdPartyDevice(mac))
			ThirdPartyDeviceService.unBindDevice(mac);
		return Boolean.TRUE;
	}
	
	
	
	private void _callTaskCreate(String mac, String subopt, Object obj){
		String extparams = JsonHelper.getJSONString(obj);
		try{
			RpcResponseDTO<TaskResDTO> result = taskRpcService.createNewTask(BusinessRuntimeConfiguration.Sys_Uid, mac,
					OperationCMD.ModifyDeviceSetting.getNo(), subopt, extparams, WifiDeviceDownTask.Task_LOCAL_CHANNEL, null);
			if(result.hasError()){
				throw new BusinessI18nCodeException(result.getErrorCode());
			}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * 国美设备控制接口，需要校验是否国美设备
	 * @param mac
	 * @param dto
	 * @return
	 */
	public Boolean gomeDeviceControl(String mac, GomeConfigDTO dto){

		if(!ThirdPartyDeviceService.isThirdPartyDevice(mac)){
			throw new BusinessI18nCodeException(ResponseErrorCode.DEVICE_DATA_NOT_EXIST);
		}
		
		WifiDeviceSetting wifiDeviceSetting = wifiDeviceSettingService.getById(mac);

		if(wifiDeviceSetting == null)
			throw new BusinessI18nCodeException(ResponseErrorCode.DEVICE_DATA_NOT_EXIST);

		WifiDeviceSettingDTO settingDto = wifiDeviceSetting.getInnerModel();
		
		boolean change = false;
		
		WifiDeviceSettingVapDTO vap = settingDto.getVaps().get(0);
		
		if(StringUtils.isNotEmpty(dto.getSsid()) && !vap.getSsid().equals(dto.getSsid())){
			vap.setSsid(dto.getSsid());
			change = true;
		}
		//修改ssid和密码
		if(dto.getPassword() != null && dto.getPassword().equals(vap.getAuth_key())){
			if(StringUtils.isEmpty(dto.getPassword())){
				vap.setAuth(WifiDeviceSetting.AUTH_MODE_OPEN);
			} else {
				vap.setAuth(WifiDeviceSetting.AUTH_MODE_WPA2);
			}
			vap.setAuth_key(dto.getPassword());
			vap.setAuth_key_rsa("");
			change = true;
		}
		if(change)
			_callTaskCreate(mac, OperationDS.DS_VapPassword.getNo(), vap);
		
		//修改信号强度 
		change = false;
		WifiDeviceSettingRadioDTO radio = settingDto.getRadios().get(0);
		if(dto.getPower() != null && dto.getPower() != Integer.parseInt(radio.getPower())){
			radio.setPower(String.valueOf(dto.getPower()));
			change = true;
		}
		if(change)
			_callTaskCreate(mac, OperationDS.DS_Power.getNo(), radio);
		
		//增加黑名单
		if (StringHelper.isNotEmpty(dto.getAddblock())){
			 String makeBlockListTaskCmd = makeBlockListTaskCmd("incr",dto.getAddblock());
			if(makeBlockListTaskCmd != null){
				_callTaskCreate(mac, OperationDS.DS_AclMacs.getNo(), makeBlockListTaskCmd);
			}else{
				throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_ERROR, new String[]{"addblock"});
			}
		}
		//删除
		if (StringHelper.isNotEmpty(dto.getDelblock())){
			String makeBlockListTaskCmd = makeBlockListTaskCmd("del",dto.getDelblock());
			if(makeBlockListTaskCmd != null){
				_callTaskCreate(mac, OperationDS.DS_AclMacs.getNo(), makeBlockListTaskCmd);
			}else{
				throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_ERROR, new String[]{"delblock"});
			}
		}
		return Boolean.TRUE;
	}
	private String makeBlockListTaskCmd(String cmd, String param){
		List<String> list = Arrays.asList(param.split(StringHelper.COMMA_STRING_GAP));
		if (StringHelper.isValidMacs(list)){
			return String.format("{\"%s\":{\"macs\":%s}}", cmd,JsonHelper.getJSONString(list));
		}else{
			return null;
		}
	}
	
	public GomeDeviceDTO gomeDeviceOnlineGet(String mac) {
		if(!ThirdPartyDeviceService.isThirdPartyDevice(mac)){
			throw new BusinessI18nCodeException(ResponseErrorCode.DEVICE_DATA_NOT_EXIST);
		}
		WifiDevice device = wifiDeviceService.getById(mac);
		GomeDeviceDTO dto = new GomeDeviceDTO();
		String online = "0";
		if (device.isOnline())
			online = "1";
		dto.setOnline(online);
		return dto;
	}

	public GomeConfigDTO gomeDeviceStatusGet(String mac) {
		WifiDeviceSetting entity = deviceFacadeService.validateDeviceSetting(mac);
		WifiDeviceSettingDTO setting_dto = entity.getInnerModel();
		
		GomeConfigDTO dto = new GomeConfigDTO();
		// 获取正常的device配置
		WifiDeviceSettingVapDTO vapCfg = DeviceHelper.getUrouterDeviceVap(setting_dto);
		dto.setSsid(vapCfg.getSsid());
		String[] powerAndRealChannel = DeviceHelper.getURouterDevicePowerAndRealChannel(setting_dto);
		dto.setPower(Integer.parseInt(powerAndRealChannel[0]));
		dto.setPassword(JNIRsaHelper.jniRsaDecryptHexStr(vapCfg.getAuth_key_rsa()));
		//获取终端列表
		List<GomeDeviceStaDTO> vtos = Collections.emptyList();
		Set<Tuple> presents = null;

		presents = WifiDeviceHandsetUnitPresentSortedSetService.getInstance().fetchPresentWithScores(mac, 0,
				100);

		// 客户端现在获取实时速率会5秒一次请求。
		if (!presents.isEmpty()) {
			List<String> hd_macs = new ArrayList<String>();
			for (Tuple tuple : presents) {
				hd_macs.add(tuple.getElement());
			}
			List<HandsetDeviceDTO> handsets = HandsetStorageFacadeService.handsets(mac, hd_macs);
			List<String> handsetAlias = WifiDeviceHandsetAliasService.getInstance().pipelineHandsetAlias(BusinessRuntimeConfiguration.GomeDistributorId,
					hd_macs);
			if (!handsets.isEmpty()) {
				vtos = new ArrayList<GomeDeviceStaDTO>();
				int cursor = 0;
				HandsetDeviceDTO hd_entity = null;
				String alia = null;
				for (Tuple tuple : presents) {
					hd_entity = handsets.get(cursor);
					if (hd_entity != null) {
						alia = handsetAlias.get(cursor);
						boolean online = WifiDeviceHandsetUnitPresentSortedSetService.getInstance()
								.isOnline(tuple.getScore());
						String tt = MacDictParserFilterHelper.prefixMactch(tuple.getElement(),true,false);
						GomeDeviceStaDTO vto = GomeDeviceStaDTO.builder(tuple.getElement(), tt, hd_entity, online, alia);
						vtos.add(vto);
						cursor++;
					}
				}
			}
		}
		dto.setStalist(getStaList(vtos));
		
		//获取黑名单列表
		WifiDeviceSettingAclDTO acl_dto = DeviceHelper.matchDefaultAcl(setting_dto);
		List<String> block_macs = Collections.emptyList();
		if (acl_dto != null){
			block_macs = acl_dto.getMacs();
		}
		dto.setBlocklist(getBlockList(block_macs));
		return dto;
	}
	
	private static String getBlockList(List<String> list){
		StringBuffer stas = new StringBuffer();
		if (!list.isEmpty()){
			for(int i = 0; i< list.size(); i++){
				stas.append(list.get(i));
				if (i != list.size() - 1){
					stas.append(StringHelper.COMMA_STRING_GAP);
				}
			}
		}
		logger.info(String.format("gomeDeviceStatusGet getBlockList[%s]", stas.toString()));
		return stas.toString();
	}
	private static String getStaList(List<GomeDeviceStaDTO> list){
		StringBuffer stas = new StringBuffer();
		if (!list.isEmpty()){
			for(int i = 0; i< list.size(); i++){
				stas.append(JsonHelper.getJSONString(list.get(i)));
				if (i != list.size() - 1){
					stas.append(StringHelper.COMMA_STRING_GAP);
				}
			}
		}
		logger.info(String.format("gomeDeviceStatusGet getStaList[%s]", stas.toString()));
		return stas.toString();
	}
}
