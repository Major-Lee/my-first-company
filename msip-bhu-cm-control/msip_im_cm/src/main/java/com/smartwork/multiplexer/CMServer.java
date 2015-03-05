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

package com.smartwork.multiplexer;

import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.SimpleBufferAllocator;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartwork.im.ServerInfo;
import com.smartwork.im.container.IServerContainer;
import com.smartwork.im.container.Module;
import com.smartwork.im.utils.JingGlobals;
import com.smartwork.im.utils.LocaleUtils;
import com.smartwork.im.utils.StringUtils;
import com.smartwork.im.utils.Version;
import com.smartwork.multiplexer.codec.ByteArrayCodecFactory;
import com.smartwork.multiplexer.component.statics.ServerStaticsManager;
import com.smartwork.multiplexer.net.ClientConnectionHandler;
import com.smartwork.multiplexer.spi.ServerSurrogate;

/**
 * Connection managers handle connections of clients that want to connect to a server. Each
 * connection manager may have one or more connections to the target server. These connections
 * are shared amongst connected clients (i.e. multiplexed) thus reducing the load on the
 * server.<p>
 *
 * The only properties that needs to be configured during Connection Managers' setup are
 * <tt>xmpp.domain</tt> and <tt>xmpp.password</tt>. The <tt>xmpp.domain</tt> property
 * defines the name of the target server that clients want to connect to. Clients are
 * redirected to a connection manager when trying to open a socket connection to the server.
 * This is typically done by configuring some local DNS server with a SRV record for the server
 * name that points to the connection manager address. More elaborated solutions may include a
 * load balancer in front of several connection managers. Since XMPP connections are state-full
 * and long-lived then the load balancer does not have to be configured with "sticky sessions".<p>
 *
 * The server and connection managers have to share a common secret so that the server can
 * let connection managers connect to the server and forward packets. Configure the
 * <tt>xmpp.password</tt> property with the same password to log into the server.<p>
 *
 * Each connection manager has to have a unique name that uniquely identifies it from other
 * connection managers. Use the property <tt>xmpp.manager.name</tt> to manually set a name. If this
 * property is not present then a random name will be created for the manager each time it is
 * started. Properties are stored in conf/manager.xml. There are several ways for locating this
 * file.
 * <ol>
 *  <li>Set the system property <b>managerHome</b> when starting up the server.
 *  <li>When running in standalone mode attempt to find it in [home]/conf/manager.xml.
 *  <li>Load the path from <b>manager_init.xml</b> which must be in the classpath.
 * </ol>
 *
 * By default connection managers will open five connections to the server. Configure the
 * <tt>xmpp.manager.connections</tt> property if you want to change the number of connections.
 *
 * @author Gaston Dombiak
 */
public class CMServer implements IServerContainer{
	private static final Logger logger = LoggerFactory.getLogger(CMServer.class);
    private static CMServer instance;

    /**
     * Name of the connection manager. Each manager MUST have a unique name. The name will
     * be used when connecting to the server.
     */
    protected ServerInfo serverInfo;
    /**
     * Name of the server to connect. This is the server where users actually want to
     * connect.
     */
    protected String serverName;
    protected Version version;
    protected Date startDate;
    protected Date stopDate;

    /**
     * Location of the home directory. All configuration files should be
     * located here.
     */
    private String managerHome;
    protected ClassLoader loader;

    /**
     * True if in setup mode
     */
    private boolean setupMode = true;

    private static final String STARTER_CLASSNAME = "com.smartwork.multiplexer.starter.ServerStarter";
    private static final String WRAPPER_CLASSNAME = "org.tanukisoftware.wrapper.WrapperManager";
    private ServerSurrogate serverSurrogate;
    //private IServerSurrogate serverSurrogate;
    private SocketAcceptor socketAcceptor;
    //private SocketAcceptor sslSocketAcceptor;

    /**
     * Returns a singleton instance of ConnectionManager.
     *
     * @return an instance.
     */
    public static CMServer getInstance() {
        return instance;
    }

    /**
     * Creates a server and starts it.
     */
    public CMServer() {
        // We may only have one instance of the server running on the JVM
        if (instance != null) {
            throw new IllegalStateException("A server is already running");
        }
        instance = this;
        start();
    }

