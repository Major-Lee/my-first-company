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

package com.smartwork.im.net;

import org.slf4j.Logger;

import com.smartwork.im.StreamError;
import com.smartwork.im.ipacket.PacketRouter;
/*import com.et.msip.business.instant.InstantModelType;
import com.et.msip.business.instant.InstantType;
import com.et.msip.business.instant.builder.InstantMessageBuilder;
import com.et.msip.business.instant.message.InstantSingleIdentifierMessage;
import com.et.msip.cores.helper.JacksonHelper;*/

/**
 * A StanzaHandler is the main responsible for handling incoming stanzas. Some stanzas like startTLS
 * are totally managed by this class. Other stanzas are just forwarded to the server.
 *
 * @author Gaston Dombiak
 */
public abstract class StanzaHandler {
	//private static final Logger logger = LoggerFactory.getLogger(StanzaHandler.class);
    /**
     * The utf-8 charset for decoding and encoding Jabber packet streams.
     */
    protected static String CHARSET = "UTF-8";
    /**
     * Reuse the same factory for all the connections.
     */
    //private static XmlPullParserFactory factory = null;

    private Connection connection;

    // DANIELE: Indicate if a session is already created
    private boolean sessionCreated = false;

    //private boolean sessionValidated = false;
    
    // Flag that indicates that the client requested to use TLS and TLS has been negotiated. Once the
    // client sent a new initial stream header the value will return to false.
/*    private boolean startedTLS = false;
    // Flag that indicates that the client requested to be authenticated. Once the
    // authentication process is over the value will return to false.
    private boolean startedSASL = false;

    // DANIELE: Indicate if a stream:stream is arrived to complete compression
    private boolean waitingCompressionACK = false;*/

    /**
     * Session associated with the socket reader.
     */
    protected Session session;
    /**
     * Server name for which we are attending clients.
     */
    private String serverName;

    /**
     * Router used to route incoming packets to the correct channels.
     */
    private PacketRouter router;

    static {
    }

    public abstract Logger getLogger();
    /**
     * Creates a dedicated reader for a socket.
     *
     * @param router the router for sending packets that were read.
     * @param serverName the name of the server this socket is working for.
     * @param connection the connection being read.
     * @throws org.xmlpull.v1.XmlPullParserException of an error while parsing occurs.
     */
    public StanzaHandler(PacketRouter router, String serverName, Connection connection){// throws XmlPullParserException {
        this.serverName = serverName;
        this.router = router;
        this.connection = connection;
    }
    
    public abstract void process(String stanza);
    
    public boolean validateStanza(String stanza){//, XmlPullParser parser) {
    	if("".equals(stanza) || stanza.charAt(0)!='{' || stanza.length() <6 ) return false;
    	return true;
    }

    public void route(String stanza) {
    	System.out.println("route : " + router + ":" + session);
        router.route(stanza, session.getStreamID());
    }

    /**
     * Close the connection since TLS was mandatory and the entity never negotiated TLS. Before
     * closing the connection a stream error will be sent to the entity.
     */
    void closeNeverSecuredConnection() {
        // Set the not_authorized error
        StreamError error = new StreamError(StreamError.Condition.not_authorized);
        // Deliver stanza
        connection.deliverRawText(error.toXML());
        // Close the underlying connection
        connection.close();
        // Log a warning so that admins can track this case from the server side
        getLogger().warn("TLS was required by the server and connection was never secured. " + "Closing connection : " + connection);
    }

