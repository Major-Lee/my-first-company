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
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.bhu.vas.api.dto.charging.ActionBuilder;
import com.bhu.vas.api.dto.charging.ActionBuilder.ActionMode;
import com.bhu.vas.api.dto.charging.DeviceOfflineAction;
import com.bhu.vas.api.dto.charging.DeviceOnlineAction;
import com.bhu.vas.api.dto.charging.HandsetOfflineAction;
import com.bhu.vas.api.dto.charging.HandsetOnlineAction;
import com.bhu.vas.api.dto.charging.HandsetSyncAction;
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
	
	//设备 在线区间段 dmac -》DeviceLineRecords
	private Map<String,DeviceLineRecords> device_records = new HashMap<>();
	//设备 终端列表 dmac -> Set<hmac>
	//private Map<String,Set<String>> device_handset_records = new HashMap<>();
	//终端 在线区间段dmac -> <hmac -》DeviceLineRecords>
	private Map<String,Map<String,DeviceLineRecords>> device_handset_records = new HashMap<>();
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
	    		processHandsetOnline(message);
	    		break;
	    	case HandsetOffline:
	    		processHandsetOffline(message);
	    		break;
	    	case HandsetSync:
	    		break;	
	    	default:
	    		//throw new UnsupportedOperationException(actType.getCname()+" message not yet implement handler process!");
	    }
	}
	
	private Map<String, DeviceLineRecords> handsetRecordGetOrCreate(String dmac){
		Map<String, DeviceLineRecords> recordmap = device_handset_records.get(dmac);
		if(recordmap == null){
			recordmap = new HashMap<String, DeviceLineRecords>();
			device_handset_records.put(dmac, recordmap);
		}
		return recordmap;
	}
	/**
	 * 当设备下线后，其当前连接的终端也需要一起下线
	 * @param dmac
	 * @param offline_ts
	 */
	private void processHandsetWhenDeviceOffline(String dmac,String offline_ts){
		
	}
	
	/**
	 * 设备掉线又上线后，会sync过来所有的在线终端列表
	 * 0、在线终端列表A
	 * 1、当前缓存中的在线终端列表B（records.getCurrent() ！= null）
	 * 2、B中的不在A中的终端进行下线操作，在A中的终端不操作
	 * 3、如果B为空或empty，则A进行上线操作
	 * @param message
	 */
	private void processHandsetSync(String message){
		HandsetSyncAction dto = JsonHelper.getDTO(message, HandsetSyncAction.class);
	}
	
	
	private void processHandsetOnline(String message){
		HandsetOnlineAction dto = JsonHelper.getDTO(message, HandsetOnlineAction.class);
		//handsetComming(dto.getMac(),dto.getHmac());
		Map<String, DeviceLineRecords> handset_records = handsetRecordGetOrCreate(dto.getMac());
		DeviceLineRecords records = handset_records.get(dto.getHmac());
		if(records == null){
			records = new DeviceLineRecords();
			handset_records.put(dto.getHmac(), records);
		}
		if(!records.hasCurrent()){
			//records.setCurrent(new );
			DeviceLineRecord record = new DeviceLineRecord();
			record.setUp_ts(dto.getTs());
			records.setCurrent(record);
		}else{
			if(records.currentHasUp()){//重复两次up,不进行累积
				records.getCurrent().setDown_ts(dto.getTs());
				records.getCurrent().setHint("缺失down，补齐");
				records.getRecords().add(records.getCurrent());
				
				DeviceLineRecord record = new DeviceLineRecord();
				record.setUp_ts(dto.getTs());
				records.setCurrent(record);
			}else{
				System.out.println("此情况貌似不存在2");;//此情况貌似不存在
			}
		}
	}
	
	private void processHandsetOffline(String message){
		HandsetOfflineAction dto = JsonHelper.getDTO(message, HandsetOfflineAction.class);
		Map<String, DeviceLineRecords> handset_records = handsetRecordGetOrCreate(dto.getMac());
		DeviceLineRecords records = handset_records.get(dto.getHmac());
		if(records == null){
			records = new DeviceLineRecords();
			device_records.put(dto.getHmac(), records);
		}
		
		if(!records.hasCurrent()){
			if(records.getRecords().isEmpty()){//此mac当天的第一条为down，则开始时间为今天的零点
				DeviceLineRecord record = new DeviceLineRecord();
				record.setUp_ts(DateTimeHelper.getDateStart(currentDate).getTime());
				record.setDown_ts(dto.getTs());
				record.setHint("缺失up，补齐到当天开始");
				records.getRecords().add(record);
			}else{
				//合并列表中最后一条数据的down 替换为当前down
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
				
				//合并列表中最后一条数据的down 替换为当前down
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
		
		
		Iterator<Entry<String, Map<String, DeviceLineRecords>>> iter_first = device_handset_records.entrySet().iterator();
		
		while (iter_first.hasNext()) {
			Entry<String, Map<String,DeviceLineRecords>> next = iter_first.next();
			//String dmac = next.getKey();
			Map<String, DeviceLineRecords> value = next.getValue();
			Iterator<Entry<String, DeviceLineRecords>> iter_second = value.entrySet().iterator();
			while(iter_second.hasNext()){
				Entry<String, DeviceLineRecords> next2 = iter_second.next();
				//String hmac = next2.getKey();
				DeviceLineRecords val = next2.getValue();
				if(val.getCurrent() !=  null){//补齐最后登出时间
					val.getCurrent().setDown_ts(DateTimeHelper.getDateEnd(currentDate).getTime());
					val.getCurrent().setHint("日志截尾，补齐到当天最后时间");
					val.getRecords().add(val.getCurrent());
					val.setCurrent(null);
				}
			}
			
			/*DeviceLineRecords  val = value;
			if(val.getCurrent() !=  null){//补齐最后登出时间
				val.getCurrent().setDown_ts(DateTimeHelper.getDateEnd(currentDate).getTime());
				val.getCurrent().setHint("日志截尾，补齐到当天最后时间");
				val.getRecords().add(val.getCurrent());
				val.setCurrent(null);
			}*/
		}
	}
	
	public Map<String, DeviceLineRecords> getDevice_records() {
		return device_records;
	}


	public Map<String, Map<String, DeviceLineRecords>> getDevice_handset_records() {
		return device_handset_records;
	}

	public void setDevice_handset_records(
			Map<String, Map<String, DeviceLineRecords>> device_handset_records) {
		this.device_handset_records = device_handset_records;
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
		
		sb.append("~~~~~~~~~~~~~~~~~~~~~~~gap line~~~~~~~~~~~~~~~~~~\n");
		Iterator<Entry<String, Map<String, DeviceLineRecords>>> iter_first = op.getDevice_handset_records().entrySet().iterator();
		
		while (iter_first.hasNext()) {
			Entry<String, Map<String,DeviceLineRecords>> next = iter_first.next();
			String dmac = next.getKey();
			sb.append("dmac:"+dmac).append('\n');
			Map<String, DeviceLineRecords> value = next.getValue();
			Iterator<Entry<String, DeviceLineRecords>> iter_second = value.entrySet().iterator();
			while(iter_second.hasNext()){
				Entry<String, DeviceLineRecords> next2 = iter_second.next();
				String hmac = next2.getKey();
				sb.append("      hmac:"+hmac).append('\n');
				DeviceLineRecords val = next2.getValue();
				for(DeviceLineRecord record:val.getRecords()){
					sb.append("		      "+record).append('\n');
				}
			}
			
			/*DeviceLineRecords  val = value;
			if(val.getCurrent() !=  null){//补齐最后登出时间
				val.getCurrent().setDown_ts(DateTimeHelper.getDateEnd(currentDate).getTime());
				val.getCurrent().setHint("日志截尾，补齐到当天最后时间");
				val.getRecords().add(val.getCurrent());
				val.setCurrent(null);
			}*/
		}
		
		FileHelper.generateFile("/BHUData/data/abcd.txt", new ByteArrayInputStream(sb.toString().getBytes("UTF-8")));
	}
}
