package com.bhu.vas.api.rpc.devices.model;

import org.springframework.util.StringUtils;

import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
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
	private Integer idle_timeout;	//因为网安特别添加，当此字段存在时，需要忽略json中的idle_timeout值
	
	private String ssid;
	private Integer rate;
	
	//采用的模板编号四位字符串 整数format
	private String template;
	private String sharednetwork_type;


	public SharedNetworkSettingDTO getInnerModel(){
		SharedNetworkSettingDTO dto = super.getInnerModel();
		if(dto == null)
			return null;
		ParamSharedNetworkDTO psn = dto.getPsn();
		if(psn != null){
			if(idle_timeout != null)
				psn.setIdle_timeout(idle_timeout.intValue());
			if(!StringUtils.isEmpty(ssid))
				psn.setSsid(ssid);
			if(rate != null){
				psn.setUsers_tx_rate(rate.intValue());
				psn.setUsers_rx_rate(rate.intValue());
			}
		}
		return dto;
	}
	
	public Integer getIdle_timeout() {
		return idle_timeout;
	}

	public void setIdle_timeout(Integer idle_timeout) {
		this.idle_timeout = idle_timeout;
	}

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

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public Integer getRate() {
		return rate;
	}

	public void setRate(Integer rate) {
		this.rate = rate;
	}
	
	
}
