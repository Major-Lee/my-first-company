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
import com.bhu.vas.business.search.service.increment.WifiDeviceStatusIndexIncrementService;
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
			final BatchImportVTO importVto = batchImport.toBatchImportVTO(null, null,null);
			//final String mobileno = batchImport.getMobileno();
			final Integer uid_willbinded = UniqueFacadeService.fetchUidByMobileno(86,batchImport.getMobileno());
			final ParamSharedNetworkDTO psn = sharedNetworksFacadeService.fetchUserSharedNetworkConf(batchImport.getDistributor(), VapEnumType.SharedNetworkType.SafeSecure);

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
							List<WifiDeviceSharedNetwork> wifiDevicesSnks = sharedNetworksFacadeService.getWifiDeviceSharedNetworkService().findByIds(pages);
							for(WifiDeviceSharedNetwork wsnk:wifiDevicesSnks){
								if(DistributorType.City.getType().equals(distributor_type)){
									//城市运营商的设备，需要检查是否需要变更模板
									if(distributor != -1 && wsnk.getOwner() != distributor){
										wsnk.setOwner(distributor);
										wsnk.setTemplate(psn.getTemplate());
										wsnk.setSharednetwork_type(psn.getNtype());
										SharedNetworkSettingDTO dto = wsnk.getInnerModel();
										dto.setOn(true);
										dto.setPsn(psn);
										wsnk.putInnerModel(dto);
										sharedNetworksFacadeService.getWifiDeviceSharedNetworkService().update(wsnk); //后面会针对设备重建索引 
									}
								} else {
									//如果之前是运营商设备，出货给分销商,暂时保留不动
									
								}
							}
							
							if(user_willbinded != null){
								//userDeviceFacadeService.doForceBindDevices(uid_willbinded.intValue(),pages);
								List<String> group_macs = new ArrayList<String>();
								for(String dmac:pages){
									//UserDevicePK udp = userDeviceFacadeService.deviceBinded(dmac);
									UserWifiDevice userWifiDevice = userWifiDeviceService.getById(dmac);
									//if(udp != null){
									if(userWifiDevice != null){
										//if(udp.getUid() != uid_willbinded.intValue()){
										if(userWifiDevice.getUid() != uid_willbinded.intValue()){
											//userDeviceFacadeService.getUserDeviceService().deleteById(udp);
											userWifiDeviceService.delete(userWifiDevice);
											userWifiDeviceFacadeService.insertUserWifiDevice(dmac, uid_willbinded.intValue());
								            deviceFacadeService.gainDeviceMobilePresentString(uid_willbinded,dmac);
											group_macs.add(dmac);
										}else{
											//已经此用户绑定，不动作
										}
									}else{
	/*									UserDevice userDevice = new UserDevice();
							            userDevice.setId(new UserDevicePK(dmac, uid_willbinded.intValue()));
							            userDevice.setCreated_at(new Date());
							            userDeviceFacadeService.getUserDeviceService().insert(userDevice);*/
										userWifiDeviceFacadeService.insertUserWifiDevice(dmac, uid_willbinded.intValue());
							            deviceFacadeService.gainDeviceMobilePresentString(uid_willbinded,dmac);
									}
									
									chargingFacadeService.doWifiDeviceSharedealConfigsUpdate(batchno,uid_willbinded, importVto.getDistributor(), importVto.getDistributor_type(),
											dmac, 
											importVto.isCanbeturnoff(),importVto.isEnterpriselevel(),
											importVto.isCustomized(),
											importVto.getOwner_percent(), importVto.getManufacturer_percent(),importVto.getDistributor_percent(),
						        			//importVto.getRcm(), importVto.getRcp(), importVto.getAit(), 
						        			false);
						        	chargingFacadeService.getWifiDeviceBatchDetailService().deviceStore(dmac, importVto.getSellor(), importVto.getPartner(), importVto.getImportor(), batchno);
								}
	
								//清除分组关系
								if(!group_macs.isEmpty()){
									for(String gmac:group_macs){
										deviceFacadeService.destoryDeviceMobilePresentString(gmac);
									}
									tagGroupRelationService.cleanDeviceGroupRels(group_macs);
									wifiDeviceStatusIndexIncrementService.ucExtensionMultiUpdIncrement(group_macs, null);
								}
								logger.info(String.format("A uid_willbinded:%s doForceBindDevices:%s", uid_willbinded,pages));
							}else{
								//如果设备从未上线则强制解绑，如果设备上线过则不动作
								List<String> forceUnbindedDevices = new ArrayList<>();
								List<String> noActionDevices = new ArrayList<>();
								try{
									for(WifiDevice device:wifiDevices){
										if(device.getLast_logout_at() == null 
												&& device.getLast_reged_at() == null
												&& device.getOem_swver().contains("P06V0.0.0Build0000")){//从未上线
											forceUnbindedDevices.add(device.getId());
										}else{
											noActionDevices.add(device.getId());
										}
									}
									if(!forceUnbindedDevices.isEmpty()){
										//userDeviceFacadeService.doForceUnbindDevice(forceUnbindedDevices);
										userWifiDeviceService.deleteByIds(forceUnbindedDevices);
										for(String dmac:forceUnbindedDevices){
											deviceFacadeService.destoryDeviceMobilePresentString(dmac);
										}
										tagGroupRelationService.cleanDeviceGroupRels(forceUnbindedDevices);
										wifiDeviceStatusIndexIncrementService.ucExtensionMultiUpdIncrement(forceUnbindedDevices, null);
									}
										
									//变更分成比例
									for(String dmac:forceUnbindedDevices){//需要变更owner = -1
										chargingFacadeService.doWifiDeviceSharedealConfigsUpdate(batchno,-1, importVto.getDistributor(), importVto.getDistributor_type(), dmac, 
												importVto.isCanbeturnoff(),importVto.isEnterpriselevel(),
												importVto.isCustomized(),
												importVto.getOwner_percent(), importVto.getManufacturer_percent(),importVto.getDistributor_percent(),
							        			//importVto.getRcm(), importVto.getRcp(), importVto.getAit(),
							        			false);
							        	chargingFacadeService.getWifiDeviceBatchDetailService().deviceStore(dmac, importVto.getSellor(), importVto.getPartner(), importVto.getImportor(), batchno);
									}
									logger.info(String.format("B uid_willbinded:%s forceUnbindedDevices:%s", uid_willbinded,forceUnbindedDevices));
									for(String dmac:noActionDevices){//不需要变更owner 
										chargingFacadeService.doWifiDeviceSharedealConfigsUpdate(batchno,null, importVto.getDistributor(), importVto.getDistributor_type(), dmac,
												importVto.isCanbeturnoff(),importVto.isEnterpriselevel(),
												importVto.isCustomized(),
												importVto.getOwner_percent(), importVto.getManufacturer_percent(),importVto.getDistributor_percent(),
							        			//importVto.getRcm(), importVto.getRcp(), importVto.getAit(),
							        			false);
							        	chargingFacadeService.getWifiDeviceBatchDetailService().deviceStore(dmac, importVto.getSellor(), importVto.getPartner(), importVto.getImportor(), batchno);
									}
									logger.info(String.format("B uid_willbinded:%s noActionDevices:%s", uid_willbinded,noActionDevices));
								}catch(Exception ex){
									ex.printStackTrace(System.out);
								}finally{
									if(forceUnbindedDevices != null){
										forceUnbindedDevices.clear();
										forceUnbindedDevices = null;
									}
									if(noActionDevices != null){
										noActionDevices.clear();
										noActionDevices = null;
									}
									if(wifiDevices != null){
										wifiDevices.clear();
										wifiDevices = null;
									}
								}
								
							}
							
							try {
								RewardOrderAmountHashService.getInstance().removeAllRAmountByMacs(pages.toArray(new String[0]));
								backendBusinessService.blukIndexs(pages);
								Thread.sleep(500);
							} catch (InterruptedException e) {
								e.printStackTrace(System.out);
							} catch (Exception e) {
								e.printStackTrace(System.out);
							} finally{
								
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
