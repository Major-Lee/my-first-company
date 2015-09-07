package com.bhu.vas.rpc.facade;

import com.bhu.vas.business.ds.user.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by bluesand on 9/7/15.
 */
@Service
public class AgentFacadeService {

    @Resource
    public UserService userService;

    public String hello() {
        System.out.println("hello, world!!!");
        return "hello, worlld!!!!";
    }
}
