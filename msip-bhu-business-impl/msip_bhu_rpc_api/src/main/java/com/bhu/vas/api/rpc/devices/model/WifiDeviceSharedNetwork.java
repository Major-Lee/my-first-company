package com.bhu.vas.api.rpc.devices.model;

import com.bhu.vas.api.rpc.devices.dto.sharednetwork.SharedNetworkSettingDTO;
import com.smartwork.msip.cores.orm.model.extjson.DtoJsonExtPKModel;

/**
 * 针对设备持久化下发指令，一般都是设备不保存的指令，重启后会丢失的指令
 * 注意事项 涉及到修改配置的指令需要合并成一个
 * @author Edmond Lee
 *
 */
@SuppressWarnings("serial")
public class WifiDeviceSharedNetwork extends DtoJsonExtPKModel<String,SharedNetworkSettingDTO> {
	
	private String sharednetwork_type;
	
	public String getSharednetwork_type() {
		return sharednetwork_type;
	}

	public void setSharednetwork_type(String sharednetwork_type) {
		this.sharednetwork_type = sharednetwork_type;
	}

	@Override
	public Class<SharedNetworkSettingDTO> getJsonParserModel() {
		return SharedNetworkSettingDTO.class;
	}

	@Override
	protected Class<String> getPKClass() {
		return String.class;
	}

	@Override
	public String getId() {
		return super.getId();
	}

	@Override
	public void setId(String id) {
		super.setId(id);
	}
}
