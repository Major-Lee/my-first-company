package com.bhu.vas.api.user.model;

import java.util.Map;

import com.bhu.vas.api.user.dto.DeviceDTO;
import com.smartwork.msip.cores.orm.model.extjson.KeyDtoMapJsonExtStringModel;
/**
 * 保存用户的历史设备登陆状态
 * @author lawliet
 *
 */
@SuppressWarnings("serial")
public class UserDeviceState extends KeyDtoMapJsonExtStringModel<DeviceDTO>{

	@Override
	public Class<DeviceDTO> getJsonParserModel() {
		return DeviceDTO.class;
	}

	public boolean hasDevice(DeviceEnum device){
		return this.containsKey(device.getSname());
	}
	
	public Map<String,DeviceDTO> fetchAllDevices(){
		return this.getExtension();
	}
	
	public DeviceDTO fetchDevice(DeviceEnum device){
		return this.getInnerModel(device.getSname());
	}
	
	public void addDevice(DeviceEnum device,DeviceDTO dto){
		this.putInnerModel(device.getSname(), dto);
	}
	
	public void replaceDevice(DeviceEnum device,DeviceDTO dto){
		this.putInnerModel(device.getSname(), dto);
	}
	
	public void removeDevice(DeviceEnum device){
		this.removeInnerModel(device.getSname());
	}
	
}
