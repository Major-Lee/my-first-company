package com.smartwork.im.message;

import java.util.ArrayList;
import java.util.List;

import com.smartwork.im.message.cli2s.CliSignoffRequest;
import com.smartwork.im.message.cli2s.CliSignoffResponse;
import com.smartwork.im.message.cli2s.CliSignonRequest;
import com.smartwork.im.message.cli2s.CliSignonResponse;
import com.smartwork.im.message.cm2s.CmClientSessionAuthenticatedNotifyRequest;
import com.smartwork.im.message.cm2s.CmClientSessionClosedNotifyRequest;
import com.smartwork.im.message.cm2s.CmRegisterRequest;
import com.smartwork.im.message.common.HintMessage;
import com.smartwork.im.message.s2cm.DispatcherServerHaltedRequest;
import com.smartwork.im.message.u2u.BroadcastMessage;
import com.smartwork.im.message.u2u.ChatDTO;
import com.smartwork.im.message.u2u.ChatMessage;
import com.smartwork.im.message.u2u.ChatReadedMessage;
import com.smartwork.im.message.u2u.ChatResponseMessage;
import com.smartwork.im.message.u2u.NotifyCommonMessage;
import com.smartwork.im.message.u2u.NotifyUserFrdApplyMessage;
import com.smartwork.im.message.u2u.NotifyUserFrdIntroMessage;
import com.smartwork.im.message.u2u.NotifyUserFrdJoinMessage;
import com.smartwork.im.message.u2u.NotifyUserFrdRemovedMessage;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;

public class CopyOfMessageBuilder {
	public static Message builderDispatcherServerHaltedRequest(String mark){
		Message message = new Message();
		message.setFrom(null);
		message.setTo(null);//new String[]{to});
		DispatcherServerHaltedRequest request = new DispatcherServerHaltedRequest();
		request.setServerMark(mark);
		request.setTs(System.currentTimeMillis());
		message.setPayload(JsonHelper.getJSONString(request));
		message.setMt(MessageType.DispatcherServerHaltedRequest.getDisplay());
		return message;
	}
	
	public static Message builderCliRegisterMessage(String from,String to,String user,String pwd){
		Message message = new Message();
		message.setFrom(from);
		return message;
	}
	
	public static Message builderCliSignonRequestMessage(String from,String to,String pwd){
		Message message = new Message();
		message.setFrom(from);
		message.setTo(to);//new String[]{to});
		CliSignonRequest request = new CliSignonRequest();
		request.setUser(from);
		request.setPwd(pwd);
		message.setPayload(JsonHelper.getJSONString(request));
		message.setMt(MessageType.CliSignonRequest.getDisplay());
		return message;
	}
	
	public static Message builderCliSignonReponseMessage(String to){
		Message message = new Message();
		message.setFrom(to);
		message.setTo(to);//new String[]{to});
		CliSignonResponse request = new CliSignonResponse();
		request.setUser(to);
		message.setPayload(JsonHelper.getJSONString(request));
		message.setMt(MessageType.CliSignonResponse.getDisplay());
		return message;
	}
	
	public static Message builderCliSignoffRequestMessage(String from){
		Message message = new Message();
		message.setFrom(from);
		CliSignoffRequest request = new CliSignoffRequest();
		request.setUser(from);
		message.setPayload(JsonHelper.getJSONString(request));
		message.setMt(MessageType.CliSignoffRequest.getDisplay());
		return message;
	}
	
	public static Message builderCliSignoffResponseMessage(String from){
		Message message = new Message();
		message.setFrom(from);
		CliSignoffResponse request = new CliSignoffResponse();
		request.setUser(from);
		message.setPayload(JsonHelper.getJSONString(request));
		message.setMt(MessageType.CliSignoffResponse.getDisplay());
		return message;
	}
	
	public static Message builderCliBroadcastMessage(String from,String tos,String mt,String body,String dur){
		Message message = new Message();
		message.setFrom(from);
		BroadcastMessage textm = new BroadcastMessage();
		textm.setTos(tos);
		textm.setMt(mt);
		textm.setBody(body);
		textm.setDur(dur);
		message.setPayload(JsonHelper.getJSONString(textm));
		message.setMt(MessageType.BroadcastMessage.getDisplay());
		return message;
	}
	
	/*public static Message builderCliPeerMessage(String from,String to,String tid,String text){
		Message message = new Message();
		message.setTo(new String[]{to});
		message.setFrom(from);
		ChatTextMessage textm = new ChatTextMessage();
		textm.setTid(tid);
		textm.setT(text);
		message.setPayload(JsonHelper.getJSONString(textm));
		message.setMt(MessageType.ChatPeerTextMessage.getDisplay());
		return message;
	}*/
	
