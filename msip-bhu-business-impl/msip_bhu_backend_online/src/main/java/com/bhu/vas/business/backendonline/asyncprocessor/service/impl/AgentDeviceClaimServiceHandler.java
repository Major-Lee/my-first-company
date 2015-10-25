package com.bhu.vas.business.backendonline.asyncprocessor.service.impl;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.bhu.vas.api.helper.AgentBulltinType;
import com.bhu.vas.api.rpc.agent.dto.AgentOutputDTO;
import com.bhu.vas.api.rpc.agent.model.AgentBulltinBoard;
import com.bhu.vas.api.rpc.agent.model.AgentDeviceImportLog;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.business.asyn.spring.model.agent.AgentDeviceClaimUpdateDTO;
import com.bhu.vas.business.ds.agent.service.AgentBulltinBoardService;
import com.bhu.vas.business.ds.agent.service.AgentDeviceImportLogService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import org.apache.poi.hssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.agent.model.AgentDeviceClaim;
import com.bhu.vas.business.asyn.spring.model.agent.AgentDeviceClaimImportDTO;
import com.bhu.vas.business.ds.agent.service.AgentDeviceClaimService;
import com.smartwork.msip.cores.helper.JsonHelper;

/**
 * Created by bluesand on 9/8/15.
 */
@Service
public class AgentDeviceClaimServiceHandler {

    private final Logger logger = LoggerFactory.getLogger(AgentDeviceClaimServiceHandler.class);

    @Resource
    private AgentDeviceClaimService agentDeviceClaimService;

    @Resource
    private AgentBulltinBoardService agentBulltinBoardService;

    @Resource
    private AgentDeviceImportLogService agentDeviceImportLogService;

    @Resource
    private WifiDeviceService wifiDeviceService;

    /**
     * 导入代理商设备
     * @param message
     */
    public void importAgentDeviceClaim(String message) {
        logger.info(String.format("AgentDeviceClaimServiceHandler importAgentDeviceClaim message[%s]", message));
        AgentDeviceClaimImportDTO dto =  JsonHelper.getDTO(message, AgentDeviceClaimImportDTO.class);

        //todo(bluesand)：处理POI excel,导入数据
        try {
            logger.info(String.format("Agent excel..."));
            excel(dto);
        }catch (Exception e) {
            logger.error(String.format("error[%s]",e.getMessage()));
            e.printStackTrace();

        }
    }


    public void updateAgentDeviceClaim(String message) {

        logger.info(String.format("AgentDeviceClaimServiceHandler updateAgentDeviceClaim message[%s]", message));
        AgentDeviceClaimUpdateDTO dto = JsonHelper.getDTO(message, AgentDeviceClaimUpdateDTO.class);

        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andSimpleCaulse("1=1").andColumnEqualTo("import_id", dto.getLogId());
        List<AgentDeviceClaim> agentDeviceClaims =  agentDeviceClaimService.findModelByCommonCriteria(mc);

        if (agentDeviceClaims != null) {
            for (AgentDeviceClaim agentDeviceClaim : agentDeviceClaims) {
                if (agentDeviceClaim.getStatus() != 1) {
                    agentDeviceClaim.setImport_status(1);
                    List<String> ids = wifiDeviceService.findIds("sn",agentDeviceClaim.getId());
                    if (ids != null && ids.size()>0) {
                        String id = ids.get(0);
                        WifiDevice wifiDevice = wifiDeviceService.getById(id);
                        if (wifiDevice != null) {
                            wifiDevice.setAgentuser(dto.getUid());
                            agentDeviceClaim.setMac(wifiDevice.getId());
                            wifiDeviceService.update(wifiDevice);
                        }
                        //logger.info("wifiDeviceService.update" + wifiDevice.getSn());
                    }
                }



            }
            agentDeviceClaimService.updateAll(agentDeviceClaims);
        }




    }




