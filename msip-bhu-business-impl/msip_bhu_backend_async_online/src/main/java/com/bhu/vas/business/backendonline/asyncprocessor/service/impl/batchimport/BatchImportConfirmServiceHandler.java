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

import com.bhu.vas.api.rpc.charging.model.WifiDeviceBatchImport;
import com.bhu.vas.api.rpc.charging.vto.BatchImportVTO;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.business.asyn.spring.model.async.BatchImportConfirmDTO;
import com.bhu.vas.business.backendonline.asyncprocessor.buservice.BackendBusinessService;
import com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchimport.callback.ExcelElementCallback;
import com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchimport.dto.DeviceCallbackDTO;
import com.bhu.vas.business.backendonline.asyncprocessor.service.iservice.IMsgHandlerService;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.facade.UniqueFacadeService;
import com.bhu.vas.business.ds.charging.facade.ChargingFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.user.facade.UserDeviceFacadeService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.page.PageHelper;

@Service
public class BatchImportConfirmServiceHandler implements IMsgHandlerService {
	private final Logger logger = LoggerFactory.getLogger(BatchImportConfirmServiceHandler.class);
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	@Resource
	private UserDeviceFacadeService userDeviceFacadeService;
	
	@Resource
	private ChargingFacadeService chargingFacadeService;
	
