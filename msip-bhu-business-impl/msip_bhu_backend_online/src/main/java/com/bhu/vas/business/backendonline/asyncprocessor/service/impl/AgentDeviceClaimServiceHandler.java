package com.bhu.vas.business.backendonline.asyncprocessor.service.impl;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.annotation.Resource;

import com.bhu.vas.api.helper.AgentBulltinType;
import com.bhu.vas.api.rpc.agent.dto.AgentOutputDTO;
import com.bhu.vas.api.rpc.agent.model.AgentDeviceImportLog;
import com.bhu.vas.business.ds.agent.service.AgentBulltinBoardService;
import com.bhu.vas.business.ds.agent.service.AgentDeviceImportLogService;
import com.smartwork.msip.cores.helper.StringHelper;
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


    private void excel(AgentDeviceClaimImportDTO dto) throws Exception {

        InputStream is = null;
        HSSFWorkbook hssfWorkbook = null;//new HSSFWorkbook(is);
        AgentDeviceClaim agentDeviceClaim = null;

        HSSFWorkbook outWorkbook = new HSSFWorkbook();
        OutputStream out = null;


        try{
            is = new FileInputStream(dto.getInputPath());
            out = new FileOutputStream(dto.getOutputPath());
            hssfWorkbook = new HSSFWorkbook(is);


            int totalCount = 0;


            for (int numSheet = 0; numSheet <hssfWorkbook.getNumberOfSheets(); numSheet++) {
                HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);

                HSSFSheet outSheet = outWorkbook.createSheet(hssfSheet.getSheetName());

                if (hssfSheet == null) {
                    continue;
                }

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

                for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                    HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                    if (hssfRow == null) {
                        continue;
                    }
                    agentDeviceClaim = new AgentDeviceClaim();

                    HSSFCell stock_code = hssfRow.getCell(0);
                    agentDeviceClaim.setStock_code(String.valueOf(stock_code.getNumericCellValue()));

                    HSSFCell stock_name = hssfRow.getCell(1);
                    agentDeviceClaim.setStock_name(stock_name.getStringCellValue());

                    HSSFCell sn = hssfRow.getCell(2);
                    if (StringHelper.isEmpty(sn.getStringCellValue())) {
                        continue;
                    }
                    agentDeviceClaim.setId(sn.getStringCellValue());

                    HSSFCell mac = hssfRow.getCell(3);
                    if (StringHelper.isEmpty(mac.getStringCellValue())) {
                        continue;
                    }
                    agentDeviceClaim.setMac(StringHelper.formatMacAddress(mac.getStringCellValue()));

                    Date date = new Date();
                    agentDeviceClaim.setSold_at(date);

                    agentDeviceClaim.setUid(dto.getAid());

                    logger.info(String.format("agentDeviceClaimService insert agentDeviceClaim[%s]", JsonHelper.getJSONString(agentDeviceClaim)));
                    agentDeviceClaimService.insert(agentDeviceClaim);

                    HSSFRow outRow  = outSheet.createRow(rowNum);
                    HSSFCell outUid = outRow.createCell(0);
                    outUid.setCellValue(dto.getAid());
                    HSSFCell outStockCode = outRow.createCell(1);
                    outStockCode.setCellValue(String.valueOf(stock_code.getNumericCellValue()));
                    HSSFCell outStockName = outRow.createCell(2);
                    outStockName.setCellValue(stock_name.getStringCellValue());
                    HSSFCell outSN = outRow.createCell(3);
                    outSN.setCellValue(sn.getStringCellValue());
                    HSSFCell outMAC = outRow.createCell(4);
                    outMAC.setCellValue(mac.getStringCellValue());


                    totalCount ++;

                }
            }

            outWorkbook.write(out);

            AgentOutputDTO agentOutputDTO = new AgentOutputDTO();
            agentOutputDTO.setAid(dto.getAid());
            agentOutputDTO.setPath(dto.getOutputPath());

            agentBulltinBoardService.bulltinPublish(dto.getUid(), dto.getAid(), AgentBulltinType.BatchImport,
                    JsonHelper.getJSONString(agentOutputDTO));

            AgentDeviceImportLog agentDeviceImportLog = new AgentDeviceImportLog();
            agentDeviceImportLog.setCount(totalCount);
            agentDeviceImportLog.setAid(dto.getAid());
            agentDeviceImportLog.setCreated_at(new Date());
            agentDeviceImportLogService.insert(agentDeviceImportLog);

        }catch(Exception ex){
        	ex.printStackTrace(System.out);
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

    }


    public static void main(String[] args) throws  Exception{
        AgentDeviceClaimImportDTO dto = new AgentDeviceClaimImportDTO();
        dto.setAid(1000023);
        dto.setUid(6);
        dto.setInputPath("/Users/bluesand/Downloads/山西岩涛网络NMAC对应代理商.xls");
        dto.setOriginName("山西岩涛网络NMAC对应代理商.xls");
        dto.setOutputPath("/Users/bluesand/Desktop/output.xls");
        dto.setTs(new Date().getTime());

        new AgentDeviceClaimServiceHandler().excel(dto);
    }
}
