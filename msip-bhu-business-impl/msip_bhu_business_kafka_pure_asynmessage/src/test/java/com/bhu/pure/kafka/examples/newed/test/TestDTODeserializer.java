package com.bhu.pure.kafka.examples.newed.test;

import com.bhu.pure.kafka.examples.newed.deserializer.DTODeserializer;

public class TestDTODeserializer extends DTODeserializer<TestDTO>{

	@Override
	public Class<TestDTO> getDTOClass() {
		return TestDTO.class;
	}

}
