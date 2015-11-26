package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.UserType;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.agent.model.AgentBillSummaryView;
import com.bhu.vas.api.rpc.agent.vto.AgentUserDetailVTO;
import com.bhu.vas.api.rpc.user.model.DeviceEnum;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserToken;
import com.bhu.vas.business.bucache.redis.serviceimpl.token.IegalTokenHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.facade.UniqueFacadeService;
import com.bhu.vas.business.ds.agent.facade.AgentBillFacadeService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.ds.user.service.UserTokenService;
import com.bhu.vas.exception.TokenValidateBusinessException;
import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.cores.helper.encrypt.BCryptHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.criteria.PerfectCriteria.Criteria;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class AgentUserUnitFacadeService {
	@Resource
	private UserService userService;
	@Resource
	private UserTokenService userTokenService;

	@Resource
	private AgentBillFacadeService agentBillFacadeService;
	
	public RpcResponseDTO<Boolean> tokenValidate(String uidParam, String token) {
		try{
				int uid = Integer.parseInt(uidParam); 
				boolean validate = IegalTokenHashService.getInstance().validateUserToken(token,uidParam);
				//还需验证此用户是否是代理商用户或者是管理员用户
				if(validate){
					if(RuntimeConfiguration.isConsoleUser(uid))//管理员账户直接通过验证
						return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
					User user = userService.getById(uid);
					if(user == null){
						return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.USER_DATA_NOT_EXIST,Boolean.FALSE);
					}
					if(UserTypeValidateService.validNotNormalUser(user)){
						return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
					}else{
						return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.USER_TYPE_WASNOT_CONSOLEORAGENT,Boolean.FALSE);
					}
					/*if(User.Agent_User != user.getUtype()){
						return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.USER_TYPE_WASNOT_AGENT,Boolean.FALSE);
					}else{
						return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
					}*/
				}else{
					return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.FALSE);
				}
			}catch(NumberFormatException ex){
				ex.printStackTrace(System.out);
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL, Boolean.FALSE);
			}catch(BusinessI18nCodeException bex){
				bex.printStackTrace(System.out);
				return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload(), Boolean.FALSE);
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
			String nick, String sex,
			String org,
			String bln,
			String addr1,
			String addr2,
			String memo,
			String device,String regIp) {
		if(UniqueFacadeService.checkMobilenoExist(countrycode,acc)){//userService.isPermalinkExist(permalink)){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_MOBILENO_DATA_EXIST);
		}
		if (userService.isExistsOrg(org)) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_ORG_DATA_EXIST);
		}
		try{
			User user = new User();
			user.setCountrycode(countrycode);
			user.setMobileno(acc);
			//user.addSafety(SafetyBitMarkHelper.mobileno);
			if(StringUtils.isEmpty(pwd)) 
				user.setPlainpwd(RuntimeConfiguration.Default_Agent_Pwd);
			else
				user.setPlainpwd(pwd);
			user.setNick(nick);
			user.setSex(sex);
			user.setOrg(org);
			user.setBln(bln);
			user.setAddr1(addr1);
			user.setAddr2(addr2);
			user.setMemo(memo);
			user.setRegip(regIp);
			//标记用户注册时使用的设备，缺省为DeviceEnum.Android
			user.setRegdevice(device);
			//标记用户最后登录设备，缺省为DeviceEnum.PC
			user.setLastlogindevice(device);
			user.setUtype(UserType.Agent.getIndex());
			user = this.userService.insert(user);
			{//更新summary相关的容易字段
				try{
					agentBillFacadeService.getAgentBillSummaryViewService().insert(AgentBillSummaryView.buildDefault(user.getId(), org));
				}catch(Exception ex){
					ex.printStackTrace(System.out);
				}
			}
			UniqueFacadeService.uniqueRegister(user.getId(), user.getCountrycode(), user.getMobileno());
			// token validate code
			UserToken uToken = userTokenService.generateUserAccessToken(user.getId().intValue(), true, true);
			{//write header to response header
				//BusinessWebHelper.setCustomizeHeader(response, uToken);
				IegalTokenHashService.getInstance().userTokenRegister(user.getId().intValue(), uToken.getAccess_token());
			}
			//deliverMessageService.sendUserRegisteredActionMessage(user.getId(), null, device,regIp);
			Map<String, Object> rpcPayload = RpcResponseDTOBuilder.builderUserRpcPayload4Agent(user,
					uToken.getAccess_token(), uToken.getRefresh_token(), true);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(rpcPayload);
		}catch(BusinessI18nCodeException bex){
			bex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	public RpcResponseDTO<Map<String, Object>> userLogin(int countrycode, String acc,String pwd,String ut,String device,String remoteIp) {
		try{
			if(ut == null){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.USER_TYPE_PARAM_VALIDATE_EMPTY);
			}
			
			//step 2.生产环境下的手机号验证码验证
			Integer uid = UniqueFacadeService.fetchUidByMobileno(countrycode,acc);
			if(uid == null || uid.intValue() == 0){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.LOGIN_USER_DATA_NOTEXIST);
			}
			User user = this.userService.getById(uid);
			
			UserTypeValidateService.validUserType(user, ut);
			/*if(user == null){//存在不干净的数据，需要清理数据
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.LOGIN_USER_DATA_NOTEXIST);
			}*/
			if(!BCryptHelper.checkpw(pwd,user.getPassword())){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.LOGIN_UNAME_OR_PWD_INVALID);
			}
			//管理账户或者代理商账户才能继续
			/*if(!RuntimeConfiguration.isConsoleUser(uid) && User.Agent_User != user.getUtype()){//管理员账户直接通过验证
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.USER_TYPE_WASNOT_AGENT);
			}*/
			//UserTypeValidateService.validConsoleOrAgentUser(user);
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
			Map<String, Object> rpcPayload = RpcResponseDTOBuilder.builderUserRpcPayload4Agent(user,
					uToken.getAccess_token(), uToken.getRefresh_token(), false);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(rpcPayload);
		}catch(BusinessI18nCodeException bex){
			bex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}

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
		try{
			User user  = userService.getById(uToken.getId());
			if(user == null){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.LOGIN_USER_DATA_NOTEXIST);
			}
			UserTypeValidateService.validNotNormalUser(user);//validConsoleOrAgentUser(user);
			//管理账户或者代理商账户才能继续
			/*if(!RuntimeConfiguration.isConsoleUser(user.getId()) && User.Agent_User != user.getUtype()){//管理员账户直接通过验证
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.USER_TYPE_WASNOT_AGENT);
			}*/
			if(StringUtils.isEmpty(user.getRegip())){
				user.setRegip(remoteIp);
			}
			if(!user.getLastlogindevice().equals(device)){
				user.setLastlogindevice(DeviceEnum.getBySName(device).getSname());
			}
			this.userService.update(user);
			Map<String, Object> rpcPayload = RpcResponseDTOBuilder.builderUserRpcPayload4Agent(user,
					uToken.getAccess_token(), uToken.getRefresh_token(), false);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(rpcPayload);
		}catch(BusinessI18nCodeException bex){
			bex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
			//return new RpcResponseDTO<TaskResDTO>(bex.getErrorCode(),null);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
			//return new RpcResponseDTO<TaskResDTO>(ResponseErrorCode.COMMON_BUSINESS_ERROR,null);
		}
		
	}
	
	
	public RpcResponseDTO<TailPage<AgentUserDetailVTO>> pageAgentUsers(int uid,String keywords,int pageno,int pagesize){
		//管理账户才能继续
		/*if(!RuntimeConfiguration.isConsoleUser(uid)){//管理员账户直接通过验证
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.USER_TYPE_WASNOT_AGENT);
		}*/
		try{
			//UserTypeValidateService.validConsoleUser(uid);
			User user  = userService.getById(uid);
			UserTypeValidateService.validNotNormalUser(user);
			ModelCriteria mc = new ModelCriteria();
			Criteria cri = mc.createCriteria();
			cri.andColumnEqualTo("utype", UserType.Agent.getIndex());

			if(keywords!=null && StringUtils.isNotEmpty(keywords.trim())){
				cri.andColumnLike("org", "%"+keywords+"%");
			}
			mc.setOrderByClause(" id desc ");
			mc.setPageNumber(pageno);
			mc.setPageSize(pagesize);
			TailPage<User> tailusers = this.userService.findModelTailPageByModelCriteria(mc);
			List<AgentUserDetailVTO> vtos = new ArrayList<>();
			for(User _user:tailusers.getItems()){
				vtos.add(RpcResponseDTOBuilder.builderAgentUserDetailVTOFromUser(_user, false));
			}
			TailPage<AgentUserDetailVTO> pages = new CommonPage<AgentUserDetailVTO>(tailusers.getPageNumber(), pagesize, tailusers.getTotalItemsCount(), vtos);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(pages);
		}catch(BusinessI18nCodeException bex){
			bex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
			//return new RpcResponseDTO<TaskResDTO>(bex.getErrorCode(),null);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
			//return new RpcResponseDTO<TaskResDTO>(ResponseErrorCode.COMMON_BUSINESS_ERROR,null);
		}

	}
	
	public RpcResponseDTO<AgentUserDetailVTO> userDetail(int uid,int tid) {
		try{
			User user  = userService.getById(tid);
			/*if(user == null){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.USER_DATA_NOT_EXIST);
			}*/
			UserTypeValidateService.validNotNormalUser(user);//.validConsoleOrAgentUser(user);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(RpcResponseDTOBuilder.builderAgentUserDetailVTOFromUser(user, false));
		}catch(BusinessI18nCodeException bex){
			bex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<AgentUserDetailVTO> userModify(int uid,int tid, String nick,
			String org,String bln, String addr1, String addr2, String memo) {
		try{
			User user  = userService.getById(tid);
			if(user == null){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.USER_DATA_NOT_EXIST);
			}
			//UserTypeValidateService.validConsoleOrAgentUser(user);
			UserTypeValidateService.validNotNormalUser(user);
			if(StringUtils.isNotEmpty(nick)){
				user.setNick(nick);
			}
			boolean org_changed = false;
			if(StringUtils.isNotEmpty(org)){
				String old_org = user.getOrg();
				if(!org.equals(old_org)){
					if (userService.isExistsOrg(org)) {
						return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.AUTH_ORG_DATA_EXIST);
					}
					org_changed = true;
					user.setOrg(org);
				}
			}
			if(StringUtils.isNotEmpty(bln)){
				user.setBln(bln);
			}
			if(StringUtils.isNotEmpty(addr1)){
				user.setAddr1(addr1);
			}
			if(StringUtils.isNotEmpty(addr2)){
				user.setAddr2(addr2);
			}
			if(StringUtils.isNotEmpty(memo)){
				user.setMemo(memo);
			}
			this.userService.update(user);
			try{
				if(org_changed){//更新summary相关的容易字段
					AgentBillSummaryView sview = agentBillFacadeService.getAgentBillSummaryViewService().getById(user.getId());
					if(sview != null){
						sview.setOrg(org);
						agentBillFacadeService.getAgentBillSummaryViewService().update(sview);
					}
				}
			}catch(Exception ex){
				ex.printStackTrace(System.out);
			}
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(RpcResponseDTOBuilder.builderAgentUserDetailVTOFromUser(user, false));
		}catch(BusinessI18nCodeException bex){
			bex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
}
