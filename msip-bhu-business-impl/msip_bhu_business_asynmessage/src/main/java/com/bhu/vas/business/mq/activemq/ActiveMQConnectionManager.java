package com.bhu.vas.business.mq.activemq;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.business.mq.activemq.listener.NotifyMessageConsumerListener;


public class ActiveMQConnectionManager{
	private final Logger logger = LoggerFactory.getLogger(ActiveMQConnectionManager.class);
	private ActiveMQConnectionFactory connectionFactory = null;
	//org.apache.activemq.pool.PooledConnectionFactory connectionFactory = null;
	private Map<String,Connection> connections = null;//new HashMap<String,Connection>();
	private Map<String,Session> sessions = null;
	private Map<String,MessageProducer> producers = null;
	private Properties properties = new Properties();
	private boolean porperties_loaded = false;
	//ActiveMQDynamicService dynamicService;
	private static class ActiveMQConnectionManagerHolder{ 
		private static ActiveMQConnectionManager instance =new ActiveMQConnectionManager(); 
		static{
			instance.initialize();
			instance.start();
		}
	} 
	
	public static ActiveMQConnectionManager getInstance() { 
		return ActiveMQConnectionManagerHolder.instance; 
	}
    
	public ActiveMQConnectionManager(){//ActiveMQDynamicService dynamicService) {
		//this.dynamicService = dynamicService;
		//super("ActiveMQConnectionManager");
	}
	
	public void initialize() {
		//start();
	}

