package com.bhu.vas.di.op;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceGray;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceModule;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSharedNetwork;
import com.bhu.vas.api.rpc.tag.model.TagDevices;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserDevice;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.ds.device.service.WifiDeviceGrayService;
import com.bhu.vas.business.ds.device.service.WifiDeviceModuleService;
import com.bhu.vas.business.ds.device.service.WifiDevicePersistenceCMDStateService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.device.service.WifiDeviceSharedNetworkService;
import com.bhu.vas.business.ds.tag.service.TagDevicesService;
import com.bhu.vas.business.ds.user.service.UserDeviceService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.model.WifiDeviceDocumentHelper;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
/**
 * 全量创建wifiDevice的索引数据
 * @author lawliet
 *
 */
public class BuilderWifiDeviceIndexOp {

	
	//public static WifiDeviceIndexService wifiDeviceIndexService = null;
	private static WifiDeviceDataSearchService wifiDeviceDataSearchService;
	//private static AgentDeviceClaimService agentDeviceClaimService;
	private static WifiDeviceGrayService wifiDeviceGrayService;
	private static WifiDevicePersistenceCMDStateService wifiDevicePersistenceCMDStateService;
	private static UserService userService;
	private static WifiDeviceService wifiDeviceService;
	private static WifiDeviceModuleService wifiDeviceModuleService;
	private static UserDeviceService userDeviceService;
	private static TagDevicesService tagDevicesService;
	private static WifiDeviceSharedNetworkService wifiDeviceSharedNetworkService;
	
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
			userDeviceService = (UserDeviceService)ctx.getBean("userDeviceService");
			tagDevicesService = (TagDevicesService)ctx.getBean("tagDevicesService");
			wifiDeviceSharedNetworkService = (WifiDeviceSharedNetworkService)ctx.getBean("wifiDeviceSharedNetworkService");
			
			long t0 = System.currentTimeMillis();
			
			//agentsDevicesClaimIndexs();
			wifiDeviceIndexs();
			
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
	public static void wifiDeviceIndexs(){
		int bulk_success = 0;//批量成功次数
		int index_count = 0;//建立的索引数据条数
		
		try{
			ModelCriteria mc = new ModelCriteria();
	    	mc.setPageNumber(1);
	    	mc.setPageSize(500);
			EntityIterator<String, WifiDevice> it = new KeyBasedEntityBatchIterator<String,WifiDevice>(String.class
					,WifiDevice.class, wifiDeviceService.getEntityDao(), mc);
			while(it.hasNext()){
				List<WifiDeviceDocument> docs = new ArrayList<WifiDeviceDocument>();
				WifiDeviceDocument doc = null;
				List<WifiDevice> entitys = it.next();
				for(WifiDevice wifiDevice : entitys){
					String mac = wifiDevice.getId();
					//System.out.println("2="+mac);
					WifiDeviceGray wifiDeviceGray = wifiDeviceGrayService.getById(mac);
					WifiDeviceModule deviceModule = wifiDeviceModuleService.getById(mac);
					//TagDevices tagDevices = tagDevicesService.getById(mac);
					//AgentDeviceClaim agentDeviceClaim = agentDeviceClaimService.getById(wifiDevice.getSn());
					String o_template = wifiDevicePersistenceCMDStateService.fetchDeviceVapModuleStyle(mac);
					long hoc = WifiDeviceHandsetPresentSortedSetService.getInstance().presentOnlineSize(mac);
					//long hoc = 0;
/*					User agentUser = null;
					if(wifiDevice.getAgentuser() > 0){
						agentUser = userService.getById(wifiDevice.getAgentuser());
					}*/
					
					User bindUser = null;
					String bindUserDNick = null;
					//Integer bindUserId = userDeviceService.fetchBindUid(mac);
					UserDevice userDevice = userDeviceService.fetchBindByMac(mac);
					if(userDevice != null){
						bindUserDNick = userDevice.getDevice_name();
						Integer bindUserId = userDevice.getId().getUid();
						if(bindUserId != null){
							bindUser = userService.getById(bindUserId);
						}
					}
					
					WifiDeviceSharedNetwork wifiDeviceSharedNetwork = wifiDeviceSharedNetworkService.getById(mac);
					
					doc = WifiDeviceDocumentHelper.fromNormalWifiDevice(wifiDevice, deviceModule, 
							wifiDeviceGray, bindUser, bindUserDNick, null,
							o_template, (int)hoc, wifiDeviceSharedNetwork);
					
/*					//构建设备索引的扩展字段
					WifiDeviceSharedNetwork wifiDeviceSharedNetwork = wifiDeviceSharedNetworkService.getById(mac);
					doc = WifiDeviceDocumentHelper.fromExtension(doc, wifiDeviceSharedNetwork);*/
					
					if(doc != null){
						docs.add(doc);
						index_count++;
					}
				}
				
				if(!docs.isEmpty()){
					wifiDeviceDataSearchService.bulkIndex(docs);
					bulk_success++;
					System.out.println("bluk successed");
				}
			}
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}
		
		System.out.println("wifiDevice 数据全量导入 成功批量次数:" + bulk_success + " 一共索引数量:" + index_count);
	}
	
}
