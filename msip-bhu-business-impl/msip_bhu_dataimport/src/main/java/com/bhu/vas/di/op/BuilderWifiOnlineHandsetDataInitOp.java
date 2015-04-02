package com.bhu.vas.di.op;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.elasticsearch.ElasticsearchException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.api.dto.search.WifiDeviceIndexDTO;
import com.bhu.vas.api.helper.IndexDTOBuilder;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.StatisticsFragmentMaxOnlineHandsetService;
import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.search.service.device.WifiDeviceIndexService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.es.exception.ESException;
import com.smartwork.msip.localunit.RandomData;
/**
 * 全量创建wifiDevice的索引数据
 * @author lawliet
 *
 */
public class BuilderWifiOnlineHandsetDataInitOp {
	
	public static void main(String[] argv) throws ElasticsearchException, ESException, IOException, ParseException{
		ExecutorService exec = Executors.newFixedThreadPool(10);
		Date current = DateTimeHelper.getDateDaysAgo(500);
		//int count = RandomData.intNumber(300,500);
		Calendar c = Calendar.getInstance();
		c.setTime(current);
		for(int j=0;j<500;j++){//模拟后100天的数据
			c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH)+1);
			for(int i=0;i<24;i++){
				c.set(Calendar.HOUR_OF_DAY, i);
				final Date dd = c.getTime();
				exec.submit((new Runnable() {
					@Override
					public void run() {
						StatisticsFragmentMaxOnlineHandsetService.getInstance().fragmentAllSet(dd, RandomData.intNumber(200,40000));
						System.out.println(DateTimeHelper.formatDate(dd, DateTimeHelper.FormatPattern9));
					}
				}));
			}
		}		
		System.out.println("exec正在shutdown");
		exec.shutdown();
		System.out.println("exec正在shutdown成功");
		while(true){
			System.out.println("正在判断exec是否执行完毕");
			if(exec.isTerminated()){
				System.out.println("exec是否执行完毕,终止exec...");
				exec.shutdownNow();
				System.out.println("exec是否执行完毕,终止exec成功");
				break;
			}else{
				System.out.println("exec未执行完毕...");
				try {
					Thread.sleep(2*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		System.exit(1);
	}
}