    private void excel(AgentDeviceClaimImportDTO dto) throws Exception {

        InputStream is = null;
        HSSFWorkbook hssfWorkbook = null;//new HSSFWorkbook(is);
        AgentDeviceClaim agentDeviceClaim = null;

        HSSFWorkbook outWorkbook = new HSSFWorkbook();
        OutputStream out = null;

        AgentDeviceImportLog agentDeviceImportLog = agentDeviceImportLogService.getById(dto.getLogId());

        int successCount = 0;
        int failCount = 0;
        try{
            is = new FileInputStream(dto.getInputPath());
            out = new FileOutputStream(dto.getOutputPath());
            hssfWorkbook = new HSSFWorkbook(is);
            //代理商导入记录
            Long import_id = agentDeviceImportLog.getId();
            logger.info(String.format("sheets size ===" + hssfWorkbook.getNumberOfSheets()));
            for (int numSheet = 0; numSheet <hssfWorkbook.getNumberOfSheets(); numSheet++) {
                logger.info(String.format("numSheet ===" + numSheet));
                HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);

                logger.info(String.format("hssfSheet ===" + hssfSheet));
                if (hssfSheet == null) {
                    continue;
                }
                HSSFSheet outSheet = outWorkbook.createSheet(hssfSheet.getSheetName());

                HSSFRow outputFirstRow = outSheet.createRow(0); // 下标为0的行开始
                HSSFCell[] firstcell = new HSSFCell[3];
                firstcell[0] = outputFirstRow.createCell(0);
                firstcell[0].setCellValue("用户id");
                firstcell[1] = outputFirstRow.createCell(1);
                firstcell[1].setCellValue("存货编码");
                firstcell[2] = outputFirstRow.createCell(2);
                firstcell[2].setCellValue("存货名称");
                firstcell[2] = outputFirstRow.createCell(3);
                firstcell[2].setCellValue("序列号");
                firstcell[2] = outputFirstRow.createCell(4);
                firstcell[2].setCellValue("MAC");
                firstcell[2] = outputFirstRow.createCell(5);
                firstcell[2].setCellValue("导入结果");

                logger.info(String.format("row size ===" + hssfSheet.getLastRowNum()));

                for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                    logger.info(String.format("row num ===" + rowNum));
                    HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                    logger.info(String.format("hssfRow" + hssfRow));
                    if (hssfRow == null) {
                        continue;
                    }
                    agentDeviceClaim = new AgentDeviceClaim();

                    HSSFCell stock_code = hssfRow.getCell(0);
                    if (stock_code == null) {
                        continue;
                    }
                    agentDeviceClaim.setStock_code(String.valueOf((int) stock_code.getNumericCellValue()));

                    HSSFCell stock_name = hssfRow.getCell(1);
                    if (stock_name == null || StringHelper.isEmpty(stock_name.getStringCellValue())) {
                        continue;
                    }
                    agentDeviceClaim.setStock_name(stock_name.getStringCellValue());

                    HSSFCell sn = hssfRow.getCell(2);
                    if (sn == null || StringHelper.isEmpty(sn.getStringCellValue())) {
                        continue;
                    }
                    agentDeviceClaim.setId(sn.getStringCellValue());

                    HSSFCell mac = hssfRow.getCell(3);
                    String macStr = "";
                    if (mac != null && !StringHelper.isEmpty(mac.getStringCellValue())) {
                        macStr = StringHelper.formatMacAddress(mac.getStringCellValue());
                    }
                    agentDeviceClaim.setMac(macStr);

                    Date date = new Date();
                    agentDeviceClaim.setSold_at(date);

                    agentDeviceClaim.setUid(agentDeviceImportLog.getAid());

                    logger.info(String.format("agentDeviceClaimService insert agentDeviceClaim[%s]", JsonHelper.getJSONString(agentDeviceClaim)));

                    WifiDevice wifiDevice = wifiDeviceService.getById(macStr);
                    if (wifiDevice != null) {
                        agentDeviceClaim.setClaim_at(date);
                        agentDeviceClaim.setStatus(1);
                    }


                    
                    HSSFRow outRow  = outSheet.createRow(rowNum);
                    HSSFCell outUid = outRow.createCell(0);
                    outUid.setCellValue(agentDeviceImportLog.getAid());
                    HSSFCell outStockCode = outRow.createCell(1);
                    outStockCode.setCellValue(String.valueOf((int) stock_code.getNumericCellValue()));
                    HSSFCell outStockName = outRow.createCell(2);
                    outStockName.setCellValue(stock_name.getStringCellValue());
                    HSSFCell outSN = outRow.createCell(3);
                    outSN.setCellValue(sn.getStringCellValue());
                    HSSFCell outMAC = outRow.createCell(4);
                    outMAC.setCellValue(macStr);

                    HSSFCell outResult = outRow.createCell(5);
                    agentDeviceClaim.setImport_id(import_id);
                    if (agentDeviceClaimService.getById(agentDeviceClaim.getId()) == null) {
                        agentDeviceClaimService.insert(agentDeviceClaim);
                        successCount ++;
                        outResult.setCellValue("success");

                    } else {
                        failCount ++;
                        outResult.setCellValue("fail");
                    }


                }
            }

            outWorkbook.write(out);

            AgentOutputDTO agentOutputDTO = new AgentOutputDTO();
            agentOutputDTO.setAid(agentDeviceImportLog.getAid());
            agentOutputDTO.setPath(dto.getOutputPath());
            agentOutputDTO.setName(dto.getOriginName());

            //发布公告给代理商
            AgentBulltinBoard agentBulltinBoard = agentBulltinBoardService.bulltinPublish(dto.getUid(), agentDeviceImportLog.getAid(), AgentBulltinType.BatchImport,
                    JsonHelper.getJSONString(agentOutputDTO));

            agentDeviceImportLog.setBid(agentBulltinBoard.getId());

            logger.info("agent excel over..... ");

        }catch(Exception ex) {
            logger.error(String.format("ex[%s]", ex.getStackTrace()));
        	ex.printStackTrace();
        }finally{
        	if(hssfWorkbook != null){
        		hssfWorkbook.close();
        		hssfWorkbook = null;
        	}
        	if(is != null){
        		is.close();
        		is = null;
        	}

            if (outWorkbook != null) {
                outWorkbook.close();
            }
            if (out != null) {
                out.close();
                out = null;
            }
        }

        //更新导入记录
        agentDeviceImportLog.setSuccess_count(successCount);
        agentDeviceImportLog.setFail_count(failCount);
        agentDeviceImportLog.setStatus(AgentDeviceImportLog.IMPORT_DONE);
        agentDeviceImportLogService.update(agentDeviceImportLog);

    }


    public static void main(String[] args) throws  Exception{
        AgentDeviceClaimImportDTO dto = new AgentDeviceClaimImportDTO();
        dto.setUid(6);
        dto.setInputPath("/Users/bluesand/Downloads/山西岩涛网络NMAC对应代理商.xls");
        dto.setOriginName("山西岩涛网络NMAC对应代理商.xls");
        dto.setOutputPath("/Users/bluesand/Desktop/output.xls");
        dto.setTs(new Date().getTime());

        new AgentDeviceClaimServiceHandler().excel(dto);
    }
}
