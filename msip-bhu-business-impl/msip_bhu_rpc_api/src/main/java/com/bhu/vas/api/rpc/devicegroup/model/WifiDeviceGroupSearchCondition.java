package com.bhu.vas.api.rpc.devicegroup.model;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.bhu.vas.api.rpc.user.dto.UserSearchConditionDTO;
import com.smartwork.msip.cores.orm.model.extjson.SimpleJsonExtPKModel;
/*
 * wifi设备的组对应的搜索条件
 */
@SuppressWarnings("serial")
public class WifiDeviceGroupSearchCondition extends SimpleJsonExtPKModel<UserSearchConditionDTO,Long>{
	
	public WifiDeviceGroupSearchCondition() {
		super();
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(obj instanceof WifiDeviceGroupSearchCondition){
			WifiDeviceGroupSearchCondition dto = (WifiDeviceGroupSearchCondition)obj;
			if(dto.getId().intValue() == this.id.intValue()) return true;
			else return false;
		}else return false;
	}
	@Override
	public int hashCode() {
		if(this.getId() == null) this.setId(0l);
		return this.getId().hashCode();
	}
	
	@Override
	public Class<UserSearchConditionDTO> getJsonParserModel() {
		return UserSearchConditionDTO.class;
	}
	@Override
	protected Class<Long> getPKClass() {
		return Long.class;
	}

	@Override
	public Long getId() {
		return super.getId();
	}

	@Override
	public void setId(Long id) {
		super.setId(id);
	}
	
}