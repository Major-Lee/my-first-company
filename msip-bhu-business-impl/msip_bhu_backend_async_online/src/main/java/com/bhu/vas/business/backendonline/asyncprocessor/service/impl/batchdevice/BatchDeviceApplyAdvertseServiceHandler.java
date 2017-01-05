package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchdevice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.DownCmds;
import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.OperationDS;
import com.bhu.vas.api.rpc.advertise.model.Advertise;
import com.bhu.vas.api.rpc.advertise.model.AdvertiseDetails;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.DeviceStatusExchangeDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.SharedNetworkSettingDTO;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSharedNetwork;
import com.bhu.vas.api.vto.advertise.AdvertiseTrashPositionVTO;
import com.bhu.vas.business.asyn.spring.model.IDTO;
import com.bhu.vas.business.asyn.spring.model.async.device.BatchDeviceApplyAdvertiseDTO;
import com.bhu.vas.business.backendonline.asyncprocessor.service.iservice.IMsgHandlerService;
import com.bhu.vas.business.bucache.redis.serviceimpl.advertise.AdvertiseSnapShotListService;
import com.bhu.vas.business.bucache.redis.serviceimpl.advertise.WifiDeviceAdvertiseSortedSetService;
import com.bhu.vas.business.ds.advertise.facade.AdvertiseFacadeService;
import com.bhu.vas.business.ds.advertise.service.AdvertiseDevicesIncomeService;
import com.bhu.vas.business.ds.advertise.service.AdvertiseService;
import com.bhu.vas.business.ds.device.facade.DeviceCMDGenFacadeService;
import com.bhu.vas.business.ds.device.facade.SharedNetworksFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.device.service.WifiDeviceSharedNetworkService;
import com.bhu.vas.business.ds.user.facade.UserFacadeService;
import com.bhu.vas.business.search.model.device.WifiDeviceDocument;
import com.bhu.vas.business.search.service.device.WifiDeviceDataSearchService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

@Service
public class BatchDeviceApplyAdvertseServiceHandler implements IMsgHandlerService {
	private final Logger logger = LoggerFactory
			.getLogger(BatchDeviceApplyAdvertseServiceHandler.class);

	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;

	@Resource
	private WifiDeviceSharedNetworkService wifiDeviceSharedNetworkService;

	@Resource
	private WifiDeviceService wifiDeviceService;

	@Resource
	private SharedNetworksFacadeService sharedNetworksFacadeService;

	@Resource
	private DeviceCMDGenFacadeService deviceCMDGenFacadeService;

	@Resource
	private IDaemonRpcService daemonRpcService;
	
	@Resource
	private AdvertiseService advertiseService;
	
	@Resource
	private AdvertiseFacadeService advertiseFacadeService;
	
	@Resource
	private AdvertiseDevicesIncomeService advertiseDevicesIncomeService;

	@Resource 
	private UserFacadeService userFacadeService;
	
	@Override
	public void process(String message) {
		logger.info(String.format("process message[%s]", message));
		final BatchDeviceApplyAdvertiseDTO adDTO = JsonHelper.getDTO(message,
				BatchDeviceApplyAdvertiseDTO.class);
		List<Advertise> adlists = advertiseService.findByIds(adDTO.getIds());
		int batch = 200;
		for (final Advertise ad : adlists) {
			List<String> macList = null;
			
			String start = null;
			String end = null;
//			SimpleDateFormat sdf = new SimpleDateFormat(DateTimeHelper.FormatPattern5); 
			
			try {
				start = DateTimeHelper.getAfterDate(DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern5), 1);
				end = DateTimeHelper.getAfterDate(DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern5), 2);
//				List<Advertise> ads = advertiseService.getEntityDao().queryByAdvertiseTimeExcept(start, end, ad.getProvince(), ad.getCity(), ad.getDistrict(), ad.getId());
//				List<AdvertiseTrashPositionVTO> trashs = AdvertiseHelper.buildAdvertiseTrashs(ads, sdf.parse(start));
				if(ad.getType() == BusinessEnumType.AdvertiseType.HomeImage_SmallArea.getType()){
					StringBuffer sb = new StringBuffer(ad.getProvince());
					sb.append(ad.getCity()).append(ad.getDistrict());
					
					macList = advertiseHomeImage_SmallAreaApply(sb.toString(), ad.getLat(), ad.getLon(), ad.getDistance(), batch);
					ad.setState(BusinessEnumType.AdvertiseStateType.OnPublish.getType());
					ad.setSign(true);

					SimpleDateFormat sdf=new SimpleDateFormat(DateTimeHelper.FormatPattern1);  
					String date = DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern1);
					ad.setStart(sdf.parse(date));
					ad.setEnd(sdf.parse(DateTimeHelper.getAfterDate(date, 1)));
					advertiseService.update(ad);
					AdvertiseSnapShotListService.getInstance().generateSnapShot(ad.getId(), macList);
				}else{
					macList = advertiseHomeImageApply(start, end, ad, batch);
				}
				
