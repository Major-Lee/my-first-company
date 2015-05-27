package com.bhu.vas.api.rpc.user.model;

import java.util.Map;

import com.bhu.vas.api.rpc.user.dto.MobileDeviceDTO;
import com.smartwork.msip.cores.orm.model.extjson.KeyDtoMapJsonExtIntModel;
/**
 * 保存用户的历史设备登陆状态
 * @author lawliet
 *
 */
@SuppressWarnings("serial")
public class UserMobileDeviceState extends KeyDtoMapJsonExtIntModel<MobileDeviceDTO>{

	@Override
	public Class<MobileDeviceDTO> getJsonParserModel() {
		return MobileDeviceDTO.class;
	}

	public boolean hasDevice(DeviceEnum device){
		return this.containsKey(device.getSname());
	}
	
	public Map<String,MobileDeviceDTO> fetchAllDevices(){
		return this.getExtension();
	}
	
	public MobileDeviceDTO fetchDevice(DeviceEnum device){
		return this.getInnerModel(device.getSname());
	}
	
	public void addDevice(DeviceEnum device,MobileDeviceDTO dto){
		this.putInnerModel(device.getSname(), dto);
	}
	
	public void replaceDevice(DeviceEnum device,MobileDeviceDTO dto){
		this.putInnerModel(device.getSname(), dto);
	}
	
	public void removeDevice(DeviceEnum device){
		this.removeInnerModel(device.getSname());
	}
	
}

