package com.bhu.vas.business.agent;

import com.bhu.vas.api.rpc.agent.model.AgentDeviceClaim;
import com.bhu.vas.api.rpc.agent.model.AgentDeviceImportLog;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserDevice;
import com.bhu.vas.api.vto.agent.AgentDeviceImportLogVTO;
import com.bhu.vas.business.ds.agent.service.AgentDeviceClaimService;
import com.bhu.vas.business.ds.agent.service.AgentDeviceImportLogService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.localunit.BaseTest;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
                vto.setCount(log.getCount());
                vto.setCreated_at(log.getCreated_at().getTime());

                User agent = userService.getById(log.getAid());
                if (agent != null) {
                    vto.setName(agent.getNick() == null ? "" : agent.getNick());
                }
                vtos.add(vto);
            }
        }
        new CommonPage<AgentDeviceImportLogVTO>(1, 20, total, vtos);
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
