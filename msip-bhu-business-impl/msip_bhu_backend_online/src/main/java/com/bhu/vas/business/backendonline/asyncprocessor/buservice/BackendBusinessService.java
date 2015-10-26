package com.bhu.vas.business.backendonline.asyncprocessor.buservice;

import java.util.ArrayList;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.mdto.WifiHandsetDeviceItemLogMDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.marker.BusinessMarkerService;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.WifiDeviceRealtimeRateStatisticsStringService;
import com.bhu.vas.business.bucache.redis.serviceimpl.wifistasniffer.TerminalDetailRecentSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.wifistasniffer.TerminalDeviceTypeCountHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.wifistasniffer.TerminalHotSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.wifistasniffer.TerminalLastTimeStringService;
import com.bhu.vas.business.bucache.redis.serviceimpl.wifistasniffer.TerminalRecentSortedSetService;
import com.bhu.vas.business.ds.device.dao.WifiHandsetDeviceRelationMDao;
import com.bhu.vas.business.ds.user.service.UserDeviceService;
import com.bhu.vas.business.ds.user.service.UserSettingStateService;

/**
 * backend专属业务service
 * @author tangzichao
 *
 */
@Service
public class BackendBusinessService {
	
	@Resource
	private WifiHandsetDeviceRelationMDao wifiHandsetDeviceRelationMDao;
	
	@Resource
	private UserSettingStateService userSettingStateService;
	
	@Resource
	private UserDeviceService userDeviceService;
	/**********************************     清除设备数据业务 start   *****************************************/
	
	/**
	 * 设备恢复出厂的相关数据清除函数
		1:周边探测数据
		2:周边探测开关是否恢复初始  目前初始是关闭的
		3:终端上线通知开关是否恢复初始 目前初始是开启的 时间段为全天
		4:定时开关恢复初始 目前初始为关闭
		5:设备测速数据清除
		6:终端列表清除离线终端数据
		7:流量统计数据清除
		8:终端详情数据清除
	 * @param mac
	 */
	public void deviceRestoreFactory(String mac){
		//1:周边探测数据
		clearWifistasnifferData(mac);
		//2:周边探测开关是否恢复初始 3:终端上线通知开关是否恢复初始 4:定时开关恢复初始
		initUserSettingData(mac);
		//5:设备测速数据清除
		clearRealtimeSpeedData(mac);
		//6:终端列表清除离线终端数据
		clearTerminalOfflineListData(mac);
		//7流量统计数据清除
		clearDeviceUsedStatisticsData(mac);
		//8终端详情数据清除
		clearDeviceHandsetData(mac);
		//9清除设备和用户的绑定关系
		clearDeviceBindedRelation(mac);
	}
	
	
	/**
	 * 清除设备终端列表中的离线终端数据
	 * @param mac
	 */
	public void clearTerminalOfflineListData(String mac){
		try{
			WifiDeviceHandsetPresentSortedSetService.getInstance().clearOfflinePresents(mac);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}
	}
	
	public void clearDeviceUsedStatisticsData(String mac){
		try{
			BusinessMarkerService.getInstance().deviceUsedStatisticsClear(mac);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}
	}

	public void clearDeviceHandsetData(String mac) {
		try{
			Query query = new Query(Criteria.where(WifiHandsetDeviceRelationMDao.M_WIFIID).is(mac));
			Update update = new Update();
			update.set(WifiHandsetDeviceRelationMDao.M_TOTAL_RX_BYTES, 0);
			update.set(WifiHandsetDeviceRelationMDao.M_LOGS, new ArrayList<WifiHandsetDeviceItemLogMDTO>());
			wifiHandsetDeviceRelationMDao.updateMulti(query, update);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}


	}

	/**
	 * 初始化设备的用户设置
	 * 1:定时开关
	 * 2:终端上线通知开关
	 * 3:周边探测开关
	 * 4:访客网络开关
	 * @param mac
	 */
	public void initUserSettingData(String mac){
		try{
			userSettingStateService.deleteById(mac);
			userSettingStateService.initUserSettingState(mac);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}
	}
	
	
	/**
	 * 清除周边探测收集的相关数据
	 * @param mac
	 */
	public void clearWifistasnifferData(String mac){
		try{
			int start = 0;
			int size = 100;
			int count = 0;
			//遍历获取recent探测数据
			do{
				Set<String> recent_set = TerminalRecentSortedSetService.getInstance().fetchTerminalRecent(mac, start, size);
				if(recent_set == null || recent_set.isEmpty()){
					count = 0;
				}else{
					count = recent_set.size();
					
					String[] recent_array = recent_set.toArray(new String[]{});
					//删除最后一次探测上线时间数据
					TerminalLastTimeStringService.getInstance().dels(mac, recent_array);
					//删除终端探测细节数据
					TerminalDetailRecentSortedSetService.getInstance().dels(mac, recent_array);
					//删除终端探测隔壁老王数据
					TerminalHotSortedSetService.getInstance().del(mac);
					//删除社区类型数据
					TerminalDeviceTypeCountHashService.getInstance().del(mac);
				}
				start = start + size;
			}while(count == size);
			
			//删除recent探测数据
			TerminalRecentSortedSetService.getInstance().del(mac);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}
	}
	
	/**
	 * 清除设备的测速数据和实时速率残留数据
	 */
	public void clearRealtimeSpeedData(String mac){
		try{
			WifiDeviceRealtimeRateStatisticsStringService.getInstance().clearAll(mac);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}
	}
	
	/**
	 * 清除设备和用户绑定关系
	 * @param mac
	 */
	public void clearDeviceBindedRelation(String mac){
		try{
			userDeviceService.clearDeviceBinded(mac);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}
	}
	
	/**********************************     清除设备数据业务 end   *****************************************/
}
