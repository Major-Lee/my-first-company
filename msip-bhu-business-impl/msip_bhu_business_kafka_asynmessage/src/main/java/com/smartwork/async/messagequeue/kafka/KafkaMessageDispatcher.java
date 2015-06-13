package com.smartwork.async.messagequeue.kafka;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.messaging.Message;

import com.smartwork.async.messagequeue.kafka.parser.iface.IMessageHandler;

public class KafkaMessageDispatcher {
	
	public Map<String,IMessageHandler<? extends Object>> messageHandlers;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void dispatcher(Message<?> inMessage){
		//System.out.println("~~~~~~~~~gogo:"+inMessage);
		
		/*MessageHeaders headers = inMessage.getHeaders();
		System.out.println("	headers:"+headers);
		String header_topic = (String)headers.get("topic");
		
		System.out.println("	header_topic:"+header_topic);*/
		
		final Map<String, Map<Integer, List<Object>>> origData =
				(Map<String, Map<Integer, List<Object>>>) inMessage.getPayload();

		Iterator<Entry<String, Map<Integer, List<Object>>>> iter = origData.entrySet().iterator();
		while(iter.hasNext()){
			Entry<String, Map<Integer, List<Object>>> element = iter.next();
			IMessageHandler handler = messageHandlers.get(element.getKey());
			//IMessageHandler<?> handler = iMessageHandler;
			if(handler != null){
				handler.handler(element.getKey(), element.getValue());
			}else{
				System.err.println("not support ! MessageHandler implementation not defined!"+element.getKey());
			}
		}
		
	}	
	public Map<String, IMessageHandler<? extends Object>> getMessageHandlers() {
		return messageHandlers;
	}
	public void setMessageHandlers(Map<String, IMessageHandler<? extends Object>> messageHandlers) {
		this.messageHandlers = messageHandlers;
	}
	
}
