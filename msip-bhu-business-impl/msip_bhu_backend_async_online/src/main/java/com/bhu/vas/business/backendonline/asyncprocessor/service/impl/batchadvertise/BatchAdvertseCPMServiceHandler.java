package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchadvertise;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.business.backendonline.asyncprocessor.service.iservice.IMsgHandlerService;

@Service
public class BatchAdvertseCPMServiceHandler implements IMsgHandlerService{
	private final Logger logger = LoggerFactory
			.getLogger(BatchAdvertseCPMServiceHandler.class);
	@Override
	public void process(String message) {
		
	}

}
