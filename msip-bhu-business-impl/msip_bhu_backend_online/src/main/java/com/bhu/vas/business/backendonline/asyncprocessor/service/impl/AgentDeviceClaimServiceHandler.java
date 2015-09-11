package com.bhu.vas.business.backendonline.asyncprocessor.service.impl;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
        try{
            is = new FileInputStream(dto.getPath());
            hssfWorkbook = new HSSFWorkbook(is);
            for (int numSheet = 0; numSheet <hssfWorkbook.getNumberOfSheets(); numSheet++) {
                HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
                if (hssfSheet == null) {
                    continue;
                }
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
                    agentDeviceClaim.setUid((int)uid.getNumericCellValue());

                    HSSFCell sn = hssfRow.getCell(1);

                    agentDeviceClaim.setId(sn.getStringCellValue());

                    Date date = new Date();
                    agentDeviceClaim.setSold_at(date);
                    logger.info(String.format("agentDeviceClaimService insert agentDeviceClaim[%s]",JsonHelper.getJSONString(agentDeviceClaim)));
                    agentDeviceClaimService.insert(agentDeviceClaim);
                }
            }
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
        }
        

    }
}
