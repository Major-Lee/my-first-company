package com.bhu.vas.business.asyn.normal.activemq.MQSession;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.jms.Connection;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class ActiveMQSessions {
	private ActiveMQConnectionFactory connectionFactory = null;
	private Map<String,Connection> connections = null;//new HashMap<String,Connection>();
	private Map<String,Session> sessions = null;
	private Map<String,MessageProducer> producers = null;
	public ActiveMQConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}
	public void setConnectionFactory(ActiveMQConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}
	public Map<String, Connection> getConnections() {
		return connections;
	}
	public void setConnections(Map<String, Connection> connections) {
		this.connections = connections;
	}
	public Map<String, Session> getSessions() {
		return sessions;
	}
	public void setSessions(Map<String, Session> sessions) {
		this.sessions = sessions;
	}
	public Map<String, MessageProducer> getProducers() {
		return producers;
	}
	public void setProducers(Map<String, MessageProducer> producers) {
		this.producers = producers;
	}
	
	public static ActiveMQSessions build(String activemqUrl){
		ActiveMQSessions result = new ActiveMQSessions();
		result.setConnectionFactory(new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER,ActiveMQConnection.DEFAULT_USER,activemqUrl));//"tcp://192.168.101.251:61616"););
		result.setConnections(new HashMap<String,Connection>());
		result.setSessions(new HashMap<String,Session>());
		result.setProducers(new HashMap<String,MessageProducer>());
		return result;
	}
	
	public void closeAll(){
		Iterator<Entry<String,Session>> iterSession = sessions.entrySet().iterator(); 
		while (iterSession.hasNext()) { 
		    Map.Entry<String,Session> entry = iterSession.next(); 
		    String key = entry.getKey(); 
		    Session conn = entry.getValue();
		    System.out.println(String.format("正在停止Session Key:[%s] value[%s]", key,conn));
		    if(conn != null){
		    	try{
		    		conn.close();
		    	}catch(Exception ex){
		    		ex.printStackTrace();
		    	}finally{
		    		conn = null;
		    	}
		    }
		    System.out.println(String.format("停止Session Key:[%s] successfully!", key));
		} 
		sessions.clear();
		Iterator<Entry<String,Connection>> iterConn = connections.entrySet().iterator(); 
		while (iterConn.hasNext()) { 
		    Map.Entry<String,Connection> entry = iterConn.next(); 
		    String key = entry.getKey(); 
		    Connection conn = entry.getValue();
		    System.out.println(String.format("正在停止Connection Key:[%s] value[%s]", key,conn));
		    if(conn != null){
		    	try{
		    		conn.close();
		    	}catch(Exception ex){
		    		ex.printStackTrace();
		    	}finally{
		    		conn = null;
		    	}
		    }
		    System.out.println(String.format("停止Connection Key:[%s] successfully!", key));
		} 
		connections.clear();
		connectionFactory = null;
	}
	
	public void closeKey(String key){
		if(!sessions.isEmpty()){
			Session session = sessions.get(key);
			if(session != null){
				System.out.println(String.format("正在停止Session Key:[%s] value[%s]", key,session));
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
			    System.out.println(String.format("停止Session Key:[%s] successfully!", key));//  "停止Session:"+activemqUrl);
			}
		}
		
		if(!connections.isEmpty()){
			Connection connection = connections.get(key);
			if(connection != null){
				System.out.println(String.format("正在停止Connection Key:[%s] value[%s]", key,connection));
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
			    System.out.println(String.format("停止Connection Key:[%s] successfully!", key));
			}
		}
	}
}
