package com.bhu.vas.business.warmup;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.business.device.service.WifiDeviceService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.orm.support.criteria.CommonCriteria;
/**
 * warmup 主要是防止访问中初始化时间开销相对大的操作而且进行的预热程序
 * 1：数据库连接池预热
 * 2：redis 预热
 * 3：
 * @author tangzichao
 *
 */
@Service
public class WarmupService {
	private final Logger logger = LoggerFactory.getLogger(WarmupService.class);
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@PostConstruct
	public void initialize(){
		logger.info("warmup service start initialize...");
		//1：数据库连接池预热
		if(warmup_db()){
			logger.info(String.format("warmup db success at [%s]", DateTimeHelper.getDateTime()));
		}else{
			logger.info(String.format("warmup db failed at [%s]", DateTimeHelper.getDateTime()));
		}
		
	}
	/**
	 * 进行数据库的连接池初始化
	 * @return
	 */
	public boolean warmup_db(){
		try{
			CommonCriteria mc = new CommonCriteria();
			mc.setStart(0);
			mc.setSize(10);
			wifiDeviceService.findModelByCommonCriteria(mc);
			
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(ex.getMessage(), ex);
			return false;
		}
		return true;
	}
}
