package com.bhu.vas.rpc.facade;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.BusinessEnumType.TimPushChannel;
import com.bhu.vas.api.helper.BusinessEnumType.TimPushMsgType;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.message.dto.MessageSystemFailTaskDTO;
import com.bhu.vas.api.rpc.message.dto.MessageUserSigDTO;
import com.bhu.vas.api.rpc.message.dto.TimPushDTO;
import com.bhu.vas.api.rpc.message.dto.TimResponseBasicDTO;
import com.bhu.vas.api.rpc.message.dto.TimSendMsgDTO;
import com.bhu.vas.api.rpc.message.helper.MessageTimHelper;
import com.bhu.vas.business.asyn.spring.activemq.service.async.AsyncDeliverMessageService;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.bhu.vas.business.bucache.redis.serviceimpl.message.MessageSystemFailedTaskListService;
import com.bhu.vas.business.bucache.redis.serviceimpl.message.MessageSystemHashService;
import com.bhu.vas.business.ds.message.facade.MessageUserFacadeService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class MessageUnitFacadeService {
	private final Logger logger = LoggerFactory.getLogger(MessageUnitFacadeService.class);
	@Resource
	private MessageUserFacadeService messageUserFacadeService;
	@Resource
	private AsyncDeliverMessageService asyncDeliverMessageService;
	public RpcResponseDTO<MessageUserSigDTO> fetch_usersig(Integer uid, Integer channel) {
		try{
			if (!MessageTimHelper.isSupportChannel(channel)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_DATA_PARAM_ERROR,new String[]{"channel"});
			}
			MessageUserSigDTO dto = new MessageUserSigDTO();
			String sig = fetchTimUserSig(uid+"", BusinessKeyDefine.Message.User, channel, MessageTimHelper.defaultExpire);
			if (sig != null){
				dto.setSig(sig);
				logger.info(String.format("fetch_usersig user[%s] sig[%s] successful!", uid, sig));
				return RpcResponseDTOBuilder.builderSuccessRpcResponse(dto);
			}else{
				logger.info(String.format("fetch_usersig failed user[%s]",uid));
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
			}
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error("fetch_usersig exception", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<MessageUserSigDTO> fetch_visitor_usersig(String user, Integer channel) {
		try{
			if (!MessageTimHelper.isSupportChannel(channel)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_DATA_PARAM_ERROR,new String[]{"channel"});
			}
			MessageUserSigDTO dto = new MessageUserSigDTO();
			String sig = fetchTimUserSig(user, BusinessKeyDefine.Message.Visitor, channel, MessageTimHelper.visitorExpire);
			if (sig != null){
				dto.setSig(sig);
				logger.info(String.format("fetch_visitor_usersig user[%s] sig[%s] successful!", user, sig));
				return RpcResponseDTOBuilder.builderSuccessRpcResponse(dto);
			}else{
				logger.info(String.format("fetch_visitor_usersig failed user[%s]",user));
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
			}
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error("fetch_usersig exception", ex);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	private String fetchTimUserSig(String user, String utype, Integer channel, String expire){
		String sig = MessageSystemHashService.getInstance().fetchMessageUserSig(user, utype);
		boolean newly = false;
		if (sig == null){
			logger.info(String.format("fetch_usersig can't fetch user[%s] from redis", user));
			sig = MessageTimHelper.createUserSig(user, expire);
			
			MessageSystemHashService.getInstance().setMessageUserSig(user, utype, sig);
			MessageSystemHashService.getInstance().setMessageUserSigExpire(user, utype, expire);
			newly = true;
		}
		asyncDeliverMessageService.sendBatchTimUserAddTagActionMessage(user, sig, utype, channel, newly);
		return sig;
	}

	public RpcResponseDTO<TimResponseBasicDTO> send_single_msg(Integer sendChannel, String toAcc, Integer msgType,
			String content) {
		String url = MessageTimHelper.Tim_Url+MessageTimHelper.OpenIM+MessageTimHelper.Action_Send_Msg;
		TimPushChannel channel = MessageTimHelper.validateTimPushChannel(sendChannel);
		if (channel == null)
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.MESSAGE_PUSH_CHANNEL_ERROR);
		
		TimPushMsgType type = MessageTimHelper.validateTimPushMsgType(msgType);
		if (type == null)
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.MESSAGE_MSG_TYPE_ERROR);
		
		TimSendMsgDTO<Object> sendDto = MessageTimHelper.builderSendMsg(channel, 
				toAcc, type, content);
		String message = JsonHelper.getJSONString(sendDto);
		
		try{
			TimResponseBasicDTO ret_dto = MessageTimHelper.CreateTimUrlCommunication(url, message);
			if (ret_dto == null){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_CONNECT_TIMEOUT_ERROR);
			}
			if (ret_dto.isTimServerError()){
				logger.info(String.format("send_single_msg url[%s] message[%s] failed ", url, message));
				Long rpush_ret = MessageSystemFailedTaskListService.getInstance().rpushFailedTaskKey(MessageSystemFailTaskDTO.builder(url, message));
				if (rpush_ret != null && rpush_ret > 0){
					logger.info(String.format("send_single_msg add failedTask url[%s] message[%s] ", url, message));
				}
			}
			if (ret_dto.isExecutedSuccess()){
				logger.info(String.format("send_single_msg url[%s] message[%s] successful ", url, message));
			}
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(ret_dto);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			Long rpush_ret = MessageSystemFailedTaskListService.getInstance().rpushFailedTaskKey(MessageSystemFailTaskDTO.builder(url, message));
			if (rpush_ret != null && rpush_ret > 0){
				logger.info(String.format("send_single_msg catch Exception add failedTask url[%s] message[%s] ", url, message));
			}
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<TimResponseBasicDTO> send_push(Integer sendChannel, String tags, int msgLifeTime,
			Integer msgType, String content) {
		String url = MessageTimHelper.Tim_Url+MessageTimHelper.OpenIM+MessageTimHelper.Action_IM_Push;
		TimPushChannel channel = MessageTimHelper.validateTimPushChannel(sendChannel);
		if (channel == null)
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.MESSAGE_PUSH_CHANNEL_ERROR);
		
		TimPushMsgType type = MessageTimHelper.validateTimPushMsgType(msgType);
		if (type == null)
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.MESSAGE_MSG_TYPE_ERROR);
		
		TimPushDTO<Object> pushDTO = MessageTimHelper.builderPush(channel, msgLifeTime, tags, type, content);
		String message = JsonHelper.getJSONString(pushDTO);
		
		try{
			TimResponseBasicDTO ret_dto = MessageTimHelper.CreateTimUrlCommunication(url, message);
			if (ret_dto == null){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_CONNECT_TIMEOUT_ERROR);
			}
			if (ret_dto.isTimServerError()){
				logger.info(String.format("send_push url[%s] message[%s] failed ", url, message));
				Long rpush_ret = MessageSystemFailedTaskListService.getInstance().rpushFailedTaskKey(MessageSystemFailTaskDTO.builder(url, message));
				if (rpush_ret != null && rpush_ret > 0){
					logger.info(String.format("send_push add failedTask url[%s] message[%s] ", url, message));
				}
			}
			
			if (ret_dto.isExecutedSuccess()){
				logger.info(String.format("send_push url[%s] message[%s] successful ", url, message));
			}
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(ret_dto);
		}catch(Exception ex){
			Long rpush_ret = MessageSystemFailedTaskListService.getInstance().rpushFailedTaskKey(MessageSystemFailTaskDTO.builder(url, message));
			if (rpush_ret != null && rpush_ret > 0){
				logger.info(String.format("send_push catch Exception add failedTask url[%s] message[%s] ", url, message));
			}
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
}
