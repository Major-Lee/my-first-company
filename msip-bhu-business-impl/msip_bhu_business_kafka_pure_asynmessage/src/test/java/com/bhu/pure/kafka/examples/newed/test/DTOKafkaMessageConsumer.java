package com.bhu.pure.kafka.examples.newed.test;

import com.bhu.pure.kafka.examples.newed.client.consumer.KafkaMessageConsumer;

public class DTOKafkaMessageConsumer extends KafkaMessageConsumer<String, TestDTO>{

/*	@Override
	public Properties getClientProperties() {
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ConsumerConfig.GROUP_ID_CONFIG, this.getClass().getSimpleName());
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, TestDTODeserializer.class.getName());
		return props;
	}*/

	@Override
	public long pollSize() {
		return DEFAULT_POLLSIZE;
	}

}
