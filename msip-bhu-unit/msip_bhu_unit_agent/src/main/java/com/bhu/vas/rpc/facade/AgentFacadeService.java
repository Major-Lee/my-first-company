package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.dto.UserType;
import com.bhu.vas.api.helper.AgentBulltinType;
import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.agent.dto.AgentOutputDTO;
import com.bhu.vas.api.rpc.agent.dto.AgentSettlementBulltinBoardDTO;
import com.bhu.vas.api.rpc.agent.model.AgentBulltinBoard;
import com.bhu.vas.api.rpc.agent.model.AgentDeviceClaim;
import com.bhu.vas.api.rpc.agent.model.AgentDeviceImportLog;
import com.bhu.vas.api.rpc.agent.model.AgentFinancialSettlement;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.vto.agent.AgentBulltinBoardVTO;
import com.bhu.vas.api.vto.agent.AgentDeviceClaimVTO;
import com.bhu.vas.api.vto.agent.AgentDeviceImportLogVTO;
import com.bhu.vas.api.vto.agent.AgentDeviceVTO;
import com.bhu.vas.api.vto.agent.AgentFinancialSettlementVTO;
import com.bhu.vas.api.vto.agent.UserAgentVTO;
import com.bhu.vas.api.vto.agent.UserVTO;
import com.bhu.vas.business.asyn.spring.activemq.service.DeliverMessageService;
import com.bhu.vas.business.ds.agent.dto.RecordSummaryDTO;
import com.bhu.vas.business.ds.agent.facade.AgentBillFacadeService;
import com.bhu.vas.business.ds.agent.helper.AgentHelper;
import com.bhu.vas.business.ds.agent.mdto.WifiDeviceWholeMonthMDTO;
import com.bhu.vas.business.ds.agent.mservice.WifiDeviceWholeMonthMService;
import com.bhu.vas.business.ds.agent.service.AgentBulltinBoardService;
import com.bhu.vas.business.ds.agent.service.AgentDeviceClaimService;
import com.bhu.vas.business.ds.agent.service.AgentDeviceImportLogService;
import com.bhu.vas.business.ds.agent.service.AgentFinancialSettlementService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.IdHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * Created by bluesand on 9/7/15.
 */
@Service
public class AgentFacadeService {

    private final static int DEVICE_ONLINE_STATUS = 1;
    private final static int DEVICE_OFFLINE_STATUS = 0;


    private final Logger logger = LoggerFactory.getLogger(AgentFacadeService.class);

    /*List<String> massAPList = new ArrayList<String>();
    {
        //todo(bluesand):合并分支后处理
        massAPList.add("H103");
        massAPList.add("H110");
        massAPList.add("H201");
        massAPList.add("H303");
        massAPList.add("H305");
    }*/

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

    @Resource
    private AgentFinancialSettlementService agentFinancialSettlementService;

    @Resource
	private AgentBillFacadeService agentBillFacadeService;
    
    //@Resource
    //private AgentSettlementsRecordMService agentSettlementsRecordMService;

    @Resource
    private AgentBackendFacadeService agentBackendFacadeService;
    
    public int claimAgentDevice(String sn, String mac, String hdtype) {
        logger.info(String.format("AgentFacadeService claimAgentDevice sn[%s] mac[%s] hdtype[%s]", sn, mac, hdtype));
        return agentDeviceClaimService.claimAgentDevice(sn, mac, hdtype);
    }


    private ModelCriteria builderClaimedAgentDeviceModelCriteria(int uid ,boolean online) {
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnEqualTo("agentuser", uid).andColumnEqualTo("online", online).andColumnIn("hdtype", VapEnumType.DeviceUnitType.getAllMassAPHdTypes());
        return mc;
    }

    private ModelCriteria builderClaimedAgentDeviceModelCriteria(boolean online) {
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnGreaterThan("agentuser", 0).andColumnEqualTo("online", online).andColumnIn("hdtype", VapEnumType.DeviceUnitType.getAllMassAPHdTypes());
        return mc;
    }

    private int totalCountClaimedAgentDevice(ModelCriteria mc) {
        int total_count = wifiDeviceService.countByCommonCriteria(mc);
        return total_count;
    }

    private int totalCountYetClaimedAgentDevice(int uid) {
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnEqualTo("uid", uid).andColumnEqualTo("status", 0).andColumnEqualTo("import_status", 1);
        int yetTotal = agentDeviceClaimService.countByCommonCriteria(mc);
        return yetTotal;
    }

