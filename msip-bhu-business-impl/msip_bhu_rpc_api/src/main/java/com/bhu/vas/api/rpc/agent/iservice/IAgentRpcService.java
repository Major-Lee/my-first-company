package com.bhu.vas.api.rpc.agent.iservice;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.agent.vto.AgentDeviceStatisticsVTO;
import com.bhu.vas.api.rpc.agent.vto.AgentRevenueStatisticsVTO;
import com.bhu.vas.api.rpc.agent.vto.DailyRevenueRecordVTO;
import com.bhu.vas.api.rpc.agent.vto.SettlementPageVTO;
import com.bhu.vas.api.vto.agent.AgentBulltinBoardVTO;
import com.bhu.vas.api.vto.agent.AgentDeviceClaimVTO;
import com.bhu.vas.api.vto.agent.AgentDeviceImportLogVTO;
import com.bhu.vas.api.vto.agent.AgentDeviceVTO;
import com.smartwork.msip.cores.orm.support.page.TailPage;

/**
 * Created by bluesand on 9/7/15.
 */
public interface IAgentRpcService {

      String PATH_INPUT_PREFIX = "/BHUData/agent/input";
      String PATH_OUTPUT_PREFIX = "/BHUData/agent/output";

//    /**
//     * 生成代理商
//     * @param agentDeviceClaim
//     */
//    void createAgent(AgentDeviceClaim agentDeviceClaim);
//
    /**
     * 设备认领代理商
     * @param sn
     */
    int claimAgentDevice(String sn);

//    /**
//     * 代理商设备列表
//     * @param uid
//     * @param pageNo
//     * @param pageSize
//     * @return
//     */
//    TailPage<AgentDeviceClaimVTO> pageClaimedAgentDeviceByUid(int uid, int pageNo, int pageSize);


    /**
     *
     * @param uid
     * @param type
     * @param pageNo
     * @param pageSize
     * @return
     */
    AgentDeviceVTO pageClaimedAgentDeviceByUid(int uid, int status, int pageNo, int pageSize);


    /**
     * 管理员获取代理商列表
     * @param pageNo
     * @param pageSize
     * @return
     */
    TailPage<AgentDeviceClaimVTO> pageClaimedAgentDevice(int pageNo, int pageSize);

    /**
     * 获取具体代理商未认领的设备
     * @param uid
     * @param pageNo
     * @param pageSize
     * @return
     */
    TailPage<AgentDeviceClaimVTO> pageUnClaimAgentDeviceByUid(int uid, int pageNo, int pageSize);

    /**
     * 获取未认领的设备
     * @param pageNo
     * @param pageSize
     * @return
     */
    TailPage<AgentDeviceClaimVTO> pageUnClaimAgentDevice(int pageNo, int pageSize);

    /**
     * 代理商导入设备
     * @param uid
     * @param aid
     * @param inputPath excel文件位置
     * @param outputPath excel文件位置
     * @param originName 文件原始名称
     */
    void importAgentDeviceClaim(int uid, int aid, String inputPath, String outputPath, String originName);


    /**
     * 代理商导入日志
     * @param pageNo
     * @param pageSize
     * @return
     */
    TailPage<AgentDeviceImportLogVTO> pageAgentDeviceImportLog(int pageNo, int pageSize);

    /**
     * 代理商首页面的统计数据，包括本月收入，上月收入，昨日收入，总上线设备数，总收入数以及图表数据
     * @param uid
     * @param enddate 截止日期
     * @return
     */
    public RpcResponseDTO<AgentRevenueStatisticsVTO> statistics(int uid, String enddate);
    
    /**
     * 代理商首页面首页面 每日历史收益列表
     * @param uid
     * @param dateEndStr
     * @param pageNo
     * @param pageSize 
     * @return
     */
    public RpcResponseDTO<TailPage<DailyRevenueRecordVTO>> pageHistoryRecords(int uid,String dateEndStr,int pageNo, int pageSize);

    /**
     * 代理商结算列表页面
     * @param uid
     * @param pageNo
     * @param pageSize
     * @return
     */
    public RpcResponseDTO<SettlementPageVTO> pageSettlements(int uid,String dateCurrent,int pageNo, int pageSize);
    
    /**
     * 获取代理商代理设备的所有数量、在线数量、离线数量
     * @param agentuser
     * @return
     */
    public RpcResponseDTO<AgentDeviceStatisticsVTO> fetchAgentDeviceStatistics(int agentuser);
    /**
     * 获取公告
     * @param bid
     * @return
     */
    AgentBulltinBoardVTO findAgentBulltinBoardById(long bid);

    /**
     * 获取代理商公告
     * @param uid
     * @param pageNo
     * @param pageSize
     * @return
     */
    TailPage<AgentBulltinBoardVTO> pageAgentBulltinBoardByUid(int uid, int pageNo, int pageSize);


}
