package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.helper.AgentBulltinType;
import com.bhu.vas.api.rpc.agent.dto.AgentDeviceClaimDTO;
import com.bhu.vas.api.rpc.agent.model.AgentBulltinBoard;
import com.bhu.vas.api.rpc.agent.model.AgentDeviceImportLog;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserDevice;
import com.bhu.vas.api.vto.agent.AgentBulltinBoardVTO;
import com.bhu.vas.api.vto.agent.AgentDeviceClaimVTO;
import com.bhu.vas.api.vto.agent.AgentDeviceImportLogVTO;
import com.bhu.vas.api.vto.agent.AgentDeviceVTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.ds.agent.service.AgentBulltinBoardService;
import com.bhu.vas.business.ds.agent.service.AgentDeviceImportLogService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.user.service.UserService;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.agent.model.AgentDeviceClaim;
import com.bhu.vas.business.asyn.spring.activemq.service.DeliverMessageService;
import com.bhu.vas.business.ds.agent.service.AgentDeviceClaimService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.TailPage;

/**
 * Created by bluesand on 9/7/15.
 */
@Service
public class AgentFacadeService {

    private final static int DEVICE_ONLINE_STATUS = 1;
    private final static int DEVICE_OFFLINE_STATUS = 0;


    private final Logger logger = LoggerFactory.getLogger(AgentFacadeService.class);

    @Resource
    private AgentDeviceClaimService agentDeviceClaimService;

    @Resource
    private DeliverMessageService deliverMessageService;

    @Resource
    private AgentDeviceImportLogService agentDeviceImportLogService;

    @Resource
    private UserService userService;

    @Resource
    private WifiDeviceService wifiDeviceService;

    @Resource
    private AgentBulltinBoardService agentBulltinBoardService;

    public int claimAgentDevice(String sn) {
        logger.info(String.format("AgentFacadeService claimAgentDevice sn[%s]", sn));
        return agentDeviceClaimService.claimAgentDevice(sn);
    }


    public AgentDeviceVTO pageClaimedAgentDeviceById(int uid, int type, int pageNo, int pageSize) {
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnEqualTo("agentuser", uid);

        int total_count = wifiDeviceService.countByCommonCriteria(mc);

        mc.createCriteria().andColumnEqualTo("online", true);
        int online_count = wifiDeviceService.countByCommonCriteria(mc);
        int offline_count = 0;
        int total_query = 0;
        switch (type) {
            case DEVICE_ONLINE_STATUS:
                total_query = online_count;
                offline_count = total_count - online_count;
                break;
            case DEVICE_OFFLINE_STATUS:
                offline_count = total_count - online_count;
                total_query = offline_count;
                break;
            default:
                total_query = total_count;
                offline_count = total_count - online_count;
                break;
        }

        mc.setPageNumber(pageNo);
        mc.setPageSize(pageSize);
        List<WifiDevice> devices = wifiDeviceService.findModelByModelCriteria(mc);
        List<AgentDeviceClaimVTO>  vtos = new ArrayList<AgentDeviceClaimVTO>();
        if (devices != null) {
            AgentDeviceClaimVTO vto = null;
            for (WifiDevice wifiDevice : devices) {
                vto = buildAgentDeviceClaimVTO(wifiDevice);
                vtos.add(vto);
            }
        }

        AgentDeviceVTO agentDeviceVTO = new AgentDeviceVTO();
        agentDeviceVTO.setVtos(new CommonPage<AgentDeviceClaimVTO>(pageNo, pageSize, total_query, vtos));
        agentDeviceVTO.setTotal_count(total_count);
        agentDeviceVTO.setOnline_count(online_count);
        agentDeviceVTO.setOffline_count(offline_count);

        return agentDeviceVTO;

    }


    public TailPage<AgentDeviceClaimVTO> pageClaimedAgentDeviceById(int pageNo, int pageSize) {
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnEqualTo("status",1);
        int total = agentDeviceClaimService.countByCommonCriteria(mc);

        mc.setPageNumber(pageNo);
        mc.setPageSize(pageSize);
        List<AgentDeviceClaim> agents = agentDeviceClaimService.findModelByModelCriteria(mc);
        List<AgentDeviceClaimVTO>  vtos = new ArrayList<AgentDeviceClaimVTO>();
        if (agents != null) {
            AgentDeviceClaimVTO vto = null;
            for (AgentDeviceClaim agentDeviceClaim : agents) {
                vto = buildAgentDeviceClaimVTO(agentDeviceClaim);
                vtos.add(vto);
            }
        }
        return new CommonPage<AgentDeviceClaimVTO>(pageNo, pageSize, total,vtos);
    }

