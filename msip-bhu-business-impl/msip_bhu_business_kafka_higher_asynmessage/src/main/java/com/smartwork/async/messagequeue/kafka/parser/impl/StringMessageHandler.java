package com.smartwork.async.messagequeue.kafka.parser.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.smartwork.async.messagequeue.kafka.parser.iface.IMessageHandler;


public class StringMessageHandler implements IMessageHandler<byte[]>{

	@Override
	public void handler(String topic, Map<Integer, List<byte[]>> value) {
		System.out.println("	topic:"+topic);
		Iterator<Entry<Integer, List<byte[]>>> iter = value.entrySet().iterator();
		while(iter.hasNext()){
			Entry<Integer, List<byte[]>> element = iter.next();
			Integer index = element.getKey();
			List<byte[]> data = element.getValue();
			System.out.println("		index:"+index);
			for(Object d:data){
				//if(d instanceof byte[]){
				System.out.println("			"+new String((byte[])d));//.substring(6));
				//}
			}
		}
	}

}
