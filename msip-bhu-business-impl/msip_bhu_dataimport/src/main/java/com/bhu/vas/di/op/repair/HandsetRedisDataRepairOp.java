package com.bhu.vas.di.op.repair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.api.rpc.devices.model.HandsetDevice;
import com.bhu.vas.business.bucache.redis.serviceimpl.handset.HandsetStorageFacadeService;
import com.bhu.vas.business.ds.builder.BusinessModelBuilder;
import com.bhu.vas.business.ds.device.service.HandsetDeviceService;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

/**
 * 数据迁移
 * WifiDevicePresentService ——》WifiDevicePresentCtxService
 * @author Edmond
 *
 */
public class HandsetRedisDataRepairOp {
	
	public static void main(String[] argv) {//throws ElasticsearchException, ESException, IOException, ParseException{
		//if(argv.length <1) return;
		//String oper = argv[0];// ADD REMOVE
		
		ApplicationContext actx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		
		/*HandsetDeviceService handsetDeviceService = (HandsetDeviceService)actx.getBean("handsetDeviceService");
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andSimpleCaulse(" 1=1 ");//.andColumnIsNotNull("lat").andColumnIsNotNull("lon");//.andColumnEqualTo("online", 1);
    	mc.setPageNumber(1);
    	mc.setPageSize(400);
    	long total = 0;
    	long count = 0;
    	long online = 0;
		EntityIterator<String, HandsetDevice> it = new KeyBasedEntityBatchIterator<String,HandsetDevice>(String.class
				,HandsetDevice.class, handsetDeviceService.getEntityDao(), mc);
		while(it.hasNext()){
			List<HandsetDevice> entitys = it.next();
			total+= entitys.size();
			List<HandsetDeviceDTO> dtos = new ArrayList<HandsetDeviceDTO>();
			for(HandsetDevice hd:entitys){
				HandsetDeviceDTO handset = BusinessModelBuilder.fromHandsetDevice(hd);
				if(handset.wasOnline()) online++;
				dtos.add(handset);
			}
			if(!dtos.isEmpty()){
				HandsetStorageFacadeService.handsetsComming(dtos);
				count+=dtos.size();
			}
			System.out.println("数据迁移中 count:"+count +" total:"+total+" online:"+online);
		}
		System.out.println("数据迁移成功。。。 count:"+count +" total:"+total+" online:"+online);
		System.out.println("开始统计redis记录条数");
		HandsetStorageFacadeService.statisticsSet(online, total);
		long all = HandsetStorageFacadeService.countAll();
		System.out.println("开始统计redis记录条数 all:"+all);
		*/
		
		
		final AtomicLong total = new AtomicLong(0);
		final AtomicLong online = new AtomicLong(0);
		HandsetStorageFacadeService.iteratorAll(new IteratorNotify<Map<String,String>>(){
			@Override
			public void notifyComming(Map<String, String> t) {
				Iterator<Entry<String, String>> iter = t.entrySet().iterator();
				while(iter.hasNext()){
					Entry<String, String> next = iter.next();
					String value = next.getValue();//value
					if(StringUtils.isNotEmpty(value)){
						if(!value.contains(HandsetDeviceDTO.Action_Offline)){
							online.incrementAndGet();
						}
						total.incrementAndGet();
					}
				}
			}
		});
		HandsetStorageFacadeService.statisticsSet(online.get(),total.get());
		
		System.out.println("数据 total:"+total.get()+" online:"+online.get());
	}
}
