package com.bhu.vas.business.asyn.kafka.consumer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.Message;

import com.bhu.vas.business.observer.QueueMsgObserverManager;

/**
 * Date: 2008-8-28
 * Time: 17:10:34
 */
public class KafkaMessageQueueConsumer implements Runnable{
	
	private QueueChannel channel;
	@PostConstruct
	public void initialize(){
		//QueueMsgObserverManager.CmMessageObserver.addCmMessageListener(this);
		//初始化ActiveMQConnectionManager
		//ActiveMQConnectionManager.getInstance().initConsumerQueues();
    	new Thread(this).start();
    }
    
	@SuppressWarnings({ "rawtypes", "unchecked" })
    public void run(){
    	Message msg;		
		while((msg = channel.receive()) != null) {
			Map map = (HashMap)msg.getPayload();
			Set<Map.Entry> set = map.entrySet();
			for (Map.Entry entry : set) {
				String topic = (String)entry.getKey();
				System.out.println("Topic:" + topic);
				Map<Integer,List<byte[]>> messages = (ConcurrentHashMap<Integer,List<byte[]>>)entry.getValue();
				Collection<List<byte[]>> values = messages.values();
				for (Iterator<List<byte[]>> iterator = values.iterator(); iterator.hasNext();) {
					List<byte[]> list = iterator.next();
					for (byte[] object : list) {
						try{
							String message = new String(object,"UTF-8");
							QueueMsgObserverManager.SpringKafkaQueueMessageObserver.notifyMsgComming(message);
							System.out.println("	:"+message);
						}catch(Exception ex){
							ex.printStackTrace(System.out);
						}
					}
				}
			}
		}
    }
    
	public QueueChannel getChannel() {
		return channel;
	}
	public void setChannel(QueueChannel channel) {
		this.channel = channel;
	}
}
