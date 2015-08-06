package com.bhu.vas.di.op.clear;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.elasticsearch.ElasticsearchException;

import com.bhu.vas.business.bucache.redis.serviceimpl.wifistasniffer.TerminalDetailRecentSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.wifistasniffer.TerminalDeviceTypeCountHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.wifistasniffer.TerminalHotSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.wifistasniffer.TerminalLastTimeStringService;
import com.bhu.vas.business.bucache.redis.serviceimpl.wifistasniffer.TerminalRecentSortedSetService;
import com.smartwork.msip.es.exception.ESException;
/**
 * 清除设备的周边探测记录
 * @author lawliet
 *
 */
public class ClearDeviceWifistasnifferOp {
	public static List<String> device_macs = new ArrayList<String>();
	
	static{
		device_macs.add("62:68:75:f1:10:80");
	}
	
	public static void main(String[] argv) throws ElasticsearchException, ESException, IOException, ParseException{
		
		for(String mac : device_macs){
			try{
				System.out.println("开始清除 " + mac + " 周边探测数据");
				doClearRedis(mac);
				System.out.println("成功清除 " + mac + " 周边探测数据");
			}catch(Exception ex){
				System.out.println("异常清除 " + mac + " 周边探测数据");
				ex.printStackTrace();
			}
		}
	}
	
	public static void doClearRedis(String mac){
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
			}
			String[] recent_array = recent_set.toArray(new String[]{});
			//删除最后一次探测上线时间数据
			TerminalLastTimeStringService.getInstance().dels(mac, recent_array);
			//删除终端探测细节数据
			TerminalDetailRecentSortedSetService.getInstance().dels(mac, recent_array);
			//删除终端探测隔壁老王数据
			TerminalHotSortedSetService.getInstance().del(mac);
			//删除社区类型数据
			TerminalDeviceTypeCountHashService.getInstance().del(mac);
		}while(count == size);
		
		//删除recent探测数据
		TerminalRecentSortedSetService.getInstance().del(mac);
		
	}
}
