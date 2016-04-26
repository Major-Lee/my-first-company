package com.bhu.vas.di.op.batchimport.frommanufacturing;


/**
 * 出厂设备清单数据导入程序
 * @author Edmond
 *
 */
public class ManufacturerDeviceDataImportOP {
	
	/*public static void main(String[] argv) {//throws ElasticsearchException, ESException, IOException, ParseException{
		if(argv.length < 5) return;
		String oper = argv[0];// ADD REMOVE
		
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		UserService userService = (UserService)ctx.getBean("userService");
		UserTokenService userTokenService = (UserTokenService)ctx.getBean("userTokenService");
		UserFacadeService userFacadeService = (UserFacadeService)ctx.getBean("userFacadeService");
		
        InputStream is = null;
        HSSFWorkbook hssfWorkbook = null;//new HSSFWorkbook(is);
        AgentDeviceClaim agentDeviceClaim = null;

        HSSFWorkbook outWorkbook = new HSSFWorkbook();
        OutputStream out = null;

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
                    agentDeviceClaim.setMac(macStr.toLowerCase());

                    Date date = new Date();
                    agentDeviceClaim.setSold_at(date);

                    agentDeviceClaim.setUid(agentDeviceImportLog.getAid());

                    logger.info(String.format("agentDeviceClaimService insert agentDeviceClaim[%s]", JsonHelper.getJSONString(agentDeviceClaim)));

//                    WifiDevice wifiDevice = wifiDeviceService.getById(macStr);
//                    if (wifiDevice != null) {
//                        agentDeviceClaim.setClaim_at(date);
//                        agentDeviceClaim.setStatus(1);
//                    }



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
                        outResult.setCellValue("duplicate imported");
                    }


                }
            }

            outWorkbook.write(out);

        }catch(Exception ex) {
            //logger.error(String.format("ex[%s]", ex.getStackTrace()));
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
	}*/
}
