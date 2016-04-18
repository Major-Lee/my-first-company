package com.bhu.vas.rpc.restful.impl;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Service;

import com.bhu.vas.rpc.restful.iface.ITestRestfulRpcService;
import com.bhu.vas.rpc.restful.model.Test;
//http://zyg345646335.iteye.com/blog/2208899
@Path("test")
@Service("testRpcService")
public class TestRestfulRpcService implements ITestRestfulRpcService{

	@POST
    @Path("register")
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
	@Produces({MediaType.APPLICATION_JSON})
	@Override
	public Test register(Test test) {
		return test;
	}

	@Override
	public void remove(Test test) {
		// TODO Auto-generated method stub
	}
}
