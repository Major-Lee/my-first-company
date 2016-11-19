package com.bhu.vas.rpc.facade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.message.dto.MessageUserSigDTO;
import com.bhu.vas.api.rpc.message.helper.MessageTimHelper;
import com.bhu.vas.business.bucache.redis.serviceimpl.message.MessageSystemHashService;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class MessageUnitFacadeService {
	private final Logger logger = LoggerFactory.getLogger(MessageUnitFacadeService.class);

	public RpcResponseDTO<MessageUserSigDTO> fetch_usersig(Integer uid, Integer channel) {
		try{
			MessageUserSigDTO dto = new MessageUserSigDTO();
			String sig = MessageSystemHashService.getInstance().fetchMessageUserSig(String.valueOf(uid));
			if (sig == null){
				logger.info(String.format("fetch_usersig can't fetch user[%s] from redis", uid));
				sig = MessageTimHelper.createUserSig(uid+"", MessageTimHelper.defaultExpire);
				MessageSystemHashService.getInstance().setMessageUserSig(uid+"", sig);
			}else{
				sig = MessageSystemHashService.getInstance().fetchMessageUserSig(uid+"");
			}
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

}
