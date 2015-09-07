package com.bhu.vas.rpc.facade;

import org.springframework.stereotype.Service;

/**
 * Created by bluesand on 9/7/15.
 */
@Service
public class AgentFacadeService {

    public String hello() {
        System.out.println("hello, world!!!");
        return "hello, worlld!!!!";
    }
}
