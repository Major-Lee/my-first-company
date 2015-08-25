package com.bhu.vas.business.backendonline.asyncprocessor.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.daemon.helper.DaemonHelper;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceGroup;
import com.bhu.vas.api.rpc.task.model.WifiDeviceDownTask;
import com.bhu.vas.business.asyn.spring.model.WifiDeviceAsynCmdGenerateDTO;
import com.bhu.vas.business.asyn.spring.model.WifiDeviceGroupAsynCreateIndexDTO;
import com.bhu.vas.business.backendonline.asyncprocessor.service.AsyncMsgHandleService;
import com.bhu.vas.business.backendonline.asyncprocessor.service.indexincr.WifiDeviceIndexIncrementService;
import com.bhu.vas.business.backendonline.asyncprocessor.service.iservice.IMsgHandlerService;
import com.bhu.vas.business.ds.device.service.WifiDeviceGroupRelationService;
import com.bhu.vas.business.ds.device.service.WifiDeviceGroupService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.task.facade.TaskFacadeService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;

@Service
public class WifiDeviceGroupServiceHandler implements IMsgHandlerService {
	private final Logger logger = LoggerFactory.getLogger(AsyncMsgHandleService.class);
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private WifiDeviceGroupService wifiDeviceGroupService;

	@Resource
	private WifiDeviceGroupRelationService wifiDeviceGroupRelationService;
	
	@Resource
	private TaskFacadeService taskFacadeService;
	
	@Resource
	private IDaemonRpcService daemonRpcService;

	@Resource
	private WifiDeviceIndexIncrementService wifiDeviceIndexIncrementService;


	@Override
	public void process(String message) {
		logger.info(String.format("WifiDeviceGroupServiceHandler process message[%s]", message));
		List<String> totalDevices = null;
		WifiDeviceGroup dgroup = null;
		List<String> onlineDevices = null;
		try{
			WifiDeviceAsynCmdGenerateDTO dto = JsonHelper.getDTO(message, WifiDeviceAsynCmdGenerateDTO.class);
			totalDevices = new ArrayList<String>();
			if(StringUtils.isNotEmpty(dto.getMac())) totalDevices.add(dto.getMac());
			if(dto.getGid() > 0){
				dgroup = wifiDeviceGroupService.getById(dto.getGid());
				if(dgroup != null){
					if(!dto.isDependency()){
						totalDevices = wifiDeviceGroupRelationService.getDeviceIdsByGroupId(dto.getGid());
					}else{
						List<WifiDeviceGroup> allByPath = wifiDeviceGroupService.fetchAllByPath(dgroup.getPath(), true);
						for(WifiDeviceGroup _dgroup:allByPath){
							totalDevices.addAll(wifiDeviceGroupRelationService.getDeviceIdsByGroupId(_dgroup.getId()));
						}
					}
				}
			}
			if(totalDevices.isEmpty()) {
				logger.info(String.format("WifiDeviceGroupServiceHandler totalDevices empty!"));
				return;
			}
			//只给在线的设备发送指令
			onlineDevices = wifiDeviceService.filterOnlineIdsWith(totalDevices, true);
			if(onlineDevices.isEmpty()){
				logger.info(String.format("WifiDeviceGroupServiceHandler onlineDevices empty!"));
				return;
			}
			for(String wifi_id:onlineDevices){
				try{
					WifiDeviceDownTask downTask = taskFacadeService.apiTaskGenerate(dto.getUid(), wifi_id, dto.getOpt(), dto.getSubopt(), 
							dto.getExtparams(), dto.getChannel(), dto.getChannel_taskid());
					DaemonHelper.daemonCmdDown(wifi_id, downTask.getPayload(), daemonRpcService);
				}catch(BusinessI18nCodeException bex){
					System.out.println(bex.getErrorCode());
					bex.printStackTrace(System.out);
					logger.error("TaskGenerate invoke exception : " + bex.getMessage(), bex);
				}catch(Exception ex){
					ex.printStackTrace(System.out);
					logger.error("TaskGenerate invoke exception : " + ex.getMessage(), ex);
				}
			}
		}finally{
			if(onlineDevices != null){
				onlineDevices.clear();
				onlineDevices = null;
			}
			if(totalDevices != null){
				totalDevices.clear();
				totalDevices = null;
			}
			if(dgroup != null){
				dgroup = null;
			}
		}
		
		logger.info(String.format("WifiDeviceGroupServiceHandler process message[%s] successful", message));
	}


	@Override
	public void createDeviceGroupIndex(String message) {

		logger.info(String.format("WifiDeviceGroupServiceHandler createDeviceGroupIndex message[%s]", message));
		WifiDeviceGroupAsynCreateIndexDTO dto = JsonHelper.getDTO(message, WifiDeviceGroupAsynCreateIndexDTO.class);
		String wifiIdsStr = dto.getWifiIds();
		String gidsStr = dto.getGroupIds();

		if(StringUtils.isEmpty(gidsStr)) return;
		
		List<String> wifiIds = new ArrayList<>();
		String[] wifiIdArray = wifiIdsStr.split(StringHelper.COMMA_STRING_GAP);
		for (String wifiId : wifiIdArray) {
			wifiIds.add(wifiId);
		}
		List<WifiDevice> wifiDeviceList = wifiDeviceService.findByIds(wifiIds);


		List<List<Long>> groupIdList = new ArrayList<List<Long>>();

		String[] groupidArray = gidsStr.split(StringHelper.COMMA_STRING_GAP);

		for (String singleGroupIds : groupidArray) {
			List<Long> groupIds = new ArrayList<Long>();
			String[] groupids_array = singleGroupIds.split(StringHelper.WHITESPACE_STRING_GAP);
			for(String gid : groupids_array){
				try {
					if(!StringHelper.isEmpty(gid))
						groupIds.add(Long.parseLong(gid));
				} catch (Exception e) {
					e.printStackTrace(System.out);
					logger.error("createDeviceGroupIndex invoke exception : " + e.getMessage(), e);
				}
			}
			groupIdList.add(groupIds);
		}

		wifiDeviceIndexIncrementService.wifiDeviceIndexBlukIncrement(wifiDeviceList, groupIdList);

	}
}
