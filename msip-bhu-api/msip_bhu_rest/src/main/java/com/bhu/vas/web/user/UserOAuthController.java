package com.bhu.vas.web.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;

@Controller
@RequestMapping(value = "/oauth")
public class UserOAuthController extends BaseController{
	
	//@Resource
	//private UserSnsStateService userSnsStateService;

	@ResponseBody()
	@RequestMapping(value="/fetch_identifies", method={RequestMethod.GET,RequestMethod.POST})
	public void fetch_identifies(HttpServletResponse response, 
			@RequestParam(required=true) Integer uid){
		/*CommonCriteria mc = new CommonCriteria();
		mc.createCriteria().andColumnEqualTo("uid", uid);
		List<UserSnsState> models = userSnsStateService.findModelByCommonCriteria(mc);
		
		List<UserSnsIndetifyDTO> dtos = new ArrayList<UserSnsIndetifyDTO>();
		if(!models.isEmpty()){
			for(UserSnsState model : models){
				dtos.add(new UserSnsIndetifyDTO(model.getIdentify(), model.getUpdated_at().getTime()));
			}
		}
		SpringMVCHelper.renderJson(response, ResponseSuccess.embed(dtos));*/
	}
	
	
	@ResponseBody()
	@RequestMapping(value="/remove", method={RequestMethod.GET,RequestMethod.POST})
	public void removebind(
			HttpServletResponse response, 
			@RequestParam(required=true) Integer uid,
			@RequestParam(required=true) String identify){
		
		/*UserSnsState model = userSnsStateService.getById(new UserSnsStatePK(uid, identify.toString()));
		if(model != null){
			bindManager.unbind(uid, identify);
			//userSnsStateService.removeByIdAndIdentify(uid, identify.toString());
			userSnsStateService.delete(model);
			UniqueSnsRelationHashService.getInstance().userSnsRemove(identify.toString(), model.getAuid());
			
			SnsFriendService.getInstance().deleteFriendList(uid, identify.toString());
			SnsFriendService.getInstance().deleteBlackList(uid, identify.toString());
		}
		SpringMVCHelper.renderJson(response, ResponseSuccess.SUCCESS);*/
	}
	
	@ResponseBody()
	@RequestMapping(value="/create", method={RequestMethod.GET,RequestMethod.POST})
	public void bind(ModelAndView mv, HttpServletRequest request, HttpServletResponse response, 
			@RequestParam(required=true) Integer uid,
			@RequestParam(required=true) String identify) throws Exception{
		/*try{
			//GeneralOAuth2AccessToken generalOAuth2AccessToken = super.currentGeneralAccessToken(request, identify, null);
			GeneralOAuth2AccessToken generalOAuth2AccessToken = TokenStoreUtils.getBindToken(uid, identify);
			if(generalOAuth2AccessToken == null){
				SpringMVCHelper.renderJson(response,ResponseError.embed(ResponseErrorCode.SNS_FIND_NOT_TOKEN));
				return;
			}
			String existouid = UniqueSnsRelationHashService.getInstance().userSnsQuery(identify.toString(), 
					generalOAuth2AccessToken.getGeneralOAuth2User().getAuid());
			if(StringHelper.isEmpty(existouid)){
				bindManager.bind(uid, identify, generalOAuth2AccessToken);
				super.associationUser(uid, identify, generalOAuth2AccessToken.getGeneralOAuth2User().getAuid());
			}
			//else if(state.getId().getUid() == uid.intValue()){
			else if(existouid.equals(String.valueOf(uid))){
				bindManager.bind(uid, identify, generalOAuth2AccessToken);
			}
			else {
				//System.out.println("snsbind 1:" + state.getId().getUid() + " " + uid.intValue());
				//throw new OAuth2Exception(ResponseErrorCode.SNS_SAME_AUID_BIND);
				SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.SNS_SAME_AUID_BIND));
				return;
			}
			super.addAvator(uid, generalOAuth2AccessToken.getGeneralOAuth2User().getAuid(),
					generalOAuth2AccessToken.getGeneralOAuth2User().getPortrait(), 
					generalOAuth2AccessToken.getGeneralOAuth2User().getPortraitTiny(), identify);
			//super.addCurrentAvator(uid, generalOAuth2AccessToken.getGeneralOAuth2User().getPortrait());
			super.mqHandle(uid, identify, generalOAuth2AccessToken.getGeneralOAuth2User().getAuid());
			//super.unlockSNSBadge(uid, identify);
			//super.dolockUserMenu(uid, identify, true);
	//		Map<String,Object> ret = new HashMap<String,Object>();
	//		ret.put("token", generalOAuth2AccessToken.getAccessToken());
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(generalOAuth2AccessToken.getAccessToken()));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}*/
	}

}
