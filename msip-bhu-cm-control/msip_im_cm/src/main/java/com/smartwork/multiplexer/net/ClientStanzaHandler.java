/**
 * $RCSfile$
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Public License (GPL),
 * a copy of which is included in this distribution.
 */

package com.smartwork.multiplexer.net;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartwork.im.ipacket.PacketRouter;
import com.smartwork.im.message.HintCode;
import com.smartwork.im.message.Message;
import com.smartwork.im.message.MessageBuilder;
import com.smartwork.im.message.MessageType;
import com.smartwork.im.message.cli2s.CliSignoffRequest;
import com.smartwork.im.message.cli2s.CliSignonRequest;
import com.smartwork.im.net.Connection;
import com.smartwork.im.net.Session;
import com.smartwork.im.net.StanzaHandler;
import com.smartwork.im.utils.JingGlobals;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.multiplexer.CMServer;
import com.smartwork.multiplexer.ClientSession;
import com.smartwork.multiplexer.SessionManager;

/**
 * Handler of XML stanzas sent by clients.
 *
 * @author Gaston Dombiak
 */
class ClientStanzaHandler extends StanzaHandler {
	private static final Logger logger = LoggerFactory.getLogger(ClientStanzaHandler.class);
    public ClientStanzaHandler(PacketRouter router, String serverName, Connection connection){//throws XmlPullParserException {
        super(router, serverName, connection);
    }

    public String getNamespace() {
        return "jabber:client";
    }

    public boolean validateHost() {
        return JingGlobals.getBooleanProperty("cm.client.validate.host",false);
    }

    public boolean createSession(String user,String serverName, Connection connection){//String namespace, String serverName, XmlPullParser xpp, Connection connection) throws XmlPullParserException {
        /*if ("jabber:client".equals(namespace)) {
            // The connected client is a regular client so create a ClientSession
            session = ClientSession.createSession(serverName, xpp, connection);
            return true;
        }*/
    	try{
    		session = ClientSession.createSession(user,serverName, connection);
            return true;
    	}catch(Exception ex){
    		ex.printStackTrace();
    		return false;
    	}
        //return false;
    }
    
