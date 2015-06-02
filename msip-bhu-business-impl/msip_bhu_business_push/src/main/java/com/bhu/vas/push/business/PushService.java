package com.bhu.vas.push.business;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bhu.vas.api.dto.push.HandsetDeviceOnlinePushDTO;
import com.bhu.vas.api.dto.push.PushDTO;
import com.bhu.vas.api.dto.redis.DeviceMobilePresentDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingDTO;
import com.bhu.vas.api.helper.DeviceHelper;
import com.bhu.vas.api.rpc.devices.model.HandsetDevice;
import com.bhu.vas.api.rpc.user.dto.UserTerminalOnlineSettingDTO;
import com.bhu.vas.api.rpc.user.model.DeviceEnum;
import com.bhu.vas.api.rpc.user.model.PushType;
import com.bhu.vas.api.rpc.user.model.UserSettingState;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceMobilePresentStringService;
import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;
import com.bhu.vas.business.ds.device.service.HandsetDeviceService;
import com.bhu.vas.business.ds.user.service.UserSettingStateService;
import com.bhu.vas.push.common.dto.PushMsg;
import com.bhu.vas.push.common.service.gexin.GexinPushService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.JsonHelper;

/**
 * 业务push service
 * @author tangzichao
 *
 */
@Service
public class PushService{
	private static final Logger logger = LoggerFactory.getLogger(PushService.class);
	
	@Resource
	private UserSettingStateService userSettingStateService;
	
	@Resource
	private DeviceFacadeService deviceFacadeService;
	
	@Resource
	private HandsetDeviceService handsetDeviceService;
	
	/**
	 * 业务逻辑发送push消息统一接口
	 * @param pushDto
	 */
	public void push(PushDTO pushDto){
		if(pushDto != null){
			PushType pushType = PushType.getPushTypeFromType(pushDto.getPushType());
			if(pushType != null){
				switch(pushType){
					case HandsetDeviceOnline:
						this.pushHandsetDeviceOnline(pushDto);
						break;
					default:
						break;
				}
			}
		}
	}
	
	/**
	 * 终端上线push
	 * @param uid
	 * @param mac 设备mac 
	 * @param hd_mac 终端mac
	 */
	public void pushHandsetDeviceOnline(PushDTO pushDto){
		try{
			DeviceMobilePresentDTO presentDto = this.getMobilePresent(pushDto.getMac());
			if(presentDto != null){
				HandsetDeviceOnlinePushDTO hd_push_dto = (HandsetDeviceOnlinePushDTO)pushDto;
				UserSettingState userSettingState = userSettingStateService.getById(pushDto.getMac());
				if(userSettingState != null){
					UserTerminalOnlineSettingDTO dto = userSettingState.getUserSetting(UserTerminalOnlineSettingDTO
							.Setting_Key, UserTerminalOnlineSettingDTO.class);
					if(dto != null){
						//判断终端上线通知开关
						if(dto.isOn()){
							if(!StringUtils.isEmpty(dto.getTimeslot())){
								//根据时间段模式 判断是否在有效的时间段内
								boolean valid_time = false;
								//正常模式
								if(UserTerminalOnlineSettingDTO.Timeslot_Mode_Normal == dto.getTimeslot_mode()){
									valid_time = DateTimeHelper.isInTime(dto.getTimeslot());
								}else if(UserTerminalOnlineSettingDTO.Timeslot_Mode_Silent == dto.getTimeslot_mode()){
									valid_time = !DateTimeHelper.isInTime(dto.getTimeslot());
								}
								
								if(valid_time){
									boolean need_push = false;
									//判断是否开启陌生终端设置
									if(dto.isStranger_on()){
										//第一次接入的终端算是陌生终端
										if(hd_push_dto.isNewed()) 
											need_push = true;
									}else{
										need_push = true;
									}
									
									if(need_push){
										PushMsg pushMsg = this.generatePushMsg(hd_push_dto.getMac());
										if(pushMsg != null){
											pushMsg.setTitle(PushType.HandsetDeviceOnline.getTitle());
											pushMsg.setText(String.format(PushType.HandsetDeviceOnline.getText(), 
													hd_push_dto.getHd_mac(), hd_push_dto.getMac()));
											pushMsg.setPaylod(builderHandsetDeviceOnlinePushPaylod(hd_push_dto));
											boolean ret = pushNotification(pushMsg);
											if(ret){
												logger.info("PushHandsetDeviceOnline Successed " + pushMsg.toString());
											}else{
												logger.info("PushHandsetDeviceOnline Failed " + pushMsg.toString());
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("PushHandsetDeviceOnline exception " + ex.getMessage(), ex);
		}
	}
	
	/**
	 * 构建终端上线push的透传内容
	 * @param hd_push_dto
	 * @return
	 */
	public String builderHandsetDeviceOnlinePushPaylod(HandsetDeviceOnlinePushDTO hd_push_dto){
		WifiDeviceSettingDTO setting_dto = deviceFacadeService.queryDeviceSettingDTO(hd_push_dto.getMac());
		if(setting_dto != null){
			//设置终端别名
			String alias = DeviceHelper.getHandsetDeviceAlias(hd_push_dto.getHd_mac(), setting_dto);
			hd_push_dto.setN(alias);
		}
		//如果没有别名 以终端主机名填充
		if(StringUtils.isEmpty(hd_push_dto.getN())){
			HandsetDevice hd_entity = handsetDeviceService.getById(hd_push_dto.getHd_mac());
			if(hd_entity != null){
				hd_push_dto.setN(hd_entity.getHostname());
			}
		}
		return JsonHelper.getJSONString(hd_push_dto);
	}
	
	/**
	 * 获取用户的mobile push信息数据
	 * @param mac
	 * @return
	 */
	protected DeviceMobilePresentDTO getMobilePresent(String mac){
		String present = WifiDeviceMobilePresentStringService.getInstance().getMobilePresent(mac);
		if(!StringUtils.isEmpty(present)){
			return JsonHelper.getDTO(present, DeviceMobilePresentDTO.class);
		}
		return null;
		//return new DeviceMobilePresentDTO(100017,"D","a0fa58123b6c4735159ba718cdf5d88c","O");
		//return new DeviceMobilePresentDTO(100017,"R","d52bf656bb5f70670c785e62812a2528","O");
	}
	
	/**
	 * 发送通知类型push
	 * @param pushMsg
	 */
	protected boolean pushNotification(PushMsg pushMsg){
		if(DeviceEnum.isIos(pushMsg.getD())){
			return GexinPushService.getInstance().pushNotification4ios(pushMsg);
		}else{
			return GexinPushService.getInstance().pushNotification(pushMsg);
		}
	}
	
	/**
	 * 根据mobile push信息数据生成PushMsg对象
	 * @param mac 设备mac
	 * @return
	 */
	protected PushMsg generatePushMsg(String mac){
		DeviceMobilePresentDTO presentDto = this.getMobilePresent(mac);
		if(presentDto == null) return null;
		
		PushMsg pushMsg = new PushMsg();
		BeanUtils.copyProperties(presentDto, pushMsg);
		return pushMsg;
	}
	
}
