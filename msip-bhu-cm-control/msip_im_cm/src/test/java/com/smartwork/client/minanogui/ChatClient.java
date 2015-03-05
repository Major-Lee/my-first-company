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
package com.smartwork.client.minanogui;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.smartwork.client.minanogui.ChatClientHandler.Callback;

/**
 * Simple chat client based on Swing & MINA that implements the chat protocol.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class ChatClient implements Callback {
    private static final long serialVersionUID = 1538675161745436968L;

    private ChatClientSupport client;

    private ChatClientHandler handler;

    private NioSocketConnector connector;
    

    public ChatClient() {
        //super("Chat Client based on Apache MINA");
        connector = new NioSocketConnector();
    }
    
    public boolean connect(){
        handler = new ChatClientHandler(ChatClient.this);
        client = new ChatClientSupport("lawliet", handler);
        
        SocketAddress address = parseSocketAddress("127.0.0.1:5222");
        if (!client.connect(connector, address, false)) {
            return false;
        }else{     	 
        	return true;
        }
    }


    private SocketAddress parseSocketAddress(String s) {
        s = s.trim();
        int colonIndex = s.indexOf(":");
        if (colonIndex > 0) {
            String host = s.substring(0, colonIndex);
            int port = parsePort(s.substring(colonIndex + 1));
            return new InetSocketAddress(host, port);
        } else {
            int port = parsePort(s.substring(colonIndex + 1));
            return new InetSocketAddress(port);
        }
    }

    private int parsePort(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Illegal port number: " + s);
        }
    }
    @Override
    public void connected() {
    	System.out.println("连接成功");
    }
    
    @Override
    public void disconnected() {
        System.out.println("连接断开.\n");
        this.connector.getFilterChain().clear();
        client.forseQuitByServer();
    }
    
    public void doLogin(){
    	client.login();
    }
    
    public void sendMessage(String message){
    	client.broadcast(message);
    	//client.quit();
    }
    
    public void disconnect(){
    	client.quit();
    }
    
    @Override
    public void messageReceived(String message) {
    	System.out.println("收到消息:" + message + "\n");
    	//if(message.equals("Oye login le")){
    		//client.broadcast("test");
    	//this.sendMessage("test");
    	//}
    	//this.sendMessage("Oye login le");
    }

    public static void main(String[] args) throws Exception{
        ChatClient client = new ChatClient();
        boolean connected = client.connect();
        if(connected){
        	//client.doLogin();
        	client.sendMessage("Oye login le");
        	Thread.sleep(2000);
        	client.disconnect();
        	//client.quit();
        }
    }

	@Override
	public void loggedIn() {
		System.out.println("loggedIn callback");
		
	}

	@Override
	public void loggedOut() {
		System.out.println("loggedOut");
	}

	@Override
	public void error(String message) {
		System.out.println("error:"+message);
	}
}
