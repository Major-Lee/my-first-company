package com.bhu.vas.business.backendws.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.bhu.vas.api.dto.push.HandsetDeviceWSOnlinePushDTO;
import com.bhu.vas.api.dto.redis.DeviceMobilePresentDTO;
import com.bhu.vas.api.dto.wifistasniffer.UserTerminalFocusDTO;
import com.bhu.vas.api.dto.wifistasniffer.WifistasnifferItemRddto;
import com.bhu.vas.api.dto.wifistasniffer.WifistasnifferRddto;
import com.bhu.vas.business.bucache.local.serviceimpl.BusinessWSCacheService;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceMobilePresentStringService;
import com.bhu.vas.business.bucache.redis.serviceimpl.wifistasniffer.UserTerminalFocusHashService;
import com.bhu.vas.push.business.PushService;
import com.smartwork.async.messagequeue.kafka.parser.iface.IMessageHandler;
import com.smartwork.msip.cores.helper.JsonHelper;

/**
 * 周边探测终端发送push handle
 * @author tangzichao
 *
 */
public class WSMessageHandler implements IMessageHandler<byte[]>{
	private final Logger logger = LoggerFactory.getLogger(WSMessageHandler.class);

	@Resource
	private BusinessWSCacheService businessWSCacheService;
	
	@Resource
	private PushService pushService;
	
	@Override
	public void handler(String topic, Map<Integer, List<byte[]>> value) {
		//System.out.println("	topic:"+topic);
		Iterator<Entry<Integer, List<byte[]>>> iter = value.entrySet().iterator();
		while(iter.hasNext()){
			Entry<Integer, List<byte[]>> element = iter.next();
			//Integer index = element.getKey();
			List<byte[]> data = element.getValue();
			//System.out.println("		index:"+index);
			for(Object d:data){
				//if(d instanceof byte[]){
				//System.out.println("			"+new String((byte[])d));//.substring(6));
				//}
				this.doSingleMessage(new String((byte[])d));
			}
		}
	}
	
	
	public void doSingleMessage(String message){
		try{
			if(StringUtils.isEmpty(message)) return;
			logger.info(String.format("WSMessageHandler doSingleMessage [%s]", message));
			
			WifistasnifferRddto dto = JsonHelper.getDTO(message, WifistasnifferRddto.class);
			//判断设备是否被用户绑定并且注册push服务
			String present = WifiDeviceMobilePresentStringService.getInstance().getMobilePresent(dto.getMac());
			if(!StringUtils.isEmpty(present)){
				DeviceMobilePresentDTO mobile_present_dto = JsonHelper.getDTO(present, DeviceMobilePresentDTO.class);
				if(mobile_present_dto != null){
					List<HandsetDeviceWSOnlinePushDTO> push_dtos = this.filtersNeedPushItems(dto, mobile_present_dto);
					if(push_dtos != null && !push_dtos.isEmpty()){
						this.wsMessagePush(push_dtos, mobile_present_dto);
					}
				}
			}
			

		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error("WSMessageHandler exception ", ex);
		}
	}
	
	/**
	 * 对探测的终端进行过滤
	 * 1：终端上线数据
	 * 2：终端被用户关注
	 * 3：终端push延迟过滤
	 * @param dto
	 * @return
	 */
	public List<HandsetDeviceWSOnlinePushDTO> filtersNeedPushItems(WifistasnifferRddto dto, 
			DeviceMobilePresentDTO mobile_present_dto){
		if(dto == null) return null;
		List<WifistasnifferItemRddto> item_dtos = dto.getItems();
		if(item_dtos == null || item_dtos.isEmpty()) return null;
		
		//过滤出不需要延迟push的终端
		List<HandsetDeviceWSOnlinePushDTO> push_dtos = null;
		
		List<String> needPushHdMacs = null;
		for(WifistasnifferItemRddto item_dto : item_dtos){
			//如果探测到上线
			if(item_dto.isOnline()){
				if(needPushHdMacs == null){
					needPushHdMacs = new ArrayList<String>();
				}
				needPushHdMacs.add(item_dto.getMac());
			}
		}
		//如果存在上线消息 过滤探测的上线终端是否有关注
		if(needPushHdMacs != null){
			Integer uid = mobile_present_dto.getUid();
			
			List<UserTerminalFocusDTO> focus_dtos = UserTerminalFocusHashService.getInstance().fetchUserTerminalFocus(
					uid, needPushHdMacs.toArray(new String[]{}));
			if(focus_dtos != null && !focus_dtos.isEmpty()){
				//过滤出存在关注的终端
				int cursor = 0;
				for(UserTerminalFocusDTO focus_dto : focus_dtos){
					if(focus_dto != null && focus_dto.isFocus()){
						String hd_mac = needPushHdMacs.get(cursor);
						//判断是否在延迟范围内
						boolean notify_mark = businessWSCacheService.getQWSPushNotifyCacheByQ(dto.getMac(), hd_mac);
						if(!notify_mark){
							logger.info(String.format("FiltersNeedPushItems handsetDeviceWSOnline mac[%s] hd_mac[%s] "
									+ "ready do Push", dto.getMac(), hd_mac));
							if(push_dtos == null){
								push_dtos = new ArrayList<HandsetDeviceWSOnlinePushDTO>();
							}
							//构造pushDto
							HandsetDeviceWSOnlinePushDTO push_dto = new HandsetDeviceWSOnlinePushDTO();
							push_dto.setMac(dto.getMac());
							push_dto.setHd_mac(hd_mac);
							push_dto.setN(focus_dto.getNick());
							push_dto.setTs(System.currentTimeMillis());
							push_dtos.add(push_dto);
						}else{
							logger.info(String.format("FiltersNeedPushItems handsetDeviceWSOnline mac[%s] hd_mac[%s] "
									+ "push has mark", dto.getMac(), hd_mac));
						}
					}
					cursor++;
				}
			}
		}
		//UserTerminalFocusHashService
		return push_dtos;
	}
	
	/**
	 * 发送终端探测push消息
	 * 并且加入延迟标记
	 * @param push_dtos
	 */
	public void wsMessagePush(List<HandsetDeviceWSOnlinePushDTO> push_dtos, DeviceMobilePresentDTO mobile_present_dto){
		if(push_dtos == null || push_dtos.isEmpty()) return;
		
		for(HandsetDeviceWSOnlinePushDTO push_dto : push_dtos){
			boolean push_successed = pushService.pushHandsetDeviceWSOnline(push_dto, mobile_present_dto);
			if(push_successed){
//				logger.info(String.format("WSMessagePush Successed mac[%s] hd_mac[%s]", dto.getMac(), hd_mac));
				businessWSCacheService.storeQWSPushNotifyCacheResult(push_dto.getMac(), push_dto.getHd_mac());
			}
		}
	}
}