	public void start(){
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~:start:"+this);
		InputStream in = null;
	    try {
	    	in = ActiveMQConnectionManager.class.getResourceAsStream("/deploy/lazyloadconf/dynamic.activemq.properties");
			properties.load(in);
			porperties_loaded = true;
		} catch (Exception e) {
			try{
				in = ActiveMQConnectionManager.class.getResourceAsStream("/lazyloadconf/dynamic.activemq.properties");
				properties.load(in);
				porperties_loaded = true;
			}catch(Exception ex){
				logger.error("init loading /deploy/lazyloadconf/dynamic.activemq.properties or  /lazyloadconf/dynamic.activemq.properties failed!", e);
				e.printStackTrace(System.out);
			}
		}finally{
			if(in != null){
				try {
					in.close();
					in = null;
				} catch (IOException e) {
					e.printStackTrace(System.out);
				}
			}
		}
	    if(porperties_loaded){
	    	String activemqUrl = properties.getProperty("mq.activemq.server.url");
	    	System.out.println("dynamicActivemqUrl:"+activemqUrl);
	    	connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER,ActiveMQConnection.DEFAULT_USER,activemqUrl);//"tcp://192.168.101.251:61616");
	    	/*connectionFactory = new org.apache.activemq.pool.PooledConnectionFactory();
	    	connectionFactory.setConnectionFactory(factory);
	    	connectionFactory.setMaxConnections(20);*/
			connections = new HashMap<String,Connection>(); 
			sessions = new HashMap<String,Session>();
			producers = new HashMap<String,MessageProducer>();
			logger.info("初始化MQ监听...@Url:"+activemqUrl);
			
	    }
	    
	}

	public void initConsumerQueues(){
		if(porperties_loaded){
			String consumerQueues = properties.getProperty("mq.activemq.server.consumer.queues");
			String[] consumerQueue_array = consumerQueues.split(",");
			for(String consumerQueue:consumerQueue_array){
				try {
					setupMessageConsumer("in",consumerQueue);
				} catch (JMSException e) {
					e.printStackTrace(System.out);
				}
			}
		}
	}
	public void initProducerQueues(){
		if(porperties_loaded){
			String producerQueues = properties.getProperty("mq.activemq.server.producer.queues");
			String[] producerQueue_array = producerQueues.split(",");
			for(String producerQueue:producerQueue_array){
				try {
					setupMessageProducer("out",producerQueue);
				} catch (JMSException e) {
					e.printStackTrace(System.out);
				}
			}
		}
	}
	
	public void stop() {
		String activemqUrl = null;//JingGlobals.getXMLProperty("mq.activemq.url", "failover:(tcp://192.168.1.2:61616)");
		logger.info("停止MQ监听...@Url:"+activemqUrl);
		Iterator<Entry<String,Session>> iterSession = sessions.entrySet().iterator(); 
		while (iterSession.hasNext()) { 
		    Map.Entry<String,Session> entry = iterSession.next(); 
		    String key = entry.getKey(); 
		    Session sess = entry.getValue();
		    logger.info(String.format("正在停止Session Key:[%s] value[%s]", key,sess));
		    if(sess != null){
		    	try{
		    		sess.close();
		    	}catch(Exception ex){
		    		ex.printStackTrace(System.out);
		    	}finally{
		    		sess = null;
		    	}
		    }
		    logger.info(String.format("停止Session Key:[%s] successfully!", key));//  "停止Session:"+activemqUrl);
		}
		sessions.clear();
		
		Iterator<Entry<String,Connection>> iterConn = connections.entrySet().iterator(); 
		while (iterConn.hasNext()) { 
		    Map.Entry<String,Connection> entry = iterConn.next(); 
		    String key = entry.getKey(); 
		    Connection conn = entry.getValue();
		    logger.info(String.format("正在停止Connection Key:[%s] value[%s]", key,conn));
		    if(conn != null){
		    	try{
		    		conn.close();
		    	}catch(Exception ex){
		    		ex.printStackTrace();
		    	}finally{
		    		conn = null;
		    	}
		    }
		    logger.info(String.format("停止Connection Key:[%s] successfully!", key));
		} 
		connections.clear();
	}
	
	public void stop(String key){
		if(!sessions.isEmpty()){
			Session session = sessions.get(key);
			if(session != null){
				logger.info(String.format("正在停止Session Key:[%s] value[%s]", key,session));
			    if(session != null){
			    	try{
			    		session.close();
			    	}catch(Exception ex){
			    		ex.printStackTrace(System.out);
			    	}finally{
			    		session = null;
			    	}
			    }
			    sessions.remove(key);
			    logger.info(String.format("停止Session Key:[%s] successfully!", key));//  "停止Session:"+activemqUrl);
			}
		}
		
		if(!connections.isEmpty()){
			Connection connection = connections.get(key);
			if(connection != null){
				logger.info(String.format("正在停止Connection Key:[%s] value[%s]", key,connection));
			    if(connection != null){
			    	try{
			    		connection.close();
			    	}catch(Exception ex){
			    		ex.printStackTrace();
			    	}finally{
			    		connection = null;
			    	}
			    }
			    connections.remove(key);
			    logger.info(String.format("停止Connection Key:[%s] successfully!", key));
			}
		}
	}
	
	public void destroy() {
		connectionFactory = null;
	}
	
	public void restart(){
		stop();
		start();
	}
	
	public Session createConnectionAndSession(String key) throws JMSException{
		Connection conn = connectionFactory.createConnection();
		conn.setExceptionListener(new ExceptionListener(){
			@Override
			public void onException(JMSException ex) {
				System.out.println("ExceptionListener:"+ex.getMessage());
				ex.printStackTrace();
				fail = true;
			}
		});
		conn.start();
		connections.put(key, conn);
		Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		sessions.put(key, session);
		return session;
	}
	
	boolean failover = true;
	boolean fail = false;
	
	public Session getSession(String key){
		if(sessions != null && !sessions.isEmpty()){
			return sessions.get(key);
		}
		return null;
	}
	
	public MessageProducer getProducer(String key){
		if(producers != null && !producers.isEmpty()){
			return producers.get(key);
		}
		return null;
	}
	
	private void setupMessageConsumer(final String in,final String c_id_name) throws JMSException {
		String in_c_id_name = in+"_"+c_id_name;
		final Session session = createConnectionAndSession(in_c_id_name);
		NotifyMessageConsumerListener consumerListener = new NotifyMessageConsumerListener(in_c_id_name);
		Queue queueReceive 	= new ActiveMQQueue(in_c_id_name+"?consumer.prefetchSize=100");
		logger.info("初始化MQ监听 Consumer...@Queue:"+in_c_id_name+"初始化成功...");
		System.out.println("初始化MQ监听 Consumer...@Queue:"+in_c_id_name+"初始化成功...");
		//创建一个消费者，它只接受属于它自己的消息
		MessageConsumer queueConsumer = session.createConsumer(queueReceive);//, "receiver='" + name + "'");
		queueConsumer.setMessageListener(consumerListener);///*name, Receiver_KEY,*/server));//, this));
    }
	
	private MessageProducer setupMessageProducer(final String out,final String c_id_name) throws JMSException {
		String out_c_id_name = out+"_"+c_id_name;
		//Connection connection = createConnection(Sender_KEY);
		Queue queueSender = new ActiveMQQueue(out_c_id_name);
		logger.info("初始化MQ Producer...@Queue:"+out_c_id_name);
		System.out.println("初始化MQ监听 Producer...@Queue:"+out_c_id_name+"初始化成功...");
		Session session = createConnectionAndSession(out_c_id_name);//connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageProducer producer = session.createProducer(queueSender);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        producers.put(out_c_id_name, producer);
        return producer;
    }
}
