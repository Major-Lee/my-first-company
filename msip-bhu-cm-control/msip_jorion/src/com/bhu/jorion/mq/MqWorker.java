package com.bhu.jorion.mq;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.jorion.JOrion;
import com.bhu.jorion.JOrionConfig;
import com.bhu.jorion.util.StringHelper;

public class MqWorker implements Runnable{
    private final static Logger LOGGER = LoggerFactory.getLogger(MqWorker.class);
	private Session session;
	private Map<String, MessageConsumer> consumerMap;
	private Map<String, MessageProducer> publisherMap;
	private MessageProducer mangQueueProducer;
	private JOrion orion;
	
	
	public MqWorker(JOrion orion) {
		consumerMap = new ConcurrentHashMap<String, MessageConsumer>();
		publisherMap = new ConcurrentHashMap<String, MessageProducer>();
		this.orion = orion;
	}
	
	public  boolean ursidsJoin(String id){
//		long start = System.currentTimeMillis();
		synchronized(this){
			try {
				String q_id = "down_" + id;
				MessageConsumer c = consumerMap.get(id);
				if (c == null) {
					c = session.createConsumer(session.createQueue(q_id));
					LOGGER.debug("set mq message listen on " + q_id);
					c.setMessageListener(new MqMessageListener(orion, id));
					consumerMap.put(id, c);
				}
				MessageProducer p = publisherMap.get(id);
				if (p == null) {
					publisherMap.put(id, session.createProducer(session
									.createQueue("up_" + id)));
				}
			} catch (JMSException e) {
				LOGGER.error(StringHelper.getStackTrace(e));
				e.printStackTrace();
				return false;
			}
		}
//		LOGGER.debug("publish business mq cost:" + (System.currentTimeMillis() - start));
		return true;
	}
	public  void ursidsLeave(String id){
		synchronized(this){
			try {
				String q_id = "down_" + id;
				MessageConsumer c = consumerMap.get(id);
				if (c != null) {
					consumerMap.remove(id);
					c.setMessageListener(null);
					c.close();
					LOGGER.debug("remove consumer from mq session, on " + q_id);
				}
				MessageProducer p = publisherMap.get(id);
				if (p != null) {
					publisherMap.remove(id);
					p.close();
					p = null;
				}
			} catch (JMSException e) {
				LOGGER.error(StringHelper.getStackTrace(e));
				e.printStackTrace();
			}
		}
	}

	
	public  void publishManagementMessage(String msg){
//		long start = System.currentTimeMillis();
		synchronized(this){
			try{
				TextMessage m = session.createTextMessage(msg);
				LOGGER.debug("Sending ursids mng message to mq" + msg);
				mangQueueProducer.send(m);
			}catch(JMSException e){
				LOGGER.error(StringHelper.getStackTrace(e));
				e.printStackTrace();
			}
		}
//		LOGGER.debug("publish mng mq cost:" + (System.currentTimeMillis() - start));
	}
	
	public  void publishBusiness(String id, String msg){
//		long start = System.currentTimeMillis();
//		synchronized(this){
			try{
				MessageProducer p = publisherMap.get(id);
				if(p != null){
					TextMessage m = session.createTextMessage(msg);
					LOGGER.debug("Sending message to mq " + id + "\n" + msg);
					p.send(m);
				}
			}catch(JMSException e){
				LOGGER.error(StringHelper.getStackTrace(e));
				e.printStackTrace();
			}
//		}
			
//		LOGGER.debug("publish business mq cost:" + (System.currentTimeMillis() - start));
	}
	
	public void run(){
		try {
	    	LOGGER.info("Mq Server:" + JOrionConfig.MQ_URL);

	       ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(JOrionConfig.MQ_URL);
	       factory.setUseAsyncSend(true);
	        Connection connection;
				connection = factory.createConnection("admin", "admin");
	        connection.start();
	        LOGGER.info("Mq connected!");
	        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	        mangQueueProducer = session.createProducer(session.createQueue(JOrionConfig.MANAGEMENT_MQ_NAME));
	        LOGGER.info("Mq managemnet queue created!");
		} catch (JMSException e) {
			LOGGER.error(StringHelper.getStackTrace(e));
			e.printStackTrace();
		}
	}

	
    public void start(){
//		new Thread(this, "mq_eceiver").start();
    	run();
    }

}

