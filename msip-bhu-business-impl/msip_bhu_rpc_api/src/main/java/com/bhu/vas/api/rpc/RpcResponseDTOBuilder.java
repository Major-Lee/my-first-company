package com.bhu.vas.api.rpc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.helper.ExchangeBBSHelper;
import com.bhu.vas.api.rpc.agent.vto.AgentUserDetailVTO;
import com.bhu.vas.api.rpc.user.dto.UserDTO;
import com.bhu.vas.api.rpc.user.dto.UserDeviceDTO;
import com.bhu.vas.api.rpc.user.dto.UserInnerExchangeDTO;
import com.bhu.vas.api.rpc.user.model.User;
import com.smartwork.msip.business.token.UserTokenDTO;
import com.smartwork.msip.jdo.ResponseErrorCode;


public class RpcResponseDTOBuilder {
	public static <T> RpcResponseDTO<T> builderErrorRpcResponse(ResponseErrorCode errorCode){//,Class<T> classz){
		/*RpcResponseDTO<T> res = new RpcResponseDTO<T>();
		res.setErrorCode(errorCode);
		res.setPayload(null);
		return res;*/
		return builderErrorRpcResponse(errorCode,null);
	}
	
	public static <T> RpcResponseDTO<T> builderErrorRpcResponse(ResponseErrorCode errorCode,String[] errorCodeAttach){//,Class<T> classz){
		return builderErrorRpcResponse(errorCode,errorCodeAttach,null);
	}
	
	public static <T> RpcResponseDTO<T> builderErrorRpcResponse(ResponseErrorCode errorCode,T payload){//,Class<T> classz){
		RpcResponseDTO<T> res = new RpcResponseDTO<T>();
		res.setErrorCode(errorCode);
		res.setPayload(payload);
		return res;
	}
	
	public static <T> RpcResponseDTO<T> builderErrorRpcResponse(ResponseErrorCode errorCode,String[] errorCodeAttach,T payload){//,Class<T> classz){
		RpcResponseDTO<T> res = new RpcResponseDTO<T>();
		res.setErrorCode(errorCode);
		res.setErrorCodeAttach(errorCodeAttach);
		res.setPayload(payload);
		return res;
	}
	
	public static <T> RpcResponseDTO<T> builderSuccessRpcResponse(T payload){
		RpcResponseDTO<T> res = new RpcResponseDTO<T>();
		res.setErrorCode(null);
		res.setPayload(payload);
		return res;
	}
	public static Map<String,Object> builderSimpleUserRpcPayload(User user,UserTokenDTO token, boolean isReg){
		return builderUserRpcPayload(user,token,isReg,null);
	}
	/*public static Map<String,Object> builderSimpleUserRpcPayload(int uid, int countrycode, String acc, String nick,int utype,
			   String atoken, String rtoken, boolean isReg){
		return builderUserRpcPayload(uid,countrycode,acc,nick,utype,atoken,rtoken,isReg,null);
	}*/
	
	/**
	 * 进行了新老uuid的比对，在登录的时候调用
	 * 再新老uuid都不为空的情况下进行比对，如果有一个为空则忽略
	 * @param user
	 * @param token
	 * @param isReg
	 * @param old_uuid
	 * @param new_uuid
	 * @param userDeviceDTOList
	 * @return
	 */
	public static Map<String,Object> builderUserRpcPayload(User user,UserTokenDTO token, boolean isReg,String old_uuid,String new_uuid,List<UserDeviceDTO> userDeviceDTOList){
		Map<String,Object> ret = new HashMap<String,Object>();
		ret.put(Key_User, builderUserDTOFromUser(user,isReg));
		if(token != null)
			ret.put(Key_UserToken,token);
		ret.put(Key_UserToken_BBS, ExchangeBBSHelper.bbsPwdGen(user.getMobileno()));
		ret.put(Key_Cm, "60");
		
		if(StringUtils.isNotEmpty(old_uuid) && StringUtils.isNotEmpty(new_uuid) && !new_uuid.equals(old_uuid)){
			//可能登录客户端变动了
			ret.put(Key_Handset_Changed, ResponseErrorCode.AUTH_UUID_VALID_SELFCURRENT_HANDSET_CHANGED.code());
		}
		if(userDeviceDTOList != null && !userDeviceDTOList.isEmpty())
			ret.put(Key_Devices, userDeviceDTOList);
		return ret;
	}
	
