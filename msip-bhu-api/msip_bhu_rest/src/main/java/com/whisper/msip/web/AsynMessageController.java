package com.whisper.msip.web;


public class AsynMessageController extends ValidatePermissionCheckController{
	
	/*@Resource
	private DeliverSnsMessageService deliverSnsMessageService;*/
	
	/*@Resource
	private UserSnsTokenFacadeService userSnsTokenFacadeService;*/
	
	/**
	 * 验证用户是否有sns执行时间过期的第三方信息
	 * 如果有, 才发送到队列执行
	 * @param uid
	 */
//	public void sendUserSnsJoinMessageByLastActionAt(int uid){
//		Collection<UserSnsTokenDTO> dtos = userSnsTokenFacadeService.getUserSnsTokens(uid);
//		if(!dtos.isEmpty()){
//			List<String> types = null;
//			List<String> auids = null;
//			List<String> access_tokens = null;//new ArrayList<String>()
//			for(UserSnsTokenDTO dto : dtos){
//				boolean expire = userSnsTokenFacadeService.validateExpireSnsTokenLastActionAt(dto, RuntimeConfiguration
//						.UserJoinFrdLastActionAtExpireDays);
//				//如果sns最后执行时间过期,可以发送队列执行
//				if(expire){
//					if(types == null) types = new ArrayList<String>();
//					if(auids == null) auids = new ArrayList<String>();
//					if(access_tokens == null) access_tokens = new ArrayList<String>();
//					
//					types.add(dto.getType());
//					auids.add(dto.getAuid());
//					access_tokens.add(dto.getAccess_token());
//				}
//			}
//			
//			if(types != null){
//				deliverSnsMessageService.sendUserSnsFriendIdsMessages(uid, types, auids, access_tokens);
//			}
//		}
//	}
	
	/**
	 * 发送用户第三方sns提取好友入驻异步消息
	 * @param uid
	 * @param type
	 * @param auid
	 * @param access_token
	 */
//	public void sendUserSnsJoinMessage(int uid, String type, String auid, String access_token){
//		deliverSnsMessageService.sendUserSnsFriendIdsMessage(uid, type, auid, access_token);
//	}
	
}
