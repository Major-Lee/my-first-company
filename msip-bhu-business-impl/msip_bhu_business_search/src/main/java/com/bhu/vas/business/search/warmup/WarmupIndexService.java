package com.bhu.vas.business.search.warmup;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.business.search.service.device.WifiDeviceIndexService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
/**
 * warmup 主要是防止访问中初始化时间开销相对大的操作而且进行的预热程序
 * 1：wifi设备索引库的初始化
 * @author tangzichao
 *
 */

public class WarmupIndexService {
	private final Logger logger = LoggerFactory.getLogger(WarmupIndexService.class);
	
	@Resource
	private WifiDeviceIndexService wifiDeviceIndexService;
	
	@PostConstruct
	public void initialize(){
		logger.info("warmup index service start initialize...");
		//1：进行索引库的初始化
		if(warmup_wifi_index()){
			logger.info(String.format("warmup wifi index success at [%s]", DateTimeHelper.getDateTime()));
		}else{
			logger.info(String.format("warmup wifi index failed at [%s]", DateTimeHelper.getDateTime()));
		}
		
	}
	/**
	 * 进行索引库的初始化
	 * @return
	 */
	public boolean warmup_wifi_index(){
		try{
			wifiDeviceIndexService.warmup();
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(ex.getMessage(), ex);
			return false;
		}
		return true;
	}
}
