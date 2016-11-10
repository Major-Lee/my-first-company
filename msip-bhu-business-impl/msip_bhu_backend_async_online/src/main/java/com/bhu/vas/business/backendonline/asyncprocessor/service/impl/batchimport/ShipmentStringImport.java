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
	
	public static boolean stringImport(String fileinputpath, String fileoutpath, ImportElementCallback callback){
		System.out.println(Thread.currentThread().getId() + ":input file:"+fileinputpath);
		String snsstr = null;
		try {
			snsstr = FileUtils.readFileToString(new File(fileinputpath));
		} catch (IOException e) {
			System.out.println(String.format(Thread.currentThread().getId() + ":文件[%s]打开失败", fileinputpath));
			e.printStackTrace();
			return false;
		}
		if(StringUtils.isEmpty(snsstr)){
			System.out.println(Thread.currentThread().getId() + ":input file:"+fileinputpath);
			return false;
		}
		String[] sns = snsstr.split(StringHelper.COMMA_STRING_GAP);
		
        Set<String> devices = new HashSet<String>();
        Set<String> failed_sn = new HashSet<String>();
        StringBuilder sb = new StringBuilder();
		try{
	         System.out.println(String.format(Thread.currentThread().getId() + ":sns size ===" + sns.length));
	         for (String sn:sns){
        		if(StringUtils.isNotEmpty(sn) && StringUtils.isNotEmpty(sn.trim())){
	        		System.out.println("sn:" + sn);
        			sn = sn.trim();
        			DeviceCallbackDTO dcDTO = callback.elementDeviceInfoFetch(sn);
        			if(dcDTO == null){
        				failed_sn.add(sn);
        				sb.append(sn);
        		        System.out.println(String.format(Thread.currentThread().getId() + ":[%s] 不存在", sn));
        			}else{
        				devices.add(dcDTO.getMac());
        				sb.append(String.format("%s %s", sn, dcDTO.getMac()));
            			System.out.println(Thread.currentThread().getId() + ":sn -> " + dcDTO.getMac());
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
	         return true;
		}catch(Exception ex){
			System.out.println(Thread.currentThread().getId() + ":~~~~~~~~~~~~~~~~~~~~exception");
			ex.printStackTrace(System.out);
			return false;
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
