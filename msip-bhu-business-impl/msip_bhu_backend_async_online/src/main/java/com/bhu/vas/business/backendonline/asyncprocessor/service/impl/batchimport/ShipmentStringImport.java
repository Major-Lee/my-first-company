package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchimport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchimport.callback.ImportElementCallback;
import com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchimport.dto.DeviceCallbackDTO;
import com.smartwork.msip.cores.helper.FileHelper;
import com.smartwork.msip.cores.helper.StringHelper;

public class ShipmentStringImport {
	
	public static void stringImport(String fileinputpath, String fileoutpath, ImportElementCallback callback){
		System.out.println("input file:"+fileinputpath);
		String snsstr = null;
		try {
			snsstr = FileUtils.readFileToString(new File(fileinputpath));
		} catch (IOException e) {
			System.out.println(String.format("文件[%s]打开失败", fileinputpath));
			e.printStackTrace();
			return;
		}
		if(StringUtils.isEmpty(snsstr)){
			System.out.println("input file:"+fileinputpath);
			return;
		}
		String[] sns = snsstr.split(StringHelper.COMMA_STRING_GAP);
		
        Set<String> devices = new HashSet<String>();
        Set<String> failed_sn = new HashSet<String>();
        StringBuilder sb = new StringBuilder();
		try{
	         System.out.println(String.format("sns size ===" + sns.length));
	         for (String sn:sns){
        		if(StringUtils.isNotEmpty(sn) && StringUtils.isNotEmpty(sn.trim())){
	        		System.out.println("sn:" + sn);
        			sn = sn.trim();
        			DeviceCallbackDTO dcDTO = callback.elementDeviceInfoFetch(sn);
        			if(dcDTO == null){
        				failed_sn.add(sn);
        				sb.append(sn);
        		        System.out.println(String.format("[%s] 不存在", sn));
        			}else{
        				devices.add(dcDTO.getMac());
        				sb.append(String.format("%s %s", sn, dcDTO.getMac()));
        			}
    				sb.append("\n");
        		}
	         }
	         callback.afterExcelImported(devices, failed_sn);
	         String foutstr = sb.toString();
	         if(StringUtils.isNotEmpty(foutstr)){
		         File targetFile = new File(fileoutpath);
				 targetFile.getParentFile().mkdirs();
	        	 FileHelper.StrToFile(foutstr, fileoutpath);
	         }

		}catch(Exception ex){
			System.out.println("~~~~~~~~~~~~~~~~~~~~exception");
			ex.printStackTrace(System.out);
		}
	}

	public static void main(String[] argv){
		String filepath = "/Users/Edmond/gospace/20160523-00000008.xlsx";
		ShipmentStringImport.stringImport(filepath, null, new ImportElementCallback(){
			@Override
			public DeviceCallbackDTO elementDeviceInfoFetch(String sn) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void afterExcelImported(Set<String> dmacs, Set<String> failed_sn) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}
