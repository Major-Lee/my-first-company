package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.user.dto.UserDTO;
import com.bhu.vas.api.rpc.user.model.DeviceEnum;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserToken;
import com.bhu.vas.business.bucache.redis.serviceimpl.token.IegalTokenHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.facade.UniqueFacadeService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.ds.user.service.UserTokenService;
import com.bhu.vas.exception.TokenValidateBusinessException;
import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.cores.helper.encrypt.BCryptHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class AgentUserUnitFacadeService {
	@Resource
	private UserService userService;
	@Resource
	private UserTokenService userTokenService;

	public RpcResponseDTO<Boolean> tokenValidate(String uidParam, String token) {
		try{
				int uid = Integer.parseInt(uidParam); 
				boolean validate = IegalTokenHashService.getInstance().validateUserToken(token,uidParam);
				//还需验证此用户是否是代理商用户或者是管理员用户
				if(validate){
					if(RuntimeConfiguration.isConsoleUser(uid))
						return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
					User user = userService.getById(uid);
					if(user == null){
						return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.USER_DATA_NOT_EXIST,Boolean.FALSE);
					}else{
						if(User.Agent_User != user.getUtype()){
							return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.USER_TYPE_WASNOT_AGENT,Boolean.FALSE);
						}else{
							return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
						}
					}
				}else{
					return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.FALSE);
				}
			}catch(NumberFormatException ex){
				ex.printStackTrace(System.out);
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL, Boolean.FALSE);
			}catch(Exception ex){
				ex.printStackTrace(System.out);
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL, Boolean.FALSE);
			}
		}
	
	/**
	 * 检查手机号是否注册过
	 * @param countrycode
	 * @param acc
	 * @return 
	 * 		true 系统不存在此手机号  
	 * 		false系统存在此手机号 ，并带有错误码
	 */
	public RpcResponseDTO<Boolean> checkAcc(int countrycode, String acc){
		if(UniqueFacadeService.checkMobilenoExist(countrycode,acc)){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_MOBILENO_DATA_EXIST,Boolean.FALSE);
		}else{
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		}
	}
			
	public RpcResponseDTO<Map<String, Object>> createNewUser(int countrycode, String acc,String pwd,
			String nick, String sex, String device,String regIp) {
		if(UniqueFacadeService.checkMobilenoExist(countrycode,acc)){//userService.isPermalinkExist(permalink)){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_MOBILENO_DATA_EXIST);
			//return ResponseError.embed(ResponseErrorCode.AUTH_MOBILENO_DATA_EXIST);
		}
		
		User user = new User();
		user.setCountrycode(countrycode);
		user.setMobileno(acc);
		//user.addSafety(SafetyBitMarkHelper.mobileno);
		user.setPlainpwd(pwd);
		user.setNick(nick);
		user.setSex(sex);
		user.setRegip(regIp);
		//标记用户注册时使用的设备，缺省为DeviceEnum.Android
		user.setRegdevice(device);
		//标记用户最后登录设备，缺省为DeviceEnum.PC
		user.setLastlogindevice(device);
		user.setUtype(User.Agent_User);
		user = this.userService.insert(user);
		UniqueFacadeService.uniqueRegister(user.getId(), user.getCountrycode(), user.getMobileno());
		// token validate code
		UserToken uToken = userTokenService.generateUserAccessToken(user.getId().intValue(), true, true);
		{//write header to response header
			//BusinessWebHelper.setCustomizeHeader(response, uToken);
			IegalTokenHashService.getInstance().userTokenRegister(user.getId().intValue(), uToken.getAccess_token());
		}
		//deliverMessageService.sendUserRegisteredActionMessage(user.getId(), null, device,regIp);
		Map<String, Object> rpcPayload = RpcResponseDTOBuilder.builderUserRpcPayload4Agent(
				user.getId(), user.getCountrycode(), user.getMobileno(), user.getNick(), user.getUtype(),
				uToken.getAccess_token(), uToken.getRefresh_token(), true);
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(rpcPayload);
	}
	
	public RpcResponseDTO<Map<String, Object>> userLogin(int countrycode, String acc,String pwd,String device,String remoteIp) {
		
		//step 2.生产环境下的手机号验证码验证
		Integer uid = UniqueFacadeService.fetchUidByMobileno(countrycode,acc);
		if(uid == null || uid.intValue() == 0){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.LOGIN_USER_DATA_NOTEXIST);
		}
		User user = this.userService.getById(uid);
		if(user == null){//存在不干净的数据，需要清理数据
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.LOGIN_USER_DATA_NOTEXIST);
		}
		if(!BCryptHelper.checkpw(pwd,user.getPassword())){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.LOGIN_UNAME_OR_PWD_INVALID);
		}
		
		if(StringUtils.isEmpty(user.getRegip())){
			user.setRegip(remoteIp);
		}
		if(!user.getLastlogindevice().equals(device)){
			user.setLastlogindevice(DeviceEnum.getBySName(device).getSname());
		}
		this.userService.update(user);
		
		UserToken uToken = userTokenService.generateUserAccessToken(user.getId().intValue(), true, false);
		{//write header to response header
			//BusinessWebHelper.setCustomizeHeader(response, uToken);
			IegalTokenHashService.getInstance().userTokenRegister(user.getId().intValue(), uToken.getAccess_token());
		}
		//deliverMessageService.sendUserSignedonActionMessage(user.getId(), remoteIp,device);
		Map<String, Object> rpcPayload = RpcResponseDTOBuilder.builderUserRpcPayload4Agent(
				user.getId(), user.getCountrycode(), user.getMobileno(), user.getNick(), user.getUtype(),
				uToken.getAccess_token(), uToken.getRefresh_token(), false);
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(rpcPayload);
	}
	
	public RpcResponseDTO<Map<String, Object>> userValidate(String aToken,String device,String remoteIp) {
		UserToken uToken = null;
		try{
			uToken = userTokenService.validateUserAccessToken(aToken);
			System.out.println("~~~~~step4 id:"+uToken.getId()+" token:"+uToken.getAccess_token());
			//write header to response header
			//BusinessWebHelper.setCustomizeHeader(response, uToken);
			IegalTokenHashService.getInstance().userTokenRegister(uToken.getId().intValue(), uToken.getAccess_token());
		}catch(TokenValidateBusinessException ex){
			int validateCode = ex.getValidateCode();
			System.out.println("~~~~step5 failure~~~~~~token validatecode:"+validateCode);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_TOKEN_INVALID);
		}
		
		User user  = userService.getById(uToken.getId());
		if(user == null){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.LOGIN_USER_DATA_NOTEXIST);
		}
		
		if(StringUtils.isEmpty(user.getRegip())){
			user.setRegip(remoteIp);
		}
		if(!user.getLastlogindevice().equals(device)){
			user.setLastlogindevice(DeviceEnum.getBySName(device).getSname());
		}
		this.userService.update(user);
		Map<String, Object> rpcPayload = RpcResponseDTOBuilder.builderUserRpcPayload4Agent(
				user.getId(), user.getCountrycode(), user.getMobileno(), user.getNick(), user.getUtype(),
				uToken.getAccess_token(), uToken.getRefresh_token(), false);
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(rpcPayload);
	}
	
	
	public RpcResponseDTO<TailPage<UserDTO>> pageAgentUsers(int pageno,int pagesize){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("utype", User.Agent_User);
		mc.setOrderByClause("id");
		mc.setPageNumber(pageno);
		mc.setPageSize(pagesize);
		TailPage<User> tailusers = this.userService.findModelTailPageByModelCriteria(mc);
		List<UserDTO> vtos = new ArrayList<>();
		for(User user:tailusers.getItems()){
			vtos.add(RpcResponseDTOBuilder.builderUserDTOFromUser(user, false));
		}
		TailPage<UserDTO> pages = new CommonPage<UserDTO>(tailusers.getPageNumber(), pagesize, tailusers.getTotalItemsCount(), vtos);
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(pages);
	}
}
