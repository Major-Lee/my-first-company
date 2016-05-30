package com.bhu.vas.business.ds.payment.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.dto.procedure.DeviceGroupPaymentTotalProcedureDTO;
import com.bhu.vas.api.rpc.payment.dto.PaymentRecordInfoDTO;
import com.bhu.vas.api.rpc.payment.model.PaymentRecord;
import com.bhu.vas.business.ds.payment.dao.PaymentRecordDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractPaymentService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * @Editor Eclipse
 * @Author Zongshuai
 * @CreateTime 2016年4月18日 下午5:40:19
 */

@Service
@Transactional("paymentTransactionManager")
public class PaymentRecordService extends AbstractPaymentService<String, PaymentRecord, PaymentRecordDao> {

	@Resource
	@Override
	public void setEntityDao(PaymentRecordDao paymentRecordDao) {
		super.setEntityDao(paymentRecordDao);
	}

	public PaymentRecord findById(String id) {
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("id", id);
		List<PaymentRecord> list = this.findModelByModelCriteria(mc);
		return list.isEmpty() ? null : list.get(0);
	}

	public PaymentRecord findByCount(int count) {
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("count", count);
		List<PaymentRecord> list = this.findModelByModelCriteria(mc);
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
	
	public void AddRecord(PaymentRecord data){
		if(this.isExistById(data.getId())){
			this.update(data);
		}else{
			this.insert(data);
		}
	}
	
	public List<PaymentRecord> queryOrderByIdDesc(int limit){
		System.out.println(limit);
		ModelCriteria mc = new ModelCriteria();
		mc.setOrderByClause("id");
		mc.setLimit(limit);
		return this.findModelByModelCriteria(mc);
	}
	
	public void paymentRecordInfo(){
		PaymentRecordInfoDTO recordInfoDTO = new PaymentRecordInfoDTO();
		int executeRet = this.executeProcedure(recordInfoDTO);
		if(executeRet == 0){
			;
		}else{
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR,new String[]{recordInfoDTO.getName()});
		}
		int count = recordInfoDTO.getCount();
		System.out.println(count);
	}
	 /**
     * 统计收益总额/次数
     * 并且更新总额数据
     * @param uid
     * @param groupid
     */
	public void paymentTotalWithProcedure(int uid, String gid){
		DeviceGroupPaymentTotalProcedureDTO procedureDTO = new DeviceGroupPaymentTotalProcedureDTO();
		procedureDTO.setUserid(uid);
		procedureDTO.setGid(gid);

		int executeRet = this.executeProcedure(procedureDTO);
		if(executeRet == 0){
			;
		}else{
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR,new String[]{procedureDTO.getName()});
		}
	}
}
