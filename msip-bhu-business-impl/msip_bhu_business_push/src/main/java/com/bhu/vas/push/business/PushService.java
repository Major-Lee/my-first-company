package com.bhu.vas.push.business;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bhu.vas.api.dto.push.HandsetDeviceOnlinePushDTO;
import com.bhu.vas.api.dto.push.PushDTO;
import com.bhu.vas.api.dto.redis.DeviceMobilePresentDTO;
import com.bhu.vas.api.rpc.user.model.DeviceEnum;
import com.bhu.vas.api.rpc.user.model.PushType;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceMobilePresentStringService;
import com.bhu.vas.push.common.dto.PushMsg;
import com.bhu.vas.push.common.service.gexin.GexinPushService;
import com.smartwork.msip.cores.helper.JsonHelper;

/**
 * 业务push service
 * @author tangzichao
 *
 */
@Service
public class PushService{
	
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
		HandsetDeviceOnlinePushDTO dto = (HandsetDeviceOnlinePushDTO)pushDto;
		PushMsg pushMsg = this.generatePushMsg(dto.getMac());
		if(pushMsg != null){
			pushMsg.setTitle(PushType.HandsetDeviceOnline.getTitle());
			pushMsg.setText(String.format(PushType.HandsetDeviceOnline.getText(), dto.getHd_mac(), dto.getMac()));
			pushMsg.setPaylod(dto.getPayload());
			pushNotification(pushMsg);
		}
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
	protected void pushNotification(PushMsg pushMsg){
		if(DeviceEnum.isIos(pushMsg.getD())){
			GexinPushService.getInstance().pushNotification4ios(pushMsg);
		}else{
			GexinPushService.getInstance().pushNotification(pushMsg);
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