    protected void initialize() throws FileNotFoundException {
        locateHome();
        String no = JingGlobals.getXMLProperty("cm.manager.no", StringUtils.randomString(5));//.toLowerCase();
        String name = JingGlobals.getXMLProperty("cm.manager.name", StringUtils.randomString(5));//.toLowerCase();
        // serverInfo = new ServerInfo(name,nameext);
        InetAddress addr = null;  
        try {  
            addr = InetAddress.getLocalHost();  
        } catch (UnknownHostException e) {  
            System.out.println("本机没有IP");  
            e.printStackTrace();  
        }  
        String ip = addr.getHostAddress();  
        String hostname = addr.getHostName();  
        serverInfo = new ServerInfo(name,no,hostname,ip);
        serverName = JingGlobals.getXMLProperty("cm.manager.name");

        version = new Version(3, 6, 3, Version.ReleaseStatus.Release, -1);
        if (serverName != null) {
            setupMode = false;
        }
        else  {
        	logger.warn(LocaleUtils.getLocalizedString("setup.no_server_name"));
            System.err.println(LocaleUtils.getLocalizedString("setup.no_server_name"));
            // Pause 5 seconds so the user knows what's going on. This especially helps users
            // of the .bat file. Otherwise, an error message is displayed and the server
            // dissapears right away.
            try {
                Thread.sleep(4000);
            }
            catch (Exception e) {
                // Ignore.
            }
        }

        if (isStandAlone()) {
            Runtime.getRuntime().addShutdownHook(new ShutdownHookThread());
        }

        loader = Thread.currentThread().getContextClassLoader();
        //initializedPlugins();
        
    }
    /*public void initializedPlugins(){
		ActiveMQConnectionManager.getInstance();
	}*/
    /**
     * Finish the setup process. Because this method is meant to be called from inside
     * the Admin console plugin, it spawns its own thread to do the work so that the
     * class loader is correct.
     */
    public void finishSetup() {
        if (!setupMode) {
            return;
        }
        // Make sure that setup finished correctly.
        if ("true".equals(JingGlobals.getXMLProperty("setup"))) {
            // Set the new server domain assigned during the setup process
            //name = JingGlobals.getXMLProperty("cm.manager.name", StringUtils.randomString(5)).toLowerCase();
            serverName = JingGlobals.getXMLProperty("cm.domain").toLowerCase();

            Thread finishSetup = new Thread() {
                public void run() {
                    try {
                        // Start modules
                        startModules();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        logger.error(e.getMessage(),e);
                        shutdownServer();
                    }
                }
            };
            // Use the correct class loader.
            finishSetup.setContextClassLoader(loader);
            finishSetup.start();
            // We can now safely indicate that setup has finished
            setupMode = false;
        }
    }

