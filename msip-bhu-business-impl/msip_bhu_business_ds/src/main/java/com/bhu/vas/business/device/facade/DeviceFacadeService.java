package com.bhu.vas.business.device.facade;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.api.dto.WifiDeviceAlarmDTO;
import com.bhu.vas.api.dto.WifiDeviceDTO;
import com.bhu.vas.api.rpc.devices.model.HandsetDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceAlarm;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDevicePresentService;
import com.bhu.vas.business.builder.BusinessModelBuilder;
import com.bhu.vas.business.device.service.HandsetDeviceService;
import com.bhu.vas.business.device.service.WifiDeviceAlarmService;
import com.bhu.vas.business.device.service.WifiDeviceService;
import com.bhu.vas.business.device.service.WifiHandsetDeviceRelationMService;
import com.smartwork.msip.exception.RpcBusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class DeviceFacadeService {
	private final Logger logger = LoggerFactory.getLogger(DeviceFacadeService.class);
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private WifiDeviceAlarmService wifiDeviceAlarmService;
	
	@Resource
	private HandsetDeviceService handsetDeviceService;
	
	@Resource
	private WifiHandsetDeviceRelationMService wifiHandsetDeviceRelationMService;
	
	/*********************************** RPC Business Start **************************************/
	
	/**
	 * 
	 * wifi设备上线
	 * 1：wifi设备基础信息更新
	 * 2：wifi设备在线状态Redis更新
	 * 3:wifi设备对应handset在线列表redis初始化 根据设备上线时间作为阀值来进行列表清理, 防止多线程情况下清除有效移动设备 (backend)
	 * @param dto
	 */
	public void wifiDeviceOnline(String ctx, WifiDeviceDTO dto){
		if(dto == null) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY.code());
		if(StringUtils.isEmpty(dto.getMac()) || StringUtils.isEmpty(ctx))
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY.code());

		//1:wifi设备基础信息更新
		WifiDevice wifi_device_entity = BusinessModelBuilder.wifiDeviceDtoToEntity(dto);
		WifiDevice exist_wifi_device_entity = wifiDeviceService.getById(wifi_device_entity.getId());
		if(exist_wifi_device_entity == null){
			wifiDeviceService.insert(wifi_device_entity);
		}else{
			if(exist_wifi_device_entity.isOnline()){
				//说明设备离线消息未能到达，需要对wifi对应的移动设备列表进行清除，清除掉wifi设备本次时间之前的数据
			}
			wifiDeviceService.update(wifi_device_entity);
		}
		//2:wifi设备在线状态Redis更新
		WifiDevicePresentService.getInstance().addPresent(wifi_device_entity.getId(), ctx);
		//3:wifi设备对应handset在线列表初始化 根据设备上线时间作为阀值来进行列表清理, 防止多线程情况下清除有效移动设备
		WifiDeviceHandsetPresentSortedSetService.getInstance().clearPresents(wifi_device_entity.getId(), 
				wifi_device_entity.getLast_reged_at().getTime());
	}
	
	/**
	 * wifi设备离线
	 * 1:wifi设备基础信息表中的在线状态更新为离线
	 * 2:wifi设备在线状态redis移除
	 * 3:wifi上的移动设备基础信息表的在线状态更新 (backend)
	 * 4:wifi设备对应handset在线列表redis清除 (backend)
	 * @param ctx
	 * @param mac
	 */
	public void wifiDeviceOffline(String ctx, String mac){
		if(StringUtils.isEmpty(ctx) || StringUtils.isEmpty(mac)) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY.code());
		String lowercase_mac = mac.toLowerCase();

		//1:wifi设备基础信息表中的在线状态更新
		WifiDevice exist_wifi_device_entity = wifiDeviceService.getById(lowercase_mac);
		if(exist_wifi_device_entity != null){
			exist_wifi_device_entity.setOnline(false);
			wifiDeviceService.update(exist_wifi_device_entity);
		}
		//2:wifi设备在线状态redis移除 TODO:多线程情况可能下，设备先离线再上线，两条消息并发处理，如果上线消息先完成，可能会清除掉有效数据
		WifiDevicePresentService.getInstance().removePresent(lowercase_mac);
//		String ctx_present = WifiDevicePresentService.getInstance().getPresent(lowercase_mac);
//		if(ctx.equals(ctx_present)){
//			WifiDevicePresentService.getInstance().removePresent(lowercase_mac);
		
		//3:wifi上的移动设备基础信息表的在线状态更新
		List<HandsetDevice> handset_devices_online_entitys = handsetDeviceService.findModelByWifiIdAndOnline(lowercase_mac);
		if(!handset_devices_online_entitys.isEmpty()){
			for(HandsetDevice handset_devices_online_entity : handset_devices_online_entitys){
				handset_devices_online_entity.setOnline(false);
			}
			handsetDeviceService.updateAll(handset_devices_online_entitys);
		}
		//4:wifi设备对应handset在线列表redis清除
		WifiDeviceHandsetPresentSortedSetService.getInstance().clearPresents(lowercase_mac);
