package com.bhu.jorion.ursids;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.jorion.JOrion;
import com.bhu.jorion.JOrionConfig;
import com.bhu.jorion.message.UrsidsMessage;
import com.bhu.jorion.util.StringHelper;

public class UrsidsWorker // implements Runnable 
{
    private final static Logger LOGGER = LoggerFactory.getLogger(UrsidsWorker.class);

	private JOrion orion;
	
	public UrsidsWorker(JOrion orion){
		this.orion = orion;
	}
    
    public void sendMessage(IoSession session, UrsidsMessage msg) throws Exception
    {
    	long start = System.currentTimeMillis();
    	LOGGER.debug(new String(msg.getBody(), "UTF-8"));
    	session.write(msg);
    	LOGGER.debug("wirte ursids, cost:" + (System.currentTimeMillis() - start));
    }
    
    public void run(){
    	try{
    		NioSocketAcceptor acceptor = new NioSocketAcceptor();
	
			// Add two filters : a logger and a codec
			acceptor.getFilterChain().addLast("logger", new LoggingFilter());
			acceptor.getFilterChain().addLast("codec",new ProtocolCodecFilter(new UrsidsCodecFactory()));
//			acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(Executors.newCachedThreadPool()));
			acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(Executors.newFixedThreadPool(JOrionConfig.URSIDS_THREADS_NUMBER)));

			// Attach the business logic to the server
			acceptor.setHandler(new UrsidsIoHandler(orion));
	
			// Configurate the buffer size and the iddle time
			acceptor.getSessionConfig().setReadBufferSize(JOrionConfig.URSIDS_READ_BUFFER_SIZE);
			acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 9999999);
	
			acceptor.setReuseAddress(true);
			// And bind !
			acceptor.bind(new InetSocketAddress(JOrionConfig.JORION_LISTEN_PORT));
			LOGGER.info("Listening on port:" + JOrionConfig.JORION_LISTEN_PORT);
    	}catch(IOException e){
			LOGGER.error(StringHelper.getStackTrace(e));
    		e.printStackTrace();
    	}
    }
    
    public void start(){
    	run();
   	//LOGGER.info("Creating ursids receiver thread");
    	
//		new Thread(this, "ursids_receiver").start();
    }
}
