package com.bhu.vas.rpc.service.device;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.header.ParserHeader;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceMessageDispatchRpcService;

/**
 * 
 * @author tangzichao
 *
 */
@Service("deviceMessageDispatchRpcService")
public class DeviceMessageDispatchRpcService implements IDeviceMessageDispatchRpcService {
	private final Logger logger = LoggerFactory.getLogger(DeviceMessageDispatchRpcService.class);

	@Resource
	private DeviceBusinessRpcService deviceBusinessRpcService;

	/**
	 * rpc message dispatch
	 * @param type 消息主类型
	 * @param payload 消息内容
	 * @param parserHeader 其他header信息
	 */
	@Override
	public void messageDispatch(String payload, ParserHeader parserHeader) {
		logger.info(String.format("DeviceMessageRPC messageDispatch invoke message [%]", payload));
		int messageType = parserHeader.getType();
//		if(type == 5){
//			
//		}
		int masterType = parserHeader.getMt();
		int subType = parserHeader.getSt();
		if(masterType == 0){
			switch(subType){//子类型判断
				case 1://3.4.2	设备上线请求
					break;
				case 2://3.4.3	设备上线回应
					break;
				case 3://3.4.4	连接重定向消息
					break;
				case 4://3.4.5	时间同步请求
					break;
				case 5://3.4.6	时间同步响应
					break;
				case 6://3.4.7	管理参数下发
					break;
				case 7://3.4.8	保活请求
					break;
				case 8://3.4.9	保活响应
					break;
				default:
					break;
			}
		}else if(masterType == 1){
			switch(subType){//子类型判断
				case 1://3.4.14	文件传输消息(暂不实现)
					break;
				case 2://3.4.10	XML请求
					break;
				case 3://3.4.11	XML请求回应
					break;
				case 4://3.4.13	文件传输响应
					break;
				case 5://3.4.14	文件传输消息(暂不实现)
					break;
				case 6://3.4.15	设备事件信息
					break;
				case 7://3.4.16	WLAN用户上下线消息
					break;
				case 8://3.4.17	应用隧道消息
					break;
				default:
					break;
			}
		}
	}
	
	/*@Override
	public boolean deviceRegister(DeviceDTO dto, WifiDeviceContextDTO contextDto) {
		System.out.println(dto.toString());
		return false;
	}*/
	
	/**
	 * wifi设备上线
	 * 1：wifi设备基础信息更新
	 * 2：wifi设备在线更新
	 */
//	@Override
//	public boolean wifiDeviceRegister(String message, WifiDeviceContextDTO contextDto) {
//		logger.info(String.format("wifiDeviceRegister with params: message[%s]", message));
//		JoinReqDTO messageDto = RPCMessageParseHelper.generateDTOFromMessage(message, JoinReqDTO.class);
//		System.out.println(messageDto.getDto().getMac());
//		//System.out.println(messageDto.getDto().getOem_swver().length());
//		deviceFacadeService.wifiDeviceRegister(messageDto.getDto(), contextDto);
//		return true;
//	}
//
//	@Override
//	public boolean wifiDeviceLogout(String message, WifiDeviceContextDTO contextDto) {
//		return false;
//	}

}
