package com.bhu.vas.business.backendonline.asyncprocessor.service.impl;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.annotation.Resource;

import com.bhu.vas.api.helper.AgentBulltinType;
import com.bhu.vas.business.ds.agent.service.AgentBulltinBoardService;
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
            for (int numSheet = 0; numSheet <hssfWorkbook.getNumberOfSheets(); numSheet++) {
                HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);

                HSSFSheet outSheet = hssfSheet;

                if (hssfSheet == null) {
                    continue;
                }

                HSSFRow outputFirstRow = outSheet.createRow(0); // 下标为0的行开始
                HSSFCell[] firstcell = new HSSFCell[3];
                firstcell[0] = outputFirstRow.createCell(0);
                firstcell[0].setCellValue("uid");
                firstcell[1] = outputFirstRow.createCell(1);
                firstcell[1].setCellValue("SN");
                firstcell[2] = outputFirstRow.createCell(2);
                firstcell[2].setCellValue("aid");

                for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                    HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                    if (hssfRow == null) {
                        continue;
                    }
                    agentDeviceClaim = new AgentDeviceClaim();

                    HSSFCell uid = hssfRow.getCell(0);

                    if (uid == null) {
                        continue;
                    }
                    agentDeviceClaim.setUid((int) uid.getNumericCellValue());

                    HSSFCell sn = hssfRow.getCell(1);

                    agentDeviceClaim.setId(sn.getStringCellValue());

                    Date date = new Date();
                    agentDeviceClaim.setSold_at(date);
                    logger.info(String.format("agentDeviceClaimService insert agentDeviceClaim[%s]",JsonHelper.getJSONString(agentDeviceClaim)));
                    agentDeviceClaimService.insert(agentDeviceClaim);


                    HSSFRow outRow  = outSheet.createRow(rowNum);
                    HSSFCell outUid = outRow.createCell(0);
                    outUid.setCellValue(String.valueOf((int) uid.getNumericCellValue()));
                    HSSFCell outSN = outRow.createCell(1);
                    outSN.setCellValue(sn.getStringCellValue());
                    HSSFCell outUUid = outRow.createCell(2);
                    outUUid.setCellValue(String.valueOf(dto.getAid()));

                }
            }


            outWorkbook.write(out);

            agentBulltinBoardService.bulltinPublish(dto.getUid(), dto.getAid(), AgentBulltinType.BatchImport,
                    "设备发放完毕，<a href='"+dto.getOutputPath() + "'>下载</a>");



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
}
