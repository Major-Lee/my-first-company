/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.smartwork.async.messagequeue.kafka.outbound;

import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.Transformer;
import org.springframework.messaging.Message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Soby Chacko
 */
public class PartitionlessTransformer implements Transformer {
	@Override
	@SuppressWarnings("unchecked")
	public Message<?> transform(final Message<?> message) {
		System.out.println("--------:"+message.getPayload());
		final Map<String, Map<Integer, List<Object>>> origData =
				(Map<String, Map<Integer, List<Object>>>) message.getPayload();

		final Map<String, List<Object>> nonPartitionedData = new HashMap<>();

		for(final String topic : origData.keySet()) {
			//System.out.println("	topic:"+topic);
			final Map<Integer, List<Object>> partitionedData = origData.get(topic);
			onMessage(topic,partitionedData);
			final Collection<List<Object>> nonPartitionedDataFromTopic = partitionedData.values();

			final List<Object> mergedList = new ArrayList<>();
			
			for (final List<Object> l : nonPartitionedDataFromTopic){
				/*for(Object obj:l){
					if(obj instanceof byte[]){
						System.out.println("byte[]:"+new String((byte[])obj));
					}else if(obj instanceof String){
						System.out.println("String:"+obj);
					}else{
						System.out.println("Object:"+obj);
					}
				}*/
				mergedList.addAll(l);
			}

			nonPartitionedData.put(topic, mergedList);
		}

		return MessageBuilder.withPayload(nonPartitionedData).build();
	}
	
	public void onMessage(String topic,Object value){
		System.out.println("	topic:"+topic+ "	value:"+value);
	}
}
