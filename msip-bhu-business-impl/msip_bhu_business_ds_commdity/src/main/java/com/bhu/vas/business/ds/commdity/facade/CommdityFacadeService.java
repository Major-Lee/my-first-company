package com.bhu.vas.business.ds.commdity.facade;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.commdity.CommdityPhysicalDTO;
import com.bhu.vas.api.rpc.commdity.helper.CommdityHelper;
import com.bhu.vas.api.rpc.commdity.model.Commdity;
import com.bhu.vas.api.rpc.commdity.model.CommdityPhysical;
import com.bhu.vas.business.ds.commdity.service.CommdityPhysicalService;
import com.bhu.vas.business.ds.commdity.service.CommdityService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.criteria.PerfectCriteria.Criteria;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class CommdityFacadeService {
	
	@Resource
	private CommdityService commdityService;
	
	@Resource 
	private CommdityPhysicalService commdityPhysicalService;
	/**
	 * 根据商品不同状态查询商品数量
	 * @param status 商品状态
	 */
	public int countCommdityByParam(Integer status, Integer category){
		ModelCriteria mc = new ModelCriteria();
		Criteria criteria = mc.createCriteria();
		if(status != null){
			criteria.andColumnEqualTo("status", status);
		}
		if(category != null){
			criteria.andColumnEqualTo("category", category);
		}
		return commdityService.countByModelCriteria(mc);
	}
	/**
	 * 根据商品不同状态查询商品列表
	 * @param status 商品状态
	 * @param category 商品分类
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public List<Commdity> findCommdityPageByParam(Integer status, Integer category, int pageNo, int pageSize){
		ModelCriteria mc = new ModelCriteria();
		Criteria criteria = mc.createCriteria();
		if(status != null){
			criteria.andColumnEqualTo("status", status);
		}
		if(category != null){
			criteria.andColumnEqualTo("category", category);
		}
		mc.setPageNumber(pageNo);
		mc.setPageSize(pageSize);
		return commdityService.findModelByModelCriteria(mc);
	}
	
	/**
	 * 验证商品id是否可用 是否在售
	 * @param commdityid
	 * @return
	 */
	public Commdity validateCommdity(Integer commdityid){
		if(commdityid == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_COMMDITY_DATA_NOTEXIST);
		}
		//商品信息验证
		Commdity commdity = commdityService.getById(commdityid);
		if(commdity == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_COMMDITY_DATA_NOTEXIST);
		}
		if(!CommdityHelper.onsale(commdity.getStatus())){
			throw new BusinessI18nCodeException(ResponseErrorCode.VALIDATE_COMMDITY_NOT_ONSALE);
		}
		return commdity;
	}
	
	public CommdityPhysical updateCommdityPhysical(CommdityPhysical commdityPhysical){
		return commdityPhysicalService.update(commdityPhysical);
	}
	
	public CommdityPhysical insertCommdityPhysical(CommdityPhysical commdityPhysical){
		return commdityPhysicalService.insert(commdityPhysical);
	}
	
	public CommdityPhysicalDTO getCommdityPhysicalDTO(String umac){
		return commdityPhysicalService.getById(umac).getInnerModel();
	}
	
	/**
	 * 如果商品是区间金额 则返回随机金额
	 * 如果不是 正常返回
	 * @param commdityid
	 * @return
	 */
/*	public String commdityAmount(Integer commdityid){
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
	}*/
}
