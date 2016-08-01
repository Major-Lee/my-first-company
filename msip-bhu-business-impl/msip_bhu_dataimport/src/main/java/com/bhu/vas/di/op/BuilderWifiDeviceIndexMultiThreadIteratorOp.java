package com.bhu.vas.di.op;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

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
import com.smartwork.msip.cores.orm.iterator.IteratorMultiThreadNotify;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchMultiThreadIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
/**
 * 全量创建wifiDevice的索引数据 多线程
 * @author lawliet
 *
 */
public class BuilderWifiDeviceIndexMultiThreadIteratorOp {

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

	private static int pageSize = 500;

	
	public static void main(String[] argv) throws IOException, ParseException{
		
		try{
			int processesThreadCount = 0;
			if(argv != null && argv.length == 1){
				processesThreadCount = Integer.parseInt(argv[0]);
			}
			//long t0 = System.currentTimeMillis();
			initialize();
			
			ModelCriteria mc = new ModelCriteria();
	    	mc.setPageNumber(1);
	    	mc.setPageSize(500);
	    	KeyBasedEntityBatchMultiThreadIterator<String, WifiDevice> it = new KeyBasedEntityBatchMultiThreadIterator<String,WifiDevice>(
					wifiDeviceService.getEntityDao(), mc, pageSize, processesThreadCount, new IteratorMultiThreadNotify<List<WifiDevice>>(){

						@Override
						public void notifyComming(String processerName, List<WifiDevice> wifiDevices) {
							wifiDeviceIndexs(wifiDevices);
						}

						@Override
						public void notifyCompleted(long totalCompletedCount) {
							wifiDeviceDataSearchService.refresh(true);
							System.exit(1);
						}
					});
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
				User distributor = null;
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
				
				if(wifiDeviceShareConfig != null){
					if (wifiDeviceShareConfig.getDistributor() > 0) {
						distributor = userService.getById(wifiDeviceShareConfig.getDistributor());
					}
				}
				
				doc = WifiDeviceDocumentHelper.fromNormalWifiDevice(wifiDevice, deviceModule, 
						wifiDeviceGray, bindUser, bindUserDNick, tagDevices,
						o_template, (int)hoc, wifiDeviceSharedNetwork, wifiDeviceShareConfig, t_uc_extension ,distributor);
				
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