    private int totalCountYetClaimedAgentDevice() {
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnEqualTo("status", 0).andColumnEqualTo("import_status", 1);
        int yetTotal = agentDeviceClaimService.countByCommonCriteria(mc);
        return yetTotal;
    }

    public AgentDeviceVTO pageClaimedAgentDeviceByUid(int uid, int status, int pageNo, int pageSize) {

        User operUser = userService.getById(uid);
        UserTypeValidateService.validUserType(operUser, UserType.AgentNormal.getSname());

        ModelCriteria totalmc = new ModelCriteria();
        totalmc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnEqualTo("agentuser", uid).andColumnIn("hdtype", VapEnumType.DeviceUnitType.getAllMassAPHdTypes());
        int total_count = totalCountClaimedAgentDevice(totalmc);

        ModelCriteria onlinemc = new ModelCriteria();
        ModelCriteria querymc = new ModelCriteria();

        int online_count = 0;
        int offline_count = 0;
        int total_query = 0;
        switch (status) {
            case DEVICE_ONLINE_STATUS:

                onlinemc = builderClaimedAgentDeviceModelCriteria(uid, true);
                querymc = onlinemc;
                total_query = wifiDeviceService.countByCommonCriteria(onlinemc);

                online_count = total_query;
                offline_count = total_count - online_count;
                break;
            case DEVICE_OFFLINE_STATUS:

                onlinemc = builderClaimedAgentDeviceModelCriteria(uid, false);
                querymc = onlinemc;
                total_query = wifiDeviceService.countByCommonCriteria(onlinemc);

                offline_count = total_query;
                online_count = total_count - offline_count;
                break;
            default:
                onlinemc = builderClaimedAgentDeviceModelCriteria(uid, true);
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
                vto = buildUnAgentDeviceClaimVTO(wifiDevice,monthlyDtos,summaryDtos);
                vtos.add(vto);
            }
        }*/
        List<AgentDeviceClaimVTO>  vtos = buildAgentDeviceClaimVTOs(devices, monthlyDtos, summaryDtos);
        AgentDeviceVTO agentDeviceVTO = new AgentDeviceVTO();
        agentDeviceVTO.setVtos(new CommonPage<AgentDeviceClaimVTO>(pageNo, pageSize, total_query, vtos));


        int yetTotal = totalCountYetClaimedAgentDevice(uid);

        agentDeviceVTO.setTotal_count(total_count);
        agentDeviceVTO.setOnline_count(online_count);
        agentDeviceVTO.setOffline_count(offline_count);
        agentDeviceVTO.setYet_count(yetTotal);

