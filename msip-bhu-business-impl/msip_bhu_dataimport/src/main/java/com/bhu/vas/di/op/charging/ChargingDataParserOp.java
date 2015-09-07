package com.bhu.vas.di.op.charging;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.bhu.vas.api.dto.charging.ActionBuilder;
import com.bhu.vas.api.dto.charging.ActionBuilder.ActionMode;
import com.bhu.vas.api.dto.charging.DeviceOfflineAction;
import com.bhu.vas.api.dto.charging.DeviceOnlineAction;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.FileHelper;
import com.smartwork.msip.cores.helper.JsonHelper;

/**
 * 每日必须进行状态保活，每天凌晨需要把在线的设备重新写入到日志中，作为模拟登录，模拟登录的时间缺省为当日的起始时间，防止漏算了长时间在线的设备
 * 对于终端则无需这样
 * @author Edmond
 *
 */
public class ChargingDataParserOp {
	private Date currentDate;
	/**
	 * 
	 * @param date yyyy-MM-dd
	 */
	public void perdayDataGen(String date){
		this.currentDate = DateTimeHelper.parseDate(date, DateTimeHelper.shortDateFormat);
		File[] files = FileHelper.getFilesFilterName("/Users/Edmond/Msip.Test/bulogs/charginglogs/", date);//"2012-08-01""2012-07-31"
		if(files != null && files.length != 0){
			Arrays.sort(files, new FileHelper.CompratorByLastModified());
			for(File file : files){
				System.out.println("log file :" + file.getName() + " start");
				try{
					if(file.getName().indexOf("business-charging") == -1) continue;
					
					ZipFile zf = new ZipFile(new File(file.getAbsolutePath()), ZipFile.OPEN_READ);
					// 返回 ZIP file entries的枚举.
					Enumeration<? extends ZipEntry> entries = zf.entries();
	
					while (entries.hasMoreElements()) {
						ZipEntry ze = entries.nextElement();
						long size = ze.getSize();
						if (size > 0) {
							System.out.println("Length is " + size);
							BufferedReader br = new BufferedReader(
									new InputStreamReader(zf.getInputStream(ze)));
							String line = null;
							while ((line = br.readLine()) != null) {
								try{
									String[] split = line.split(" - ");
									if(split.length == 2)
									this.processBackendActionMessage(split[1]);
								}catch(Exception ex) {
									ex.printStackTrace();
									continue;
								}
							}
							br.close();
						}
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}
	}
	
	private Map<String,DeviceLineRecords> device_records = new HashMap<>();
	private Map<String,Set<String>> device_handset_records = new HashMap<>();
	public void processBackendActionMessage(String messagejsonHasPrefix) throws Exception{
		//System.out.println(messagejsonHasPrefix);
		ActionMode actionType = ActionBuilder.determineActionType(messagejsonHasPrefix);
	    if(actionType == null)
	    	throw new UnsupportedOperationException(messagejsonHasPrefix+" message can not be parsed!");
		String message = ActionBuilder.determineActionMessage(messagejsonHasPrefix);
		//ActionBuilder.fromJson(messagejson, classz);
	    switch(actionType){
	    	case DeviceOnline:
	    		processDeviceOnline(message);
	    		break;
	    	case DeviceOffline:
	    		processDeviceOffline(message);
	    		break;
	    	case HandsetOnline:
	    		break;
	    	case HandsetOffline:
	    		break;
	    	case HandsetSync:
	    		break;	
	    	default:
	    		//throw new UnsupportedOperationException(actType.getCname()+" message not yet implement handler process!");
	    }
	}
	
	
	private void processDeviceOnline(String message){
		DeviceOnlineAction dto = JsonHelper.getDTO(message, DeviceOnlineAction.class);
		DeviceLineRecords records = device_records.get(dto.getMac());
		if(records == null){
			records = new DeviceLineRecords();
			device_records.put(dto.getMac(), records);
		}
		
		if(!records.hasCurrent()){
			//records.setCurrent(new );
			DeviceLineRecord record = new DeviceLineRecord();
			record.setUp_ts(dto.getTs());
			records.setCurrent(record);
		}else{
			if(records.currentHasUp()){//重复两次up,不进行累积
				;
				records.getCurrent().setDown_ts(dto.getTs());
				records.getCurrent().setHint("缺失down，补齐");
				records.getRecords().add(records.getCurrent());
				
				DeviceLineRecord record = new DeviceLineRecord();
				record.setUp_ts(dto.getTs());
				records.setCurrent(record);
			}else{
				System.out.println("此情况貌似不存在2");;//此情况貌似不存在
			}
			/*
			if(records.currentHasDown()){//先有down，再来up，此情况貌似不存在
				
			}*/
		}
	}
	
	
	private void processDeviceOffline(String message){
		DeviceOfflineAction dto = JsonHelper.getDTO(message, DeviceOfflineAction.class);
		DeviceLineRecords records = device_records.get(dto.getMac());
		if(records == null){
			records = new DeviceLineRecords();
			device_records.put(dto.getMac(), records);
		}
		
		if(!records.hasCurrent()){
			if(records.getRecords().isEmpty()){//此mac当天的第一条为down，则开始时间为今天的零点
				DeviceLineRecord record = new DeviceLineRecord();
				record.setUp_ts(DateTimeHelper.getDateStart(currentDate).getTime());
				record.setDown_ts(dto.getTs());
				record.setHint("缺失up，补齐到当天开始");
				records.getRecords().add(record);
			}else{
				/*//去records中最后一条记录的down时间作为up时间
				DeviceLineRecord previous = records.getRecords().get(records.getRecords().size()-1);
				if(previous == null){
					System.out.println("~~~~~~~~~~~~");;
				}
				DeviceLineRecord record = new DeviceLineRecord();
				record.setUp_ts(previous.getDown_ts());
				record.setDown_ts(dto.getTs());
				record.setHint("缺失up，去上一条数据的down补齐到up");
				records.getRecords().add(record);*/
				
				//合并最后一条数据的down 替换为当前down
				DeviceLineRecord previous = records.getRecords().get(records.getRecords().size()-1);
				long down_ts= previous.getDown_ts();
				previous.setDown_ts(dto.getTs());
				previous.setHint("缺失up，合并上条数据中的down为当前记录down时间，上条记录down_ts:"+down_ts);
			}
			
		}else{
			if(records.currentHasUp()){
				records.getCurrent().setDown_ts(dto.getTs());
				records.getRecords().add(records.getCurrent());
				//records.setCurrent(new DeviceLineRecord());
				records.setCurrent(null);
			}else{
				System.out.println("此情况貌似不存在3");
			}
			if(records.currentHasDown()){//此情况貌似不存在
				System.out.println("此情况貌似不存在4");
			}
			/*if(records.currentHasUp()){//重复两次up
				records.getCurrent().setDown_ts(dto.getTs());
				records.getCurrent().setHint("缺失down，补齐");
				records.getRecords().add(records.getCurrent());
				
				DeviceLineRecord record = new DeviceLineRecord();
				record.setUp_ts(dto.getTs());
				records.setCurrent(record);
			}else{
				;//此情况貌似不存在
			}*/
			/*
			if(records.currentHasDown()){//先有down，再来up，此情况貌似不存在
				
			}*/
		}
	}
	
	public void processEnd(Map<String, DeviceLineRecords> records){
		Iterator<Entry<String, DeviceLineRecords>> iter = records.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, DeviceLineRecords> next = iter.next();
			DeviceLineRecords  val = next.getValue();
			if(val.getCurrent() !=  null){//补齐最后登出时间
				val.getCurrent().setDown_ts(DateTimeHelper.getDateEnd(currentDate).getTime());
				val.getCurrent().setHint("日志截尾，补齐到当天最后时间");
				val.getRecords().add(val.getCurrent());
				val.setCurrent(null);
			}
		}
	}
	
	public Map<String, DeviceLineRecords> getDevice_records() {
		return device_records;
	}


	public void setDevice_records(Map<String, DeviceLineRecords> device_records) {
		this.device_records = device_records;
	}


	public static void main(String[] argv) throws UnsupportedEncodingException, IOException{
		ChargingDataParserOp op = new ChargingDataParserOp();
		op.perdayDataGen("2015-09-01");
		op.processEnd(op.getDevice_records());
		StringBuilder sb = new StringBuilder();
		Iterator<Entry<String, DeviceLineRecords>> iter = op.getDevice_records().entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, DeviceLineRecords> next = iter.next();
			String key = next.getKey();
			DeviceLineRecords  val = next.getValue();
			sb.append("mac:"+key).append('\n');
			for(DeviceLineRecord record:val.getRecords()){
				sb.append("		"+record).append('\n');
			}
		}
		FileHelper.generateFile("/BHUData/data/abcd.txt", new ByteArrayInputStream(sb.toString().getBytes("UTF-8")));
		/*
		File file = new File(RuntimeConfiguration.PathFile_Blur_BadgeNormalDict);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		File file1 = new File(RuntimeConfiguration.PathFile_Blur_BadgeSpecialDict);
		if (!file1.exists()) {
			file1.getParentFile().mkdirs();
			try {
				file1.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
		/*FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		BufferedWriter  bw = null;
		
		FileOutputStream fos1 = null;
		OutputStreamWriter osw1 = null;
		BufferedWriter  bw1 = null;
		try{
			fos=new FileOutputStream(new File(RuntimeConfiguration.PathFile_Blur_BadgeNormalDict));
			osw=new OutputStreamWriter(fos, "UTF-8");
			bw=new BufferedWriter(osw);
			
			fos1=new FileOutputStream(new File(RuntimeConfiguration.PathFile_Blur_BadgeSpecialDict));
			osw1=new OutputStreamWriter(fos1, "UTF-8");
			bw1=new BufferedWriter(osw1);
			for (Map.Entry<String, String> entry : badgeSynonyms.entrySet()) {
				String key = entry.getKey();
				//if("-".equals(key)) continue;
				//if(StringHelper.containsPlusOrMinus(key)){
				if(StringHelper.containsSpecialBusinessSplitChar(key)){
					bw1.write(StringHelper.replaceBlankAndLowercase(key).concat(StringHelper.EQUAL_STRING_GAP).concat(entry.getValue())+"\n");
				}
				bw.write(StringHelper.replaceBlankAndLowercase(key).concat(StringHelper.EQUAL_STRING_GAP).concat(StringHelper.replaceEnterAndOtherLineChar(entry.getValue()))+"\n");
				//bw.write(artist+"\n");
				//System.out.println(meuser.getKey()+"::"+meuser.getValue());
			}
			//yearsBetween
			String datetemplete = "%s=%s$+%s$+-1$+%s$+V1,"+CmbtExtensionField.TagsPatternField+CmbtTagPatternDefine.Pattern_Date;
			for(int year = yearsBetween[0];year<=yearsBetween[1];year++){
				String yearstr =String.valueOf(year);
				bw.write(String.format(datetemplete,yearstr,yearstr,yearstr,CmbtCategory.TimeYear.getName())+"\n");
			}
			for(int month = monthsBetween[0];month<=monthsBetween[1];month++){
				String monthstr =String.format("%02d", month);
				String fullstr = monthstr+"月";
				bw.write(String.format(datetemplete,fullstr,fullstr,monthstr,CmbtCategory.TimeMonth.getName())+"\n");
				String monthstr1 = month+"月";
				bw.write(String.format(datetemplete,monthstr1,monthstr1,monthstr,CmbtCategory.TimeMonth.getName())+"\n");
			}
			for(int day = daysBetween[0];day<=daysBetween[1];day++){
				String daystr =String.format("%02d", day);
				String fullstr = daystr+"日";
				bw.write(String.format(datetemplete,fullstr,fullstr,daystr,CmbtCategory.TimeMonth.getName())+"\n");
				String daystr1 = day+"日";
				bw.write(String.format(datetemplete,daystr1,daystr1,daystr,CmbtCategory.TimeDay.getName())+"\n");
			}
		    bw.close();
		    osw.close();
		    fos.close();
		    
		}catch(IOException ex){
			ex.printStackTrace();
		}finally{
			try {
				if(bw != null)
					bw.close();
				if(osw != null)
					osw.close();
				if(fos != null)
					fos.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.init();
		}*/
	}
}
