package com.bhu.vas.business.ds.commdity.dao;

import org.springframework.stereotype.Repository;

import com.bhu.vas.api.rpc.commdity.model.Commdity;
import com.smartwork.msip.business.abstractmsd.dao.AbstractCommdityDao;

@Repository
public class CommdityDao extends AbstractCommdityDao<Integer, Commdity>{//ReadWriteSpliterEntityDao<StorePurchasedItemPK,StorePurchasedItem,Integer>{
	
/*	public int porcByCommdityTest(CommdityPorcDTO dto) {
//		Map<String, Integer> parameterMap = new HashMap<String, Integer>();
//		parameterMap.put("id", commdityid);
		Object ret = super.getSqlSessionMasterTemplate().selectOne(entityClass.getName() + ".porcByCommdityTest", dto);
		if(ret == null){
			return 0;
		}
		return (int)ret;
	}*/
}
