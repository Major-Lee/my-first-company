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

package com.smartwork.multiplexer.task;

import com.smartwork.multiplexer.SessionManager;
import com.smartwork.multiplexer.spi.ConnectionWorkerThread;

/**
 * Task that forwards client packets to the server.
 *
 * @author Gaston Dombiak
 */
public class RouteTask extends ClientTask {

    private String stanza;

    public RouteTask(String streamID, String stanza) {
        super(streamID);
        this.stanza = stanza;
    }

    public void run() {
        ConnectionWorkerThread workerThread = (ConnectionWorkerThread) Thread.currentThread();
        workerThread.deliver(stanza, streamID);
    }

    public void serverNotAvailable() {
        // Close client session indicating that the server is not available
    	SessionManager.getInstance().getSession(streamID).close(true);
    }
}
