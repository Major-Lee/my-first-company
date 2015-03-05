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

import java.net.InetAddress;

import com.smartwork.multiplexer.SessionManager;
import com.smartwork.multiplexer.spi.ConnectionWorkerThread;

/**
 * Task that notifies the server that a new client session has been created. This task
 * is executed right after clients send their initial stream header.
 *
 * @author Gaston Dombiak
 */
public class NewSessionTask extends ClientTask {

    private InetAddress address;

    public NewSessionTask(String streamID, InetAddress address) {
        super(streamID);
        this.address = address;
    }

    public void run() {
        ConnectionWorkerThread workerThread = (ConnectionWorkerThread) Thread.currentThread();
        workerThread.clientSessionCreated(streamID, address);
    }

    public void serverNotAvailable() {
        // Close client session indicating that the server is not available
    	SessionManager.getInstance().getSession(streamID).close(true);
    }
}
