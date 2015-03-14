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
	 * @param type 消息类型
	 * @param payload 消息内容
	 * @param parserHeader 其他header信息
	 */
	@Override
	public void messageDispatch(String ctx, String payload, ParserHeader parserHeader) {
		logger.info(String.format("DeviceMessageRPC messageDispatch invoke message [%]", payload));
//		int type = parserHeader.getType();
//		switch(type){
//			case ParserHeader.DeviceOffline_Prefix:
//				break;
//			case ParserHeader.DeviceNotExist_Prefix:
//				break;
//			case ParserHeader.Transfer_Prefix:
//				transferMessageDispatch(ctx, payload, parserHeader);
//				break;
//			default:
//				throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_ERROR.code());
//		}
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
					deviceBusinessRpcService.wifiDeviceOnline(ctx, payload);
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
		}else if(mType == ParserHeader.Transfer_mtype_1){
			switch(sType){//子类型判断
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
					deviceBusinessRpcService.handsetDeviceConnectState(ctx, payload);
					break;
				case 8://3.4.17	应用隧道消息
					break;
				default:
					break;
			}
		}
	}

	@Override
	public void messageDispatch1(String ctx, String payload) {
		// TODO Auto-generated method stub
		System.out.println(String.format("ctx[%s]", ctx));
	}

	@Override
	public void test(String ctx, ParserHeader parserHeader) {
		System.out.println(String.format("ctx[%s] dto[%s]", ctx,parserHeader));
		
	}

}
