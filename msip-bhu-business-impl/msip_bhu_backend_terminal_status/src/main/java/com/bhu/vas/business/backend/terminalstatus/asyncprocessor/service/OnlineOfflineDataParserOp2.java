package com.bhu.vas.business.backend.terminalstatus.asyncprocessor.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.bhu.vas.api.dto.charging.ActionBuilder;
import com.bhu.vas.api.dto.charging.ActionBuilder.ActionMode;
import com.bhu.vas.api.dto.charging.ActionBuilder.Hint;
import com.bhu.vas.api.dto.charging.DeviceNotExistAction;
import com.bhu.vas.api.dto.charging.DeviceOfflineAction;
import com.bhu.vas.api.dto.charging.DeviceOnlineAction;
import com.bhu.vas.api.dto.charging.HandsetSyncAction;
import com.bhu.vas.api.dto.handset.HandsetOfflineAction;
import com.bhu.vas.api.dto.handset.HandsetOnlineAction;
import com.bhu.vas.business.ds.agent.mdto.LineRecord;
import com.bhu.vas.business.ds.agent.mdto.LineRecords;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.FileHelper;
import com.smartwork.msip.cores.helper.JsonHelper;

/**
 * 每日必须进行状态保活，每天凌晨需要把在线的设备重新写入到日志中，作为模拟登录，模拟登录的时间缺省为当日的起始时间，防止漏算了长时间在线的设备
 * 对于终端则无需这样
 * @author Reid Zhang
 * @Time 2016-07-12 15:12:23
 *
 */
public class OnlineOfflineDataParserOp2 {
	

	public static void main(String[] argv) throws UnsupportedEncodingException, IOException{
		
		//ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		//WifiDeviceWholeDayMService wifiDeviceWholeDayMService = (WifiDeviceWholeDayMService)ctx.getBean("wifiDeviceWholeDayMService");
		//201607121035.business-reporting
		String date = "201607121035";
		OnlineOfflineDataParserOp2 op = new OnlineOfflineDataParserOp2();
		op.perdayDataGen(date);
	}
	
