package com.bhu.vas.rpc.service.device;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.api.dto.header.EventDTO;
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
	public void wifiDeviceOnline(String ctx, String payload) {
		logger.info(String.format("wifiDeviceRegister with params: payload[%s] ctx[%s]", payload, ctx));
		JoinReqDTO messageDto = RPCMessageParseHelper.generateDTOFromMessage(payload, JoinReqDTO.class);
		System.out.println(messageDto.getDto().getMac());

		deviceFacadeService.wifiDeviceOnline(ctx, messageDto.getDto());
	}
	
	/**
	 * 移动设备连接状态请求
	 * 1:online
	 * 2:offline
	 * 3:sync
	 * @param ctx
	 * @param payload
	 */
	public void handsetDeviceConnectState(String ctx, String payload) {
		logger.info(String.format("handsetDeviceConnectState with params: payload[%s] ctx[%s]", payload, ctx));
		EventDTO messageDto = RPCMessageParseHelper.generateDTOFromMessage(payload, EventDTO.class);
		HandsetDeviceDTO itemDto = messageDto.getWlanDto().getDto();
		System.out.println(itemDto.getAction());
		if(HandsetDeviceDTO.Action_Online.equals(itemDto.getAction())){
			deviceFacadeService.handsetDeviceOnline(ctx, itemDto);
		}
		else if(HandsetDeviceDTO.Action_Offline.equals(itemDto.getAction())){
			deviceFacadeService.handsetDeviceOffline(ctx, itemDto);
		}
		else if(HandsetDeviceDTO.Action_Sync.equals(itemDto.getAction())){
			deviceFacadeService.handsetDeviceSync(ctx, itemDto);
		}
	}


}
