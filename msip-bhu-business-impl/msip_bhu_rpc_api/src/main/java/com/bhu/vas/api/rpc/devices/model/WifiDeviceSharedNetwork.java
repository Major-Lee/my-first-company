package com.bhu.vas.api.rpc.devices.model;

import com.bhu.vas.api.rpc.devices.dto.sharednetwork.SharedNetworkSettingDTO;
import com.smartwork.msip.cores.orm.model.extjson.DtoJsonExtPKModel;

/**
 * 识别设备的访客网络状态（初始未开启、开启、开启后关闭）
 * 初始未开启：此表中不存在 设备的记录则为
 * 开启：此表中存在并且 sharednetwork_type !=null && on = true && psn !=null
 * 开启后关闭：此表中存在 sharednetwork_type=null && on=false && psn==null
 * @author Edmond Lee
 *
 */
@SuppressWarnings("serial")
public class WifiDeviceSharedNetwork extends DtoJsonExtPKModel<String,SharedNetworkSettingDTO> {
	public static final String ModuleStyleTemplete = "%04d";
	private Integer owner;
	//采用的模板编号四位字符串 整数format
	private int template;
	private String sharednetwork_type;
	
	public String getSharednetwork_type() {
		return sharednetwork_type;
	}

	public void setSharednetwork_type(String sharednetwork_type) {
		this.sharednetwork_type = sharednetwork_type;
	}

	@Override
	public Class<SharedNetworkSettingDTO> getJsonParserModel() {
		return SharedNetworkSettingDTO.class;
	}

	@Override
	protected Class<String> getPKClass() {
		return String.class;
	}

	@Override
	public String getId() {
		return super.getId();
	}

	@Override
	public void setId(String id) {
		super.setId(id);
	}

	public Integer getOwner() {
		return owner;
	}

	public void setOwner(Integer owner) {
		this.owner = owner;
	}

	public int getTemplate() {
		return template;
	}

	public void setTemplate(int template) {
		this.template = template;
	}
}
