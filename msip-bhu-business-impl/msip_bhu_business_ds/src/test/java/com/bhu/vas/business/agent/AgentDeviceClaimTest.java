package com.bhu.vas.business.agent;

import com.bhu.vas.api.rpc.agent.model.AgentDeviceClaim;
import com.bhu.vas.business.ds.agent.service.AgentDeviceClaimService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.localunit.BaseTest;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created by bluesand on 9/7/15.
 */
public class AgentDeviceClaimTest extends BaseTest {

    @Resource
    public AgentDeviceClaimService agentDeviceClaimService;

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
        mc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnEqualTo("uid", 6).andColumnIsNotNull("mac");
        int total = agentDeviceClaimService.countByCommonCriteria(mc);

        System.out.println("total:" + total);
        mc.setPageNumber(1);
        mc.setPageSize(20);
        List<AgentDeviceClaim> agents = agentDeviceClaimService.findModelByModelCriteria(mc);


        System.out.println("agents:" + agents);
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
