package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchimport;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.charging.model.WifiDeviceBatchDetail;
import com.bhu.vas.api.rpc.charging.model.WifiDeviceBatchImport;
import com.bhu.vas.api.rpc.charging.vto.BatchImportVTO;
import com.bhu.vas.business.asyn.spring.model.async.BatchImportPreCheckDTO;
import com.bhu.vas.business.backendonline.asyncprocessor.buservice.BackendBusinessService;
import com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchimport.callback.ImportElementCallback;
import com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchimport.dto.DeviceCallbackDTO;
import com.bhu.vas.business.backendonline.asyncprocessor.service.iservice.IMsgHandlerService;
import com.bhu.vas.business.ds.charging.facade.ChargingFacadeService;
import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.tag.service.TagGroupRelationService;
import com.bhu.vas.business.ds.user.facade.UserWifiDeviceFacadeService;
import com.bhu.vas.business.ds.user.service.UserWifiDeviceService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

@Service
public class BatchImportPreCheckServiceHandler implements IMsgHandlerService {
	private final Logger logger = LoggerFactory.getLogger(BatchImportPreCheckServiceHandler.class);
	
	@Resource
	private WifiDeviceService wifiDeviceService;

	@Resource
	private DeviceFacadeService deviceFacadeService;
	
/*	@Resource
	private UserDeviceFacadeService userDeviceFacadeService;*/
	
	@Resource
	private UserWifiDeviceService userWifiDeviceService;
	
	@Resource
	private UserWifiDeviceFacadeService userWifiDeviceFacadeService;
	
	@Resource
	private ChargingFacadeService chargingFacadeService;
	
	@Resource
	private BackendBusinessService backendBusinessService;
	
	@Resource
	private TagGroupRelationService tagGroupRelationService;
	
	@Override
	public void process(String message) {
		logger.info(String.format("process message[%s]", message));
		try{
			BatchImportPreCheckDTO preCheck_dto = JsonHelper.getDTO(message, BatchImportPreCheckDTO.class);
			final String batchno = preCheck_dto.getBatchno();
			if(StringUtils.isEmpty(batchno)) return;
			WifiDeviceBatchImport batchImport = chargingFacadeService.getWifiDeviceBatchImportService().getById(batchno);
			if(batchImport == null){
				logger.info(String.format("process batchno[%s] not exist!", batchno));
			}
			if(batchImport.getStatus() != WifiDeviceBatchImport.STATUS_IMPORTED_FILE){
				logger.info(String.format("batchno[%s] is not in STATUS_IMPORTED_FILE status",  batchno));
				return;
			}
			batchImport.setStatus(WifiDeviceBatchImport.STATUS_CONTENT_PRE_CHECK);
			chargingFacadeService.getWifiDeviceBatchImportService().update(batchImport);
			final BatchImportVTO importVto = batchImport.toBatchImportVTO(null, null,null);
			int failed = ShipmentExcelImport.excelPreCheck(importVto.toAbsoluteFileInputPath(),importVto.toAbsoluteFileOutputPath(), new ImportElementCallback(){
				@Override
				public DeviceCallbackDTO elementDeviceInfoFetch(String sn) {
					ModelCriteria mc = new ModelCriteria();
			        mc.createCriteria().andSimpleCaulse("1=1").andColumnEqualTo("sn", sn);
			        List<String> macs_fromdb = wifiDeviceService.findIdsByModelCriteria(mc);//.findModelByModelCriteria(mc);
			        if(macs_fromdb.isEmpty()){
			        	return null;
			        }else{
			        	String dmac = macs_fromdb.get(0);
			        	DeviceCallbackDTO result = new DeviceCallbackDTO();
			        	result.setMac(dmac);
			        	WifiDeviceBatchDetail batch_detail = chargingFacadeService.getWifiDeviceBatchDetailService().getById(dmac);
			        	if(batch_detail != null)
			        		result.setLast_batchno(batch_detail.getLast_batchno());
			        	return result;
			        }
				}

				@Override
				public void afterExcelImported(Set<String> dmacs, Set<String> failed_sns) {
				}
			});
			
			batchImport.setSucceed(0);
			batchImport.setFailed(failed);
			batchImport.setStatus(WifiDeviceBatchImport.STATUS_CONTENT_PRE_CHECK_DONE);
			chargingFacadeService.getWifiDeviceBatchImportService().update(batchImport);
			logger.info(String.format("process message[%s] successful", message));
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error("error when import precheck check");
		}
	}	
}
