package com.bhu.vas.business.search.service;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.elasticsearch.common.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bhu.vas.business.search.BusinessIndexDefine;
import com.bhu.vas.business.search.builder.WifiDeviceSearchMessageBuilder;
import com.bhu.vas.business.search.core.condition.AbstractDataSearchConditionService;
import com.bhu.vas.business.search.core.condition.component.SearchConditionMessage;
import com.bhu.vas.business.search.core.field.FieldDefine;
import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.repository.WifiDeviceDocumentRepository;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;

@Service
public class WifiDeviceDataSearchService extends AbstractDataSearchConditionService<WifiDeviceDocument>{
    @Resource
    private WifiDeviceDocumentRepository wifiDeviceDocumentRepository;
	
	public WifiDeviceDocumentRepository getRepository(){
		return wifiDeviceDocumentRepository;
	}
	
	@Override
	public FieldDefine getFieldByName(String fieldName){
		if(StringUtils.isEmpty(fieldName)) return null;
		return BusinessIndexDefine.WifiDevice.Field.getByName(fieldName);
	}
	
	@Override
	public int retryOnConflict(){
		return BusinessIndexDefine.WifiDevice.RetryOnConflict;
	}
	
	public WifiDeviceDocument searchById(String id){
		return this.getRepository().findOne(id);
	}
	
	/**
	 * 根据条件搜索数据分页
	 * 绑定设备的用户id
	 * 设备的业务线类型
	 * @param u_id
	 * @param d_dut
	 * @return
	 */
	public Page<WifiDeviceDocument> searchPageByUidAndDut(Integer u_id, String d_dut, int pageNo, int pageSize){
		if(u_id == null) return null;
		
		SearchConditionMessage scm = WifiDeviceSearchMessageBuilder.builderSearchMessageWithDut(u_id, d_dut);
		return super.searchByConditionMessage(scm, pageNo, pageSize);
	}
	
	/**
	 * 根据条件搜索数据
	 * 绑定设备的用户id
	 * 设备的业务线类型
	 * @param u_id
	 * @param d_dut
	 * @return
	 */
	public List<WifiDeviceDocument> searchListByUidAndDut(Integer u_id, String d_dut){
		if(u_id == null) return Collections.emptyList();
		
		SearchConditionMessage scm = WifiDeviceSearchMessageBuilder.builderSearchMessageWithDut(u_id, d_dut);
		return super.searchByConditionMessage(scm);
	}
	/**
	 * 根据设备共享网络类型进行分页数据查询
	 * @param u_id 用户uid
	 * @param sharedNetwork_type 共享网络类型
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Page<WifiDeviceDocument> searchPageBySharedNetwork(Integer u_id, String sharedNetwork_type, String d_dut, String d_snk_template, int pageNo, int pageSize){
		if(u_id == null) return null;
		
		SearchConditionMessage scm = WifiDeviceSearchMessageBuilder.builderSearchMessageWithSharedNetwork(u_id, sharedNetwork_type, d_dut, d_snk_template);
		return super.searchByConditionMessage(scm, pageNo, pageSize);
	}
	
	/**
	 * 根据设备共享网络类型进行scan的iterator
	 * @param u_id 用户uid
	 * @param sharedNetwork_type 共享网络类型
	 * @param d_snk_template 模板 空字符串 忽略此条件 null 没有赋予template的sharedNetwork_type的设备 正常值则是取服务此值的数据
	 * @param notify
	 * @param pageSize
	 */
	public void iteratorWithSharedNetwork(Integer u_id, String sharedNetwork_type,String d_snk_template, String d_dut, 
			 int pageSize, IteratorNotify<Page<WifiDeviceDocument>> notify){
		if(u_id == null) return;
		
		SearchConditionMessage scm = WifiDeviceSearchMessageBuilder.builderSearchMessageWithSharedNetwork(u_id, sharedNetwork_type, d_dut, d_snk_template);
		String message = WifiDeviceSearchMessageBuilder.builderSearchMessageString(scm);
		super.iteratorAll(BusinessIndexDefine.WifiDevice.IndexNameNew, BusinessIndexDefine.WifiDevice.Type, 
				message, pageSize, notify);
	}
	
	/**
	 * 根据message动态条件进行scan的iterator
	 * @param message
	 * @param notify
	 */
	public void iteratorAll(String message, IteratorNotify<Page<WifiDeviceDocument>> notify){
		super.iteratorAll(BusinessIndexDefine.WifiDevice.IndexNameNew, BusinessIndexDefine.WifiDevice.Type, 
				message, notify);
	}
}
