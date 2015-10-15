package com.bhu.vas.business.asyn.normal.activemq.multi;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.dto.CmCtxInfo;
import com.bhu.vas.business.asyn.normal.activemq.MQSession.ActiveMQSessions;
import com.bhu.vas.business.asyn.normal.activemq.listener.DynaMessageConsumerListener;
import com.bhu.vas.business.asyn.normal.activemq.model.QueueInfo;
import com.bhu.vas.business.asyn.normal.activemq.xml.XmlDynamic;
import com.bhu.vas.business.asyn.normal.activemq.xml.XmlServer;
import com.smartwork.msip.cores.helper.xml.jaxb.JAXBXMLHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * 多个active mq server host 连接管理
 * @author Edmond
 *
 */
public class ActiveMQConnectionsManager{
	private final Logger logger = LoggerFactory.getLogger(ActiveMQConnectionsManager.class);
	private Map<String,ActiveMQSessions> mqSessions;
	private Map<String,Session> sessions = null;
	private Map<String,MessageProducer> producers = null;
	private Map<String,MessageProducer> testProducers = null;
	private boolean xmlConfFile_Loaded = false;
	private String  xmlConfFile = null;//PropertiesRuntimeTest.class.getResource("/deploy/lazyloadconf/dynamic.activemq.properties").getFile();
	private JAXBXMLHelper xmlHelper = null;
	private XmlDynamic dynamicConf = null;
	//ActiveMQDynamicService dynamicService;
	private static class ActiveMQConnectionManagerHolder{ 
		private static ActiveMQConnectionsManager instance =new ActiveMQConnectionsManager(); 
		static{
			instance.initialize();
			instance.start();
		}
	} 
	
	public static ActiveMQConnectionsManager getInstance() { 
		return ActiveMQConnectionManagerHolder.instance; 
	}
    
	public ActiveMQConnectionsManager(){//ActiveMQDynamicService dynamicService) {
	}
	
	public void initialize() {
		mqSessions = new HashMap<String,ActiveMQSessions>();
		xmlHelper = new JAXBXMLHelper(XmlDynamic.class);
	}

	public void start(){
		System.out.println("ActiveMQConnectionsManager start:"+this);
	    try {
			xmlConfFile = ActiveMQConnectionsManager.class.getResource("/deploy/lazyloadconf/dynamic.activemq.xml").getFile();
			xmlConfFile_Loaded = true;
		} catch (Exception e) {
			try{
				xmlConfFile = ActiveMQConnectionsManager.class.getResource("/deploy/lazyloadconf/dynamic.activemq.xml").getFile();
				xmlConfFile_Loaded = true;
			}catch(Exception ex){
				logger.error("init loading /deploy/lazyloadconf/dynamic.activemq.xml or  /lazyloadconf/dynamic.activemq.xml failed!", e);
				e.printStackTrace(System.out);
			}
		}finally{
		}
	    if(xmlConfFile_Loaded){
			sessions = new HashMap<String,Session>();
			producers = new HashMap<String,MessageProducer>();
			testProducers = new HashMap<String,MessageProducer>();
	    	logger.info("初始化读取dynamic.activemq.xml 路径:"+xmlConfFile);
	    	dynamicConf = xmlHelper.fromXmlFile(xmlConfFile,false,XmlDynamic.class);
	    	if(dynamicConf != null){
	    		if(dynamicConf.getServers() == null || dynamicConf.getServers().isEmpty()){
	    			logger.info("初始化读取dynamic.activemq.xml内容没有mq server的配置");
	    		}else{
	    			for(XmlServer server:dynamicConf.getServers()){
	    				if(server.getQueues() == null || server.getQueues().isEmpty()){
	    					logger.info("不进行初始化MQ连接...@Url:"+server.getUrl()+" cause queues is empty");
	    				}else{
	    					mqSessions.put(server.uniqueKey(), ActiveMQSessions.build(server.getUrl()));
		    				logger.info("进行配置文件定义-初始化MQ连接...@Url:"+server.getUrl());
	    				}
	    			}
	    		}
	    	}else{
	    		logger.info("初始化读取dynamic.activemq.xml配置失败");
	    	}
	    }
	}

	public void initConsumerQueues(){
		if(xmlConfFile_Loaded){
			if(dynamicConf.getServers() != null && !dynamicConf.getServers().isEmpty()){
				for(XmlServer server:dynamicConf.getServers()){
					if(server.getQueues() != null && !server.getQueues().isEmpty()){
						for(String consumerQueue:server.getQueues()){
							createNewConsumerQueues(QueueInfo.build(server.getHost(), server.getPort(), consumerQueue),false);
						}
					}
				}
			}
		}
	}
	
	public void initProducerQueues(){
		if(xmlConfFile_Loaded){
			if(dynamicConf.getServers() != null && !dynamicConf.getServers().isEmpty()){
				for(XmlServer server:dynamicConf.getServers()){
					if(server.getQueues() != null && !server.getQueues().isEmpty()){
						for(String producerQueue:server.getQueues()){
							createNewProducerQueues(QueueInfo.build(server.getHost(), server.getPort(), producerQueue),false);
						}
					}
				}
			}
		}
	}	
	
