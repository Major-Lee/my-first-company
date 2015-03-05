package com.smartwork.im.extension.support.mq.activemq;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartwork.im.utils.JingGlobals;


public class ActiveMQConnectionManager{
	private final Logger logger = LoggerFactory.getLogger(ActiveMQConnectionManager.class);
	private ActiveMQConnectionFactory connectionFactory = null;
	private Map<String,Connection> connections = null;//new HashMap<String,Connection>();
	private Map<String,Session> sessions = null;
	
	private static class ActiveMQConnectionManagerHolder{ 
		private static ActiveMQConnectionManager instance =new ActiveMQConnectionManager(); 
		static{
			instance.initialize();
			instance.start();
		}
	} 
	
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static ActiveMQConnectionManager getInstance() { 
		return ActiveMQConnectionManagerHolder.instance; 
	}
    
	public ActiveMQConnectionManager() {
		//super("ActiveMQConnectionManager");
	}
	
	public void initialize() {
		String activemqUrl = JingGlobals.getXMLProperty("mq.activemq.url", "failover:(tcp://192.168.1.2:61616)");
		connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER,ActiveMQConnection.DEFAULT_USER,activemqUrl);//"tcp://192.168.101.251:61616");
		connections = new HashMap<String,Connection>(); 
		sessions = new HashMap<String,Session>();
		logger.info("初始化MQ监听...@Url:"+activemqUrl);
	}

	public void start(){
		
	}

	public void stop() {
		String activemqUrl = JingGlobals.getXMLProperty("mq.activemq.url", "failover:(tcp://192.168.1.2:61616)");
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
		if(sessions != null){
			return sessions.get(key);
		}
		return null;
	}
	
}