    public void start() {
        try {
            initialize();

            // If the server has already been setup then we can start all the server's modules
            if (!setupMode) {
            	// First load all the modules so that modules may access other modules while
                // being initialized
                loadModules();
                // Initize all the modules
                initModules();
                // Start all the modules
                startModules();
            }
            // Log that the server has been started
            List<String> params = new ArrayList<String>();
            params.add(version.getVersionString());
            params.add(JingGlobals.formatDateTime(new Date()));
            String startupBanner = LocaleUtils.getLocalizedString("startup.name", params);
            logger.info(startupBanner);
            System.out.println(startupBanner);

            startDate = new Date();
            stopDate = null;
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(),e);
            System.out.println(LocaleUtils.getLocalizedString("startup.error"));
            shutdownServer();
        }
    }

    private void startModules() {
    	
    	serverSurrogate = new ServerSurrogate();
    	serverSurrogate.start();
        String localIPAddress;
        // Setup port info
        try {
            localIPAddress = InetAddress.getLocalHost().getHostAddress();
        }
        catch (UnknownHostException e) {
            localIPAddress = "Unknown";
        }
        // Start process that checks health of socket connections
        //SocketSendingTracker.getInstance().start();
        // Check if we need to configure MINA to use Direct or Heap Buffers
        // Note: It has been reported that heap buffers are 50% faster than direct buffers
        if (!JingGlobals.getBooleanProperty("xmpp.socket.directBuffer", false)) {
        	IoBuffer.setUseDirectBuffer(false);
        	IoBuffer.setAllocator(new SimpleBufferAllocator());//new IoBufferAllocator()
        }
        // Start the port listener for clients
        startClientListeners(localIPAddress);
        
        //startIMMessageProducer();
        //startIMMessageConsumer();
        // Start the port listener for secured clients
        //startClientSSLListeners(localIPAddress);
        // Start http bind listener
        //startHttpBindServlet();
        
        for (Module module : modules.values()) {
            boolean started = false;
            try {
                module.start();
            }
            catch (Exception e) {
                if (started && module != null) {
                    module.stop();
                    module.destroy();
                }
                logger.error(LocaleUtils.getLocalizedString("admin.error"), e);
            }
        }
        
    }

    private void stopModules() {
        stopClientListeners();
        //stopClientSSLListeners();
        //stopHttpBindServlet();
        // Stop process that checks health of socket connections
        //SocketSendingTracker.getInstance().shutdown();
        // Stop service that forwards packets to the server
        if (serverSurrogate != null) {
            serverSurrogate.shutdown(false);
        }
        //stopIMMessageProducer();
        //stopIMMessageConsumer();
    }

    public int getClientListenerPort() {
        int port = 5222;
        // Check if old property is being used for storing c2s port
        if (JingGlobals.getXMLProperty("cm.socket.plain.port") != null) {
            port = JingGlobals.getIntProperty("cm.socket.plain.port", 5222);
        }
        // Check if new property is being used for storing c2s port
        else if (JingGlobals.getXMLProperty("cm.socket.default.port") != null) {
            port = JingGlobals.getIntProperty("cm.socket.default.port", 5222);
        }
        return port;
    }

    private void startClientListeners(String localIPAddress) {
        if (!JingGlobals.getBooleanProperty("cm.socket.default.active", true)) {
            // Do not start listener if service is disabled
            return;
        }
        // Start clients plain socket unless it's been disabled.
        int port = getClientListenerPort();
        // Create SocketAcceptor with correct number of processors
        SocketAcceptor acceptor  = buildSocketAcceptor();
        
        /*TextLineCodecFactory lineCodec = new TextLineCodecFactory(Charset.forName("UTF-8"), 
        		LineDelimiter.UNIX.getValue(),LineDelimiter.UNIX.getValue());
        lineCodec.setDecoderMaxLineLength(4*1024);//4k
        lineCodec.setEncoderMaxLineLength(4*1024);//4k
*/       
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ByteArrayCodecFactory()));//new ProtocolCodecFilter(lineCodec));//new TextLineCodecFactory()));//
        acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(100));
        //acceptor.getFilterChain().addLast("logger", new LoggingFilter());
        try {
            // Listen on a specific network interface if it has been set.
            String interfaceName = JingGlobals.getXMLProperty("cm.socket.network.interface");
            InetAddress bindInterface = null;
            if (interfaceName != null) {
                if (interfaceName.trim().length() > 0) {
                    bindInterface = InetAddress.getByName(interfaceName);
                }
            }
            
            // Start accepting connections
            acceptor.setHandler(new ClientConnectionHandler());
            //acceptor.bind(new InetSocketAddress("0.0.0.0", port));
            acceptor.bind(new InetSocketAddress(bindInterface, port));//, new ClientConnectionHandler());
            List<String> params = new ArrayList<String>();
            params.add(Integer.toString(port));
            logger.info(LocaleUtils.getLocalizedString("startup.plain", params));
            socketAcceptor = acceptor;
        }
        catch (Exception e) {
            System.err.println("Error starting XMPP listener on port " + port + ": " + e.getMessage());
            logger.error(LocaleUtils.getLocalizedString("admin.error.socket-setup"), e);
        }
    }

    private void stopClientListeners() {
        if (socketAcceptor != null) {
            socketAcceptor.unbind();//.unbindAll();
            socketAcceptor = null;
        }
    }
/*    public void startIMMessageProducer(){
		try{
			IMMessageProducer.getInstance().initialize(this);
			IMMessageProducer.getInstance().start();
			IMMessageProducer.getInstance().deliverServerInitMessage();
		}catch(Exception ex){
			ex.printStackTrace();
			this.stop();
		}
	}
	
	public void stopIMMessageProducer(){
		IMMessageProducer.getInstance().stop();
		IMMessageProducer.getInstance().destroy();
	}
	
	public void startIMMessageConsumer(){
		try{
			IMMessageConsumer.getInstance().initialize(this);
			IMMessageConsumer.getInstance().start();
		}catch(Exception ex){
			ex.printStackTrace();
			this.stop();
		}
	}
	
	public void stopIMMessageConsumer(){
		IMMessageConsumer.getInstance().stop();
		IMMessageConsumer.getInstance().destroy();
	}*/
    
