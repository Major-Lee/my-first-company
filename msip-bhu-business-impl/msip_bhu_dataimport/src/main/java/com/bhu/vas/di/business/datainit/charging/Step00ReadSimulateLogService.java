package com.bhu.vas.di.business.datainit.charging;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.springframework.stereotype.Service;

import com.smartwork.msip.cores.orm.iterator.IteratorNotify;
@Service
public class Step00ReadSimulateLogService {
	private static String hashFileMatchTemplate = "*.%s.log.zip";
	@SuppressWarnings("unchecked")
	public void parser(String date,IteratorNotify<String> notify,String logpath){
		//String date = "2015-10-17";
		String wildcard = String.format(hashFileMatchTemplate,date);
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
							notify.notifyComming(content);
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
	}
}
