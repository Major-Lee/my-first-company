package com.whisper.business.service;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.cores.helper.StringHelper;
import com.whisper.api.user.model.User;
import com.whisper.business.ucenter.service.UserAvatarStateService;
import com.whisper.business.ucenter.service.UserExtensionService;
import com.whisper.helper.UserSymbolHelper;

@Service
public class UserLoginDataService{
	private final String NAME_USER = "usr";
	private final String NAME_SETTING = "set";
	//客户端同步通信录的时间间隔 单位为小时
	private final String Setting_Post_Address_Interval = "s_pai";
	//即时通信消息过期时间 单位小时
	private final String Setting_Context_Msg_Expire_Hours = "s_cmeh";
	//private final String Setting_Syncfrds_all_Interval = "s_sfa";
//	private final String Setting_Syncfrds_increment_Interval = "s_sfi";
	//private final String NAME_USER_KEYSTATUS = "keysta";
	//private final String NAME_USER_SNS = "sns";
	//private final String NAME_MENU = "m";
	//private final String IOS_CLIENT_CACHE_KEY = "cm";
	/*//private final String NAME_KEY = "k";
	//private final String NAME_INSTALL_KEY = "instl";
	private final String NAME_PLAYING = "pld";
	//private final String NAME_FUNCTION = "avbF";
	private final String NAME_SETTING = "sts";
	//private final String NAME_SNS_TOKENS = "snstokens";
	private final String NAME_USER_MENU_VIEW = "m";
	//private final String NAME_USER_TASK_VIEW = "k";
	private final String NAME_USER_THEME_VIEW = "t";
	private final String NEW_VIEW = "newview";
	private final String RELEASE = "release";
	private final String EXIST_USER_ONLINE_DEVICE = "eod";*/
	
	//@Resource
	//private UserKeyCdService userKeyCdService;
	
	//@Resource
	//private UserInviteTokensStateService userInviteTokensStateService;
	
	//@Resource
	//private UserSnsTokenFacadeService userSnsTokenFacadeService;
	
	/*@Resource
	private UserSettingStateService userSettingStateService;*/
	
	/*@Resource
	private UserMenuViewStateService userMenuViewStateService;
	
	@Resource
	private UserTaskViewStateService userTaskViewStateService;*/
	
	/*@Resource
	private UserThemeViewStateService userThemeViewStateService;
	
	@Resource
	private ReleaseService releaseService;*/
	
	@Resource
	private UserExtensionService userExtensionService;
	
/*	@Resource
	private UserMenuViewStateService userMenuViewStateService;
	
	@Resource
	private MenuService menuService;*/
	
	@Resource
	private UserAvatarStateService userAvatarStateService;
	

	/*@Resource
	private UserSnsBindService userSnsBindService;*/
	
	
	
	/*public Map<String,Object> genernalAppUserLoginData(String ip, AppUser user){
		Map<String,Object> loginDataMap = new HashMap<String,Object>();
		//loginDataMap.put(NAME_KEY, this.keyData(ip));
		loginDataMap.put(NAME_USER, this.appUserData(user));
		return loginDataMap;
	}*/
	
