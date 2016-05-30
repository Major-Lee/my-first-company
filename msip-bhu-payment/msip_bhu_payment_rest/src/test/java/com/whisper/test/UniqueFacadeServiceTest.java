package com.whisper.test;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.api.dto.procedure.ShareDealDailyUserSummaryProcedureDTO;
import com.bhu.vas.api.rpc.payment.dto.PaymentRecordInfoDTO;
import com.bhu.vas.api.rpc.payment.model.PaymentReckoning;
import com.bhu.vas.api.rpc.payment.model.PaymentRecord;
import com.bhu.vas.api.rpc.payment.vto.PaymentRecordVTO;
import com.bhu.vas.api.vto.payment.RecordInfoVTO;
import com.bhu.vas.business.ds.payment.service.PaymentReckoningService;
import com.bhu.vas.business.ds.payment.service.PaymentRecordService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.localunit.BaseTest;

public class UniqueFacadeServiceTest extends BaseTest{
	@Resource
	PaymentRecordService paymentRecordService;
	@Resource
	PaymentReckoningService paymentReckoningService;
	
	@Test
	public void checkEmailExist(){
		
//		ResponseError error =  ValidateService.validateEmail("brook6@jing.fm");
//		if(error != null){
//			System.out.println(error.getMsg());
//			System.out.println(error.getCodemsg());
//		}else
//			System.out.println("不存在");
		
	}
	
	@Test
	public void paymentRecord(){
		
		//PaymentReckoning order = paymentReckoningService.findByOrderId("1463656048000");
		//System.out.println(order.getToken());
//		PaymentRecord data = new PaymentRecord();
//		data.setId("1235");
//		data.setAmount(12.35F);
//		data.setCount(223);
//		data.setInfo("{123343}");
//		Boolean order = paymentRecordService.AddRecord(data);
//		System.out.println(order);
		
		List<PaymentRecord> list = paymentRecordService.queryOrderByIdDesc(2);
		List<PaymentRecordVTO> infos = new ArrayList<PaymentRecordVTO>();
		for(int i=0;i<list.size();i++){
			PaymentRecordVTO paymentRecordVTO = new PaymentRecordVTO();
			paymentRecordVTO.setTimeD(list.get(i).getId().toString().substring(3));
			paymentRecordVTO.setAmount(list.get(i).getAmount());
			paymentRecordVTO.setCount(list.get(i).getCount());
			paymentRecordVTO.setInfo(list.get(i).getInfo());
			infos.add(paymentRecordVTO);
		}
		RecordInfoVTO recordInfo = new RecordInfoVTO();
		
		recordInfo.setInfo(infos);
		recordInfo.setHistory_order_count(paymentReckoningService.countOfAll());
		recordInfo.setHistory_order_user(paymentReckoningService.countOfUser());
		recordInfo.setPay_again_user_count(3432);
		
		String json = JsonHelper.getJSONString(recordInfo);
		System.out.println(json);
	}
	
	@Test
	public void paymentReckoning(){
		List<PaymentReckoning> list = paymentReckoningService.listToday();
		for(int i=0;i<list.size();i++){
			System.out.println(list.get(i).getId());
		}
	}
	
	@Test
	public void recordd(){
		paymentRecordService.paymentRecordInfo();
//		PaymentRecordInfoDTO recordInfoDTO = new PaymentRecordInfoDTO();
//		int executeRet = paymentRecordService.executeProcedure(recordInfoDTO);
//		System.out.println("executeRet:"+executeRet);
//		if(executeRet == 0){
//			;
//		}else{
//			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR,new String[]{recordInfoDTO.getName()});
//		}
//		System.out.println(recordInfoDTO.toString());
//		System.out.println(1111);
//		int count = recordInfoDTO.count();
//		System.out.println(count);
	}
}
