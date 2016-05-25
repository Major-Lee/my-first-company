package com.bhu.statistics.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.springframework.expression.spel.ast.OpNE;

import com.bhu.statistics.util.um.OpenApiCnzzImpl;

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
		OpenApiCnzzImpl apiCnzzImpl=new OpenApiCnzzImpl();
		//String mobileUv= apiCnzzImpl.queryCnzzStatistic("mobile打赏页PV", "2015-05-09", "2016-5-25", "date,os", "os in ('android','ios')",2);
		//String mobileUvs= apiCnzzImpl.queryCnzzStatistic("mobile打赏页PV", "2015-05-09", "2016-5-25", "date", "os in ('android','ios')",2);
		//System.out.println(mobileUvs);
		String str="2016-05-09";
        String str1="2016-5-25";
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        try {
            start.setTime(format.parse(str));
            end.setTime(format.parse(str1));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        while(start.before(end))
        {
            System.out.println(format.format(start.getTime()));
            start.add(Calendar.DAY_OF_MONTH,1);
            
            System.out.println("---");
        }

		//		 try {  
//             readZipFile("E:\\test.zip");  
//         } catch (Exception e) {  
//             // TODO Auto-generated catch block  
//             e.printStackTrace();  
//         } 
	}
}
