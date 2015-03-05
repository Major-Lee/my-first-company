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

package com.smartwork.multiplexer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartwork.im.listener.ConnectionCloseListener;
import com.smartwork.im.net.Connection;
import com.smartwork.im.net.Session;
import com.smartwork.im.utils.LocaleUtils;
import com.smartwork.multiplexer.spi.ClientFailoverDeliverer;

/**
 * Session that represents a client to server connection.
 *
 * @author Gaston Dombiak
 */
public class ClientSession extends Session {
	private static final Logger logger = LoggerFactory.getLogger(ClientSession.class);
    //private static final String ETHERX_NAMESPACE = "http://etherx.jabber.org/streams";
    //private static final String FLASH_NAMESPACE = "http://www.jabber.com/streams/flash";
    private static ConnectionCloseListener closeListener;

    static {
        closeListener = new ConnectionCloseListener() {
            public void onConnectionClose(Object handback) {
                ClientSession session = (ClientSession) handback;
                // Mark the session as closed
                session.close(false);
            }
        };
    }

    public static Session createSession(String user,String serverName, Connection connection){//String serverName, XmlPullParser xpp, Connection connection) throws XmlPullParserException {
        // Create a ClientSession for this user.
        String streamID = null;
        //if(user == null)
        	//streamID = idFactory.createStreamID();
        //else 
        streamID = user;
        ClientSession session = new ClientSession(serverName, streamID, connection);
        //session.setUserAttachedPayload(payload);
        connection.init(session);
        // Set the stream ID that identifies the client when forwarding traffic to a client fails
        ((ClientFailoverDeliverer) connection.getPacketDeliverer()).setStreamID(streamID);
        // Listen when the connection is closed
        connection.registerCloseListener(closeListener, session);
        // Register that the new session is associated with the specified stream ID
        //Session.addSession(streamID, session);
        SessionManager.getInstance().addSession(streamID, session);
        // Send to the server that a new client session has been created
        return session;
    }

    public ClientSession(String serverName, String streamID, Connection connection) {
        super(serverName, streamID, connection);
    }

    /**
     * Delivers a stanza sent by the server to the client.
     *
     * @param stanza the stanza sent by the server.
     */
    public void deliver(String stanza) {
        // Until session is not authenticated we need to inspect server traffic
        /*if (status != Session.STATUS_AUTHENTICATED) {
            char tag = stanza.charAt(0);
            if ("success".equals(tag)) {
                // Session has been authenticated (using SASL). Update status
                setStatus(Session.STATUS_AUTHENTICATED);
            }
            else if ("failure".equals(tag)) {
                // Sasl authentication has failed
                // Ignore for now
            }
            else if ("challenge".equals(tag)) {
                // A challenge was sent to the client. Client needs to respond
                // Ignore for now
            }
        }*/
        // Deliver stanza to client
        if (conn != null && !conn.isClosed()) {
            try {
                conn.deliver(stanza);//+"\n");
            }
            catch (Exception e) {
                logger.error(LocaleUtils.getLocalizedString("admin.error"), e);
            }
        }
    }

    public void close() {
        close(false);
    }

    /**
     * Closes the client connection. The <tt>systemStopped</tt> parameter indicates if the
     * client connection is being closed because the server is shutting down or unavailable
     * or if it is because the connection manager is being shutdown.
     *
     * @param systemStopped true when the server is no longer available or the
     *        connection manager is being shutdown.
     */
    public void close(boolean systemStopped) {
        if (status != STATUS_CLOSED) {
            // Change the status to closed
            status = STATUS_CLOSED;
            // Close the connection of the client
            if (systemStopped) {
                conn.systemShutdown();
            }
            else  {
                conn.close();
            }
            // Remove session from list of sessions
            SessionManager.getInstance().removeSession(getStreamID());
            //removeSession(getStreamID());
            // Tell the server that the client session has been closed
            CMServer.getInstance().getServerSurrogate().clientSessionClosed(getStreamID(),null);
        }
    }

    public boolean isClosed() {
        return status == STATUS_CLOSED;
    }
}
