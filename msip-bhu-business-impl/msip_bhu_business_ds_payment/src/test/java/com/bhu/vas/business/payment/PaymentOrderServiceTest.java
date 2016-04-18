package com.bhu.vas.business.payment;

import javax.annotation.Resource;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.bhu.vas.api.rpc.payment.model.PaymentOrder;
import com.bhu.vas.business.ds.payment.service.PaymentOrderService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.localunit.BaseTest;
import com.smartwork.msip.localunit.RandomPicker;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PaymentOrderServiceTest extends BaseTest{
	@Resource
	PaymentOrderService paymentOrderService;
	
	static String[] letters = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
    @Test
	public void test001BatchCreateOrder(){
    	ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andSimpleCaulse(" 1=1");
		paymentOrderService.deleteByModelCriteria(mc);
		for(int i=1;i<100;i++){
			PaymentOrder order  = new PaymentOrder();
			order.setId(new Long(i));
			order.setTid(RandomPicker.randString(letters, 10));
			order.setGid(RandomPicker.randString(letters, 10));
			paymentOrderService.insert(order);	
		}
	}	
    
    @Test
    public void test002RemoveOrder(){
    	int ret  = paymentOrderService.deleteById(99l);
    	System.out.println(ret);
    }
    
}
