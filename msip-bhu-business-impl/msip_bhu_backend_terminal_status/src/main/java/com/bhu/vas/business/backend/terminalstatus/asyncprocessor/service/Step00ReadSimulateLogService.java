package com.bhu.vas.business.backend.terminalstatus.asyncprocessor.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.charging.ActionBuilder;
import com.bhu.vas.api.dto.charging.ActionBuilder.ActionMode;
import com.bhu.vas.api.dto.commdity.internal.useragent.OrderUserAgentDTO;
import com.bhu.vas.api.dto.handset.HandsetOfflineAction;
import com.bhu.vas.api.dto.handset.HandsetOnlineAction;
import com.bhu.vas.business.backend.terminalstatus.logger.TerminalStatusNotifyLogger;
import com.bhu.vas.business.bucache.redis.serviceimpl.commdity.UserOrderDetailsHashService;
import com.bhu.vas.business.portrait.ds.hportrait.service.BusinessCacheService;
import com.smartwork.msip.cores.helper.JsonHelper;
@Service
public class Step00ReadSimulateLogService {
	
	@Resource
	private BusinessCacheService businessCacheService;
	
	private static String hashFileMatchTemplate = "%s.business-reporting.zip";
	@SuppressWarnings("unchecked")
	public void parser(String date,String logpath){
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
				if(file.getName().indexOf("business-reporting") == -1) continue;
				zf = new ZipFile(new File(file.getAbsolutePath()), ZipFile.OPEN_READ);
				// 返回 ZIP file entries的枚举.
				Enumeration<? extends ZipEntry> entries = zf.entries();

				while (entries.hasMoreElements()) {
					ZipEntry ze = entries.nextElement();
					long size = ze.getSize();
					if (size > 0) {
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
			System.out.println(index);
		}
	}
	
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
	private void processHandsetOnline(String message){
		HandsetOnlineAction dto = JsonHelper.getDTO(message, HandsetOnlineAction.class);
		//获取远程磁盘文件上的数据，读入缓存，
		//从缓存中根据Mac 取对应的终端上线信息，
		//
		String mac = dto.getMac();
		String hdMac = dto.getHmac();
		String newAddFields = UserOrderDetailsHashService.getInstance().fetchUserOrderDetail(mac, hdMac);
		if(!newAddFields.isEmpty()){
			OrderUserAgentDTO addMsg = JsonHelper.getDTO(newAddFields, OrderUserAgentDTO.class);
			dto.setWan(addMsg.getWan_ip());
			dto.setInternet(addMsg.getIp());
			int vipType = addMsg.getType();
			switch (vipType) {
			case 0:
				dto.setViptype("WX");
				break;
			case 10:
				dto.setViptype("DX");
				dto.setVipacc(addMsg.getUmac_mobileno());
				break;

			default:
				break;
			}
		}
		
		message =  JsonHelper.getJSONString(dto);
		TerminalStatusNotifyLogger.doTerminalStatusMessageLog(ActionMode.HandsetOnline.getPrefix()+message);
		businessCacheService.storePortraitCacheResult(hdMac, message);
		
	}
	
	private void processHandsetOffline(String message){
		HandsetOfflineAction dto = JsonHelper.getDTO(message, HandsetOfflineAction.class);
		System.out.println("mac :"+dto.getMac());
		long endTs = dto.getTs();
		String handsetOnline = businessCacheService.getPortraitOrderCacheByOrderId(dto.getHmac());
		HandsetOnlineAction onlineDto = JsonHelper.getDTO(handsetOnline, HandsetOnlineAction.class);
		dto.setEndTs(endTs);
		dto.setTs(onlineDto.getTs());
		dto.setHip(onlineDto.getHip());
		dto.setHname(onlineDto.getHname());
		
		String mac = dto.getMac();
		String hdMac = dto.getHmac();
		String newAddFields = UserOrderDetailsHashService.getInstance().fetchUserOrderDetail(mac, hdMac);
		if(!newAddFields.isEmpty()){
			OrderUserAgentDTO addMsg = JsonHelper.getDTO(newAddFields, OrderUserAgentDTO.class);
			dto.setWan(addMsg.getWan_ip());
			dto.setInternet(addMsg.getIp());
			int vipType = addMsg.getType();
			switch (vipType) {
			case 0:
				dto.setViptype("WX");
				break;
			case 10:
				dto.setViptype("DX");
				dto.setVipAcc(addMsg.getUmac_mobileno());
				break;

			default:
				break;
			}
		}
		
		message =  JsonHelper.getJSONString(dto);
		TerminalStatusNotifyLogger.doTerminalStatusMessageLog(ActionMode.HandsetOffline.getPrefix()+message);
	}
	
	
	private void processDeviceOnline(String message){
		TerminalStatusNotifyLogger.doTerminalStatusMessageLog(ActionMode.DeviceOnline.getPrefix()+message);
	}
	
	private void processDeviceOffline(String message){
		TerminalStatusNotifyLogger.doTerminalStatusMessageLog(ActionMode.DeviceOffline.getPrefix()+message);
	}
	
	public void processDeviceNotExist(String message){
		TerminalStatusNotifyLogger.doTerminalStatusMessageLog(ActionMode.DeviceNotExist.getPrefix()+message);
	}
	private void processHandsetSync(String message){
		//this.currentDate = DateTimeHelper.parseDate(, );
		TerminalStatusNotifyLogger.doTerminalStatusMessageLog(ActionMode.HandsetSync.getPrefix()+message);
	}
}