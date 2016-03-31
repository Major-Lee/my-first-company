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
	 * 生成一个新的帐号
	 * @param uid 如果uid！=null && uid >0 则为关联帐号操作，否则为新建
	 * @param identify
	 * @param auid
	 * @param nick
	 * @param avatar
	 * @return
	 */
	public RpcResponseDTO<Map<String, Object>> createIdentifies(Integer uid,String identify,String auid,String openid,String nick,String avatar,String device,String regIp,String deviceuuid, String ut);

	
	/**
	 * 通过auid填充openid接口
	 * @param identify
	 * @param auid
	 * @param openid
	 * @return
	 */
	public RpcResponseDTO<Boolean> fullfillOpenid(String identify,String auid,String openid);
}
