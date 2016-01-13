package com.bhu.vas.plugins.quartz;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.api.helper.DeviceHelper;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
/**
 * 此类用于临时修复设备部分指令的发送
 * 一般不加在quartz配置文件中，临时加入
 * 目前用于关闭增值指令
 * @author Edmond Lee
 *
 */
public class WifiDeviceOnlineTmpCmdDownLoader {
	private static Logger logger = LoggerFactory.getLogger(WifiDeviceOnlineTmpCmdDownLoader.class);
	
	@Resource
	private IDaemonRpcService daemonRpcService;
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	public void execute() {
		logger.info("WifiDeviceOnlineTmpCmdDownLoader starting...");
		List<String> hdTypes = new ArrayList<String>();
		hdTypes.add("H106");
		//Mass AP H103 H110
		hdTypes.add("H103");
		hdTypes.add("H110");
		//Mass AP Pro H201 H303
		hdTypes.add("H201");
		hdTypes.add("H303");
		int total = 0;
		//List<DownCmds> downCmds = new ArrayList<DownCmds>();
		try{
			ModelCriteria mc = new ModelCriteria();
			mc.createCriteria().andColumnIn("hdtype", hdTypes).andColumnEqualTo("online", 1);//.andColumnNotEqualTo("orig_model", WifiDeviceHelper.WIFI_URouter_DEVICE_ORIGIN_MODEL);
	    	mc.setPageNumber(1);
	    	mc.setPageSize(100);
			EntityIterator<String, WifiDevice> it = new KeyBasedEntityBatchIterator<String,WifiDevice>(String.class
					,WifiDevice.class, wifiDeviceService.getEntityDao(), mc);
			while(it.hasNext()){
				//downTask.setPayload(CMDBuilder.autoBuilderVapFullCMD4Opt(mac, downTask.getId(), DeviceHelper.DeviceSetting_VapModuleFull_Stop));
				List<String> next = it.nextKeys();
				for(String mac:next){
					//logger.info(String.format("id[%s] orig_model[%s] cmd[%s]", device.getId(),device.getOrig_model(),payloads));
					//downCmds.add(DownCmds.builderDownCmds(mac, CMDBuilder.autoBuilderVapFullCMD4Opt(mac, CMDBuilder.auto_taskid_fragment.getNextSequence(), DeviceHelper.DeviceSetting_VapModuleFull_Stop)));
					String cmd = CMDBuilder.autoBuilderVapFullCMD4Opt(mac, CMDBuilder.auto_taskid_vapstop_fragment.getNextSequence(), DeviceHelper.DeviceSetting_VapModuleFull_Stop);
					boolean ret = daemonRpcService.wifiDeviceCmdDown(null, mac, cmd);
					Thread.sleep(50);
					//System.out.println(ret+"  "+ cmd);
					total++;
				}
				/*if(!downCmds.isEmpty()){
					daemonRpcService.wifiMultiDevicesCmdsDown(downCmds.toArray(new DownCmds[0]));
					downCmds.clear();
				}*/
			}
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(ex.getMessage(), ex);
		}finally{
			/*if(downCmds != null){
				downCmds.clear();
				downCmds = null;
			}*/
		}
		logger.info(String.format("WifiDeviceOnlineTmpCmdDownLoader ended total[%s]",total));
	}
}
