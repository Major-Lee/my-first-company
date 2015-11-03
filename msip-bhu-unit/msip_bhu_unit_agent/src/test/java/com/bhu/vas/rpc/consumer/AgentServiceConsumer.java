package com.bhu.vas.rpc.consumer;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.rpc.agent.iservice.IAgentRpcService;
import com.bhu.vas.api.rpc.agent.iservice.IAgentUserRpcService;
import com.smartwork.msip.cores.helper.JsonHelper;

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

        IAgentUserRpcService agentUserRpcService = (IAgentUserRpcService)context.getBean("agentUserRpcService");

        System.out.println(JsonHelper.getJSONString(agentRpcService.pageUnClaimAgentDevice(10016,1, 5)));

        System.out.println(agentUserRpcService.tokenValidate("6","NTtMV1JXARFBSENdXVYN").getPayload().booleanValue());

//        System.out.println(JsonHelper.getJSONString(agentRpcService.pageClaimedAgentDeviceByUid(6, 1, 5)));



        Thread.currentThread().join();
        context.close();
    }
}
