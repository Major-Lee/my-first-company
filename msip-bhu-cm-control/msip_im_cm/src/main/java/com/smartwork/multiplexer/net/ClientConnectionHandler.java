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

import org.apache.mina.core.session.IoSession;

import com.smartwork.im.net.NIOConnection;
import com.smartwork.im.net.StanzaHandler;
import com.smartwork.im.utils.JingGlobals;
import com.smartwork.multiplexer.spi.ClientFailoverDeliverer;

/**
 * ConnectionHandler that knows which subclass of {@link StanzaHandler} should
 * be created and how to build and configure a {@link NIOConnection}.
 *
 * @author Gaston Dombiak
 */
public class ClientConnectionHandler extends ConnectionHandler {

    StanzaHandler createStanzaHandler(NIOConnection connection){// throws XmlPullParserException {
        return new ClientStanzaHandler(router, serverName, connection);
    }

    NIOConnection createNIOConnection(IoSession session) {
        return new NIOConnection(session, new ClientFailoverDeliverer());
    }

    int getMaxIdleTimeAfterSessionAuthorized() {
        // Return 30 minuntes
        return JingGlobals.getIntProperty("cm.client.idle.aftersessionauthorized", 30 * 60);
    }

	@Override
	int getMaxIdleTimeWhenSessionOpened() {
		// Return 30 s
		return JingGlobals.getIntProperty("cm.client.idle.whensessionopened", 30);
	}
}
