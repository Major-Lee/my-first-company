package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.helper.AgentBulltinType;
import com.bhu.vas.api.helper.ChargingCurrencyHelper;
import com.bhu.vas.api.rpc.agent.model.AgentBulltinBoard;
import com.bhu.vas.api.rpc.agent.model.AgentDeviceClaim;
import com.bhu.vas.api.rpc.agent.model.AgentDeviceImportLog;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.vto.agent.AgentBulltinBoardVTO;
import com.bhu.vas.api.vto.agent.AgentDeviceClaimVTO;
import com.bhu.vas.api.vto.agent.AgentDeviceImportLogVTO;
import com.bhu.vas.api.vto.agent.AgentDeviceVTO;
import com.bhu.vas.business.asyn.spring.activemq.service.DeliverMessageService;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.ds.agent.dto.RecordSummaryDTO;
import com.bhu.vas.business.ds.agent.mdto.WifiDeviceWholeMonthMDTO;
import com.bhu.vas.business.ds.agent.mservice.WifiDeviceWholeMonthMService;
import com.bhu.vas.business.ds.agent.service.AgentBulltinBoardService;
import com.bhu.vas.business.ds.agent.service.AgentDeviceClaimService;
import com.bhu.vas.business.ds.agent.service.AgentDeviceImportLogService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.IdHelper;
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
    
    @Resource
    private WifiDeviceWholeMonthMService wifiDeviceWholeMonthMService;

    public int claimAgentDevice(String sn) {
        logger.info(String.format("AgentFacadeService claimAgentDevice sn[%s]", sn));
        return agentDeviceClaimService.claimAgentDevice(sn);
    }


    public AgentDeviceVTO pageClaimedAgentDeviceById(int uid, int status, int pageNo, int pageSize) {

        ModelCriteria totalmc = new ModelCriteria();
        totalmc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnEqualTo("agentuser", uid);
        int total_count = wifiDeviceService.countByCommonCriteria(totalmc);

        ModelCriteria onlinemc = new ModelCriteria();
        ModelCriteria querymc = new ModelCriteria();

        int online_count = 0;
        int offline_count = 0;
        int total_query = 0;
        switch (status) {
            case DEVICE_ONLINE_STATUS:

                onlinemc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnEqualTo("agentuser", uid).andColumnEqualTo("online", true);
                querymc = onlinemc;
                total_query = wifiDeviceService.countByCommonCriteria(onlinemc);

                online_count = total_query;
                offline_count = total_count - online_count;
                break;
            case DEVICE_OFFLINE_STATUS:

                onlinemc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnEqualTo("agentuser", uid).andColumnEqualTo("online", false);
                querymc = onlinemc;
                total_query = wifiDeviceService.countByCommonCriteria(onlinemc);

                offline_count = total_query;
                online_count = total_count - offline_count;
                break;
            default:
                onlinemc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnEqualTo("agentuser", uid).andColumnEqualTo("online", true);
                online_count = wifiDeviceService.countByCommonCriteria(onlinemc);
                querymc = totalmc;
                
                total_query = total_count;
                offline_count = total_count - online_count;
                break;
        }

        querymc.setPageNumber(pageNo);
        querymc.setPageSize(pageSize);
        List<WifiDevice> devices = wifiDeviceService.findModelByModelCriteria(querymc);
        
        List<String> device_macs = IdHelper.getPKs(devices, String.class);
        //取当前时间的上月数据 和所有数据的汇总
        String previous_month_key = DateTimeHelper.formatDate(DateTimeHelper.getDateFirstDayOfPreviousMonth(), DateTimeHelper.FormatPattern11);
		
        List<WifiDeviceWholeMonthMDTO> monthlyDtos = wifiDeviceWholeMonthMService.fetchByDate(device_macs, previous_month_key);
        List<RecordSummaryDTO> summaryDtos = wifiDeviceWholeMonthMService.summaryAggregationBetween(device_macs, null, null);
        
       /*List<AgentDeviceClaimVTO>  vtos = new ArrayList<AgentDeviceClaimVTO>();
        if (devices != null) {
            AgentDeviceClaimVTO vto = null;
            for (WifiDevice wifiDevice : devices) {
                vto = buildAgentDeviceClaimVTO(wifiDevice,monthlyDtos,summaryDtos);
                vtos.add(vto);
            }
        }*/
        List<AgentDeviceClaimVTO>  vtos = buildAgentDeviceClaimVTOs(devices,monthlyDtos,summaryDtos);
        AgentDeviceVTO agentDeviceVTO = new AgentDeviceVTO();
        agentDeviceVTO.setVtos(new CommonPage<AgentDeviceClaimVTO>(pageNo, pageSize, total_query, vtos));
        agentDeviceVTO.setTotal_count(total_count);
        agentDeviceVTO.setOnline_count(online_count);
        agentDeviceVTO.setOffline_count(offline_count);

        return agentDeviceVTO;

    }

    private List<AgentDeviceClaimVTO> buildAgentDeviceClaimVTOs(List<WifiDevice> devices,List<WifiDeviceWholeMonthMDTO> monthlyDtos,List<RecordSummaryDTO> summaryDtos){
    	List<AgentDeviceClaimVTO>  results = new ArrayList<AgentDeviceClaimVTO>();
    	AgentDeviceClaimVTO vto = null;
    	WifiDeviceWholeMonthMDTO wholeMonthDTO = null;
    	RecordSummaryDTO summaryDTO = null;
        for (WifiDevice device : devices) {
        	String mac = device.getId();
        	for(WifiDeviceWholeMonthMDTO dto:monthlyDtos){
        		if(dto.getMac().equals(mac)){
        			wholeMonthDTO = dto;
        			break;
        		}
        	}
        	
        	for(RecordSummaryDTO dto:summaryDtos){
        		if(dto.getId().equals(mac)){
        			summaryDTO = dto;
        			break;
        		}
        	}
            vto = buildAgentDeviceClaimVTO(device,wholeMonthDTO,summaryDTO);
            results.add(vto);
        }
        return results;
    }
    
    private AgentDeviceClaimVTO buildAgentDeviceClaimVTO(WifiDevice wifiDevice,WifiDeviceWholeMonthMDTO wholeMonthDTO,RecordSummaryDTO summaryDTO) {
        AgentDeviceClaimVTO vto = new AgentDeviceClaimVTO();
        vto.setSn(wifiDevice.getSn());
        vto.setMac(wifiDevice.getId());
        vto.setOnline(wifiDevice.isOnline());
        long total_dod =  summaryDTO != null?summaryDTO.getT_dod():0l;
        vto.setUptime(DateTimeHelper.getTimeDiff(total_dod));
        vto.setTotal_income(ChargingCurrencyHelper.currency(total_dod));
        long previous_dod = wholeMonthDTO !=null ?wholeMonthDTO.getDod():0l;
        vto.setMonth_income(ChargingCurrencyHelper.currency(previous_dod));
        long total_handsets =  summaryDTO != null?summaryDTO.getT_handsets():0l;
        vto.setHd_count(total_handsets);
        vto.setCreated_at(wifiDevice.getCreated_at());
        vto.setOsv(wifiDevice.getOem_swver());
        //vto.setHd_count(WifiDeviceHandsetPresentSortedSetService.getInstance().presentOnlineSize(wifiDevice.getId()));
        //todo(bluesand):收入
        //vto.setMonth_income();
//            vto.setTotal_income();
        vto.setAdr(wifiDevice.getFormatted_address());

        int uid = wifiDevice.getAgentuser();
        vto.setUid(uid);
        User user = userService.getById(uid);
        String nick = "";
        if (user != null) {
            nick = user.getNick();
        }
        vto.setNick(nick == null ? "" : nick);

        AgentDeviceClaim agentDeviceClaim = agentDeviceClaimService.getById(wifiDevice.getSn());
        if (agentDeviceClaim != null) {
            vto.setStock_code(agentDeviceClaim.getStock_code());
            vto.setStock_name(agentDeviceClaim.getStock_name());
            vto.setSold_at(agentDeviceClaim.getSold_at());
            vto.setClaim_at(agentDeviceClaim.getClaim_at());
            vto.setImport_id(agentDeviceClaim.getImport_id());
        }
        return vto;
    }

    
    
    

    public AgentDeviceVTO pageClaimedAgentDeviceById(int status, int pageNo, int pageSize) {
        ModelCriteria totalmc = new ModelCriteria();
        totalmc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnGreaterThan("agentuser", 0);
        int total_count = wifiDeviceService.countByCommonCriteria(totalmc);

        ModelCriteria onlinemc = new ModelCriteria();
        ModelCriteria querymc = new ModelCriteria();

        int online_count = 0;
        int offline_count = 0;
        int total_query = 0;
        switch (status) {
            case DEVICE_ONLINE_STATUS:

                onlinemc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnGreaterThan("agentuser", 0).andColumnEqualTo("online", true);
                querymc = onlinemc;
                total_query = wifiDeviceService.countByCommonCriteria(onlinemc);

                online_count = total_query;
                offline_count = total_count - online_count;
                break;
            case DEVICE_OFFLINE_STATUS:

                onlinemc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnGreaterThan("agentuser", 0).andColumnEqualTo("online", false);
                querymc = onlinemc;
                total_query = wifiDeviceService.countByCommonCriteria(onlinemc);

                offline_count = total_query;
                online_count = total_count - offline_count;
                break;
            default:

                onlinemc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnGreaterThan("agentuser", 0).andColumnEqualTo("online", true);
                online_count = wifiDeviceService.countByCommonCriteria(onlinemc);
                querymc = totalmc;

                total_query = total_count;
                offline_count = total_count - online_count;
                break;
        }

        querymc.setPageNumber(pageNo);
        querymc.setPageSize(pageSize);
        List<WifiDevice> devices = wifiDeviceService.findModelByModelCriteria(querymc);
        List<String> device_macs = IdHelper.getPKs(devices, String.class);
        //取当前时间的上月数据 和所有数据的汇总
        String previous_month_key = DateTimeHelper.formatDate(DateTimeHelper.getDateFirstDayOfPreviousMonth(), DateTimeHelper.FormatPattern11);

        List<WifiDeviceWholeMonthMDTO> monthlyDtos = wifiDeviceWholeMonthMService.fetchByDate(device_macs, previous_month_key);
        List<RecordSummaryDTO> summaryDtos = wifiDeviceWholeMonthMService.summaryAggregationBetween(device_macs, null, null);
        
        List<AgentDeviceClaimVTO>  vtos = buildAgentDeviceClaimVTOs(devices,monthlyDtos,summaryDtos);
        /*List<AgentDeviceClaimVTO>  vtos = new ArrayList<AgentDeviceClaimVTO>();
        if (devices != null) {
            AgentDeviceClaimVTO vto = null;
            for (WifiDevice wifiDevice : devices) {
            	
                vto = buildAgentDeviceClaimVTO(wifiDevice);
                vtos.add(vto);
            }
        }*/

        AgentDeviceVTO agentDeviceVTO = new AgentDeviceVTO();
        agentDeviceVTO.setVtos(new CommonPage<AgentDeviceClaimVTO>(pageNo, pageSize, total_query, vtos));
        agentDeviceVTO.setTotal_count(total_count);
        agentDeviceVTO.setOnline_count(online_count);
        agentDeviceVTO.setOffline_count(offline_count);

        return agentDeviceVTO;
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
        vto.setSn(agentDeviceClaim.getId());
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
            vto.setCreated_at(wifiDevice.getCreated_at());
            vto.setOsv(wifiDevice.getOem_swver());
            vto.setHd_count(WifiDeviceHandsetPresentSortedSetService.getInstance().presentOnlineSize(wifiDevice.getId()));
            //todo(bluesand):收入
            //vto.setMonth_income();
//            vto.setTotal_income();
            vto.setAdr(wifiDevice.getFormatted_address());
        }

        return vto;
    }

/*    private AgentDeviceClaimVTO buildAgentDeviceClaimVTO(WifiDevice wifiDevice) {
        AgentDeviceClaimVTO vto = new AgentDeviceClaimVTO();
        vto.setSn(wifiDevice.getSn());
        vto.setMac(wifiDevice.getId());
        vto.setOnline(wifiDevice.isOnline());
        long uptime = 0;
        try {
           uptime =  Long.parseLong(wifiDevice.getUptime());
        } catch (Exception e) {
            uptime = 0;
        }

        vto.setUptime(DateTimeHelper.getTimeDiff(uptime));
        vto.setCreated_at(wifiDevice.getCreated_at());
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
    }*/



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
                vto.setId(log.getId());
                vto.setAid(log.getAid());
                vto.setCount(log.getCount());
                vto.setCreated_at(log.getCreated_at());

                User agent = userService.getById(log.getAid());
                if (agent != null) {
                    vto.setNick(agent.getNick() == null ? "" : agent.getNick());
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
