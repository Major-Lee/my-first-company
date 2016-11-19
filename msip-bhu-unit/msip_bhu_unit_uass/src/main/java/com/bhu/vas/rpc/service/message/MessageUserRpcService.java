package com.bhu.vas.rpc.service.message;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.message.dto.MessageUserSigDTO;
import com.bhu.vas.api.rpc.message.iservice.IMessageUserRpcService;
import com.bhu.vas.rpc.facade.MessageUnitFacadeService;

@Service("messageUserRpcService")
public class MessageUserRpcService implements IMessageUserRpcService{
	private final Logger logger = LoggerFactory.getLogger(MessageUserRpcService.class);
	@Resource
	private MessageUnitFacadeService messageUnitFacadeService;
	@Override
	public RpcResponseDTO<MessageUserSigDTO> fetch_usersig(Integer uid, Integer channel) {
		logger.info(String.format("fetch_usersig uid[%s] channel[%s]", uid, channel));
		return messageUnitFacadeService.fetch_usersig(uid, channel);
	}

}