/*    private void startClientSSLListeners(String localIPAddress) {
        if (!JiveGlobals.getBooleanProperty("xmpp.socket.ssl.active", true)) {
            // Do not start listener if service is disabled
            return;
        }
        // Start clients SSL unless it's been disabled.
        int port = JiveGlobals.getIntProperty("xmpp.socket.ssl.port", 5223);
        String algorithm = JiveGlobals.getXMLProperty("xmpp.socket.ssl.algorithm");
        if ("".equals(algorithm) || algorithm == null) {
            algorithm = "TLS";
        }
        // Create SocketAcceptor with correct number of processors
        sslSocketAcceptor = buildSocketAcceptor();
        // Customize thread model for c2s (old ssl port)
        int eventThreads = JiveGlobals.getIntProperty("xmpp.processor.threads.ssl", 16);
        ExecutorFilter executorFilter = new ExecutorFilter();
        ThreadPoolExecutor eventExecutor = (ThreadPoolExecutor)executorFilter.getExecutor();
        final ThreadFactory originalThreadFactory = eventExecutor.getThreadFactory();
        ThreadFactory newThreadFactory = new ThreadFactory()
        {
            private final AtomicInteger threadId = new AtomicInteger( 0 );

            public Thread newThread( Runnable runnable )
            {
                Thread t = originalThreadFactory.newThread( runnable );
                t.setName("Old SSL executor thread - " + threadId.incrementAndGet() );
                t.setDaemon( true );
                return t;
            }
        };
        eventExecutor.setThreadFactory( newThreadFactory );
        eventExecutor.setCorePoolSize(eventThreads + 1);
        eventExecutor.setMaximumPoolSize(eventThreads + 1);
        eventExecutor.setKeepAliveTime(60, TimeUnit.SECONDS);

        sslSocketAcceptor.getDefaultConfig().setThreadModel(ThreadModel.MANUAL);
        // Add the XMPP codec filter
        sslSocketAcceptor.getFilterChain().addFirst("xmpp", new ProtocolCodecFilter(new XMPPCodecFactory()));
        sslSocketAcceptor.getFilterChain().addFirst("threadModel", executorFilter);
                                                                  
        try {
            // Add the SSL filter now since sockets are "borned" encrypted in the old ssl method
            SSLContext sslContext = SSLContext.getInstance(algorithm);
            KeyManagerFactory keyFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyFactory.init(SSLConfig.getKeyStore(), SSLConfig.getKeyPassword().toCharArray());
            TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustFactory.init(SSLConfig.getTrustStore());

            sslContext.init(keyFactory.getKeyManagers(),trustFactory.getTrustManagers(),new java.security.SecureRandom());

            sslSocketAcceptor.getFilterChain().addFirst("tls", new SslFilter(sslContext));

            // Listen on a specific network interface if it has been set.
            String interfaceName = JiveGlobals.getXMLProperty("xmpp.socket.network.interface");
            InetAddress bindInterface = null;
            if (interfaceName != null) {
                if (interfaceName.trim().length() > 0) {
                    bindInterface = InetAddress.getByName(interfaceName);
                }
            }
            // Start accepting connections
            //sslSocketAcceptor.bind(new InetSocketAddress(bindInterface, port), new ClientConnectionHandler());
            sslSocketAcceptor.setHandler(new ClientConnectionHandler());
            sslSocketAcceptor.bind(new InetSocketAddress(bindInterface, port));
            
            
            List<String> params = new ArrayList<String>();
            params.add(Integer.toString(port));
            Log.info(LocaleUtils.getLocalizedString("startup.ssl", params));
        }
        catch (Exception e) {
            System.err.println("Error starting SSL XMPP listener on port " + port + ": " +
                    e.getMessage());
            Log.error(LocaleUtils.getLocalizedString("admin.error.ssl"), e);
        }
    }*/

    /*private void stopClientSSLListeners() {
        if (sslSocketAcceptor != null) {
            sslSocketAcceptor.unbind();//.unbindAll();
            sslSocketAcceptor = null;
        }
    }*/

    /*private void startHttpBindServlet() {
        boolean httpBindEnabled = JiveGlobals.getBooleanProperty("xmpp.httpbind.enabled", false);
        if (!httpBindEnabled) {
            return;
        }

        try {
            HttpBindManager.getInstance().start();
        }
        catch (Exception e) {
            Log.error(LocaleUtils.getLocalizedString("admin.error.http.bind"), e);
        }
    }

    private void stopHttpBindServlet() {
        try {
            HttpBindManager.getInstance().stop();
        }
        catch (Exception e) {
            Log.error(e);
        }
    }*/

    /**
     * Restarts the server and all it's modules only if the server is restartable. Otherwise do
     * nothing.
     */
    public void restart() {
        if (isStandAlone() && isRestartable()) {
            try {
                Class<?> wrapperClass = Class.forName(WRAPPER_CLASSNAME);
                Method restartMethod = wrapperClass.getMethod("restart", (Class []) null);
                restartMethod.invoke(null, (Object []) null);
            }
            catch (Exception e) {
                logger.error("Could not restart container", e);
            }
        }
    }

    /**
     * Stops the server only if running in standalone mode. Do nothing if the server is running
     * inside of another server.
     */
    public void stop() {
        // Only do a system exit if we're running standalone
        if (isStandAlone()) {
            // if we're in a wrapper, we have to tell the wrapper to shut us down
            if (isRestartable()) {
                try {
                    Class<?> wrapperClass = Class.forName(WRAPPER_CLASSNAME);
                    Method stopMethod = wrapperClass.getMethod("stop", Integer.TYPE);
                    stopMethod.invoke(null, 0);
                }
                catch (Exception e) {
                    logger.error("Could not stop container", e);
                }
            }
            else {
                shutdownServer();
                stopDate = new Date();
                Thread shutdownThread = new ShutdownThread();
                shutdownThread.setDaemon(true);
                shutdownThread.start();
            }
        }
        else {
            // Close listening socket no matter what the condition is in order to be able
            // to be restartable inside a container.
            shutdownServer();
            stopDate = new Date();
        }
    }

    /**
     * Makes a best effort attempt to shutdown the server
     */
    private void shutdownServer() {
        // Stop modules
        stopModules();
        // hack to allow safe stopping
        logger.info("Connection Manager stopped");
    }

    public boolean isSetupMode() {
        return setupMode;
    }

    public boolean isRestartable() {
        boolean restartable;
        try {
            restartable = Class.forName(WRAPPER_CLASSNAME) != null;
        }
        catch (ClassNotFoundException e) {
            restartable = false;
        }
        return restartable;
    }

    /**
     * Returns if the server is running in standalone mode. We consider that it's running in
     * standalone if the "org.jivesoftware.multiplexer.starter.ServerStarter" class is present in the
     * system.
     *
     * @return true if the server is running in standalone mode.
     */
    public boolean isStandAlone() {
        boolean standalone;
        try {
            standalone = Class.forName(STARTER_CLASSNAME) != null;
        }
        catch (ClassNotFoundException e) {
            standalone = false;
        }
        return standalone;
    }

    /**
     * Returns the service responsible for forwarding stanzas to the server.
     *
     * @return the service responsible for forwarding stanzas to the server.
     */
    /*public IServerSurrogate getServerSurrogate() {
        return serverSurrogate;
    }*/
    public ServerSurrogate getServerSurrogate() {
        return serverSurrogate;
    }
    /**
     * Returns the name of the main server where received packets will be forwarded.
     *
     * @return the name of the main server where received packets will be forwarded.
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * Returns the name that uniquely identifies this connection manager. Use the property
     * <tt>xmpp.manager.name</tt> to manually set a name. If the property is not present then a
     * random name will be created for the manager each time it is started. Properties
     * are stored in conf/manager.xml. There are several ways for locating this file.
     * <ol>
     *  <li>Set the system property <b>managerHome</b> when starting up the server.
     *  <li>When running in standalone mode attempt to find it in [home]/conf/manager.xml.
     *  <li>Load the path from <b>manager_init.xml</b> which must be in the classpath.
     * </ol>
     *
     * @return the name that uniquely identifies this connection manager.
     */
    @Override
    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    private SocketAcceptor buildSocketAcceptor() {
    	int ioThreads = JingGlobals.getIntProperty("cm.processor.count", Runtime.getRuntime().availableProcessors());
    	NioSocketAcceptor acceptor = new NioSocketAcceptor(ioThreads+1);
    	//SocketAcceptorConfig socketAcceptorConfig = (SocketAcceptorConfig) socketAcceptor.getDefaultConfig();
    	acceptor.setReuseAddress(true);
        // Set the listen backlog (queue) length. Default is 50.
    	acceptor.setBacklog(JingGlobals.getIntProperty("cm.socket.backlog", 50));
    	SocketSessionConfig socketSessionConfig = acceptor.getSessionConfig();
        int receiveBuffer = JingGlobals.getIntProperty("cm.socket.buffer.receive", -1);
        if (receiveBuffer > 0 ) {
        	socketSessionConfig.setReceiveBufferSize(receiveBuffer);
        }
        int sendBuffer = JingGlobals.getIntProperty("cm.socket.buffer.send", -1);
        if (sendBuffer > 0 ) {
            socketSessionConfig.setSendBufferSize(sendBuffer);
        }
        int linger = JingGlobals.getIntProperty("cm.socket.linger", -1);
        if (linger > 0 ) {
            socketSessionConfig.setSoLinger(linger);
        }
        socketSessionConfig.setTcpNoDelay(
        		JingGlobals.getBooleanProperty("cm.socket.tcp-nodelay", socketSessionConfig.isTcpNoDelay()));
        return acceptor;
/*    	SocketAcceptor socketAcceptor;
        // Create SocketAcceptor with correct number of processors
        int ioThreads = JiveGlobals.getIntProperty("xmpp.processor.count", Runtime.getRuntime().availableProcessors());
        // Set the executor that processors will use. Note that processors will use another executor
        // for processing events (i.e. incoming traffic)
        //Executor ioExecutor = new ThreadPoolExecutor(ioThreads + 1, ioThreads + 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>() );
        socketAcceptor = new NioSocketAcceptor(ioThreads);//, ioExecutor);
        // Set that it will be possible to bind a socket if there is a connection in the timeout state
        //SocketAcceptorConfig socketAcceptorConfig = (SocketAcceptorConfig) socketAcceptor.getDefaultConfig();
        socketAcceptor.setReuseAddress(true);
        // Set the listen backlog (queue) length. Default is 50.
        socketAcceptor.setBacklog(JiveGlobals.getIntProperty("xmpp.socket.backlog", 50));
        // Set default (low level) settings for new socket connections
        SocketSessionConfig socketSessionConfig = socketAcceptor.getSessionConfig();
        //socketSessionConfig.setKeepAlive();
        int receiveBuffer = JiveGlobals.getIntProperty("xmpp.socket.buffer.receive", -1);
        if (receiveBuffer > 0 ) {
            socketSessionConfig.setReceiveBufferSize(receiveBuffer);
        }
        int sendBuffer = JiveGlobals.getIntProperty("xmpp.socket.buffer.send", -1);
        if (sendBuffer > 0 ) {
            socketSessionConfig.setSendBufferSize(sendBuffer);
        }
        int linger = JiveGlobals.getIntProperty("xmpp.socket.linger", -1);
        if (linger > 0 ) {
            socketSessionConfig.setSoLinger(linger);
        }
        socketSessionConfig.setTcpNoDelay(
                JiveGlobals.getBooleanProperty("xmpp.socket.tcp-nodelay", socketSessionConfig.isTcpNoDelay()));
        return socketAcceptor;*/
    }

    /**
     * Verifies that the given home guess is a real Connection Manager home directory.
     * We do the verification by checking for the Connection Manager config file in
     * the config dir of jiveHome.
     *
     * @param homeGuess a guess at the path to the home directory.
     * @param jiveConfigName the name of the config file to check.
     * @return a file pointing to the home directory or null if the
     *         home directory guess was wrong.
     * @throws java.io.FileNotFoundException if there was a problem with the home
     *                                       directory provided
     */
