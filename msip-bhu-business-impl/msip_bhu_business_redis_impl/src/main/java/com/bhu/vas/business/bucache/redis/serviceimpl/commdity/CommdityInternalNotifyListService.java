package com.bhu.vas.business.bucache.redis.serviceimpl.commdity;

import org.apache.commons.lang.StringUtils;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.api.dto.commdity.internal.pay.RequestWithdrawNotifyDTO;
import com.bhu.vas.api.dto.commdity.internal.pay.ResponsePaymentCompletedNotifyDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationListCache;
import com.smartwork.msip.cores.helper.JsonHelper;

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
    	
    	String orderid = "10012016030900000000000000000004";
    	boolean success = true;
    	//simulateResponsePaymentCompletedNotify(orderid, success);
    	simulateUpayDrawPaymentCompletedNotify(success);
    }
    
    /*************           test           **************/
    
    public static void simulateResponsePaymentCompletedNotify(String orderid, boolean success){
        ResponsePaymentCompletedNotifyDTO opn_dto = new ResponsePaymentCompletedNotifyDTO();
    	opn_dto.setSuccess(success);
    	opn_dto.setOrderid(orderid);
    	opn_dto.setPaymented_ts(System.currentTimeMillis());
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

}