	public static Map<String,Object> builderUserRpcPayload(User user,UserTokenDTO token, boolean isReg,List<UserDeviceDTO> userDeviceDTOList){
		Map<String,Object> ret = new HashMap<String,Object>();
		ret.put(Key_User, builderUserDTOFromUser(user,isReg));
		if(token != null)
			ret.put(Key_UserToken,token);
		ret.put(Key_UserToken_BBS, ExchangeBBSHelper.bbsPwdGen(user.getMobileno()));
		ret.put(Key_Cm, "60");
		if(userDeviceDTOList != null && !userDeviceDTOList.isEmpty())
			ret.put(Key_Devices, userDeviceDTOList);
		return ret;
	}

	public static Map<String,Object> builderUserRpcPayload(UserInnerExchangeDTO exchangeDTO){
		return builderUserRpcPayload(exchangeDTO, null);
		/*Map<String,Object> ret = new HashMap<String,Object>();
		ret.put(Key_User, exchangeDTO.getUser());
		if(exchangeDTO.getToken() != null)
			ret.put(Key_UserToken,exchangeDTO.getToken());
		ret.put(Key_UserToken_BBS, ExchangeBBSHelper.bbsPwdGen(exchangeDTO.getUser().getMobileno()));
		ret.put(Key_Cm, "60");
		if(exchangeDTO.getOauths() != null && !exchangeDTO.getOauths().isEmpty())
			ret.put(Key_UserOAuth, exchangeDTO.getOauths());
		if(userDeviceDTOList != null && !userDeviceDTOList.isEmpty())
			ret.put(Key_Devices, userDeviceDTOList);
		return ret;*/
	}
	public static Map<String,Object> builderUserRpcPayload(UserInnerExchangeDTO exchangeDTO,List<UserDeviceDTO> userDeviceDTOList){
		Map<String,Object> ret = new HashMap<String,Object>();
		ret.put(Key_User, exchangeDTO.getUser());
		if(exchangeDTO.getToken() != null)
			ret.put(Key_UserToken,exchangeDTO.getToken());
		if(exchangeDTO.getWallet() != null)
			ret.put(Key_UserWallet,exchangeDTO.getWallet());
		
		ret.put(Key_UserToken_BBS, ExchangeBBSHelper.bbsPwdGen(exchangeDTO.getUser().getMobileno()));
		ret.put(Key_Cm, "60");
		if(exchangeDTO.getOauths() != null && !exchangeDTO.getOauths().isEmpty())
			ret.put(Key_UserOAuth, exchangeDTO.getOauths());
		if(userDeviceDTOList != null && !userDeviceDTOList.isEmpty())
			ret.put(Key_Devices, userDeviceDTOList);
		return ret;
	}
	
	public static UserDTO builderUserDTOFromUser(User user,boolean isReg){
		UserDTO ret = new UserDTO(user.getId(), user.getCountrycode(), user.getMobileno(), user.getNick(),user.getUtype(),isReg);
		ret.setAvatar(user.getAvatar());
		ret.setSex(user.getSex());
		ret.setOrg(user.getOrg());
		return ret;
	}
	
	
	public static Map<String,Object> builderUserRpcPayload4Agent(User user,
			UserTokenDTO token, boolean isReg){
		Map<String,Object> ret = new HashMap<String,Object>();
		ret.put(Key_User, builderAgentUserDetailVTOFromUser(user,isReg));
		ret.put(Key_UserToken, token);
		ret.put(Key_Cm, "60");
		return ret;
	}
	
	public static AgentUserDetailVTO builderAgentUserDetailVTOFromUser(User user,boolean isReg){
		AgentUserDetailVTO ret = new AgentUserDetailVTO(user.getId(), user.getCountrycode(), user.getMobileno(), user.getNick(),
				user.getOrg(),
				user.getBln(),
				user.getAddr1(),
				user.getAddr2(),
				user.getMemo(),
				user.getUtype(),isReg);
		return ret;
	}
	public static String Key_User = "usr";
	public static String Key_UserOAuth = "uoa";
	public static String Key_UserToken = "utk";
	public static String Key_UserWallet = "uwa";
	public static String Key_UserToken_BBS = "utb";
	public static String Key_Setting = "setting";
	public static String Key_Cm = "cm";
	public static String Key_Devices = "devices";
	public static String Key_Handset_Changed = "hc";
}