    @Override
    public void process(String stanza){
    	//logger.info(stanza);
    	//System.out.println("1 StanzaHandler process:"+stanza);
    	if(!validateStanza(stanza)) return;
    	
       	Message message = JsonHelper.getDTO(stanza, Message.class);
    	MessageType mtype = MessageType.getByDisplay(message.getMt());
    	if(mtype == null) {
    		System.out.println("unsupported message type:"+ stanza);
    		return;
    	}
    	//如果连接没有验证通过 并且是非CliSignonRequest消息，则直接断开长连接
    	if(mtype != MessageType.CliSignonRequest && !isSessionCreated()){
    		this.getConnection().deliver(JsonHelper.getJSONString(MessageBuilder.builderSimpleHintMessage(message.getFrom(), message.getFrom(), HintCode.IllegalMessageInteractive)));
    		this.getConnection().close();
    		return;
    	}
    	
    	switch(mtype){
    		case  CliSignonRequest:
    			CliSignonRequest request = JsonHelper.getDTO(message.getPayload(), CliSignonRequest.class);
    			if(isSessionCreated()){
                    if (session.getStreamID() != null) {
                    	if(session.getStreamID().equals(request.getUser())){
                    		//session.deliver("LOGIN ERROR user " + user + " already logged in.\n");
                    		this.getConnection().deliver(JsonHelper.getJSONString(MessageBuilder.builderSimpleHintMessage(request.getUser(), request.getUser(), HintCode.CertainUserRepeatSignedWithSameConnectionATSameCM)));//"LOGIN ERROR the name " + request.getUser()+ " is already used.");
                        	return;
                    	}else{
                    		this.getConnection().deliver(JsonHelper.getJSONString(MessageBuilder.builderSimpleHintMessage(request.getUser(), request.getUser(), HintCode.DiffenentUserSignedWithSameConnectionATSameCM)));
                        	return;
                    	}
                    }
            	}else{
            		if(StringHelper.isEmpty(request.getUser()) || StringHelper.isEmpty(request.getPwd())){//登录用户名密码数据为空
            			this.getConnection().deliver(JsonHelper.getJSONString(MessageBuilder.builderSimpleHintMessage(request.getUser(), request.getUser(), HintCode.UsernameAndPwdValidWhenUserSigned)));//"LOGIN ERROR the name " + request.getUser()+ " is already used.");
            			this.getConnection().close();
            			return;
            		}
            		boolean isReg = true;//IegalTokenHashService.getInstance().validateUserToken(request.getPwd(),request.getUser());
            		if(!isReg){
            			this.getConnection().deliver(JsonHelper.getJSONString(MessageBuilder.builderSimpleHintMessage(request.getUser(), request.getUser(), HintCode.CertainUserPwdValidError)));//"LOGIN ERROR the name " + request.getUser()+ " is already used.");
            			this.getConnection().close();
            			return;
            		}
            		
            		if (SessionManager.getInstance().containsUser(request.getUser())) {//后登录的踢出前登录
            			System.out.println(request.getUser()+" already in sessions! then kick off another session");
            			Session otherSession = SessionManager.getInstance().getSession(request.getUser());
            			otherSession.deliver(JsonHelper.getJSONString(MessageBuilder.builderSimpleHintMessage(request.getUser(), request.getUser(), HintCode.CertainUserRepeatSignedWithDifferentConnectionATSameCM)));
            			otherSession.close();
            			/*//此时session实例还未创建，因此使用connection进行发送
            			//this.getConnection().deliver(JsonHelper.getJSONString(MessageBuilder.builderSimpleHintMessage(request.getUser(), request.getUser(), HintCode.AlreadySignedIn)));//"LOGIN ERROR the name " + request.getUser()+ " is already used.");
            			//this.getConnection().close();
            			//session.deliver("LOGIN ERROR the name " + request.getUser()+ " is already used.");
                        //return;*/
                    }
            		createSession(request.getUser());
                    //sessionCreated = true;
            		this.setSessionCreated(true);
                    session.setStatus(Session.STATUS_AUTHENTICATED);
                    //session.setUserAttachedPayload(request.getUser());//.setUser(user);
                    InetAddress address = null;
                    try {
                        address = this.getConnection().getInetAddress();
                    } catch (UnknownHostException ex) {
                        // Do nothing
                    	ex.printStackTrace(System.out);
                    } catch(Exception ex){
                    	// Do nothing
                    	ex.printStackTrace(System.out);
                    }
                    CMServer.getInstance().getServerSurrogate().clientSessionAuthenticated(request.getUser(), address);
                    System.out.println("auth ok!");
                    session.deliver(JsonHelper.getJSONString(MessageBuilder.builderCliSignonReponseMessage(request.getUser())));
            	}
    			break;
    		case CliSignoffRequest:
    			CliSignoffRequest offrequest = JsonHelper.getDTO(message.getPayload(), CliSignoffRequest.class);
                session.deliver(JsonHelper.getJSONString(MessageBuilder.builderCliSignoffResponseMessage(offrequest.getUser())));
                session.close();
    			break;
    		default:
    			//BusinessMsgProcess4CM.process(mtype, stanza, message, session, this);
    			break;
    		/*case ChatPeerMediaMessage:
    			if(StringHelper.isNotEmpty(message.getTo())){
    				ChatMessage chatMsg = JsonHelper.getDTO(message.getPayload(), ChatMessage.class);
    				if(chatMsg.wasContextMsg()){
    					chatMsg.setC(true);
    					if(StringHelper.isEmpty(chatMsg.getCid())){
    						chatMsg.setCid(MsgIDGenHelper.generate(message.getFrom(), message.getTo(),MsgIDGenHelper.MsgID_PeerPrefix));
    						BusinessDefinedLogger.doCidLogger(BusinessDefinedLogger.CID_ACT_GEN, chatMsg.getCid());
    					}else{
    						BusinessDefinedLogger.doCidLogger(BusinessDefinedLogger.CID_ACT_UP, chatMsg.getCid());
    					}
    				}
    				if(JingGlobals.getBooleanProperty(SmartXmlKeys.Share.contentFilterWord, false) && chatMsg.getDto().getT() == ChatMessage.Type_Text){
						chatMsg.getDto().setBody(WordFilterHelper.filterHtml(chatMsg.getDto().getBody(), StringHelper.STAR_CHAR_GAP).getFilteredContent());
						message.setPayload(JsonHelper.getJSONString(chatMsg));
    				}
					if(JingConstants.isSystemSupportedUsers(message.getTo())){//对于机器人，目前直接返回随机内容
						MessageProcessor4CM.messagePeerMediaChat2RobotFromInstantChannel(message, chatMsg, session);
					}else{
						Session sessionTo =  SessionManager.getInstance().getSession(message.getTo());
    					if(sessionTo != null && !sessionTo.isClosed()){//在线 online
							List<String> history_contextMsg = null;
							String chatMsgDTO_json = JsonHelper.getJSONString(chatMsg.getDto());
							try{
								history_contextMsg = UserContextMessageService.getInstance().userContextMessageElementAll(chatMsg.getCid());
								UserContextMessageService.getInstance().userContextMessageElementComming(chatMsg.getCid(), chatMsgDTO_json,chatMsg.wasContextMsg());
							}catch(Exception ex){
								ex.printStackTrace(System.out);
								history_contextMsg = new ArrayList<String>();
							}
							history_contextMsg.add(chatMsgDTO_json);
							sessionTo.deliver(JsonHelper.getJSONString(
									MessageBuilder.builderCliPeerChatResponseMessage(message.getFrom(), message.getTo(), chatMsg.getCid(), history_contextMsg)));
    					}else{//offline
    						this.route(JsonHelper.getJSONString(message));
    					}
					}
    			}
    			break;
    		case ChatReadedMessage:
    			if(StringHelper.isNotEmpty(message.getTo())){
    				//if(IMHelper.isPeer2Peer(message.getTo())){
					if(JingConstants.isSystemSupportedUsers(message.getTo())){//对于机器人，则不做任何处理
						;
					}else{
						Session sessionTo =  SessionManager.getInstance().getSession(message.getTo());
    					if(sessionTo != null && !sessionTo.isClosed()){//在此cm在线 online 直接转发
							session.deliver(stanza);
    					}else{//不在此cm，转递到dispatcher
    						this.route(JsonHelper.getJSONString(message));
    					}
					}
    				//}
    			}
    			break;
    		case ChatGroupMediaMessage://goup消息，这类型消息直接传递到dispatcher server
    			this.route(stanza);
    			break;
    		case ChatDeprecatedMediaMessage:
    			break;
    		case BroadcastDeprecatedMessage:
    			break;
    		default:
    			this.route(stanza);
    			break;*/
    	}
    }

	@Override
	public Logger getLogger() {
		return logger;
	}
}