	public static Message builderCliPeerMediaMessage(String from,String to,String cid,char mediaType,String body){
		return builderCliPeerMediaMessage(from,to,cid,mediaType,body,StringHelper.EMPTY_STRING_GAP);
	}
	
	public static Message builderCliPeerMediaMessage(String from,String to,String cid,char mediaType,String body,String dur){
		Message message = new Message();
		message.setTo(to);//new String[]{to});
		message.setFrom(from);
		ChatMessage textm = new ChatMessage();
		textm.setCid(cid);
		textm.setDto(ChatDTO.builder(mediaType,from, body, System.currentTimeMillis()));
		/*textm.setBody(body);
		textm.setT(mediaType);
		textm.setTs(System.currentTimeMillis());*/
		message.setPayload(JsonHelper.getJSONString(textm));
		message.setMt(MessageType.ChatPeerMediaMessage.getDisplay());
		return message;
	}
	
	/*public static Message builderCliPeerMediaRefMessage(String from,String to,String cid,char mediaType,String body,String dur,String ref){
		Message message = new Message();
		message.setTo(to);//new String[]{to});
		message.setFrom(from);
		ChatMessage textm = new ChatMessage();
		textm.setCid(cid);
		textm.setDto(ChatDTO.builderWithRef(mediaType,from, body,dur, System.currentTimeMillis(),ref));
		textm.setBody(body);
		textm.setT(mediaType);
		textm.setTs(System.currentTimeMillis());
		message.setPayload(JsonHelper.getJSONString(textm));
		message.setMt(MessageType.ChatPeerMediaMessage.getDisplay());
		return message;
	}*/
	
	public static Message builderCliPeerMediaRefMessage(String from,String to,String cid/*,boolean isContext*/,char mediaType,String body,String dur,String ref){
		Message message = new Message();
		message.setTo(to);//new String[]{to});
		message.setFrom(from);
		ChatMessage textm = new ChatMessage();
		textm.setCid(cid);
		//textm.setC(isContext);
		textm.setDto(ChatDTO.builderWithRef(mediaType,from, body,dur, System.currentTimeMillis(),ref));
		/*textm.setBody(body);
		textm.setT(mediaType);
		textm.setTs(System.currentTimeMillis());*/
		message.setPayload(JsonHelper.getJSONString(textm));
		message.setMt(MessageType.ChatPeerMediaMessage.getDisplay());
		return message;
	}
	
	public static Message builderCliPeerMediaReadedMessage(String from,String to,String cid){
		Message message = new Message();
		message.setTo(to);//new String[]{to});
		message.setFrom(from);
		ChatReadedMessage textm = new ChatReadedMessage();
		textm.setCid(cid);
		textm.setTs(System.currentTimeMillis());
		message.setPayload(JsonHelper.getJSONString(textm));
		message.setMt(MessageType.ChatReadedMessage.getDisplay());
		return message;
	}
	
	
	public static Message builderCliPeerChatResponseMessage(String from,String to,String cid,List<String> bodies){
		Message message = new Message();
		message.setTo(to);//new String[]{to});
		message.setFrom(from);
		ChatResponseMessage textm = new ChatResponseMessage();
		textm.setCid(cid);
		textm.setBodies(bodies);
		message.setPayload(JsonHelper.getJSONString(textm));
		message.setMt(MessageType.ChatPeerMediaMessage.getDisplay());
		return message;
	}
	
	/*public static Message builderCliGroupMessage(String from,String group,String cid,char mediaType,String body){
		Message message = new Message();
		message.setTo("@"+group);//.setG(group);
		message.setFrom(from);
		ChatMessage textm = new ChatMessage();
		textm.setCid(cid);
		textm.setDto(ChatDTO.builder(mediaType, body, System.currentTimeMillis()));
		textm.setBody(body);
		textm.setT(mediaType);
		message.setPayload(JsonHelper.getJSONString(textm));
		message.setMt(MessageType.ChatGroupMediaMessage.getDisplay());
		return message;
	}*/
	
	public static Message builderCmRegisterRequestMessage(String from,String to,
			String mark,String pwd){
		Message message = new Message();
		message.setFrom(from);
		message.setTo(to);//new String[]{to});
		CmRegisterRequest request = new CmRegisterRequest();
		request.setMark(mark);
		request.setPwd(pwd);
		message.setPayload(JsonHelper.getJSONString(request));
		message.setMt(MessageType.CmRegisterRequest.getDisplay());
		return message;
	}
	
	public static Message builderCmClientSessionAuthenticatedNotifyRequestMessage(String from,String to,
			String mark,String user,String pwd,String ip){
		Message message = new Message();
		message.setFrom(from);
		message.setTo(to);//new String[]{to});
		CmClientSessionAuthenticatedNotifyRequest request = new CmClientSessionAuthenticatedNotifyRequest();
		request.setUser(user);
		request.setPwd(pwd);
		request.setIp(ip);
		request.setMark(mark);
		message.setPayload(JsonHelper.getJSONString(request));
		message.setMt(MessageType.CmClientSessionAuthenticatedNotifyRequest.getDisplay());
		return message;
	}
	
