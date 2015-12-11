package com.bhu.vas.business.backendonline.asyncprocessor.service.indexincr;

import java.util.List;

import com.bhu.vas.api.rpc.agent.model.AgentDeviceClaim;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.user.model.User;

public interface IWifiDeviceIndexIncrement {
	/**
	 * 设备位置发生变更
	 * @param id 设备mac
	 * @param lat 纬度
	 * @param lon 经度
	 * @param d_address 详细地址
	 */
	public void locaitionUpdIncrement(String id, double lat, double lon, String d_address);
	
	/**
	 * 设备模块上线发生变更
	 * @param id 设备mac
	 * @param origvapmodule 原始模块软件版本号
	 */
	public void moduleOnlineUpdIncrement(String id, String d_origvapmodule);
	
	/**
	 * 设备下线发生变更
	 * @param id 设备mac
	 * @param d_uptime 设备运行总时长
	 * @param d_lastlogoutat 设备的最后下线的时间
	 */
	public void offlineUpdIncrement(String id, String d_uptime, long d_lastlogoutat);
	
	/**
	 * 创建在导入的确认的设备数据multi
	 * @param importId 导入批次
	 * @param agentDeviceClaims
	 */
	public void batchConfirmMultiCrdIncrement(long importId, List<AgentDeviceClaim> agentDeviceClaims);
	
	/**
	 * 设备上线发生变更
	 * @param entity
	 */
	public void onlineUpdIncrement(WifiDevice entity);
	
	/**
	 * 设备上线发生变更multi
	 * @param entitys 设备实体集合
	 */
	public void onlineMultiUpdIncrement(List<WifiDevice> entitys);
	
	/**
	 * 设备认领上线处理或首次上线，按照全字段重建覆盖标准
	 * @param entity
	 */
	public void onlineCrdIncrement(WifiDevice entity);
	
	/**
	 * 设备绑定或解绑的变更
	 * @param id 设备mac
	 * @param bindUser 如果为null表示解绑设备
	 */
	public void bindUserUpdIncrement(String id, User bindUser);
	
	/**
	 * 设备运营模板的变更
	 * @param id
	 * @param o_template 运营模板编号
	 */
	public void templateUpdIncrement(String id, String o_template);
	
	/**
	 * 设备运营模板的变更multi
	 * @param ids 设备mac的集合
	 * @param o_template
	 */
	public void templateMultiUpdIncrement(List<String> ids, String o_template);
	
	/**
	 * 设备运营灰度级别的变更
	 * @param id
	 * @param o_graylevel
	 */
	public void graylevelUpdIncrement(String id, String o_graylevel);
	
	/**
	 * 设备运营灰度级别的变更multi
	 * @param ids 设备mac的集合
	 * @param o_graylevel
	 */
	public void graylevelMultiUpdIncrement(List<String> ids, String o_graylevel);
	
	/**
	 * 设备的终端数量的变更multi
	 * @param ids 设备mac的集合
	 * @param hocs 设备的终端数量的集合
	 */
	public void hocMultiUpdIncrement(List<String> ids, List<Integer> hocs);
}