    /**
     * Uses the XPP to grab the opening stream tag and create an active session
     * object. The session to create will depend on the sent namespace. In all
     * cases, the method obtains the opening stream tag, checks for errors, and
     * either creates a session or returns an error and kills the connection.
     * If the connection remains open, the XPP will be set to be ready for the
     * first packet. A call to next() should result in an START_TAG state with
     * the first packet in the stream.
     * @param xpp
     * @throws java.io.IOException
     * @throws org.xmlpull.v1.XmlPullParserException
     */
    protected void createSession(String streamID){//XmlPullParser xpp) throws XmlPullParserException, IOException {

    	if (!createSession(streamID,serverName, connection)) {
            // No session was created because of an invalid namespace prefix so answer a stream
            // error and close the underlying connection
            /*StringBuilder sb = new StringBuilder(250);
            sb.append("<?xml version='1.0' encoding='");
            sb.append(CHARSET);
            sb.append("'?>");
            // Append stream header
            sb.append("<stream:stream ");
            sb.append("from=\"").append(serverName).append("\" ");
            sb.append("id=\"").append(StringUtils.randomString(5)).append("\" ");
            sb.append("xmlns=\"").append(xpp.getNamespace(null)).append("\" ");
            sb.append("xmlns:stream=\"").append(xpp.getNamespace("stream")).append("\" ");
            sb.append("version=\"1.0\">");*/
            // Include the bad-namespace-prefix in the response
            StreamError error = new StreamError(StreamError.Condition.bad_namespace_prefix);
            //sb.append(error.toXML());
            connection.deliverRawText(error.toString());
            // Close the underlying connection
            connection.close();
            // Log a warning so that admins can track this cases from the server side
            getLogger().warn("Closing session due to bad_namespace_prefix in stream header. Prefix: " + ". Connection: " + connection);
        }
    	/*        for (int eventType = xpp.getEventType(); eventType != XmlPullParser.START_TAG;) {
            eventType = xpp.next();
        }

        // Check that the TO attribute of the stream header matches the server name or a valid
        // subdomain. If the value of the 'to' attribute is not valid then return a host-unknown
        // error and close the underlying connection.
        String host = xpp.getAttributeValue("", "to");
        if (validateHost() && isHostUnknown(host)) {
            StringBuilder sb = new StringBuilder(250);
            sb.append("<?xml version='1.0' encoding='");
            sb.append(CHARSET);
            sb.append("'?>");
            // Append stream header
            sb.append("<stream:stream ");
            sb.append("from=\"").append(serverName).append("\" ");
            sb.append("id=\"").append(StringUtils.randomString(5)).append("\" ");
            sb.append("xmlns=\"").append(xpp.getNamespace(null)).append("\" ");
            sb.append("xmlns:stream=\"").append(xpp.getNamespace("stream")).append("\" ");
            sb.append("version=\"1.0\">");
            // Set the host_unknown error
            StreamError error = new StreamError(StreamError.Condition.host_unknown);
            sb.append(error.toXML());
            // Deliver stanza
            connection.deliverRawText(sb.toString());
            // Close the underlying connection
            connection.close();
            // Log a warning so that admins can track this cases from the server side
            Log.warn("Closing session due to incorrect hostname in stream header. Host: " + host +
                    ". Connection: " + connection);
        }

        // Create the correct session based on the sent namespace. At this point the server
        // may offer the client to secure the connection. If the client decides to secure
        // the connection then a <starttls> stanza should be received
        else if (!createSession(xpp.getNamespace(null), serverName, xpp, connection)) {
            // No session was created because of an invalid namespace prefix so answer a stream
            // error and close the underlying connection
            StringBuilder sb = new StringBuilder(250);
            sb.append("<?xml version='1.0' encoding='");
            sb.append(CHARSET);
            sb.append("'?>");
            // Append stream header
            sb.append("<stream:stream ");
            sb.append("from=\"").append(serverName).append("\" ");
            sb.append("id=\"").append(StringUtils.randomString(5)).append("\" ");
            sb.append("xmlns=\"").append(xpp.getNamespace(null)).append("\" ");
            sb.append("xmlns:stream=\"").append(xpp.getNamespace("stream")).append("\" ");
            sb.append("version=\"1.0\">");
            // Include the bad-namespace-prefix in the response
            StreamError error = new StreamError(StreamError.Condition.bad_namespace_prefix);
            sb.append(error.toXML());
            connection.deliverRawText(sb.toString());
            // Close the underlying connection
            connection.close();
            // Log a warning so that admins can track this cases from the server side
            Log.warn("Closing session due to bad_namespace_prefix in stream header. Prefix: " +
                    xpp.getNamespace(null) + ". Connection: " + connection);
        }*/
    }

    /*private boolean isHostUnknown(String host) {
        if (host == null) {
            // Answer false since when using server dialback the stream header will not
            // have a TO attribute
            return false;
        }
        if (serverName.equals(host)) {
            // requested host matched the server name
            return false;
        }
        return true;
    }*/

    /**
     * Returns the stream namespace. (E.g. jabber:client, jabber:server, etc.).
     *
     * @return the stream namespace.
     */
    public abstract String getNamespace();

    /**
     * Returns true if the value of the 'to' attribute in the stream header should be
     * validated. If the value of the 'to' attribute is not valid then a host-unknown error
     * will be returned and the underlying connection will be closed.
     *
     * @return true if the value of the 'to' attribute in the initial stream header should be
     *         validated.
     */
    public abstract boolean validateHost();

    /**
     * Creates the appropriate {@link Session} subclass based on the specified namespace.
     *
     * @param namespace the namespace sent in the stream element. eg. jabber:client.
     * @param serverName
     * @param xpp
     * @param connection
     * @return the created session or null.
     * @throws org.xmlpull.v1.XmlPullParserException
     */
    public abstract boolean createSession(String user, String serverName, Connection connection);

	public boolean isSessionCreated() {
		return sessionCreated;
	}
	public void setSessionCreated(boolean sessionCreated) {
		this.sessionCreated = sessionCreated;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	public PacketRouter getRouter(){
		return router;
	}
    
}