	private Date currentDate;
	/**
	 * 
	 * @param date yyyy-MM-dd
	 */
	public void perdayDataGen(String date){
		this.currentDate = DateTimeHelper.parseDate(date, DateTimeHelper.FormatPattern0);
		System.out.println(this.currentDate);
		//File[] files = FileHelper.getFilesFilterName("/BHUData/bulogs/reportinglogs/", date);//"2012-08-01""2012-07-31"
		File[] files = FileHelper.getFilesFilterName("E:/onORoff/", date);
		if(files != null && files.length != 0){
			Arrays.sort(files, new FileHelper.CompratorByLastModified());
			for(File file : files){
				System.out.println("log file :" + file.getName() + " start");
				ZipFile zf = null;
				try{
					if(file.getName().indexOf("business-reporting") == -1) continue;
					zf = new ZipFile(new File(file.getAbsolutePath()), ZipFile.OPEN_READ);
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
			}
		}
	}
	
	//设备 在线区间段 dmac -》LineRecords
	private Map<String,LineRecords> device_records = new HashMap<>();
	//设备 终端列表 dmac -> Set<hmac>
	//private Map<String,Set<String>> device_handset_records = new HashMap<>();
	//终端 在线区间段dmac -> <hmac -》LineRecords>
	private Map<String,Map<String,LineRecords>> device_handset_records = new HashMap<>();
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
	    	case DeviceNotExist:
	    		processDeviceNotExist(message);
	    		break;
	    	case HandsetOnline:
	    		processHandsetOnline(message);
	    		break;
	    	case HandsetOffline:
	    		processHandsetOffline(message);
	    		break;
	    	case HandsetSync:
	    		processHandsetSync(message);
	    		break;	
	    	default:
	    		//throw new UnsupportedOperationException(actType.getCname()+" message not yet implement handler process!");
	    }
	}
	
	private Map<String, LineRecords> handsetRecordGetOrCreate(String dmac){
		Map<String, LineRecords> recordmap = device_handset_records.get(dmac);
		if(recordmap == null){
			recordmap = new HashMap<String, LineRecords>();
			device_handset_records.put(dmac, recordmap);
		}
		return recordmap;
	}
	/**
	 * 当设备下线后，其当前连接的终端也需要一起下线
	 * 所有此设备所属的在线终端都进行下线操作
	 * @param dmac
	 * @param offline_ts
	 */
	private void processHandsetWhenDeviceOffline(String dmac,long offline_ts){
		
		doAllHandsetOfDeviceOffline(dmac,offline_ts,Hint.DeviceOffline);//"所属设备下线");
		/*Map<String, LineRecords> handset_records = handsetRecordGetOrCreate(dmac);
		if(!handset_records.isEmpty()){
		}*/
	}
	
	/**
	 * 设备掉线又上线后，会sync过来所有的在线终端列表
	 * 0、sync过来在线终端列表A
	 * 1、当前缓存中的在线终端列表B（records.getCurrent() ！= null）
	 * 2、B中的不在A中的终端进行下线操作，在A中的终端不操作
	 * 3、如果B为空或empty，则A中所有终端进行上线操作
	 * @param message
	 */
	private void processHandsetSync(String message){
		HandsetSyncAction dto = JsonHelper.getDTO(message, HandsetSyncAction.class);
		Map<String, LineRecords> handset_records = handsetRecordGetOrCreate(dto.getMac());
		if(handset_records.isEmpty()){
			for(String hmac:dto.getHmacs()){
				LineRecords records = new LineRecords();
				LineRecord record = new LineRecord();
				record.setUts(dto.getTs());
				records.setCurrent(record);
				handset_records.put(hmac, records);
			}
		}else{
			//缓存中的终端列表B
			Set<String> handsets_current = handset_records.keySet();
			Set<String> needDowns = new HashSet<>();
			for(String hmac:handsets_current){
				if(!dto.getHmacs().contains(hmac)){
					needDowns.add(hmac);
				}
			}
			//needDowns的所有终端下线，如果handset_records的终端已经处于下线，则跳过
			for(String hmac:needDowns){
				LineRecords records = handset_records.get(hmac);
				if(records == null){
					//不可能出现此情况
					//System.out.println("aooooo");;
				}else{
					if(records.wasWaittingUpStatus()){
						//System.out.println("booooo");
					}else{
						/*records.getCurrent().setDown_ts(dto.getTs());
						records.getCurrent().appendHint("不在sync列表中，强制下线");
						records.getRecords().add(records.getCurrent());
						records.setCurrent(null);*/
						records.forceCurrentDownOper(dto.getTs(),Hint.HandsetNotInSyncSoForceOffline);// "不在sync列表中，强制下线");
					}
				}
			}
			
			//dto.getHmacs()的所有终端上线，如果handset_records的终端已经处于下线，则进行上线操作
			for(String hmac:dto.getHmacs()){
				LineRecords records = handset_records.get(hmac);
				if(records == null){
					records = new LineRecords();
					LineRecord record = new LineRecord();
					record.setUts(dto.getTs());
					records.setCurrent(record);
					handset_records.put(hmac, records);
				}else{
					/*if(records.getCurrent() == null){
						System.out.println("gooooo");
						;//按理不应该出现此情况，如果出现，应该补齐上线操作
					}else{*/
					if(records.wasWaittingUpStatus()){
						LineRecord record = new LineRecord();
						record.setUts(dto.getTs());
						record.appendHint(Hint.HandsetInSyncSoForceOnline);//"在sync列表中，强制上线");
						records.setCurrent(record);
					}else{
						//System.out.println("gooooo");
					}
					//}
				}
			}
		}
	}
	
	
	private void processHandsetOnline(String message){
		HandsetOnlineAction dto = JsonHelper.getDTO(message, HandsetOnlineAction.class);
		//handsetComming(dto.getMac(),dto.getHmac());
		//获取远程磁盘文件上的数据，读入缓存，
		//从缓存中根据Mac 取对应的终端上线信息，
		//
		Map<String, LineRecords> handset_records = handsetRecordGetOrCreate(dto.getMac());
		LineRecords records = handset_records.get(dto.getHmac());
		if(records == null){
			records = new LineRecords();
			handset_records.put(dto.getHmac(), records);
		}
		if(!records.hasCurrent()){
			//records.setCurrent(new );
			LineRecord record = new LineRecord();
			record.setUts(dto.getTs());
			records.setCurrent(record);
		}else{
			if(records.currentHasUp()){//重复两次up,不进行累积
				records.getCurrent().setDts(dto.getTs());
				records.getCurrent().appendHint(Hint.ElementDownLose);//"缺失down，补齐");
				records.getRecords().add(records.getCurrent());
				
				LineRecord record = new LineRecord();
				record.setUts(dto.getTs());
				records.setCurrent(record);
			}else{
				System.out.println("此情况貌似不存在2");;//此情况貌似不存在
			}
		}
	}
	
	private void processHandsetOffline(String message){
		HandsetOfflineAction dto = JsonHelper.getDTO(message, HandsetOfflineAction.class);
		Map<String, LineRecords> handset_records = handsetRecordGetOrCreate(dto.getMac());
		LineRecords records = handset_records.get(dto.getHmac());
		if(records == null){
			records = new LineRecords();
			device_records.put(dto.getHmac(), records);
		}
		
		if(!records.hasCurrent()){
			if(records.getRecords().isEmpty()){//此mac当天的第一条为down，则开始时间为今天的零点
				LineRecord record = new LineRecord();
				record.setUts(DateTimeHelper.getDateStart(currentDate).getTime());
				record.setDts(dto.getTs());
				record.appendHint(Hint.ElementUpLoseSoPollishDaysBegin);//"缺失up，补齐到当天开始");
				records.getRecords().add(record);
			}else{
				//合并列表中最后一条数据的down 替换为当前down
				LineRecord previous = records.getRecords().get(records.getRecords().size()-1);
				long down_ts= previous.getDts();
				previous.setDts(dto.getTs());
				previous.appendHint(Hint.ElementUpLoseSoCombinCurrentLastElement);//"缺失up，合并上条数据中的down为当前记录down时间，上条记录down_ts:"+down_ts);
			}
			
		}else{
			if(records.currentHasUp()){
				records.getCurrent().setDts(dto.getTs());
				records.getRecords().add(records.getCurrent());
				//records.setCurrent(new LineRecord());
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
		LineRecords records = device_records.get(dto.getMac());
		if(records == null){
			records = new LineRecords();
			device_records.put(dto.getMac(), records);
		}
		if(!records.hasCurrent()){
			//records.setCurrent(new );
			LineRecord record = new LineRecord();
			record.setUts(dto.getTs());
			records.setCurrent(record);
		}else{
			if(records.currentHasUp()){//重复两次up,不进行累积
				;
				records.getCurrent().setDts(dto.getTs());
				records.getCurrent().appendHint(Hint.ElementDownLose);//"缺失down，补齐");
				records.getRecords().add(records.getCurrent());
				
				LineRecord record = new LineRecord();
				record.setUts(dto.getTs());
				records.setCurrent(record);
			}else{
				System.out.println("此情况貌似不存在2");//此情况貌似不存在
			}
			/*
			if(records.currentHasDown()){//先有down，再来up，此情况貌似不存在
				
			}*/
		}
	}
	
	private void processDeviceOffline(String message){
		DeviceOfflineAction dto = JsonHelper.getDTO(message, DeviceOfflineAction.class);
		LineRecords records = device_records.get(dto.getMac());
		if(records == null){
			records = new LineRecords();
			device_records.put(dto.getMac(), records);
		}
		
		if(!records.hasCurrent()){
			if(records.getRecords().isEmpty()){//此mac当天的第一条为down，则开始时间为今天的零点
				LineRecord record = new LineRecord();
				record.setUts(DateTimeHelper.getDateStart(currentDate).getTime());
				record.setDts(dto.getTs());
				record.appendHint(Hint.ElementUpLoseSoPollishDaysBegin);//"缺失up，补齐到当天开始");
				records.getRecords().add(record);
			}else{
				/*//去records中最后一条记录的down时间作为up时间
				LineRecord previous = records.getRecords().get(records.getRecords().size()-1);
				if(previous == null){
					System.out.println("~~~~~~~~~~~~");;
				}
				LineRecord record = new LineRecord();
				record.setUp_ts(previous.getDown_ts());
				record.setDown_ts(dto.getTs());
				record.setHint("缺失up，去上一条数据的down补齐到up");
				records.getRecords().add(record);*/
				
				//合并列表中最后一条数据的down 替换为当前down
				LineRecord previous = records.getRecords().get(records.getRecords().size()-1);
				long down_ts= previous.getDts();
				previous.setDts(dto.getTs());
				previous.appendHint(Hint.ElementUpLoseSoCombinCurrentLastElement);//"缺失up，合并上条数据中的down为当前记录down时间，上条记录down_ts:"+down_ts);
			}
			
		}else{
			if(records.currentHasUp()){
				records.getCurrent().setDts(dto.getTs());
				records.getRecords().add(records.getCurrent());
				//records.setCurrent(new LineRecord());
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
				
				LineRecord record = new LineRecord();
				record.setUp_ts(dto.getTs());
				records.setCurrent(record);
			}else{
				;//此情况貌似不存在
			}*/
			/*
			if(records.currentHasDown()){//先有down，再来up，此情况貌似不存在
				
			}*/
		}
		processHandsetWhenDeviceOffline(dto.getMac(),dto.getTs());
	}
	
	public void processDeviceNotExist(String message){
		DeviceNotExistAction dto = JsonHelper.getDTO(message, DeviceNotExistAction.class);
		LineRecords records = device_records.get(dto.getMac());
		if(records == null){
			return;
		}
		//如果设备处于在线状态则离线
		/*if(records.wasWaittingDownStatus()){
			records.getCurrent().setDown_ts(dto.getTs());
			records.getCurrent().appendHint("设备不存在，强制下线，补齐到不存在时间");
			records.getRecords().add(records.getCurrent());
			records.setCurrent(null);
		}*/
		if(records.wasWaittingDownStatus()){//补齐最后登出时间
			records.forceCurrentDownOper(dto.getTs(), Hint.ElementNotExist);// "设备不存在，强制下线，补齐到不存在时间");
		}
		
	}
	
	public void processEnd(Map<String, LineRecords> records){
		Iterator<Entry<String, LineRecords>> iter = records.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, LineRecords> next = iter.next();
			LineRecords  val = next.getValue();
			/*if(val.getCurrent() !=  null){//补齐最后登出时间
				val.getCurrent().setDown_ts(DateTimeHelper.getDateEnd(currentDate).getTime());
				val.getCurrent().appendHint("日志截尾，补齐到当天最后时间");
				val.getRecords().add(val.getCurrent());
				val.setCurrent(null);
			}*/
			if(val.wasWaittingDownStatus()){//补齐最后登出时间
				val.forceCurrentDownOper(DateTimeHelper.getDateEnd(currentDate).getTime(), Hint.LogAnalyseDone);//"日志截尾，补齐到当天最后时间");
			}
		}
		
		doAllHandsetOffline(DateTimeHelper.getDateEnd(currentDate).getTime(),Hint.LogAnalyseDone);//"日志截尾，补齐到当天最后时间");
		/*Iterator<Entry<String, Map<String, LineRecords>>> iter_first = device_handset_records.entrySet().iterator();
		
		while (iter_first.hasNext()) {
			Entry<String, Map<String,LineRecords>> next = iter_first.next();
			//String dmac = next.getKey();
			Map<String, LineRecords> value = next.getValue();
			Iterator<Entry<String, LineRecords>> iter_second = value.entrySet().iterator();
			while(iter_second.hasNext()){
				Entry<String, LineRecords> next2 = iter_second.next();
				//String hmac = next2.getKey();
				LineRecords val = next2.getValue();
				if(val.getCurrent() !=  null){//补齐最后登出时间
					val.getCurrent().setDown_ts(DateTimeHelper.getDateEnd(currentDate).getTime());
					val.getCurrent().appendHint("日志截尾，补齐到当天最后时间");
					val.getRecords().add(val.getCurrent());
					val.setCurrent(null);
				}
			}
			
		}*/
	}
	
	private void doAllHandsetOffline(long offline_ts,Hint hint){
		Iterator<Entry<String, Map<String, LineRecords>>> iter_first = device_handset_records.entrySet().iterator();
		
		while (iter_first.hasNext()) {
			Entry<String, Map<String,LineRecords>> next = iter_first.next();
			doAllHandsetOfDeviceOffline(next.getKey(),offline_ts,hint);
			/*Entry<String, Map<String,LineRecords>> next = iter_first.next();
			//String dmac = next.getKey();
			Map<String, LineRecords> value = next.getValue();
			Iterator<Entry<String, LineRecords>> iter_second = value.entrySet().iterator();
			while(iter_second.hasNext()){
				Entry<String, LineRecords> next2 = iter_second.next();
				//String hmac = next2.getKey();
				LineRecords val = next2.getValue();
				if(val.getCurrent() !=  null){//补齐最后登出时间
					//val.getCurrent().setDown_ts(DateTimeHelper.getDateEnd(currentDate).getTime());
					val.getCurrent().setDown_ts(offline_ts);
					val.getCurrent().appendHint(hint);//"日志截尾，补齐到当天最后时间");
					val.getRecords().add(val.getCurrent());
					val.setCurrent(null);
				}
			}*/
		}
	}
	
	private void doAllHandsetOfDeviceOffline(String dmac,long offline_ts,Hint hint){
		Map<String, LineRecords> records = device_handset_records.get(dmac);
		if(records==null || records.isEmpty()) return;
		Iterator<Entry<String, LineRecords>> iter_second = records.entrySet().iterator();
		while(iter_second.hasNext()){
			Entry<String, LineRecords> next2 = iter_second.next();
			//String hmac = next2.getKey();
			LineRecords val = next2.getValue();
			if(val.wasWaittingDownStatus()){//补齐最后登出时间
				val.forceCurrentDownOper(offline_ts, hint);
				/*val.getCurrent().setDown_ts(offline_ts);
				val.getCurrent().appendHint(hint);//"日志截尾，补齐到当天最后时间");
				val.getRecords().add(val.getCurrent());
				val.setCurrent(null);*/
			}
		}
	}
	
	public Map<String, LineRecords> getDevice_records() {
		return device_records;
	}


	public Map<String, Map<String, LineRecords>> getDevice_handset_records() {
		return device_handset_records;
	}

	public void setDevice_handset_records(
			Map<String, Map<String, LineRecords>> device_handset_records) {
		this.device_handset_records = device_handset_records;
	}

//	public static void main(String[] argv) throws UnsupportedEncodingException, IOException{
//		
//		//ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
//		//WifiDeviceWholeDayMService wifiDeviceWholeDayMService = (WifiDeviceWholeDayMService)ctx.getBean("wifiDeviceWholeDayMService");
//
//		String date = "2016-07-12";
//		OnlineOfflineDataParserOp op = new OnlineOfflineDataParserOp();
//		op.perdayDataGen(date);
//		//op.processEnd(op.getDevice_records());
////		StringBuilder sb = new StringBuilder();
////		Iterator<Entry<String, LineRecords>> iter = op.getDevice_records().entrySet().iterator();
////		while (iter.hasNext()) {
////			Entry<String, LineRecords> next = iter.next();
////			String key = next.getKey();
////			LineRecords  val = next.getValue();
////			sb.append("mac:"+key).append(String.format("[%s]", MacDictParserFilterHelper.prefixMactch(key,false,false))).append('\n');
////			int times = 0;
////			long total_online_duration = 0l;
////			for(LineRecord record:val.getRecords()){
////				sb.append("		"+record).append('\n');
////				times++;
////				total_online_duration += record.gaps();
////			}
////			int handsets = 0;
////			Map<String, LineRecords> map = op.getDevice_handset_records().get(key);
////			if(map != null){
////				handsets = map.size();
////			}
////			sb.append(String.format("      统计 次数[%s] 时长[%s] 终端数[%s]", times,DateTimeHelper.getTimeDiff(total_online_duration),handsets)).append('\n');
////			
////			WifiDeviceWholeDayMDTO dto = new WifiDeviceWholeDayMDTO();
////			dto.setId(WifiDeviceWholeDayMDTO.generateId(date, key));
////			dto.setMac(key);
////			dto.setDate(date);
////			dto.setDct(times);//.setConnecttimes(times);
////			dto.setDod(total_online_duration);//.setOnlineduration(total_online_duration);
////			dto.setHandsets(handsets);
////			dto.setRecords(val.getRecords());
////			//wifiDeviceWholeDayMService.save(dto);
////			System.out.println(dto.getId());
////		}
////		
////		sb.append("~~~~~~~~~~~~~~~~~~~~~~~gap line~~~~~~~~~~~~~~~~~~\n");
////		Iterator<Entry<String, Map<String, LineRecords>>> iter_first = op.getDevice_handset_records().entrySet().iterator();
////		
////		while (iter_first.hasNext()) {
////			Entry<String, Map<String,LineRecords>> next = iter_first.next();
////			String dmac = next.getKey();
////			sb.append("dmac:"+dmac).append(String.format("[%s]", MacDictParserFilterHelper.prefixMactch(dmac,false,false))).append('\n');
////			Map<String, LineRecords> value = next.getValue();
////			Iterator<Entry<String, LineRecords>> iter_second = value.entrySet().iterator();
////			while(iter_second.hasNext()){
////				Entry<String, LineRecords> next2 = iter_second.next();
////				String hmac = next2.getKey();
////				sb.append("      hmac:"+hmac).append(String.format("[%s]", MacDictParserFilterHelper.prefixMactch(hmac,false,false))).append('\n');
////				LineRecords val = next2.getValue();
////				int times = 0;
////				long total_online_time = 0l;
////				for(LineRecord record:val.getRecords()){
////					sb.append("		      "+record).append('\n');
////					times++;
////					total_online_time += record.gaps();
////				}
////				sb.append(String.format("		      统计 次数[%s] 时长[%s]", times,DateTimeHelper.getTimeDiff(total_online_time))).append('\n');
////			}
////			
////			/*LineRecords  val = value;
////			if(val.getCurrent() !=  null){//补齐最后登出时间
////				val.getCurrent().setDown_ts(DateTimeHelper.getDateEnd(currentDate).getTime());
////				val.getCurrent().setHint("日志截尾，补齐到当天最后时间");
////				val.getRecords().add(val.getCurrent());
////				val.setCurrent(null);
////			}*/
////		}
////		
////		//FileHelper.generateFile("/BHUData/data/abcd.txt", new ByteArrayInputStream(sb.toString().getBytes("UTF-8")));
////		FileHelper.generateFile("E:/data/abcd.txt", new ByteArrayInputStream(sb.toString().getBytes("UTF-8")));
//	}
}
