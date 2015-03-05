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
package com.smartwork.client.minatest;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

/**
 * {@link IoHandler} implementation of the client side of the simple chat protocol.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class TestClientHandler extends IoHandlerAdapter {

    public interface Callback {
        void connected();

        void loggedIn();

        void loggedOut();

        void disconnected();

        void messageReceived(String message);

        void error(String message);
        
        void append(String message);
        
        void flRequestSuccess(String masterhost);
        
        void flResponseAgree(String masterhost, String message);
        
        String getUser();
    }

    private final Callback callback;

    public TestClientHandler(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        callback.connected();
    }

    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception {
    	System.out.println("["+callback.getUser()+"]~~~~~~~~:recvd:"+message);
    	
    	
/*    	String receivedMessage = message.toString();
    	callback.messageReceived(receivedMessage);
    	
    	InstantModelType instantModelType = InstantMessageBuilder.determineInstantModelType(receivedMessage);
    	if(instantModelType == null) return;
    	InstantType instantType = InstantMessageBuilder.determineInstantType(receivedMessage);
    	if(instantType == null) return;
    	
    	switch(instantType){
	    	case SIGNEDONOK:
	    		callback.loggedIn();
	    		break;
	    	case FOLLOWLISTENRESPONSEOK:
	    		InstantSingleMessage messageFLRequestSuccessDto = JacksonHelper.getDTO(InstantMessageBuilder.determineInstantMessage(receivedMessage), InstantSingleMessage.class);
	    		callback.flRequestSuccess(messageFLRequestSuccessDto.getFrom());
	    		break;
	    	case FOLLOWLISTENAGREE:
	    		InstantSingleMessage messageFLAgreeDto = JacksonHelper.getDTO(InstantMessageBuilder.determineInstantMessage(receivedMessage), InstantSingleMessage.class);
	    		callback.flResponseAgree(messageFLAgreeDto.getFrom(), messageFLAgreeDto.getPayload());
	    		break;	
	    	default:
	    		//this.route(stanza);
	    		break;
		}*/
        /*String theMessage = (String) message;
        String[] result = theMessage.split(" ", 3);
        String status = result[1];
        String theCommand = result[0];
        ChatCommand command = ChatCommand.valueOf(theCommand);

        if ("OK".equals(status)) {

            switch (command.toInt()) {

            case ChatCommand.BROADCAST:
                if (result.length == 3) {
                    callback.messageReceived(result[2]);
                }
                break;
            case ChatCommand.LOGIN:
                callback.loggedIn();
                break;

            case ChatCommand.QUIT:
                callback.loggedOut();
                break;
            case ChatCommand.HEARTBEAT:
                System.out.println("heart beat from server!");
                break;    
            }

        } else {
            if (result.length == 3) {
                callback.error(result[2]);
            }
        }*/
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        callback.disconnected();
    }

}
