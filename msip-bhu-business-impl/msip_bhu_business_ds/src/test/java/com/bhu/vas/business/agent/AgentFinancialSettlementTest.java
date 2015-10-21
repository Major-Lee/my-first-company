package com.bhu.vas.business.agent;

import com.bhu.vas.api.rpc.agent.model.AgentFinancialSettlement;
import com.bhu.vas.business.ds.agent.service.AgentFinancialSettlementService;
import com.smartwork.msip.localunit.BaseTest;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * Created by bluesand on 10/21/15.
 */
public class AgentFinancialSettlementTest extends BaseTest {

    @Resource
    public AgentFinancialSettlementService agentFinancialSettlementService;



    @Test
    public void insert() {
        AgentFinancialSettlement agentFinancialSettlement = new AgentFinancialSettlement();

        agentFinancialSettlement.setUid(34);
        agentFinancialSettlement.setAid(12);
        agentFinancialSettlement.setAmount(22.3456);
        agentFinancialSettlement.setInvoice_fid("1234");
        agentFinancialSettlement.setReceipt_fid("456");
        agentFinancialSettlement.setRemark("备注");
        agentFinancialSettlement.setDetail("详细");
        agentFinancialSettlementService.insert(agentFinancialSettlement);

    }
}
