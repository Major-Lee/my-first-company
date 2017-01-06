package com.bhu.vas.plugins.quartz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.bhu.vas.api.helper.WifiDeviceHelper;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceModuleService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.search.BusinessIndexDefine;
import com.bhu.vas.business.search.service.device.WifiDeviceDataSearchService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
/**
 * 每30分钟执行一次
 * 所有的在线设备进行索引增量,主要用于更新wifi设备的在线移动设备数
 * 如果在线设备存在经纬度，但是没有获取详细地址，也会进行获取
 * @author tangzichao
 *
 */
public class WifiDeviceOnlineLoader {
	private static Logger logger = LoggerFactory.getLogger(WifiDeviceOnlineLoader.class);
	
	public int bulk_success = 0;
	public int bulk_fail = 0;
	public int index_count = 0;
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private WifiDeviceModuleService wifiDeviceModuleService;
	//@Resource
	//private WifiDeviceIndexService wifiDeviceIndexService;
	@Resource
	private DeviceFacadeService deviceFacadeService;
	
	//@Resource
	//private WifiDeviceGroupRelationService wifiDeviceGroupRelationService;
	
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	
	public void init(){
		bulk_success = 0;
		bulk_fail = 0;
		index_count = 0;
	}
	public void execute() {
		logger.info("WifiDeviceOnlineUser starting...");
		try{
			init();
			//wifiDeviceIndexService.disableIndexRefresh();
			
			ModelCriteria mc = new ModelCriteria();
			mc.createCriteria().andColumnEqualTo("online", 1);
			//mc.setOrderByClause(" created_at ");
	    	mc.setPageNumber(1);
	    	mc.setPageSize(500);
			EntityIterator<String, WifiDevice> it = new KeyBasedEntityBatchIterator<String,WifiDevice>(String.class
					,WifiDevice.class, wifiDeviceService.getEntityDao(), mc);
			while(it.hasNext()){
				wifiDeviceHocIncrement(it.next());
			}
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(ex.getMessage(), ex);
		}finally{
			wifiDeviceDataSearchService.refresh(false);
			//wifiDeviceIndexService.openIndexRefresh();
			//wifiDeviceIndexService.destroy();
		}
		
		logger.info(String.format("WifiDeviceOnlineUser ended, total index [%s] bluk success [%s] fail [%s]", 
				index_count, bulk_success, bulk_fail));
	}
	
/*	*//**
	 * 增量索引
	 * @param entitys
	 *//*
	public void wifiDeviceIndexIncrement(List<WifiDevice> entitys){
		List<WifiDeviceDocument> docs = new ArrayList<>();
		try{
			//List<WifiDeviceIndexDTO> indexDtos = new ArrayList<WifiDeviceIndexDTO>();
			//WifiDeviceIndexDTO indexDto = null;
			WifiDeviceDocument doc = null;
			for(WifiDevice device:entitys){
				//如果在线设备存在经纬度，但是没有获取详细地址，也会进行获取
				if(validateCoordinateAndGet(device)){
					wifiDeviceService.update(device);
				}
				String wifi_mac = device.getId();
				long count = WifiDeviceHandsetPresentSortedSetService.getInstance().presentOnlineSize(wifi_mac);
				//List<Long> groupids = wifiDeviceGroupRelationService.getDeviceGroupIds(wifi_mac);
				
				doc = WifiDeviceDocumentHelper.fromWifiDevice(device,wifiDeviceModuleService.getById(device.getId()), groupids);
				//indexDto = IndexDTOBuilder.builderWifiDeviceIndexDTO(device, groupids);
				//indexDto.setOnline(WifiDeviceIndexDTO.Online_Status);
				//indexDto.setCount((int)count);
				//indexDtos.add(indexDto);
				doc.setCount((int)count);
				docs.add(doc);
				if(count > 0){
					logger.info(String.format("WifiDeviceOnlineUser index dto id[%s] count[%s]", doc.getId(), count));
				}
			}
			
			if(!docs.isEmpty()){
				wifiDeviceDataSearchService.bulkIndex(docs);
				//wifiDeviceDataSearchService.getRepository().save(docs);
				boolean bulk_result = wifiDeviceIndexService.createIndexComponents(indexDtos);
				if(bulk_result){
					bulk_success++;
				}else{
					bulk_fail++;
				}
				index_count += docs.size();
			}
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(ex.getMessage(), ex);
		}
	}*/
	/**
	 * 查询当前设备的终端数量并更新索引
	 * @param entitys
	 */
	public void wifiDeviceHocIncrement(List<WifiDevice> entitys){
		if(entitys == null || entitys.isEmpty()) return;
		try{
			List<String> ids = new ArrayList<String>();
			List<Map<String,Object>> sourceMaps = new ArrayList<Map<String,Object>>();
			String updatedat = DateTimeHelper.getDateTime();
			for(WifiDevice entity : entitys){
				ids.add(entity.getId());
				
				Map<String,Object> sourceMap = new HashMap<String,Object>();
				Long pos = WifiDeviceHandsetPresentSortedSetService.getInstance().presentOnlineSize(entity.getId());
				if(pos != null){
					sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_HANDSETONLINECOUNT.getName(), pos.intValue());
				}else{
					sourceMap.put(BusinessIndexDefine.WifiDevice.Field.D_HANDSETONLINECOUNT.getName(), 0);
				}
				sourceMap.put(BusinessIndexDefine.WifiDevice.Field.UPDATEDAT.getName(), updatedat);
				sourceMaps.add(sourceMap);
			}
			wifiDeviceDataSearchService.bulkUpdate(ids, sourceMaps, false, true, true);
			index_count += ids.size();
			bulk_success++;
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(ex.getMessage(), ex);
			bulk_fail++;
		}
	}
	
	/**
	 * 如果在线设备存在经纬度，但是没有获取详细地址，也会进行获取
	 */
	public boolean validateCoordinateAndGet(WifiDevice device){
		if((WifiDeviceHelper.Device_Location_By_APP != device.getLoc_method()) && StringUtils.isEmpty(device.getFormatted_address())){
			if(!StringUtils.isEmpty(device.getLat()) && !StringUtils.isEmpty(device.getLon())){
				return deviceFacadeService.wifiDeiviceGeocoding(device);
			}
		}
		return false;
	}
}
