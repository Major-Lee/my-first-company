package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchimport;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.DistributorType;
import com.bhu.vas.api.helper.OpsHttpHelper;
import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.rpc.charging.model.WifiDeviceBatchImport;
import com.bhu.vas.api.rpc.charging.vto.BatchImportVTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.SharedNetworkSettingDTO;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSharedNetwork;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserWifiDevice;
import com.bhu.vas.business.asyn.spring.model.async.BatchImportConfirmDTO;
import com.bhu.vas.business.backendonline.asyncprocessor.buservice.BackendBusinessService;
import com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchimport.callback.ImportElementCallback;
import com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchimport.dto.DeviceCallbackDTO;
import com.bhu.vas.business.backendonline.asyncprocessor.service.iservice.IMsgHandlerService;
import com.bhu.vas.business.bucache.redis.serviceimpl.commdity.RewardOrderAmountHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.facade.UniqueFacadeService;
import com.bhu.vas.business.ds.charging.facade.ChargingFacadeService;
import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;
import com.bhu.vas.business.ds.device.facade.SharedNetworksFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.tag.service.TagGroupRelationService;
import com.bhu.vas.business.ds.user.facade.UserWifiDeviceFacadeService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.ds.user.service.UserWifiDeviceService;
import com.bhu.vas.business.search.service.increment.device.WifiDeviceStatusIndexIncrementService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.page.PageHelper;

@Service
public class BatchImportConfirmServiceHandler implements IMsgHandlerService {
	private final Logger logger = LoggerFactory.getLogger(BatchImportConfirmServiceHandler.class);
	
	@Resource
	private WifiDeviceService wifiDeviceService;

	@Resource
	private DeviceFacadeService deviceFacadeService;
	
/*	@Resource
	private UserDeviceFacadeService userDeviceFacadeService;*/
	
	@Resource
	private UserWifiDeviceService userWifiDeviceService;

	@Resource
	private UserService userService;
	
	@Resource
	private WifiDeviceStatusIndexIncrementService wifiDeviceStatusIndexIncrementService;
	
	@Resource
	private UserWifiDeviceFacadeService userWifiDeviceFacadeService;
	
	@Resource
	private ChargingFacadeService chargingFacadeService;
	
	@Resource
	private BackendBusinessService backendBusinessService;
	
	@Resource
	private TagGroupRelationService tagGroupRelationService;
	
	@Resource
	private SharedNetworksFacadeService sharedNetworksFacadeService;
	
	@Resource
	private BindUnbindDeviceService bindUnbindDeviceService;

