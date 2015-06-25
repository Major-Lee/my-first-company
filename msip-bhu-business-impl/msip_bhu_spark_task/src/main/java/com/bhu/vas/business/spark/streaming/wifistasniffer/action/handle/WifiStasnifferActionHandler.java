package com.bhu.vas.business.spark.streaming.wifistasniffer.action.handle;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.bhu.vas.api.dto.wifistasniffer.TerminalDetailDTO;
import com.bhu.vas.api.dto.wifistasniffer.WifistasnifferItemRddto;
import com.bhu.vas.business.bucache.redis.serviceimpl.wifistasniffer.TerminalDetailRecentSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.wifistasniffer.TerminalDeviceTypeCountHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.wifistasniffer.TerminalHotSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.wifistasniffer.TerminalLastTimeStringService;
import com.bhu.vas.business.bucache.redis.serviceimpl.wifistasniffer.TerminalRecentSortedSetService;
import com.bhu.vas.business.spark.streaming.log.SparkTaskLog;
import com.smartwork.msip.cores.plugins.dictparser.impl.mac.MacDictParserFilterHelper;

@SuppressWarnings("serial")
@Component
public class WifiStasnifferActionHandler implements Serializable{
	/**
	 * 同一个设备mac的探测数据
	 * @param mac
	 * @param item_rddtos_iterator
	 */
	public void wifistasnifferSparkHandle(String mac, Iterator<WifistasnifferItemRddto> item_rddtos_iterator){
		try{
			SparkTaskLog.wifistasniffer().info(String.format("wifistasnifferSparkHandle mac [%s] start", mac));
			//探测到的上线终端集合
			Set<WifistasnifferItemRddto> wifistasnifferOnlines = new HashSet<WifistasnifferItemRddto>();
			//探测到的下线终端集合
			Set<WifistasnifferItemRddto> wifistasnifferOfflines = new HashSet<WifistasnifferItemRddto>();
			
			while(item_rddtos_iterator.hasNext()){
				WifistasnifferItemRddto item_dto = item_rddtos_iterator.next();
				if(item_dto.isOnline()){
					wifistasnifferOnlines.add(item_dto);
				}else{
					wifistasnifferOfflines.add(item_dto);
				}
			}
			//处理最近的探测终端和最热的探测终端
			this.doWifiStasnifferRecentAndHot(mac, wifistasnifferOnlines);
			//处理终端探测流水记录
			this.doWifiStasnifferDetail(mac, wifistasnifferOnlines, wifistasnifferOfflines);
			//TerminalRecentSortedSetService.getInstance().addTerminalRecent(mac, "123456", System.currentTimeMillis());
			SparkTaskLog.wifistasniffer().info(String.format("wifistasnifferSparkHandle mac [%s] success", mac));
		}catch(Exception ex){
			ex.printStackTrace();
			SparkTaskLog.wifistasniffer().error("wifistasnifferSparkHandle mac [%s] failed", ex);
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
		
		SparkTaskLog.wifistasniffer().info(String.format("doWifiStasnifferRecentAndHot mac [%s] terminal [%s]", mac, onlines_size));
		
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
			this.doTerminalLastTime(mac, item_dto.getMac(), item_dto.getSnifftime());
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
	public void doTerminalLastTime(String mac, String hd_mac, long snifftime){
		if(!StringUtils.isEmpty(mac) && !StringUtils.isEmpty(hd_mac)){
			TerminalLastTimeStringService.getInstance().set(mac, hd_mac, snifftime);
		}
	}
	
	/**
	 * 处理终端探测流水记录
	 * @param mac 设备mac
	 * @param wifistasnifferOnlines 上线终端
	 * @param wifistasnifferOfflines 下线终端
	 */
	public void doWifiStasnifferDetail(String mac, Set<WifistasnifferItemRddto> wifistasnifferOnlines,
			Set<WifistasnifferItemRddto> wifistasnifferOfflines){
		//如果存在相同终端的上线和下线效果 进行排重 只保留下线消息即可
		wifistasnifferOnlines.removeAll(wifistasnifferOfflines);
		
		int detail_onlines = wifistasnifferOnlines.size();
		int detail_offlines = wifistasnifferOfflines.size();
		
		SparkTaskLog.wifistasniffer().info(String.format("doWifiStasnifferDetail mac [%s] ol_terminal [%s] of_terminal [%s]", 
				mac, detail_onlines, detail_offlines));
		//处理终端流水上线情况
		if(detail_onlines > 0){
			for(WifistasnifferItemRddto item_dto : wifistasnifferOnlines){
				//String detail_item_value = WifiStasnifferBuilder.generateDetailItemValue(item_dto);
				//if(!StringUtils.isEmpty(detail_item_value)){
				TerminalDetailDTO online_dto = new TerminalDetailDTO();
				online_dto.setSnifftime(item_dto.getSnifftime());
				TerminalDetailRecentSortedSetService.getInstance().addTerminalDetailOnline(mac, item_dto.getMac(),
						online_dto);
				//}
			}
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
				offline_dto.setState(WifistasnifferItemRddto.State_Online);
				TerminalDetailRecentSortedSetService.getInstance().addTerminalDetailOffline(mac, item_dto.getMac(),
						offline_dto);
				//}
			}
		}
	}
	

}