	/**
	 * 仅仅测试时候针对消费queue建立producer
	 */
	public void initConsumerTestProducers(){
		if(xmlConfFile_Loaded){
			for(XmlServer server:dynamicConf.getServers()){
				for(String producerQueue:server.getQueues()){
					try {
						setupMessageTestProducer(QueueInfo.build(server.getHost(), server.getPort(), producerQueue),CmCtxInfo.UpPrefix);
					} catch (JMSException e) {
						e.printStackTrace(System.out);
					}
				}
			}
		}
	}
	
	public void createNewConsumerQueues(QueueInfo info,boolean noExistThenUpdatePropertie){
		if(xmlConfFile_Loaded){
			try {
				setupMessageConsumer(info,CmCtxInfo.UpPrefix);
				if(noExistThenUpdatePropertie){
					storeProperties(info);
				}
			} catch(BusinessI18nCodeException e){
				e.printStackTrace(System.out);
			} catch (JMSException e) {
				e.printStackTrace(System.out);
			} 
		}
	}
	
	public void createNewProducerQueues(QueueInfo info,boolean noExistThenUpdatePropertie){
		if(xmlConfFile_Loaded){
			try {
				setupMessageProducer(info,CmCtxInfo.DownPrefix);
				if(noExistThenUpdatePropertie){
					storeProperties(info);
				}
			} catch(BusinessI18nCodeException e){
				e.printStackTrace(System.out);
			} catch (JMSException e) {
				e.printStackTrace(System.out);
			} 
		}
	}
	
	/**
	 * 创建 mq connection 和 session
	 * @param info queue信息 包括 host port queuename
	 * @param key map中存储connection和session的key
	 * @return
	 * @throws JMSException
	 */
	public Session createConnectionAndSession(final QueueInfo info,String key) throws JMSException{
		String uniqueKey = info.uniqueKey();
		ActiveMQSessions activeMQSession = mqSessions.get(uniqueKey);
		if(activeMQSession == null){//activemq 连接不存在
			activeMQSession = ActiveMQSessions.build(info.activeMqUrl());
			mqSessions.put(uniqueKey, activeMQSession);
			logger.info("进行生产环境动态 - 初始化创建MQ连接...@Url:"+info.activeMqUrl() + " uniqueKey:"+uniqueKey);
		}else{
			logger.info("进行生产环境动态 - 连接已存在...@Url:"+info.activeMqUrl() + " uniqueKey:"+uniqueKey);
		}
		
		if(activeMQSession.getConnections().containsKey(key) || activeMQSession.getSessions().containsKey(key)){
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_ALREADYEXIST);
		}
		
