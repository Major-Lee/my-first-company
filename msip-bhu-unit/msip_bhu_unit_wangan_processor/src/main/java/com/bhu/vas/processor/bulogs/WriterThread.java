package com.bhu.vas.processor.bulogs;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WriterThread extends Thread {
	private static Long count;
	private static long startTime;
	private static String logFile;
	private static String archiveFile;
	private static FileWriter  fw = null; 
	private static boolean quit;
	public static final SimpleDateFormat yyyymmddhhmmss = new SimpleDateFormat(
			"yyyyMMddHHmmss");
	
	public static final  SimpleDateFormat datetimeFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	 static {
		 count = Long.valueOf(0);
		 startTime = System.currentTimeMillis();
		 WriterThread th = new WriterThread();
		 quit = false;
		 logFile = "/BHUData/bulogs/ualog/police.business-reporting";
		 archiveFile = "/BHUData/bulogs/ualogs/";
		 openLogFile();
		 th.start();
	 }
/*	
	public WriterThread(String logFile, String archiveFile){
		this.quit = false;
		this.logFile = logFile;
		this.archiveFile = archiveFile;
		openLogFile();
	}

	public static WriterThread getFileWriter(String logFile, String archiveFile){
		WriterThread ret = new WriterThread(logFile, archiveFile);
		ret.start();
		return ret;
	}
*/
	public static void writeLog(String str){
		synchronized(count){
			if(fw != null){
				try{
					fw.write(str);
					fw.write("\r\n");
					count ++;
//					System.out.println("write finished," + this.count);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	private static void openLogFile(){
		try {
//			System.out.println("new fileWriter:" + this.logFile);
			fw = new FileWriter(logFile);
		} catch (Exception e) {
			e.printStackTrace();
			fw = null;
		} 
		startTime = System.currentTimeMillis();
		count = Long.valueOf(0);
	}

	private static void archiveLogFile(){
        try {  
            File afile = new File(logFile);
            String suffix =  getCurrentString();
            afile.renameTo(new File(archiveFile+suffix + "."+"business-reporting"));
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
	}
	
	public static String getCurrentString(){
		   Calendar c = Calendar.getInstance();
       	c.add(Calendar.MINUTE,0);//1分钟前
		return yyyymmddhhmmss.format(c.getTime());
    }
	
	public static String getCurrentTime(){
		   Calendar c = Calendar.getInstance();
    	c.add(Calendar.MINUTE,0);//1分钟前
		return datetimeFormat.format(c.getTime());
 }
	
	private static void flushLogFile(){
		try{
			fw.close();
			if(count > 0){
				archiveLogFile();
				//move to archive file
				startTime = System.currentTimeMillis();
				count = Long.valueOf(0);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void stopLog(){
		quit = true;
	}
	
    public void run(){
    	while(true){
			synchronized(WriterThread.count){
				if(WriterThread.quit){
					flushLogFile();
		    		return;
				}

				if(WriterThread.count.longValue() > 0){
					if((System.currentTimeMillis() - WriterThread.startTime > 1000*1)/* ||
							this.count.longValue() > 100*/){
						flushLogFile();
						openLogFile();
					}
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    }
    
    
	public static void main(String[] args) throws Exception {
//    	WriterThread logger = WriterThread.getFileWriter("/tmp/xx.log", "/tmp/xx.log.bak_");
//    	System.out.println("get logger succeed");
//    	for(int i=1; i < 300; i ++){
//    		WriterThread.writeLog("time " + i + "  " + System.currentTimeMillis());
//    		Thread.sleep(1000);
//    	};
//    	WriterThread.stopLog();
		System.out.println(getCurrentTime());;
    }
}
