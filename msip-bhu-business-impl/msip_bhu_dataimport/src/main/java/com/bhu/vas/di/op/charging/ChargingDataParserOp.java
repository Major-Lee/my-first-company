package com.bhu.vas.di.op.charging;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
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
			if(records.currentHasUp()){//重复两次up
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
				record.setUp_ts(this.currentDate.getTime());
				record.setDown_ts(dto.getTs());
				record.setHint("缺失up，补齐到当天开始");
				records.getRecords().add(record);
			}else{//去records中最后一条记录的down时间作为up时间
				DeviceLineRecord previous = records.getRecords().get(records.getRecords().size()-1);
				if(previous == null){
					System.out.println("~~~~~~~~~~~~");;
				}
				DeviceLineRecord record = new DeviceLineRecord();
				record.setUp_ts(previous.getDown_ts());
				record.setDown_ts(dto.getTs());
				record.setHint("缺失up，去上一条数据的down补齐到up");
				records.getRecords().add(record);
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
	
	
	public Map<String, DeviceLineRecords> getDevice_records() {
		return device_records;
	}


	public void setDevice_records(Map<String, DeviceLineRecords> device_records) {
		this.device_records = device_records;
	}


	public static void main(String[] argv){
		ChargingDataParserOp op = new ChargingDataParserOp();
		op.perdayDataGen("2015-09-01");
		
		
		Iterator<Entry<String, DeviceLineRecords>> iter = op.getDevice_records().entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, DeviceLineRecords> next = iter.next();
			String key = next.getKey();
			DeviceLineRecords  val = next.getValue();
			System.out.println("mac:"+key);
			for(DeviceLineRecord record:val.getRecords()){
				System.out.println("		"+record);
			}
		}
	}
}