		Connection conn = activeMQSession.getConnectionFactory().createConnection();
		conn.setExceptionListener(new ExceptionListener(){
			@Override
			public void onException(JMSException ex) {
				System.out.println("ExceptionListener:"+ex.getMessage());
				ex.printStackTrace();
				//fail = true;
			}
		});
		conn.start();
		activeMQSession.getConnections().put(key, conn);
		Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		activeMQSession.getSessions().put(key, session);
		sessions.put(key, session);
		return session;
	}
	
	//boolean failover = true;
	//boolean fail = false;
	private void setupMessageConsumer(final QueueInfo info,final String in) throws JMSException {
		String in_c_id_name = in+info.getQueue();
		final Session session = createConnectionAndSession(info,in_c_id_name);
		DynaMessageConsumerListener consumerListener = new DynaMessageConsumerListener(in_c_id_name,info.getQueue());
		Queue queueReceive 	= new ActiveMQQueue(in_c_id_name+"?consumer.prefetchSize=100");
		logger.info("初始化MQ监听 Consumer...@Queue:"+in_c_id_name+"初始化成功...");
		//System.out.println("初始化MQ监听 Consumer...@Queue:"+in_c_id_name+"初始化成功...");
		//创建一个消费者，它只接受属于它自己的消息
		MessageConsumer queueConsumer = session.createConsumer(queueReceive);//, "receiver='" + name + "'");
		queueConsumer.setMessageListener(consumerListener);///*name, Receiver_KEY,*/server));//, this));
    }
	
	private MessageProducer setupMessageProducer(final QueueInfo info,final String out) throws JMSException {
		String out_c_id_name = out+info.getQueue();
		final Session session = createConnectionAndSession(info,out_c_id_name);//connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		//Connection connection = createConnection(Sender_KEY);
		Queue queueSender = new ActiveMQQueue(out_c_id_name);
		logger.info("初始化MQ Producer...@Queue:"+out_c_id_name);
		//System.out.println("初始化MQ监听 Producer...@Queue:"+out_c_id_name+"初始化成功...");
		MessageProducer producer = session.createProducer(queueSender);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        producers.put(out_c_id_name, producer);
        return producer;
    }
	
	private MessageProducer setupMessageTestProducer(final QueueInfo info,final String in) throws JMSException {
		String in_c_id_name = in+info.getQueue();
		//Connection connection = createConnection(Sender_KEY);
		Queue queueSender = new ActiveMQQueue(in_c_id_name);
		logger.info("初始化MQ TestProducer...@Queue:"+in_c_id_name);
		//System.out.println("初始化MQ监听 TestProducer...@Queue:"+in_c_id_name+"初始化成功...");
		Session session = createConnectionAndSession(info,in_c_id_name);//connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageProducer producer = session.createProducer(queueSender);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        testProducers.put(in_c_id_name, producer);
        return producer;
    }

	public void stop() {
		String activemqUrl = null;//JingGlobals.getXMLProperty("mq.activemq.url", "failover:(tcp://192.168.1.2:61616)");
		logger.info("停止MQ监听...@Url:"+activemqUrl);
		Iterator<Entry<String,ActiveMQSessions>> iterSession = mqSessions.entrySet().iterator(); 
		while (iterSession.hasNext()) { 
		    Map.Entry<String,ActiveMQSessions> entry = iterSession.next(); 
		    String key = entry.getKey(); 
		    ActiveMQSessions sess = entry.getValue();
		    logger.info(String.format("正在停止MQSessions Key:[%s] value[%s]", key,sess));
		    if(sess != null){
		    	try{
		    		sess.closeAll();
		    	}catch(Exception ex){
		    		ex.printStackTrace(System.out);
		    	}finally{
		    		sess = null;
		    	}
		    }
		    logger.info(String.format("停止MQSessions Key:[%s] successfully!", key));//  "停止Session:"+activemqUrl);
		}
		mqSessions.clear();
	}

	/**
	 * 有新的mq host加入 则更新
	 * 有新的queue 加入 则更新
	 * @param info
	 */
	private synchronized void storeProperties(QueueInfo info){
		if(xmlConfFile_Loaded && dynamicConf != null){
			boolean needUpdated = true;
			boolean hostexist  = false;
			for(XmlServer server:dynamicConf.getServers()){
				if(server.uniqueKey().equals(info.uniqueKey())){
					hostexist = true;
					boolean added = server.addQueue(info.getQueue());
					if(added){
						needUpdated = true;
					}else{
						needUpdated = false;
					}
					break;
					/*if(server.getQueues().contains(info.getQueue())){
						needUpdated = false;
					}else{
						server.getQueues().add(info.getQueue());
						break;
					}*/
				}
			}
			if(!hostexist){//添加 host server 节点
				XmlServer newMqServer = new XmlServer();
				newMqServer.setHost(info.getHost());
				newMqServer.setPort(info.getPort());
				newMqServer.setUrl(info.activeMqUrl());
				newMqServer.addQueue(info.getQueue());
				dynamicConf.getServers().add(newMqServer);
				needUpdated = true;
			}
			if(needUpdated){
				xmlHelper.toXmlFile(xmlConfFile, dynamicConf, "utf-8", false);
				logger.info("更新xml配置文件成功...@Url:"+info);
			}
		}
		
		/*String consumers_defined = properties.getProperty(consumers_key,StringHelper.EMPTY_STRING_GAP);
		String producers_defined = properties.getProperty(producers_key,StringHelper.EMPTY_STRING_GAP);
		boolean needStoreUpdate = false;
		if(!consumers_defined.contains(cmInfo)){
			properties.setProperty(consumers_key, consumers_defined.concat(StringHelper.COMMA_STRING_GAP).concat(cmInfo));
			needStoreUpdate = true;
		}
		if(!producers_defined.contains(cmInfo)){
			properties.setProperty(producers_key, producers_defined.concat(StringHelper.COMMA_STRING_GAP).concat(cmInfo));
			needStoreUpdate = true;
		} 
		if(needStoreUpdate){
			OutputStream outputStream = null;
			try {
				outputStream = new FileOutputStream(porperties_file);
				properties.store(outputStream, "author: liwh@bhunetworks.com"); 
	            outputStream.close();
			} catch (IOException e) {
				e.printStackTrace(System.out);
			} finally{
				if(outputStream != null){
					outputStream = null;
				}
			}
		}*/
	}
	
	public Session getSession(String key){
		//System.out.println("~~~~~~~~~~~~~~getSession:"+sessions.keySet());
		if(sessions != null && !sessions.isEmpty()){
			return sessions.get(key);
		}
		return null;
	}
	
	public MessageProducer getProducer(String key){
		//System.out.println("~~~~~~~~~~~~~~getProducer:"+sessions.keySet());
		if(producers != null && !producers.isEmpty()){
			return producers.get(key);
		}
		return null;
	}
	
	public Set<String> currentProducerKeys(){
		return this.producers.keySet();
	}
	
	public Set<String> currentTestProducerKeys(){
		return this.testProducers.keySet();
	}
	
	public MessageProducer getTestProducer(String key){
		if(testProducers != null && !testProducers.isEmpty()){
			return testProducers.get(key);
		}
		return null;
	}
	
	public void restart(){
		stop();
		start();
	}
	
	public void destroy() {
	}
}
