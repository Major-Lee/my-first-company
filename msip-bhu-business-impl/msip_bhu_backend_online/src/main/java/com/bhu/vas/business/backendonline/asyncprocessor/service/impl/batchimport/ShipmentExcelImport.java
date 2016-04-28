package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchimport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchimport.callback.ExcelElementCallback;
import com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchimport.dto.DeviceCallbackDTO;

public class ShipmentExcelImport {
	/*private static final Set<String> MacFields = new HashSet<String>();
	private static final Set<String> SNFields = new HashSet<String>();
	static{
		//共进
		MacFields.add("mac");
		SNFields.add("sn");
		//双翼
		MacFields.add("MAC");
		SNFields.add("CISN");
	}*/
	
	public static void excelImport(String fileinputpath,String fileoutpath,ExcelElementCallback callback){
		System.out.println("input file:"+fileinputpath);
		System.out.println("output file:"+fileoutpath);
        InputStream is = null;
        //HSSFWorkbook hssfWorkbook = null;//new HSSFWorkbook(is);
        //Workbook workbook = null;
        Workbook wb = null;
		try{//XSSFWorkbook wb2 = (XSSFWorkbook)
			 is = new FileInputStream(fileinputpath);
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
	        			DeviceCallbackDTO dcDTO = callback.elementDeviceInfoFetch(sn);
	        			Cell cell_mac = row.getCell(1);
        				if(cell_mac == null){
        					cell_mac = row.createCell(1);
        				}
	        			if(dcDTO == null){
	        				row.getCell(1).setCellValue("不存在");
	        			}else{
	        				cell_mac.setCellValue(dcDTO.getMac());//"84:82:f4:32:3c:80");
	        			}
	        		}
	        	}
	         }
	         File targetFile = new File(fileoutpath);
			 targetFile.mkdirs();
	         FileOutputStream fileOut = new FileOutputStream(targetFile);
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
	/*public static void main(String[] argv){
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
	}*/
}
