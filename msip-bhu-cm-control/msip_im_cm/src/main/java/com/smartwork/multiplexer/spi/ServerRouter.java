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

import com.smartwork.im.ipacket.PacketRouter;
import com.smartwork.multiplexer.CMServer;

/**
 * Packet router that will route all traffic to the server.
 *
 * @author Gaston Dombiak
 */
public class ServerRouter implements PacketRouter {

    private ServerSurrogate serverSurrogate;

    public ServerRouter() {
        serverSurrogate = CMServer.getInstance().getServerSurrogate();
    }

    public void route(String stanza, String streamID) {
    	System.out.println("Server Router route:"+stanza+" streamID:"+streamID);
        serverSurrogate.send(stanza, streamID);
    }
}
