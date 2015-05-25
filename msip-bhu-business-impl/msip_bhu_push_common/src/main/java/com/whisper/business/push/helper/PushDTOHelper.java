package com.whisper.business.push.helper;

import com.smartwork.msip.business.dto.push.PushDTO;
import com.smartwork.msip.business.dto.push.PushMessageDTO;
import com.smartwork.msip.cores.helper.StringHelper;
import com.whisper.api.user.model.DeviceEnum;
import com.whisper.business.bucache.redis.serviceimpl.push.UserPushNotifyCountService;
import com.whisper.business.push.engine.android.AndroidPushEngine;
import com.whisper.business.push.engine.ios.IOSPushEngine;

public class PushDTOHelper {
	/**
	 * 生成发送push的DTO
	 * @param uid 用户id
	 * @param dt 设备token
	 * @param d 设备类型
	 * @param pt push消息渠道(商店，企业)
	 * @param bu_payload 业务携带payload
	 * @param show 业务显示的内容
	 * @param business_type 业务类型
	 * @return
	 */
	public static PushDTO generatePushDTO(int uid, String dt, String d, String pt, 
			String bu_payload, String show, String business_type){
		return generatePushDTO(uid, dt, d, pt, bu_payload, show, business_type, null);
	}
	
	public static PushDTO generatePushDTO(int uid, String dt, String d, String pt, 
			String bu_payload, String show, String business_type, String push_sound){
		if(StringHelper.isEmpty(dt)) return null;
		if(StringHelper.isEmpty(d)) return null;
		
		PushDTO pushDto = new PushDTO();
		pushDto.setDt(dt);
		pushDto.setD(d);
		pushDto.setPt(pt);
		pushDto.setUid(uid);
		PushMessageDTO push_message_dto = new PushMessageDTO(business_type);
		push_message_dto.setPayload(bu_payload);
		push_message_dto.setShow(show);
		push_message_dto.setT(business_type);
		push_message_dto.setTs(System.currentTimeMillis());
		push_message_dto.setSound(push_sound);
		pushDto.setMessage(push_message_dto);
		return pushDto;
	}
	
	/**
	 * 增加PUSH消息的数量标记，并返回当前数量
	 * @param uid
	 * @return
	 */
	public static long incrPushNotifyCount(int uid){
		try{
			return UserPushNotifyCountService.getInstance().incr(uid);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}
		return 0l;
	}
	/**
	 * 发送push消息
	 * @param pushDto
	 */
	public static void sendPushMsg(PushDTO pushDto){
		if(DeviceEnum.isIos(pushDto.getD())){
			IOSPushEngine.getInstance().doPush(pushDto);
		}else{
			AndroidPushEngine.getInstance().doPush(pushDto);
		}
	}
}
