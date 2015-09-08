package com.bhu.vas.api.rpc.agent.iservice;

import com.bhu.vas.api.rpc.agent.model.AgentDeviceClaim;
import com.smartwork.msip.cores.orm.support.page.TailPage;

/**
 * Created by bluesand on 9/7/15.
 */
public interface IAgentRpcService {
//    /**
//     * 生成代理商
//     * @param agentDeviceClaim
//     */
//    void createAgent(AgentDeviceClaim agentDeviceClaim);
//
//    /**
//     * 设备认领代理商
//     * @param sn
//     * @param uid
//     * @param mac
//     */
//    void claimAgentDevice(String sn, int uid, String mac);

    /**
     * 代理商设备列表
     * @param uid
     * @param pageNo
     * @param pageSize
     * @return
     */
    TailPage<AgentDeviceClaim> pageClaimedAgentDevice(int uid, int pageNo, int pageSize);

    /**
     * 获取未认领的设备
     * @param pageNo
     * @param pageSize
     * @return
     */
    TailPage<AgentDeviceClaim> pageUnClaimAgentDevice(int pageNo, int pageSize);




}
