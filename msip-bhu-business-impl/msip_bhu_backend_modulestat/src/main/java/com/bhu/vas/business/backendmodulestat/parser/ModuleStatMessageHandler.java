package com.bhu.vas.business.backendmodulestat.parser;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.bhu.vas.api.dto.modulestat.WifiDeviceModuleStatDTO;
import com.smartwork.msip.cores.helper.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.smartwork.async.messagequeue.kafka.parser.iface.IMessageHandler;

/**
 * 周边探测终端handle
 * 1) 周边探测业务数据持久化
 * 2) 周边探测push发送
 * @author tangzichao
 *
 */
public class ModuleStatMessageHandler implements IMessageHandler<byte[]>{
	private final Logger logger = LoggerFactory.getLogger(ModuleStatMessageHandler.class);

	//@Resource
	//private BusinessWSCacheService businessWSCacheService;
	
	//@Resource
	//private PushService pushService;
	
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
				System.out.println(String.format("WSMessageHandler [%s]", message));
				/*WifistasnifferRddto dto = JsonHelper.getDTO(message, WifistasnifferRddto.class);
				//进行周边探测业务数据持久化
				this.doBusinessWifistasnifferData(dto);
				//进行push消息的发送
				this.doPushMessage(dto);*/

				WifiDeviceModuleStatDTO dto = JsonHelper.getDTO(message, WifiDeviceModuleStatDTO.class);
				
			}
		}
		
		
	}
}