	/*public Map<String,Object> buildFormalUserLoginData(User user){
		Map<String,Object> loginDataMap = new HashMap<String,Object>();
		//loginDataMap.put(NAME_KEY, this.keyData(ip));
		//loginDataMap.put(NAME_INSTALL_KEY, this.installData(user));
		loginDataMap.put(NAME_USER, this.userData(user));
		//loginDataMap.put(NAME_MENU, this.menuData(user));
		loginDataMap.put(NAME_PLAYING, this.playingData(user));
		//loginDataMap.put(NAME_FUNCTION, this.functionData(user));
		loginDataMap.put(NEW_VIEW, this.userNewViewData(user));
		loginDataMap.put(NAME_SNS_TOKENS, this.snsTokensData(user));
		UserSettingState state = this.userSettingStateService.getById(user.getId());
		loginDataMap.put(NAME_SETTING, this.settingData(state));
		UserExtension userExtension = userExtensionService.getOrCreateById(user.getId());
		if(userExtension != null){
			if(System.currentTimeMillis() < userExtension.getAttachcached_expired())
				loginDataMap.put(IOS_CLIENT_CACHE_KEY, String.valueOf(userExtension.getLocalcached_limit()+userExtension.getAttachcached_limit()));
			else
				loginDataMap.put(IOS_CLIENT_CACHE_KEY, String.valueOf(userExtension.getLocalcached_limit()));
		}else{
			loginDataMap.put(IOS_CLIENT_CACHE_KEY, String.valueOf(UserExtension.Default_LocalCachedLimit));
		}
		
		if(user.getNewbie() == User.STATUS_USER_REGISTER_FULL) loginDataMap.put(RELEASE, this.releaseData(state));
		else loginDataMap.put(RELEASE, null);
		
		String existUserOnlineDevice = UserOnlineFacadeService.fetchUserSignedOnDevice(String.valueOf(user.getId()));
		if(StringHelper.isNotEmpty(existUserOnlineDevice)){
			loginDataMap.put(EXIST_USER_ONLINE_DEVICE, existUserOnlineDevice);
		}
		
		
		return loginDataMap;
	}*/
	
	public Map<String,Object> buildLoginData(User user){
		Map<String,Object> loginDataMap = new HashMap<String,Object>();
		loginDataMap.put(NAME_USER, this.userData(user));
		loginDataMap.put(NAME_SETTING, this.settingData(user));
		//loginDataMap.put(NAME_USER_KEYSTATUS, userKeyCdService.fetchKeyStatus(user.getId()));
		//loginDataMap.put(NAME_USER_SNS, this.userSnsData(user.getId()));
		//loginDataMap.put(NAME_PLAYING, this.playingData(user));
		//loginDataMap.put(NEW_VIEW, this.userNewViewData(user));
		//loginDataMap.put(NAME_SNS_TOKENS, this.snsTokensData(user));
		//UserSettingState state = this.userSettingStateService.getById(user.getId());
		//loginDataMap.put(NAME_SETTING, this.settingData(state));
		//UserExtension userExtension = userExtensionService.getOrCreateById(user.getId());
		
		/*if(System.currentTimeMillis() < userExtension.getAttachcached_expired())
			loginDataMap.put(IOS_CLIENT_CACHE_KEY, String.valueOf(userExtension.getLocalcached_limit()+userExtension.getAttachcached_limit()));
		else
			loginDataMap.put(IOS_CLIENT_CACHE_KEY, String.valueOf(userExtension.getLocalcached_limit()));*/
		
		/*if(user.getNewbie() == User.STATUS_USER_REGISTER_FULL) loginDataMap.put(RELEASE, this.releaseData(state));
		else loginDataMap.put(RELEASE, null);
		
		String existUserOnlineDevice = UserOnlineFacadeService.fetchUserSignedOnDevice(String.valueOf(user.getId()));
		if(StringHelper.isNotEmpty(existUserOnlineDevice)){
			loginDataMap.put(EXIST_USER_ONLINE_DEVICE, existUserOnlineDevice);
		}*/
		
		return loginDataMap;
	}
	
	
	/*public Object userNewViewData(User user){
		Map<String,Object> newViewDataMap = new HashMap<String,Object>();
		newViewDataMap.put(NAME_USER_MENU_VIEW, this.userMenusViewData(user));
		//newViewDataMap.put(NAME_USER_TASK_VIEW, this.userTasksViewData(user));
		newViewDataMap.put(NAME_USER_THEME_VIEW, this.userThemesViewData(user));
		return newViewDataMap;
	}*/
	
	/*public Object snsTokensData(User user){
		Map<String,Object> tokensMap = new HashMap<String,Object>();
		UserSnsBind state = userSnsBindService.getById(user.getId());
		//System.out.println("uid:" + user.getId());
		if(state != null){
			Set<String> keySet = state.getTags();
			//System.out.println("keys:" + keySet);
			Object tObj = null;
			for(String key : keySet){
				tObj = state.getTagValue(key);
				if(tObj != null){
					String gtoken = (String)tObj;
					String[] tokenAttrs = gtoken.split(StringHelper.COMMA_STRING_GAP);
					tokensMap.put(key, tokenAttrs[0]);
				}
			}
		}
		return tokensMap;
	}*/
	
