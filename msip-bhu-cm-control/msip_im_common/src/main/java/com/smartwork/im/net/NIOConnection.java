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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.IoFilterChain;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.compression.CompressionFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartwork.im.ipacket.PacketDeliverer;
import com.smartwork.im.listener.ConnectionCloseListener;
import com.smartwork.im.message.HintCode;
import com.smartwork.im.message.MessageBuilder;
import com.smartwork.msip.cores.helper.JsonHelper;

/**
 * Implementation of {@link Connection} inteface specific for NIO connections when using
 * the MINA framework.<p>
 *
 * MINA project can be found at <a href="http://mina.apache.org">here</a>. 
 *
 * @author Gaston Dombiak
 */
public class NIOConnection implements Connection {
	private static final Logger logger = LoggerFactory.getLogger(NIOConnection.class);

    /**
     * The utf-8 charset for decoding and encoding XMPP packet streams.
     */
    public static final String CHARSET = "UTF-8";
    /**
     * Reuse the same factory for all the connections.
     */
    //private static XmlPullParserFactory factory = null;

    private Session session;
    private IoSession ioSession;

    private ConnectionCloseListener closeListener;

    /**
     * Deliverer to use when the connection is closed or was closed when delivering
     * a packet.
     */
    private PacketDeliverer backupDeliverer;
    private boolean flashClient = false;
    /*private int majorVersion = 1;
    private int minorVersion = 0;
    private String language = null;

    private TLSPolicy tlsPolicy = TLSPolicy.optional;

    private CompressionPolicy compressionPolicy = CompressionPolicy.disabled;*/
    private CharsetEncoder encoder;

    static {
        /*try {
            factory = XmlPullParserFactory.newInstance(MXParser.class.getName(), null);
            factory.setNamespaceAware(true);
        }
        catch (XmlPullParserException e) {
            Log.error("Error creating a parser factory", e);
        }*/
    }

    public NIOConnection(IoSession session, PacketDeliverer packetDeliverer) {
        this.ioSession = session;
        this.backupDeliverer = packetDeliverer;
        encoder = Charset.forName(CHARSET).newEncoder();
    }

    public boolean validate() {
        if (isClosed()) {
            return false;
        }
        deliverRawText("\n");
        return !isClosed();
    }

    public void registerCloseListener(ConnectionCloseListener listener, Object ignore) {
        if (closeListener != null) {
            throw new IllegalStateException("Close listener already configured");
        }
        if (isClosed()) {
            listener.onConnectionClose(session);
        }
        else {
            closeListener = listener;
        }
    }

    public void removeCloseListener(ConnectionCloseListener listener) {
        if (closeListener == listener) {
            closeListener = null;
        }
    }

    public InetAddress getInetAddress() throws UnknownHostException {
        return ((InetSocketAddress) ioSession.getRemoteAddress()).getAddress();
    }

    public PacketDeliverer getPacketDeliverer() {
        return backupDeliverer;
    }

    public void close() {
        boolean wasClosed = false;
        synchronized (this) {
            if (!isClosed()) {
                try {
                    deliverRawText(
                    		JsonHelper.getJSONString(
                    				MessageBuilder.builderSimpleHintMessage(null,null, HintCode.ConnectionWillBeClosed))+"\n"
                    		,false);
                    //flashClient ? "</flash:stream>" : "</stream:stream>", false);
                } catch (Exception e) {
                    // Ignore
                }
                closeConnection();
                wasClosed = true;
            }
        }
        if (wasClosed) {
            notifyCloseListeners();
        }
    }

    public void systemShutdown() {
        //deliverRawText("<stream:error><system-shutdown " + "xmlns='urn:ietf:params:xml:ns:xmpp-streams'/></stream:error>");
        close();
    }

    /**
     * Forces the connection to be closed immediately no matter if closing the socket takes
     * a long time. This method should only be called from {@link SocketSendingTracker} when
     * sending data over the socket has taken a long time and we need to close the socket, discard
     * the connection and its ioSession.
     */
    @SuppressWarnings("unused")
	private void forceClose() {
        closeConnection();
        // Notify the close listeners so that the SessionManager can send unavailable
        // presences if required.
        notifyCloseListeners();
    }

    private void closeConnection() {
        ioSession.close(true);//.close();
    }

    /**
     * Notifies all close listeners that the connection has been closed.
     * Used by subclasses to properly finish closing the connection.
     */
    private void notifyCloseListeners() {
        if (closeListener != null) {
            try {
                closeListener.onConnectionClose(session);
            } catch (Exception e) {
                logger.error("Error notifying listener: " + closeListener, e);
            }
        }
    }

    public void init(Session owner) {
        session = owner;
    }

    public boolean isClosed() {
        if (session == null) {
            return !ioSession.isConnected();
        }
        return session.getStatus() == Session.STATUS_CLOSED && !ioSession.isConnected();
    }

    public boolean isSecure() {
        return ioSession.getFilterChain().contains("tls");
    }

