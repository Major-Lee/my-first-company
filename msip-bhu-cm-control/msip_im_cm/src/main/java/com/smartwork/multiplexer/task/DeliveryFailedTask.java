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

import com.smartwork.multiplexer.spi.ConnectionWorkerThread;

/**
 * Task that indicates the server that delivery of a packet to a client has failed.
 * The most probable reason for this is that the client logged out at the time
 * the server sent a stanza to the client.
 *
 * @author Gaston Dombiak
 */
public class DeliveryFailedTask extends ClientTask {

    private String stanza;

    public DeliveryFailedTask(String streamID, String stanza) {
        super(streamID);
        this.stanza = stanza;
    }

    public void run() {
        ConnectionWorkerThread workerThread = (ConnectionWorkerThread) Thread.currentThread();
        workerThread.deliveryFailed(stanza, streamID);
    }

    public void serverNotAvailable() {
        // Do nothing;
    }
}
