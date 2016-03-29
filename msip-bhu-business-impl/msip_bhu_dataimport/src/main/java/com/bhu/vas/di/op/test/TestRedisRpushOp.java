package com.bhu.vas.di.op.test;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import com.bhu.vas.api.dto.commdity.internal.portal.RequestDeliverNotifyDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.commdity.CommdityInternalNotifyListService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.localunit.RandomData;
/**
 * 测试redis rpush
 * @author lawliet
 *
 */
public class TestRedisRpushOp {
	
	public static void main(String[] argv) throws IOException, ParseException{
		
		try{
			if(argv.length != 1){
				System.out.println("argv params is valid");
				System.exit(1);
			}
			int count = Integer.parseInt(argv[0]);
			long t0 = System.currentTimeMillis();
	    	String umac_prefix = "80:bc:1a:2f:7e:";
	    	String orderid_prefix = "10012016010100000000";
	    	//System.out.println(umac_prefix.concat(String.format("%02d", RandomData.intNumber(99))));
	    	String mac = "84:82:f4:09:54:80";
	    	long order_sequence = 1;
	    	
	    	for(int i = 1;i<count+1;i++){
				String umac = umac_prefix.concat(String.format("%02d", RandomData.intNumber(99)));
				String orderid = orderid_prefix.concat(String.format("%012d", order_sequence));
	    		RequestDeliverNotifyDTO requestDeliverNotifyDto = new RequestDeliverNotifyDTO();
	    		requestDeliverNotifyDto.setOrderid(orderid);
	    		requestDeliverNotifyDto.setAmount("0.1");
	    		requestDeliverNotifyDto.setApp_deliver_detail("14400");
	    		requestDeliverNotifyDto.setBu_mobileno("18673117874");
	    		requestDeliverNotifyDto.setMac(mac);
	    		requestDeliverNotifyDto.setUmac(umac);
	    		requestDeliverNotifyDto.setPaymented_ds(DateTimeHelper.getDateTime());
	    		requestDeliverNotifyDto.setCommdityid(1);
	    		requestDeliverNotifyDto.setContext("aaa");
	    		String notify_message = JsonHelper.getJSONString(requestDeliverNotifyDto);
	    		
				List<Object> notify_ret = CommdityInternalNotifyListService.getInstance().rpushOrderDeliverNotifyTransaction(notify_message);
				//判断通知发货成功
				if(notify_ret != null && notify_ret.size() == 3){
					System.out.println(String.format("OrderDeliverNotify success deliver notify: message[%s] i[%s] slen[%s] rpush_ret[%s] elen[%s]", 
							notify_message, i, notify_ret.get(0), notify_ret.get(1), notify_ret.get(2)));
				}
				order_sequence++;
	    		Thread.sleep(10l);
	    	}
			System.out.println("数据全量导入，总耗时"+((System.currentTimeMillis()-t0)/1000)+"s");

		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}finally{

		}
		System.exit(1);
	}
	
}