    public TailPage<AgentDeviceClaimVTO> pageUnClaimAgentDeviceByUid(int uid, int pageNo, int pageSize) {
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnEqualTo("uid", uid).andColumnNotEqualTo("status", 1);
        int total = agentDeviceClaimService.countByCommonCriteria(mc);
        mc.setPageNumber(pageNo);
        mc.setPageSize(pageSize);
        List<AgentDeviceClaim> agents = agentDeviceClaimService.findModelByModelCriteria(mc);
        List<AgentDeviceClaimVTO>  vtos = new ArrayList<AgentDeviceClaimVTO>();
        if (agents != null) {
            AgentDeviceClaimVTO vto = null;
            for (AgentDeviceClaim agentDeviceClaim : agents) {
                vto = buildAgentDeviceClaimVTO(agentDeviceClaim);
                vtos.add(vto);
            }
        }
        return new CommonPage<AgentDeviceClaimVTO>(pageNo, pageSize, total, vtos);
    }

    public TailPage<AgentDeviceClaimVTO> pageUnClaimAgentDevice(int pageNo, int pageSize) {
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnNotEqualTo("status", 1);
        int total = agentDeviceClaimService.countByCommonCriteria(mc);
        mc.setPageNumber(pageNo);
        mc.setPageSize(pageSize);
        List<AgentDeviceClaim> agents = agentDeviceClaimService.findModelByModelCriteria(mc);
        List<AgentDeviceClaimVTO>  vtos = new ArrayList<AgentDeviceClaimVTO>();
        if (agents != null) {
            AgentDeviceClaimVTO vto = null;
            for (AgentDeviceClaim agentDeviceClaim : agents) {
                vto = buildAgentDeviceClaimVTO(agentDeviceClaim);
                vtos.add(vto);
            }
        }
        return new CommonPage<AgentDeviceClaimVTO>(pageNo, pageSize, total,vtos);
    }


    private AgentDeviceClaimVTO buildAgentDeviceClaimVTO(AgentDeviceClaim agentDeviceClaim) {
        AgentDeviceClaimVTO vto = new AgentDeviceClaimVTO();
        vto.setId(agentDeviceClaim.getId());
        vto.setMac(agentDeviceClaim.getMac());
        vto.setStock_code(agentDeviceClaim.getStock_code());
        vto.setStock_name(agentDeviceClaim.getStock_name());
        vto.setSold_at(agentDeviceClaim.getSold_at());
        vto.setClaim_at(agentDeviceClaim.getClaim_at());
        vto.setUid(agentDeviceClaim.getUid());

        WifiDevice wifiDevice = wifiDeviceService.getById(agentDeviceClaim.getMac());
        if ( wifiDevice != null){
            vto.setOnline(wifiDevice.isOnline());
            vto.setUptime(wifiDevice.getUptime());
            vto.setCreate_at(wifiDevice.getCreated_at());
            vto.setOsv(wifiDevice.getOem_swver());
            vto.setHd_count(WifiDeviceHandsetPresentSortedSetService.getInstance().presentOnlineSize(wifiDevice.getId()));
            //todo(bluesand):收入
            //vto.setMonth_income();
//            vto.setTotal_income();
            vto.setAdr(wifiDevice.getFormatted_address());
        }

        return vto;
    }

    private AgentDeviceClaimVTO buildAgentDeviceClaimVTO(WifiDevice wifiDevice) {
        AgentDeviceClaimVTO vto = new AgentDeviceClaimVTO();
        vto.setId(wifiDevice.getSn());
        vto.setMac(wifiDevice.getId());
        vto.setOnline(wifiDevice.isOnline());
        vto.setUptime(wifiDevice.getUptime());
        vto.setCreate_at(wifiDevice.getCreated_at());
        vto.setOsv(wifiDevice.getOem_swver());
        vto.setHd_count(WifiDeviceHandsetPresentSortedSetService.getInstance().presentOnlineSize(wifiDevice.getId()));
        //todo(bluesand):收入
        //vto.setMonth_income();
//            vto.setTotal_income();
        vto.setAdr(wifiDevice.getFormatted_address());
        vto.setUid(wifiDevice.getAgentuser());

        AgentDeviceClaim agentDeviceClaim = agentDeviceClaimService.getById(wifiDevice.getSn());
        if (agentDeviceClaim != null) {
            vto.setStock_code(agentDeviceClaim.getStock_code());
            vto.setStock_name(agentDeviceClaim.getStock_name());
            vto.setSold_at(agentDeviceClaim.getSold_at());
            vto.setClaim_at(agentDeviceClaim.getClaim_at());

        }
        return vto;
    }



