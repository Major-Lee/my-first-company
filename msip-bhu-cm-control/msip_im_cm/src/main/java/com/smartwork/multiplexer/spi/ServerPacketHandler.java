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

package com.smartwork.multiplexer.spi;

import com.smartwork.im.message.HintCode;
import com.smartwork.im.message.Message;
import com.smartwork.im.message.MessageType;
import com.smartwork.im.message.common.HintMessage;
import com.smartwork.im.net.Session;
import com.smartwork.im.utils.IMHelper;
import com.smartwork.im.utils.ToDto;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.multiplexer.CMServer;
import com.smartwork.multiplexer.SessionManager;
import com.smartwork.multiplexer.net.SocketConnection;


/**
 * A ServerPacketHandler is responsible for handling stanzas sent from the server. For each
 * server connection there is going to be an instance of this class.<p>
 *
 * Route stanzas are forwarded to clients. IQ stanzas are used when the server wants to
 * close a client connection or wants to update the clients connections configurations.
 * Stream errors with condition <tt>system-shutdown</tt> indicate that the server is
 * shutting down. The connection manager will close existing client connections but
 * will keep running.
 *
 * @author Gaston Dombiak
 */
class ServerPacketHandler {

    //private CMServer server = CMServer.getInstance();

    /**
     * Connection to the server.
     */
    //private SocketConnection connection;
    /**
     * JID that identifies this connection to the server. The address is composed by
     * the connection manager name and the name of the thread. e.g.: connManager1/thread1
     */
    //private String jidAddress;

    public ServerPacketHandler(SocketConnection connection, String jidAddress) {

    }

    
    public boolean validateStanza(String stanza){//, XmlPullParser parser) {
    	if("".equals(stanza) || stanza.length() <6 ) return false;
    	return true;
    }
    /**
     * Handles stanza sent from the server. Route stanzas are forwarded to clients. IQ
     * stanzas are used when the server wants to close a client connection or wants to
     * update the clients connections configurations. Stream errors with condition
     * <tt>system-shutdown</tt> indicate that the server is shutting down. The connection
     * manager will close existing client connections but will keep running.
     *
     * @param stanza stanza sent from the server.
     */
    public void handle(String stanza) {
    	System.out.println(stanza);
    	if(!validateStanza(stanza)) return;
    	Message message = JsonHelper.getDTO(stanza, Message.class);
    	MessageType mtype = MessageType.getByDisplay(message.getMt());
    	if(mtype == null) {
    		System.out.println("unsupported message type:"+ stanza);
    		return;
    	}
    	switch(mtype){
    		case DispatcherServerHaltedRequest:
    			CMServer.getInstance().getServerSurrogate().closeAll();
    			break;
    		case KickSimpleHintMessage:
    			ToDto kickTo = IMHelper.parserTo(message.getTo());
    			Session kickClientSession = SessionManager.getInstance().getSession(kickTo.getU());
    			if(kickClientSession != null && !kickClientSession.isClosed()){
    				kickClientSession.deliver(stanza);
    				kickClientSession.close();
    			}
    			//HintMessage hintMsg = JsonHelper.getDTO(message.getPayload(), HintMessage.class);
    			//if(hintMsg.getCode().equals(HintCode.CertainUserRepeatSignedATSameCM.getCode()))
    			break;
    		default:
    			//String to = message.getTo();
    	    	ToDto to = IMHelper.parserTo(message.getTo());
    			Session clientSession = SessionManager.getInstance().getSession(to.getU());
    			if(clientSession != null && !clientSession.isClosed()){
    				clientSession.deliver(stanza);
    			}else{
    				//deliver fail message with stanza body back to dispatcher
    				CMServer.getInstance().getServerSurrogate().deliveryFailed(stanza, to.getU());
    			}
    			break;
    	}
		
    	/*String[] tos = message.getTo();
    	for(String to : tos){
    		Session clientSession = SessionManager.getInstance().getSession(to);
    		if(clientSession != null && !clientSession.isClosed()){
    			clientSession.deliver(stanza);
    		}else{
    			//TODO:deliver fail message with stanza body back to dispatcher
    		}
    	}*/
    	
    }

