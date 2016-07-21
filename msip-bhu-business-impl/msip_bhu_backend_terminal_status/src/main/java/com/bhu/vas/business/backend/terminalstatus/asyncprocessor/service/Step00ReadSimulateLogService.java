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

import com.alisoft.xplatform.asf.cache.ICacheManager;
import com.alisoft.xplatform.asf.cache.IMemcachedCache;
import com.alisoft.xplatform.asf.cache.memcached.CacheUtil;
import com.alisoft.xplatform.asf.cache.memcached.MemcachedCacheManager;
import com.bhu.vas.api.dto.charging.ActionBuilder;
import com.bhu.vas.api.dto.charging.ActionBuilder.ActionMode;
import com.bhu.vas.api.dto.commdity.internal.useragent.OrderUserAgentDTO;
import com.bhu.vas.api.dto.handset.HandsetOfflineAction;
import com.bhu.vas.api.dto.handset.HandsetOnlineAction;
import com.bhu.vas.business.backend.terminalstatus.logger.TerminalStatusNotifyLogger;
import com.bhu.vas.business.bucache.redis.serviceimpl.commdity.UserOrderDetailsHashService;
import com.smartwork.msip.cores.helper.JsonHelper;
@Service
public class Step00ReadSimulateLogService {
	
//	@Resource
//	private BusinessCacheService businessCacheService;
	
	@Resource
	private PortraitMemcachedCacheService portraitMemcachedCacheService;
	   
	private static String hashFileMatchTemplate = "%s.business-reporting.zip";
	@SuppressWarnings("unchecked")
	public void parser(String date,String logpath){
		String wildcard = String.format(hashFileMatchTemplate,date);
		Collection<File> listFiles = FileUtils.listFiles(new File(logpath),
				new WildcardFileFilter(wildcard), 
		        new IOFileFilter() {
                    public boolean accept(File file, String s) {
                        return true;
                    }    
                    public boolean accept(File file) {
                        return true;
                    }
                });
		
		for(File file : listFiles){
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
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public void processBackendActionMessage(String messagejsonHasPrefix) throws Exception{
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
	    		System.out.println("default");
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
		if(newAddFields != null){
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
		portraitMemcachedCacheService.storePortraitCacheResult(hdMac, message);
		
	}
	
	private void processHandsetOffline(String message){
		HandsetOfflineAction dto = JsonHelper.getDTO(message, HandsetOfflineAction.class);
		String handsetOnline = portraitMemcachedCacheService.getPortraitOrderCacheByOrderId(dto.getHmac());
		if(handsetOnline != null || handsetOnline != ""){
			HandsetOnlineAction onlineDto = JsonHelper.getDTO(handsetOnline, HandsetOnlineAction.class);
			dto.setTs(onlineDto.getTs());
			dto.setHip(onlineDto.getHip());
			dto.setHname(onlineDto.getHname());
		}
		
		dto.setEnd_ts(dto.getTs());
		String mac = dto.getMac();
		String hdMac = dto.getHmac();
		String newAddFields = UserOrderDetailsHashService.getInstance().fetchUserOrderDetail(mac, hdMac);
		if(newAddFields != null){
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
	
	public static void main(String[] args) {
//		String mac = "84:82:f4:19:01:0c";
//		String hdMac = "68:3e:34:48:b7:35";
//		String newAddFields = UserOrderDetailsHashService.getInstance().fetchUserOrderDetail(mac, hdMac);
//		if(newAddFields.isEmpty()){
//			System.out.println(newAddFields);
//		}
//		OrderUserAgentDTO addMsg = JsonHelper.getDTO(newAddFields, OrderUserAgentDTO.class);
//		System.out.println(JsonHelper.getJSONString(addMsg));
//		System.out.println(addMsg.getWan_ip());
//		System.out.println(addMsg.getIp());
		
		ICacheManager<IMemcachedCache> manager;  
        manager = CacheUtil.getCacheManager(IMemcachedCache.class, MemcachedCacheManager.class.getName());  
        manager.setConfigFile("memcached.xml");  
        manager.start();  
        try {  
            IMemcachedCache cache = manager.getCache("default.memcached");  
            cache.put("key", "value");  
            System.out.println(cache.get("key"));  
        } finally {  
            manager.stop();  
        }  
	}
}
