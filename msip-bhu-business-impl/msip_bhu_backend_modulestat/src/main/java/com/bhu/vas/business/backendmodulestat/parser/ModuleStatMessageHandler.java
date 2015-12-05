package com.bhu.vas.business.backendmodulestat.parser;

import java.util.*;
import java.util.Map.Entry;

import com.bhu.vas.api.dto.modulestat.WifiDeviceModuleStatDTO;
import com.bhu.vas.api.dto.modulestat.WifiDeviceModuleStatItemDTO;
import com.bhu.vas.api.helper.OperationDS;
import com.bhu.vas.api.rpc.task.model.VasModuleCmdDefined;
import com.bhu.vas.api.rpc.task.model.pk.VasModuleCmdPK;
import com.bhu.vas.api.vto.modulestat.*;
import com.bhu.vas.business.bucache.redis.serviceimpl.modulestat.WifiDeviceModuleStatService;
import com.smartwork.msip.cores.helper.DateHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.smartwork.async.messagequeue.kafka.parser.iface.IMessageHandler;

/**
 * 增值服务统计
 *
 *
 * redis:
 * {key: stlye + daydate field:type.sequence value: count}
 * {key: style002.20151212 field: 4.1 value:1}
 *
 * {key: stlye + monthdate field:type.sequence value: count}
 * {key: style002.201512 field: 4.1 value:1}
 *
 *
 * @author bluesand
 *
 */
public class ModuleStatMessageHandler implements IMessageHandler<byte[]>{
	private final Logger logger = LoggerFactory.getLogger(ModuleStatMessageHandler.class);


	@Override
	public void handler(String topic, Map<Integer, List<byte[]>> value) {
		//logger.info("WSMessageHandler Thread " + Thread.currentThread().getName());
		//System.out.println("	topic:"+topic);
		
		Iterator<Entry<Integer, List<byte[]>>> iter = value.entrySet().iterator();
		while(iter.hasNext()){
			Entry<Integer, List<byte[]>> element = iter.next();

			List<byte[]> data = element.getValue();
			for(Object d:data){
				String message = new String((byte[])d);
				if(StringUtils.isEmpty(message)) continue;
				logger.info(String.format("WSMessageHandler [%s]", message));
				System.out.println(String.format("WSMessageHandler [%s]", message));

				builderMessage(message);

			}
		}
		
	}


	/**
	 *
	 * @param type
	 * @param sequence
	 * @return
	 */
	private String generateModuleKey(int type, int sequence) {
		return type + "." + sequence;
	}

	private String generateStyleKey(String ver) {
		return ver.substring(0,8);
	}

	private String generateMStyleKey(String ver, long time) {
		return generateStyleKey(ver) + "." + DateTimeHelper.formatDate(new Date(time * 1000), "yyyyMM");
	}

	private String generateDStyleKey(String ver, long time) {
		return generateStyleKey(ver) + "." + DateTimeHelper.formatDate(new Date(time * 1000), "yyyyMMdd");
	}

