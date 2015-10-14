package com.bhu.vas.plugins.quartz;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.handset.HandsetStorageFacadeService;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;

/**
* 每5分钟启动一次
* 统计计算系统数据并写入redis
* 1:总wifi设备数
* 2:总移动设备数
* 3:在线wifi设备数
* 4:在线移动设备数
* 5:总移动设备接入次数 (通过每日定时程序来生成)
* 6:总移动设备访问时长 (通过每日定时程序来生成)
 * @author tangzichao
 *
 */
public class HandsetStatisticsLoader {
	private static Logger logger = LoggerFactory.getLogger(HandsetStatisticsLoader.class);
	
	public void execute() {
		try{
			logger.info("HandsetStatisticsLoader starting...");
			
			//long total = HandsetStorageFacadeService.countAll();
			final AtomicLong total = new AtomicLong(0);
			final AtomicLong online = new AtomicLong(0);
			HandsetStorageFacadeService.iteratorAll(new IteratorNotify<Map<String,String>>(){
				@Override
				public void notifyComming(Map<String, String> t) {
					Iterator<Entry<String, String>> iter = t.entrySet().iterator();
					while(iter.hasNext()){
						Entry<String, String> next = iter.next();
						String value = next.getValue();//value
						if(StringUtils.isNotEmpty(value)){
							if(!value.contains(HandsetDeviceDTO.Action_Offline)){
								online.incrementAndGet();
							}
							total.incrementAndGet();
						}
					}
				}
			});
			HandsetStorageFacadeService.statisticsSet(online.get(),total.get());
			logger.info(String.format("HandsetStatisticsLoader ended online[%s] total[%s]", online.get(),total.get()));
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error("SystemStatisticsLoader throw exception", ex);
		}
	}
	
}
