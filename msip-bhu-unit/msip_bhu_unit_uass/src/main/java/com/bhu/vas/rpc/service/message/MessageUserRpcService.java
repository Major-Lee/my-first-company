package com.bhu.vas.rpc.service.message;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.message.dto.MessageUserSigDTO;
import com.bhu.vas.api.rpc.message.dto.TimResponseBasicDTO;
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
	@Override
	public RpcResponseDTO<MessageUserSigDTO> fetch_visitor_usersig(String user, Integer channel) {
		logger.info(String.format("fetch_visitor_usersig user[%s] channel[%s]", user, channel));
		return messageUnitFacadeService.fetch_visitor_usersig(user, channel);
	}
	@Override
	public RpcResponseDTO<TimResponseBasicDTO> send_single_msg(Integer sendChannel, String toAcc, Integer msgType,
			String content) {
		logger.info(String.format("send_single_msg sendChannel[%s] toAcc[%s] msgType[%s] "
				+ "content[%s]", sendChannel.intValue(), toAcc, msgType.intValue(), content)); 
		return messageUnitFacadeService.send_single_msg(sendChannel, toAcc, msgType, content);
	}
	@Override
	public RpcResponseDTO<TimResponseBasicDTO> send_push(Integer sendChannel, String tags, int msgLifeTime,
			Integer msgType, String content) {
		logger.info(String.format("send_push sendChannel[%s] tags[%s] msgLifeTime[%s] msgType[%s] "
				+ "content[%s]", sendChannel.intValue(), tags, msgLifeTime, msgType.intValue(), content)); 
		return messageUnitFacadeService.send_push(sendChannel, tags, msgLifeTime, msgType, content);
	}

}
