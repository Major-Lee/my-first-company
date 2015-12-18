package com.bhu.vas.business.backendws.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.bhu.vas.api.dto.push.HandsetDeviceWSOnlinePushDTO;
import com.bhu.vas.api.dto.redis.DeviceMobilePresentDTO;
import com.bhu.vas.api.dto.wifistasniffer.TerminalDetailDTO;
import com.bhu.vas.api.dto.wifistasniffer.UserTerminalFocusDTO;
import com.bhu.vas.api.dto.wifistasniffer.WifistasnifferItemRddto;
import com.bhu.vas.api.dto.wifistasniffer.WifistasnifferRddto;
import com.bhu.vas.business.bucache.local.serviceimpl.BusinessWSCacheService;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceMobilePresentStringService;
import com.bhu.vas.business.bucache.redis.serviceimpl.wifistasniffer.TerminalDetailRecentSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.wifistasniffer.TerminalDeviceTypeCountHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.wifistasniffer.TerminalHotSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.wifistasniffer.TerminalRecentSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.wifistasniffer.UserTerminalFocusHashService;
import com.bhu.vas.push.business.PushService;
import com.smartwork.async.messagequeue.kafka.parser.iface.IMessageHandler;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.plugins.dictparser.impl.mac.MacDictParserFilterHelper;

/**
 * 周边探测终端handle
 * 1) 周边探测业务数据持久化
 * 2) 周边探测push发送
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
		//logger.info("WSMessageHandler Thread " + Thread.currentThread().getName());
		//System.out.println("	topic:"+topic);
		
		Iterator<Entry<Integer, List<byte[]>>> iter = value.entrySet().iterator();
		while(iter.hasNext()){
			Entry<Integer, List<byte[]>> element = iter.next();

			List<byte[]> data = element.getValue();
			for(Object d:data){
				String message = new String((byte[])d);
				if(StringUtils.isEmpty(message)) continue;
				logger.info(String.format("WSMessageHandler [%s]", message));
				
				WifistasnifferRddto dto = JsonHelper.getDTO(message, WifistasnifferRddto.class);
				//进行周边探测业务数据持久化
				this.doBusinessWifistasnifferData(dto);
				//进行push消息的发送
				this.doPushMessage(dto);
				
			}
		}
		
		
	}
	
	/**
	 * 同一个设备mac的探测数据
	 * 1)处理终端探测流水记录
	 * 2)处理最近的探测终端和最热的探测终端
	 * @param message
	 */
	public void doBusinessWifistasnifferData(WifistasnifferRddto dto){
		String mac = dto.getMac();
		try{
			logger.info(String.format("WSMessageHandler doBusinessWifistasnifferData mac [%s] start", mac));
			
			//探测到的上线终端集合
			Set<WifistasnifferItemRddto> wifistasnifferOnlines = new HashSet<WifistasnifferItemRddto>();
			//探测到的下线终端集合
			Set<WifistasnifferItemRddto> wifistasnifferOfflines = new HashSet<WifistasnifferItemRddto>();
			
			List<WifistasnifferItemRddto> items = dto.getItems();
			for(WifistasnifferItemRddto item_dto : items){
				item_dto.setSnifftime(item_dto.getSnifftime() * 1000l);//上报的是秒 转换成毫秒
				if(item_dto.isOnline()){
					wifistasnifferOnlines.add(item_dto);
				}else{
					wifistasnifferOfflines.add(item_dto);
				}
			}
			//处理终端探测流水记录
			this.doWifiStasnifferDetail(mac, wifistasnifferOnlines, wifistasnifferOfflines);
			//处理最近的探测终端和最热的探测终端
			this.doWifiStasnifferRecentAndHot(mac, wifistasnifferOnlines);
			//TerminalRecentSortedSetService.getInstance().addTerminalRecent(mac, "123456", System.currentTimeMillis());
			logger.info(String.format("WSMessageHandler doBusinessWifistasnifferData mac [%s] success", mac));
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error(String.format("WSMessageHandler doBusinessWifistasnifferData mac [%s] failed", mac), ex);
		}
	}
	
	/**
	 * 处理最近的探测终端和最热的探测终端
	 * 只针对上线终端消息
	 * @param mac
	 * @param wifistasnifferOnlines
	 */
	public void doWifiStasnifferRecentAndHot(String mac, Set<WifistasnifferItemRddto> wifistasnifferOnlines){
		int onlines_size = wifistasnifferOnlines.size();
		if(onlines_size == 0) return;
		
		//SparkTaskLog.wifistasniffer().info(String.format("doWifiStasnifferRecentAndHot mac [%s] terminal [%s]", mac, onlines_size));
		
		//收集出现的终端记录
		String[] hd_macs = new String[onlines_size];
		double[] snifftimes = new double[onlines_size];
		double[] incr_sniffcounts = new double[onlines_size];
		
		int cursor = 0;
		for(WifistasnifferItemRddto item_dto : wifistasnifferOnlines){
			hd_macs[cursor] = item_dto.getMac();
			snifftimes[cursor] = item_dto.getSnifftime();
			incr_sniffcounts[cursor] = 1d;
			cursor++;
			this.doTerminalDeviceTypeCount(mac, item_dto.getMac());
			//this.doTerminalLastTime(mac, item_dto.getMac(), item_dto.getSnifftime());
		}
		//录入最近出现的终端记录
		TerminalRecentSortedSetService.getInstance().addTerminalRecents(mac, hd_macs, snifftimes);
		//录入最热的终端记录
		TerminalHotSortedSetService.getInstance().addTerminalHots(mac, hd_macs, incr_sniffcounts);
	}
	
	/**
	 * 根据终端mac分析终端设备型号
	 * 统计周边探测的型号次数
	 * @param mac
	 * @param hd_mac
	 */
	public void doTerminalDeviceTypeCount(String mac, String hd_mac){
		if(!StringUtils.isEmpty(mac) && !StringUtils.isEmpty(hd_mac)){
			String scn = MacDictParserFilterHelper.prefixMactch(hd_mac,true,false);
			if(!StringUtils.isEmpty(scn)){
				TerminalDeviceTypeCountHashService.getInstance().incrby(mac, scn);
			}
		}
	}
	/**
	 * 记录终端最后探测时间
	 * @param mac
	 * @param hd_mac
	 * @param snifftime
	 */
