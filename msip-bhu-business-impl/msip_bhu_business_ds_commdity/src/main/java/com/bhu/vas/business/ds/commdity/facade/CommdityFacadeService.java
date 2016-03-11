package com.bhu.vas.business.ds.commdity.facade;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.commdity.helper.CommdityHelper;
import com.bhu.vas.api.rpc.commdity.model.Commdity;
import com.bhu.vas.business.ds.commdity.service.CommdityService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class CommdityFacadeService {
	
	@Resource
	private CommdityService commdityService;
	
	/**
	 * 根据商品不同状态查询商品数量
	 * @param status 商品状态
	 */
	public int countCommdityByStatus(Integer status){
		ModelCriteria mc = new ModelCriteria();
		if(status != null){
			mc.createCriteria().andColumnEqualTo("status", status);
		}
		return commdityService.countByModelCriteria(mc);
	}
	/**
	 * 根据商品不同状态查询商品列表
	 * @param status 商品状态
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public List<Commdity> findCommditysByStatus(Integer status, int pageNo, int pageSize){
		ModelCriteria mc = new ModelCriteria();
		if(status != null){
			mc.createCriteria().andColumnEqualTo("status", status);
		}
		mc.setPageNumber(pageNo);
		mc.setSize(pageSize);
		return commdityService.findModelByModelCriteria(mc);
	}
	
	
	/**
	 * 如果商品是区间金额 则返回随机金额
	 * 如果不是 正常返回
	 * @param commdityid
	 * @return
	 */
	public String commdityAmount(Integer commdityid){
		//商品信息验证
		Commdity commdity = commdityService.getById(commdityid);
		if(commdity == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_COMMDITY_DATA_NOTEXIST);
		}
		if(!CommdityHelper.onsale(commdity.getStatus())){
			throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_COMMDITY_NOT_ONSALE);
		}
		//订单金额处理
		String amount = CommdityHelper.generateCommdityAmount(commdity.getPrice());
		if(StringUtils.isEmpty(amount)){
			throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_COMMDITY_AMOUNT_ILLEGAL);
		}
		return amount;
	}
}
