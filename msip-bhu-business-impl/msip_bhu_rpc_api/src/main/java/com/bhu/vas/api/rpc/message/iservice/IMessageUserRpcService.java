package com.bhu.vas.api.rpc.message.iservice;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.message.dto.MessageUserSigDTO;
import com.bhu.vas.api.rpc.message.dto.TimResponseBasicDTO;

/**
 * 必虎消息接口定义
 * @author fenghsibo
 *
 */
public interface IMessageUserRpcService {

	RpcResponseDTO<MessageUserSigDTO> fetch_usersig(Integer uid, Integer channel);

	RpcResponseDTO<MessageUserSigDTO> fetch_visitor_usersig(String user, Integer channel);

	RpcResponseDTO<TimResponseBasicDTO> send_single_msg(Integer sendChannel, String toAcc, Integer msgType,
			String content);

	RpcResponseDTO<TimResponseBasicDTO> send_push(Integer sendChannel, String tags, int msgLifeTime, Integer msgType,
			String content);

	
}
