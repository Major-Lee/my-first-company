package com.bhu.vas.di.op.batchimport.shipmentlist;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.di.op.batchimport.shipmentlist.callback.ExcelElementCallback;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

public class ShipmentExcelImport {
	private static final Set<String> MacFields = new HashSet<String>();
	private static final Set<String> SNFields = new HashSet<String>();
	static{
		//共进
		MacFields.add("mac");
		SNFields.add("sn");
		//双翼
		MacFields.add("MAC");
		SNFields.add("CISN");
	}
	
	public static void excelImport(String filepath,ExcelElementCallback callback){
        InputStream is = null;
        //HSSFWorkbook hssfWorkbook = null;//new HSSFWorkbook(is);
        //Workbook workbook = null;
        Workbook wb = null;
		try{//XSSFWorkbook wb2 = (XSSFWorkbook)
			 is = new FileInputStream(filepath);
			 wb = WorkbookFactory.create(is);//.openSampleWorkbook("CustomXMLMappings.xlsx");
			 //new XSSFWorkbook(new FileInputStream(args[0]));
	         //workbook = new XSSFWorkbook(is);
	         System.out.println(String.format("sheets size ===" + wb.getNumberOfSheets()));
	         for (int numSheet = 0; numSheet < wb.getNumberOfSheets(); numSheet++) {
	        	Sheet sheet = wb.getSheetAt(numSheet);
	        	System.out.println(String.format("numSheet[%s] SheetName[%s]",numSheet,sheet.getSheetName()));
	        	int totalRowNum = sheet.getLastRowNum();
	        	if(totalRowNum == 0){
	        		continue;
	        	}

	        	for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
	        		Row row = sheet.getRow(rowNum);
	        		String sn = row.getCell(0).getStringCellValue();
	        		if(StringUtils.isNotEmpty(sn)){
	        			WifiDevice wifiDevice = callback.elementCallback(sn);
	        			if(wifiDevice == null){
	        				;//row.getCell(1).setCellValue("不存在");
	        			}else{
	        				Cell cell_mac = row.getCell(1);
	        				if(cell_mac == null){
	        					cell_mac = row.createCell(1);
	        				}
	        				//System.out.println(wifiDevice.getId());
	        				cell_mac.setCellValue(wifiDevice.getId());//"84:82:f4:32:3c:80");
	        			}
	        		}
	        	}
	        	
	            /*HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
                System.out.println(String.format("hssfSheet ===" + hssfSheet));
                if (hssfSheet == null) {
                    continue;
                }
                System.out.println(String.format("row size ===" + hssfSheet.getLastRowNum()));
                for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                	System.out.println(String.format("row num ===" + rowNum));
                    HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                    System.out.println(String.format("hssfRow" + hssfRow));
                    if (hssfRow == null) {
                        continue;
                    }
                    HSSFCell stock_code = hssfRow.getCell(0);
                    if (stock_code == null) {
                        continue;
                    }

                    HSSFCell stock_name = hssfRow.getCell(1);
                    if (stock_name == null || StringHelper.isEmpty(stock_name.getStringCellValue())) {
                        continue;
                    }

                    HSSFCell sn = hssfRow.getCell(2);
                    if (sn == null || StringHelper.isEmpty(sn.getStringCellValue())) {
                        continue;
                    }

                    HSSFCell mac = hssfRow.getCell(3);
                    String macStr = "";
                    if (mac != null && !StringHelper.isEmpty(mac.getStringCellValue())) {
                        macStr = StringHelper.formatMacAddress(mac.getStringCellValue());
                    }

                }*/
	         }
	         
	         FileOutputStream fileOut = new FileOutputStream("/Users/Edmond/gospace/ooxml_dataFormat.xlsx");
	         wb.write(fileOut);
	         fileOut.close();
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}finally{
			/*if(wb != null){
                try {
					wb.close();
				} catch (IOException e) {
					e.printStackTrace(System.out);
				}
                wb = null;
            }*/
            if(is != null){
                try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace(System.out);
				}
                is = null;
            }
		}
	}
	
	
	public static void main(String[] argv){
		//uRouter-20160426-双翼.xlsx
		//uRouter-20160426-共进.xlsx
		String filepath = "/Users/Edmond/gospace/库房出库清单-20160426-0001.xlsx";
		ShipmentExcelImport.excelImport(filepath, new ExcelElementCallback(){
			@Override
			public WifiDevice elementCallback(String sn) {
				// TODO Auto-generated method stub
				return null;
			}
		});
	}
}
