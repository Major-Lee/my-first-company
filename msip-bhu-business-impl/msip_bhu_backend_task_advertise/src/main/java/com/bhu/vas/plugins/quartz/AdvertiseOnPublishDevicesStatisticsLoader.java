package com.bhu.vas.plugins.quartz;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.rpc.advertise.model.Advertise;
import com.bhu.vas.api.rpc.advertise.model.AdvertiseDetails;
import com.bhu.vas.api.vto.advertise.AdvertiseTrashPositionVTO;
import com.bhu.vas.business.ds.advertise.service.AdvertiseDevicesIncomeService;
import com.bhu.vas.business.ds.advertise.service.AdvertiseService;
import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;


/**
 * 每天中午十二点统计符合广告分成的设备及数量
 * @author xiaowei
 *
 */
public class AdvertiseOnPublishDevicesStatisticsLoader {
	private static Logger logger = LoggerFactory.getLogger(AdvertiseOnPublishDevicesStatisticsLoader.class);
	
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	@Resource
	private AdvertiseService advertiseService;
	
	@Resource
	private AdvertiseDevicesIncomeService advertiseDevicesIncomeService;
	
	public void execute() {
		logger.info("AdvertiseOnPublishDevicesStatisticsLoader start....");
		
		String startTime = null;
		String endTime = null;
		try {
			startTime = DateTimeHelper.getAfterDate(DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern5), 0);
			endTime = DateTimeHelper.getAfterDate(DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern5), 1);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		List<Advertise> ads = fetchOnpublishAdvertise();
		for(Advertise ad: ads){
			logger.info(String.format("AdvertiseOnPublishDevicesStatisticsLoader onpulishAdvertiseId[%s]", ad.getId()));
			
			final List<String> devices = new ArrayList<String>();
			List<Advertise> trashAds = advertiseService.getEntityDao().queryByAdvertiseTimeExcept(startTime, endTime, ad.getProvince(), ad.getCity(), ad.getDistrict(),ad.getId());
			List<AdvertiseTrashPositionVTO> trashs = new ArrayList<AdvertiseTrashPositionVTO>();
			
			for(Advertise trashAd : trashAds){
				logger.info(String.format("AdvertiseOnPublishDevicesStatisticsLoader trashAd province[%s] city[%s] district[%s]",trashAd.getProvince(),trashAd.getCity(),trashAd.getDistrict()));
				AdvertiseTrashPositionVTO trashVto = new AdvertiseTrashPositionVTO();
				trashVto.setProvince(trashAd.getProvince());
				trashVto.setCity(trashAd.getCity());
				trashVto.setDistrict(trashAd.getDistrict());
				trashs.add(trashVto);
			}
			
			wifiDeviceDataSearchService.iteratorWithPosition(trashs, ad.getProvince(), ad.getCity(), ad.getDistrict(), true, 200, new IteratorNotify<Page<WifiDeviceDocument>>() {
						@Override
						public void notifyComming(Page<WifiDeviceDocument> pages) {
							for(WifiDeviceDocument doc: pages){
								devices.add(doc.getD_mac());
							}
						}
			});
			
					String afterDate =null;
//					afterDate = DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern5);
					try {
						afterDate = DateTimeHelper.getAfterDate(DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern5), 1);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					ModelCriteria mc = new ModelCriteria();
					mc.createCriteria().andColumnEqualTo("advertiseid", ad.getId()).andColumnEqualTo("publish_time", afterDate);
					logger.info(String.format("AdvertiseOnPublishDevicesStatisticsLoader  publish_time [%s]  advertiseid[%s]", afterDate,ad.getId()));
					List<AdvertiseDetails> details = advertiseDevicesIncomeService.findModelByModelCriteria(mc);
					if(!details.isEmpty()){
						logger.info("AdvertiseOnPublishDevicesStatisticsLoader  details update ");
						AdvertiseDetails detail = details.get(0);
						detail.setActual_count(devices.size());
						detail.setState(BusinessEnumType.AdvertiseType.UnSharedeal.getType());
						detail.replaceInnerModels(devices);
//						detail.setCash((float)(devices.size()*BusinessRuntimeConfiguration.Advertise_Unit_Price));
						advertiseDevicesIncomeService.update(detail);
					}else{
						logger.info("AdvertiseOnPublishDevicesStatisticsLoader details null");
					}
		}
				
//		if(!devicesIncome.isEmpty()){
//			advertiseDevicesIncomeService.updateAll(devicesIncome);
//		}
		logger.info("AdvertiseOnPublishDevicesStatisticsLoader end....");
	}
	
	public List<Advertise> fetchOnpublishAdvertise(){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("state", BusinessEnumType.AdvertiseType.OnPublish.getType());
		return advertiseService.findModelByModelCriteria(mc);
	}
}
