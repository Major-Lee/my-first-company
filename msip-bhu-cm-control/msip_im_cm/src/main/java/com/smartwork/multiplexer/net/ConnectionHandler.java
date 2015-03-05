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

import java.io.IOException;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartwork.im.ipacket.PacketRouter;
import com.smartwork.im.message.MessageBuilder;
import com.smartwork.im.net.Connection;
import com.smartwork.im.net.NIOConnection;
import com.smartwork.im.net.StanzaHandler;
import com.smartwork.multiplexer.CMServer;
import com.smartwork.multiplexer.spi.ServerRouter;

/**
 * A ConnectionHandler is responsible for creating new sessions, destroying sessions and delivering
 * received XML stanzas to the proper StanzaHandler.
 *
 * @author Gaston Dombiak
 */
public abstract class ConnectionHandler extends IoHandlerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(ConnectionHandler.class);
    /**
     * The utf-8 charset for decoding and encoding Jabber packet streams.
     */
    static final String CHARSET = "UTF-8";
    //static final String XML_PARSER = "XML-PARSER";
    private static final String HANDLER = "HANDLER";
    private static final String CONNECTION = "CONNECTION";
    //private static final String CONNECTIME = "CONNECTIME";
    protected static PacketRouter router = new ServerRouter();
    protected static String serverName = CMServer.getInstance().getServerName();
    //private static Map<Integer, XmlPullParser> parsers = new ConcurrentHashMap<Integer, XmlPullParser>();
    /**
     * Reuse the same factory for all the connections.
     */
    //private static XmlPullParserFactory factory = null;

    static {
        /*try {
            factory = XmlPullParserFactory.newInstance(MXParser.class.getName(), null);
            factory.setNamespaceAware(true);
        }
        catch (XmlPullParserException e) {
            Log.error("Error creating a parser factory", e);
        }*/
    }
    
    public static String IoSessionInfo(IoSession session){
    	StringBuilder sb = new StringBuilder();
    	if(session != null && session.isConnected()){
    		NIOConnection connection = (NIOConnection) session.getAttribute(CONNECTION);
    		if(connection.getSession() != null)
    			sb.append(connection.getSession().infos());
    		sb.append(" remote:").append(session.getRemoteAddress().toString());
    	}else{
    		//
    		sb.append("session is null");
    	}
    	return sb.toString();
    }

    public void sessionOpened(IoSession session) throws Exception {
    	
        // Create a new XML parser for the new connection. The parser will be used by the XMPPDecoder filter.
        //XMLLightweightParser parser = new XMLLightweightParser(CHARSET);
        //session.setAttribute(XML_PARSER, parser);
        // Create a new NIOConnection for the new session
        NIOConnection connection = createNIOConnection(session);
        session.setAttribute(CONNECTION, connection);
        session.setAttribute(HANDLER, createStanzaHandler(connection));
        int idleTime = this.getMaxIdleTimeWhenSessionOpened();
        session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, idleTime);//20秒
        logger.info("Session Opened:"+ IoSessionInfo(session));
        // Set the max time a connection can be idle before closing it
        /*int idleTime = getMaxIdleTime();
        if (idleTime > 0) {
        	//session.get
            session.setIdleTime(IdleStatus.READER_IDLE, idleTime);
        }*/
    }

    public void sessionClosed(IoSession session) throws Exception {
    	logger.info("Session Closed:"+ IoSessionInfo(session));
        // Get the connection for this session
        Connection connection = (Connection) session.getAttribute(CONNECTION);
        // Inform the connection that it was closed
        connection.close();
    }

    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
    	logger.info("Session Idle:"+ IoSessionInfo(session));
        // Get the connection for this session
        Connection connection = (Connection) session.getAttribute(CONNECTION);
        //logger.info("Session Idle:"+ connection.g);//("Session Idle:");
        System.out.println("Session Idle:"+connection.hasAuthSession());
        if(connection.hasAuthSession()){
        	connection.deliver(MessageBuilder.builderHeartBeatMessage());//+LineDelimiter.UNIX.getValue());
        	int idleTime = this.getMaxIdleTimeAfterSessionAuthorized();
            session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, idleTime);
        	//if(session.getConfig().getIdleTime(IdleStatus.BOTH_IDLE) == )
        }else{
        	//Session clientSession = (Connection) session.getAttribute(CONNECTION);
            // Close idle connection
            if (logger.isDebugEnabled()) {
            	logger.debug("Closing connection that has been idle: " + connection);
            }
            connection.close();
        }
        
    }

    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        if (cause instanceof IOException) {
            // TODO Verify if there were packets pending to be sent and decide what to do with them
        	logger.debug(cause.getMessage(),cause);
        }
        else if (cause instanceof ProtocolDecoderException) {
        	logger.warn("Closing session due to exception: " + session, cause);
            session.close(true);
        }
        else {
        	logger.error(cause.getMessage(),cause);
        }
    }

    public void messageReceived(IoSession session, Object message) throws Exception {
    	logger.info("RCVD: " + message);// +" info:"+IoSessionInfo(session));
    	//logger.info("RCVD: " + IoSessionInfo(session));
        //System.out.println("RCVD: " + message);
        // Get the stanza handler for this session
        StanzaHandler handler = (StanzaHandler) session.getAttribute(HANDLER);
        // Get the parser to use to process stanza. For optimization there is going
        // to be a parser for each running thread. Each Filter will be executed
        // by the Executor placed as the first Filter. So we can have a parser associated
        // to each Thread
        //int hashCode = Thread.currentThread().hashCode();
        /*XmlPullParser parser = parsers.get(hashCode);
        if (parser == null) {
            parser = factory.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
            parsers.put(hashCode, parser);
        }*/

        // Let the stanza handler process the received stanza
        try {
            handler.process( (String) message);//, parser);
        } catch (Exception e) {
        	logger.error("Closing connection due to error while processing message: " + message, e);
            Connection connection = (Connection) session.getAttribute(CONNECTION);
            connection.close();
        }
    }

    abstract NIOConnection createNIOConnection(IoSession session);

    abstract StanzaHandler createStanzaHandler(NIOConnection connection);// throws XmlPullParserException;

    /**
     * Returns the max number of seconds a connection can be idle (both ways) before
     * being closed.<p>
     *
     * @return the max number of seconds a connection can be idle.
     */
    abstract int getMaxIdleTimeAfterSessionAuthorized();
    abstract int getMaxIdleTimeWhenSessionOpened();
}
