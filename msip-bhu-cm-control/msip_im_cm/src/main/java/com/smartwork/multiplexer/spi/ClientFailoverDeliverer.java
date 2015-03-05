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

import com.smartwork.im.ipacket.PacketDeliverer;
import com.smartwork.multiplexer.CMServer;

/**
 * Deliverer to use when a stanza received from the server failed to be forwarded
 * to a client. The deliverer will inform the server of the failed operation.
 *
 * @author Gaston Dombiak
 */
public class ClientFailoverDeliverer implements PacketDeliverer {

    private ServerSurrogate serverSurrogate = CMServer.getInstance().getServerSurrogate();
    private String streamID;

    public void setStreamID(String streamID) {
        this.streamID = streamID;
    }

    public void deliver(String stanza) {
    	serverSurrogate.deliveryFailed(stanza, streamID);
        // Inform the server that the wrapped stanza was not delivered
        /*char tag = stanza.charAt(0);
        if ('E' == tag) {
            serverSurrogate.deliveryFailed(stanza, streamID);
        }
        else{
        	serverSurrogate.send(stanza, streamID);
        }*/
    }
}
