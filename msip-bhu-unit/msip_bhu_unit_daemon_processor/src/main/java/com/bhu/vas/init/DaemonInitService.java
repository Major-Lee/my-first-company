package com.bhu.vas.init;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDevicePresentService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.daemon.SessionManager;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.IdHelper;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

/**
 * 系统重新启动后加载数据库中在线标记的设备
 * 初始化session数据
 * @author Edmond
 *
 */
@Service
public class DaemonInitService {
	private final Logger logger = LoggerFactory.getLogger(DaemonInitService.class);
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@PostConstruct
	public void initialize(){
		logger.info("DaemonInitService start initialize...");
		//1：数据库连接池预热
		if(execute()){
			logger.info(String.format("init success at [%s]", DateTimeHelper.getDateTime()));
		}else{
			logger.info(String.format("init failed at [%s]", DateTimeHelper.getDateTime()));
		}
		
	}
	
	public boolean execute(){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("online", 1).andSimpleCaulse(" 1=1 ");//.andColumnIsNotNull("lat").andColumnIsNotNull("lon");//.andColumnEqualTo("online", 1);
    	mc.setPageNumber(1);
    	mc.setPageSize(200);
    	int count = 0;
		EntityIterator<String, WifiDevice> it = new KeyBasedEntityBatchIterator<String,WifiDevice>(String.class
				,WifiDevice.class, wifiDeviceService.getEntityDao(), mc);
		while(it.hasNext()){
			List<WifiDevice> entitys = it.next();
			List<String> ids = IdHelper.getStringIds(entitys);
			List<String> presents = WifiDevicePresentService.getInstance().getPresents(ids);
			if(presents !=null && !presents.isEmpty()){
				int index = 0;
				for(String ctx:presents){
					if(!StringUtils.isEmpty(ctx)){
						String mac = ids.get(index);
						SessionManager.getInstance().addSession(mac, ctx);
						logger.info(String.format("Online device[%s] ctx[%s]", mac,ctx));
						System.out.println(String.format("Online device[%s] ctx[%s]", mac,ctx));
						count++;
					}
					index++;
				}
			}
		}
		logger.info(String.format("Init Online total[%s] count[%s]", it.getTotalItemsCount(),count));
		System.out.println(String.format("Init Online total[%s] count[%s]", it.getTotalItemsCount(),count));
		return true;
	}
}
