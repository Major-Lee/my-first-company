package com.bhu.vas.business.ds.tag.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.tag.model.TagDevices;
import com.bhu.vas.api.rpc.tag.model.TagName;
import com.bhu.vas.business.ds.tag.dao.TagDevicesDao;
import com.bhu.vas.business.ds.tag.dao.TagNameDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractTagService;

/**
 * 
 * @author xiaowei
 *		by 16/04/12
 */

@Service
@Transactional("socialTransactionManager")
public class TagDevicesService extends AbstractTagService<String, TagDevices, TagDevicesDao> {

    @Resource
    @Override
    public void setEntityDao(TagDevicesDao tagDevicesDao) {
        super.setEntityDao(tagDevicesDao);
    }
    
}