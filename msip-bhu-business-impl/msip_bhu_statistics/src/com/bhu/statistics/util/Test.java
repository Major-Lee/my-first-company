package com.bhu.statistics.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

public class Test {
	
	public static void readZipFile(String file) throws Exception {  
        ZipFile zf = new ZipFile(file);  
        InputStream in = new BufferedInputStream(new FileInputStream(file)); 
        ZipInputStream zin = new ZipInputStream(in);  
        ZipEntry ze;  
        while ((ze = zin.getNextEntry()) != null) {  
            if (ze.isDirectory()) {
            } else {  
                System.err.println("file - " + ze.getName() + " : "  
                        + ze.getSize() + " bytes");  
                long size = ze.getSize();  
                if (size > 0) {  
                    BufferedReader br = new BufferedReader(  
                            new InputStreamReader(zf.getInputStream(ze)));  
                    String line;  
                    while ((line = br.readLine()) != null) {
                    	if(StringUtils.contains(line, "HO{")){
                    		//System.out.println(line);
                    		String[] array = line.split(" - ");
                    		for (int i = 0; i < array.length; i++) {
								System.out.println(array[1]);
								String s = array[1].substring(2);
								//String str = "{\"mac\":\"84:82:f4:19:de:eb\",\"ts\":1445070529791,\"sm\":false,\"act\":\"DO\"}";
								JSONObject obj = new JSONObject(s);
								System.out.println(obj.getString("mac"));
							}
                    	}
                    }  
                    br.close();  
                }  
                System.out.println();  
            }  
        }  
        zin.closeEntry();  
    } 
	
	public static void main(String[] args) {
		String currDay=DataUtils.currDay();
		System.out.println(currDay);
//		 try {  
//             readZipFile("E:\\test.zip");  
//         } catch (Exception e) {  
//             // TODO Auto-generated catch block  
//             e.printStackTrace();  
//         } 
	}
}
