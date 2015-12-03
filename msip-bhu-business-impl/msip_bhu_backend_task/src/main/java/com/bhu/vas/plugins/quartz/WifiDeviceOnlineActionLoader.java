package com.bhu.vas.plugins.quartz;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.dto.DownCmds;
import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.api.helper.WifiDeviceHelper;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.business.bucache.redis.serviceimpl.marker.BusinessMarkerService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
/**
 * 每60分钟执行一次
 * 对所有在线商业wifi发送在线终端查询指令（此业务在商业wif【非uRouter】上线不发送触发终端上下线请求）
 * 对非商业wifi设备（uRouter）
 * 	  下发设备状态查询
 * @author Edmond Lee
 *
 */
public class WifiDeviceOnlineActionLoader {
	private static Logger logger = LoggerFactory.getLogger(WifiDeviceOnlineActionLoader.class);
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private IDaemonRpcService daemonRpcService;	
	
	public void execute() {
		logger.info("WifiDeviceOnlineActionLoader starting...");
		int total = 0;
		//String cmdPayload = null;
		List<DownCmds> downCmds = new ArrayList<DownCmds>();
		try{
			ModelCriteria mc = new ModelCriteria();
			mc.createCriteria().andColumnEqualTo("online", 1);//.andColumnNotEqualTo("orig_model", WifiDeviceHelper.WIFI_URouter_DEVICE_ORIGIN_MODEL);
	    	mc.setPageNumber(1);
	    	mc.setPageSize(250);
			EntityIterator<String, WifiDevice> it = new KeyBasedEntityBatchIterator<String,WifiDevice>(String.class
					,WifiDevice.class, wifiDeviceService.getEntityDao(), mc);
			while(it.hasNext()){
				List<WifiDevice> next = it.next();
				for(WifiDevice device:next){
					{
						Set<String> payloads =  new HashSet<String>();
						if(WifiDeviceHelper.isURouterDevice(device.getOrig_swver())){
							//确定是否需要下发指令
							boolean needDeviceUsedQuery = BusinessMarkerService.getInstance().needNewRequestAndMarker(device.getId(),false);
							if(needDeviceUsedQuery){
								payloads.add(CMDBuilder.builderDeviceUsedStatusQuery(device.getId()));
							}
							if(StringUtils.isEmpty(device.getLat()) || StringUtils.isEmpty(device.getLon())){
								payloads.add(CMDBuilder.builderDeviceLocationNotifyQuery(device.getId()));
							}
							
						}else{
							//非uRouter设备定时下发查询终端在线指令
							payloads.add(CMDBuilder.builderQuerySyncDeviceOnlineTerminalsQuery(device.getId()));
							//cmdPayload = CMDBuilder.builderQuerySyncDeviceOnlineTerminalsQuery(device.getId());
							//downCmds.add(DownCmds.builderDownCmds(device.getId(), new String[]{cmdPayload}));
						}
						if(!payloads.isEmpty()){
							//logger.info(String.format("id[%s] orig_model[%s] cmd[%s]", device.getId(),device.getOrig_model(),payloads));
							downCmds.add(DownCmds.builderDownCmds(device.getId(), payloads.toArray(new String[0])));
							payloads.clear();
							payloads = null;
						}
					}
					total++;
				}
				if(!downCmds.isEmpty()){
					daemonRpcService.wifiMultiDevicesCmdsDown(downCmds.toArray(new DownCmds[0]));
					downCmds.clear();
				}
			}
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(ex.getMessage(), ex);
		}finally{
			if(downCmds != null){
				downCmds.clear();
				downCmds = null;
			}
		}
		logger.info(String.format("WifiDeviceOnlineActionLoader ended, total devices [%s]", total));
	}
}
