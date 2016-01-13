package com.bhu.pure.kafka.examples.newed.test;

import java.util.Properties;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import com.bhu.pure.kafka.examples.newed.client.producer.KafkaMessageProducer;

public class CustomKafkaMessageProducer extends KafkaMessageProducer<Integer, String>{

	@Override
	public Properties getClientProperties() {
		Properties props = new Properties();
	    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
	    props.put(ProducerConfig.CLIENT_ID_CONFIG, "CustomKafkaMessageProducer");
	    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
	    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		return props;
	}

}
