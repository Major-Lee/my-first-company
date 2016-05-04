package com.bhu.vas.api.rpc.index.iservice;

import java.util.List;

import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.user.model.User;



public interface IWifiDeviceDocumentRpcService {
	public void messageDispose(String id, String message);
	/**
	 * 设备模块上线发生变更
	 * 变更涉及的更改索引字段是
	 * 1) d_monline
	 * 2) d_origvapmodule
	 * 3) o_operate
	 * @param id 设备mac
	 * @param origvapmodule 原始模块软件版本号
	 */
	public void moduleOnlineUpdIncrement(String id, String d_origvapmodule);
	
	/**
	 * 设备下线发生变更
	 * 变更涉及的更改索引字段是
	 * 1) d_online
	 * 2) d_monline
	 * 3) d_uptime
	 * 4) d_lastlogoutat
	 * 5) d_hoc
	 * @param id 设备mac
	 * @param d_uptime 设备运行总时长
	 * @param d_lastlogoutat 设备的最后下线的时间
	 */
	public void offlineUpdIncrement(String id, String d_uptime, long d_lastlogoutat);
	

	/**
	 * 设备上线发生变更
	 * 变更涉及的更改索引字段是
	 * 1) d_online
	 * 2) d_origswver
	 * 3) d_workmodel
	 * 4) d_configmodel
	 * 5) d_type
	 * 6) d_type_sname
	 * 7) d_lastregedat
	 * 8) d_dut
	 * @param entity
	 * @param newWifi
	 */
	public void onlineUpsertIncrement(WifiDevice entity, boolean newWifi);
	
	/**
	 * 设备绑定或解绑的变更
	 * 变更涉及的更改索引字段是
	 * 1) u_id
	 * 2) u_nick
	 * 3) u_mno
	 * 4) u_mcc
	 * 5) u_type
	 * 6) u_binded
	 * 7) u_dnick
	 * 8) d_industry 
	 * @param id 设备mac
	 * @param bindUser 如果为null表示解绑设备
	 * @param bindUserDNick 用户绑定的设备的昵称
	 * @param d_industry 设备的行业信息
	 */
	public void bindUserUpdIncrement(String id, User bindUser, String bindUserDNick, String d_industry);
	
	/**
	 * 用户设置绑定的设备的昵称
	 * 变更涉及的更改索引字段是
	 * 1) u_dnick
	 * @param id 设备mac
	 * @param bindUserDNick 用户绑定的设备的昵称
	 */
	public void bindUserDNickUpdIncrement(String id, String bindUserDNick);
	
	/**
	 * 清除设备的搜索引擎记录的相关数据
	 * 1：绑定数据关系
	 * 2：设备业务信息
	 * @param id
	 */
	public void resetUpdIncrement(String id);
	
	/**
	 * 用户设置绑定的设备的tags
	 * 变更涉及的更改索引字段是
	 * 1) d_tags
	 * @param id 设备mac
	 * @param d_tags 设备tags
	 */
	public void bindDTagsUpdIncrement(String id, String d_tags);
	
	/**
	 * 用户设置绑定的设备的tags multi
	 * 变更涉及的更改索引字段是
	 * 1) d_tags
	 * @param id 设备mac
	 * @param d_tags 设备tags
	 */
	public void bindDTagsMultiUpdIncrement(List<String> ids, List<String> d_tags);

	/**
	 * 设备位置发生变更
	 * 变更涉及的更改索引字段是
	 * 1) d_address
	 * 2) d_geopoint
	 * @param id 设备mac
	 * @param lat 纬度
	 * @param lon 经度
	 * @param d_address 详细地址
	 */
	public void locaitionUpdIncrement(String id, double lat, double lon, String d_address);

	/**
	 * 设备上线发生变更multi
	 * 变更涉及的更改索引字段是
	 * 1) d_online
	 * 2) d_origswver
	 * 3) d_workmodel
	 * 4) d_configmodel
	 * 5) d_type
	 * 6) d_type_sname
	 * 7) d_lastregedat
	 * 8) d_dut
	 * @param entitys 设备实体集合
	 */
	public void onlineMultiUpsertIncrement(List<WifiDevice> entitys);

	
	/**
	 * 设备运营模板的变更
	 * 变更涉及的更改索引字段是
	 * 1) o_template
	 * @param id
	 * @param o_template 运营模板编号
	 */
	public void templateUpdIncrement(String id, String o_template);
	
	/**
	 * 设备运营模板的变更multi
	 * 变更涉及的更改索引字段是
	 * 1) o_template
	 * @param ids 设备mac的集合
	 * @param o_template
	 */
	public void templateMultiUpdIncrement(List<String> ids, String o_template);
	
	/**
	 * 设备运营灰度级别的变更
	 * 变更涉及的更改索引字段是
	 * 1) o_graylevel
	 * @param id
	 * @param o_graylevel
	 */
	public void graylevelUpdIncrement(String id, String o_graylevel);
	
	/**
	 * 设备运营灰度级别的变更multi
	 * 变更涉及的更改索引字段是
	 * 1) o_graylevel
	 * @param ids 设备mac的集合
	 * @param o_graylevel
	 */
	public void graylevelMultiUpdIncrement(List<String> ids, String o_graylevel);
	
	/**
	 * 设备的终端数量的变更multi
	 * 变更涉及的更改索引字段是
	 * 1) d_hoc
	 * @param ids 设备mac的集合
	 * @param hocs 设备的终端数量的集合
	 */
	public void hocMultiUpdIncrement(List<String> ids, List<Integer> hocs);

	/**
	 * 设备的共享网络的变更multi
	 * 变更涉及的更改索引字段是
	 * 1) d_snk_type
	 */
	public void sharedNetworkMultiUpdIncrement(List<String> ids, String sharedNetwork_type, String template);

	/**
	 * 设备的共享网络的变更
	 * 变更涉及的更改索引字段是
	 * 1) d_snk_type
	 * @param id
	 * @param sharedNetwork_type
	 * @param template
	 */
	public void sharedNetworkUpdIncrement(String id, String sharedNetwork_type, String template);
	
	/**
	 * 批量打包全量覆盖索引
	 * @param macs
	 */
	public void blukIndexs(List<String> macs);
	
}
