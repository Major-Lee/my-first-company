package com.whisper.web.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smartwork.im.utils.JingConstants;
import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;
import com.smartwork.msip.localunit.RandomPicker;
import com.whisper.api.frd.dto.UserFrdDTO;
import com.whisper.api.frd.model.UserFrdPK;
import com.whisper.api.frd.model.UserFrdRelation;
import com.whisper.api.user.model.User;
import com.whisper.business.asyn.notify.service.DeliverNotifyMessageService;
import com.whisper.business.asyn.web.builder.DeliverMessageType;
import com.whisper.business.asyn.web.model.IDTO;
import com.whisper.business.asyn.web.service.DeliverMessageService;
import com.whisper.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.whisper.business.bucache.redis.serviceimpl.present.frd.UserFrdCountService;
import com.whisper.business.facade.ucenter.UserBusinessCacheFacadeService;
import com.whisper.business.facade.ucenter.UserFrdIncrementStateFacadeService;
import com.whisper.business.facade.ucenter.UserFrdRelationFacadeService;
import com.whisper.business.ucenter.service.UserFrdRelationService;
import com.whisper.business.ucenter.service.UserService;
import com.whisper.common.BusinessEnumType;
import com.whisper.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.whisper.msip.web.ValidateUserCheckController;

/**
 * 好友Controller
 * @author lawliet
 *
 */
@Controller
@RequestMapping("/frd")
public class UserFrdController extends ValidateUserCheckController{

	@Resource
	private UserService userService;
	
	@Resource
	private UserFrdRelationService userFrdRelationService;
	
	@Resource
	private UserFrdRelationFacadeService userFrdRelationFacadeService;
	
	@Resource
	private DeliverNotifyMessageService deliverNotifyMessageService;
	
	@Resource
	private DeliverMessageService deliverMessageService;
	
	@Resource
	private UserFrdIncrementStateFacadeService userFrdIncrementStateFacadeService;
	
	@Resource
	private UserBusinessCacheFacadeService userBusinessCacheFacadeService;
	
