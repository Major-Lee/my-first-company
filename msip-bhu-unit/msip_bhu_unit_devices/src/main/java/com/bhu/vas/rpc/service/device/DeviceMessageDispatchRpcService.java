package com.bhu.vas.rpc.service.device;

import java.util.List;

import javax.annotation.Resource;

import org.dom4j.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bhu.vas.api.dto.WifiDeviceDTO;
import com.bhu.vas.api.dto.header.ParserHeader;
import com.bhu.vas.api.dto.ret.QuerySerialReturnDTO;
import com.bhu.vas.api.dto.ret.WifiDeviceVapReturnDTO;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.RPCMessageParseHelper;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceMessageDispatchRpcService;
import com.bhu.vas.api.rpc.task.model.WifiDeviceDownTask;
import com.bhu.vas.rpc.facade.DeviceBusinessFacadeService;
import com.smartwork.msip.exception.RpcBusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * device rpc组件服务service 对外暴露接口
 * 处理设备上行消息
 * @author tangzichao
 *
 */
@Service("deviceMessageDispatchRpcService")
public class DeviceMessageDispatchRpcService implements IDeviceMessageDispatchRpcService {
	private final Logger logger = LoggerFactory.getLogger(DeviceMessageDispatchRpcService.class);

	@Resource
	private DeviceBusinessFacadeService deviceBusinessFacadeService;

