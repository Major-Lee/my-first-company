package com.bhu.vas.business.asyn.web.builder;

import org.apache.commons.lang.StringUtils;

import com.smartwork.msip.cores.helper.JsonHelper;

public class ActionMessageFactoryBuilder {
	
//	public static ActionDTO fromJsonHasPrefix(String messagejsonHasPrefix){
//		if(StringUtils.isEmpty(messagejsonHasPrefix)) return null;
//    	String prefix = messagejsonHasPrefix.substring(0,1);
//    	ActionMessageType type = ActionMessageType.fromPrefix(prefix);
//		if(type == null) return null;
//		ActionDTO dto = null;
//		switch(type){
//			case BADGECMBT:
//				dto = JsonHelper.getDTO(messagejsonHasPrefix.substring(2), BadgeCmbtDTO.class);
//				break;
//			case MUSICHEARD:
//				dto = JsonHelper.getDTO(messagejsonHasPrefix.substring(2), MusicHeardDTO.class);
//				break;
//			case MUSICLISTENING:
//				dto = JsonHelper.getDTO(messagejsonHasPrefix.substring(2), MusicListeningDTO.class);
//				break;
//			case MUISCLOVE:
//				dto = JsonHelper.getDTO(messagejsonHasPrefix.substring(2), MusicLoveDTO.class);
//				break;
//			case MUSICHATE:
//				dto = JsonHelper.getDTO(messagejsonHasPrefix.substring(2), MusicHateDTO.class);
//				break;
//			case SNSBINDED:
//				dto = JsonHelper.getDTO(messagejsonHasPrefix.substring(2), SnsBindedDTO.class);
//				break;
//			case USERATTE:
//				dto = JsonHelper.getDTO(messagejsonHasPrefix.substring(2), FriendActDTO.class);	
//				break;
//			case USERSIGNEDON:
//				dto = JsonHelper.getDTO(messagejsonHasPrefix.substring(2), UserSignedonDTO.class);	
//				break;
//			case USERNICKUPD:
//				dto = JsonHelper.getDTO(messagejsonHasPrefix.substring(2), UserNickUpdDTO.class);	
//				break;
//			case USERAVATARUPD:
//				dto = JsonHelper.getDTO(messagejsonHasPrefix.substring(2), UserAvatarUpdDTO.class);	
//				break;	
//			case ATEDUSER:
//				dto = JsonHelper.getDTO(messagejsonHasPrefix.substring(2), UserAtedDTO.class);	
//				break;
//			case REGISTERED:
//				dto = JsonHelper.getDTO(messagejsonHasPrefix.substring(2), UserRegisteredDTO.class);	
//				break;	
//			/*case TASKACCEPT:
//				dto = JsonHelper.getDTO(messagejsonHasPrefix.substring(2), UserTaskAcceptDTO.class);	
//				break;	*/
//			case USERBLACK:
//				dto = JsonHelper.getDTO(messagejsonHasPrefix.substring(2), BlackActDTO.class);	
//				break;
//			case USERCHATPAYLOAD:
//				dto = JsonHelper.getDTO(messagejsonHasPrefix.substring(2), UserChatPayloadDTO.class);	
//				break;
//			case USERMAILCONTACT:
//				dto = JsonHelper.getDTO(messagejsonHasPrefix.substring(2), UserMailContactDTO.class);	
//				break;
//			case USERNOTIFYDCMBT:
//				dto = JsonHelper.getDTO(messagejsonHasPrefix.substring(2), UserNotifydCmbtDTO.class);	
//				break;	
//			case USERRESETPWD:
//				dto = JsonHelper.getDTO(messagejsonHasPrefix.substring(2), UserResetPwdDTO.class);	
//				break;	
//			/*case PROFESSIONALMUSICACCEPT:
//				dto = JsonHelper.getDTO(messagejsonHasPrefix.substring(2), UserProfessionalMusicDTO.class);	
//				break;	*/
//			case USERREMIND:
//				dto = JsonHelper.getDTO(messagejsonHasPrefix.substring(2), FriendRemindDTO.class);	
//				break;	
//			case ALIKECMBT:
//				dto = JsonHelper.getDTO(messagejsonHasPrefix.substring(2), AlikeCmbtDTO.class);	
//				break;	
//			case USERFRIENDRELATION:
//				dto = JsonHelper.getDTO(messagejsonHasPrefix.substring(2), UserFriendRelationActDTO.class);	
//				break;
//			case USERFOLLOWREQUEST:
//				dto = JsonHelper.getDTO(messagejsonHasPrefix.substring(2), UserFollowRequestDTO.class);	
//				break;
//			case USERFOLLOWSUCCESS:
//				dto = JsonHelper.getDTO(messagejsonHasPrefix.substring(2), UserFollowSuccessDTO.class);	
//				break;
//			default:
//				throw new UnsupportedOperationException("not yet implement");
//		}
//		return dto;
//	}
	
	public static ActionMessageType determineActionType(String messagejsonHasPrefix){
		if(StringUtils.isEmpty(messagejsonHasPrefix)) return null;
    	String prefix = messagejsonHasPrefix.substring(0,2);
    	ActionMessageType type = ActionMessageType.fromPrefix(prefix);
    	return type;
	}
	public static String determineActionMessage(String messagejsonHasPrefix){
		if(StringUtils.isEmpty(messagejsonHasPrefix)) return null;
    	return messagejsonHasPrefix.substring(2);
	}
/*	public static ActionDTO fromJson(String messagejson){
		if(StringUtils.isEmpty(messagejson)) return null;
		
		return JsonHelper.getDTO(messagejson, DeliverMessage.class);
	}	*/
	public static String toJsonHasPrefix(ActionDTO message){
		StringBuilder sb = new StringBuilder();
		sb.append(message.getActionType()).append(toJson(message));
		return sb.toString();
	}
	public static String toJson(ActionDTO message){
		return JsonHelper.getJSONString(message,false);
	}
	
}
