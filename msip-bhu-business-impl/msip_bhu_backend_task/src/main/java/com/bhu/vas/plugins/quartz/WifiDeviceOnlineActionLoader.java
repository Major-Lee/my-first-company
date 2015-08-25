package com.bhu.vas.plugins.quartz;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.api.helper.WifiDeviceHelper;
import com.bhu.vas.api.rpc.daemon.helper.DaemonHelper;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
/**
 * 每60分钟执行一次
 * 对所有在线商业wifi发送在线终端查询指令（此业务在商业wif【非uRouter】上线不发送触发终端上下线请求）
 * 如果在线设备存在经纬度，但是没有获取详细地址，也会进行获取
 * @author tangzichao
 *
 */
public class WifiDeviceOnlineActionLoader {
	private static Logger logger = LoggerFactory.getLogger(WifiDeviceOnlineActionLoader.class);
	
	public int bulk_success = 0;
	public int bulk_fail = 0;
	public int index_count = 0;
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private IDaemonRpcService daemonRpcService;	
	
	public void execute() {
		logger.info("WifiDeviceOnlineActionLoader starting...");
		int total = 0;
		String cmdPayload = null;
		try{
			ModelCriteria mc = new ModelCriteria();
			mc.createCriteria().andColumnEqualTo("online", 1).andColumnNotEqualTo("orig_model", WifiDeviceHelper.WIFI_URouter_DEVICE_ORIGIN_MODEL);
	    	mc.setPageNumber(1);
	    	mc.setPageSize(500);
			EntityIterator<String, WifiDevice> it = new KeyBasedEntityBatchIterator<String,WifiDevice>(String.class
					,WifiDevice.class, wifiDeviceService.getEntityDao(), mc);
			while(it.hasNext()){
				List<WifiDevice> next = it.next();
				
				//List<String> payloads = new ArrayList<>();
				for(WifiDevice device:next){
					if("84:82:f4:17:c2:94".equalsIgnoreCase(device.getId())){
						cmdPayload = CMDBuilder.builderDeviceTerminalsQuery(device.getId(), 
								CMDBuilder.auto_special_query_commercial_terminals_taskid_fragment.getNextSequence(), 
								10, 
								60);
						DaemonHelper.daemonCmdDown(device.getId(), cmdPayload, daemonRpcService);
						System.out.println(String.format("id[%s] orig_model[%s] cmd[%s]", device.getId(),device.getOrig_model(),cmdPayload));
					}
					total++;
				}
			}
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(ex.getMessage(), ex);
		}finally{
		}
		logger.info(String.format("WifiDeviceOnlineActionLoader ended, total devices [%s]", total));
	}
}
