package com.bhu.vas.di.op.advertise;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.sms.SmsSenderFactory;




public class smsXlsxOp {
	public static void main(String[] argv){
		String filepath = "E://运营商1229.xlsx";
		String accs[] = excelImport(filepath);
		String msg ="如何组网、路由 or AP、信道划分、网络优化、快速定位及故障排查等，更多无线网络专业知识就在今晚（12月28日）18：30分，锁定斗鱼直播间！https://www.douyu.com/room/share/1461579，必虎解决方案部高级经理无线wifi行业资深老炮—王景召同学，教你WiFi行业相关技能，带你步入专家行列！！！ 回复TD退订";
		String response = SmsSenderFactory.buildSender(
				BusinessRuntimeConfiguration.InternalMarketingSMS_Gateway).send(msg, accs);
		System.out.println(response);
	}
	
	public static String[] excelImport(String filepath){
        InputStream is = null;
        Workbook wb = null;
        String[] accs = null;
		try{
			 is = new FileInputStream(filepath);
			 wb = WorkbookFactory.create(is);
	         System.out.println(String.format("sheets size ===" + wb.getNumberOfSheets()));
	         for (int numSheet = 0; numSheet < wb.getNumberOfSheets(); numSheet++) {
	        	Sheet sheet = wb.getSheetAt(numSheet);
	        	System.out.println(String.format("numSheet[%s] SheetName[%s]",numSheet,sheet.getSheetName()));
	        	int totalRowNum = sheet.getLastRowNum();
	        	accs = new String[totalRowNum]; 
	        	System.out.println("totalRowNum:" +totalRowNum );
	        	if(totalRowNum == 0){
	        		continue;
	        	}
	        	for (int rowNum = 0; rowNum < totalRowNum; rowNum++) {
		        	Row rowHeader = sheet.getRow(rowNum);
	        		Cell cellHeader = rowHeader.getCell(0);
	        		double cellValue = cellHeader.getNumericCellValue();
	        		BigDecimal bigDecimal = new BigDecimal(cellValue);
	        		String acc = bigDecimal.toString();
	        		accs[rowNum] = acc;
	        	}
	         }
		}catch(Exception ex){
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
		return accs;
	}
}