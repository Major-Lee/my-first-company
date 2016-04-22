package com.bhu.vas.api.rpc.devices.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.helper.VapEnumType.SharedNetworkType;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.smartwork.msip.cores.orm.model.extjson.KeyListMapJsonExtPKModel;

/**
 * 用户统一共享网络配置存储
 * 一个用户可以配置多个共享网络的配置，应用到不同的设备上
 * 目前共享网络分为安全共享网络和uplink共享网络
 * @author Edmond Lee
 *
 */
@SuppressWarnings("serial")
public class UserDevicesSharedNetworks extends KeyListMapJsonExtPKModel<Integer,ParamSharedNetworkDTO> {
	@Override
	public Class<ParamSharedNetworkDTO> getJsonParserModel() {
		return ParamSharedNetworkDTO.class;
	}

	@Override
	protected Class<Integer> getPKClass() {
		return Integer.class;
	}

	@Override
	public Integer getId() {
		return super.getId();
	}

	@Override
	public void setId(Integer id) {
		super.setId(id);
	}
	
	public static UserDevicesSharedNetworks buildDefault(int uid,ParamSharedNetworkDTO paramDto){
		UserDevicesSharedNetworks configs = new UserDevicesSharedNetworks();
		configs.setId(uid);
		if(StringUtils.isEmpty(paramDto.getTemplate_name())){
			SharedNetworkType sharedNetwork = VapEnumType.SharedNetworkType.fromKey(paramDto.getNtype());
			paramDto.setTemplate_name(sharedNetwork.getName().concat(paramDto.getTemplate()));
		}
		paramDto.setTs(System.currentTimeMillis());
		List<ParamSharedNetworkDTO> sharedNetworkType_models = new ArrayList<ParamSharedNetworkDTO>();
		sharedNetworkType_models.add(paramDto);
		configs.put(paramDto.getNtype(), sharedNetworkType_models);
		return configs;
		/*UserDevicesSharedNetworks configs = new UserDevicesSharedNetworks();
		configs.setId(uid);
		List<ParamSharedNetworkDTO> sharedNetworkType_models = new ArrayList<ParamSharedNetworkDTO>();
		ParamSharedNetworkDTO dto = ParamSharedNetworkDTO.builderDefault(sharedNetwork.getKey());
		dto.setTemplate(template);
		dto.setTemplate_name(sharedNetwork.getName().concat(template));
		sharedNetworkType_models.add(dto);
		configs.put(sharedNetwork.getKey(), sharedNetworkType_models);
		return configs;
		
		
		configs = new UserDevicesSharedNetworks();
		configs.setId(uid);
		List<ParamSharedNetworkDTO> models = new ArrayList<ParamSharedNetworkDTO>();
		paramDto.setTemplate(DefaultTemplate);
		//paramDto.setTemplate_name(template_name);
		models.add(paramDto);
		configs.put(paramDto.getNtype(), models);
		userDevicesSharedNetworksService.insert(configs);*/
	}
	
	public static UserDevicesSharedNetworks buildDefault(int uid,VapEnumType.SharedNetworkType sharedNetwork,String template){
		UserDevicesSharedNetworks configs = new UserDevicesSharedNetworks();
		configs.setId(uid);
		List<ParamSharedNetworkDTO> sharedNetworkType_models = new ArrayList<ParamSharedNetworkDTO>();
		ParamSharedNetworkDTO dto = ParamSharedNetworkDTO.builderDefault(sharedNetwork.getKey());
		dto.setTs(System.currentTimeMillis());
		dto.setTemplate(template);
		dto.setTemplate_name(sharedNetwork.getName().concat(template));
		sharedNetworkType_models.add(dto);
		configs.put(sharedNetwork.getKey(), sharedNetworkType_models);
		return configs;
	}
}