/*    private File verifyHome(String homeGuess, String jiveConfigName) throws FileNotFoundException {
        File managerHome = new File(homeGuess);
        File configFile = new File(managerHome, jiveConfigName);
        if (!configFile.exists()) {
            throw new FileNotFoundException();
        }
        else {
            try {
                return new File(managerHome.getCanonicalPath());
            }
            catch (Exception ex) {
                throw new FileNotFoundException();
            }
        }
    }*/

    /**
     * <p>Retrieve the jive home for the container.</p>
     *
     * @throws FileNotFoundException If jiveHome could not be located
     */
    private void locateHome() throws FileNotFoundException {
    	if (managerHome == null) {
            managerHome = System.getProperty("SMART_CM_HOME");
            //System.out.println(System.getProperty("user.dir"));
            if (managerHome == null) {
                managerHome = System.getProperty("user.dir");
            }
            
        }
	    if (managerHome == null) {
	        System.err.println("Could not locate home");
	        throw new FileNotFoundException();
	    }
	    else {
	    	// Set the home directory for the config file
	    	JingGlobals.setHomeDirectory(managerHome.toString());
	    }
    }
        // Set the name of the config file
        //JingGlobals.setConfigName(jiveConfigName);
        /*String jiveConfigName = "conf" + File.separator + "manager.xml";
        // First, try to load it managerHome as a system property.
        if (managerHome == null) {
            String homeProperty = System.getProperty("managerHome");
            try {
                if (homeProperty != null) {
                    managerHome = verifyHome(homeProperty, jiveConfigName);
                }
            }
            catch (FileNotFoundException fe) {
                // Ignore.
            }
        }

        // If we still don't have home, let's assume this is standalone
        // and just look for home in a standard sub-dir location and verify
        // by looking for the config file
        if (managerHome == null) {
            try {
                managerHome = verifyHome("..", jiveConfigName).getCanonicalFile();
            }
            catch (FileNotFoundException fe) {
                // Ignore.
            }
            catch (IOException ie) {
                // Ignore.
            }
        }

        // If home is still null, no outside process has set it and
        // we have to attempt to load the value from manager_init.xml,
        // which must be in the classpath.
        if (managerHome == null) {
            InputStream in = null;
            try {
                in = getClass().getResourceAsStream("/manager_init.xml");
                if (in != null) {
                    SAXReader reader = new SAXReader();
                    Document doc = reader.read(in);
                    String path = doc.getRootElement().getText();
                    try {
                        if (path != null) {
                            managerHome = verifyHome(path, jiveConfigName);
                        }
                    }
                    catch (FileNotFoundException fe) {
                        fe.printStackTrace();
                    }
                }
            }
            catch (Exception e) {
                System.err.println("Error loading manager_init.xml to find home.");
                e.printStackTrace();
            }
            finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                }
                catch (Exception e) {
                    System.err.println("Could not close open connection");
                    e.printStackTrace();
                }
            }
        }

        if (managerHome == null) {
            System.err.println("Could not locate home");
            throw new FileNotFoundException();
        }
        else {
            // Set the home directory for the config file
            JiveGlobals.setHomeDirectory(managerHome.toString());
            // Set the name of the config file
            JiveGlobals.setConfigName(jiveConfigName);
        }*/
    //}


    //}
    /**
     * <p>A thread to ensure the server shuts down no matter what.</p>
     * <p>Spawned when stop() is called in standalone mode, we wait a few
     * seconds then call system exit().</p>
     *
     * @author Iain Shigeoka
     */
    private class ShutdownHookThread extends Thread {

        /**
         * <p>Logs the server shutdown.</p>
         */
        public void run() {
            shutdownServer();
            logger.info("Connection Manager halted");
            System.err.println("Connection Manager halted");
        }
    }

    /**
     * <p>A thread to ensure the server shuts down no matter what.</p>
     * <p>Spawned when stop() is called in standalone mode, we wait a few
     * seconds then call system exit().</p>
     *
     * @author Iain Shigeoka
     */
    private class ShutdownThread extends Thread {

        /**
         * <p>Shuts down the JVM after a 5 second delay.</p>
         */
        public void run() {
            try {
                Thread.sleep(5000);
                // No matter what, we make sure it's dead
                System.exit(0);
            }
            catch (InterruptedException e) {
                // Ignore.
            }

        }
    }
    
    private Map<Class<?>, Module> modules = new LinkedHashMap<Class<?>, Module>();
    
    private void loadModules() {
        // Load boot modules
        //loadModule(NotifyMessageConsumer.class.getName());
        loadModule(ServerStaticsManager.class.getName());
    }
    
    private void loadModule(String module) {
        try {
            Class<?> modClass = loader.loadClass(module);
            Module mod = (Module) modClass.newInstance();
            this.modules.put(modClass, mod);
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error(LocaleUtils.getLocalizedString("admin.error"), e);
        }
    }

    private void initModules() {
        for (Module module : modules.values()) {
            boolean isInitialized = false;
            try {
                module.initialize(this);
                isInitialized = true;
            }
            catch (Exception e) {
                e.printStackTrace();
                // Remove the failed initialized module
                this.modules.remove(module.getClass());
                if (isInitialized) {
                    module.stop();
                    module.destroy();
                }
                logger.error(LocaleUtils.getLocalizedString("admin.error"), e);
            }
        }
    }
}
