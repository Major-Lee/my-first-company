package com.bhu.vas.api.rpc.message.iservice;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.message.dto.MessageUserSigDTO;

/**
 * 必虎消息接口定义
 * @author fenghsibo
 *
 */
public interface IMessageUserRpcService {

	RpcResponseDTO<MessageUserSigDTO> fetch_usersig(Integer uid, Integer channel);

	
}
