package com.bhu.vas.business.search.service;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.elasticsearch.common.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.vto.advertise.AdvertiseTrashPositionVTO;
import com.bhu.vas.business.search.BusinessIndexDefine;
import com.bhu.vas.business.search.builder.WifiDeviceSearchMessageBuilder;
import com.bhu.vas.business.search.core.condition.AbstractDataSearchConditionService;
import com.bhu.vas.business.search.core.condition.component.SearchConditionMessage;
import com.bhu.vas.business.search.core.field.FieldDefine;
import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.repository.WifiDeviceDocumentRepository;
import com.smartwork.msip.cores.helper.StringHelper;
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
	
	public long searchCountByCommon(Integer u_id, String sharedNetwork_type, 
			String d_dut, String t_uc_extension, String d_online, String d_snk_turnstate, String d_tags){
		
		SearchConditionMessage scm = WifiDeviceSearchMessageBuilder.builderSearchMessageCommon(u_id, sharedNetwork_type, d_dut, t_uc_extension, d_online, d_snk_turnstate, d_tags);
		return super.searchCountByConditionMessage(scm);
	}
	
	public Page<WifiDeviceDocument> searchPageByCommon(Integer u_id, String sharedNetwork_type, 
			String d_dut, String t_uc_extension, String d_online, String d_snk_turnstate, String d_tags, int pageNo, int pageSize){

		SearchConditionMessage scm = WifiDeviceSearchMessageBuilder.builderSearchMessageCommon(u_id, sharedNetwork_type, d_dut, t_uc_extension, d_online, d_snk_turnstate, d_tags);
		return super.searchByConditionMessage(scm, pageNo, pageSize);
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
	public List<WifiDeviceDocument> searchListByUidAndDut(Integer u_id, String d_dut, int pageNo, int pageSize){
		if(u_id == null) return Collections.emptyList();
		
		SearchConditionMessage scm = WifiDeviceSearchMessageBuilder.builderSearchMessageWithDut(u_id, d_dut);
		return super.searchListByConditionMessage(scm, pageNo, pageSize);
	}
	/**
	 * 根据设备共享网络类型进行分页数据查询
	 * @param u_id 用户uid
	 * @param sharedNetwork_type 共享网络类型
	 * @param d_snk_template 模板 空字符串 忽略此条件 null 没有赋予template的sharedNetwork_type的设备 正常值则是取服务此值的数据
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Page<WifiDeviceDocument> searchPageBySharedNetwork(Integer u_id, String sharedNetwork_type, String d_snk_template, String d_dut, int pageNo, int pageSize){
		if(u_id == null) return null;
		
		SearchConditionMessage scm = WifiDeviceSearchMessageBuilder.builderSearchMessageWithSharedNetwork(u_id, sharedNetwork_type,d_snk_template, d_dut);
		return super.searchByConditionMessage(scm, pageNo, pageSize);
	}
	
	/**
	 * 根据用户分组查询设备数量
	 * @param u_id
	 * @param t_uc_extension
	 * @param d_online
	 * @return
	 */
	public long searchCountByUserGroup(Integer u_id, String t_uc_extension, String d_online){
		if(u_id == null) return 0;
		
		SearchConditionMessage scm = WifiDeviceSearchMessageBuilder.builderSearchMessageWithUserGroup(u_id, t_uc_extension, d_online);
		return super.searchCountByConditionMessage(scm);
	}
	
	/**
	 * 根据地理位置查询设备数量
	 * @param d_province
	 * @param d_city
	 * @param d_distrcy
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public long searchCountByPosition(List<AdvertiseTrashPositionVTO> must_not_positions,String d_province,String d_city,String d_distrcy){
		
		SearchConditionMessage scm = WifiDeviceSearchMessageBuilder.builderSearchMessageWithPosition(must_not_positions,d_province, d_city, d_distrcy,false);
		if(scm == null || scm.equals(null)){
			return 0L;
		}
		return super.searchCountByConditionMessage(scm);
	}
	
	/**
	 * 根据snk查询设备数量
	 * @param u_id
	 * @param d_snk_type
	 * @param d_snk_turnstate
	 * @param d_snk_template
	 * @return
	 */
	public long searchCountBySnkType(Integer u_id, String d_snk_type, String d_snk_turnstate, String d_snk_template){
		if(u_id == null || StringUtils.isEmpty(d_snk_type)) return 0;
		
		SearchConditionMessage scm = WifiDeviceSearchMessageBuilder.builderSearchMessageWithSnkType(u_id, d_snk_type, d_snk_turnstate, d_snk_template);
		return super.searchCountByConditionMessage(scm);
	}
	
	
	/**
	 * 根据设备共享网络类型进行scan的iterator
	 * @param u_id 用户uid
	 * @param sharedNetwork_type 共享网络类型 null 忽略此条件  空字符串 没有赋予sharedNetwork_type的设备 正常值则是取服务此值的数据
	 * @param d_snk_template 模板 null 忽略此条件  空字符串 没有赋予template的sharedNetwork_type的设备 正常值则是取服务此值的数据
	 * @param notify
	 * @param pageSize
	 */
	public void iteratorWithSharedNetwork(Integer u_id, String sharedNetwork_type,String d_snk_template, String d_dut, 
			 int pageSize, IteratorNotify<Page<WifiDeviceDocument>> notify){
		if(u_id == null) return;
		
		SearchConditionMessage scm = WifiDeviceSearchMessageBuilder.builderSearchMessageWithSharedNetwork(u_id, sharedNetwork_type,d_snk_template, d_dut);
		String message = WifiDeviceSearchMessageBuilder.builderSearchMessageString(scm);
		super.iteratorAll(BusinessIndexDefine.WifiDevice.IndexName, BusinessIndexDefine.WifiDevice.Type, 
				message, pageSize, notify);
	}
	
	
	/**
	 * 根据设备的定位位置进行scan的iterator
	 * @param d_province
	 * @param d_city
	 * @param d_distrcy
	 * @param pageSize
	 * @param notify
	 */
	public void iteratorWithPosition(List<AdvertiseTrashPositionVTO> must_not_positions,String d_province,String d_city, String d_distrcy, 
			boolean snkTurnOn, int pageSize, IteratorNotify<Page<WifiDeviceDocument>> notify){
		
		SearchConditionMessage scm = WifiDeviceSearchMessageBuilder.builderSearchMessageWithPosition(must_not_positions,d_province, d_city, d_distrcy,snkTurnOn);
		String message = WifiDeviceSearchMessageBuilder.builderSearchMessageString(scm);
		super.iteratorAll(BusinessIndexDefine.WifiDevice.IndexName, BusinessIndexDefine.WifiDevice.Type, 
				message, pageSize, notify);
	}
	
	/**
	 * 根据通用条件进行scan的iterator
	 * @param u_id
	 * @param sharedNetwork_type
	 * @param d_dut
	 * @param t_uc_extension
	 * @param d_online
	 * @param d_snk_turnstate
	 * @param d_tags
	 * @param pageSize
	 * @param notify
	 */
	public void iteratorAllByCommon(Integer u_id, String sharedNetwork_type, 
			String d_dut, String t_uc_extension, String d_online, String d_snk_turnstate, String d_tags,
			 int pageSize, IteratorNotify<Page<WifiDeviceDocument>> notify){
		
		SearchConditionMessage scm = WifiDeviceSearchMessageBuilder.builderSearchMessageCommon(u_id, sharedNetwork_type, d_dut, t_uc_extension, d_online, d_snk_turnstate, d_tags);
		String message = WifiDeviceSearchMessageBuilder.builderSearchMessageString(scm);
		super.iteratorAll(BusinessIndexDefine.WifiDevice.IndexName, BusinessIndexDefine.WifiDevice.Type, 
				message, pageSize, notify);
	}
	
	/**
	 * 根据message动态条件进行scan的iterator
	 * @param message
	 * @param notify
	 */
	public void iteratorAll(String message, IteratorNotify<Page<WifiDeviceDocument>> notify){
		super.iteratorAll(BusinessIndexDefine.WifiDevice.IndexName, BusinessIndexDefine.WifiDevice.Type, 
				message, notify);
	}
	
	/**
	 * 根据message动态条件进行scan的iterator
	 * @param message
	 * @param notify
	 */
	public void iteratorAll(String message, int pageSize, IteratorNotify<Page<WifiDeviceDocument>> notify){
		super.iteratorAll(BusinessIndexDefine.WifiDevice.IndexName, BusinessIndexDefine.WifiDevice.Type, 
				message, pageSize, notify);
	}
}
