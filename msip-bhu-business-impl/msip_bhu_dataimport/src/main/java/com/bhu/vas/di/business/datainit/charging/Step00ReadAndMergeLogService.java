package com.bhu.vas.di.business.datainit.charging;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.elasticsearch.common.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.smartwork.msip.cores.orm.iterator.IteratorNotify;
@Service
public class Step00ReadAndMergeLogService {
	private static String hashFileMatchTemplate = "*.%s.%s.log.zip";
	@SuppressWarnings("unchecked")
	public void parser(String date,IteratorNotify<String> notify,String logpath){
		//String date = "2015-10-17";
		String logtimetemplate = "%s - %s";
		for(int i = 0;i<10;i++){
			List<String> logtimes = new ArrayList<String>();
			Map<String,String> logdedetails = new HashMap<>();
			String wildcard = String.format(hashFileMatchTemplate, String.format("%04d", i),date);
			//System.out.println(String.format("%04d", i));
			Collection<File> listFiles = FileUtils.listFiles(new File(logpath),//"/BHUData/bulogs/charginglogs-a/"), 
					new WildcardFileFilter(wildcard), 
			        new IOFileFilter() {
	                    public boolean accept(File file, String s) {
	                        return true;
	                    }
	                    public boolean accept(File file) {
	                        return true;
	                    }
	                });//("/BHUData/bulogs/charginglogs/",new WildcardFileFilter("*.???"), null);
			long index = 0l;
			boolean needsort = false;
			if(listFiles.size() > 1){
				needsort = true;
			}else{
				needsort = false;
			}
			
			
			for(File file : listFiles){
				System.out.println("log file :" + file.getAbsolutePath() + " start");
				ZipFile zf = null;
				try{
					if(file.getName().indexOf("business-charging") == -1) continue;
					zf = new ZipFile(new File(file.getAbsolutePath()), ZipFile.OPEN_READ);
					// 返回 ZIP file entries的枚举.
					Enumeration<? extends ZipEntry> entries = zf.entries();
	
					while (entries.hasMoreElements()) {
						ZipEntry ze = entries.nextElement();
						long size = ze.getSize();
						if (size > 0) {
							//System.out.println("Length is " + size);
							BufferedReader br = new BufferedReader(
									new InputStreamReader(zf.getInputStream(ze)));
							String line = null;
							while ((line = br.readLine()) != null) {
								String newkeyPrefix = line.substring(0,23);
								String content = line.substring(26);
								String key = String.format(logtimetemplate, newkeyPrefix,index);
								logtimes.add(key);
								logdedetails.put(key, content);
								index++;
							}
							br.close();
						}
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}finally{
					if(zf != null){
						try {
							zf.close();
							zf = null;
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
				System.out.println(index);
			}
			
			if(needsort){
				Collections.sort(logtimes);
				System.out.println("排序合并成功！");
			}
			
			for(String key:logtimes){
				String line = logdedetails.get(key);
				if(StringUtils.isNotEmpty(line))
					notify.notifyComming(line);
			}
			{
				logtimes.clear();logtimes = null;
				logdedetails.clear();logdedetails = null;
			}
		}
		
		/*Collection<File> listFiles = FileUtils.listFiles(new File("/BHUData/bulogs/charginglogs-a/"), 
				new WildcardFileFilter("*.2015-10-17.log.zip"), 
		        new IOFileFilter() {
                    public boolean accept(File file, String s) {
                        return true;
                    }
                    public boolean accept(File file) {
                        return true;
                    }
                });//("/BHUData/bulogs/charginglogs/",new WildcardFileFilter("*.???"), null);
		
		System.out.println(listFiles.size());
		for(File file:listFiles){
			System.out.println(file.getAbsolutePath());
		}*/
	}
}
