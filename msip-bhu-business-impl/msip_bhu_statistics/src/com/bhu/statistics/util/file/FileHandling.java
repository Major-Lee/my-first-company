package com.bhu.statistics.util.file;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.json.JSONObject;

import com.bhu.statistics.util.cache.BhuCache;

/**
 * 文件处理类
 * @author Jason
 *
 */
public class FileHandling {
	//PV【设备连接总数】总和
	private static long pvNum = 0;
	//UV【设备连接总人数】
	private static long uvNum = 0;
	//存储当日设备连接人数hmac
	private static List<String> uvList = new ArrayList<String>();
	//存储当日设备的连接总数
	private static List<String> pvList = new ArrayList<String>();
	/**
	 * 读取文件
	 * @author Jason
	 * @param filePath 文件路径
	 * @return
	 */
	public static String readFile(String filePath) {
		if(StringUtils.isBlank(filePath) || StringUtils.isEmpty(filePath)){
			System.out.println("文件路径为空");
			return null;
		}
		try {
			//文件读取
			File file = new File(filePath);
			//获取当前路径下所有文件以及文件夹
			File[] FileList = file.listFiles();
			for (int i = 0; i < FileList.length; i++) {
				File currFile = FileList[i];
				if(currFile.getName().contains(".DS_Store")){
					continue;
				}
				if(currFile.getName().contains("chargingsimulogs")){
					continue;
				}
				if(currFile.isDirectory()){
					readFile(currFile.getAbsolutePath());
				}else{
					readZipFile(currFile.getAbsolutePath());
				}
			}
		} catch (Exception e) {
			
		}
		return null;
	}
	
	/**
	 * 读取压缩包内容
	 * @param filePath
	 * @return 
	 */
	public static String readZipFile(String filePath){
		//获取当前缓存中PV总数
		String totalPvNum = BhuCache.getInstance().getTotalPV("totalPV");
		if(StringUtils.isBlank(totalPvNum)){
			totalPvNum = "0";
		}
		pvNum = NumberUtils.toLong(totalPvNum);
		//返回结果
		String result = StringUtils.EMPTY;
		
		 try {
				ZipFile zipFile = new ZipFile(filePath);
				InputStream in = new BufferedInputStream(new FileInputStream(filePath)); 
		        ZipInputStream zin = new ZipInputStream(in);
		        ZipEntry ze;  
		        int n = 1;
		        while ((ze = zin.getNextEntry()) != null) {  
		        	zin.closeEntry();
		            if (!ze.isDirectory()) {
		            	String name = ze.getName();
		                //long size = ze.getSize();
		            	System.err.println("file - " + ze.getName() + " : "  
		                        + ze.getSize() + " bytes");
		                long size = ze.getSize();  
		                if (size > 0) {  
		                    BufferedReader br = new BufferedReader(  
		                            new InputStreamReader(zipFile.getInputStream(ze)));  
		                    //读取内容
		                    String line = StringUtils.EMPTY;  
		                    while ((line = br.readLine()) != null) {
		                    	if(StringUtils.contains(line, "HO{")){
		                    		//System.out.println(line);
		                    		String[] array = line.split(" - ");
		                    		//String totalPvNum = BhuCache.getInstance().getTotalPV("totalPV");
		                    		System.out.println("totalPV***************"+totalPvNum);
										System.out.println(array[1]);
										String s = array[1].substring(2);
										//String str = "{\"mac\":\"84:82:f4:19:de:eb\",\"ts\":1445070529791,\"sm\":false,\"act\":\"DO\"}";
										JSONObject obj = new JSONObject(s);
										System.out.println("*****mac****"+obj.getString("mac"));
										System.out.println("*****hmac****"+obj.getString("hmac"));
										//取出当前设备的mac地址
										result = obj.getString("mac");
										pvList.add(result);
										//存储当前设备的连接总次数
										BhuCache.getInstance().setTotalPV("totalPV", String.valueOf(pvNum));
										//根据mac
										pvNum++;
										//获取当前连接此设备的终端Mac地址
										result = obj.getString("hmac");
										if(uvList.contains(result)){
											uvList.add(result);
										}
		                    	}
		                    }  
		                    br.close();  
		                }
		            } 
		        }  
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//获取当前缓存中的UV总数
		String totalUvNum = BhuCache.getInstance().getTotalPV("totalPV");
		if(StringUtils.isBlank(totalUvNum)){
			totalUvNum = "0";
		}
		uvNum = NumberUtils.toLong(totalUvNum);
		uvNum = uvNum+uvList.size();
		//存储当前设备连接的总人数
		BhuCache.getInstance().setTotalUV("totalUV", String.valueOf(uvNum));
		//存储当日设备的连接总数
		BhuCache.getInstance().setDayPV(getNextDay(), "dayPV", String.valueOf(pvList.size()));
		//存储当日设备连接总人数
		BhuCache.getInstance().setDayUV(getNextDay(), "dayUV", String.valueOf(uvList.size()));
		return result;
	} 
	
	public static void main(String args[]){
		String filePath = "E:\\2016-04-20";
		try {
			readFile(filePath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String totalPvNum = BhuCache.getInstance().getTotalPV("totalPV");
		System.out.println("totalPV***************"+totalPvNum);
	}
	
	public static String getNextDay() {
		Date d = new Date();  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(d);  
        calendar.add(Calendar.DAY_OF_MONTH, -1);  
        d = calendar.getTime();  
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
        String dateNowStr = sdf.format(d); 
        return dateNowStr;
    } 
}
