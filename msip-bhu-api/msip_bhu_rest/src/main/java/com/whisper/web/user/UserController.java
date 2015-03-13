package com.whisper.web.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.helper.json.DateConvertType;
import com.smartwork.msip.cores.helper.phone.PhoneHelper;
import com.smartwork.msip.jdo.Response;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;
import com.whisper.api.user.dto.AvatarDTO;
import com.whisper.api.user.model.DeviceEnum;
import com.whisper.api.user.model.User;
import com.whisper.api.user.model.UserAddressbookState;
import com.whisper.api.user.model.UserAvatarState;
import com.whisper.api.user.model.UserToken;
import com.whisper.business.asyn.web.builder.DeliverMessageType;
import com.whisper.business.asyn.web.model.IDTO;
import com.whisper.business.asyn.web.service.DeliverMessageService;
import com.whisper.business.bucache.redis.serviceimpl.present.token.IegalTokenHashService;
import com.whisper.business.bucache.redis.serviceimpl.unique.facade.UniqueFacadeService;
import com.whisper.business.captcha.service.CaptchaCodeService;
import com.whisper.business.helper.BusinessWebHelper;
import com.whisper.business.service.UserLoginDataService;
import com.whisper.business.ucenter.service.UserAddressbookStateService;
import com.whisper.business.ucenter.service.UserAvatarStateService;
import com.whisper.business.ucenter.service.UserTokenService;
import com.whisper.helper.SafetyBitMarkHelper;
import com.whisper.helper.UserSymbolHelper;
import com.whisper.msip.cores.web.mvc.WebHelper;
import com.whisper.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.whisper.msip.web.AsynMessageController;
import com.whisper.validate.ValidateService;

@Controller
@RequestMapping("/account")
public class UserController extends AsynMessageController{
	@Resource
	private UserAvatarStateService userAvatarStateService;
	
	@Resource
	private DeliverMessageService deliverMessageService;
	
	@Resource
	private UserLoginDataService userLoginDataService;
	
	@Resource
	private UserTokenService userTokenService;
	
	@Resource
	private UserAddressbookStateService userAddressbookStateService;
	
	@Resource
	private CaptchaCodeService captchaCodeService;
	
	static String[] userCommonRetFields = {"id","nick","email","mobileno","sex","locked","avatar","safety_mark","memo","birthday"};//,"email","created_at","updated_at"};
	
