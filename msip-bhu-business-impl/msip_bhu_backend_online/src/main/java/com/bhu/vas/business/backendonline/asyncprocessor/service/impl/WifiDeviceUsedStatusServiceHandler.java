package com.bhu.vas.business.backendonline.asyncprocessor.service.impl;

import javax.annotation.Resource;

import org.dom4j.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.redis.DeviceUsedStatisticsDTO;
import com.bhu.vas.api.dto.ret.QuerySerialReturnDTO;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.RPCMessageParseHelper;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
import com.bhu.vas.api.rpc.task.model.WifiDeviceDownTask;
import com.bhu.vas.business.asyn.spring.model.WifiDeviceUsedStatusDTO;
import com.bhu.vas.business.backendonline.asyncprocessor.service.AsyncMsgHandleService;
import com.bhu.vas.business.backendonline.asyncprocessor.service.iservice.IMsgHandlerService;
import com.bhu.vas.business.bucache.redis.serviceimpl.marker.BusinessMarkerService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.task.facade.TaskFacadeService;
import com.smartwork.msip.cores.helper.JsonHelper;

@Service
public class WifiDeviceUsedStatusServiceHandler implements IMsgHandlerService {
	private final Logger logger = LoggerFactory.getLogger(AsyncMsgHandleService.class);
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
/*	@Resource
	private WifiDeviceGroupService wifiDeviceGroupService;

	@Resource
	private WifiDeviceGroupRelationService wifiDeviceGroupRelationService;*/
	
	@Resource
	private TaskFacadeService taskFacadeService;
	
	@Resource
	private IDaemonRpcService daemonRpcService;

	//@Resource
	//private WifiDeviceWholeDayUsedLogMService wifiDeviceWholeDayUsedLogMService;


	@Override
	public void process(String message) {
		logger.info(String.format("WifiDeviceUsedStatusServiceHandler process message[%s]", message));
		try{
			WifiDeviceUsedStatusDTO dto = JsonHelper.getDTO(message, WifiDeviceUsedStatusDTO.class);
			Document doc = RPCMessageParseHelper.parserMessage(dto.getResponse());
			QuerySerialReturnDTO serialDto = RPCMessageParseHelper.generateDTOFromMessage(doc, QuerySerialReturnDTO.class);
			if(WifiDeviceDownTask.State_Done.equals(serialDto.getStatus()) 
					&& OperationCMD.QueryDeviceUsedStatus.getCmd().equals(serialDto.getCmd())){
				DeviceUsedStatisticsDTO parser_dto = RPCMessageParseHelper.generateDTOFromQueryDeviceUsedStatus(doc);
				BusinessMarkerService.getInstance().deviceUsedStatisticsSet(dto.getMac(), parser_dto);
/*				//TODO：写入mongo日志表中
				Date current = new Date();
				String today = DateTimeHelper.formatDate(current, DateTimeHelper.FormatPattern5);
				wifiDeviceWholeDayUsedLogMService.upsertUsedStatus(today, dto.getMac(),
						WifiDeviceWholeDayUsedLogMDTO.fromDailyUsedStatisticsDTO(today, dto.getMac(), parser_dto.getToday()) );
				String yesterday = DateTimeHelper.formatDate( DateTimeHelper.getDateDaysAgo(1), DateTimeHelper.FormatPattern5);
				wifiDeviceWholeDayUsedLogMService.upsertUsedStatus(yesterday, dto.getMac(), 
						WifiDeviceWholeDayUsedLogMDTO.fromDailyUsedStatisticsDTO(yesterday, dto.getMac(), parser_dto.getYesterday()));*/
			}
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}
		
		logger.info(String.format("WifiDeviceUsedStatusServiceHandler process message[%s] successful", message));
	}

	@Override
	public void createDeviceGroupIndex(String message) {
		// TODO Auto-generated method stub
	}

}