	/**
	 * rpc message dispatch
	 * @param type 消息类型
	 * @param payload 消息内容
	 * @param parserHeader 其他header信息
	 */
	@Override
	public void messageDispatch(String ctx, String payload, ParserHeader parserHeader) {
		logger.info(String.format("DeviceMessageRPC messageDispatch invoke ctx [%s] payload [%s] header[%s]", ctx, payload, parserHeader));
		
		try{
			int type = parserHeader.getType();
			switch(type){
				case ParserHeader.DeviceOffline_Prefix:
					deviceBusinessFacadeService.wifiDeviceOffline(ctx, parserHeader.getMac());
					break;
				case ParserHeader.DeviceNotExist_Prefix:
					deviceBusinessFacadeService.wifiDeviceNotExist(ctx, parserHeader.getMac());
					break;
				case ParserHeader.Transfer_Prefix:
					transferMessageDispatch(ctx, payload, parserHeader);
					break;
				default:
					messageDispatchUnsupport(ctx, payload, parserHeader);
					break;
			}
			logger.info(String.format("DeviceMessageRPC messageDispatch successful ctx [%s] payload [%s] header[%s]",
					ctx, payload, parserHeader));
			//logger.info("1");
		}catch(RpcBusinessI18nCodeException ex){
			logger.info(String.format("DeviceMessageRPC messageDispatch failed ctx [%s] payload [%s] header[%s]", 
					ctx, payload, parserHeader));
			throw ex;
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceMessageRPC messageDispatch exception ctx [%s] payload [%s] header[%s] exmsg[%s]",
					ctx, payload, parserHeader, ex.getMessage()), ex);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR.code());
		}
		
	}
	
	/**
	 * 处理transfer message的业务调用 messageType 5
	 * @param payload
	 * @param parserHeader
	 */
	public void transferMessageDispatch(String ctx, String payload, ParserHeader parserHeader){
		int mType = parserHeader.getMt();
		int sType = parserHeader.getSt();
		if(mType == ParserHeader.Transfer_mtype_0){
			switch(sType){//子类型判断
				case 1://3.4.2	设备上线请求
					deviceBusinessFacadeService.wifiDeviceOnline(ctx, payload);
					break;
//				case 2://3.4.3	设备上线回应
//					break;
//				case 3://3.4.4	连接重定向消息
//					break;
//				case 4://3.4.5	时间同步请求
//					break;
//				case 5://3.4.6	时间同步响应
//					break;
//				case 6://3.4.7	管理参数下发
//					break;
//				case 7://3.4.8	保活请求
//					break;
//				case 8://3.4.9	保活响应
//					break;
				default:
					messageDispatchUnsupport(ctx, payload, parserHeader);
					break;
			}
		}else if(mType == ParserHeader.Transfer_mtype_1){
			switch(sType){//子类型判断
//				case 1://3.4.10	XML请求
//					break;
				case 2://3.4.11	XML请求回应
					taskResponse(ctx, payload, parserHeader);
					break;
//				case 3://3.4.12	文件传输请求
//					break;
//				case 4://3.4.13	文件传输响应
//					break;
//				case 5://3.4.14	文件传输消息(暂不实现)
//					break;
				case 6://3.4.15	设备事件信息
					deviceBusinessFacadeService.wifiDeviceAlarm(ctx, payload);
					break;
				case 7://3.4.16	WLAN用户上下线消息
					deviceBusinessFacadeService.handsetDeviceConnectState(ctx, payload, parserHeader);
					break;
//				case 8://3.4.17	应用隧道消息
//					break;
				case 10://3.4.19 设备配置变更消息
					deviceBusinessFacadeService.deviceSettingChanged(ctx, payload, parserHeader.getMac(), parserHeader.getTaskid());
					break;
				case 11://3.4.20 notify命令执行结果通知消息
					taskNotifyResponse(ctx, payload, parserHeader);
					break;
				case ParserHeader.Transfer_stype_12://增值指令
					deviceVapModuleResponse(ctx, payload, parserHeader);
					//taskNotifyResponse(ctx, payload, parserHeader);
					break;	
				default:
					messageDispatchUnsupport(ctx, payload, parserHeader);
					break;
			}
		}else{
			messageDispatchUnsupport(ctx, payload, parserHeader);
		}
	}

	/**
	 * 任务响应处理
	 * @param ctx
	 * @param payload
	 * @param parserHeader
	 */
	public void taskResponse(String ctx, String payload, ParserHeader parserHeader){
		String opt = parserHeader.getOpt();
		if(!StringUtils.isEmpty(opt)){
			String mac = parserHeader.getMac();
			long taskid = parserHeader.getTaskid();
			OperationCMD cmd = OperationCMD.getOperationCMDFromNo(opt);
			if(cmd != null){
				switch(cmd){
					case QueryDeviceStatus:
						deviceBusinessFacadeService.taskQueryDeviceStatus(ctx, payload, mac, taskid);
						break;
					case QueryDeviceFlow:
						deviceBusinessFacadeService.taskQueryDeviceFlow(ctx, payload, mac, taskid);
						break;
					case TurnOnDeviceDPINotify:
						deviceBusinessFacadeService.taskCommonProcessor(ctx,payload,mac,taskid);
						break;
					case QueryDeviceLocationNotify:
						deviceBusinessFacadeService.taskQueryOldDeviceLocationNotifyProcessor(ctx,payload,mac,taskid);
						break;
					case QueryDeviceLocationS2:
						deviceBusinessFacadeService.taskQueryDeviceLocationS2(ctx, payload, mac, taskid);
						break;
					case QueryDeviceSetting:
						deviceBusinessFacadeService.taskQueryDeviceSetting(ctx, payload, mac, taskid);
						break;
					case ModifyDeviceSetting:
						deviceBusinessFacadeService.taskModifyDeviceSetting(ctx, payload, mac, taskid);
						break;
					case DeviceDelayReboot:
						deviceBusinessFacadeService.taskCommonProcessor(ctx, payload, mac, taskid);
						break;
					case QueryDhcpcStatus:
						deviceBusinessFacadeService.taskQueryDhcpcStatus(ctx, payload, mac, taskid);
						break;
					case QueryDeviceUsedStatus:
						deviceBusinessFacadeService.taskQueryDeviceUsedStatus(ctx, payload, mac, taskid);
						break;
					case DeviceWifiTimerStart:
						deviceBusinessFacadeService.taskWifiTimerStart(ctx, payload, mac, taskid);
						break;
					case DeviceWifiTimerStop:
						deviceBusinessFacadeService.taskWifiTimerStop(ctx, payload, mac, taskid);
						break;
					case DeviceWifiTimerQuery:
						deviceBusinessFacadeService.taskWifiTimerQuery(ctx, payload, mac, taskid);
						break;
					case QueryDeviceSpeedNotify:
						deviceBusinessFacadeService.taskQueryDeviceSpeed(ctx, payload, mac, taskid);
						break;
					/*case TriggerHttp404ResourceUpdate:
						deviceBusinessFacadeService.taskTriggerHttp404Processor(ctx, payload, mac, taskid);
						break;	*/
					case TriggerHttpPortalResourceUpdate:
						deviceBusinessFacadeService.taskTriggerHttpPortalProcessor(ctx, payload, mac, taskid);
						break;
					case DeviceUpgrade:
						deviceBusinessFacadeService.taskDeviceUpgrade(ctx, payload, mac, taskid);
						break;
					case QueryDeviceSysinfo:
						deviceBusinessFacadeService.taskQuerySysinfoSpeed(ctx, payload, mac, taskid);
						break;
					default:
						messageDispatchUnsupport(ctx, payload, parserHeader);
						break;
				}
			}else{
				messageDispatchUnsupport(ctx, payload, parserHeader);
			}
			
			/*if(OperationCMD.QueryDeviceStatus.getNo().equals(opt)){
				deviceBusinessFacadeService.taskQueryDeviceStatus(ctx, payload, mac, taskid);
			}else if(OperationCMD.QueryDeviceFlow.getNo().equals(opt)){
				deviceBusinessFacadeService.taskQueryDeviceFlow(ctx, payload, mac, taskid);
			}else if(OperationCMD.TurnOnDeviceDPINotify.getNo().equals(opt) || OperationCMD.TurnOffDeviceDPINotify.getNo().equals(opt)){
				deviceBusinessFacadeService.taskCommonProcessor(ctx,payload,mac,taskid);
			}
			else if(OperationCMD.QueryDeviceLocationNotify.getNo().equals(opt)){
				//老版本由于不支持notify，所以会出现到这里，需要特殊处理
				//TODO：do nothing 由input processor解析后直接转到daemon
				deviceBusinessFacadeService.taskQueryOldDeviceLocationNotifyProcessor(ctx,payload,mac,taskid);
			}
			else if(OperationCMD.QueryDeviceLocationS2.getNo().equals(opt)){
				deviceBusinessFacadeService.taskQueryDeviceLocationS2(ctx, payload, mac, taskid);
			}
			else if(OperationCMD.QueryDeviceSetting.getNo().equals(opt)){
				deviceBusinessFacadeService.taskQueryDeviceSetting(ctx, payload, mac, taskid);
			}
			else if(OperationCMD.ModifyDeviceSetting.getNo().equals(opt)){
				deviceBusinessFacadeService.taskModifyDeviceSetting(ctx, payload, mac, taskid);
			}
			else if(OperationCMD.DeviceDelayReboot.getNo().equals(opt)){
				deviceBusinessFacadeService.taskCommonProcessor(ctx, payload, mac, taskid);
			}
			else if(OperationCMD.QueryDhcpcStatus.getNo().equals(opt)){
				deviceBusinessFacadeService.taskQueryDhcpcStatus(ctx, payload, mac, taskid);
			}
			else if(OperationCMD.QueryDeviceUsedStatus.getNo().equals(opt)){
				
			}
			else if(OperationCMD.DeviceWifiTimerStart.getNo().equals(opt)){
				deviceBusinessFacadeService.taskWifiTimerStart(ctx, payload, mac, taskid);
			}
			else if(OperationCMD.DeviceWifiTimerStop.getNo().equals(opt)){
				deviceBusinessFacadeService.taskWifiTimerStop(ctx, payload, mac, taskid);
			}
			else if(OperationCMD.DeviceWifiTimerQuery.getNo().equals(opt)){
				deviceBusinessFacadeService.taskWifiTimerQuery(ctx, payload, mac, taskid);
			}
			else if(OperationCMD.TriggerHttp404ResourceUpdate.getNo().equals(opt)){
				deviceBusinessFacadeService.taskTriggerHttp404Processor(ctx, payload, mac, taskid);
			}
			else if(OperationCMD.TriggerHttpPortalResourceUpdate.getNo().equals(opt)){
				deviceBusinessFacadeService.taskTriggerHttpPortalProcessor(ctx, payload, mac, taskid);
			}
			else{
				messageDispatchUnsupport(ctx, payload, parserHeader);
			}*/
		}
	}
	/**
	 * 通过notify参数下发的任务响应处理
	 * @param ctx
	 * @param payload
	 * @param parserHeader
	 */
	public void taskNotifyResponse(String ctx, String payload, ParserHeader parserHeader){
		Document doc = RPCMessageParseHelper.parserMessage(payload);
		QuerySerialReturnDTO serialDto = RPCMessageParseHelper.generateDTOFromMessage(doc, QuerySerialReturnDTO.class);
		long taskid = 0l; 

		String serial = serialDto.getSerial();
		if(!StringUtils.isEmpty(serial)){
			//AP106P06V1.2.15Build8057以后版本的设备以13位作为serial
			if(serial.length() == 13){
				String opt = serial.substring(0, 3);
				//long taskid = Long.parseLong(serial.substring(3, 10));
				taskid = Long.parseLong(serial.substring(3, 13));
				String mac = parserHeader.getMac().toLowerCase();
				
				if(WifiDeviceDownTask.State_Done.equals(serialDto.getStatus())
						|| WifiDeviceDownTask.State_Next.equals(serialDto.getStatus())){
					
					if(OperationCMD.QueryDeviceLocationNotify.getNo().equals(opt)){
						deviceBusinessFacadeService.taskQueryDeviceLocationNotify(ctx, doc, serialDto, mac, taskid);
					}
					else if(OperationCMD.QueryDeviceSpeedNotify.getNo().equals(opt)){
						deviceBusinessFacadeService.taskQueryDeviceSpeedNotify(ctx, doc, serialDto, mac, taskid);
					}
					else if(OperationCMD.QueryDeviceRateNotify.getNo().equals(opt)){
						deviceBusinessFacadeService.taskQueryDeviceRateNotify(ctx, doc, serialDto, mac, taskid);
					}
					else if(OperationCMD.QueryDeviceTerminals.getNo().equals(opt)){
						deviceBusinessFacadeService.taskQueryDeviceTerminalsNotify(ctx, doc, serialDto, mac, taskid);
					}
					else if(OperationCMD.TriggerHttpPortalResourceUpdate.getNo().equals(opt)){
						deviceBusinessFacadeService.taskNotifyTriggerHttpPortalProcessor(ctx, payload, mac, taskid);
					}
				}
			}
			//兼容AP106P06V1.2.15Build8057版本以前的设备 升级发送的serial为10位 处理方式 截取第一位后面的内容作为任务id
			else{
				String serial_substring = serial.substring(1);
				if(!StringUtils.isEmpty(serial)){
					taskid = Long.parseLong(serial_substring);
				}
			}
		}	
		
		//2:任务callback
		deviceBusinessFacadeService.doTaskCallback(taskid, serialDto.getStatus(), payload);
	}
	
	public void messageDispatchUnsupport(String ctx, String payload, ParserHeader parserHeader){
		logger.info(String.format("DeviceMessageRPC messageDispatch unsupport msg ctx [%s] payload [%s] header[%s]", ctx, payload, parserHeader));
		//throw new RpcBusinessI18nCodeException(ResponseErrorCode.RPC_MESSAGE_UNSUPPORT.code());
	}

	
	public void deviceVapModuleResponse(String ctx, String payload, ParserHeader parserHeader){
		System.out.printf("~~~~~~~~~~ctx[%s] ParserHeader[%s] playload[%s]", ctx,parserHeader,payload);
		long taskid = parserHeader.getTaskid();
		String mac = parserHeader.getMac().toLowerCase();
		switch(parserHeader.getVaptype()){
			case ParserHeader.Vap_Module_Register_D2S:
			case ParserHeader.Vap_Module_VapQuery_D2S:
				Document doc = RPCMessageParseHelper.parserMessage(payload);
				WifiDeviceVapReturnDTO vapDTO = RPCMessageParseHelper.generateVapDTOFromMessage(doc);
				deviceBusinessFacadeService.processVapModuleResponse(ctx,mac, vapDTO,taskid);
				break;
			default:
				messageDispatchUnsupport(ctx, payload, parserHeader);
				break;
		}
	}
	
	/**
	 * CM与控制层的连接断开以后 会分批次批量发送在此CM上的wifi设备在线信息
	 */
	@Override
	public void cmupWithWifiDeviceOnlines(String ctx, List<WifiDeviceDTO> dtos) {
		try{
			logger.info(String.format("DeviceMessageRPC cmupWithWifiDeviceOnlines invoke ctx [%s] ", ctx));
			
			deviceBusinessFacadeService.cmupWithWifiDeviceOnlines(ctx, dtos);
			
			logger.info(String.format("DeviceMessageRPC cmupWithWifiDeviceOnlines successful ctx [%s] ",ctx));
		}catch(RpcBusinessI18nCodeException ex){
			logger.info(String.format("DeviceMessageRPC cmupWithWifiDeviceOnlines failed ctx [%s] ", ctx));
			throw ex;
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceMessageRPC cmupWithWifiDeviceOnlines exception ctx [%s] exmsg[%s]",
					ctx, ex.getMessage()), ex);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR.code());
		}
	}

}
