package com.bhu.vas.di.op;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceGroupRelationService;
import com.bhu.vas.business.ds.device.service.WifiDeviceModuleService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
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
	public static int bulk_success = 0;//批量成功次数
	public static int bulk_fail = 0;//批量失败次数
	public static int index_count = 0;//建立的索引数据条数
	
	//public static WifiDeviceIndexService wifiDeviceIndexService = null;
	private static WifiDeviceDataSearchService wifiDeviceDataSearchService;
	public static DeviceFacadeService deviceFacadeService = null;
	public static WifiDeviceService wifiDeviceService = null;
	
	public static WifiDeviceModuleService wifiDeviceModuleService;
	public static WifiDeviceGroupRelationService wifiDeviceGroupRelationService = null;
	
	public static void main(String[] argv) throws IOException, ParseException{
		
		try{
			/*//是否需要更新索引库mapping
			boolean updated_mapping = false;
			//是否需要更新wifi设备的地理位置
			boolean updated_geocoding = false;
			if(argv != null){
				if(argv.length == 1){
					updated_mapping = Boolean.valueOf(argv[0]);
				}
				else if(argv.length == 2){
					updated_mapping = Boolean.valueOf(argv[0]);
					updated_geocoding = Boolean.valueOf(argv[1]);
				}
			}*/
			
			ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
	
			//wifiDeviceIndexService = (WifiDeviceIndexService)ctx.getBean("wifiDeviceIndexService");
			
			wifiDeviceDataSearchService = (WifiDeviceDataSearchService)ctx.getBean("wifiDeviceDataSearchService");
			
			wifiDeviceService = (WifiDeviceService)ctx.getBean("wifiDeviceService");
			wifiDeviceModuleService= (WifiDeviceModuleService)ctx.getBean("wifiDeviceModuleService");
			deviceFacadeService = (DeviceFacadeService)ctx.getBean("deviceFacadeService");
			wifiDeviceGroupRelationService = (WifiDeviceGroupRelationService)ctx.getBean("wifiDeviceGroupRelationService");
			
			long t0 = System.currentTimeMillis();
/*			//建立索引库, 如果库存在, 不会重新创建
			wifiDeviceIndexService.createResponse();
			if(updated_mapping){
				wifiDeviceIndexService.createMapping();
			}
			
			wifiDeviceIndexService.disableIndexRefresh();*/
			
			ModelCriteria mc = new ModelCriteria();
	    	mc.setPageNumber(1);
	    	mc.setPageSize(500);
			EntityIterator<String, WifiDevice> it = new KeyBasedEntityBatchIterator<String,WifiDevice>(String.class
					,WifiDevice.class, wifiDeviceService.getEntityDao(), mc);
			while(it.hasNext()){
				List<WifiDevice> entitys = it.next();
				
				//List<WifiDeviceIndexDTO> indexDtos = new ArrayList<WifiDeviceIndexDTO>();
				List<WifiDeviceDocument> docs = new ArrayList<>();
				//WifiDeviceIndexDTO indexDto = null;
				WifiDeviceDocument doc = null;
				for(WifiDevice device:entitys){
					String wifi_mac = device.getId();
					/*if(false){//updated_geocoding){
						if(deviceFacadeService.wifiDeiviceGeocoding(device)){
							wifiDeviceService.update(device);
						}
					}*/
					//long count = WifiDeviceHandsetPresentSortedSetService.getInstance().presentNotOfflineSize(wifi_mac);
					long count = WifiDeviceHandsetPresentSortedSetService.getInstance().presentOnlineSize(wifi_mac);
					List<Long> groupids = wifiDeviceGroupRelationService.getDeviceGroupIds(wifi_mac);
					//System.out.println(wifi_mac+"-"+count);
					//indexDto = IndexDTOBuilder.builderWifiDeviceIndexDTO(device, groupids);
					doc = WifiDeviceDocumentHelper.fromWifiDevice(device,wifiDeviceModuleService.getById(device.getId()), groupids);
					doc.setCount((int)count);
					//indexDto.setOnline(device.isOnline() ? 1 : 0);
					//indexDto.setCount((int)count);
					//indexDtos.add(indexDto);
					docs.add(doc);
				}
				
				/*boolean bulk_result = wifiDeviceIndexService.createIndexComponents(indexDtos);
				if(bulk_result){
					bulk_success++;
				}else{
					bulk_fail++;
				}
				index_count = index_count + entitys.size();*/
				if(!docs.isEmpty()){
					wifiDeviceDataSearchService.getRepository().save(docs);
					System.out.println(docs.size());//）
				}
			}
			
			System.out.println("数据全量导入，总耗时"+((System.currentTimeMillis()-t0)/1000)+"s 成功批量次数:" 
					+  bulk_success + " 失败批量次数:" + bulk_fail + " 一共索引数量:" + index_count);

		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}finally{
			/*wifiDeviceIndexService.openIndexRefresh();
			
			wifiDeviceIndexService.optimize();
			//关闭es连接
			wifiDeviceIndexService.destroy();*/
		}
		System.exit(1);
	}
}