	/*public Object userMenusViewData(User user){
		List<String> viewList = new ArrayList<String>();
		UserMenuViewState defaultUserMenuViewState = userMenuViewStateService.getById(UserMenuViewState.DEFAULT);
		UserMenuViewState userMenuViewState = userMenuViewStateService.getOrCreateById(user.getId());
		//新增默认显示菜单(不包括需要解锁的新增菜单)
		if(defaultUserMenuViewState != null){
			List<String> defaultAdd = defaultUserMenuViewState.getTagValues(UserMenuViewState.ADD);
			if(!defaultAdd.isEmpty()){
				List<String> userMenuViewAdd = userMenuViewState.getTagValues(UserMenuViewState.ADD);
				for(String defaultView : defaultAdd){
					if(!userMenuViewAdd.contains(defaultView)) viewList.add(defaultView); 
				}
			}
		}
		
		//解锁的需要NEW的菜单
		List<String> userMenuViewUnlock = userMenuViewState.getTagValues(UserMenuViewState.UNLOCK);
		viewList.addAll(userMenuViewUnlock);
		
		if(!viewList.isEmpty()){
			List<String> midPathList = new ArrayList<String>(viewList.size());
			List<Menu> menuList = menuService.findByIds(viewList);
			for(Menu menu : menuList){
				midPathList.add(menu.getPath());
			}
			return midPathList;
		}
		return viewList;
		
	}*/
	
	/*public Object userThemesViewData(User user){
		return userThemeViewStateService.fetchThemeUnViewAct(user.getId());
	}*/
	
	/*public Object appUserData(AppUser user){
		//Map<String,Object> userDataMap = JsonHelper.filterObjectData(user,DateConvertType.TOSTRING_EN_COMMON_YMDHMS, false, userRetFields);
		Map<String,Object> userDataMap = new HashMap<String,Object>();
		userDataMap.put("id", user.getId());
		//String app4id = String.format("%s_%s", DeviceEnum.Desktop_360_WEB_Type,user.getId());
		String guesttoken = TokenServiceHelper.generateToken4Guest(TokenServiceHelper.GuestToken);
		userDataMap.put("guesttoken", guesttoken);
		//userDataMap.put("nick", user.getNickname());
		//userDataMap.put("regip", user.getRegip());
		//userDataMap.put("sid", genernalUUID());
		
		return userDataMap;
	}*/
	
	public Object userData(User user){
		//Map<String,Object> userDataMap = JsonHelper.filterObjectData(user,DateConvertType.TOSTRING_EN_COMMON_YMDHMS, false, userRetFields);
		Map<String,Object> userDataMap = new HashMap<String,Object>();
		userDataMap.put("id", user.getId());
		userDataMap.put("nick", user.getNick());
		userDataMap.put("safety", user.getSafety_mark());
		userDataMap.put("avatar", user.getAvatar());
		userDataMap.put("regip", user.getRegip());
		userDataMap.put("mobileno", user.getMobileno());
		userDataMap.put("symbol", UserSymbolHelper.generate(user));
		//userDataMap.put("sid", genernalUUID());
		//userDataMap.put("tags", user.getTags());
		//userDataMap.put("wp_type", WallPaperDeviceHelper.getImageTypeByDevice(user.getl));
		/*UserAvatarState state = userAvatarStateService.getById(user.getId());
		if(state != null){
			if(user.getAvatar() != null){
				if(user.getAvatar().equals(state.getRenRenTag())){
					userDataMap.put("avatartiny", state.getRenRenTinyTag());
				}
			}
		}*/
		//int tokensCanGen = userInviteTokensStateService.inviteTokenCanbeGen(user.getId(), user.getCreated_at());
		//userDataMap.put("c", tokensCanGen);
		return userDataMap;
	}
	
	public Object settingData(User user){
		Map<String,Object> settingDataMap = new HashMap<String,Object>();
		settingDataMap.put(Setting_Post_Address_Interval, RuntimeConfiguration.Setting_Post_Address_Interval);
		settingDataMap.put(Setting_Context_Msg_Expire_Hours, RuntimeConfiguration.UserContextMsgExpireHours);
		//settingDataMap.put(Setting_Syncfrds_all_Interval, RuntimeConfiguration.Setting_Syncfrds_all_Interval);
		//settingDataMap.put(Setting_Syncfrds_increment_Interval, RuntimeConfiguration.Setting_Syncfrds_increment_Interval);
		return settingDataMap;
	}
	
