package com.bhu.vas.di.op;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

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
import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.model.WifiDeviceDocumentHelper;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.page.PageHelper;
import com.smartwork.msip.plugins.hook.observer.ExecObserverManager;
/**
 * 全量创建wifiDevice的索引数据 多线程
 * @author lawliet
 *
 */
public class BuilderWifiDeviceIndexMultiThreadOp {

	private static WifiDeviceDataSearchService wifiDeviceDataSearchService;
	private static WifiDeviceGrayService wifiDeviceGrayService;
	private static WifiDevicePersistenceCMDStateService wifiDevicePersistenceCMDStateService;
	private static UserService userService;
	private static WifiDeviceService wifiDeviceService;
	private static WifiDeviceModuleService wifiDeviceModuleService;
	private static UserWifiDeviceService userWifiDeviceService;
	private static TagDevicesService tagDevicesService;
	private static WifiDeviceSharedNetworkService wifiDeviceSharedNetworkService;
	private static WifiDeviceSharedealConfigsService wifiDeviceSharedealConfigsService;
	private static TagGroupRelationService tagGroupRelationService;
	private static ThreadPoolExecutor exec_processes;
	private static ExecutorService exec_monitor;
	private static int processesThreadCount = 5;
	private static ConcurrentLinkedQueue<Integer> pageQueue = null;
	private static int pageSize = 500;
	private static int totalCompletedCount = 0;
	
	public static void main(String[] argv) throws IOException, ParseException{
		
		try{
			if(argv != null && argv.length == 1){
				processesThreadCount = Integer.parseInt(argv[0]);
			}
			//long t0 = System.currentTimeMillis();
			initialize();
			generatePageQueue();
			if(pageQueue == null){
				System.out.println("PageQueue Not Generate");
				return;
			}
			monitor();
			dispatch();
			
			//wifiDeviceDataSearchService.refresh(true);
			
			//System.out.println("数据全量导入，总耗时"+((System.currentTimeMillis()-t0)/1000)+"s");

		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}finally{

		}
		//System.exit(1);
	}
	
	public static void initialize(){
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		wifiDeviceDataSearchService = (WifiDeviceDataSearchService)ctx.getBean("wifiDeviceDataSearchService");
		wifiDeviceGrayService = (WifiDeviceGrayService)ctx.getBean("wifiDeviceGrayService");
		wifiDevicePersistenceCMDStateService = (WifiDevicePersistenceCMDStateService)ctx.getBean("wifiDevicePersistenceCMDStateService");
		userService = (UserService)ctx.getBean("userService");
		wifiDeviceService = (WifiDeviceService)ctx.getBean("wifiDeviceService");
		wifiDeviceModuleService= (WifiDeviceModuleService)ctx.getBean("wifiDeviceModuleService");
		userWifiDeviceService = (UserWifiDeviceService)ctx.getBean("userWifiDeviceService");
		tagDevicesService = (TagDevicesService)ctx.getBean("tagDevicesService");
		wifiDeviceSharedNetworkService = (WifiDeviceSharedNetworkService)ctx.getBean("wifiDeviceSharedNetworkService");
		wifiDeviceSharedealConfigsService = (WifiDeviceSharedealConfigsService)ctx.getBean("wifiDeviceSharedealConfigsService");
		tagGroupRelationService = (TagGroupRelationService)ctx.getBean("tagGroupRelationService");
	}
	
	
	public static void dispatch(){
		exec_processes = (ThreadPoolExecutor)ExecObserverManager.buildExecutorService(BuilderWifiDeviceIndexMultiThreadOp.class,
				"BuilderWifiDeviceIndexMultiThreadOp processes索引处理", processesThreadCount);
		System.out.println("Start Dispatch Exec_processes");
		for(int i = 0;i<processesThreadCount;i++){
			exec_processes.submit((new Runnable() {
				@Override
				public void run() {
					String processerName = Thread.currentThread().getName();
					System.out.println(String.format("Exec_processer[%s] started", processerName));
					int records = 0;
					while(true){
						try{
							Integer pageNo = pageQueue.poll();
							if(pageNo == null){
								System.out.println(String.format("Exec_processer[%s] completed records[%s]", 
										processerName, records));
								totalCompletedCount += records;
								break;
							}
							System.out.println(String.format("Exec_processer[%s] excuting pageNo[%s]", 
									processerName, pageNo));
							
							ModelCriteria mc = new ModelCriteria();
					    	mc.setPageNumber(pageNo);
					    	mc.setPageSize(pageSize);
					    	List<WifiDevice> wifiDevices = wifiDeviceService.findModelByModelCriteria(mc);
					    	wifiDeviceIndexs(wifiDevices);
					    	records += wifiDevices.size();
						}catch(Exception ex){
							ex.printStackTrace(System.out);
						}
					}
				}
			}));
		}
	}
	
	/**
	 * 生成页码数据队列
	 */
	public static void generatePageQueue(){
		long count = wifiDeviceService.count();
		if(count == 0) return;
		int totalPageCount = PageHelper.getTotalPages((int)count, pageSize);
		pageQueue = new ConcurrentLinkedQueue<Integer>();
		for(int i = 1;i<totalPageCount+1;i++){
			pageQueue.offer(i);
		}
	}
	
	public static void monitor(){
		exec_monitor = ExecObserverManager.buildExecutorService(BuilderWifiDeviceIndexMultiThreadOp.class, 
				"BuilderWifiDeviceIndexMultiThreadOp monitor",1);
		exec_monitor.submit((new Runnable() {
			@Override
			public void run() {
				long t0 = System.currentTimeMillis();
				while(true){
					if(pageQueue.isEmpty() && exec_processes.getActiveCount() == 0){
						break;
					}
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				System.out.println(String.format("数据全量导入总数据[%s] 总耗时[%s]秒", totalCompletedCount, ((System.currentTimeMillis()-t0)/1000)));
				System.exit(1);
			}
		}));
	}
	
	/**
	 * 从t_wifi_devices表中创建索引数据
	 */
	public static void wifiDeviceIndexs(List<WifiDevice> entitys){
		try{
			List<WifiDeviceDocument> docs = new ArrayList<WifiDeviceDocument>();
			WifiDeviceDocument doc = null;
			for(WifiDevice wifiDevice : entitys){
				String mac = wifiDevice.getId();
				WifiDeviceGray wifiDeviceGray = wifiDeviceGrayService.getById(mac);
				WifiDeviceModule deviceModule = wifiDeviceModuleService.getById(mac);
				TagDevices tagDevices = tagDevicesService.getById(mac);
				String o_template = wifiDevicePersistenceCMDStateService.fetchDeviceVapModuleStyle(mac);
				long hoc = WifiDeviceHandsetPresentSortedSetService.getInstance().presentOnlineSize(mac);
				User bindUser = null;
				String bindUserDNick = null;
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
				
				doc = WifiDeviceDocumentHelper.fromNormalWifiDevice(wifiDevice, deviceModule, 
						wifiDeviceGray, bindUser, bindUserDNick, tagDevices,
						o_template, (int)hoc, wifiDeviceSharedNetwork, wifiDeviceShareConfig, t_uc_extension);
				
/*					//构建设备索引的扩展字段
					WifiDeviceSharedNetwork wifiDeviceSharedNetwork = wifiDeviceSharedNetworkService.getById(mac);
					doc = WifiDeviceDocumentHelper.fromExtension(doc, wifiDeviceSharedNetwork);*/
				
				if(doc != null){
					docs.add(doc);
				}
			}
			
			if(!docs.isEmpty()){
				wifiDeviceDataSearchService.bulkIndex(docs);
			}
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}
	}
	
}
