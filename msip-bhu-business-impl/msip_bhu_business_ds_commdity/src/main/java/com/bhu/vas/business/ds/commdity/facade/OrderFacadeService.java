package com.bhu.vas.business.ds.commdity.facade;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.business.ds.commdity.service.OrderService;

@Service
public class OrderFacadeService {
	
	@Resource
	private OrderService orderService;
}
