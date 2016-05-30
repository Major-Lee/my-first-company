package com.bhu.vas.init;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDevicePresentCtxService;
import com.bhu.vas.daemon.SessionManager;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;

/**
 * 系统重新启动后加载数据库中在线标记的设备
 * 初始化session数据
 * @author Edmond
 *
 */
@Service
public class DaemonInitService {
	private final Logger logger = LoggerFactory.getLogger(DaemonInitService.class);
	//@Resource
	//private WifiDeviceService wifiDeviceService;
	
	@PostConstruct
	public void initialize(){
		//System.out.println("DaemonInitService start initialize...");
		logger.info("DaemonInitService start initialize...");
		//1：数据库连接池预热
		if(execute()){
			//System.out.println(String.format("init success at [%s]", DateTimeHelper.getDateTime()));
			logger.info(String.format("init success at [%s]", DateTimeHelper.getDateTime()));
		}else{
			//System.out.println(String.format("init failed at [%s]", DateTimeHelper.getDateTime()));
			logger.info(String.format("init failed at [%s]", DateTimeHelper.getDateTime()));
		}
		//74-27-EA   (hex)		Elitegroup Computer Systems Co., Ltd.
	}
	//TODO:如果在线设备量级太多如果加载数据 || 通过线程执行
	public boolean execute(){
		//WifiDevicePresentCtxService.getInstance().iteratorAll(new );
		/*ModelCriteria mc = new ModelCriteria();
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
		}*/
		final AtomicInteger atomic = new AtomicInteger(0);
		//System.out.println("~~~~~~~~~~ gogogo:");
		WifiDevicePresentCtxService.getInstance().iteratorAll(new IteratorNotify<Map<String,String>>(){
			@Override
			public void notifyComming(Map<String, String> onlineMap) {
				//System.out.println("~~~~~~~~~~:"+onlineMap);
				Iterator<Entry<String, String>> iter = onlineMap.entrySet().iterator();
				while(iter.hasNext()){
					Entry<String, String> next = iter.next();
					String key = next.getKey();//mac
					String value = next.getValue();//ctx
					SessionManager.getInstance().addSession(key, value);
					atomic.incrementAndGet();
					//System.out.println(String.format("Online device[%s] ctx[%s]", key,value));
				}
				//System.out.println(t);
			}
		});
		System.out.println(String.format("Init Online count[%s] Ok~~~~~~",atomic.get()));
		//logger.info(String.format("Init Online total[%s] count[%s]", it.getTotalItemsCount(),count));
		//System.out.println(String.format("Init Online total[%s] count[%s]", it.getTotalItemsCount(),count));
		return true;
	}
}
