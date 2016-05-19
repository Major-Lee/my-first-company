package com.bhu.statistics.util;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.bhu.statistics.util.cache.BhuCache;

/**
 * �ļ���ȡUtil
 * @author Jason
 *
 */
public class FileReadUtils {
	private static Logger log = Logger.getLogger(FileReadUtils.class);
	/**
	 * ��ȡ�ļ�����
	 * @param filePath �ļ�·��
	 * @return
	 * @throws IOException 
	 */
	public static  String readFile(String filePath) throws IOException{
		if(StringUtils.isBlank(filePath) || StringUtils.isEmpty(filePath)){
			//log.info("��ȡ��ȡ�ļ�·��Ϊ��");
			System.out.println("��ȡ��ȡ�ļ�·��Ϊ��");
			return null;
		}
		try {
			File file = new File(filePath);
			if(file.isDirectory()){
				//log.info("����charginglogsĿ¼����������");
				System.out.println("����charginglogsĿ¼����������");
				String[] filelist = file.list();
				//mac��ַ
				String mac = StringUtils.EMPTY;
				for (int i = 0; i < filelist.length; i++) {
					//��ȡ�ļ��µ��ļ����� ��ȡmac��ַ
					System.out.println(filePath+"\\"+filelist[i]);
					mac = readZipFile(filePath+"\\"+filelist[i]);
					if(mac == null){
						continue;
					}
					//log.info("��ȡ��ǰmac��ַΪ��"+mac);
					System.out.println("��ȡ��ǰmac��ַΪ��"+mac);
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return null;
	}
	
	/**
	 * 读取压缩包内容
	 * @param filePath
	 * @return 
	 */
	public static String readZipFile(String filePath){
		//返回结果
		String result = StringUtils.EMPTY;
		 try {
			//获取当前 文件
			File file = new File(filePath);
			String[] filelist = file.list();
			if(filelist == null || filelist.length<=0){
				return null;
			}
			//压缩包路径
			String zipFilePath = StringUtils.EMPTY;
			for (int i = 0; i < filelist.length; i++) {
				zipFilePath = filePath+"\\"+filelist[i];
				ZipFile zipFile = new ZipFile(zipFilePath);
				InputStream in = new BufferedInputStream(new FileInputStream(zipFilePath)); 
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
		                    //��ȡ����
		                    String line = StringUtils.EMPTY;  
		                    while ((line = br.readLine()) != null) {
		                    	if(StringUtils.contains(line, "HO{")){
		                    		//System.out.println(line);
		                    		String[] array = line.split(" - ");
		                    		String totalPvNum = BhuCache.getInstance().getTotalPV("totalPV");
		                    		System.out.println("totalPV***************"+totalPvNum);
										System.out.println(array[1]);
										String s = array[1].substring(2);
										//String str = "{\"mac\":\"84:82:f4:19:de:eb\",\"ts\":1445070529791,\"sm\":false,\"act\":\"DO\"}";
										JSONObject obj = new JSONObject(s);
										System.out.println("*****mac****"+obj.getString("mac"));
										System.out.println("*****hmac****"+obj.getString("hmac"));
										result = obj.getString("mac");
										BhuCache.getInstance().setTotalPV("totalPV", String.valueOf(n));
										n++;
		                    	}
		                    }  
		                    br.close();  
		                }
		            } 
		        }  
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	} 
	
	public static void main(String args[]){
		String filePath = "E:\\test.zip";
		try {
			readFile(filePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String totalPvNum = BhuCache.getInstance().getTotalPV("totalPV");
		System.out.println("totalPV***************"+totalPvNum);
	}
}
