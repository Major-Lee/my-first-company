package com.bhu.vas.rpc.service.device;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.WifiDeviceContextDTO;
import com.bhu.vas.api.dto.header.JoinReqDTO;
import com.bhu.vas.api.helper.RPCMessageParseHelper;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceRpcService;
import com.bhu.vas.business.device.facade.DeviceFacadeService;

/**
 * 去除掉token存储在db中？只使用redis会比较好？
 * @author Edmond
 *
 */
@Service("deviceRpcService")
public class DeviceRpcService implements IDeviceRpcService {
	private final Logger logger = LoggerFactory.getLogger(DeviceRpcService.class);

	@Resource
	private DeviceFacadeService deviceFacadeService;
	
	/*@Override
	public boolean deviceRegister(DeviceDTO dto, WifiDeviceContextDTO contextDto) {
		System.out.println(dto.toString());
		return false;
	}*/
	
	/**
	 * wifi设备上线
	 * 1：wifi设备基础信息更新
	 * 2：wifi设备在线更新
	 */
	@Override
	public boolean wifiDeviceRegister(String message, WifiDeviceContextDTO contextDto) {
		logger.info(String.format("wifiDeviceRegister with params: message[%s]", message));
		JoinReqDTO messageDto = RPCMessageParseHelper.generateDTOFromMessage(message, JoinReqDTO.class);
		System.out.println(messageDto.getDto().getMac());
		//System.out.println(messageDto.getDto().getOem_swver().length());
		deviceFacadeService.wifiDeviceRegister(messageDto.getDto(), contextDto);
		return true;
	}

	@Override
	public boolean wifiDeviceLogout(String message, WifiDeviceContextDTO contextDto) {
		return false;
	}

}
