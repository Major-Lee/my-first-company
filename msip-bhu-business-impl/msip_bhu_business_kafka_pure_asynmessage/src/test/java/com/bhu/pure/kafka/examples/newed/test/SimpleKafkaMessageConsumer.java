package com.bhu.pure.kafka.examples.newed.test;

import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import com.bhu.pure.kafka.client.consumer.KafkaMessageConsumer;

public class SimpleKafkaMessageConsumer extends KafkaMessageConsumer<Integer, String>{

/*	@Override
	public Properties getClientProperties() {
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ConsumerConfig.GROUP_ID_CONFIG, this.getClass().getSimpleName());
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		return props;
	}*/


	@Override
	public String keyDeserializer() {
		return IntegerDeserializer.class.getName();
	}

	@Override
	public String valueDeserializer() {
		return StringDeserializer.class.getName();
	}
}