	/**
	 * 用户账号创建
	 * 实现方式：deviceuuid 通过此方式 直接缺省新注册个帐号，不考虑以前是否此设备曾经登录使用过
	 * 1、支持用户快速直接注册
	 * 2、支持输入accemail或者mobileno 和密码注册
	 * 3、支持注册是填写 sex，lang，region
	 * @param request
	 * @param response
	 * @param deviceuuid 设备uuid
	 * @param acc 登录帐号指email或者mobileno
	 * @param nick 昵称
	 * @param pwd 密码 在acc不为空的情况下 pwd必须不为空
	 * @param lang 语言
	 * @param region 区域
	 * @param device 设备类型
	 * @param itoken 注册邀请码
	 * @param token  渠道邀请码
	 * 
	 */
	@ResponseBody()
	@RequestMapping(value="/create",method={RequestMethod.GET,RequestMethod.POST})
	public void create(HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = false, value="du") String deviceuuid,
			@RequestParam(required = false,value="cc",defaultValue="86") int countrycode,
			@RequestParam(required = true) String acc,
			@RequestParam(required = true) String captcha,
			//@RequestParam(required = false) String nick,
			@RequestParam(required = false,defaultValue="男") String sex,
			@RequestParam(required = false,defaultValue="") String lang,
			@RequestParam(required = false,defaultValue="") String region,
			@RequestParam(required = false, value="d",defaultValue="R") String device,
			@RequestParam(required = false, value="c",defaultValue="appstore") String channel
			//@RequestParam(required = false, value="i") String itoken,
			//@RequestParam(required = false) String token
			) {
		//step 1.deviceuuid 验证
		ResponseError validateError = null;
		if(StringUtils.isNotEmpty(deviceuuid)){
			validateError = ValidateService.validateDeviceUUID(deviceuuid);//, userService);
			if(validateError != null){
				SpringMVCHelper.renderJson(response, validateError);
				return;
			}
		}
		/*validateError = ValidateService.validateMobilenoRegx(countrycode, acc);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}*/
		
		//Integer inviteuid =null;
		//if(StringUtils.isNotEmpty(itoken))
		//	inviteuid = this.validateUserInviteToken(itoken);
		String remoteIp = WebHelper.getRemoteAddr(request);
		String from_device = DeviceEnum.getBySName(device).getSname();
		User user = null;
		UserToken uToken = null;
		try{
			//step 2.手机号正则验证及手机是否存在验证
			validateError = ValidateService.validateMobileno(countrycode,acc);
			if(validateError != null){
				SpringMVCHelper.renderJson(response, validateError);
				return;
			}
			//step 3.生产环境下的手机号验证码验证
			if(!RuntimeConfiguration.SecretInnerTest){
				String accWithCountryCode = PhoneHelper.format(countrycode, acc);
				if(!RuntimeConfiguration.isSystemNoneedCaptchaValidAcc(accWithCountryCode)){
					ResponseErrorCode errorCode = captchaCodeService.validCaptchaCode(accWithCountryCode, captcha);
					if(errorCode != null){
						SpringMVCHelper.renderJson(response, ResponseError.embed(errorCode));
						return;
					}
				}
			}/*else{
				String accWithCountryCode = PhoneHelper.format(countrycode, acc);
				if("86 18612272825".equals(accWithCountryCode) || "86 13810048517".equals(accWithCountryCode)){
					ResponseErrorCode errorCode = captchaCodeService.validCaptchaCode(accWithCountryCode, captcha);
					if(errorCode != null){
						SpringMVCHelper.renderJson(response, ResponseError.embed(errorCode));
						return;
					}
				}
			}*/
			
			user = new User();
			user.setCountrycode(countrycode);
			user.setMobileno(acc);
			user.addSafety(SafetyBitMarkHelper.mobileno);
			user.setPlainpwd(RuntimeConfiguration.Default_Whisper_Pwd);
			
			//user.setNick(nick);
			user.setSex(sex);
			user.setLastlogindevice_uuid(deviceuuid);
			user.setRegion(region);
			user.setLang(lang);
			//user.setInviteuid(inviteuid);
			user.setChannel(channel);
			user.setRegip(remoteIp);
			//标记用户注册时使用的设备，缺省为DeviceEnum.Android
			user.setRegdevice(from_device);
			//标记用户最后登录设备，缺省为DeviceEnum.PC
			user.setLastlogindevice(from_device);
			Date now = new Date();
			user.setLastlogin_at(now);
			user.setToday_firstlogin_at(now);
			user = this.userService.insert(user);
			UniqueFacadeService.uniqueRegister(user.getId(), user.getCountrycode(), user.getMobileno());
			// token validate code
			uToken = userTokenService.generateUserAccessToken(user.getId().intValue(), true, true);
			{//write header to response header
				BusinessWebHelper.setCustomizeHeader(response, uToken);
				IegalTokenHashService.getInstance().userTokenRegister(user.getId().intValue(), uToken.getAccess_token());
			}
			deliverMessageService.sendUserRegisteredActionMessage(DeliverMessageType.AC.getPrefix(), user.getId(), channel, from_device,remoteIp);//sendUserSignedonActionMessage(DeliverMessageType.AC.getPrefix(), user.getId(), remoteIp, from_device);
			//TODO: 新增用户数据, 需要增量索引
			Map<String,Object> map = userLoginDataService.buildLoginData(user);
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(map));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}finally{
			
		}
	}
	
	
	/**
	 * 用户信息修改
	 * @param request
	 * @param response
	 * @param uid 用户uid
	 * @param nickname 昵称
	 * @param birthday 生日
	 * @param sex 性别
	 * @param memo 信息描述
	 * @param lang 语言
	 * @param region 地域
	 * @param isreg 是否是注册步骤
	 */
	@ResponseBody()
	@RequestMapping(value="/update_profile",method={RequestMethod.GET,RequestMethod.POST})
	public void updateprofile(HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = false) String nick,
			@RequestParam(required = false,value="bday") String birthday,
			@RequestParam(required = false) String sex,
			@RequestParam(required = false,defaultValue="") String lang,
			@RequestParam(required = false,defaultValue="") String region,
			@RequestParam(required = false) String memo,
			@RequestParam(required = false,defaultValue="false") boolean isreg//是否是完善注册
			) {
		try{
			if(StringUtils.isEmpty(nick) && StringUtils.isEmpty(birthday) && StringUtils.isEmpty(sex) 
					&& StringUtils.isEmpty(memo)){
				SpringMVCHelper.renderJson(response, Response.SUCCESS);
				return;
			}
			User user = this.userService.getById(uid);
			validateUserNotNull(user);
			//TODO:nick格式和字符正则验证
			//是否需要更新用户索引
			boolean nick_updated = false;
			String old_nick = user.getNick();
			if(StringUtils.isNotEmpty(nick)){
				ResponseError validateError = ValidateService.validateNick(nick);
				if(validateError != null){
					SpringMVCHelper.renderJson(response, validateError);
					return;
				}
				/*int charlen = nick.length();
				if(charlen < 2 || charlen > 14){
					SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.AUTH_NICKNAME_INVALID_LENGTH,new String[]{"2","14"}));//renderHtml(response, html, headers)
					return;
				}*/
				//如果修改的昵称和之前不同, 则需要更新用户索引
				if(!nick.equals(old_nick)){
					nick_updated = true;
					if(StringUtils.isEmpty(old_nick)){
						String remoteIp = WebHelper.getRemoteAddr(request);
						user.setRegip(remoteIp);
					}
				}
				user.setValidated(true);
				user.setNick(nick);
			}
			if(StringUtils.isNotEmpty(birthday)) user.setBirthday(birthday);
			if(StringUtils.isNotEmpty(sex)) user.setSex(sex);
			if(StringUtils.isNotEmpty(region)) user.setRegion(region);
			if(StringUtils.isNotEmpty(lang)) user.setLang(lang);
			if(StringUtils.isNotEmpty(memo)) user.setMemo(memo);
			user = this.userService.update(user);
			
			if(nick_updated)
				deliverMessageService.sendUserNickUpdActionMessage(DeliverMessageType.AC.getPrefix(), uid, nick, old_nick);
			
			Map<String,Object> map = JsonHelper.filterObjectData(user,DateConvertType.TOSTRING_EN_COMMON_YMDHMS, false, userCommonRetFields);
			map.put("symbol", UserSymbolHelper.generate(user));
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(map));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	
	/**
	 * 提取用户头像信息
	 * @param request
	 * @param response
	 * @param uid
	 */
	@ResponseBody()
	@RequestMapping(value="/fetch_avatar",method={RequestMethod.GET,RequestMethod.POST})
	public void fetchavatar(HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = true) Integer uid) {
		User user = this.userService.getById(uid);
		validateUserNotNull(user);
		try{
			UserAvatarState state = this.userAvatarStateService.getOrCreateById(uid);//.getById(id);
			if(state == null){
				SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_NOTEXIST));
				return;
			}
			List<AvatarDTO> result = new ArrayList<AvatarDTO>();
			Set<String> set = state.keySet();
			for(String key:set){
				String value = state.getTagValue(key);
				result.add(new AvatarDTO(key,value,value.equals(user.getAvatar())));
				continue;
			}
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("id", state.getId());
			map.put("items", result);
			//Map<String,Object> map = JsonHelper.filterObjectData(state,DateConvertType.TOSTRING_EN_COMMON_YMDHMS, false, "id","localAvatorTag","grAvatorTag","grAvatorUrl","qqAvatorTag","renRenTag","sinaAvatorTag","updated_at");
	    	SpringMVCHelper.renderJson(response, ResponseSuccess.embed(map));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}

	/**
	 * 提取用户个人信息
	 * @param request
	 * @param response
	 * @param uid
	 */
	@ResponseBody()
	@RequestMapping(value="/fetch_profile",method={RequestMethod.GET,RequestMethod.POST})
	public void fetchprofile(HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = false) Integer ouid) {
		User user = this.userService.getById(uid);
		validateUserNotNull(user);
		User ouser = null;
		if(ouid != null){
			ouser = this.userService.getById(ouid);
			validateUserNotNull(ouser);
		}
		try{
			/*User user = this.userService.getById(uid);
			if(user == null){
				SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_NOTEXIST));
				return;
			}*/
			Map<String,Object> map = JsonHelper.filterObjectData(ouser==null?user:ouser,DateConvertType.TOSTRING_EN_COMMON_YMDHMS, false, userCommonRetFields);
	    	SpringMVCHelper.renderJson(response, ResponseSuccess.embed(map));
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	
	/**
	 * 切换头像
	 * @param request
	 * @param response
	 * @param uid
	 * @param type
	 */
	@ResponseBody()
	@RequestMapping(value="/change_avatar",method={RequestMethod.GET,RequestMethod.POST})
	public void changeavatar(HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String type) {
		try{
			User user = this.userService.getById(uid);
			validateUserNotNull(user);
			UserAvatarState state = this.userAvatarStateService.getOrCreateById(uid);//.getById(id);
			if(state == null){
				SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_NOTEXIST));
				return;
			}
			String value = state.getTagValue(type);
			
			if(StringUtils.isNotEmpty(value)){
				user.setAvatar(value);
				userService.update(user);
				deliverMessageService.sendUserAvatarUpdActionMessage(DeliverMessageType.AC.getPrefix(), user.getId(), value, IDTO.ACT_UPDATE);
				/*//如果是人人网的头像,则传回人人网正方形的tiny图
				if(type.equals(SnsType.RENREN.getShortname())){
					SpringMVCHelper.renderJson(response, ResponseSuccess.embed(state.getRenRenTinyTag()));
					return;
				}*/
			}
	    	SpringMVCHelper.renderJson(response, Response.SUCCESS);
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	
	/**
	 * 更新用户通信录
	 * 如果是全量导入用户通信录, 已经存在通信录, 会删除。再重新导入
	 * 如果是增量, 只更新变化部分
	 * 1)增量数据格式
	 * {"del":[{"fn":"lawliet","ln":"tang","mno":"13832"}],"upd":[{"fn":"lawliet","ln":"tang","mno":"13832"}],"add":[{"fn":"lawliet","ln":"tang","mno":"13832"}]}
	 * 
	 * 2)全量数据格式
	 * [{"fn":"lawliet","ln":"tang","mno":"13832"},{"fn":"zhou","ln":"enlai","mno":"14444"}]
	 * @param request
	 * @param response
	 * @param uid
	 * @param json
	 * @param increment 是否为增量更新
	 */
	@ResponseBody()
	@RequestMapping(value="/post_addressbook",method={RequestMethod.GET,RequestMethod.POST})
	public void post_addressbook(HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = false) String json,
			@RequestParam(required = true, value="inct",defaultValue="true") boolean increment) {
		try{
			//json = StringHelper.filterOffUtf8Mb4(json);
			//System.out.println("address json by filter: " + json);
			if(StringHelper.isNotEmpty(json)){
				//增量更新
/*				if(increment){
					Map<String, Set<UserAddressbookDTO>> paramMapDtos = JsonHelper.getDTOMapKeySet(json, UserAddressbookDTO.class);
					if(paramMapDtos == null || paramMapDtos.isEmpty()){
						SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL));
						return;
					}
					UserAddressbookState entity = userAddressbookStateService.getById(uid);
					if(entity == null){
						SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_NOTEXIST));
						return;
					}
					
					boolean needUpdated = false;
					//数据增加部分
					Set<UserAddressbookDTO> addedDtos = paramMapDtos.get(UserAddressbookDTO.Increment_Added);
					if(addedDtos != null && !addedDtos.isEmpty()){
						this.filterInvaildAddressDtos(addedDtos);
						if(!addedDtos.isEmpty()){
							entity.putInnerModels(addedDtos);
							needUpdated = true;
						}
					}
					//数据修改部分
					Set<UserAddressbookDTO> updatedDtos = paramMapDtos.get(UserAddressbookDTO.Increment_Updated);
					if(updatedDtos != null && !updatedDtos.isEmpty()){
						this.filterInvaildAddressDtos(updatedDtos);
						if(!updatedDtos.isEmpty()){
							for(UserAddressbookDTO updatedDto : updatedDtos){
								entity.removeInnerModel(updatedDto);
								entity.putInnerModel(updatedDto);
							}
							needUpdated = true;
						}
					}
					//数据删除部分
					Set<UserAddressbookDTO> deletedDtos = paramMapDtos.get(UserAddressbookDTO.Increment_Deleted);
					if(deletedDtos != null && !deletedDtos.isEmpty()){
						this.filterInvaildAddressDtos(deletedDtos);
						if(!deletedDtos.isEmpty()){
							for(UserAddressbookDTO deletedDto : deletedDtos){
								entity.removeInnerModel(deletedDto);
							}
							needUpdated = true;
						}
					}
					
					if(needUpdated){
						entity.setSync_at(new Date());
						userAddressbookStateService.update(entity);
						
						deliverMessageService.sendUserAddressbookUpdActionMessage(DeliverMessageType.AC.getPrefix(), uid);
					}
				}*/
				//全量更新
				//else{
					/*Set<UserAddressbookDTO> paramDtos = JsonHelper.getDTOSet(json, UserAddressbookDTO.class);
					if(paramDtos == null || paramDtos.isEmpty()){
						SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL));
						return;
					}*/
					
					//this.filterInvaildAddressDtos(paramDtos);
					if(JsonHelper.EmptyArrayJsonString.equals(json)){
						SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL));
						return;
					}
					
					
					UserAddressbookState entity = userAddressbookStateService.getOrCreateById(uid);
					//直接清除原有数据
					entity.getInnerModels().clear();
					//全量导入通信录数据
					//entity.putInnerModels(paramDtos);
					entity.setInnerModelJsons(json);
					entity.setSync_at(new Date());
					userAddressbookStateService.update(entity);
					
					deliverMessageService.sendUserAddressbookUpdActionMessage(DeliverMessageType.AC.getPrefix(), uid, true);
				//}
			}
			SpringMVCHelper.renderJson(response, Response.SUCCESS);
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	
	/**
	 * 用户注册流程完成
	 * @param request
	 * @param response
	 * @param email
	 * @param nickname
	 * @param password
	 */
	@ResponseBody()
	@RequestMapping(value="/register_completed",method={RequestMethod.GET,RequestMethod.POST})
	public void registercompleted(HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = true) int uid) {
		SpringMVCHelper.renderJson(response, Response.SUCCESS);
/*		//User user = this.userService.getById(uid);
		//validateUserNotNull(user);
		try{
//			String remoteIp = WebHelper.getRemoteAddr(request);
//			user.setRegip(remoteIp);
//			user.setValidated(true);
//			this.userService.update(user);
//			deliverMessageService.sendUserRegisteredActionMessage(DeliverMessageType.AC.getPrefix(), user.getId(),user.getInviteuid(), null,user.getRegdevice(),remoteIp);
			SpringMVCHelper.renderJson(response, Response.SUCCESS);
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}finally{
			
		}*/
	}
	/**
	 * 注册成功以后,sns立刻绑定数据, 修复用户分多次注册情况下的SNS数据加载问题
	 * @param uid
	 */
	/*public void registercompletedAfter(int uid){
		List<UserSnsState> snslist = userSnsStateService.queryByUid(uid);
		if(!snslist.isEmpty()){
			UserSnsState state = snslist.get(0);
			deliverMessageService.sendSnsBindedActionMessage(DeliverMessageType.AC.getPrefix(), uid, 
					state.getId().getIdentify(), state.getAuid());

		}
	}*/
	

	
