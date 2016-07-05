package com.bhu.vas.api.rpc.portrait.iservice;

import java.util.Map;

import com.bhu.vas.api.rpc.RpcResponseDTO;

/**
 * 画像RPC Service Interface
 * @author Edmond Lee
 *
 */
public interface IPortraitRpcService {
	/**
	 * 创建 hportrait
	 * @param hmac
	 * @param mobileno
	 * @param useragent
	 * @return
	 */
	public RpcResponseDTO<Map<String, Object>> create(
			String hmac,
			String mobileno,
			String useragent
			);
}
