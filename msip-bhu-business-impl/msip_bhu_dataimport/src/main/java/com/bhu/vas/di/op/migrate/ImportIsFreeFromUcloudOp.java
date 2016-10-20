package com.bhu.vas.di.op.migrate;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.common.lang3.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.OperationDS;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.DeviceStatusExchangeDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.SharedNetworkSettingDTO;
import com.bhu.vas.api.rpc.devices.model.UserDevicesSharedNetworks;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSharedNetwork;
import com.bhu.vas.business.ds.device.service.UserDevicesSharedNetworksService;
import com.bhu.vas.business.ds.device.service.WifiDeviceSharedNetworkService;
import com.bhu.vas.business.ds.user.facade.UserWifiDeviceFacadeService;
import com.smartwork.msip.cores.helper.JsonHelper;

/**
 * 从ucloud中导入是否允许免费上网到用户的portal模板以及设备上
 * @author Yetao
 *
 */
public class ImportIsFreeFromUcloudOp {
	public static void main(String[] argv){
		
		if(argv == null || argv.length < 1){
			System.out.println("参数不足，参数：数据文件路径");
			System.out.println("Param not enough, param：data file path");
			return;
		}

		long t0 = System.currentTimeMillis();
		String[] CONFIG = {"/com/bhu/vas/di/business/dataimport/dataImportCtx.xml"};
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(CONFIG, ImportIsFreeFromUcloudOp.class);
		ctx.start();
		UserDevicesSharedNetworksService userDevicesSharedNetworksService = (UserDevicesSharedNetworksService)ctx.getBean("userDevicesSharedNetworksService");
		UserWifiDeviceFacadeService userWifiDeviceFacadeService = (UserWifiDeviceFacadeService)ctx.getBean("userWifiDeviceFacadeService");
		WifiDeviceSharedNetworkService wifiDeviceSharedNetworkService = (WifiDeviceSharedNetworkService)ctx.getBean("wifiDeviceSharedNetworkService");

		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null; // 用于包装InputStreamReader,提高处理性能。因为BufferedReader有缓冲的，而InputStreamReader没有。
		
		try {
			String str = null;
			fis = new FileInputStream(argv[0]);
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			while ((str = br.readLine()) != null) {
				String[] fields = str.split(",");
				if(fields == null || fields.length <= 0)
					continue;
				boolean empty = false;
				for(String s:fields){
					if(StringUtils.isEmpty(s)){
						empty = true;
						break;
					}
				}
				if(empty)
					continue;
				
				int uid = Integer.parseInt(fields[0]);
				String mode = fields[1];
				String tpl = fields[2];
				int isfree = Integer.parseInt(fields[3]);
				int firstlogin = Integer.parseInt(fields[4]);
				if(!StringUtils.isEmpty(mode) && mode.equals("dashang"))
					mode = "SafeSecure";
				else
					mode = "SmsSecure";
				
				UserDevicesSharedNetworks configs = userDevicesSharedNetworksService.getById(uid);
				List<ParamSharedNetworkDTO> models = configs.get(mode,new ArrayList<ParamSharedNetworkDTO>(),true);
				if(models.isEmpty()){
					continue;
				}else{
					ParamSharedNetworkDTO temp = new ParamSharedNetworkDTO();
					temp.setTemplate(tpl);
					int index = models.indexOf(temp);
					if(index != -1){
						ParamSharedNetworkDTO dto =  models.get(index);
						dto.setIsfree(isfree);
						dto.setFirstLogin(firstlogin);
						models.set(index, dto);
						userDevicesSharedNetworksService.update(configs);
					}
				}
				
				List<String> wifiIds = userWifiDeviceFacadeService.findUserWifiDeviceIdsByUid(uid);
				if(wifiIds != null && wifiIds.size() > 0){
					List<WifiDeviceSharedNetwork> snks = wifiDeviceSharedNetworkService.findByIds(wifiIds);
					if(snks != null && !snks.isEmpty()){
						for(WifiDeviceSharedNetwork snk:snks){
							if(mode.equals(snk.getSharednetwork_type()) && tpl.equals(snk.getTemplate())){
								SharedNetworkSettingDTO snkDTO = snk.getInnerModel();
								ParamSharedNetworkDTO psn = snkDTO.getPsn();
								psn.setIsfree(isfree);
								psn.setFirstLogin(firstlogin);
								snk.putInnerModel(snkDTO);
							}
						}
						wifiDeviceSharedNetworkService.updateAll(snks);
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace(System.out);
		} finally {
			try {
				br.close();
				isr.close();
				fis.close();
				// 关闭的时候最好按照先后顺序关闭最后开的先关闭所以先关s,再关n,最后关m
			} catch (Exception e) {
				e.printStackTrace(System.out);
			}
		}		
		
		ctx.stop();
		ctx.close();
		System.exit(1);
	}
}