	public static Message builderCmClientSessionClosedNotifyRequestMessage(String from,String to,
			String mark,String user,String ip){
		Message message = new Message();
		message.setFrom(from);
		message.setTo(to);//new String[]{to});
		CmClientSessionClosedNotifyRequest request = new CmClientSessionClosedNotifyRequest();
		request.setUser(user);
		request.setIp(ip);
		request.setMark(mark);
		message.setPayload(JsonHelper.getJSONString(request));
		message.setMt(MessageType.CmClientSessionClosedNotifyRequest.getDisplay());
		return message;
	}
	
	
	public static Message builderSimpleHintMessage(String from,String to,HintCode hcode){
		Message message = new Message();
		message.setFrom(from);
		message.setTo(to);//new String[]{to});
		HintMessage request = new HintMessage();
		request.setCode(hcode.getCode());
		request.setHint(hcode.getHint());
		message.setPayload(JsonHelper.getJSONString(request));
		message.setMt(MessageType.KickSimpleHintMessage.getDisplay());
		return message;
	}
	
	/*public static Message builderNotifyMessageWithPayload(String from,String to,String payload){
		Message message = new Message();
		message.setTo(new String[]{to});
		message.setFrom(from);
		message.setPayload(payload);
		message.setMt(MessageType.ChatPeerTextMessage.getDisplay());
		return message;
	}*/
	
/*	public static Message builderNotifyTopicAnswerMessage(String from,String to,
			String tid,String townernick,String title,String auid,String anick,String avatar,boolean correct){
		Message message = new Message();
		message.setFrom(from);
		message.setTo(new String[]{to});
		NotifyTopicAnswerMessage request = new NotifyTopicAnswerMessage();
		request.setTid(tid);
		request.setT(title);
		request.setC(correct);
		request.setTn(townernick);
		request.setAuid(auid);
		request.setAn(anick);
		request.setAv(avatar);
		message.setPayload(JsonHelper.getJSONString(request));
		message.setMt(MessageType.NotifyAnswerActionMessage.getDisplay());
		return message;
	}
	
	public static Message builderNotifyTopicReviveMessage(String from,String to,
			String tid,String townernick,String title,String auid,String anick,String avatar,boolean correct){
		Message message = new Message();
		message.setFrom(from);
		message.setTo(new String[]{to});
		NotifyTopicReviveMessage request = new NotifyTopicReviveMessage();
		request.setTid(tid);
		request.setT(title);
		request.setC(correct);
		request.setTn(townernick);
		request.setAuid(auid);
		request.setAn(anick);
		request.setAv(avatar);
		message.setPayload(JsonHelper.getJSONString(request));
		message.setMt(MessageType.NotifyReviveActionMessage.getDisplay());
		return message;
	}*/
	
	public static Message builderNotifyCommonMessage(String from,String to,String msg){
		Message message = new Message();
		message.setFrom(from);
		message.setTo(to);//new String[]{to});
		NotifyCommonMessage request = new NotifyCommonMessage();
		request.setBody(msg);
		request.setTs(System.currentTimeMillis());
		message.setPayload(JsonHelper.getJSONString(request));
		message.setMt(MessageType.NotifyCommonActionMessage.getDisplay());
		return message;
	}
	
	public static Message builderNotifyUserFrdApplyMessage(String from, String to, String auid, String anick, 
			String avatar, String amobileno){
		Message message = new Message();
		message.setFrom(from);
		message.setTo(to);//new String[]{to});
		NotifyUserFrdApplyMessage request = new NotifyUserFrdApplyMessage();
		request.setAuid(auid);
		request.setAn(anick);
		request.setAv(avatar);
		request.setAmno(amobileno);
		message.setPayload(JsonHelper.getJSONString(request));
		message.setMt(MessageType.NotifyUserFrdApplyActionMessage.getDisplay());
		return message;
	}
	
//	public static Message builderNotifyUserFrdJoinMessage(String from, String to, String frdid, String frdnick, 
//			String frdavatar, String frdmobileno, String frd_from){
//		Message message = new Message();
//		message.setFrom(from);
//		message.setTo(to);//new String[]{to});
//		NotifyUserFrdJoinMessage request = new NotifyUserFrdJoinMessage();
//		request.setFrdid(frdid);
//		request.setFn(frdnick);
//		request.setFv(frdavatar);
//		request.setFmn(frdmobileno);
//		request.setFrom(frd_from);
//		request.setFrd_ts(System.currentTimeMillis());
//		message.setPayload(JsonHelper.getJSONString(request));
//		message.setMt(MessageType.NotifyUserFrdJoinActionMessage.getDisplay());
//		return message;
//	}
	