    public void importAgentDeviceClaim(int uid, int aid, String inputPath, String outputPath, String originName) {
        deliverMessageService.sendAgentDeviceClaimImportMessage(uid, aid, inputPath, outputPath, originName);
    }

    public TailPage<AgentDeviceImportLogVTO> pageAgentDeviceImportLog(int pageNo, int pageSize) {
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andSimpleCaulse(" 1=1 ");
        int total = agentDeviceImportLogService.countByCommonCriteria(mc);
        mc.setPageNumber(pageNo);
        mc.setPageSize(pageSize);
        List<AgentDeviceImportLog> logs = agentDeviceImportLogService.findModelByModelCriteria(mc);
        List<AgentDeviceImportLogVTO>  vtos = new ArrayList<AgentDeviceImportLogVTO>();
        if (logs != null) {
            AgentDeviceImportLogVTO vto = null;
            for (AgentDeviceImportLog log : logs) {
                vto = new AgentDeviceImportLogVTO();
                vto.setAid(log.getAid());
                vto.setCount(log.getCount());
                vto.setCreated_at(log.getCreated_at().getTime());

                User agent = userService.getById(log.getAid());
                if (agent != null) {
                    vto.setName(agent.getNick() == null ? "" : agent.getNick());
                }
                vtos.add(vto);
            }
        }
        return new CommonPage<AgentDeviceImportLogVTO>(pageNo, pageSize, total, vtos);
    }

    public AgentBulltinBoardVTO findAgentBulltinBoardById(long bid) {
        AgentBulltinBoard agentBulltinBoard = agentBulltinBoardService.getById(bid);

        AgentBulltinBoardVTO vto = null;
        if (agentBulltinBoard != null) {
            vto = new AgentBulltinBoardVTO();
            vto.setId(agentBulltinBoard.getId());
            int cid = agentBulltinBoard.getConsumer();
            vto.setCid(cid);
            User consumer = userService.getById(cid);
            if (consumer != null) {
                vto.setCn(consumer.getNick());
            }
            int pid = agentBulltinBoard.getPublisher();
            vto.setPid(pid);
            User publisher = userService.getById(pid);
            if (publisher != null) {
                vto.setPn(publisher.getNick());
            }

            vto.setM(agentBulltinBoard.getContent());
            vto.setType(agentBulltinBoard.getType());
            vto.setD(agentBulltinBoard.getCreated_at());
        }

        return vto;

    }

    public TailPage<AgentBulltinBoardVTO> pageAgentBulltinBoardByUid(int uid, int pageNo, int pageSize) {
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andSimpleCaulse("1=1").andColumnEqualTo("consumer", uid);
        int total = agentBulltinBoardService.countByCommonCriteria(mc);
        mc.setPageNumber(pageNo);
        mc.setPageSize(pageSize);

        List<AgentBulltinBoard> agentBulltinBoards = agentBulltinBoardService.findModelByCommonCriteria(mc);
        List<AgentBulltinBoardVTO> vtos = new ArrayList<AgentBulltinBoardVTO>();

        if (agentBulltinBoards != null) {
            AgentBulltinBoardVTO vto = null;
            for (AgentBulltinBoard agentBulltinBoard : agentBulltinBoards) {
                vto = new AgentBulltinBoardVTO();
                vto.setId(agentBulltinBoard.getId());
                int cid = agentBulltinBoard.getConsumer();
                vto.setCid(cid);
                User consumer = userService.getById(cid);
                if (consumer != null) {
                    vto.setCn(consumer.getNick());
                }
                int pid = agentBulltinBoard.getPublisher();
                vto.setPid(pid);
                User publisher = userService.getById(pid);
                if (publisher != null) {
                    vto.setPn(publisher.getNick());
                }

                vto.setType(agentBulltinBoard.getType());
                vto.setTitle(AgentBulltinType.getAgentBulltinTypeFromKey(agentBulltinBoard.getType()).getDesc());
                vto.setM(agentBulltinBoard.getContent());
                vto.setD(agentBulltinBoard.getCreated_at());
                vtos.add(vto);
            }
        }

        return new CommonPage<AgentBulltinBoardVTO>(pageNo, pageSize, total, vtos);
    }

}
