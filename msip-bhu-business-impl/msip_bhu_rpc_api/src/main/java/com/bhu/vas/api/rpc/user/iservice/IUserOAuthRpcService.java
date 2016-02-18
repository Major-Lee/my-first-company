package com.bhu.vas.api.rpc.user.iservice;

import java.util.List;
import java.util.Map;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.user.dto.UserOAuthStateDTO;

public interface IUserOAuthRpcService {
	/**
	 * 通过用户id获取其绑定或注册的第三方类型和帐号
	 * @param uid
	 * @return
	 */
	public RpcResponseDTO<List<UserOAuthStateDTO>> fetchRegisterIdentifies(Integer uid);
	
	/**
	 * 移除相关第三方的帐号绑定
	 * @param uid
	 * @param identify
	 * @return
	 */
	public RpcResponseDTO<Boolean> removeIdentifies(Integer uid,String identify);
	
	/**
	 * 创建或更新第三方帐号关联信息
	 * @param uid
	 * @param identify
	 * @param auid
	 * @param nick
	 * @param avatar
	 * @return
	 */
	public RpcResponseDTO<Map<String, Object>> createIdentifies(String identify,String auid,String nick,String avatar,String device,String regIp,String deviceuuid, String ut);
}
