package com.bhu.vas.business.index.consumer;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.pure.kafka.business.observer.KafkaMsgIndexManager;
import com.bhu.pure.kafka.client.consumer.StringKafkaMessageConsumer;
import com.bhu.pure.kafka.client.consumer.callback.PollIteratorNotify;
import com.smartwork.msip.plugins.hook.observer.ExecObserverManager;

public class IncrementReceivePerformMessageTopicConsumer extends StringKafkaMessageConsumer{
	private final Logger logger = LoggerFactory.getLogger(IncrementReceivePerformMessageTopicConsumer.class);
	private final String LoggerFormatTemplate = "Increment Perform Receive: topic[%s] partition[%s] key[%s] value[%s] offset[%s] consumerId[%s]";
	public void init(){		
		ScheduledExecutorService delay_exec = (ScheduledExecutorService) ExecObserverManager.
				buildNewScheduledThreadPool(this.getClass(), "IncrementReceivePerformMessageTopicConsumer Delay load", 1);
		delay_exec.schedule(new Runnable() {
			@Override
			public void run() {
				//System.out.println("IncrementReceivePerformMessageTopicConsumer Init Sleep");
				doSubscribeTopics(new PollIteratorNotify<ConsumerRecords<String,String>>(){
					@Override
					public void notifyComming(String consumerId, ConsumerRecords<String, String> records) {
						for(ConsumerRecord<String, String> record : records){
							logger.info(String
									.format(LoggerFormatTemplate,
											record.topic(), record.partition(),
											record.key(), record.value(),
											record.offset(), consumerId));
							KafkaMsgIndexManager.IncrementPerformMsgCommingObserver.notifyMsgComming(record.topic(), record.partition(), record.key(), record.value(), record.offset(), consumerId);
						}
					}
				});
				//System.out.println("CMNotifyTopicConsumer Init End");
			}}, 5, TimeUnit.SECONDS);
	}
}
