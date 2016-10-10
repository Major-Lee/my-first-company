package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchimport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchimport.callback.ImportElementCallback;
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
	
	public static void excelImport(String fileinputpath,String fileoutpath,ImportElementCallback callback){
		System.out.println("input file:"+fileinputpath);
		System.out.println("output file:"+fileoutpath);
        InputStream is = null;
        //HSSFWorkbook hssfWorkbook = null;//new HSSFWorkbook(is);
        //Workbook workbook = null;
        Workbook wb = null;
        Set<String> devices = new HashSet<String>();
		try{//XSSFWorkbook wb2 = (XSSFWorkbook)
			 is = new FileInputStream(fileinputpath);
			 wb = WorkbookFactory.create(is);//.openSampleWorkbook("CustomXMLMappings.xlsx");
			 //new XSSFWorkbook(new FileInputStream(args[0]));
	         //workbook = new XSSFWorkbook(is);
	         System.out.println(String.format("sheets size ===" + wb.getNumberOfSheets()));
	         for (int numSheet = 0; numSheet < wb.getNumberOfSheets(); numSheet++) {
	        	Sheet sheet = wb.getSheetAt(numSheet);
	        	if(sheet == null) continue;
	        	System.out.println(String.format("numSheet[%s] SheetName[%s]",numSheet,sheet.getSheetName()));
	        	int totalRowNum = sheet.getLastRowNum();
	        	if(totalRowNum == 0){
	        		continue;
	        	}
	        	System.out.println(String.format("numSheet[%s] SheetName[%s] totalRowNum[%s]",numSheet,sheet.getSheetName(),totalRowNum));
	        	for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
	        		System.out.println("current row:"+rowNum);
	        		Row row = sheet.getRow(rowNum);
	        		if(row == null) continue;
	        		Cell cell_sn = row.getCell(0);
	        		if(cell_sn == null) continue;
	        		String sn = cell_sn.getStringCellValue();
	        		if(StringUtils.isNotEmpty(sn) && StringUtils.isNotEmpty(sn.trim())){
	        			sn = sn.trim();
/*	        			devicesssss.add(sn);
	        			System.out.println(String.format(" row[%s] sn[%s] size[%s]", rowNum,sn,devicesssss.size()));*/
	        			DeviceCallbackDTO dcDTO = callback.elementDeviceInfoFetch(sn);
	        			Cell cell_mac = row.getCell(1);
        				if(cell_mac == null){
        					cell_mac = row.createCell(1);
        				}
        				cell_mac.setCellValue(""); //初始化为空
	        			if(dcDTO == null){
	        				row.getCell(1).setCellValue("不存在");
	        			}else{
	        				devices.add(dcDTO.getMac());
	        				cell_mac.setCellValue(dcDTO.getMac());//"84:82:f4:32:3c:80");
	        			}
	        		}
	        	}
	         }
	         File targetFile = new File(fileoutpath);
			 targetFile.getParentFile().mkdirs();
	         FileOutputStream fileOut = new FileOutputStream(targetFile);
	         wb.write(fileOut);
	         fileOut.close();
	         callback.afterExcelImported(null, devices);
		}catch(Exception ex){
			System.out.println("~~~~~~~~~~~~~~~~~~~~exception");
			ex.printStackTrace(System.out);
		}finally{
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
	
	public static int excelPreCheck(String fileinputpath,String fileoutpath,ImportElementCallback callback){
		System.out.println("input file:"+fileinputpath);
		System.out.println("output file:"+fileoutpath);
		final AtomicInteger atomic_failed = new AtomicInteger(0);
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
	        	if(sheet == null) continue;
	        	System.out.println(String.format("numSheet[%s] SheetName[%s]",numSheet,sheet.getSheetName()));
	        	int totalRowNum = sheet.getLastRowNum();
	        	if(totalRowNum == 0){
	        		continue;
	        	}
	        	System.out.println(String.format("numSheet[%s] SheetName[%s] totalRowNum[%s]",numSheet,sheet.getSheetName(),totalRowNum));
	        	for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
	        		System.out.println("current row:"+rowNum);
	        		Row row = sheet.getRow(rowNum);
	        		if(row == null) continue;
	        		Cell cell_sn = row.getCell(0);
	        		if(cell_sn == null) continue;
	        		String sn = cell_sn.getStringCellValue();
	        		if(StringUtils.isNotEmpty(sn) && StringUtils.isNotEmpty(sn.trim())){
	        			//check whether duplicated in excel
	        			int duplicated = -1;
	        			sn = sn.trim();
	        			for(int dr = 1; dr < rowNum; dr ++){
	    	        		Row check_row = sheet.getRow(dr);
	    	        		if(check_row == null)continue;
	    	        		Cell check_cell_sn = check_row.getCell(0);
	    	        		if(check_cell_sn == null) continue;
	    	        		String check_sn = check_cell_sn.getStringCellValue();
	    	        		if(!StringUtils.isEmpty(check_sn) && sn.equals(check_sn.trim())){
	    	        			duplicated = dr;
	    	        			break;
	    	        		}
	        			}
	        			Cell cell_msg = row.getCell(1);
        				if(cell_msg == null){
        					cell_msg = row.createCell(1);
        				}
        				cell_msg.setCellValue(""); //初始化为空
	        			if(duplicated != -1){
	        				cell_msg.setCellValue(String.format("和第%d行重复", duplicated));
				        	atomic_failed.incrementAndGet();
	        			} else {
		        			DeviceCallbackDTO dcDTO = callback.elementDeviceInfoFetch(sn);
		        			if(dcDTO == null){
		        				row.getCell(1).setCellValue("不存在");
					        	atomic_failed.incrementAndGet();
		        			}else{
		        				if(!StringUtils.isEmpty(dcDTO.getLast_batchno())){
			        				cell_msg.setCellValue(String.format("%s, 批次[%s]已导入", dcDTO.getMac(), dcDTO.getLast_batchno()));
						        	atomic_failed.incrementAndGet();
		        				} else {
		        					cell_msg.setCellValue(dcDTO.getMac());//"84:82:f4:32:3c:80");
		        				}
		        			}
	        			}
	        		}
	        	}
	         }
	         File targetFile = new File(fileoutpath);
			 targetFile.getParentFile().mkdirs();
	         FileOutputStream fileOut = new FileOutputStream(targetFile);
	         wb.write(fileOut);
	         fileOut.close();
		}catch(Exception ex){
			System.out.println("~~~~~~~~~~~~~~~~~~~~exception");
			ex.printStackTrace(System.out);
		}finally{
            if(is != null){
                try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace(System.out);
				}
                is = null;
            }
		}
        return atomic_failed.intValue();
	}
	
	
	public static void main(String[] argv){
		//uRouter-20160426-双翼.xlsx
		//uRouter-20160426-共进.xlsx
		String filepath = "/Users/Edmond/gospace/20160523-00000008.xlsx";
		ShipmentExcelImport.excelImport(filepath, filepath, new ImportElementCallback(){
			@Override
			public DeviceCallbackDTO elementDeviceInfoFetch(String sn) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void afterExcelImported(Set<String> dmacs) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}
