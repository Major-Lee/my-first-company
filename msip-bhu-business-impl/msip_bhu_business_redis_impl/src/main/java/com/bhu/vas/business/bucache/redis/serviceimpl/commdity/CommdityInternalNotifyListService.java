package com.bhu.vas.business.bucache.redis.serviceimpl.commdity;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.api.dto.commdity.internal.pay.RequestWithdrawNotifyDTO;
import com.bhu.vas.api.dto.commdity.internal.pay.ResponsePaymentCompletedNotifyDTO;
import com.bhu.vas.api.dto.commdity.internal.portal.RequestDeliverNotifyDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationListCache;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.localunit.RandomData;

/**
 * 用于存放支付系统支付成功的通知消息
 * @author tangzichao
 *
 */
public class CommdityInternalNotifyListService extends AbstractRelationListCache {
    private static class ServiceHolder{
        private static CommdityInternalNotifyListService instance =new CommdityInternalNotifyListService();
    }
    /**
     * 获取工厂单例
     * @return
     */
    public static CommdityInternalNotifyListService getInstance() {
        return ServiceHolder.instance;
    }

    private CommdityInternalNotifyListService(){
    	
    }

    private String generateOrderPaymentNotifyKey() {
    	return BusinessKeyDefine.Commdity.OrderPaymentNotifyKey;
    }
    
    private String generateOrderDeliverNotifyKey() {
    	return BusinessKeyDefine.Commdity.OrderDeliverNotifyKey;
    }

    private String generateWithdrawAppliesRequestNotifyKey() {
    	return BusinessKeyDefine.Commdity.WithdrawAppliesRequestNotifyKey;
    }
    
    public String lpopOrderPaymentNotify(){
    	return super.lpop(generateOrderPaymentNotifyKey());
    }
    
    public String blpopOrderPaymentNotify(){
    	//return super.blpop(generateOrderPaymentNotifyKey());
    	return null;
    }
    
    public String lpopWithdrawAppliesRequestNotify(){
    	return super.lpop(generateWithdrawAppliesRequestNotifyKey());
    }
    
    public void rpushOrderPaymentNotify(String notify_message){
    	super.rpush(generateOrderPaymentNotifyKey(), notify_message);
    }
    
    public Long rpushOrderDeliverNotify(String notify_message){
    	return super.rpush(generateOrderDeliverNotifyKey(), notify_message);
    }
    
    public void rpushWithdrawAppliesRequestNotify(String notify_message){
    	super.rpush(generateWithdrawAppliesRequestNotifyKey(), notify_message);
    }
    
    @Override
    public String getRedisKey() {
        return null;
    }

    @Override
    public String getName() {
        return CommdityInternalNotifyListService.class.getName();
    }
    @Override
    public JedisPool getRedisPool() {
        return RedisPoolManager.getInstance().getPool(RedisKeyEnum.COMMDITY);
    }


    public static void main(String[] args) {
        //System.out.println(OrderPaymentNotifyListService.getInstance().pipelineHSet_sameKeyWithDiffFieldValue("TTTT", new String[]{"TT1,TT2"}, new String[]{"1", "2"}));

        //WifiDeviceModuleStatService.getInstance().hset("TTTT", "T", "1234");
    	//CommdityInternalNotifyListService.getInstance().rpushOrderPaymentNofity(OrderPaymentNotifyDTO);
    	
    	//String orderid = "10012016030900000000000000000011";
    	//boolean success = true;
    	//simulateResponsePaymentCompletedNotify(orderid, success);
    	//simulateDeliverNotify();
//    	while(true){
//    		simulateUpayDrawPaymentCompletedNotify(true);
//    	}
    	//simulateDeliverNotify();
    	simulateMultiDeliverNotify();
    }
    
    /*************           test           **************/
    
    public static void simulateResponsePaymentCompletedNotify(String orderid, boolean success){
        ResponsePaymentCompletedNotifyDTO opn_dto = new ResponsePaymentCompletedNotifyDTO();
    	opn_dto.setSuccess(success);
    	opn_dto.setOrderid(orderid);
    	//opn_dto.setPaymented_ts(System.currentTimeMillis());
    	opn_dto.setPaymented_ds(DateTimeHelper.formatDate(DateTimeHelper.DefalutFormatPattern));
    	if(!success){
    		opn_dto.setErrorcode("001");
    		opn_dto.setMsg("simulate error msg");
    	}
    	CommdityInternalNotifyListService.getInstance().rpushOrderPaymentNotify(JsonHelper.getJSONString(opn_dto));
    }

    public static void simulateUpayDrawPaymentCompletedNotify(boolean success){
    	String lpop_draw_message = CommdityInternalNotifyListService.getInstance().lpopWithdrawAppliesRequestNotify();
    	System.out.println("lpopWithdrawAppliesRequestNotify : " + lpop_draw_message);
    	if(StringUtils.isNotEmpty(lpop_draw_message)){
    		RequestWithdrawNotifyDTO dto = JsonHelper.getDTO(lpop_draw_message, RequestWithdrawNotifyDTO.class);
    		simulateResponsePaymentCompletedNotify(dto.getOrderid(), success);
    	}
    }
    
    public static void simulateDeliverNotify(){
		RequestDeliverNotifyDTO requestDeliverNotifyDto = new RequestDeliverNotifyDTO();
		requestDeliverNotifyDto.setOrderid("10012016031800000000000000000030");
		requestDeliverNotifyDto.setAmount("0.1");
		requestDeliverNotifyDto.setApp_deliver_detail("14400");
		requestDeliverNotifyDto.setBu_mobileno("18673117874");
		requestDeliverNotifyDto.setMac("84:82:f4:09:54:27");
		requestDeliverNotifyDto.setUmac("38:bc:1a:2f:7e:2a");
		requestDeliverNotifyDto.setPaymented_ds(DateTimeHelper.getDateTime());
		requestDeliverNotifyDto.setCommdityid(1);
		requestDeliverNotifyDto.setContext("aaa");
		String notify_message = JsonHelper.getJSONString(requestDeliverNotifyDto);
		//for(int i = 0;i<1000;i++){
		System.out.println("ok1");
		CommdityInternalNotifyListService.getInstance().rpushOrderDeliverNotify(notify_message);
		//}
		System.out.println("ok");
    }
    
    public static void simulateMultiDeliverNotify(){
    	String umac_prefix = "38:bc:1a:2f:7e:";
    	String orderid_prefix = "10012016031800000000";
    	//System.out.println(umac_prefix.concat(String.format("%02d", RandomData.intNumber(99))));
    	String mac = "84:82:f4:09:54:27";
    	int batch_sequence = 1;
    	long order_sequence = 1;
    	try{
	    	while(true){
	    		for(int i=0;i<5;i++){
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
		    		CommdityInternalNotifyListService.getInstance().rpushOrderDeliverNotify(notify_message);
		    		order_sequence++;
	    		}
	    		Thread.sleep(1000l);
	    		System.out.println(batch_sequence);
	    		batch_sequence++;
	    	}
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}

		//for(int i = 0;i<1000;i++){
		//System.out.println("ok1");
		
    }

}
