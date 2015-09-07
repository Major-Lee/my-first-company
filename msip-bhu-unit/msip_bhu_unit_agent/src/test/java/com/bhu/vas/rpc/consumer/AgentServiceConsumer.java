package com.bhu.vas.rpc.consumer;

import com.bhu.vas.api.rpc.agent.iservice.IAgentRpcService;
import com.bhu.vas.api.rpc.vap.iservice.IVapRpcService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by bluesand on 9/7/15.
 */
public class AgentServiceConsumer {

    public static void main(String[] args) throws Exception{
        System.setProperty("appname", "BHUUserRpcConsumerApp");
        System.setProperty("zookeeper", "192.168.66.7:2181");
        System.setProperty("provider.port", "");
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {
                "classpath*:spring/applicationContextCore-resource.xml",
                "classpath*:/com/bhu/vas/rpc/consumer/applicationContextRpcUnitConsumer.xml" });
        context.start();

        IAgentRpcService agentRpcService = (IAgentRpcService)context.getBean("agentRpcService");
        System.out.println(agentRpcService.hello());

        Thread.currentThread().join();
    }
}
