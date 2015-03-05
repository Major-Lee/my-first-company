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
package com.smartwork.client.gsocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.smartwork.client.gsocket.SwingChatClientHandler.Callback;
import com.smartwork.im.message.Message;
import com.smartwork.im.message.MessageBuilder;
import com.smartwork.im.message.MessageType;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.msg.MsgIDGenHelper;

/**
 * A simple chat client for a given user.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class ChatClientSupport {

    private final String name;
    private final String atoken;
    protected BufferedReader socketReader;  
    protected PrintWriter socketWriter; 
    Callback callback;
    Socket conn;
    public ChatClientSupport(String name,String atoken,Callback callback) {
        if (name == null) {
            throw new IllegalArgumentException("Name can not be null");
        }
        this.name = name;
        this.atoken = atoken;
        this.callback = callback;
    }

    public boolean connect(InetSocketAddress address,boolean useSsl) {
    	
    	NetworkConfig config = new NetworkConfig();
		config.setConnectTimeout(2000*100);
		SyncMissianProxyFactory factory = new SyncMissianProxyFactory(config,new CommonSocketPool(config));
		try {
			conn = factory.createSocket(address);
			socketReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));  
            socketWriter = new PrintWriter(conn.getOutputStream());  
            login();
            new Thread(new ChatReceiveMessageRunnable(socketReader,callback)).start();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
/*        if (session != null && session.isConnected()) {
            throw new IllegalStateException(
                    "Already connected. Disconnect first.");
        }

        try {
            IoFilter LOGGING_FILTER = new LoggingFilter();

            IoFilter CODEC_FILTER = new ProtocolCodecFilter(
                    new TextLineCodecFactory());
            
            connector.getFilterChain().addLast("mdc", new MdcInjectionFilter());
            connector.getFilterChain().addLast("codec", CODEC_FILTER);
            connector.getFilterChain().addLast("logger", LOGGING_FILTER);

            if (useSsl) {
                SSLContext sslContext = BogusSslContextFactory
                        .getInstance(false);
                SslFilter sslFilter = new SslFilter(sslContext);
                sslFilter.setUseClientMode(true);
                connector.getFilterChain().addFirst("sslFilter", sslFilter);
            }

            connector.setHandler(handler);
            ConnectFuture future1 = connector.connect(address);
            future1.awaitUninterruptibly();
            if (!future1.isConnected()) {
                return false;
            }
            session = future1.getSession();
            login();

            return true;
        } catch (Exception e) {
            return false;
        }*/
    }

    public void login() {
    	Message message = MessageBuilder.builderCliSignonRequestMessage(name, null, atoken);
    	socketWriter.write(JsonHelper.getJSONString(message)+"\n");
    	//socketWriter.write("LOGIN " + name+"\n");
    	socketWriter.flush();
    }

    
    public void send(String to,String cid,boolean isContext,boolean isT,String content) {
    	if(to.indexOf(',') !=-1){
    		Message message = MessageBuilder.builderCliBroadcastMessage(name,to,MessageType.ChatPeerMediaMessage.getDisplay(),content,"");
    		socketWriter.write(JsonHelper.getJSONString(message)+"\n");
        	socketWriter.flush();
    	}else{
    		String msgid = MsgIDGenHelper.generate(name, to,MsgIDGenHelper.MsgID_PeerPrefix);
        	//String[] messages = textmessage.split(":");
    		//Message message = MessageBuilder.builderCliPeerMediaMessage(name,messages[0],messages[1].charAt(0), messages[2]);//builderCliSignonRequestMessage(name, null, "1234");
    		Message message = MessageBuilder.builderCliPeerMediaMessage(name, to,msgid,isContext, isT?'T':'Z', content);
        	socketWriter.write(JsonHelper.getJSONString(message)+"\n");
        	socketWriter.flush();
    	}
    	
    	
    	/*char prefix  = textmessage.charAt(0);
    	if(prefix == 'A'){
    		socketWriter.write(textmessage.substring(1)+"\n");
        	socketWriter.flush();
        	return;
    	}
    	
    	if(textmessage.indexOf(":") != -1){
    		String[] messages = textmessage.split(":");
    		//Message message = MessageBuilder.builderCliPeerMediaMessage(name,messages[0],messages[1].charAt(0), messages[2]);//builderCliSignonRequestMessage(name, null, "1234");
    		Message message = MessageBuilder.builderCliPeerMediaMessage(name, messages[0],"2014070515hmz.zip", messages[1].charAt(0), messages[2]);
        	socketWriter.write(JsonHelper.getJSONString(message)+"\n");
        	socketWriter.flush();
    	}else{
    		Message message = MessageBuilder.builderCliBroadcastMessage(name, textmessage);//builderCliSignonRequestMessage(name, null, "1234");
        	socketWriter.write(JsonHelper.getJSONString(message)+"\n");
        	//socketWriter.write("BROADCAST " + message+"\n");
        	socketWriter.flush();
            //session.write("BROADCAST " + message);
    	}*/
    }
    
    public void broadcast(String textmessage) {
    	char prefix  = textmessage.charAt(0);
    	if(prefix == 'A'){
    		socketWriter.write(textmessage.substring(1)+"\n");
        	socketWriter.flush();
        	return;
    	}
    	
    	/*if(textmessage.indexOf(":") != -1){
    		String[] messages = textmessage.split(":");
    		//Message message = MessageBuilder.builderCliPeerMediaMessage(name,messages[0],messages[1].charAt(0), messages[2]);//builderCliSignonRequestMessage(name, null, "1234");
    		Message message = MessageBuilder.builderCliPeerMediaMessage(name, messages[0],"2014070515hmz.zip", messages[1].charAt(0), messages[2]);
        	socketWriter.write(JsonHelper.getJSONString(message)+"\n");
        	socketWriter.flush();
    	}*//*else{
    		Message message = MessageBuilder.builderCliBroadcastMessage(name, textmessage);//builderCliSignonRequestMessage(name, null, "1234");
        	socketWriter.write(JsonHelper.getJSONString(message)+"\n");
        	//socketWriter.write("BROADCAST " + message+"\n");
        	socketWriter.flush();
            //session.write("BROADCAST " + message);
    	}*/
    }

    public void quit() {
        if (conn != null) {
            if (conn.isConnected()) {
            	Message message = MessageBuilder.builderCliSignoffRequestMessage(name);//builderCliBroadcastMessage(name, textmessage);
            	socketWriter.write(JsonHelper.getJSONString(message)+"\n");
            	//socketWriter.write("QUIT"+"\n");
            	socketWriter.flush();
                // Wait until the chat ends.
                //session.getCloseFuture().awaitUninterruptibly();
            }
            try {
				conn.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
    }

}
