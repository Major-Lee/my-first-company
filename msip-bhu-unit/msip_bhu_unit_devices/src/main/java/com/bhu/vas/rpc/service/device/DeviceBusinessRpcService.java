package com.bhu.vas.rpc.service.device;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.WifiDeviceContextDTO;
import com.bhu.vas.api.dto.header.JoinReqDTO;
import com.bhu.vas.api.helper.RPCMessageParseHelper;
import com.bhu.vas.business.device.facade.DeviceFacadeService;

/**
 * device组件的业务service
 * 不暴露接口
 * @author tangzichao
 *
 */
@Service
public class DeviceBusinessRpcService {
	private final Logger logger = LoggerFactory.getLogger(DeviceBusinessRpcService.class);

	@Resource
	private DeviceFacadeService deviceFacadeService;
	
	
	/**
	 * wifi设备上线
	 * 1：wifi设备基础信息更新
	 * 2：wifi设备在线更新
	 */
	public boolean wifiDeviceRegister(String message, WifiDeviceContextDTO contextDto) {
		logger.info(String.format("wifiDeviceRegister with params: message[%s]", message));
		JoinReqDTO messageDto = RPCMessageParseHelper.generateDTOFromMessage(message, JoinReqDTO.class);
		System.out.println(messageDto.getDto().getMac());
		//System.out.println(messageDto.getDto().getOem_swver().length());
		deviceFacadeService.wifiDeviceRegister(messageDto.getDto(), contextDto);
		return true;
	}

	public boolean wifiDeviceLogout(String message, WifiDeviceContextDTO contextDto) {
		return false;
	}

}
