package com.bhu.vas.business.agent;

import com.bhu.vas.api.rpc.agent.model.AgentDeviceImportLog;
import com.bhu.vas.business.ds.agent.service.AgentDeviceImportLogService;
import com.smartwork.msip.localunit.BaseTest;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created by bluesand on 9/15/15.
 */
public class AgentDeviceImportLogTest extends BaseTest {

    @Resource
    public AgentDeviceImportLogService agentDeviceImportLogService;

    @Test
    public void insert() {
        AgentDeviceImportLog agentDeviceImportLog = new AgentDeviceImportLog();
        agentDeviceImportLog.setAid(123);
        agentDeviceImportLog.setStatus(0);
        agentDeviceImportLog.setCreated_at(new Date());
        agentDeviceImportLogService.insert(agentDeviceImportLog);
    }
}
