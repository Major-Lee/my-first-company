package com.bhu.vas.di.op.migrate;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingRadioDTO;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSetting;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.device.service.WifiDeviceSettingService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

/**
 * 更新设备配置中的频宽， 从40M修改为20M
 * @author Yetao
 *
 */
public class UserDeviceBandWidthFrom40To20Op {
	public static void main(String[] argv){
		String[] CONFIG = {"/com/bhu/vas/di/business/dataimport/dataImportCtx.xml"};
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(CONFIG, UserDeviceBandWidthFrom40To20Op.class);
		ctx.start();
		
		WifiDeviceService wifiDeviceService = (WifiDeviceService)ctx.getBean("wifiDeviceService");
		WifiDeviceSettingService wifiDeviceSettingService = (WifiDeviceSettingService)ctx.getBean("wifiDeviceSettingService");
		IDaemonRpcService daemonRpcService =  (IDaemonRpcService)ctx.getBean("daemonRpcService");
		
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andSimpleCaulse(" 1=1 ");//.andColumnEqualTo("singer", 1);//.andColumnBetween("updated_at", d2, d1);
		mc.setOrderByClause("id desc");
		mc.setPageNumber(1);
		mc.setPageSize(200);
    	EntityIterator<String, WifiDeviceSetting> it_udsets = 
    			new KeyBasedEntityBatchIterator<String, WifiDeviceSetting>(String.class, WifiDeviceSetting.class, 
    					wifiDeviceSettingService.getEntityDao(), mc);
		while(it_udsets.hasNext()){
			List<WifiDeviceSetting> list = it_udsets.next();
			for(WifiDeviceSetting uds:list){
				WifiDeviceSettingDTO userdevice_setting = uds.getInnerModel();
				if(userdevice_setting == null)
					continue;
				List<WifiDeviceSettingRadioDTO> radios = userdevice_setting.getRadios();
				if(radios == null || radios.size() <= 0)
					continue;
				WifiDeviceSettingRadioDTO radio = radios.get(0); //只修改2.4G射频
				if(radio == null || !StringUtils.contains(radio.getChannel_bandwidth(), "40M"))
					continue;
				radio.setChannel_bandwidth("20MHz");
				userdevice_setting.setRadios(radios);
				uds.setExtension_content(JsonHelper.getJSONString(userdevice_setting));
				wifiDeviceSettingService.update(uds);
				
				WifiDevice wifiDevice = wifiDeviceService.getById(uds.getId());
				if(wifiDevice == null || !wifiDevice.isOnline()){
					System.out.println(String.format("mac[%s] is offline", uds.getId()));
					continue;
				}
				
				String cmd = "<dev><sys><config><ITEM sequence=\"-1\" /></config></sys><wifi><radio><ITEM name=\"wifi0\" channel_bandwidth=\"20MHz\" /></radio></wifi></dev>";
				System.out.println(String.format("sending cmd for mac[%s]", uds.getId()));
				daemonRpcService.wifiDeviceCmdDown(null, uds.getId(), cmd);
			}
		}
		ctx.stop();
		ctx.close();
		System.exit(1);
	}
}
