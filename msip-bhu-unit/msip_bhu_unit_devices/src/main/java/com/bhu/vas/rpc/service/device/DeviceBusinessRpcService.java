package com.bhu.vas.rpc.service.device;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.api.dto.WifiDeviceAlarmDTO;
import com.bhu.vas.api.dto.WifiDeviceDTO;
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
	 */
	public void wifiDeviceOnline(String ctx, String payload) {
		//logger.info(String.format("wifiDeviceRegister with params: payload[%s] ctx[%s]", payload, ctx));
		WifiDeviceDTO itemDto = RPCMessageParseHelper.generateDTOFromMessage(payload, WifiDeviceDTO.class);
		//System.out.println(messageDto.getDto().getMac());

		deviceFacadeService.wifiDeviceOnline(ctx, itemDto);
	}
	/**
	 * wifi设备离线
	 * @param ctx
	 * @param mac
	 */
	public void wifiDeviceOffline(String ctx, String wifiId) {
		//logger.info(String.format("wifiDeviceRegister with params: payload[%s] ctx[%s]", payload, ctx));
		//JoinReqDTO messageDto = RPCMessageParseHelper.generateDTOFromMessage(payload, JoinReqDTO.class);
		deviceFacadeService.wifiDeviceOffline(ctx, wifiId);
	}
	
	
	/**
	 * wifi设备不存在
	 * @param ctx
	 * @param mac
	 */
	public void wifiDeviceNotExist(String ctx, String wifiId) {
		deviceFacadeService.wifiDeviceOffline(ctx, wifiId);
	}
	
	/**
	 * wifi设备告警信息
	 * @param ctx
	 * @param payload
	 */
	public void wifiDeviceAlarm(String ctx, String payload) {
		//logger.info(String.format("wifiDeviceAlarm with params: payload[%s] ctx[%s]", payload, ctx));
		WifiDeviceAlarmDTO itemDto = RPCMessageParseHelper.generateDTOFromMessage(payload, WifiDeviceAlarmDTO.class);
		//System.out.println(messageDto.getTrapDto().getDto().getMac_addr());

		deviceFacadeService.wifiDeviceAlarm(ctx, itemDto);
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
		//System.out.println(payload);
		//logger.info(String.format("handsetDeviceConnectState with params: payload[%s] ctx[%s]", payload, ctx));
		HandsetDeviceDTO itemDto = RPCMessageParseHelper.generateDTOFromMessage(payload, HandsetDeviceDTO.class);
		//HandsetDeviceDTO itemDto = messageDto.getWlanDto().getDto();
		//System.out.println(itemDto.getAction());
		if(HandsetDeviceDTO.Action_Online.equals(itemDto.getAction())){
			deviceFacadeService.handsetDeviceOnline(ctx, itemDto.getBssid(), itemDto);
		}
		else if(HandsetDeviceDTO.Action_Offline.equals(itemDto.getAction())){
			deviceFacadeService.handsetDeviceOffline(ctx, itemDto);
		}
		else if(HandsetDeviceDTO.Action_Sync.equals(itemDto.getAction())){
			deviceFacadeService.handsetDeviceSync(ctx, itemDto.getBssid(), itemDto);
		}
	}

}
