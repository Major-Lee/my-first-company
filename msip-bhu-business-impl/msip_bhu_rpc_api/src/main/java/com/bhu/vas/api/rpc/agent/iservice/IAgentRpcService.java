package com.bhu.vas.api.rpc.agent.iservice;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.agent.vto.AgentDeviceStatisticsVTO;
import com.bhu.vas.api.rpc.agent.vto.AgentRevenueStatisticsVTO;
import com.bhu.vas.api.rpc.agent.vto.DailyRevenueRecordVTO;
import com.bhu.vas.api.rpc.agent.vto.SettlementPageVTO;
import com.bhu.vas.api.vto.agent.*;
import com.smartwork.msip.cores.orm.support.page.TailPage;

/**
 * Created by bluesand on 9/7/15.
 */
public interface IAgentRpcService {

    String PATH_INPUT_PREFIX = "/BHUData/agent/input";
    String PATH_OUTPUT_PREFIX = "/BHUData/agent/output";
    String PATH_INVOICE_PREFIX = "/BHUData/agent/invoice";
    String PATH_RECEIPT_PREFIX = "/BHUData/agent/receipt";

    /**
     * 设备认领代理商
     * @param sn
     */
    int claimAgentDevice(String sn, String mac, String hdtype);


    /**
     *
     * @param uid
     * @param status
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
    AgentDeviceVTO pageClaimedAgentDevice(int uid, int status, int pageNo, int pageSize);


    /**
     * 获取具体代理商未认领的设备
     * @param uid
     * @param pageNo
     * @param pageSize
     * @return
     */
    AgentDeviceVTO pageUnClaimAgentDeviceByUid(int uid, int pageNo, int pageSize);

    /**
     * 获取未认领的设备
     * @param pageNo
     * @param pageSize
     * @return
     */
    AgentDeviceVTO pageUnClaimAgentDevice(int pageNo, int pageSize);

    /**
     * 代理商导入设备
     * @param uid
     * @param aid
     * @param inputPath excel文件位置
     * @param outputPath excel文件位置
     * @param originName 文件原始名称
     */
    AgentDeviceImportLogVTO importAgentDeviceClaim(int uid, int aid, int wid,
                                                   String inputPath, String outputPath,
                                                   String originName, String remark);


    /**
     * 获取单条导入记录
     * @param uid
     * @param logId
     * @return
     */
    AgentDeviceImportLogVTO findAgentDeviceImportLogById(int uid, long logId);




    /**
     * 代理商导入日志
     * @param pageNo
     * @param pageSize
     * @return
     */
    TailPage<AgentDeviceImportLogVTO> pageAgentDeviceImportLog(int uid, int pageNo, int pageSize);

    /**
     * 代理商首页面的统计数据，包括本月收入，上月收入，昨日收入，总上线设备数，总收入数以及图表数据
     * @param uid
     * @param enddate 截止日期
     * @return
     */
    RpcResponseDTO<AgentRevenueStatisticsVTO> statistics(int uid, String enddate);
    
    /**
     * 代理商首页面首页面 每日历史收益列表
     * @param uid
     * @param dateEndStr
     * @param pageNo
     * @param pageSize 
     * @return
     */
    RpcResponseDTO<TailPage<DailyRevenueRecordVTO>> pageHistoryRecords(int uid,String dateEndStr,int pageNo, int pageSize);

    /**
     * 代理商结算列表页面
     * @param operator_user 操作用户
     * @param viewstatus -1 所有 1 settled 0 unsettled
     * @param pageNo
     * @param pageSize
     * @return
     */
    RpcResponseDTO<SettlementPageVTO> pageSettlements(int operator_user,int viewstatus,String q,String sort_field,boolean desc,int pageNo, int pageSize);
    
    /**
     * 获取代理商代理设备的所有数量、在线数量、离线数量
     * @param agentuser
     * @return
     */
    RpcResponseDTO<AgentDeviceStatisticsVTO> fetchAgentDeviceStatistics(int agentuser);
    /**
     * 获取公告
     * @param bid
     * @return
     */
    AgentBulltinBoardVTO findAgentBulltinBoardById(int uid, long bid);

    /**
     * 获取代理商公告
     * @param uid
     * @param pageNo
     * @param pageSize
     * @return
     */
    TailPage<AgentBulltinBoardVTO> pageAgentBulltinBoardByUid(int uid, int pageNo, int pageSize);


    /**
     * 获取仓库管理员
     * @param uid
     * @param pageNo
     * @param pageSize
     * @return
     */
    TailPage<UserVTO> pageSellorVTO(int uid, int pageNo, int pageSize);

    /**
     * 获取代理商
     * @param uid
     * @param pageNo
     * @param pageSize
     * @return
     */
    TailPage<UserAgentVTO> pageUserAgentVTO(int uid, int pageNo, int pageSize);


    /**
     * 确定导入完毕
     * @param uid
     * @param logId
     */
    boolean updateAgentDeviceImport(int uid, int logId);

    /**
     * 取消导入
     * @param uid
     * @param logId
     * @return
     */
    boolean cancelAgentDeviceImport(int uid, int logId);


    /**
     *
     * @param uid
     * @param aid
     * @param amount
     * @param invoice
     * @param receipt
     * @param remark
     * @return
     */
    RpcResponseDTO<Boolean> postAgentFinancialSettlement(int uid, int aid, String amount, String invoice, String receipt, String remark);


    /**
     * 获取结算记录列表
     * @param uid
     * @param pageNo
     * @param pageSize
     * @return
     */
    TailPage<AgentFinancialSettlementVTO> pageAgentFinancialSettlementVTO(int uid, int pageNo, int pageSize);
}
