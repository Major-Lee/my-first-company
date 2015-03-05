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

import java.util.Date;

/**
 * The session represents a connection between the server and a client (c2s) or
 * another server (s2s) as well as a connection with a component. Authentication and
 * user accounts are associated with c2s connections while s2s has an optional authentication
 * association but no single user user.<p>
 *
 * Obtain object managers from the session in order to access server resources.
 *
 * @author Gaston Dombiak
 */
public abstract class Session {

    /**
     * Version of the XMPP spec supported as MAJOR_VERSION.MINOR_VERSION (e.g. 1.0).
     */
    /*public static final int MAJOR_VERSION = 1;
    public static final int MINOR_VERSION = 0;*/

    /**
     * The utf-8 charset for decoding and encoding Jabber packet streams.
     */
    protected static String CHARSET = "UTF-8";

    public static final int STATUS_CLOSED = -1;
    public static final int STATUS_CONNECTED = 1;
    //public static final int STATUS_STREAMING = 2;
    public static final int STATUS_AUTHENTICATED = 3;

    /**
     * The stream id for this session (cm id).
     */
    private String streamID;
    /**
     * the sequence id for this session ( sequence id 同一cm下给session编号)
     */
    private String sequenceID;
    //private boolean authenticated = false;
    //private String user;
    /**
     * The current session status.
     */
    protected int status = STATUS_CONNECTED;

    /**
     * The connection that this session represents.
     */
    protected Connection conn;

    private String serverName;

    private Date startDate = new Date();

    /**
     * Map of existing sessions. A session is added just after the initial stream header
     * was processed. Key: stream ID, value: the session.
     */
    //private static Map<String, Session> sessions = new ConcurrentHashMap<String, Session>();

    //private static SessionCacheImpl sessions = new SessionCacheImpl(20);
    
    //public static StreamIDFactory idFactory = new StreamIDFactory();

/*    public static void addSession(String streamID, Session session) {
    	SessionManager.getInstance().addSession(streamID, session);
        //sessions.put(streamID, session);
    }

    public static void removeSession(String streamID,String sequenceID) {
    	SessionManager.getInstance().removeSession(streamID, sequenceID);
        //sessions.remove(streamID);
    }*/
    
    /**
	 * Returns the session whose stream ID matches the specified stream ID.
	 *
	 * @param streamID the stream ID of the session to look for.
	 * @return the session whose stream ID matches the specified stream ID.
	 */
/*	public static Session getSession(String streamID) {
		return SessionManager.getInstance().getSession(streamID);
	    //return sessions.get(streamID);
	}*/
    
    /*public static void broadcast(String message) {
        synchronized (sessions) {
        	Iterator<String> iter = sessions.keySet().iterator();
        	while(iter.hasNext()){
        		String key = iter.next();
        		Session session = sessions.get(key);
        		session.deliver("BROADCAST OK " + message);
        	}
        	Iterator<Entry<String, Session>> iter = sessions..entrySet().iterator();
        	while(iter.hasNext()){
        		Entry<String, Session> next = iter.next();
        		Session session = next.getValue();
        		session.deliver("BROADCAST OK " + message);
        	}
        }
    }*/
    /*public static boolean containsUser(String user){
    	return sessions.get(user) != null;
    }*/
    
    /**
     * Closes connections of connected clients since the server or the connection
     * manager is being shut down. If the server is the one that is being shut down
     * then the connection manager will keep running and will try to establish new
     * connections to the server (on demand).
     */
    /*public static void closeAll() {
        for (List<Session> sessions : SessionManager.getInstance().getSessions().values()) {
        	for(Session session:sessions){
        		session.close(true);
        	}
        }
    }*/

    /**
     * Creates a session with an underlying connection and permission protection.
     * @param serverName
     * @param streamID connection manager cm id
     * @param connection The connection we are proxying
     */
    public Session(String serverName, String streamID, Connection connection) {
        conn = connection;
        this.streamID = streamID;
        this.serverName = serverName;
    }
    public Session(String serverName, String streamID,String sequenceID, Connection connection) {
        conn = connection;
        this.streamID = streamID;
        this.sequenceID = sequenceID;//SessionManager.getInstance().assignSessionSequenceID(streamID);
        this.serverName = serverName;
    }

    /**
     * Obtain the current status of this session.
     *
     * @return The status code for this session
     */
    public int getStatus() {
        return status;
    }

    /**
     * Set the new status of this session. Setting a status may trigger
     * certain events to occur (setting a closed status will close this
     * session).
     *
     * @param status The new status code for this session
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Obtain the stream ID associated with this sesison. Stream ID's are generated by the server
     * and should be unique and random.
     *
     * @return This session's assigned stream ID
     */
    public String getStreamID() {
        return streamID;
    }

    /**
     * Obtain the name of the server this session belongs to.
     *
     * @return the server name.
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * Obtain the date the session was created.
     *
     * @return the session's creation date.
     */
    public Date getCreationDate() {
        return startDate;
    }

    /**
     * Returns a text with the available stream features. Each subclass may return different
     * values depending whether the session has been authenticated or not.
     *
     * @return a text with the available stream features or <tt>null</tt> to add nothing.
     */
    //public abstract String getAvailableStreamFeatures();

    /**
     * Indicate the server that the session has been closed. Do nothing if the session
     * was the one that originated the close action.
     */
    public abstract void close();

    public abstract void close(boolean isServerShuttingDown);

    public abstract void deliver(String stanza);

    public String toString() {
        return super.toString() + " status: " + status + " id: " + streamID;
    }

    protected static int[] decodeVersion(String version) {
        int[] answer = new int[] {0 , 0};
        String [] versionString = version.split("\\.");
        answer[0] = Integer.parseInt(versionString[0]);
        answer[1] = Integer.parseInt(versionString[1]);
        return answer;
    }

    public abstract boolean isClosed();

	public String getSequenceID() {
		return sequenceID;
	}
	public void setSequenceID(String sequenceID) {
		this.sequenceID = sequenceID;
	}
	public void setStreamID(String streamID) {
		this.streamID = streamID;
	}

	
	
	/*public boolean isAuthenticated() {
		return authenticated;
	}

	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}*/

/*	public String getUserAttachedPayload() {
		return userAttachedPayload;
	}

	public void setUserAttachedPayload(String userAttachedPayload) {
		this.userAttachedPayload = userAttachedPayload;
	}*/

	/*public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}*/
    
	public static String infos_template = "StID:[%s] SeID[%s] Server[%s] Status[%s] Date[%s]";
	public String infos(){
		return String.format(infos_template, this.streamID,this.sequenceID,this.serverName,this.status,this.startDate);
	}
    
}