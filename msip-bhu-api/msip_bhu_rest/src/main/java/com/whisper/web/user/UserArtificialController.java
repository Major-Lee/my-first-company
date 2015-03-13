package com.whisper.web.user;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;
import com.whisper.api.frd.model.UserFrdPK;
import com.whisper.api.user.model.User;
import com.whisper.business.asyn.web.builder.DeliverMessageType;
import com.whisper.business.asyn.web.model.IDTO;
import com.whisper.business.asyn.web.service.DeliverMessageService;
import com.whisper.business.bucache.redis.serviceimpl.present.frd.UserArtificialFrdMarkService;
import com.whisper.business.bucache.redis.serviceimpl.present.frd.UserFrdCountService;
import com.whisper.business.facade.ucenter.UserBusinessCacheFacadeService;
import com.whisper.business.facade.ucenter.UserFrdRelationFacadeService;
import com.whisper.business.ucenter.service.UserAdminTypeService;
import com.whisper.business.ucenter.service.UserFrdRelationService;
import com.whisper.business.ucenter.service.UserService;
import com.whisper.common.BusinessEnumType;
import com.whisper.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.whisper.msip.web.ValidateUserCheckController;

/**
 * 人工客服Controller
 * @author lawliet
 *
 */
@Controller
@RequestMapping("/artificial")
public class UserArtificialController extends ValidateUserCheckController{
	
	@Resource
	private UserService userService;
	
	@Resource
	private UserBusinessCacheFacadeService userBusinessCacheFacadeService;
	
	@Resource
	private UserFrdRelationFacadeService userFrdRelationFacadeService;
	
	@Resource
	private UserFrdRelationService userFrdRelationService;
	
	@Resource
	private UserAdminTypeService userAdminTypeService;
	
	@Resource
	private DeliverMessageService deliverMessageService;
	
	/**
	 * 获取人工客服的用户列表
	 * @param request
	 * @param response
	 * @param uid
	 */
//	@ResponseBody()
//	@RequestMapping(value="/fetch",method={RequestMethod.GET,RequestMethod.POST})
//	public void fetch(HttpServletRequest request,
//			HttpServletResponse response, 
//			@RequestParam(required = true) Integer uid) {
//		
//		User user = this.userService.getById(uid);
//		validateUserNotNull(user);
//		try{
//			int utype = BusinessEnumType.UserType.SystemArtificialUser.getType();
//			List<User> systemArtificialUsers = userBusinessCacheFacadeService.getCacheUsersByType(utype);
//			int total = systemArtificialUsers.size();
//			if(total == 0){
//				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(JsonHelper.getPartialData(Collections.emptyList(), 0, 0, total)));
//			}
//			
//			List<UserDTO> dtos = new ArrayList<UserDTO>();
//			UserDTO dto = null;
//			for(User systemArtificialUser : systemArtificialUsers){
//				dto = new UserDTO();
//				dto.setId(systemArtificialUser.getId());
//				dto.setNick(systemArtificialUser.getNick());
//				dto.setAvatar(systemArtificialUser.getAvatar());
//				dto.setMobileno(systemArtificialUser.getMobileno());
//				dtos.add(dto);
//			}
//			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(JsonHelper.getPartialData(dtos, 0, 0, total)));
//		}catch(Exception ex){
//			ex.printStackTrace();
//			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
//		}
//	}

	@ResponseBody()
	@RequestMapping(value="/post",method={RequestMethod.GET,RequestMethod.POST})
	public void post(HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String sex) {
		
		User user = this.userService.getById(uid);
		validateUserNotNull(user);
		try{
//			UserSex selectedSex = BusinessEnumType.UserSex.fromType(sex);
			
//			int utype = BusinessEnumType.UserType.SystemArtificialUser.getType();
//			UserAdminType userAdminType_entity = userAdminTypeService.getById(frdid);
//			if(userAdminType_entity == null || userAdminType_entity.getUtype() != utype){
//				SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL));
//				return;
//			}
			
			int frd_count = UserFrdCountService.getInstance().getFrdCount(String.valueOf(uid));
			if(frd_count > RuntimeConfiguration.CheckArtificialFrdsAddLimitCount){
				SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.USER_ARTIFICIAL_FRDADD_LIMIT));
				return;
			}
			
