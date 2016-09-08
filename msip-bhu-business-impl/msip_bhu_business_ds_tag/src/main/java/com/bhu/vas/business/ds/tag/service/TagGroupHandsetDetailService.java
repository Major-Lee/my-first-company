package com.bhu.vas.business.ds.tag.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.tag.model.TagGroupHandsetDetail;
import com.bhu.vas.api.rpc.user.model.UserWalletLog;
import com.bhu.vas.business.ds.tag.dao.TagGroupHandsetDetailDao;
import com.bhu.vas.business.ds.tag.dao.TagNameDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractTagService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
/**
 * 
 * @author xiaowei
 *		by 16/09/02
 */

@Service
@Transactional("tagTransactionManager")
public class TagGroupHandsetDetailService extends AbstractTagService<Long, TagGroupHandsetDetail, TagGroupHandsetDetailDao>{
	
    @Resource
    @Override
    public void setEntityDao(TagGroupHandsetDetailDao tagGroupHandsetDetailDao) {
        super.setEntityDao(tagGroupHandsetDetailDao);
    }
	
	public boolean isNewUser(int gid,String hdmac){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("gid", gid).andColumnEqualTo("hdmac", hdmac);
		int count = this.countByModelCriteria(mc);
		return count == 0;
	}

	public List<Map<String,Object>> selectHandsetDetail(int gid,String beginTime,String endTime){
		Map<String,Object> map = new HashMap<>();
		map.put("gid", gid);
		map.put("beginTime", beginTime);
		map.put("endTime", endTime);
		return this.getEntityDao().getSqlSessionMasterTemplate().selectList(TagGroupHandsetDetail.class.getName()+".selectHandsetDetail", map);
	}
}
