package com.bhu.vas.rpc.facade;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.message.dto.MessageUserSigDTO;
import com.bhu.vas.api.rpc.message.helper.MessageTimHelper;
import com.bhu.vas.api.rpc.message.model.MessageUser;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.bhu.vas.business.bucache.redis.serviceimpl.message.MessageSystemHashService;
import com.bhu.vas.business.ds.message.facade.MessageUserFacadeService;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class MessageUnitFacadeService {
	private final Logger logger = LoggerFactory.getLogger(MessageUnitFacadeService.class);
	@Resource
	MessageUserFacadeService messageUserFacadeService;
	public RpcResponseDTO<MessageUserSigDTO> fetch_usersig(Integer uid, Integer channel) {
		try{
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
		if (sig == null){
			logger.info(String.format("fetch_usersig can't fetch user[%s] from redis", user));
			sig = MessageTimHelper.createUserSig(user, expire);
			
			MessageSystemHashService.getInstance().setMessageUserSig(user, utype, sig);
			MessageSystemHashService.getInstance().setMessageUserSigExpire(user, utype, expire);
			//游客不入库,只在腾讯im存储
			if (utype.equals(BusinessKeyDefine.Message.User)){
				MessageUser messageUser = messageUserFacadeService.validate(user);
				if (messageUser == null){
					messageUser = new MessageUser();
				}		
				messageUser.setId(user);
				messageUser.setSig(sig);
				messageUserFacadeService.updateMessageUserData(messageUser);
			}
			
		}
		return sig;
	} 

}
