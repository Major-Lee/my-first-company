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

import java.net.SocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.SocketConnector;

import com.smartwork.client.minatest.TestClientHandler.Callback;
//.JacksonHelper;

/**
 * A simple chat client for a given user.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class ChatClientSupport {
    private final IoHandler handler;

    private String username;
    private IoSession session;
    
    private Callback callback;

    public ChatClientSupport(String username, IoHandler handler, Callback callback) {
        if (username == null) {
            throw new IllegalArgumentException("Name can not be null");
        }
        this.username = username;
        this.handler = handler;
        this.callback = callback;
    }

    public boolean connect(SocketConnector connector, SocketAddress address,
            boolean useSsl) {
        if (session != null && session.isConnected()) {
            throw new IllegalStateException(
                    "Already connected. Disconnect first.");
        }

        try {
            IoFilter LOGGING_FILTER = new LoggingFilter();
            TextLineCodecFactory lineCodec = new TextLineCodecFactory(Charset.forName("UTF-8"), 
            		LineDelimiter.UNIX.getValue(),LineDelimiter.UNIX.getValue());
            lineCodec.setDecoderMaxLineLength(4*1024);//4k
            lineCodec.setEncoderMaxLineLength(4*1024);//4k
            IoFilter CODEC_FILTER = new ProtocolCodecFilter(lineCodec);//new TextLineCodecFactory());//
            //connector.getFilterChain().addLast("mdc", new MdcInjectionFilter());
            connector.getFilterChain().addLast("codec", CODEC_FILTER);
            connector.getFilterChain().addLast("logger", LOGGING_FILTER);

            connector.setHandler(handler);
            ConnectFuture future1 = connector.connect(address);
            future1.awaitUninterruptibly();
            if (!future1.isConnected()) {
                return false;
            }
            session = future1.getSession();
            //login();

            return true;
        } catch (Exception e) {
        	e.printStackTrace(System.out);
            return false;
        }
    }

    /*public void login() {
    	SignedonPayload payload = new SignedonPayload();
    	payload.setClient("android");
    	payload.setClientid(String.valueOf(System.currentTimeMillis()));
    	
    	InetAddress addr = null;
    	String ip = "127.0.0.1";
		try {
			addr = InetAddress.getLocalHost();
			ip = addr.getHostAddress();//获得本机IP
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
    	
    	payload.setIp(ip);
    	payload.setToken("MgAdVh5RUlRRQkBIQV5cV1hZXFI=");
    	String loginMessage = InstantMessageBuilder.buildLoginMessage(username, JacksonHelper.toJSON(payload));
        //System.out.println(loginMessage);
    	send(loginMessage);
    }*/

    public void send(String message) {
        session.write(message);//+LineDelimiter.UNIX.getValue());
        callback.append("send:"+message+".\n");
    }

    public void forseQuitByServer(){
    	if (session != null) {
            session.close(true);
            
            session = null;
        }
    }
    public void quit() {
        if (session != null) {
            if (session.isConnected()) {
                session.write("QUIT");
                // Wait until the chat ends.
                session.getCloseFuture().awaitUninterruptibly();
            }
            session.close(true);
        }
    }
    


}