        return agentDeviceVTO;

    }

    private List<AgentDeviceClaimVTO> buildAgentDeviceClaimVTOs(List<WifiDevice> devices,List<WifiDeviceWholeMonthMDTO> monthlyDtos,List<RecordSummaryDTO> summaryDtos){
    	List<AgentDeviceClaimVTO>  results = new ArrayList<AgentDeviceClaimVTO>();
    	
        for (WifiDevice device : devices) {
        	AgentDeviceClaimVTO vto = null;
        	WifiDeviceWholeMonthMDTO wholeMonthDTO = null;
        	RecordSummaryDTO summaryDTO = null;
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
        double total_dod =  summaryDTO != null?summaryDTO.getT_dod():0d;
        int total_firstcd = summaryDTO != null?summaryDTO.getT_newdevices():0;
        vto.setUptime(AgentHelper.getTimeDiff(total_dod));
        vto.setTotal_income(AgentHelper.currency(total_dod, total_firstcd));
        
        double previous_dod = wholeMonthDTO !=null ?wholeMonthDTO.getDod():0d;
        int previous_firstcd = wholeMonthDTO != null?wholeMonthDTO.getSamedays():0;
        vto.setMonth_income(AgentHelper.currency(previous_dod,previous_firstcd));
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
        if (user != null) {
            //nick = user.getNick();
            vto.setNick(user.getNick() == null ? StringUtils.EMPTY : user.getNick());
            vto.setOrg(user.getOrg() == null?StringUtils.EMPTY:user.getOrg());
        }

        AgentDeviceClaim agentDeviceClaim = agentDeviceClaimService.getById(wifiDevice.getSn());
        if (agentDeviceClaim != null) {
            vto.setStock_code(agentDeviceClaim.getStock_code());
            //vto.setStock_name(agentDeviceClaim.getStock_name());
            String hdtype = agentDeviceClaim.getHdtype();
            if(StringUtils.isNotEmpty(hdtype)){
            	 vto.setStock_name(VapEnumType.DeviceUnitType.fromIndex(hdtype).getSname());//fromHdType(hdtype).getSname());
            }else{
            	vto.setStock_name(StringHelper.EMPTY_STRING_GAP);
            }
            vto.setSold_at(agentDeviceClaim.getSold_at());
            vto.setClaim_at(agentDeviceClaim.getClaim_at());
            vto.setImport_id(agentDeviceClaim.getImport_id());
        }
        return vto;
    }


    public AgentDeviceVTO pageClaimedAgentDevice(int uid, int status, int pageNo, int pageSize) {


        User operUser = userService.getById(uid);
        UserTypeValidateService.validUserType(operUser, UserType.AgentWarehouseManager.getSname());

        ModelCriteria totalmc = new ModelCriteria();
        totalmc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnGreaterThan("agentuser", 0).andColumnIn("hdtype", VapEnumType.DeviceUnitType.getAllMassAPHdTypes());
        int total_count = totalCountClaimedAgentDevice(totalmc);

        ModelCriteria onlinemc = new ModelCriteria();
        ModelCriteria querymc = new ModelCriteria();

        int online_count = 0;
        int offline_count = 0;
        int total_query = 0;
        switch (status) {
            case DEVICE_ONLINE_STATUS:

                onlinemc = builderClaimedAgentDeviceModelCriteria(true);
                querymc = onlinemc;
                total_query = wifiDeviceService.countByCommonCriteria(onlinemc);

                online_count = total_query;
                offline_count = total_count - online_count;
                break;
            case DEVICE_OFFLINE_STATUS:

                onlinemc = builderClaimedAgentDeviceModelCriteria(false);
                querymc = onlinemc;
                total_query = wifiDeviceService.countByCommonCriteria(onlinemc);

                offline_count = total_query;
                online_count = total_count - offline_count;
                break;
            default:
                onlinemc = builderClaimedAgentDeviceModelCriteria(true);
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
        
        List<AgentDeviceClaimVTO>  vtos = buildAgentDeviceClaimVTOs(devices, monthlyDtos, summaryDtos);
        /*List<AgentDeviceClaimVTO>  vtos = new ArrayList<AgentDeviceClaimVTO>();
        if (devices != null) {
            AgentDeviceClaimVTO vto = null;
            for (WifiDevice wifiDevice : devices) {
            	
                vto = buildUnAgentDeviceClaimVTO(wifiDevice);
                vtos.add(vto);
            }
        }*/

        AgentDeviceVTO agentDeviceVTO = new AgentDeviceVTO();
        agentDeviceVTO.setVtos(new CommonPage<AgentDeviceClaimVTO>(pageNo, pageSize, total_query, vtos));

        int yetTotal = totalCountYetClaimedAgentDevice();

        agentDeviceVTO.setTotal_count(total_count);
        agentDeviceVTO.setOnline_count(online_count);
        agentDeviceVTO.setOffline_count(offline_count);
        agentDeviceVTO.setYet_count(yetTotal);

        return agentDeviceVTO;
    }

    
    public AgentDeviceVTO pageUnClaimAgentDeviceByUid(int uid, int pageNo, int pageSize) {
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnEqualTo("uid", uid).andColumnEqualTo("status", 0).andColumnEqualTo("import_status", 1);
        int yetTotal = agentDeviceClaimService.countByCommonCriteria(mc);
        mc.setPageNumber(pageNo);
        mc.setPageSize(pageSize);
        List<AgentDeviceClaim> agents = agentDeviceClaimService.findModelByModelCriteria(mc);
        List<AgentDeviceClaimVTO>  vtos = new ArrayList<AgentDeviceClaimVTO>();
        if (agents != null) {
            AgentDeviceClaimVTO vto = null;
            for (AgentDeviceClaim agentDeviceClaim : agents) {
                vto = buildUnAgentDeviceClaimVTO(agentDeviceClaim);
                vtos.add(vto);
            }
        }

        AgentDeviceVTO agentDeviceVTO = new AgentDeviceVTO();
        agentDeviceVTO.setVtos(new CommonPage<AgentDeviceClaimVTO>(pageNo, pageSize, yetTotal, vtos));


        int online_count = 0;
        int offline_count = 0;


        ModelCriteria totalmc = new ModelCriteria();
        totalmc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnEqualTo("agentuser", uid).andColumnIn("hdtype", VapEnumType.DeviceUnitType.getAllMassAPHdTypes());
        int total_count = wifiDeviceService.countByCommonCriteria(totalmc);

        ModelCriteria onlinemc = builderClaimedAgentDeviceModelCriteria(uid, true);
        online_count = wifiDeviceService.countByCommonCriteria(onlinemc);
        offline_count = total_count - online_count;

        agentDeviceVTO.setTotal_count(total_count);
        agentDeviceVTO.setOnline_count(online_count);
        agentDeviceVTO.setOffline_count(offline_count);
        agentDeviceVTO.setYet_count(yetTotal);

        return agentDeviceVTO;
    }

    public AgentDeviceVTO pageUnClaimAgentDevice(int pageNo, int pageSize) {
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnEqualTo("status", 0).andColumnEqualTo("import_status",1);
        int yetTotal = agentDeviceClaimService.countByCommonCriteria(mc);
        mc.setPageNumber(pageNo);
        mc.setPageSize(pageSize);
        List<AgentDeviceClaim> agents = agentDeviceClaimService.findModelByModelCriteria(mc);
        List<AgentDeviceClaimVTO>  vtos = new ArrayList<AgentDeviceClaimVTO>();
        if (agents != null) {
            AgentDeviceClaimVTO vto = null;
            for (AgentDeviceClaim agentDeviceClaim : agents) {
                vto = buildUnAgentDeviceClaimVTO(agentDeviceClaim);
                vtos.add(vto);
            }
        }
        AgentDeviceVTO agentDeviceVTO = new AgentDeviceVTO();
        agentDeviceVTO.setVtos(new CommonPage<AgentDeviceClaimVTO>(pageNo, pageSize, yetTotal, vtos));


        int online_count = 0;
        int offline_count = 0;

        ModelCriteria totalmc = new ModelCriteria();
        totalmc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnGreaterThan("agentuser", 0).andColumnIn("hdtype", VapEnumType.DeviceUnitType.getAllMassAPHdTypes());
        int total_count = wifiDeviceService.countByCommonCriteria(totalmc);

        ModelCriteria onlinemc = builderClaimedAgentDeviceModelCriteria(true);
        online_count = wifiDeviceService.countByCommonCriteria(onlinemc);
        offline_count = total_count - online_count;


        agentDeviceVTO.setTotal_count(total_count);
        agentDeviceVTO.setOnline_count(online_count);
        agentDeviceVTO.setOffline_count(offline_count);
        agentDeviceVTO.setYet_count(yetTotal);

        return agentDeviceVTO;
    }


    private AgentDeviceClaimVTO buildUnAgentDeviceClaimVTO(AgentDeviceClaim agentDeviceClaim) {
        AgentDeviceClaimVTO vto = new AgentDeviceClaimVTO();
        vto.setSn(agentDeviceClaim.getId());
        vto.setMac(agentDeviceClaim.getMac());
        vto.setStock_code(agentDeviceClaim.getStock_code());
        vto.setStock_name(agentDeviceClaim.getStock_name());
        vto.setSold_at(agentDeviceClaim.getSold_at());
        vto.setClaim_at(agentDeviceClaim.getClaim_at());
        vto.setUid(agentDeviceClaim.getUid());
        vto.setImport_id(agentDeviceClaim.getImport_id());

        User agent = userService.getById(agentDeviceClaim.getUid());
        if (agent != null) {
            vto.setNick(agent.getNick() == null ? "" : agent.getNick());
            vto.setOrg(agent.getOrg() == null ? "" : agent.getOrg());
        }
        //vto.setOnline(false);
        //vto.setUptime("--");
        /*WifiDevice wifiDevice = wifiDeviceService.getById(agentDeviceClaim.getMac());
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
        }*/

        return vto;
    }


    public AgentDeviceImportLogVTO importAgentDeviceClaim(int uid, int aid, int wid,
                                                          String inputPath, String outputPath,
                                                          String originName, String remark) {
        User operUser = userService.getById(uid);
        UserTypeValidateService.validUserType(operUser, UserType.AgentWarehouseManager.getSname());

        //代理商导入记录
        AgentDeviceImportLog agentDeviceImportLog = new AgentDeviceImportLog();
        agentDeviceImportLog.setAid(aid);
        agentDeviceImportLog.setSid(wid);
        agentDeviceImportLog.setCreated_at(new Date());
        agentDeviceImportLog.setStatus(AgentDeviceImportLog.IMPORT_DOING);
        agentDeviceImportLog.setRemark(remark);
        agentDeviceImportLog = agentDeviceImportLogService.insert(agentDeviceImportLog);

        AgentDeviceImportLogVTO vto = new AgentDeviceImportLogVTO();
        vto.setId(agentDeviceImportLog.getId());
        vto.setAid(aid);
        vto.setSid(wid);
        vto.setScount(agentDeviceImportLog.getSuccess_count());
        vto.setFcount(agentDeviceImportLog.getFail_count());
        vto.setStatus(agentDeviceImportLog.getStatus());
        vto.setCreated_at(agentDeviceImportLog.getCreated_at());
        vto.setRemark(remark);
        vto.setFilename(originName);
        User agent = userService.getById(aid);
        if (agent != null) {
            vto.setNick(agent.getNick() == null ? "" : agent.getNick());
            vto.setOrg(agent.getOrg() == null ? "" : agent.getOrg());
        }

        User sellor = userService.getById(wid);
        if (sellor != null) {
            vto.setSnick(sellor.getNick() == null ? "" : sellor.getNick());
        }

        //异步处理代理商
//        deliverMessageService.sendAgentDeviceClaimImportMessage(uid, agentDeviceImportLog.getId(), inputPath, outputPath, originName);

        agentBackendFacadeService.sendAgentDeviceClaimImportMessage(uid, agentDeviceImportLog.getId(), inputPath, outputPath, originName);

        return vto;

    }


    public AgentDeviceImportLogVTO findAgentDeviceImportLogById(int uid, long logId) {

        User operUser = userService.getById(uid);
        UserTypeValidateService.validUserType(operUser, UserType.AgentWarehouseManager.getSname());

        AgentDeviceImportLog agentDeviceImportLog = agentDeviceImportLogService.getById(logId);


        AgentDeviceImportLogVTO vto = new AgentDeviceImportLogVTO();
        vto.setId(agentDeviceImportLog.getId());
        vto.setAid(agentDeviceImportLog.getAid());
        vto.setSid(agentDeviceImportLog.getSid());
        vto.setScount(agentDeviceImportLog.getSuccess_count());
        vto.setFcount(agentDeviceImportLog.getFail_count());
        vto.setStatus(agentDeviceImportLog.getStatus());
        vto.setCreated_at(agentDeviceImportLog.getCreated_at());
        vto.setRemark(agentDeviceImportLog.getRemark());


        String content = agentDeviceImportLog.getContent();
        vto.setContent(content);
        AgentOutputDTO outputDTO = JsonHelper.getDTO(content, AgentOutputDTO.class);
        vto.setFilename(outputDTO.getName());

        User agent = userService.getById(agentDeviceImportLog.getAid());
        if (agent != null) {
            vto.setNick(agent.getNick() == null ? "" : agent.getNick());
            vto.setOrg(agent.getOrg() == null ? "" : agent.getOrg());
        }

        User sellor = userService.getById(agentDeviceImportLog.getSid());
        if (sellor != null) {
            vto.setSnick(sellor.getNick() == null ? "" : sellor.getNick());
        }

        return vto;

    }

    public TailPage<AgentDeviceImportLogVTO> pageAgentDeviceImportLog(int uid, int pageNo, int pageSize) {

        User operUser = userService.getById(uid);
        UserTypeValidateService.validUserType(operUser, UserType.AgentWarehouseManager.getSname());

        ModelCriteria mc = new ModelCriteria();
        mc.setOrderByClause("id desc");
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
                vto.setSid(log.getSid());
                vto.setScount(log.getSuccess_count());
                vto.setFcount(log.getFail_count());
                vto.setCreated_at(log.getCreated_at());
                vto.setStatus(log.getStatus());
                vto.setRemark(log.getRemark());
                User agent = userService.getById(log.getAid());
                if (agent != null) {
                    vto.setNick(agent.getNick() == null ? "" : agent.getNick());
                    vto.setOrg(agent.getOrg() == null ? "" : agent.getOrg());
                }

                User sellor = userService.getById(log.getSid());
                if (sellor != null) {
                    vto.setSnick(sellor.getNick() == null ? "" : sellor.getNick());
                }

                long bid  = log.getBid();
                vto.setBid(bid);
                String content = log.getContent();
                AgentOutputDTO outputDTO = JsonHelper.getDTO(content, AgentOutputDTO.class);
                vto.setFilename(outputDTO.getName());

                vtos.add(vto);
            }
        }
        return new CommonPage<AgentDeviceImportLogVTO>(pageNo, pageSize, total, vtos);
    }

    public AgentBulltinBoardVTO findAgentBulltinBoardById(int uid, long bid) {

//        User operUser = userService.getById(uid);
//        UserTypeValidateService.validUserType(operUser, UserType.WarehouseManager.getSname());

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

        User operUser = userService.getById(uid);
        UserTypeValidateService.validUserType(operUser, UserType.AgentNormal.getSname());

        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andSimpleCaulse("1=1").andColumnEqualTo("consumer", uid);
        mc.setOrderByClause("created_at desc");
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


    public TailPage<UserVTO> pageUserVTO(int uid, int utype, int pageNo, int pageSize) {

        User operUser = userService.getById(uid);
        UserTypeValidateService.validUserType(operUser, UserType.AgentWarehouseManager.getSname());

        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andSimpleCaulse("1=1").andColumnEqualTo("utype", utype);
        int total = userService.countByCommonCriteria(mc);
        mc.setPageNumber(pageNo);
        mc.setPageSize(pageSize);

        List<User> users = userService.findModelByCommonCriteria(mc);
        List<UserVTO> vtos = new ArrayList<UserVTO>();
        if (users != null) {
            UserVTO vto = null;
            for (User user : users) {
                vto = new UserVTO();
                vto.setId(user.getId());
                vto.setN(user.getNick());
                vtos.add(vto);
            }
        }
        return new CommonPage<UserVTO>(pageNo, pageSize, total, vtos);
    }


    public TailPage<UserAgentVTO>  pageUserAgentVTO(int uid, int pageNo, int pageSize) {
        User operUser = userService.getById(uid);
        UserTypeValidateService.validUserType(operUser, UserType.AgentWarehouseManager.getSname());

        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andSimpleCaulse("1=1").andColumnEqualTo("utype", UserType.AgentNormal.getIndex());
        int total = userService.countByCommonCriteria(mc);
        mc.setPageNumber(pageNo);
        mc.setPageSize(pageSize);

        List<User> users = userService.findModelByCommonCriteria(mc);
        List<UserAgentVTO> vtos = new ArrayList<UserAgentVTO>();
        if (users != null) {
            UserAgentVTO vto = null;
            for (User user : users) {
                vto = new UserAgentVTO();
                vto.setId(user.getId());
                vto.setN(user.getNick());
                vto.setOrg(user.getOrg());
                vtos.add(vto);
            }
        }
        return new CommonPage<UserAgentVTO>(pageNo, pageSize, total, vtos);
    }


    public boolean updateAgentImportImport(int uid, long logId) {

        User operUser = userService.getById(uid);
        UserTypeValidateService.validUserType(operUser, UserType.AgentWarehouseManager.getSname());
        AgentDeviceImportLog agentDeviceImportLog =  agentDeviceImportLogService.getById(logId);
        if (agentDeviceImportLog != null) {
            agentDeviceImportLog.setStatus(AgentDeviceImportLog.CONFIRM_DONE);
            agentDeviceImportLogService.update(agentDeviceImportLog);
//            deliverMessageService.sendAgentDeviceClaimUpdateMessage(agentDeviceImportLog.getAid(), logId);
//            agentBackendFacadeService.sendAgentDeviceClaimUpdateMessage(agentDeviceImportLog.getAid(), logId);

            deliverMessageService.sendAgentDeviceClaimUpdateMessage(agentDeviceImportLog.getAid(), logId);
        }
        return true;
    }


    public boolean cancelAgentImport(int uid, long logId) {
        User operUser = userService.getById(uid);
        UserTypeValidateService.validUserType(operUser, UserType.AgentWarehouseManager.getSname());


        agentDeviceImportLogService.deleteById(logId);

        List<String> ids = agentDeviceClaimService.findIds("import_id", logId);

        agentDeviceClaimService.deleteByIds(ids);

        return true;
    }

    public  RpcResponseDTO<Boolean> postAgentFinancialSettlement(int uid, int aid, String settlementAmount, String invoice, String receipt, String remark) {
    	try{
    		//account = 
    		//if(account <=0 || ArithHelper.round(account, 2) == 0)
    		//String settlementAmountStr = String.valueOf(settlementAmount);
    		if(!AgentHelper.isValidSettledNumberCharacter(settlementAmount)){
    			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_FLOAT_DECIMAL_PART_ERROR,new String[]{settlementAmount});
    		}
    		double amount = Double.parseDouble(settlementAmount);
	    	User operUser = userService.getById(uid);
	    	UserTypeValidateService.validUserType(operUser, UserType.AgentFinance.getSname());
	    	User agentUser = userService.getById(aid);
	    	UserTypeValidateService.validUserType(agentUser, UserType.AgentNormal.getSname());
	    	String result = agentBillFacadeService.iterateSettleBills(uid,operUser.getNick(), aid, amount);
	    	AgentFinancialSettlement agentFinancialSettlement = new AgentFinancialSettlement();
	        agentFinancialSettlement.setUid(uid);
	        agentFinancialSettlement.setAid(aid);
	        agentFinancialSettlement.setAmount(amount);
	        agentFinancialSettlement.setInvoice_fid(invoice);
	        agentFinancialSettlement.setReceipt_fid(receipt);
	        agentFinancialSettlement.setRemark(remark);
	        agentFinancialSettlement.setDetail(result);
	        agentFinancialSettlementService.insert(agentFinancialSettlement);
	        AgentBulltinBoard agentBulltinBoard = new AgentBulltinBoard();
	        agentBulltinBoard.setPublisher(uid);
	        agentBulltinBoard.setConsumer(aid);
	        agentBulltinBoard.setType(AgentBulltinType.ArrivalNotice.getKey());
	        AgentSettlementBulltinBoardDTO dto = new AgentSettlementBulltinBoardDTO();
	        dto.setAid(aid);
	        dto.setAmount(amount);
	        dto.setInvoice(invoice);
	        dto.setReceipt(receipt);
	        dto.setRemark(remark);
	        agentBulltinBoard.setContent(JsonHelper.getJSONString(dto));
	        agentBulltinBoardService.insert(agentBulltinBoard);
	        return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
    	}catch(BusinessI18nCodeException bex){
			bex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
    }


    public TailPage<AgentFinancialSettlementVTO> pageAgentFinancialSettlementVTO(int uid, int pageNo, int pageSize) {

        User operUser = userService.getById(uid);
        UserTypeValidateService.validUserType(operUser, UserType.AgentFinance.getSname());

        ModelCriteria mc = new ModelCriteria();
        mc.setOrderByClause("updated_at desc");
        mc.createCriteria().andSimpleCaulse("1=1");
        int total = agentFinancialSettlementService.countByCommonCriteria(mc);
        mc.setPageNumber(pageNo);
        mc.setPageSize(pageSize);

        List<AgentFinancialSettlement> agentFinancialSettlements = agentFinancialSettlementService.findModelByCommonCriteria(mc);
        List<AgentFinancialSettlementVTO> vtos = new ArrayList<AgentFinancialSettlementVTO>();

        User user = userService.getById(uid);
        if (agentFinancialSettlements != null) {
            AgentFinancialSettlementVTO vto = null;

            for (AgentFinancialSettlement agentFinancialSettlement : agentFinancialSettlements) {
                vto = new AgentFinancialSettlementVTO();
                if (user != null) {
                    vto.setName(user.getNick());
                }
                vto.setUid(uid);
                int aid = agentFinancialSettlement.getAid();
                User agent = userService.getById(aid);
                if (agent != null) {
                    vto.setAname(agent.getNick());
                    vto.setOrg(agent.getOrg());
                }
                vto.setAid(aid);
                vto.setAmount(agentFinancialSettlement.getAmount());
                //vto.setDetail(agentFinancialSettlement.getDetail());
                vto.setInvoice(agentFinancialSettlement.getInvoice_fid());
                vto.setReceipt(agentFinancialSettlement.getReceipt_fid());
                vto.setRemark(agentFinancialSettlement.getRemark());
                vto.setUpdated_at(agentFinancialSettlement.getUpdated_at());
                vtos.add(vto);
            }
        }
        return new CommonPage<AgentFinancialSettlementVTO>(pageNo, pageSize, total, vtos);
    }

}
