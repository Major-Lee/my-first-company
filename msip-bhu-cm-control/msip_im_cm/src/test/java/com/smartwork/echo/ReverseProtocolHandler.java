package com.smartwork.echo;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

public class ReverseProtocolHandler extends IoHandlerAdapter { 
	 	@Override  
	    public void exceptionCaught(IoSession session, Throwable cause) {  
	        // Close connection when unexpected exception is caught.  
	        session.close(true);  
	    }  
	  
	    @Override  
	    public void messageReceived(IoSession session, Object message) {  
	        // Reverse reveiced string  
	        String str = message.toString();  
	        System.err.println("收到客户端发来的消息：："+str);  
	        StringBuilder buf = new StringBuilder(str.length());  
	        for (int i = str.length() - 1; i >= 0; i--) {  
	            buf.append(str.charAt(i));  
	        }  
	  
	        // and write it back.  
	        session.write(buf.toString());  
	    }  
}
