package com.bhu.vas.api.rpc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bhu.vas.api.helper.ExchangeBBSHelper;
import com.bhu.vas.api.rpc.agent.vto.AgentUserDetailVTO;
import com.bhu.vas.api.rpc.user.dto.UserDTO;
import com.bhu.vas.api.rpc.user.dto.UserDeviceDTO;
import com.bhu.vas.api.rpc.user.dto.UserTokenDTO;
import com.bhu.vas.api.rpc.user.model.User;
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
	
	public static Map<String,Object> builderSimpleUserRpcPayload(int uid, int countrycode, String acc, String nick,int utype,
			   String atoken, String rtoken, boolean isReg){
		return builderUserRpcPayload(uid,countrycode,acc,nick,utype,atoken,rtoken,isReg,null);
	}
	
	public static Map<String,Object> builderUserRpcPayload(int uid, int countrycode, String acc, String nick,int utype,
														   String atoken, String rtoken, boolean isReg,
														   List<UserDeviceDTO> userDeviceDTOList){
		Map<String,Object> ret = new HashMap<String,Object>();
		ret.put(Key_User, new UserDTO(uid,countrycode,acc,nick,utype,isReg));
		ret.put(Key_UserToken, new UserTokenDTO(uid,atoken,rtoken));
		ret.put(Key_UserToken_BBS, ExchangeBBSHelper.bbsPwdGen(acc));
		ret.put(Key_Cm, "60");
		ret.put(Key_Devices, userDeviceDTOList);
		return ret;
	}

	
	public static UserDTO builderUserDTOFromUser(User user,boolean isReg){
		UserDTO ret = new UserDTO(user.getId(), user.getCountrycode(), user.getMobileno(), user.getNick(),user.getUtype(),isReg);
		return ret;
	}
	
	
	public static Map<String,Object> builderUserRpcPayload4Agent(User user,
			   String atoken, String rtoken, boolean isReg){
		Map<String,Object> ret = new HashMap<String,Object>();
		ret.put(Key_User, builderAgentUserDetailVTOFromUser(user,isReg));
		ret.put(Key_UserToken, new UserTokenDTO(user.getId(),atoken,rtoken));
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
	public static String Key_UserToken = "utk";
	public static String Key_UserToken_BBS = "utb";
	public static String Key_Setting = "setting";
	public static String Key_Cm = "cm";
	public static String Key_Devices = "devices";
}