    /**
     * Forwards wrapped stanza contained in the <tt>route</tt> element to the specified
     * client. The target client connection is specified in the <tt>route</tt> element by
     * the <tt>streamid</tt> attribute.<p>
     *
     * Wrapped stanzas that failed to be delivered to the target client are returned to
     * the server.
     *
     * @param route the route element containing the wrapped stanza to send to the target
     *        client.
     */
/*    private void processRoute(Element route) {
        String streamID = route.attributeValue("streamid");
        // Get the wrapped stanza
        Element stanza = (Element) route.elementIterator().next();
        // Get the session that matches the requested stream ID
        Session session = Session.getSession(streamID);
        if (session != null && !session.isClosed()) {
            // Deliver the wrapped stanza to the client
            session.deliver(stanza);
        }
        else {
            // Inform the server that the wrapped stanza was not delivered
            String tag = stanza.getName();
            if ("message".equals(tag)) {
                connectionManager.getServerSurrogate().deliveryFailed(stanza, streamID);
            }
            else if ("iq".equals(tag)) {
                String type = stanza.attributeValue("type", "get");
                if ("get".equals(type) || "set".equals(type)) {
                    // Build IQ of type ERROR
                    Element reply = stanza.createCopy();
                    reply.addAttribute("type", "error");
                    reply.addAttribute("from", stanza.attributeValue("to"));
                    reply.addAttribute("to", stanza.attributeValue("from"));
                    Element error = reply.addElement("error");
                    error.addAttribute("type", "wait");
                    error.addElement("unexpected-request")
                            .addAttribute("xmlns", "urn:ietf:params:xml:ns:xmpp-stanzas");
                    // Bounce the failed IQ packet
                    connectionManager.getServerSurrogate().send(reply.asXML(), streamID);
                }
            }
        }
    }*/

    /**
     * Processes server configuration to use for client connections and store the
     * configuration in {@link ServerSurrogate}.
     *
     * @param stanza stanza sent from the server containing the configuration.
     * @param configuration the configuration element contained in the stanza.
     */
/*    private void obtainClientOptions(Element stanza, Element configuration) {
        ServerSurrogate serverSurrogate = connectionManager.getServerSurrogate();
        // Check if TLS is avaiable (and if it is required)
        Element startTLS = configuration.element("starttls");
        if (startTLS != null) {
            if (startTLS.element("required") != null) {
                serverSurrogate.setTlsPolicy(Connection.TLSPolicy.required);
            } else {
                serverSurrogate.setTlsPolicy(Connection.TLSPolicy.optional);
            }
        } else {
            serverSurrogate.setTlsPolicy(Connection.TLSPolicy.disabled);
        }
        // Check if compression is available
        Element compression = configuration.element("compression");
        if (compression != null) {
            serverSurrogate.setCompressionPolicy(Connection.CompressionPolicy.optional);
        } else {
            serverSurrogate.setCompressionPolicy(Connection.CompressionPolicy.disabled);
        }
        // Cache supported SASL mechanisms for client authentication
        Element mechanisms = configuration.element("mechanisms");
        if (mechanisms != null) {
            serverSurrogate.setSASLMechanisms(mechanisms);
        }
        // Check if anonymous login is supported
        serverSurrogate.setNonSASLAuthEnabled(configuration.element("auth") != null);
        // Check if in-band registration is supported
        serverSurrogate.setInbandRegEnabled(configuration.element("register") != null);

        // Send ACK to the server
        Element reply = stanza.createCopy();
        reply.addAttribute("type", "result");
        reply.addAttribute("to", connectionManager.getServerName());
        reply.addAttribute("from", jidAddress);
        connection.deliver(reply.asXML());
    }*/
}

