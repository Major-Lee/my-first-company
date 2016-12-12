package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchimport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.DownCmds;
import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.OperationDS;
import com.bhu.vas.api.helper.SharedNetworkChangeType;
import com.bhu.vas.api.helper.SharedNetworksHelper;
import com.bhu.vas.api.helper.VapEnumType.SharedNetworkType;
import com.bhu.vas.api.helper.WifiDeviceDocumentEnumType.SnkTurnStateEnum;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.DeviceStatusExchangeDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.notify.ISharedNetworkNotifyCallback;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserWifiDevice;
import com.bhu.vas.business.asyn.spring.model.IDTO;
import com.bhu.vas.business.asyn.spring.model.async.BatchBindUnbindDeviceDTO;
import com.bhu.vas.business.backendonline.asyncprocessor.buservice.BackendBusinessService;
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
import com.smartwork.msip.cores.orm.support.page.PageHelper;

@Service
public class BatchBindUnbindDeviceServiceHandler implements IMsgHandlerService {
	private final Logger logger = LoggerFactory.getLogger(BatchBindUnbindDeviceServiceHandler.class);
	
	@Resource
	private WifiDeviceService wifiDeviceService;

	@Resource
	private DeviceFacadeService deviceFacadeService;
	
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
			BatchBindUnbindDeviceDTO dto = JsonHelper.getDTO(message, BatchBindUnbindDeviceDTO.class);
			String[] dmacs = dto.getMacs().split(StringHelper.COMMA_STRING_GAP);
			final Integer uid_willbinded = UniqueFacadeService.fetchUidByMobileno(dto.getCc(), dto.getMobileno());
			if(dto.getDtoType() == IDTO.ACT_ADD && (uid_willbinded == null || uid_willbinded <= 0)){
				logger.error(String.format("can't find uid by cc[%s], mobile[%s]", dto.getCc(), dto.getMobileno()));
				return;
			}
			
			List<String> all_dmacs = Arrays.asList(dmacs);
			int total = all_dmacs.size();
			int totalPages = PageHelper.getTotalPages(total, 100);
			
			for(int pageno= 1;pageno<=totalPages;pageno++){
				List<String> pages = PageHelper.pageList(all_dmacs, pageno, 100);
				logger.info(String.format("pageno:%s pagesize:%s pages:%s", pageno,100,pages));

				if(dto.getDtoType() == IDTO.ACT_ADD)
					bindUnbindDeviceService.bindDevice(pages, uid_willbinded);
				else
					bindUnbindDeviceService.unbindDevice(pages, false);
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
			logger.info(String.format("process message[%s] successful", message));
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		}
	}
}