				switch (adDTO.getDtoType()) {
					case IDTO.ACT_ADD:
						WifiDeviceAdvertiseSortedSetService.getInstance().wifiDevicesAdApply(
							macList, JsonHelper.getJSONString(ad),Double.parseDouble(ad.getId()));
//						AdvertiseDetailsHashService.getInstance().advertiseInfo(ad.getId(), ad.toMap());
						advertiDetails(ad, start, macList.size());
						deviceLimitDomain(batch, macList, ad.getDomain(),IDTO.ACT_ADD, ad);
						break;
					case IDTO.ACT_DELETE:
						deviceLimitDomain(batch, macList, null,
							IDTO.ACT_DELETE, ad);
						break;
//					case IDTO.ACT_UPDATE:
//						WifiDeviceAdvertiseSortedSetService.getInstance().wifiDevicesAdApply(macList, JsonHelper.getJSONString(ad),Double.parseDouble(ad.getId()));
//						advertiSesnapshot(ad, start, macList.size());
//						break;
					default:
						break;
				}
			} catch (ParseException e) {
				logger.info("BatchDeviceApplyAdvertseServiceHandler error..");
				e.printStackTrace();
			}
		}
	}

	public void deviceLimitDomain(int batch, List<String> macList, String domain,
			char dtotype, Advertise ad) {
		int fromIndex = 0;
		int toIndex = 0;
		List<DownCmds> downCmds = new ArrayList<DownCmds>(batch);
		do {
			toIndex = fromIndex + batch;
			if (toIndex > macList.size())
				toIndex = macList.size();
			List<String> todoList = macList.subList(fromIndex, toIndex);

			for (String mac : todoList) {
				WifiDeviceSharedNetwork sharednetwork = sharedNetworksFacadeService
						.fetchDeviceSharedNetworkConfWhenEmptyThenCreate(mac,
								true, true);
				SharedNetworkSettingDTO snk = sharednetwork.getInnerModel();
				ParamSharedNetworkDTO psn = null;
				if(snk != null){
					psn = snk.getPsn();
					if(psn != null){
						psn.setOpen_resource_ad(dtotype == IDTO.ACT_ADD ? domain : null); // 设置或者清空广告白名单
						sharednetwork.putInnerModel(snk);
					}else{
						logger.info(String.format("device is not has psn psn [%s]", mac));
						continue;
					}
				}else{
					logger.info(String.format("device is not has snk snk [%s]", mac));
					continue;
				}
				sharedNetworksFacadeService.getWifiDeviceSharedNetworkService()
						.update(sharednetwork);

				WifiDevice wifiDevice = wifiDeviceService.getById(mac);
				if (wifiDevice == null)
					continue;
				if(!wifiDevice.isOnline() || !snk.isOn())
					continue;
				// 生成下发指令
				if(psn !=null){
					String cmd = CMDBuilder.autoBuilderCMD4Opt(OperationCMD.ModifyDeviceSetting,OperationDS.DS_SharedNetworkWifi_Start, mac, -1,
							JsonHelper.getJSONString(psn), DeviceStatusExchangeDTO.build(wifiDevice.getWork_mode(),wifiDevice.getOrig_swver()),
							deviceCMDGenFacadeService);
					downCmds.add(DownCmds.builderDownCmds(mac, cmd));
				}
			}

			if (!downCmds.isEmpty()) {
				daemonRpcService.wifiMultiDevicesCmdsDown(downCmds.toArray(new DownCmds[0]));
				downCmds.clear();
			}

			fromIndex += batch;
		} while (toIndex < macList.size());
	}
	
	public void advertiDetails(Advertise ad,String publishTime,int pushlist_count){
		
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("advertiseid", ad.getId()).andColumnEqualTo("publish_time", publishTime);
		List<AdvertiseDetails> details = advertiseDevicesIncomeService.findModelByModelCriteria(mc);
		if(details.isEmpty()){
			logger.info(String.format("BatchDeviceApplyAdvertseServiceHandler  publish_time [%s]  advertiseid[%s]", publishTime,ad.getId()));
			AdvertiseDetails income = new AdvertiseDetails();
			income.setAdvertiseid(ad.getId());
			income.setPublish_count(pushlist_count);
			income.setPublish_time(publishTime);
			double cash = pushlist_count*BusinessRuntimeConfiguration.Advertise_Unit_Price;
			
			if(ad.getType() == BusinessEnumType.AdvertiseType.HomeImage.getType()){
				if(userFacadeService.checkOperatorByUid(ad.getUid())){
					income.setCash(cash*BusinessRuntimeConfiguration.AdvertiseOperatorDiscount);
				}else{
					income.setCash(cash*BusinessRuntimeConfiguration.AdvertiseCommonDiscount);
				}
			}else if(ad.getType() == BusinessEnumType.AdvertiseType.HomeImage_SmallArea.getType()){
				income.setCash(1);
			}else{
				income.setCash(cash);
			}
			
			advertiseDevicesIncomeService.insert(income);
		}
	}
	
	public List<String> advertiseHomeImageApply(String start,String end , Advertise ad,int batch ){
		List<Advertise> trashAds = advertiseService.getEntityDao().queryByAdvertiseTimeExcept(start, end, ad.getProvince(), ad.getCity(), ad.getDistrict(),ad.getId());
		List<AdvertiseTrashPositionVTO> trashs = new ArrayList<AdvertiseTrashPositionVTO>();
		final List<String> macList = new ArrayList<String>();
		for(Advertise trashAd : trashAds){
			AdvertiseTrashPositionVTO trashVto = new AdvertiseTrashPositionVTO();
			trashVto.setProvince(trashAd.getProvince());
			trashVto.setCity(trashAd.getCity());
			trashVto.setDistrict(trashAd.getDistrict());
			trashs.add(trashVto);
		}
		
		wifiDeviceDataSearchService.iteratorWithPosition(trashs,ad.getProvince(),
				ad.getCity(), ad.getDistrict(),false,batch,
				new IteratorNotify<Page<WifiDeviceDocument>>() {

					@Override
					public void notifyComming(Page<WifiDeviceDocument> pages) {
						for (WifiDeviceDocument doc : pages) {
							macList.add(doc.getD_mac());
						}
				}
		});
		
		return macList;
	}
	
	public List<String> advertiseHomeImage_SmallAreaApply(String contextId, double lat, double lon, String distance, int batch){
		final List<String> macList = new ArrayList<String>();
		wifiDeviceDataSearchService.iteratorWithGeoPointDistance(contextId, lat, lon, distance, batch, new IteratorNotify<Page<WifiDeviceDocument>>() {
			@Override
			public void notifyComming(Page<WifiDeviceDocument> pages) {
				for (WifiDeviceDocument doc : pages) {
					macList.add(doc.getD_mac());
				}	
			}
		});
		return macList;
	}
}