//		}
	}
	
	/**
	 * wifi设备告警信息
	 * 1:告警信息存入告警信息表中
	 * @param ctx
	 * @param dto
	 */
	public void wifiDeviceAlarm(String ctx, WifiDeviceAlarmDTO dto){
		if(dto == null) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY.code());
		if(StringUtils.isEmpty(dto.getMac_addr()) || StringUtils.isEmpty(ctx))
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY.code());

		WifiDeviceAlarm wifi_device_alarm_entity = BusinessModelBuilder.wifiDeviceAlarmDtoToEntity(dto);
		wifiDeviceAlarmService.insert(wifi_device_alarm_entity);

	}
	
	/**
	 * 移动设备上线
	 * 1:移动设备基础信息更新
	 * 2:wifi设备对应handset在线列表redis添加
	 * 3:移动设备连接wifi设备的接入记录(非流水) (backend)
	 * 4:移动设备连接wifi设备的流水log (backend)
	 * 5:wifi设备接入移动设备的接入数量 (backend)
	 * @param ctx
	 * @param dto
	 */
	public void handsetDeviceOnline(String ctx, HandsetDeviceDTO dto){
		if(dto == null) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY.code());
		if(StringUtils.isEmpty(dto.getMac()) || StringUtils.isEmpty(dto.getBssid()) || StringUtils.isEmpty(ctx))
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY.code());

		//1:移动设备基础信息更新
		HandsetDevice handset_device_entity = BusinessModelBuilder.handsetDeviceDtoToEntity(dto);
		HandsetDevice exist_handset_device_entity = handsetDeviceService.getById(handset_device_entity.getId());
		if(exist_handset_device_entity == null){
			handsetDeviceService.insert(handset_device_entity);
		}else{
			handsetDeviceService.update(handset_device_entity);
		}
		
		String wifiId = handset_device_entity.getBssid();
		//2:wifi设备对应handset在线列表redis添加
		WifiDeviceHandsetPresentSortedSetService.getInstance().addPresent(wifiId, handset_device_entity.getId(), 
				handset_device_entity.getLast_login_at().getTime());
		//3:移动设备连接wifi设备的接入记录(非流水)
		wifiHandsetDeviceRelationMService.addRelation(wifiId, dto.getMac(), handset_device_entity.
				getLast_login_at());
	}
	/**
	 * 移动设备下线
	 * 1:更新移动设备的online状态为false
	 * 2:wifi设备对应handset在线列表redis移除
	 * @param ctx
	 * @param dto
	 */
	public void handsetDeviceOffline(String ctx, HandsetDeviceDTO dto){
		if(dto == null) 
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY.code());
		if(StringUtils.isEmpty(dto.getMac()) || StringUtils.isEmpty(dto.getBssid()) || StringUtils.isEmpty(ctx))
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY.code());

		String lowercase_mac = dto.getMac().toLowerCase();
		//1:更新移动设备的online状态为false
		HandsetDevice exist_handset_device_entity = handsetDeviceService.getById(lowercase_mac);
		if(exist_handset_device_entity != null){
			exist_handset_device_entity.setOnline(false);
			handsetDeviceService.update(exist_handset_device_entity);
		}
		//2:wifi设备对应handset在线列表redis移除
		WifiDeviceHandsetPresentSortedSetService.getInstance().removePresent(exist_handset_device_entity.
				getLast_wifi_id(), lowercase_mac);
	}
	/**
	 * 移动设备连接状态sync
	 * a:如果移动设备目前不在线或者不存在移动设备数据，则执行设备上线相同操作
	 * b:如果移动设备目前在线
	 * 	1:移动设备基础信息更新
	 *  2:wifi设备对应handset在线列表redis更新
	 * @param ctx
	 * @param dto
	 */
	public void handsetDeviceSync(String ctx, HandsetDeviceDTO dto){
		//a:如果移动设备目前不在线或者不存在移动设备数据，则执行设备上线相同操作
		HandsetDevice exist_handset_device_entity = handsetDeviceService.getById(dto.getMac());
		if(exist_handset_device_entity == null || !exist_handset_device_entity.isOnline()){
			this.handsetDeviceOnline(ctx, dto);
			return;
		}
		//b:如果移动设备目前在线
		if(exist_handset_device_entity.isOnline()){
			//1:移动设备基础信息更新
			HandsetDevice handset_device_entity = BusinessModelBuilder.handsetDeviceDtoToEntity(dto);
			handsetDeviceService.update(handset_device_entity);
			
			String wifiId = handset_device_entity.getBssid();
			//2:wifi设备对应handset在线列表redis更新
			WifiDeviceHandsetPresentSortedSetService.getInstance().addPresent(wifiId, handset_device_entity.getId(), 
					handset_device_entity.getLast_login_at().getTime());
		}
	}
	
	
	/*********************************** RPC Business End **************************************/
	
}
