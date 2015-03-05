package com.smartwork.echo;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

public class ReverseClientHandler extends IoHandlerAdapter{
    @Override  
    public void exceptionCaught(IoSession session, Throwable cause)  
            throws Exception  
    {  
        super.exceptionCaught(session, cause);  
    }  
  
    @Override  
    public void messageReceived(IoSession session, Object message)  
            throws Exception  
    {  
        String msg = (String)message;  
        System.out.println("Server sent message :"+msg);  
        session.write(msg);  
    }  
  
    @Override  
    public void sessionClosed(IoSession session) throws Exception  
    {  
        session.close(true);  
    }  
  
    @Override  
    public void sessionCreated(IoSession session) throws Exception  
    {  
        super.sessionCreated(session);  
    }  
  
    @Override  
    public void sessionOpened(IoSession session) throws Exception  
    {  
        session.write("我是好人");  
    }  
}
