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

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartwork.im.utils.JingGlobals;
import com.smartwork.multiplexer.SocketStatistic;
import com.smartwork.multiplexer.net.SocketConnection;

/**
 * Reads and processes stanzas sent from the server. Each connection to the server will
 * have an instance of this class. Read packets will be processed using a thread pool.
 * By default, the thread pool will have 5 processing threads. Configure the property
 * <tt>xmpp.manager.incoming.threads</tt> to change the number of processing threads
 * per connection to the server. 
 *
 * @author Gaston Dombiak
 */
class ServerPacketReader implements SocketStatistic {
	private static final Logger logger = LoggerFactory.getLogger(ServerPacketReader.class);
    private boolean open = true;
    private StringPacketReader reader = null;

    /**
     * Pool of threads that will process incoming stanzas from the server.
     */
    private ThreadPoolExecutor threadPool;
    /**
     * Actual object responsible for handling incoming traffic.
     */
    private ServerPacketHandler packetsHandler;

    public ServerPacketReader(StringPacketReader reader, SocketConnection connection,
                              String address) {
        this.reader = reader;
        packetsHandler = new ServerPacketHandler(connection, address);
        init();
    }

    private void init() {
        // Create a pool of threads that will process incoming packets.
        int maxThreads = JingGlobals.getIntProperty("xmpp.manager.incoming.threads", 5);
        if (maxThreads < 1) {
            // Ensure that the max number of threads in the pool is at least 1
            maxThreads = 1;
        }
        threadPool =
                new ThreadPoolExecutor(maxThreads, maxThreads, 60, TimeUnit.SECONDS,
                        new LinkedBlockingQueue<Runnable>(),
                        new ThreadPoolExecutor.CallerRunsPolicy());

        // Create a thread that will read and store DOM Elements.
        Thread thread = new Thread("Server Packet Reader") {
            public void run() {
                while (open) {
                    String doc;
                    try {
                    	//System.out.println("Server Packet Reader start reader");
                        //doc = reader.parseDocument().getRootElement();
                    	doc = reader.parseContent();
                    	//doc = reader.readLine();
                        if (doc == null) {
                            // Stop reading the stream since the remote server has sent an end of
                            // stream element and probably closed the connection.
                            shutdown();
                        }
                        else {
                        	//System.out.println("Server Packet Reader start reader content:" + doc);
                            // Queue task that process incoming stanzas
                            threadPool.execute(new ProcessStanzaTask(packetsHandler, doc));
                        }
                    }
                    catch (IOException e) {
                    	logger.debug("Finishing Incoming Server Stanzas Reader.", e);
                        shutdown();
                    }
                    catch (Exception e) {
                    	logger.error("Finishing Incoming Server Stanzas Reader.", e);
                        shutdown();
                    }
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    public long getLastActive() {
        return reader.getLastActive();
    }

    public void shutdown() {
        open = false;
        threadPool.shutdown();
    }

    /**
     * Task that processes incoming stanzas from the server.
     */
    private class ProcessStanzaTask implements Runnable {
        /**
         * Incoming stanza to process.
         */
        private String stanza;
        /**
         * Actual object responsible for handling incoming traffic.
         */
        private ServerPacketHandler handler;

        public ProcessStanzaTask(ServerPacketHandler handler, String stanza) {
            this.handler = handler;
            this.stanza = stanza;
        }

        public void run() {
            handler.handle(stanza);
        }
    }
}