	public static void main(String[] args) {

		String message = "{\n" +
						"    \"dev\": \"84:82:f4:23:06:68\",\n" +
						"    \"item\": [{\n" +
						"        \"mac\": \"a4:5e:60:bb:86:7d\",\n" +
						"        \"type\": 3,\n" +
						"        \"sequence\": 2,\n" +
						"        \"systime\": 1448862498,\n" +
						"        \"ver\": \"style000-00.00.01\"\n" +
						"    }, {\n" +
						"        \"mac\": \"a4:5e:60:bb:86:7d\",\n" +
						"        \"type\": 4,\n" +
						"        \"sequence\": 2,\n" +
						"        \"systime\": 1448862498,\n" +
						"        \"ver\": \"style001-00.00.01\"\n" +
						"    }, {\n" +
						"        \"mac\": \"a4:5e:60:bb:86:7d\",\n" +
						"        \"type\": 4,\n" +
						"        \"sequence\": 1,\n" +
						"        \"systime\": 1449862499,\n" +
						"        \"ver\": \"style002-00.00.01\"\n" +
						"    }, {\n" +
						"        \"mac\": \"a4:5e:60:bb:86:7d\",\n" +
						"        \"type\": 3,\n" +
						"        \"sequence\": 1,\n" +
						"        \"systime\": 1448862499,\n" +
						"        \"ver\": \"style000-00.00.01\"\n" +
						"    }, {\n" +
						"        \"mac\": \"a4:5e:60:bb:86:7d\",\n" +
						"        \"type\": 4,\n" +
						"        \"sequence\": 1,\n" +
						"        \"systime\": 1448862503,\n" +
						"        \"ver\": \"style000-00.00.01\"\n" +
						"    }, {\n" +
						"        \"mac\": \"a4:5e:60:bb:86:7d\",\n" +
						"        \"type\": 4,\n" +
						"        \"sequence\": 1,\n" +
						"        \"systime\": 1448862505,\n" +
						"        \"ver\": \"style000-00.00.01\"\n" +
						"    }]\n" +
						"}";

		new ModuleStatMessageHandler().builderMessage(message);
	}


	/**
	 *
	 * dev: 设备mac
	 * mac: 终端mac
	 * type: 增值类型 1: 404, 2:重定向, 3: 品牌展示, 4:渠道号
	 * sequence: 子类型编号
	 * ver: 模板类型版本
	 *
	 *
	 {
	 "dev": "84:82:f4:23:06:68",
	 	"item": [{
	 		"mac": "a4:5e:60:bb:86:7d",
	 		"type": 2,
	 		"sequence": 1,
	 		"systime": 1448859775,
	 		"ver": "style000-00.00.01"
	 	}]
	 }
	 *
	 * @param message
	 */
	private void builderMessage(String message) {
		WifiDeviceModuleStatDTO dto = JsonHelper.getDTO(message, WifiDeviceModuleStatDTO.class);

		Map<String, Map<String, Long>> dmaps = new HashMap<String,Map<String,Long>>();
		Map<String, Map<String, Long>> mmaps = new HashMap<String,Map<String,Long>>();

		if (dto != null) {
			List<WifiDeviceModuleStatItemDTO> dtos = dto.getItems();

			int cursor = 0;
			if (dtos != null && !dtos.isEmpty()) {
				Map<String,Long> dModule = null;
				Map<String,Long> mStyle = null;


				for (WifiDeviceModuleStatItemDTO item : dtos) {
					int type = item.getType();
					int sequence = item.getSequence();
					long time = item.getSystime();


					String ver = item.getVer();
					System.out.println(String.format("[%s][%s][%s]", type, sequence, ver));
					String dStyleKey = generateDStyleKey(ver, time);
					String modlueKey = generateModuleKey(type, sequence);


					System.out.println(String.format("[%s][%s]", dStyleKey, modlueKey));

					String mStyleKey = generateMStyleKey(ver, time);

					dModule = dmaps.get(dStyleKey);

					if (dModule != null) {
//						System.out.println("1111");
						if (dModule.containsKey(modlueKey)) {
							dModule.put(modlueKey, dModule.get(modlueKey) + 1);
						} else {
							dModule.put(modlueKey, 1l);
							dmaps.put(dStyleKey, dModule);
							cursor ++;
						}

					} else {

//						System.out.println("2222");
						dModule = new HashMap<String,Long>();
						dModule.put(modlueKey, 1l);
						dmaps.put(dStyleKey, dModule);
						cursor ++;

					}

					System.out.println("ret===" + dmaps);
					System.out.println();

					mStyle = mmaps.get(mStyleKey);

					if (mStyle != null) {
						if (mStyle.containsKey(modlueKey)) {
							mStyle.put(modlueKey, mStyle.get(modlueKey) + 1);
						} else {
							mStyle.put(modlueKey, 1l);
							mmaps.put(mStyleKey, mStyle);

						}

					} else {
						mStyle = new HashMap<String,Long>();
						mStyle.put(modlueKey, 1l);
						mmaps.put(mStyleKey, mStyle);
					}

				}
			}

			System.out.println(dmaps);
//		System.out.println(dmaps.size());
//		System.out.println(dmaps.keySet().size());
			//WifiDeviceModuleStatService.getInstance().addDayModuleStats(dmaps);
			System.out.println(mmaps);

			System.out.println(cursor);

			System.out.println(WifiDeviceModuleStatService.getInstance().addDayModuleStats(dmaps, cursor));
			System.out.println(WifiDeviceModuleStatService.getInstance().addDayModuleStats(mmaps, cursor));



			System.out.println(WifiDeviceModuleStatService.getInstance().hgetModuleStatsWithKey("style000.201511"));



			String  field = "MSMP.3.1=5";

			int index = field.indexOf(".");
			int lastindex = field.lastIndexOf(".");

			System.out.println(field.substring(index+1, lastindex));
			vtos("style000");


		}

	}