	/**
	 * 添加好友申请
	 * @param request
	 * @param response
	 * @param uid
	 * @param frdid
	 */
	@ResponseBody()
	@RequestMapping(value="/apply_frd_req",method={RequestMethod.GET,RequestMethod.POST})
	public void apply_frd_req(HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) Integer frdid) {
		
		if(uid.equals(frdid)){
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL));
			return;
		}
		
		User user = this.userService.getById(uid);
		validateUserNotNull(user);
		try{
			//如果是明星用户的话,好友数量收到特殊限制,直接成为好友的策略是50%概率的随机
			if(JingConstants.isStarUser(String.valueOf(frdid))){
				//检查好友数量是否超过限制
				if(this.getUserFrdCount(frdid) >= RuntimeConfiguration.StarUserFrdMaxCount){
					SpringMVCHelper.renderJson(response, ResponseSuccess.SUCCESS);
					return;
				}
				//50%概率
				//if(RandomPicker.randBoolean()){
					System.out.println("Star User become : " + frdid);
					this.doBecomeFrds(uid, frdid);
				//}
			}
			//正常用户的好友申请策略
			else{
				ResponseError validateError = this.validateFrdCountLimit(uid);
				if(validateError != null){
					SpringMVCHelper.renderJson(response, validateError);
					return;
				}
				
				UserFrdRelation entity = userFrdRelationFacadeService.getUserFrdRelation(new UserFrdPK(uid,frdid));
				if(entity != null && entity.isVisible() && entity.isFrdshp()){
					SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.USER_FRD_EXIST));
					return;
				}
				
				deliverNotifyMessageService.sendDeliverNotifyUserFrdApplyMessage(String.valueOf(uid), String.valueOf(frdid), 
						String.valueOf(user.getId()), user.getNick(), user.getAvatar(), user.getMobileno());
			}
			
			SpringMVCHelper.renderJson(response, ResponseSuccess.SUCCESS);
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}

	
	/**
	 * 回应用户申请好友请求
	 * @param request
	 * @param response
	 * @param uid
	 * @param frdid
	 * @param agreement 同意或拒绝
	 */
	@ResponseBody()
	@RequestMapping(value="/reply_frd_req",method={RequestMethod.GET,RequestMethod.POST})
	public void reply_frd_req(HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) Integer frdid,
			@RequestParam(required = false, defaultValue = "false", value = "ag") boolean agreement) {
		
		if(uid.equals(frdid)){
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL));
			return;
		}
		
		User user = this.userService.getById(uid);
		validateUserNotNull(user);
		User frd_user = this.userService.getById(frdid);
		validateUserNotNull(frd_user);
		try{

			if(agreement){
				ResponseError validateError = this.validateFrdCountLimit(uid);
				if(validateError != null){
					SpringMVCHelper.renderJson(response, validateError);
					return;
				}
				
				this.doBecomeFrds(uid, frdid);
			}
			
			SpringMVCHelper.renderJson(response, ResponseSuccess.SUCCESS);
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	/**
	 * 验证用户的好友数量是否超过限制
	 * @param uid
	 * @return
	 */
	public ResponseError validateFrdCountLimit(int uid){
		//int count = userFrdRelationFacadeService.countUserFrdRelations(uid);
		int count = this.getUserFrdCount(uid);
		if(count >= RuntimeConfiguration.UserFrdMaxCount){
			return ResponseError.embed(ResponseErrorCode.USER_FRD_COUNT_LIMIT);
		}
		return null;
	}
	/**
	 * 获取用户的好友数量
	 * @param uid
	 * @return
	 */
	public int getUserFrdCount(int uid){
		return UserFrdCountService.getInstance().getFrdCount(String.valueOf(uid));
	}
	
	/**
	 * 互相成为好友的数据处理逻辑
	 * @param uid
	 * @param frdid
	 */
	public void doBecomeFrds(int uid, int frdid){
		boolean uid_success = userFrdRelationFacadeService.addLocalUserFrdRelation(uid, frdid);
		boolean frdid_success = userFrdRelationFacadeService.addLocalUserFrdRelation(frdid, uid);
		deliverMessageService.sendUserFrdUpdActionMessage(DeliverMessageType.AC.getPrefix(), uid, uid_success, 
				frdid, frdid_success, IDTO.ACT_ADD);
	}
	
	/**
	 * 介绍用户给好友
	 * @param request
	 * @param response
	 * @param uid
	 * @param frdid
	 * @param intro_uids 介绍的用户的uids 逗号分隔
	 */
	@ResponseBody()
	@RequestMapping(value="/intro_frd_req",method={RequestMethod.GET,RequestMethod.POST})
	public void intro_frd_req(HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) Integer frdid,
			@RequestParam(required = true, value = "i_uids") String intro_uids) {
		try{
			String[] intro_uid_ay = intro_uids.split(StringHelper.COMMA_STRING_GAP);
			List<Integer> intro_uids_int = this.fromStringAyToUids(intro_uid_ay);
			
			List<User> users = userService.findByIds(intro_uids_int, true, true);
			int length = intro_uid_ay.length;
			if(length != users.size()){
				SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL));
				return;
			}
			String[] intro_nick_ay = new String[length];
			String[] intro_avatar_ay = new String[length];
			String[] intro_mobileno_ay = new String[length];
			User entity = null;
			for(int i=0;i<length;i++){
				entity = users.get(i);
				intro_nick_ay[i] = entity.getNick();
				intro_avatar_ay[i] = entity.getAvatar();
				intro_mobileno_ay[i] = entity.getMobileno();
			}