	public String genernalUUID(){
		return UUID.randomUUID().toString().replaceAll(StringHelper.MINUS_STRING_GAP, StringHelper.UNDERLINE_STRING_GAP);
	}
	
	/**
	 * 用户第三方sns账号关联数据
	 * @param uid
	 * @return
	 */
	/*public List<Map<String, Object>> userSnsData(int uid){
		Collection<UserSnsTokenDTO> entityDtos = userSnsTokenFacadeService.getUserSnsTokens(uid);
		int length = entityDtos.size();
		if(length == 0){
			return Collections.emptyList();
		}
		List<Map<String,Object>> retMapList = new ArrayList<Map<String,Object>>(length);
		Map<String,Object> retMap = null;
		for(UserSnsTokenDTO entityDto : entityDtos){
			retMap = new HashMap<String,Object>();
			retMap.put("t", entityDto.getType());
			retMap.put("auid", entityDto.getAuid());
			retMap.put("access_token", entityDto.getAccess_token());
			retMap.put("valid", entityDto.isValid());
			retMapList.add(retMap);
		}
		return retMapList;
	}*/
	/*public Object playingData(User user){
		UserPlayingDTO dto = UserPlayingService.getInstance().getUserPlaying(user.getId());
		//if(dto == null) return null;
		//return JsonHelper.getJSONString(dto);
		return dto;
	}*/
	
	
	/*public Object settingData(UserSettingState state){
		Map<String,Object> map = new HashMap<String,Object>();
		
		//List<UserSettingDTO> dtos = new ArrayList<UserSettingDTO>();
		if(state != null){
			map.put(UserSettingType.timedot.toString(), state.getTagValue(UserSettingType.timedot.toString()));
			map.put(UserSettingType.rltd.toString(), state.getTagValue(UserSettingType.rltd.toString()));
			map.put(UserSettingType.frdlvd.toString(), state.getTagValue(UserSettingType.frdlvd.toString()));
			//if(state.getTagValue(UserSettingType.rcmd.toString()) != null) 
			map.put(UserSettingType.rcmd.toString(), state.getTagValue(UserSettingType.rcmd.toString()));
			
			map.put(UserSettingType.tckNtf.toString(), state.getTagValue(UserSettingType.tckNtf.toString()));
			map.put(UserSettingType.rmdTone.toString(), state.getTagValue(UserSettingType.rmdTone.toString()));
			map.put(UserSettingType.frdCntNtf.toString(), state.getTagValue(UserSettingType.frdCntNtf.toString()));
			map.put(UserSettingType.tipNtf.toString(), state.getTagValue(UserSettingType.tipNtf.toString()));
			
			map.put(UserSettingType.thm.toString(), state.getTagValue(UserSettingType.thm.toString()));
			
			map.put(UserSettingType.lgA.toString(), state.getTagValueNullDefault(UserSettingType.lgA));
			map.put(UserSettingType.rtCv.toString(), state.getTagValueNullDefault(UserSettingType.rtCv));
			map.put(UserSettingType.autoSnc.toString(), state.getTagValueNullDefault(UserSettingType.autoSnc));
			map.put(UserSettingType.hbr.toString(), state.getTagValueNullDefault(UserSettingType.hbr));
			
			map.put(UserSettingType.h5.toString(), state.getTagValueNullDefault(UserSettingType.h5));
		}
		return map;
	}*/
	
	
	/*public Object releaseData(UserSettingState state){
		Map<String,Object> retMap = null;
		Release release = releaseService.getUserReleaseData(state);
		if(release != null){
			retMap = JsonHelper.filterObjectData(release, DateConvertType.TOSTRING_EN_LINE_YMD, false, new String[]{"title","content","released_at"});
			if(state != null){
				state.put(UserSettingType.release.toString(), release.getId());
				userSettingStateService.update(state);
			}
		}
		return retMap;
	}*/
	
}
