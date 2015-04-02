package com.bhu.jorion.ursids;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.jorion.JOrion;
import com.bhu.jorion.message.UrsidsMessage;
import com.bhu.jorion.util.StringHelper;

public class UrsidsIoHandler extends IoHandlerAdapter {
    private final static Logger LOGGER = LoggerFactory.getLogger(UrsidsIoHandler.class);
    private JOrion orion;
    
    public UrsidsIoHandler(JOrion orion){
    	this.orion = orion;
    }
    
    @Override
    public void sessionCreated(IoSession session) {
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        LOGGER.info("CLOSED");
        try{
        	orion.onUrisdsLeave(session);
        }catch(Exception e){
			LOGGER.error(StringHelper.getStackTrace(e));
			e.printStackTrace();
        }
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        LOGGER.info("OPENED");
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) {
        LOGGER.info("*** IDLE #" + session.getIdleCount(IdleStatus.BOTH_IDLE) + " ***");
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
		LOGGER.error(StringHelper.getStackTrace(cause));
        session.close(true);
    }

    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception {
    	try{
    		long start = System.currentTimeMillis();
	    	UrsidsMessage msg = (UrsidsMessage)message;
	    	LOGGER.debug("Got a message from ursids:" + session + "\n" + msg.toString());
	    	orion.onUrsidsMessage(session, msg);
    		LOGGER.debug("handle ursids msg, cost:" + (System.currentTimeMillis() - start));
        }catch(Exception e){
			LOGGER.error(StringHelper.getStackTrace(e));
			e.printStackTrace();
        }
    }
    
    /* not reliable.
    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        // Empty handler
    	try{
    		UrsidsMessage us = (UrsidsMessage)message;
	    	LOGGER.debug("Message sent on ursids:" + session + "\n msgid:" + us.getMsgid());
	    	orion.onUrsidsMessageSent(session, us);
	    }catch(Exception e){
			LOGGER.error(StringHelper.getStackTrace(e));
			e.printStackTrace();
	    }
    }
	*/
}