	@Resource
	private BackendBusinessService backendBusinessService;
	
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
			batchImport.setStatus(WifiDeviceBatchImport.STATUS_CONTENT_IMPORTING);
			chargingFacadeService.getWifiDeviceBatchImportService().update(batchImport);
			final AtomicInteger atomic_failed = new AtomicInteger(0);
			final AtomicInteger atomic_successed = new AtomicInteger(0);
			//String filepath = "/Users/Edmond/gospace/库房出库清单-20160426-0001.xlsx";
			//String mobileno = batchImport.getMobileno();
			final BatchImportVTO importVto = batchImport.toBatchImportVTO(null, null);
			//final String mobileno = batchImport.getMobileno();
			final Integer uid_willbinded = UniqueFacadeService.fetchUidByMobileno(86,batchImport.getMobileno());
			ShipmentExcelImport.excelImport(importVto.toAbsoluteFileInputPath(),importVto.toAbsoluteFileOutputPath(), new ExcelElementCallback(){
				@Override
				public DeviceCallbackDTO elementDeviceInfoFetch(String sn) {
					ModelCriteria mc = new ModelCriteria();
			        mc.createCriteria().andSimpleCaulse("1=1").andColumnEqualTo("sn", sn);
			        List<String> macs_fromdb = wifiDeviceService.findIdsByModelCriteria(mc);//.findModelByModelCriteria(mc);
			        if(macs_fromdb.isEmpty()){
			        	atomic_failed.incrementAndGet();
			        	return null;
			        }
			        else{
			        	//WifiDevice wifiDevice = models.get(0);
			        	//String dmac = wifiDevice.getId();
			        	String dmac = macs_fromdb.get(0);
			        	//chargingFacadeService.doWifiDeviceSharedealConfigsUpdate(batchno,uid_willbinded==null?-1:uid_willbinded.intValue(), dmac, importVto.getOwner_percent(), 
			        	//		importVto.getRcm(), importVto.getRcp(), importVto.getAit(), importVto.isCanbeturnoff(),importVto.isEnterpriselevel(), false);
			        	/*chargingFacadeService.doWifiDeviceSharedealConfigsUpdate(batchno,uid_willbinded, dmac, importVto.getOwner_percent(), 
			        			importVto.getRcm(), importVto.getRcp(), importVto.getAit(), importVto.isCanbeturnoff(),importVto.isEnterpriselevel(), false);
			        	chargingFacadeService.getWifiDeviceBatchDetailService().deviceStore(dmac, importVto.getSellor(), importVto.getPartner(), importVto.getImportor(), batchno);*/
			        	//TODO:增加索引更新 是否可关闭
			        	DeviceCallbackDTO result = new DeviceCallbackDTO();
			        	result.setMac(dmac);
			        	//result.setOrig_swver(wifiDevice.getOrig_swver());
			        	atomic_successed.incrementAndGet();
			        	return result;
			        	//return models.get(0);
			        }
				}

				@Override
				public void afterExcelImported(Set<String> dmacs) {
					if(dmacs.isEmpty()) return;
					List<String> all_dmacs = new ArrayList<String>(dmacs);
					int total = all_dmacs.size();
					int totalPages = PageHelper.getTotalPages(total, 100);
					for(int pageno= 1;pageno<=totalPages;pageno++){
						List<String> pages = PageHelper.pageList(all_dmacs, pageno, 100);
						logger.info(String.format("pageno:%s pagesize:%s pages:%s", pageno,100,pages));
						if(uid_willbinded != null && uid_willbinded.intValue() >0){
							userDeviceFacadeService.doForceBindDevices(uid_willbinded.intValue(),pages);
							for(String dmac:pages){
								chargingFacadeService.doWifiDeviceSharedealConfigsUpdate(batchno,uid_willbinded, dmac, 
										importVto.isCanbeturnoff(),importVto.isEnterpriselevel(),
										importVto.isCustomized(),
										importVto.getOwner_percent(), 
					        			importVto.getRcm(), importVto.getRcp(), importVto.getAit(), false);
					        	chargingFacadeService.getWifiDeviceBatchDetailService().deviceStore(dmac, importVto.getSellor(), importVto.getPartner(), importVto.getImportor(), batchno);
							}
							logger.info(String.format("A uid_willbinded:%s doForceBindDevices:%s", uid_willbinded,pages));
						}else{
							//如果设备从未上线则强制解绑，如果设备上线过则不动作
							List<String> forceUnbindedDevices = new ArrayList<>();
							List<String> noActionDevices = new ArrayList<>();
							List<WifiDevice> wifiDevices = wifiDeviceService.findByIds(pages);
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
								if(!forceUnbindedDevices.isEmpty())
									userDeviceFacadeService.doForceUnbindDevice(forceUnbindedDevices);
								//变更分成比例
								for(String dmac:forceUnbindedDevices){//需要变更owner = -1
									chargingFacadeService.doWifiDeviceSharedealConfigsUpdate(batchno,-1, dmac, 
											importVto.isCanbeturnoff(),importVto.isEnterpriselevel(),
											importVto.isCustomized(),
											importVto.getOwner_percent(), 
						        			importVto.getRcm(), importVto.getRcp(), importVto.getAit(), false);
						        	chargingFacadeService.getWifiDeviceBatchDetailService().deviceStore(dmac, importVto.getSellor(), importVto.getPartner(), importVto.getImportor(), batchno);
								}
								for(String dmac:noActionDevices){//不需要变更owner 
									chargingFacadeService.doWifiDeviceSharedealConfigsUpdate(batchno,null, dmac,
											importVto.isCanbeturnoff(),importVto.isEnterpriselevel(),
											importVto.isCustomized(),
											importVto.getOwner_percent(), 
						        			importVto.getRcm(), importVto.getRcp(), importVto.getAit(), false);
						        	chargingFacadeService.getWifiDeviceBatchDetailService().deviceStore(dmac, importVto.getSellor(), importVto.getPartner(), importVto.getImportor(), batchno);
								}
								logger.info(String.format("B uid_willbinded:%s forceUnbindedDevices:%s", uid_willbinded,forceUnbindedDevices));
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
							backendBusinessService.blukIndexs(pages);
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace(System.out);
						} catch (Exception e) {
							e.printStackTrace(System.out);
						} finally{
							if(pages != null){
								pages.clear();
								pages = null;
							}
						}
					}
				}
			});
			
			batchImport.setSucceed(atomic_successed.get());
			batchImport.setFailed(atomic_failed.get());
			batchImport.setStatus(WifiDeviceBatchImport.STATUS_CONTENT_IMPORTED);
			/*WifiDeviceSharedealConfigs configs = chargingFacadeService.getWifiDeviceSharedealConfigsService().getById(WifiDeviceSharedealConfigs.Default_ConfigsWifiID);
			if(importVto.isCustomized()){
				batchImport.setAccess_internet_time(configs.getAit_pc());
				batchImport.setOwner_percent(String.valueOf(configs.getOwner_percent()));
				batchImport.setRange_cash_mobile(configs.getRange_cash_mobile());
				batchImport.setRange_cash_pc(configs.getRange_cash_pc());
			}*/
			chargingFacadeService.getWifiDeviceBatchImportService().update(batchImport);
		}finally{
		}
		logger.info(String.format("process message[%s] successful", message));
	}
}