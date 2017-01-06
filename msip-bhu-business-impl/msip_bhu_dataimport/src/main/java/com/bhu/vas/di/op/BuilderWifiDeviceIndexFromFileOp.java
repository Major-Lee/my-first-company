package com.bhu.vas.di.op;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.common.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.api.rpc.charging.model.WifiDeviceSharedealConfigs;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceGray;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceModule;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSharedNetwork;
import com.bhu.vas.api.rpc.tag.model.TagDevices;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserWifiDevice;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.ds.charging.service.WifiDeviceSharedealConfigsService;
import com.bhu.vas.business.ds.device.service.WifiDeviceGrayService;
import com.bhu.vas.business.ds.device.service.WifiDeviceModuleService;
import com.bhu.vas.business.ds.device.service.WifiDevicePersistenceCMDStateService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.device.service.WifiDeviceSharedNetworkService;
import com.bhu.vas.business.ds.tag.service.TagDevicesService;
import com.bhu.vas.business.ds.tag.service.TagGroupRelationService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.ds.user.service.UserWifiDeviceService;
import com.bhu.vas.business.search.model.device.WifiDeviceDocument;
import com.bhu.vas.business.search.model.device.WifiDeviceDocumentHelper;
import com.bhu.vas.business.search.service.device.WifiDeviceDataSearchService;
/**
 * 全量创建wifiDevice的索引数据
 * @author lawliet
 *
 */
public class BuilderWifiDeviceIndexFromFileOp {

	
	//public static WifiDeviceIndexService wifiDeviceIndexService = null;
	private static WifiDeviceDataSearchService wifiDeviceDataSearchService;
	//private static AgentDeviceClaimService agentDeviceClaimService;
	private static WifiDeviceGrayService wifiDeviceGrayService;
	private static WifiDevicePersistenceCMDStateService wifiDevicePersistenceCMDStateService;
	private static UserService userService;
	private static WifiDeviceService wifiDeviceService;
	private static WifiDeviceModuleService wifiDeviceModuleService;
//	private static UserDeviceService userDeviceService;
	private static UserWifiDeviceService userWifiDeviceService;
	private static TagDevicesService tagDevicesService;
	private static WifiDeviceSharedNetworkService wifiDeviceSharedNetworkService;
	private static WifiDeviceSharedealConfigsService wifiDeviceSharedealConfigsService;
	private static TagGroupRelationService tagGroupRelationService;
	
	public static void main(String[] argv) throws IOException, ParseException{
		
		try{
			
			ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
	
			//wifiDeviceIndexService = (WifiDeviceIndexService)ctx.getBean("wifiDeviceIndexService");
			
			wifiDeviceDataSearchService = (WifiDeviceDataSearchService)ctx.getBean("wifiDeviceDataSearchService");
			//agentDeviceClaimService = (AgentDeviceClaimService)ctx.getBean("agentDeviceClaimService");
			wifiDeviceGrayService = (WifiDeviceGrayService)ctx.getBean("wifiDeviceGrayService");
			wifiDevicePersistenceCMDStateService = (WifiDevicePersistenceCMDStateService)ctx.getBean("wifiDevicePersistenceCMDStateService");
			userService = (UserService)ctx.getBean("userService");
			
			wifiDeviceService = (WifiDeviceService)ctx.getBean("wifiDeviceService");
			wifiDeviceModuleService= (WifiDeviceModuleService)ctx.getBean("wifiDeviceModuleService");
			//userDeviceService = (UserDeviceService)ctx.getBean("userDeviceService");
			userWifiDeviceService = (UserWifiDeviceService)ctx.getBean("userWifiDeviceService");
			tagDevicesService = (TagDevicesService)ctx.getBean("tagDevicesService");
			wifiDeviceSharedNetworkService = (WifiDeviceSharedNetworkService)ctx.getBean("wifiDeviceSharedNetworkService");
			wifiDeviceSharedealConfigsService = (WifiDeviceSharedealConfigsService)ctx.getBean("wifiDeviceSharedealConfigsService");
			tagGroupRelationService = (TagGroupRelationService)ctx.getBean("tagGroupRelationService");
			
			long t0 = System.currentTimeMillis();
			
			
			wifiDeviceIndexs(argv[0]);
			
			wifiDeviceDataSearchService.refresh(true);
			
			System.out.println("数据全量导入，总耗时"+((System.currentTimeMillis()-t0)/1000)+"s");

		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}finally{

		}
		System.exit(1);
	}
	