	@Override
	public void process(String message) {
		logger.info(String.format("process message[%s]", message));
		try{
			BatchImportConfirmDTO confirm_dto = JsonHelper.getDTO(message, BatchImportConfirmDTO.class);
			//int user = confirm_dto.getUid();
			final String batchno = confirm_dto.getBatchno();
			if(StringUtils.isEmpty(batchno)) return;
			WifiDeviceBatchImport batchImport = chargingFacadeService.getWifiDeviceBatchImportService().getById(batchno);
			if(batchImport == null){
				logger.info(String.format("process batchno[%s] not exist!", batchno));
			}
/*
			if(batchImport.getStatus() != WifiDeviceBatchImport.STATUS_CONTENT_PRE_CHECK_DONE){
				logger.error(String.format("bathno[%s] precheck is not done", batchno));
				return;
			}
*/
			final int distributor = batchImport.getDistributor();
			final String distributor_type = batchImport.getDistributor_type();
			batchImport.setStatus(WifiDeviceBatchImport.STATUS_CONTENT_IMPORTING);
			chargingFacadeService.getWifiDeviceBatchImportService().update(batchImport);
			final AtomicInteger atomic_failed = new AtomicInteger(0);
			final AtomicInteger atomic_successed = new AtomicInteger(0);
			//String filepath = "/Users/Edmond/gospace/库房出库清单-20160426-0001.xlsx";
			//String mobileno = batchImport.getMobileno();
			final BatchImportVTO importVto = batchImport.toBatchImportVTO(null, null,null, null);
			//final String mobileno = batchImport.getMobileno();
			final Integer uid_willbinded = UniqueFacadeService.fetchUidByMobileno(86,batchImport.getMobileno());
//			final ParamSharedNetworkDTO psn = sharedNetworksFacadeService.fetchUserSharedNetworkConf(batchImport.getDistributor(), VapEnumType.SharedNetworkType.SafeSecure);

			ImportElementCallback cb = new ImportElementCallback(){
				@Override
				public DeviceCallbackDTO elementDeviceInfoFetch(String sn) {
					ModelCriteria mc = new ModelCriteria();
			        mc.createCriteria().andSimpleCaulse("1=1").andColumnEqualTo("sn", sn);
			        List<String> macs_fromdb = wifiDeviceService.findIdsByModelCriteria(mc);//.findModelByModelCriteria(mc);
			        if(macs_fromdb.isEmpty()){
			        	atomic_failed.incrementAndGet();
			        	return null;
			        }else{
			        	//WifiDevice wifiDevice = models.get(0);
			        	//String dmac = wifiDevice.getId();
			        	String dmac = macs_fromdb.get(0);
			        	//TODO:增加索引更新 是否可关闭
			        	DeviceCallbackDTO result = new DeviceCallbackDTO();
			        	result.setMac(dmac);
			        	//result.setOrig_swver(wifiDevice.getOrig_swver());
			        	atomic_successed.incrementAndGet();
			        	return result;
			        }
				}

				@Override
				public void afterExcelImported(Set<String> dmacs, Set<String> failed_sns) {
					if(!dmacs.isEmpty()){
						List<String> all_dmacs = new ArrayList<String>(dmacs);
						int total = all_dmacs.size();
						int totalPages = PageHelper.getTotalPages(total, 100);
						for(int pageno= 1;pageno<=totalPages;pageno++){
							List<String> pages = PageHelper.pageList(all_dmacs, pageno, 100);
							logger.info(String.format("pageno:%s pagesize:%s pages:%s", pageno,100,pages));
							User user_willbinded = null;
							if(uid_willbinded != null && uid_willbinded > 0)
								user_willbinded = userService.getById(uid_willbinded);
	
							//设置出货渠道
							List<WifiDevice> wifiDevices = wifiDeviceService.findByIds(pages);
							for(WifiDevice device:wifiDevices){
								device.setChannel_lv1(importVto.getChannel_lv1());
								device.setChannel_lv2(importVto.getChannel_lv2());
							}
							wifiDeviceService.updateAll(wifiDevices);
	
							//检查共享网络是否需要变更模板(城市运营商变更)
//							List<WifiDeviceSharedNetwork> wifiDevicesSnks = sharedNetworksFacadeService.getWifiDeviceSharedNetworkService().findByIds(pages);
//							for(WifiDeviceSharedNetwork wsnk:wifiDevicesSnks){
//								if(wsnk == null)
//									continue;
//								if(DistributorType.City.getType().equals(distributor_type)){
//									//城市运营商的设备，需要检查是否需要变更模板
//									if(distributor != -1 && wsnk.getOwner() != null && wsnk.getOwner().intValue() != distributor){
//										wsnk.setOwner(distributor);
//										wsnk.setTemplate(psn.getTemplate());
//										wsnk.setSharednetwork_type(psn.getNtype());
//										SharedNetworkSettingDTO dto = wsnk.getInnerModel();
//										dto.setOn(true);
//										dto.setPsn(psn);
//										wsnk.putInnerModel(dto);
//										sharedNetworksFacadeService.getWifiDeviceSharedNetworkService().update(wsnk); //后面会针对设备重建索引 
//									}
//								} else {
//									//如果之前是运营商设备，出货给分销商
//									if(user_willbinded!= null){
//										if(wsnk.getOwner() == null || wsnk.getOwner().intValue() != user_willbinded.getId()){
//											sharedNetworksFacadeService.getWifiDeviceSharedNetworkService().delete(wsnk);
//										}
//									}
//								}
//							}
							
							if(user_willbinded != null){
								bindUnbindDeviceService.bindDevice(pages, uid_willbinded);
							} else {
								bindUnbindDeviceService.unbindDevice(pages, true, null);
							}
							
							for(String dmac:pages){
								chargingFacadeService.doWifiDeviceSharedealConfigsUpdate(batchno, (user_willbinded == null)?null:uid_willbinded, importVto.getDistributor(), importVto.getDistributor_l2(), importVto.getDistributor_type(),
										dmac, 
										importVto.isCanbeturnoff(), importVto.isNoapp(),
										importVto.isEnterpriselevel(),
										importVto.isCustomized(),
										importVto.getOwner_percent(), importVto.getManufacturer_percent(),importVto.getDistributor_percent(),importVto.getDistributor_l2_percent(),
					        			//importVto.getRcm(), importVto.getRcp(), importVto.getAit(), 
					        			false);
					        	chargingFacadeService.getWifiDeviceBatchDetailService().deviceStore(dmac, importVto.getSellor(), importVto.getPartner(), importVto.getImportor(), batchno);
							}
							
							try {
								RewardOrderAmountHashService.getInstance().removeAllRAmountByMacs(pages.toArray(new String[0]));
								backendBusinessService.blukIndexs(pages);
								Thread.sleep(500);
							} catch (InterruptedException e) {
								e.printStackTrace(System.out);
							} catch (Exception e) {
								e.printStackTrace(System.out);
							}
						}
					}
					
					if(!StringUtils.isEmpty(importVto.getOpsid())){
						//运营商系统的导入，需要回调通知
						OpsHttpHelper.opsImportCallBackNotify(importVto.getOpsid(), StringHelper.toString(failed_sns.toArray(), StringHelper.COMMA_STRING_GAP));
					}
				}
			};
			
			boolean result = false;
			if(StringUtils.isEmpty(batchImport.getOpsid()))
				result = ShipmentExcelImport.excelImport(importVto.toAbsoluteFileInputPath(),importVto.toAbsoluteFileOutputPath(), cb);
			else
				result = ShipmentStringImport.stringImport(importVto.toAbsoluteFileInputPath(), importVto.toAbsoluteFileOutputPath(), cb);
				
			if(result){
				batchImport.setSucceed(atomic_successed.get());
				batchImport.setFailed(atomic_failed.get());
				batchImport.setStatus(WifiDeviceBatchImport.STATUS_CONTENT_IMPORTED);
				chargingFacadeService.getWifiDeviceBatchImportService().update(batchImport);
				logger.info(String.format("process message[%s] successful", message));
			}
		}finally{
		}
	}
	
/*	*//**
	 * 清除绑定信息
	 * 如果不是此用户绑定的设备则 清除原有的绑定信息并且把此用户设备绑定
	 * @param uid
	 * @param mac
	 *//*
	private void doForceBindDevice(int uid,String mac){
		
	}*/
}