/*	public void doTerminalLastTime(String mac, String hd_mac, long snifftime){
		if(!StringUtils.isEmpty(mac) && !StringUtils.isEmpty(hd_mac)){
			TerminalLastTimeStringService.getInstance().set(mac, hd_mac, snifftime);
		}
	}*/
	
	/**
	 * 处理终端探测流水记录
	 * @param mac 设备mac
	 * @param wifistasnifferOnlines 上线终端
	 * @param wifistasnifferOfflines 下线终端
	 */
	public void doWifiStasnifferDetail(String mac, Set<WifistasnifferItemRddto> wifistasnifferOnlines,
			Set<WifistasnifferItemRddto> wifistasnifferOfflines){
		Set<WifistasnifferItemRddto> merge_onlines = new HashSet<WifistasnifferItemRddto>(wifistasnifferOnlines);
		//如果存在相同终端的上线和下线效果 进行排重 只保留下线消息即可
		merge_onlines.removeAll(wifistasnifferOfflines);
		
		int detail_onlines = merge_onlines.size();
		int detail_offlines = wifistasnifferOfflines.size();
		
//		logger.info(String.format("doWifiStasnifferDetail mac [%s] ol_terminal [%s] of_terminal [%s]", 
//				mac, detail_onlines, detail_offlines));
		//处理终端流水上线情况
		if(detail_onlines > 0){
			Iterator<WifistasnifferItemRddto> merge_onlines_iterator = merge_onlines.iterator();
			while(merge_onlines_iterator.hasNext()){
				WifistasnifferItemRddto item_dto = merge_onlines_iterator.next();
				TerminalDetailDTO online_dto = new TerminalDetailDTO();
				online_dto.setSnifftime(item_dto.getSnifftime());
				boolean newed = TerminalDetailRecentSortedSetService.getInstance().addTerminalDetailOnline(mac, item_dto.getMac(),
						online_dto);
				//设备有可能会重复上报相同时间的相同探测终端 这种情况不需要重复记录最近最热次数等数据
				if(!newed){
					wifistasnifferOnlines.remove(item_dto);
				}
			}
/*			for(WifistasnifferItemRddto item_dto : merge_onlines){
				//String detail_item_value = WifiStasnifferBuilder.generateDetailItemValue(item_dto);
				//if(!StringUtils.isEmpty(detail_item_value)){
				TerminalDetailDTO online_dto = new TerminalDetailDTO();
				online_dto.setSnifftime(item_dto.getSnifftime());
				TerminalDetailRecentSortedSetService.getInstance().addTerminalDetailOnline(mac, item_dto.getMac(),
						online_dto);
				//}
			}*/
		}
		
		//处理终端流水下线情况
		if(detail_offlines > 0){
			for(WifistasnifferItemRddto item_dto : wifistasnifferOfflines){
				//String detail_item_value = WifiStasnifferBuilder.generateDetailItemValue(item_dto);
				//String detail_item_online_value = WifiStasnifferBuilder.generateDetailItemOnlineValue(item_dto);
				//if(!StringUtils.isEmpty(detail_item_value) && !StringUtils.isEmpty(detail_item_online_value)){
				TerminalDetailDTO offline_dto = new TerminalDetailDTO();
				offline_dto.setSnifftime(item_dto.getSnifftime());
				offline_dto.setDuration(item_dto.getDuration());
				offline_dto.setState(WifistasnifferItemRddto.State_Offline);
				boolean newed = TerminalDetailRecentSortedSetService.getInstance().addTerminalDetailOffline(mac, item_dto.getMac(),
						offline_dto);
				//设备有可能会重复上报相同时间的相同探测终端 这种情况不需要重复记录最近最热次数等数据
				if(!newed){
					wifistasnifferOnlines.remove(item_dto);
				}
				//}
			}
		}
	}
	
	
	/**
	 * 处理同一个设备下的终端探测数据的push消息
	 * @param dto
	 * @return
	 */
	public void doPushMessage(WifistasnifferRddto dto){
		String mac = dto.getMac();
		try{
			logger.info(String.format("WSMessageHandler doPushMessage mac [%s]", mac));
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
			logger.error(String.format("WSMessageHandler doPushMessage mac [%s] failed ", mac), ex);
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