	private void vtos(String style) {
		VasModuleCmdPK pk = new VasModuleCmdPK();
		pk.setStyle(style);
		pk.setDref(OperationDS.DS_Http_VapModuleCMD_Start.getRef());

		ModuleDefinedItemVTO vto = new ModuleDefinedItemVTO();

		vto.setStyle(style);
		vto.setDef(OperationDS.DS_Http_VapModuleCMD_Start.getRef());


		List<ItemBrandVTO> brands = new ArrayList<ItemBrandVTO>();

		List<ItemChannelVTO> channels = new ArrayList<ItemChannelVTO>();

		List<ItemRedirectVTO> redirects = new ArrayList<ItemRedirectVTO>();

		List<ItemHttp404VTO> http404s = new ArrayList<ItemHttp404VTO>();

		//1: 404, 2:重定向, 3: 品牌展示, 4:渠道号

//		Map<String,Long> dayRets = WifiDeviceModuleStatService.getInstance().hgetModuleStatsWithKey(generateDStyleKey(style));
//		Map<String,Long> monthRets = WifiDeviceModuleStatService.getInstance().hgetModuleStatsWithKey(generateMStyleKey(style));
		Map<String,Long> dayRets = WifiDeviceModuleStatService.getInstance().hgetModuleStatsWithKey("style002.20151212");
		Map<String,Long> monthRets = WifiDeviceModuleStatService.getInstance().hgetModuleStatsWithKey("style002.201512");

		for (String key: dayRets.keySet()) {

			Long dcount = dayRets.get(key);
			Long mcount = monthRets.get(key);
			int index = key.indexOf(".");
			int lastIndex = key.lastIndexOf(".");

			int type = Integer.parseInt(key.substring(index + 1, lastIndex));
			int sequence = Integer.parseInt(key.substring(lastIndex + 1));

			if (type ==1) {
				ItemHttp404VTO item = new ItemHttp404VTO();
				item.setSequence(sequence);
				item.setDcount(dcount);
				item.setMcount(mcount);
				http404s.add(item);

			} else if (type == 2) {
				ItemRedirectVTO item = new ItemRedirectVTO();
				item.setSequence(sequence);
				item.setDcount(dcount);
				item.setMcount(mcount);
				redirects.add(item);

			} else if (type == 3) {
				ItemBrandVTO item = new ItemBrandVTO();
				item.setSequence(sequence);
				item.setDcount(dcount);
				item.setMcount(mcount);
				brands.add(item);

			} else if (type == 4) {
				ItemChannelVTO item = new ItemChannelVTO();
				item.setSequence(sequence);
				item.setDcount(dcount);
				item.setMcount(mcount);
				channels.add(item);
			}

		}



		vto.setHttp404s(http404s);
		vto.setRedirects(redirects);
		vto.setBrands(brands);
		vto.setChannels(channels);

		System.out.println(vto);
	}
}