/*	private List<Integer> getIntIds(List<UserFrdRelation> list) {
		if (CollectionUtils.isEmpty(list))
			return Collections.emptyList();
		List<Integer> ids = new ArrayList<Integer>();
		for (UserFrdRelation relation : list) {
			ids.add(relation.getFrdid());
		}
		return ids;
	}
	
	private User getUserBy(Integer uid,List<User> users) {
		for (User user : users) {
			if(uid.equals(user.getId()))
				return user;
		}
		return null;
	}*/
	

	/**
	 * 提取用户好友
	 * @param request
	 * @param response
	 * @param uid
	 * @param start
	 * @param size
	 */
//	@ResponseBody()
//	@RequestMapping(value="/fetch_friends",method={RequestMethod.GET,RequestMethod.POST})
//	public void fetchfriends(HttpServletRequest request,
//			HttpServletResponse response, 
//			@RequestParam(required = true) Integer uid,
//			@RequestParam(required = false, defaultValue = "0", value = "st") int start,
//			@RequestParam(required = false, defaultValue = "10", value = "ps") int size){
//		
//		UserFriendRelation model = null;
//		User user = null;
//		try{
//			List<UserFriendStatusDTO> pagestatuslist = null;
//
//			//int count = userFriendRelationService.countNormalFrdsBy(uid);
//			long count = businessUserFriendRelationCachedService.count(uid);
//			if(count > 0){
//				List<Integer> idList = new ArrayList<Integer>();
//				List<UserFriendPK> modelPkList = new ArrayList<UserFriendPK>();
//				UserFriendPK modelPk = null;
//				
//				//List<UserFriendRelation> friends = userFriendRelationService.queryNormalSectionFrdsBy(uid, start, size, "updated_at desc, created_at");
//				Set<String> ids = businessUserFriendRelationCachedService.query(uid, start, size);
//				//List<Integer> ids = getIntIds(friends);
//				Iterator<String> iterator = ids.iterator();
//				while(iterator.hasNext()){
//					String id = iterator.next();
//					modelPk = new UserFriendPK(uid,Integer.parseInt(id));
//					modelPkList.add(modelPk);
//					idList.add(Integer.parseInt(id));
//				}
//				
//				List<User> users = this.userService.findByIds(idList, true, true);
//				List<UserFriendRelation> modelList = userFriendRelationService.findByIds(modelPkList,true,true);
//				
//				pagestatuslist = new ArrayList<UserFriendStatusDTO>();
//				/*if(!users.isEmpty() && !modelList.isEmpty()){
//					for(User user1 : users){
//						System.out.print(" " + user1.getId());
//					}
//					System.out.println("");
//					for(UserFriendRelation relation : modelList){
//						System.out.print(" " + relation.getFrdid() + ":" + relation.isFrdshp());
//					}
//				}*/
//				
//				List<String> onlinestatus = userOnlineService.usersCheckOnline(ArrayHelper.toStringListOfInteger(idList));
//				for(int i = 0;i<users.size();i++){
//					user = users.get(i);
//					model = modelList.get(i);
//					if(user != null && model != null){
//						UserFriendStatusDTO sdto = new UserFriendStatusDTO(StringUtil.isNullOrEmpty(onlinestatus.get(i)) ? false : true);
//						//user = this.getUserBy(model.getFrdid(), users);
//						//if(user != null){
//						sdto.setNick(user.getNickname());
//						sdto.setAvatar(user.getAvatar());
//						//}
//						sdto.setFrdshp(model.isFrdshp());
//						sdto.setFrom(model.getFrdfrom());
//						sdto.setUid(String.valueOf(user.getId()));
//						pagestatuslist.add(sdto);
//					}
//				}
//			}else pagestatuslist = new ArrayList<UserFriendStatusDTO>();
//			
//			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(JsonHelper.getPartialData(pagestatuslist, start, size, (int)count)));//JsonHelper.getPageData(pageReulst,true)));
//		}catch(Exception ex){
//			ex.printStackTrace();
//			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
//		}
//	}
	
	
	/*public List<Integer> converList(Set<String> set){
		List<Integer> ret = new ArrayList<Integer>();
		if(!set.isEmpty()) {
			for(String id : set){
				ret.add(new Integer(id));
			}
		}
		return ret;
	}*/
	
	
	/**
	 * 支持使用Jquery.validate Ajax检验 某种类别数据是否重复.
	 * @param response
	 * @param type
	 * @param data
	 * @param olddata
	 */
	@ResponseBody()
	@RequestMapping(value="/check_unique",method={RequestMethod.GET,RequestMethod.POST})
	public void check_unique(
			HttpServletResponse response,
			//@RequestParam(required = true,value="t") int type,
			@RequestParam(required = false,value="cc",defaultValue="86") int countrycode,
			@RequestParam(required = true) String acc,
            @RequestParam(required = false) String oldacc) {
		try{
			if (acc == null || acc.equals(oldacc)) {
				SpringMVCHelper.renderJson(response, Response.SUCCESS);//renderHtml(response, html, headers)
				return;
			}
			/*public static final int ValidateType_Email = 0;
			public static final int ValidateType_Mobileno = 1;
			public static final int ValidateType_Nick = 2;*/
			ResponseError validateError = ValidateService.validateMobileno(countrycode,acc);
			/*switch(type){
				case ValidateService.ValidateType_Email:
					validateError = ValidateService.validateEmail(acc);
					break;
				case ValidateService.ValidateType_Mobileno:
					validateError = ValidateService.validateMobileno(acc);
					break;
				case ValidateService.ValidateType_Nick:
					validateError = ValidateService.validateNick(acc);
					break;
				default:
					break;
			}*/
			//ResponseError validateError = ValidateService.validateNick(nickname);//, userService);
			if(validateError != null){
				SpringMVCHelper.renderJson(response, validateError);
				return;
			}else{
				SpringMVCHelper.renderJson(response, Response.SUCCESS);
				return;
			}
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	
	@ResponseBody()
	@RequestMapping(value="/check_nick",method={RequestMethod.GET,RequestMethod.POST})
	public void check_nick(
			HttpServletResponse response,
			@RequestParam(required = true) String nick,
			@RequestParam(required = false) String oldnick
			) {
		
		try{
			if (nick == null || nick.equals(oldnick)) {
				SpringMVCHelper.renderJson(response, Response.SUCCESS);//renderHtml(response, html, headers)
				return;
			}
			ResponseError validateError = ValidateService.validateNick(nick);
			if(validateError != null){
				SpringMVCHelper.renderJson(response, validateError);
				return;
			}else{
				SpringMVCHelper.renderJson(response, Response.SUCCESS);
				return;
			}
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	
}

