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
import com.bhu.vas.business.asyn.spring.model.BatchImportConfirmDTO;
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
			        List<WifiDevice> models = wifiDeviceService.findModelByModelCriteria(mc);
			        if(models.isEmpty()){
			        	atomic_failed.incrementAndGet();
			        	return null;
			        }
			        else{
			        	String dmac = models.get(0).getId();
			        	chargingFacadeService.doWifiDeviceSharedealConfigsUpdate(batchno,uid_willbinded==null?-1:uid_willbinded.intValue(), dmac, importVto.getOwner_percent(), 
			        			null, null, null, importVto.isCanbeturnoff(), false);
			        	/*WifiDeviceSharedealConfigs userfulWifiDeviceSharedealConfigs = chargingFacadeService.userfulWifiDeviceSharedealConfigs(dmac);
			        	userfulWifiDeviceSharedealConfigs.setBatchno(batchno);
			        	userfulWifiDeviceSharedealConfigs.setOwner_percent(importVto.getOwner_percent());
			        	userfulWifiDeviceSharedealConfigs.setManufacturer_percent(ArithHelper.round(ArithHelper.sub(1, importVto.getOwner_percent()), 2));
			        	userfulWifiDeviceSharedealConfigs.setCanbe_turnoff(importVto.isCanbeturnoff());
			        	userfulWifiDeviceSharedealConfigs.setRuntime_applydefault(false);
			        	chargingFacadeService.getWifiDeviceSharedealConfigsService().update(userfulWifiDeviceSharedealConfigs);*/
			        	//TODO:增加索引更新 是否可关闭
			        	DeviceCallbackDTO result = new DeviceCallbackDTO();
			        	result.setMac(dmac);
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
						}else{
							userDeviceFacadeService.doForceUnbindDevice(pages);
						}
						
						try {
							backendBusinessService.blukIndexs(pages);
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace(System.out);
						} catch (Exception e) {
							e.printStackTrace(System.out);
						}	
					}
				}
			});
			batchImport.setSucceed(atomic_successed.get());
			batchImport.setFailed(atomic_failed.get());
			batchImport.setStatus(WifiDeviceBatchImport.STATUS_CONTENT_IMPORTED);
			chargingFacadeService.getWifiDeviceBatchImportService().update(batchImport);
		}finally{
		}
		logger.info(String.format("process message[%s] successful", message));
	}
}
