package com.smartwork.async.messagequeue.kafka.parser.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.smartwork.async.messagequeue.kafka.model.CommonMessage;
import com.smartwork.async.messagequeue.kafka.parser.iface.IMessageHandler;

public class CommonMessageHandler implements IMessageHandler<CommonMessage>{
	@Override
	public void handler(String topic, Map<Integer, List<CommonMessage>> value) {
		// TODO Auto-generated method stub
		System.out.println("	topic:"+topic);
		Iterator<Entry<Integer, List<CommonMessage>>> iter = value.entrySet().iterator();
		
		while(iter.hasNext()){
			Entry<Integer, List<CommonMessage>> element = iter.next();
			Integer index = element.getKey();
			List<CommonMessage> data = element.getValue();
			System.out.println("		index:"+index);
			for(Object d:data){
				System.out.println("			"+d);
				//if(d instanceof byte[]){
				//	System.out.println("			"+new String((byte[])d));
				//}
			}
		}
	}

}