			Integer selected_uid = userBusinessCacheFacadeService.findFreeUserId(uid, sex);
			if(selected_uid == null){
				SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_NOTEXIST));
				return;
			}
			
			boolean uid_success = userFrdRelationFacadeService.addUserFrdRelation(uid, selected_uid, 
					BusinessEnumType.FrdType.artificial.getType(), null, true);
//			if(uid_success){
//				UserArtificialFrdMarkHashService.getInstance().userArtificialFrdMark(uid, String.valueOf(selected_uid));
//			}
			boolean frdid_success = userFrdRelationFacadeService.addUserFrdRelation(selected_uid, uid,
					BusinessEnumType.FrdType.artificial.getType(), null, true);
			if(frdid_success){
				UserArtificialFrdMarkService.getInstance().userArtificialFrdMark(String.valueOf(selected_uid), String.valueOf(uid));
			}

			deliverMessageService.sendUserFrdUpdActionMessage(DeliverMessageType.AC.getPrefix(), uid, uid_success, 
					selected_uid, frdid_success, IDTO.ACT_ADD);
			
//			User selected_user = userService.getById(selected_uid);
//			UserDTO dto = null;
//			if(selected_user != null){
//				dto = new UserDTO();
//				dto.setId(selected_user.getId());
//				dto.setNick(selected_user.getNick());
//				dto.setAvatar(selected_user.getAvatar());
//				dto.setMobileno(selected_user.getMobileno());
//			}

			SpringMVCHelper.renderJson(response, ResponseSuccess.SUCCESS);
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	/**
	 * 客户端根据倒计时主动触发的教练过期
	 * 彻底删除好友关系
	 * 增量好友数量
	 * @param request
	 * @param response
	 * @param uid
	 * @param frdid
	 */
	@ResponseBody()
	@RequestMapping(value="/exprie",method={RequestMethod.GET,RequestMethod.POST})
	public void exprie(HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) Integer frdid) {
		
		User user_entity = this.userService.getById(uid);
		validateUserNotNull(user_entity);
		User frd_entity = this.userService.getById(frdid);
		validateUserNotNull(frd_entity);
		try{
//			if(BusinessEnumType.UserType.SystemArtificialUser.getType() != frd_entity.getUtype()){
//				SpringMVCHelper.renderJson(response, ResponseSuccess.SUCCESS);
//				return;
//			}

			int uid_ret = userFrdRelationService.deleteById(new UserFrdPK(uid, frdid));
			boolean uid_success = (uid_ret == 1);
			if(uid_success){
				if(BusinessEnumType.UserType.isSystemArtificialUser(user_entity.getUtype())){
					UserArtificialFrdMarkService.getInstance().userArtificialFrdRemove(String.valueOf(uid), String.valueOf(frdid));
				}
			}

			int frdid_ret = userFrdRelationService.deleteById(new UserFrdPK(frdid, uid));
			boolean frdid_success = (frdid_ret == 1);
			if(frdid_success){
				if(BusinessEnumType.UserType.isSystemArtificialUser(frd_entity.getUtype())){
					UserArtificialFrdMarkService.getInstance().userArtificialFrdRemove(String.valueOf(frdid), String.valueOf(uid));
				}
			}
//			boolean[] succesies = userFrdRelationFacadeService.removeUserFrdRelation(uid, frdid);
//			boolean uid_success = succesies[0];
//			boolean frdid_success = succesies[1];
//			if(frdid_success){
//				UserArtificialFrdMarkService.getInstance().userArtificialFrdRemove(String.valueOf(frdid), String.valueOf(uid));
//			}
			deliverMessageService.sendUserFrdUpdActionMessage(DeliverMessageType.AC.getPrefix(), uid, uid_success, 
						frdid, frdid_success, IDTO.ACT_DELETE);
			
			SpringMVCHelper.renderJson(response, ResponseSuccess.SUCCESS);
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
}