//			String intro_nicks = StringHelper.join(StringHelper.COMMA_STRING_GAP, intro_nick_ay);
//			String intro_avatars = StringHelper.join(StringHelper.COMMA_STRING_GAP, intro_avatar_ay);
//			String intro_mobilenos = StringHelper.join(StringHelper.COMMA_STRING_GAP, intro_mobileno_ay);
			
			deliverNotifyMessageService.sendDeliverNotifyUserFrdIntroMessage(String.valueOf(uid), String.valueOf(frdid), intro_uid_ay, 
					intro_nick_ay, intro_avatar_ay, intro_mobileno_ay);
			SpringMVCHelper.renderJson(response, ResponseSuccess.SUCCESS);
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	
	/**
	 * 移除好友关系
	 * 并给被删除的用户发送长连接消息
	 * @param request
	 * @param response
	 * @param uid
	 * @param frdid
	 */
	@ResponseBody()
	@RequestMapping(value="/remove_frd",method={RequestMethod.GET,RequestMethod.POST})
	public void remove_frd(HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) Integer frdid) {

		try{
//			List<UserFrdPK> pks = new ArrayList<UserFrdPK>();
//			pks.add(new UserFrdPK(uid, frdid));
//			pks.add(new UserFrdPK(frdid, uid));
//			List<UserFrdRelation> entitys = userFrdRelationService.findByIds(pks);
//			if(!entitys.isEmpty()){
//				boolean uid_success = false;
//				boolean frdid_success = false;
//				for(UserFrdRelation entity : entitys){
//					if(entity.isVisible()){
//						entity.setVisible(false);
//						entity.setFrdshp(false);
//						userFrdRelationService.update(entity);
//						if(uid.equals(entity.getUid()))
//							uid_success = true;
//						else
//							frdid_success = true;
//					}
//				}
			boolean[] succesies = userFrdRelationFacadeService.removeUserFrdRelation(uid, frdid);
			boolean uid_success = succesies[0];
			boolean frdid_success = succesies[1];
			deliverMessageService.sendUserFrdUpdActionMessage(DeliverMessageType.AC.getPrefix(), uid, uid_success, 
						frdid, frdid_success, IDTO.ACT_DELETE);
//				boolean frd_removed = userFrdRelationFacadeService.removeUserFrdRelation(uid, frdid);
//				if(frd_removed){
//					
//					deliverNotifyMessageService.sendDeliverNotifyUserFrdRemovedMessage(String.valueOf(uid), String.valueOf(frdid), String.valueOf(uid));
//					//userFrdIncrementStateFacadeService.storeUserFrdIncrementRemoveState(uid, frdid);
//					userFrdIncrementStateFacadeService.refreshIncrementMarks(String.valueOf(uid), String.valueOf(frdid));
//				}
			//}
			SpringMVCHelper.renderJson(response, ResponseSuccess.SUCCESS);
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	
	/**
	 * 好友全部数据（老版本）
	 * @param request
	 * @param response
	 * @param uid
	 * @param start
	 * @param size
	 */
	@Deprecated
	@ResponseBody()
	@RequestMapping(value="/fetch_all",method={RequestMethod.GET,RequestMethod.POST})
	public void fetch_frds(HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = true) Integer uid) {
		
		User user = this.userService.getById(uid);
		validateUserNotNull(user);
		try{
			//List<UserFrdRelation> entityList = userFrdRelationFacadeService.getUserFrdRelations(uid);
			List<UserFrdRelation> entityList = userFrdRelationFacadeService.segmentGetUserFrdRelations(uid);
			int total = entityList.size();
//			if(total == 0){
//				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(JsonHelper.getPartialData(Collections
//						.emptyList(), 0, 0, 0)));
//				return;
//			}
			List<UserFrdDTO> retDtos = new ArrayList<UserFrdDTO>();
			if(total > 0){
				List<Integer> frdids = this.fromEntityToUids(entityList);
				List<User> userEntityList = userService.findByIds(frdids, true, true);
				
				UserFrdDTO dto = null;
				UserFrdRelation frdEntity = null;
				int cursor = 0;
				for(User userEntity : userEntityList){
					if(userEntity != null){
						frdEntity = entityList.get(cursor);
						dto = new UserFrdDTO();
						dto.setId(userEntity.getId());
						dto.setNick(userEntity.getNick());
						dto.setAvatar(userEntity.getAvatar());
						dto.setMobileno(userEntity.getMobileno());
						dto.setFrd_ts(frdEntity.getCreated_at().getTime());
						dto.setFrom(frdEntity.getFrdfrom());
						dto.setAdn(frdEntity.getAddress_nick_decode());
						dto.setFrdshp(frdEntity.isFrdshp());
						dto.setOw(true);
						retDtos.add(dto);
					}
					cursor++;
				}
			}

			//返回默认系统机器人用户
			List<UserFrdDTO> s_frd_dtos = loadSystemRobotUsers();
			int s_frd_size = s_frd_dtos.size();
			if(s_frd_size > 0){
				retDtos.addAll(s_frd_dtos);
				total = total + s_frd_size;
			}
			
			//userFrdIncrementStateFacadeService.clearUserFrdIncrementState(uid);
			
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(JsonHelper.getPartialData(retDtos, 0, 0, total)));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	/**
	 * 验证mark标示是否匹配，如果匹配返回code码
	 * 如果不匹配，返回全部好友数据
	 * @param request
	 * @param response
	 * @param uid
	 * @param increment_mark_value
	 */
	@ResponseBody()
	@RequestMapping(value="/fetch_all_by_mark",method={RequestMethod.GET,RequestMethod.POST})
	public void fetch_frds_by_mark(HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = false, value = "mark") String increment_mark_value) {
		User user = this.userService.getById(uid);
		validateUserNotNull(user);
		try{
			//判断好友关系标记是否相同, 如果相同不返回数据, 返回code码
			String current_mark = userFrdIncrementStateFacadeService.getIncrementMark(String.valueOf(uid));
			if(current_mark.equals(increment_mark_value)){
				SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.USER_FRD_STATE_NO_CHANGE));
				return;
			}
			List<UserFrdRelation> entityList = userFrdRelationFacadeService.segmentGetUserFrdRelations(uid);
			int total = entityList.size();
			List<UserFrdDTO> retDtos = new ArrayList<UserFrdDTO>();
			if(total > 0){
				List<Integer> frdids = this.fromEntityToUids(entityList);
				List<User> userEntityList = userService.findByIds(frdids, true, true);
				
				UserFrdDTO dto = null;
				UserFrdRelation frdEntity = null;
				int cursor = 0;
				for(User userEntity : userEntityList){
					if(userEntity != null){
						frdEntity = entityList.get(cursor);
						dto = new UserFrdDTO();
						dto.setId(userEntity.getId());
						dto.setNick(userEntity.getNick());
						dto.setAvatar(userEntity.getAvatar());
						dto.setMobileno(userEntity.getMobileno());
						dto.setFrd_ts(frdEntity.getCreated_at().getTime());
						dto.setFrom(frdEntity.getFrdfrom());
						dto.setAdn(frdEntity.getAddress_nick_decode());
						dto.setUt(userEntity.getUtype());
						dto.setFrdshp(frdEntity.isFrdshp());
						dto.setOw(true);
						dto.setLastlogin_ts(userEntity.getLastlogin_at().getTime());
						dto.setZombie(true);
						retDtos.add(dto);
					}
					cursor++;
				}
			}

			//返回默认系统机器人用户
			List<UserFrdDTO> s_frd_dtos = loadSystemRobotUsers();
			int s_frd_size = s_frd_dtos.size();
			if(s_frd_size > 0){
				retDtos.addAll(s_frd_dtos);
				total = total + s_frd_size;
			}
			Map<String,Object> ret_map = new HashMap<String,Object>();
			ret_map.put(BusinessKeyDefine.Present.UserFrdIncrementMarkPrefixKey, current_mark);
			ret_map.put("frds", JsonHelper.getPartialData(retDtos, 0, 0, total));
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(ret_map));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
//	/**
//	 * 获取用户的好友增量状态数据
//	 * @param request
//	 * @param response
//	 * @param uid
//	 */
//	@SuppressWarnings("unchecked")
//	@ResponseBody()
//	@RequestMapping(value="/fetch_increment_state",method={RequestMethod.GET,RequestMethod.POST})
//	public void fetch_increment_state(HttpServletRequest request,
//			HttpServletResponse response, 
//			@RequestParam(required = true) Integer uid) {
//		try{
//			Map<String,Object> retMap = userFrdIncrementStateFacadeService.getUserFrdIncrementState(uid);
//			Object incrementByUpdate = retMap.get(BusinessKeyDefine.Present.UserFrdIncrementType_UpdatePrefixKey);
//			if(incrementByUpdate != null){
//				Set<String> incrementByUpdateSet = (Set<String>)incrementByUpdate;
//				if(!incrementByUpdateSet.isEmpty()){
//					List<Integer> increment_frdids = new ArrayList<Integer>();
//					List<UserFrdPK> increment_frdPks = new ArrayList<UserFrdPK>();
//					for(String frdid : incrementByUpdateSet){
//						int frdid_int = Integer.parseInt(frdid);
//						increment_frdids.add(frdid_int);
//						increment_frdPks.add(new UserFrdPK(uid, frdid_int));
//					}
//					List<UserFrdRelation> frd_list = userFrdRelationService.findByIds(increment_frdPks, true, true);
//					List<User> user_list = userService.findByIds(increment_frdids, true, true);
//					
//					int cursor = 0;
//					UserFrdDTO dto = null;
//					User user_entity = null;
//					List<UserFrdDTO> incrementByUpdateDtos = new ArrayList<UserFrdDTO>();
//					for(UserFrdRelation frd_entity : frd_list){
//						user_entity = user_list.get(cursor);
//						if(user_entity != null){
//							dto = new UserFrdDTO();
//							dto.setId(user_entity.getId());
//							dto.setNick(user_entity.getNick());
//							dto.setAvatar(user_entity.getAvatar());
//							dto.setMobileno(user_entity.getMobileno());
//							dto.setFrd_ts(frd_entity.getCreated_at().getTime());
//							dto.setFrom(frd_entity.getFrdfrom());
//							dto.setAdn(frd_entity.getAddress_nick_decode());
//							dto.setFrdshp(frd_entity.isFrdshp());
//							dto.setOneway(true);
//							incrementByUpdateDtos.add(dto);
//						}
//						cursor++;
//					}
//					
//					retMap.put(BusinessKeyDefine.Present.UserFrdIncrementType_UpdatePrefixKey, incrementByUpdateDtos);
//				}
//			}
//			userFrdIncrementStateFacadeService.clearUserFrdIncrementState(uid);
//			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(retMap));
//		}catch(Exception ex){
//			ex.printStackTrace();
//			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
//		}
//	}
	
	/**
	 * 从好友实体列表提取好友ids
	 * @param entitylist
	 * @return uids
	 */
	protected List<Integer> fromEntityToUids(List<UserFrdRelation> entityList){
		List<Integer> frdids = new ArrayList<Integer>();
		for(UserFrdRelation entity : entityList){
			frdids.add(entity.getFrdid());
		}
		return frdids;
	}
	
	protected List<Integer> fromEntityPKToUids(List<UserFrdPK> entityPkList){
		List<Integer> frdids = new ArrayList<Integer>();
		for(UserFrdPK entityPk : entityPkList){
			frdids.add(entityPk.getFrdid());
		}
		return frdids;
	}
	
	protected List<Integer> fromStringAyToUids(String[] uidstrs){
		if(uidstrs == null || uidstrs.length == 0) return Collections.emptyList();
		
		List<Integer> uids = new ArrayList<Integer>();
		for(String uidstr : uidstrs){
			uids.add(Integer.parseInt(uidstr));
		}
		return uids;
	}
	/**
	 * 加载系统默认好友
	 * @return
	 */
	protected List<UserFrdDTO> loadSystemRobotUsers(){
		/*List<User> s_frds = userService.findByIds(RuntimeConfiguration.SystemDefaultSupportedUserIDs);
		if(s_frds.isEmpty()){
			return Collections.emptyList();
		}*/
		int utype = BusinessEnumType.UserType.SystemRobotUser.getType();
		List<User> systemRobotUsers = userBusinessCacheFacadeService.getCacheUsersByType(utype);
		if(systemRobotUsers.isEmpty()){
			return Collections.emptyList();
		}
		
		List<UserFrdDTO> s_frd_dtos = new ArrayList<UserFrdDTO>();
		UserFrdDTO dto = null;
		for(User s_frd : systemRobotUsers){
			dto = new UserFrdDTO();
			dto.setId(s_frd.getId());
			dto.setNick(s_frd.getNick());
			dto.setAvatar(s_frd.getAvatar());
			dto.setMobileno(s_frd.getMobileno());
			dto.setFrd_ts(0);
			dto.setLastlogin_ts(System.currentTimeMillis());
			dto.setFrom(BusinessEnumType.FrdType.local.getType());
			dto.setOw(true);
			dto.setFrdshp(true);
			dto.setUt(s_frd.getUtype());
			s_frd_dtos.add(dto);
		}
		return s_frd_dtos;
	}
}
