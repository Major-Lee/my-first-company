package com.bhu.vas.business.ds.payment.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.payment.dto.PaymentRecordInfoDTO;
import com.bhu.vas.api.rpc.payment.model.PaymentChannelStat;
import com.bhu.vas.business.ds.payment.dao.PaymentChannelStatDao;
import com.smartwork.msip.cores.orm.service.EntityService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * @Editor Eclipse
 * @Author Reid
 * @CreateTime 2017年2月8日 下午5:40:19
 */

@Service
@Transactional("paymentTransactionManager")
public class PaymentChannelStatService extends EntityService<String, PaymentChannelStat, PaymentChannelStatDao> {

	@Resource
	@Override
	public void setEntityDao(PaymentChannelStatDao paymentChannelStatDao) {
		super.setEntityDao(paymentChannelStatDao);
	}

	public PaymentChannelStat findById(String id) {
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("id", id);
		List<PaymentChannelStat> list = this.findModelByModelCriteria(mc);
		return list.isEmpty() ? null : list.get(0);
	}

	public PaymentChannelStat findByCount(int count) {
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("count", count);
		List<PaymentChannelStat> list = this.findModelByModelCriteria(mc);
		return list.isEmpty() ? null : list.get(0);
	}
	
	public int countOfToday() {
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnGreaterThan("created_at",
				new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		return this.countByModelCriteria(mc);
	}
	
	public Boolean isExistById(String id) {
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("id", id);
		int count = this.countByModelCriteria(mc);
		return count>0;
	}
	
	public void AddRecord(PaymentChannelStat data){
		if(this.isExistById(data.getId())){
			this.update(data);
		}else{
			this.insert(data);
		}
	}
	
	public List<PaymentChannelStat> queryOrderByIdDesc(int limit){
		ModelCriteria mc = new ModelCriteria();
		mc.setOrderByClause("id desc");
		mc.setLimit(limit);
		return this.findModelByModelCriteria(mc);
	}
	
	public PaymentRecordInfoDTO paymentRecordInfo(){
		PaymentRecordInfoDTO recordInfoDTO = new PaymentRecordInfoDTO();
		int executeRet = this.executeProcedure(recordInfoDTO);
		if(executeRet == 0){
			;
		}else{
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR,new String[]{recordInfoDTO.getName()});
		}
		
		return recordInfoDTO;
	}
}
