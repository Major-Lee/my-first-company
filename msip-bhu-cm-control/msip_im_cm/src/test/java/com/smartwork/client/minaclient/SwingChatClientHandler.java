/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package com.smartwork.client.minaclient;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.smartwork.im.message.Message;
import com.smartwork.im.message.MessageType;
import com.smartwork.msip.cores.helper.JsonHelper;

/**
 * {@link IoHandler} implementation of the client side of the simple chat protocol.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class SwingChatClientHandler extends IoHandlerAdapter {

    public interface Callback {
        void connected();

        void loggedIn();

        void loggedOut();

        void disconnected();

        void messageReceived(String message);

        void error(String message);
        
        void append(String message);
    }

    private final Callback callback;

    public SwingChatClientHandler(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        callback.connected();
    }

    @Override
    public void messageReceived(IoSession session, Object msg)
            throws Exception {
    	//System.out.println("~~~~~~~~:recvd:"+message);
    	String theMessage = msg.toString();
    	//callback.messageReceived(theMessage);
    	
		Message message = JsonHelper.getDTO(theMessage, Message.class);
		MessageType mtype = MessageType.getByDisplay(message.getMt());
		switch(mtype){
			/*case  ChatBroadcastTextMessage:
				ChatBroadcastTextMessage request = JsonHelper.getDTO(message.getPayload(), ChatBroadcastTextMessage.class);
				callback.messageReceived(message.getFrom()+":"+request.getT());
				break;*/
			/*case  ChatPeerMediaMessage:
				ChatMessage prequest = JsonHelper.getDTO(message.getPayload(), ChatMessage.class);
				callback.messageReceived(message.getFrom()+":"+prequest.getT());
				break;	*/
			case CliSignonResponse:
				//CliSignonResponse signonres = JsonHelper.getDTO(message.getPayload(), CliSignonResponse.class);
				callback.loggedIn();
				//SessionCacheFacade.getInstance().addUserLocate(request.getUser(), request.getMark());
				//System.out.println("~~~~~~~~~"+request.getUser()+":"+SessionCacheFacade.getInstance().getUserLocate(request.getUser()));
				//session.removeSession(streamID, sequenceID);
				//this.route(stanza);
				break;
			case CliSignoffResponse:
				//CliSignoffResponse signoffres = JsonHelper.getDTO(message.getPayload(), CliSignoffResponse.class);
				callback.loggedOut();
				//SessionCacheFacade.getInstance().addUserLocate(request.getUser(), request.getMark());
				//System.out.println("~~~~~~~~~"+request.getUser()+":"+SessionCacheFacade.getInstance().getUserLocate(request.getUser()));
				//session.removeSession(streamID, sequenceID);
				//this.route(stanza);
				break;	
			default:
				//this.route(stanza);
				break;
		}
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        callback.disconnected();
    }

}