	/**
	 * 从t_agents_devices_claim表中创建索引数据
	 */
/*	public static void agentsDevicesClaimIndexs(){
		int bulk_success = 0;//批量成功次数
		int index_count = 0;//建立的索引数据条数
		
		try{
			ModelCriteria mc = new ModelCriteria();
	//		//如果是初始化，会全表遍历
	//		if(!inited){
	//			//从未上线的应该是 import_status = 1, status = 0
	//			mc.createCriteria().andColumnEqualTo("import_status", 1);
	//			mc.createCriteria().andColumnEqualTo("status", 0);
	//		}
			mc.createCriteria().andColumnEqualTo("import_status", 1);
			mc.createCriteria().andColumnEqualTo("status", 0);
	    	mc.setPageNumber(1);
	    	mc.setPageSize(500);
			EntityIterator<String, AgentDeviceClaim> it = new KeyBasedEntityBatchIterator<String,AgentDeviceClaim>(String.class
					,AgentDeviceClaim.class, agentDeviceClaimService.getEntityDao(), mc);
			while(it.hasNext()){
				List<WifiDeviceDocument> docs = new ArrayList<WifiDeviceDocument>();
				WifiDeviceDocument doc = null;
				List<AgentDeviceClaim> entitys = it.next();
				for(AgentDeviceClaim agentDeviceClaim : entitys){
					System.out.println(agentDeviceClaim.getMac());
//					WifiDeviceGray wifiDeviceGray = wifiDeviceGrayService.getById(agentDeviceClaim.getMac());
//					User agentUser = null;
//					if(agentDeviceClaim.getUid() > 0){
//						agentUser = userService.getById(agentDeviceClaim.getUid());
//					}
					doc = WifiDeviceDocumentHelper.fromClaimWifiDevice(agentDeviceClaim);
					if(doc != null){
						docs.add(doc);
						index_count++;
					}
				}
				
				if(!docs.isEmpty()){
					wifiDeviceDataSearchService.bulkIndex(docs);
					bulk_success++;
				}
			}
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}
		
		System.out.println("agentsDevicesClaim 数据全量导入 成功批量次数:" + bulk_success + " 一共索引数量:" + index_count);
	}*/
	
	/**
	 * 从t_wifi_devices表中创建索引数据
	 */
	public static void wifiDeviceIndexs(String filePath) throws Exception{
		int bulk_success = 0;//批量成功次数
		int index_count = 0;//建立的索引数据条数
		List<WifiDeviceDocument> docs = new ArrayList<WifiDeviceDocument>();
        File file = new File(filePath);
        if (file.isFile() && file.exists()) { //判断文件是否存在
            InputStreamReader read = new InputStreamReader(new FileInputStream(file));//考虑到编码格式
            BufferedReader bufferedReader = new BufferedReader(read);
            String mac = null;
            while ((mac = bufferedReader.readLine()) != null) {
            	if(StringUtils.isEmpty(mac))
            		continue;
            	mac = mac.trim().toLowerCase();
            	WifiDevice wifiDevice = wifiDeviceService.getById(mac);

				WifiDeviceGray wifiDeviceGray = wifiDeviceGrayService.getById(mac);
				WifiDeviceModule deviceModule = wifiDeviceModuleService.getById(mac);
				TagDevices tagDevices = tagDevicesService.getById(mac);
				//AgentDeviceClaim agentDeviceClaim = agentDeviceClaimService.getById(wifiDevice.getSn());
				String o_template = wifiDevicePersistenceCMDStateService.fetchDeviceVapModuleStyle(mac);
				long hoc = WifiDeviceHandsetPresentSortedSetService.getInstance().presentOnlineSize(mac);
				//long hoc = 0;
/*					User agentUser = null;
				if(wifiDevice.getAgentuser() > 0){
					agentUser = userService.getById(wifiDevice.getAgentuser());
				}*/
				
				User bindUser = null;
				User distributor = null;
				String bindUserDNick = null;
				//Integer bindUserId = userDeviceService.fetchBindUid(mac);
				//UserDevice userDevice = userDeviceService.fetchBindByMac(mac);
				UserWifiDevice userWifiDevice = userWifiDeviceService.getById(mac);
				if(userWifiDevice != null){
					bindUserDNick = userWifiDevice.getDevice_name();
					Integer bindUserId = userWifiDevice.getUid();
					if(bindUserId != null){
						bindUser = userService.getById(bindUserId);
					}
				}
				
				WifiDeviceSharedNetwork wifiDeviceSharedNetwork = wifiDeviceSharedNetworkService.getById(mac);
				WifiDeviceSharedealConfigs wifiDeviceShareConfig = wifiDeviceSharedealConfigsService.getById(mac);
				String t_uc_extension = tagGroupRelationService.fetchPathWithMac(mac);
				
				if(wifiDeviceShareConfig != null){
					if (wifiDeviceShareConfig.getDistributor() > 0) {
						distributor = userService.getById(wifiDeviceShareConfig.getDistributor());
					}
				}
				
				WifiDeviceDocument doc = WifiDeviceDocumentHelper.fromNormalWifiDevice(wifiDevice, deviceModule, 
						wifiDeviceGray, bindUser, bindUserDNick, tagDevices,
						o_template, (int)hoc, wifiDeviceSharedNetwork, wifiDeviceShareConfig, t_uc_extension, distributor);
				
/*					//构建设备索引的扩展字段
				WifiDeviceSharedNetwork wifiDeviceSharedNetwork = wifiDeviceSharedNetworkService.getById(mac);
				doc = WifiDeviceDocumentHelper.fromExtension(doc, wifiDeviceSharedNetwork);*/
				
				if(doc != null){
					docs.add(doc);
					index_count++;
				}

            }
        }
    				
		if(!docs.isEmpty()){
			wifiDeviceDataSearchService.bulkIndex(docs);
			bulk_success++;
			System.out.println("bluk successed");
		}
		
		System.out.println("wifiDevice 重建索引数:" + index_count);
	}
	
}
