package com.bhu.vas.api.rpc.message.iservice;

import com.bhu.vas.api.dto.message.MessageUserSigDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;

/**
 * 必虎消息接口定义
 * @author fenghsibo
 *
 */
public interface IMessageUserRpcService {

	RpcResponseDTO<MessageUserSigDTO> fetch_usersig(Integer uid, Integer channel);

	
}
