package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchdevice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import com.bhu.vas.business.search.service.advertise.AdvertiseDataSearchService;
import com.bhu.vas.business.search.service.device.WifiDeviceDataSearchService;
import com.bhu.vas.business.search.service.increment.advertise.AdvertiseIndexIncrementService;
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
	
	@Resource
	private AdvertiseIndexIncrementService advertiseIndexIncrementService;
	
	@Resource
	private AdvertiseDataSearchService advertiseDataSearchService;
	
	@Override
	public void process(String message) {
		logger.info(String.format("process message[%s]", message));
		final BatchDeviceApplyAdvertiseDTO adDTO = JsonHelper.getDTO(message,
				BatchDeviceApplyAdvertiseDTO.class);
		List<Advertise> adlists = advertiseService.findByIds(adDTO.getIds());
		int batch = 200;
		for (final Advertise ad : adlists) {
			List<String> macList = new ArrayList<String>();
			
			String start = null;
			String end = null;
			
			try {
				start = DateTimeHelper.getAfterDate(DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern5), 1);
				end = DateTimeHelper.getAfterDate(DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern5), 2);
				if(ad.getType() == BusinessEnumType.AdvertiseType.HomeImage_SmallArea.getType()){
					
					if(adDTO.isAdmin()){
						final List<String> maclist1 = new ArrayList<String>();
						wifiDeviceDataSearchService.iteratorWithPosition(null, ad.getProvince(), ad.getCity(), ad.getDistrict(), false, 200, new IteratorNotify<Page<WifiDeviceDocument>>() {
						@Override
						public void notifyComming(Page<WifiDeviceDocument> pages) {
							for(WifiDeviceDocument doc: pages){
								maclist1.add(doc.getD_mac());
							}
						}});
						if(maclist1 !=null){
							macList.addAll(maclist1);
						}
					}else{
						macList = advertiseHomeImage_SmallAreaApply(null, ad.getLat(), ad.getLon(), ad.getDistance());
					}
					
				}else{
					macList = advertiseHomeImageApply(start, end, ad, batch);
				}
				
				switch (adDTO.getDtoType()) {
					case IDTO.ACT_ADD:
						WifiDeviceAdvertiseSortedSetService.getInstance().wifiDevicesAdApply(
							macList, JsonHelper.getJSONString(ad.toRedis()),Double.parseDouble(ad.getId()));
						advertiDetails(ad, start, macList.size());
//						deviceLimitDomain(batch, macList, ad.getDomain(),IDTO.ACT_ADD, ad);
						advertiseIndexIncrementService.adStateUpdIncrement(ad.getId(), BusinessEnumType.AdvertiseStateType.OnPublish.getType(), null);
						break;
					case IDTO.ACT_DELETE:
						deviceLimitDomain(batch, macList, null,
							IDTO.ACT_DELETE, ad);
						advertiseIndexIncrementService.adStateUpdIncrement(ad.getId(), BusinessEnumType.AdvertiseStateType.Published.getType(), null);
						break;
					case IDTO.ACT_UPDATE:
						adStateUpdIncrement(ad, macList);
						break;
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
				sharedNetworksFacadeService.getWifiDeviceSharedNetworkService().update(sharednetwork);
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
	
	public List<String> advertiseHomeImage_SmallAreaApply(String contextId, double lat, double lon, String distance){
		final List<String> macList = new ArrayList<String>();
		Page<WifiDeviceDocument> pages= wifiDeviceDataSearchService.searchByGeoPointDistance(contextId, lat, lon, distance);
		for(WifiDeviceDocument doc : pages){
			macList.add(doc.getD_mac());
		}
		return macList;
	}
	
	public void adStateUpdIncrement(Advertise ad,List<String> maclist) throws ParseException{
		if(ad.getType() == BusinessEnumType.AdvertiseType.HomeImage_SmallArea.getType()){
			
			ad.setState(BusinessEnumType.AdvertiseStateType.OnPublish.getType());
			ad.setSign(true);
			
			SimpleDateFormat sdf=new SimpleDateFormat(DateTimeHelper.FormatPattern1);  
			String date = DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern1);
			ad.setStart(sdf.parse(date));
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(ad.getStart());
			  
			int inputDayOfYear = cal.get(Calendar.DAY_OF_YEAR);
			cal.set(Calendar.DAY_OF_YEAR , inputDayOfYear+1);
			SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date end = dateFormat2.parse("2099-12-31 23:59:59");
			ad.setEnd(end);
			System.out.println(ad.getStart() +"||" +ad.getEnd());
			advertiseService.update(ad);
			
			long score = Long.parseLong(DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern16));
			if(ad.getTop() == 1){
				score+=100000000000000L;
			}
			WifiDeviceAdvertiseSortedSetService.getInstance().wifiDevicesAdApply(
					maclist, JsonHelper.getJSONString(ad.toRedis()),score);
			advertiseIndexIncrementService.adStartAndEndUpdIncrement(ad.getId(), date, DateTimeHelper.getAfterDate(date, 1),BusinessEnumType.AdvertiseStateType.OnPublish.getType(),score);
			AdvertiseSnapShotListService.getInstance().generateSnapShot(ad.getId(), maclist);
			
		}else{
			advertiseIndexIncrementService.adStateUpdIncrement(ad.getId(), BusinessEnumType.AdvertiseStateType.UnVerified.getType(), null);
		}
	}
}
