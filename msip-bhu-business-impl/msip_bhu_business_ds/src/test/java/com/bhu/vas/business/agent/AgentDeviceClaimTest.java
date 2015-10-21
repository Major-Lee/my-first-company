package com.bhu.vas.business.agent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.api.rpc.agent.model.AgentDeviceClaim;
import com.bhu.vas.api.rpc.agent.model.AgentDeviceImportLog;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.vto.agent.AgentDeviceImportLogVTO;
import com.bhu.vas.business.ds.agent.service.AgentDeviceClaimService;
import com.bhu.vas.business.ds.agent.service.AgentDeviceImportLogService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.localunit.BaseTest;

/**
 * Created by bluesand on 9/7/15.
 */
public class AgentDeviceClaimTest extends BaseTest {

    @Resource
    public AgentDeviceClaimService agentDeviceClaimService;

    @Resource
    public AgentDeviceImportLogService agentDeviceImportLogService;

    @Resource
    public UserService userService;

    @Resource
    public WifiDeviceService wifiDeviceService;

    //@Test
    public void create() {
        AgentDeviceClaim agentDeviceClaim = new AgentDeviceClaim();
        agentDeviceClaim.setId("1234567893");
        agentDeviceClaim.setUid(6);
        Date date = new Date();
        agentDeviceClaim.setSold_at(date);
        agentDeviceClaimService.insert(agentDeviceClaim);
    }

    @Test
    public void list() {
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnEqualTo("uid", 6).andColumnEqualTo("status", 1);
        int total = agentDeviceClaimService.countByCommonCriteria(mc);

        System.out.println("total:" + total);
        mc.setPageNumber(1);
        mc.setPageSize(20);
        List<AgentDeviceClaim> agents = agentDeviceClaimService.findModelByModelCriteria(mc);


        System.out.println("agents:" + agents);
    }

    @Test
    public void test() {
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andSimpleCaulse(" 1=1 ");
        int total = agentDeviceImportLogService.countByModelCriteria(mc);
        //mc.setPageNumber(1);
        //mc.setPageSize(20);
        List<AgentDeviceImportLog> logs = agentDeviceImportLogService.findModelByModelCriteria(mc);
        List<AgentDeviceImportLogVTO>  vtos = new ArrayList<AgentDeviceImportLogVTO>();
        if (logs != null) {
            AgentDeviceImportLogVTO vto = null;
            for (AgentDeviceImportLog log : logs) {
                vto = new AgentDeviceImportLogVTO();
                vto.setAid(log.getAid());
                vto.setScount(log.getSuccess_count());
                vto.setCreated_at(log.getCreated_at());

                User agent = userService.getById(log.getAid());
                if (agent != null) {
                    vto.setNick(agent.getNick() == null ? "" : agent.getNick());
                }
                vtos.add(vto);
            }
        }
        new CommonPage<AgentDeviceImportLogVTO>(1, 20, total, vtos);
    }


    @Test
    public void claim() {
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnEqualTo("uid", 100084);
        List<AgentDeviceClaim> agents = agentDeviceClaimService.findModelByModelCriteria(mc);
        WifiDevice wifiDevice = null;
        for (AgentDeviceClaim agentDeviceClaim : agents) {
            wifiDevice = wifiDeviceService.getById(agentDeviceClaim.getMac());
            if (wifiDevice != null) {
                wifiDevice.setAgentuser(agentDeviceClaim.getUid());
                wifiDeviceService.update(wifiDevice);
            }

        }
    }

    @Test
    public void claimDevice() {
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnEqualTo("orig_model", "uRouter");
        List<WifiDevice> devices = wifiDeviceService.findModelByModelCriteria(mc);
        for (WifiDevice wifiDevice : devices) {
            try {
                AgentDeviceClaim agentDeviceClaim = new AgentDeviceClaim();
                agentDeviceClaim.setId(wifiDevice.getSn());
                agentDeviceClaim.setMac(wifiDevice.getId());
                agentDeviceClaim.setUid(100084);
                Date date = new Date();
                agentDeviceClaim.setSold_at(date);
                agentDeviceClaim.setClaim_at(date);
                agentDeviceClaim.setStock_code("10000190");
                agentDeviceClaim.setStock_name("uRouter");
                agentDeviceClaimService.insert(agentDeviceClaim);

                wifiDevice.setAgentuser(100084);
                wifiDeviceService.update(wifiDevice);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Test
    public void query() {
        int status = 0;
        ModelCriteria totalmc = new ModelCriteria();
        totalmc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnEqualTo("agentuser", 100084);
        int total_count = wifiDeviceService.countByCommonCriteria(totalmc);

        ModelCriteria onlinemc = new ModelCriteria();
        ModelCriteria querymc = new ModelCriteria();

        int online_count = 0;
        int offline_count = 0;
        int total_query = 0;
        switch (status) {
            case 1:
                onlinemc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnEqualTo("agentuser", 100084).andColumnEqualTo("online", true);
                querymc = onlinemc;
                total_query = wifiDeviceService.countByCommonCriteria(onlinemc);
                online_count = total_count;
                offline_count = total_count - online_count;
                break;
            case 0:
                onlinemc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnEqualTo("agentuser", 100084).andColumnEqualTo("online", false);
                querymc = onlinemc;
                total_query = wifiDeviceService.countByCommonCriteria(onlinemc);
                offline_count = total_query;
                online_count = total_count - offline_count;
                break;
            default:
                onlinemc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnEqualTo("agentuser", 100084).andColumnEqualTo("online", true);
                querymc = totalmc;
                online_count = wifiDeviceService.countByCommonCriteria(onlinemc);
                total_query = total_count;
                offline_count = total_count - online_count;
                break;
        }

        querymc.setPageNumber(1);
        querymc.setPageSize(5);

        List<WifiDevice> devices = wifiDeviceService.findModelByModelCriteria(querymc);
        System.out.println(devices);
    }


//    @Test
//    public void excel() throws Exception{
//        InputStream is = new FileInputStream("/Users/bluesand/Desktop/workbook.xls");
//        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
//        AgentDeviceClaim agentDeviceClaim = null;
//        List<AgentDeviceClaim> list = new ArrayList<AgentDeviceClaim>();
//
//        for (int numSheet = 0; numSheet <hssfWorkbook.getNumberOfSheets(); numSheet++) {
//            System.out.println(String.format("numSheet[%s]",numSheet));
//            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
//            if (hssfSheet == null) {
//                continue;
//            }
//            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
//                HSSFRow hssfRow = hssfSheet.getRow(rowNum);
//                if (hssfRow == null) {
//                    continue;
//                }
//
//                agentDeviceClaim = new AgentDeviceClaim();
//
//                HSSFCell uid = hssfRow.getCell(0);
//
//                if (uid == null) {
//                    continue;
//                }
//                agentDeviceClaim.setUid((int)uid.getNumericCellValue());
//
//                HSSFCell sn = hssfRow.getCell(1);
//
//                agentDeviceClaim.setId(sn.getStringCellValue());
//
//                Date date = new Date();
//                agentDeviceClaim.setSold_at(date);
//                agentDeviceClaimService.insert(agentDeviceClaim);
//            }
//        }
//    }

}
