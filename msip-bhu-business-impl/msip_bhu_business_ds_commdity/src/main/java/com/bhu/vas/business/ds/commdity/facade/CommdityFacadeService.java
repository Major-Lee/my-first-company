package com.bhu.vas.business.ds.commdity.facade;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.commdity.model.Commdity;
import com.bhu.vas.business.ds.commdity.service.CommdityService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

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
}
