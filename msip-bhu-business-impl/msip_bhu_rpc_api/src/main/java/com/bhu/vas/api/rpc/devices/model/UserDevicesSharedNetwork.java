package com.bhu.vas.api.rpc.devices.model;

import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.smartwork.msip.cores.orm.model.extjson.KeyDtoMapJsonExtPKModel;

/**
 * 用户统一共享网络配置存储
 * 一个用户可以配置多个共享网络的配置，应用到不同的设备上
 * 目前共享网络分为安全共享网络和uplink共享网络
 * @author Edmond Lee
 *
 */
@SuppressWarnings("serial")
public class UserDevicesSharedNetwork extends KeyDtoMapJsonExtPKModel<Integer,ParamSharedNetworkDTO> {
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
	
}