    public void deliver(String stanza) {
    	logger.info(stanza);
        if (isClosed()) {//当和客户端的连接已经关闭的时候，消息需要转回到dispathcer并存储为离线消息，到某个地方，进行容错
        	logger.error("Error parsing stanza: " + stanza);
        	backupDeliverer.deliver(stanza);
            /*XMPPPacketReader xmppReader = new XMPPPacketReader();
            xmppReader.setXPPFactory(factory);
            try {
                Element doc = xmppReader.read(new StringReader(stanza)).getRootElement();
                backupDeliverer.deliver(doc);
            } catch (Exception e) {
                Log.error("Error parsing stanza: " + stanza, e);
            }*/
        }
        else {
        	IoBuffer buffer = IoBuffer.allocate(stanza.length()+1);//4096);//4k
            buffer.setAutoExpand(true);

            boolean errorDelivering = false;
            try {
                buffer.putString(stanza, encoder);
                if (flashClient) {
                    buffer.put((byte) '\0');
                }else{
                	buffer.put((byte) '\n');
                }
                buffer.flip();
                //System.out.println("SENT: " + doc.asXML());
                ioSession.write(buffer);
            	//ioSession.write(stanza);
            }
            catch (Exception e) {
                logger.debug("Error delivering packet" + "\n" + this.toString(), e);
                errorDelivering = true;
            }
            if (errorDelivering) {
                close();
                logger.error("Error parsing stanza: " + stanza);
                // Retry sending the packet again. Most probably if the packet is a
                // Message it will be stored offline
                /*XMPPPacketReader xmppReader = new XMPPPacketReader();
                xmppReader.setXPPFactory(factory);
                try {
                    Element doc = xmppReader.read(new StringReader(stanza)).getRootElement();
                    backupDeliverer.deliver(doc);
                } catch (Exception e) {
                    Log.error("Error parsing stanza: " + stanza, e);
                }*/
            }
        }
    }

    public void deliverRawText(String stanza) {
    	logger.info(stanza);
        // Deliver the packet in asynchronous mode
        deliverRawText(stanza, true);
    }

    public void deliverRawText(String stanza, boolean asynchronous) {
        if (!isClosed()) {
        	IoBuffer buffer = IoBuffer.allocate(stanza.length()+1);
            buffer.setAutoExpand(true);

            boolean errorDelivering = false;
            try {
                //Charset charset = Charset.forName(CHARSET);
                //buffer.putString(text, charset.newEncoder());
                buffer.put(stanza.getBytes(CHARSET));
                if (flashClient) {
                    buffer.put((byte) '\0');
                }
                buffer.flip();
                if (asynchronous) {
                    ioSession.write(buffer);
                }
                else {
                    // Send stanza and wait for ACK (using a 2 seconds default timeout)
                    boolean ok =
                            ioSession.write(buffer).awaitUninterruptibly(2000);//.join(2000);//JingGlobals.getIntProperty("connection.ack.timeout", 2000));
                    if (!ok) {
                        logger.warn("No ACK was received when sending stanza to: " + this.toString());
                    }
                }
            }
            catch (Exception e) {
            	logger.debug("Error delivering raw text" + "\n" + this.toString(), e);
                errorDelivering = true;
            }
            if (errorDelivering) {
                close();
            }
        }
    }

    public void startCompression() {
        IoFilterChain chain = ioSession.getFilterChain();
        /*//String baseFilter = "org.apache.mina.common.ExecutorThreadModel";
        if (chain.contains("tls")) {
            baseFilter = "tls";
        }*/
        chain.addAfter("tls", "compression", new CompressionFilter(CompressionFilter.COMPRESSION_MAX));
    }

    public boolean isFlashClient() {
        return flashClient;
    }

    public void setFlashClient(boolean flashClient) {
        this.flashClient = flashClient;
    }
/*
    public int getMajorXMPPVersion() {
        return majorVersion;
    }

    public int getMinorXMPPVersion() {
        return minorVersion;
    }

    public void setXMPPVersion(int majorVersion, int minorVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanaguage(String language) {
        this.language = language;
    }

    public boolean isCompressed() {
        return ioSession.getFilterChain().contains("compression");
    }

    public CompressionPolicy getCompressionPolicy() {
        return compressionPolicy;
    }

    public void setCompressionPolicy(CompressionPolicy compressionPolicy) {
        this.compressionPolicy = compressionPolicy;
    }

    public TLSPolicy getTlsPolicy() {
        return tlsPolicy;
    }

    public void setTlsPolicy(TLSPolicy tlsPolicy) {
        this.tlsPolicy = tlsPolicy;
    }

    public String toString() {
        return super.toString() + " MINA Session: " + ioSession;
    }*/

    @Override
	public boolean hasAuthSession() {
		if(session == null || session.getStreamID() == null) return false;
		return session.getStatus() == Session.STATUS_AUTHENTICATED;
		/*String streamID = session.getStreamID();
		if(streamID == null) return false;
		else return true;*/
	}

	public Session getSession() {
		return session;
	}
    
	/*public String infos(){
		if(session != null){
			StringBuilder sb = new StringBuilder();
			
		}
	}*/
    
}