	public static Message builderNotifyUserFrdJoinMessage(String from, String to, String frdid, String frd_nick, 
			String frd_avatar, String frd_mobileno, String address_nick, String frd_from){
		return builderNotifyUserFrdJoinMessage(from,to,new String[]{frdid},new String[]{frd_nick},new String[]{frd_avatar}
			,new String[]{frd_mobileno}, new String[]{address_nick}, frd_from);
	}
	
	public static Message builderNotifyUserFrdJoinMessage(String from, String to, String[] frd_uid_ay, String[] frd_nick_ay, 
			String[] frd_avatar_ay, String[] frd_mobileno_ay, String[] address_nick_ay, String frd_from){
		Message message = new Message();
		message.setFrom(from);
		message.setTo(to);//new String[]{to});
		
		int length = frd_uid_ay.length;
		List<NotifyUserFrdJoinMessage> request_list = new ArrayList<NotifyUserFrdJoinMessage>();
		long frd_ts = System.currentTimeMillis();
		for(int i=0;i<length;i++){
			NotifyUserFrdJoinMessage request = new NotifyUserFrdJoinMessage();
			request.setFrdid(frd_uid_ay[i]);
			request.setFn(frd_nick_ay[i]);
			request.setFv(frd_avatar_ay[i]);
			request.setFmn(frd_mobileno_ay[i]);
			request.setFrom(frd_from);
			request.setFrd_ts(frd_ts);
			request.setAdn(address_nick_ay[i]);
			request_list.add(request);
		}
		
		message.setPayload(JsonHelper.getJSONString(request_list));
		message.setMt(MessageType.NotifyUserFrdJoinActionMessage.getDisplay());
		return message;
	}

	
	public static Message builderNotifyUserFrdRemovedMessage(String from, String to, String frdid){
		Message message = new Message();
		message.setFrom(from);
		message.setTo(to);//new String[]{to});
		NotifyUserFrdRemovedMessage request = new NotifyUserFrdRemovedMessage();
		request.setFrdid(frdid);
		message.setPayload(JsonHelper.getJSONString(request));
		message.setMt(MessageType.NotifyUserFrdRemovedActionMessage.getDisplay());
		return message;
	}
	
	public static Message builderNotifyUserFrdIntroMessage(String from, String to, String[] intro_uid_ay, String[] intro_nick_ay, 
			String[] intro_avatar_ay, String[] intro_mobileno_ay){
		Message message = new Message();
		message.setFrom(from);
		message.setTo(to);//new String[]{to});
		
//		String[] intro_uid_ay = intro_uids.split(StringHelper.COMMA_STRING_GAP);
//		String[] intro_nick_ay = intro_nicks.split(StringHelper.COMMA_STRING_GAP);
//		String[] intro_avatar_ay = intro_avatars.split(StringHelper.COMMA_STRING_GAP);
//		String[] intro_mobileno_ay = intro_mobilenos.split(StringHelper.COMMA_STRING_GAP);
		int length = intro_uid_ay.length;
		List<NotifyUserFrdIntroMessage> request_list = new ArrayList<NotifyUserFrdIntroMessage>();
		for(int i=0;i<length;i++){
			NotifyUserFrdIntroMessage request = new NotifyUserFrdIntroMessage();
			request.setIuid(intro_uid_ay[i]);
			request.setIn(intro_nick_ay[i]);
			request.setIv(intro_avatar_ay[i]);
			request.setImno(intro_mobileno_ay[i]);
			request_list.add(request);
		}
		message.setPayload(JsonHelper.getJSONString(request_list));
		message.setMt(MessageType.NotifyUserFrdIntroActionMessage.getDisplay());
		return message;
	}
	
	
	public static String builderHeartBeatMessage(){
		return JsonHelper.EmptyJsonString;
	}
	public static Message builderCertainMessageWithPayload(String from,String to,MessageType type,String payload){
		Message message = new Message();
		message.setTo(to);//new String[]{to});
		message.setFrom(from);
		message.setPayload(payload);
		message.setMt(type.getDisplay());
		return message;
	}
	
	public static void main(String[] argv){
		Message message = CopyOfMessageBuilder.builderCliPeerMediaReadedMessage("100012", "200048", "20140712141819bwjP200048-200037.zip");
		System.out.println(JsonHelper.getJSONString(message));
		
		message = CopyOfMessageBuilder.builderCliBroadcastMessage("100012", "200048,200049,200050",MessageType.ChatPeerMediaMessage.getDisplay(), "20140712141819bwjP100012-200048.zip","0.0");
		System.out.println(JsonHelper.getJSONString(message));
		
	}
}
