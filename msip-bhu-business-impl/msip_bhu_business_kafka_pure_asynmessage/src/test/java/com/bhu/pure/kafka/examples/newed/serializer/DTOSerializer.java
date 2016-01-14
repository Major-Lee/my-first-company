/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.bhu.pure.kafka.examples.newed.serializer;

import java.util.Map;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DTOSerializer<DTO> implements Serializer<DTO> {
	private final ObjectMapper objectMapper = new ObjectMapper(new JsonFactory());
	
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    	//nothing to do
    }

    @Override
    public byte[] serialize(String topic, DTO dto) {
        if (dto == null)
            return null;

        try {
        	return objectMapper.writeValueAsBytes(dto);
        } catch (Exception e) {
            throw new SerializationException("Error serializing JSON message", e);
        }
    }

	@Override
	public void close() {
		//nothing to do
	}

}