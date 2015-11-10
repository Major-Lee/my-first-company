package com.bhu.vas.business.search.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;

import com.bhu.vas.business.search.BusinessIndexDefine;

@Document(	indexName = BusinessIndexDefine.WifiDevice.IndexNameNew, 
			type = BusinessIndexDefine.WifiDevice.Type, 
			shards = BusinessIndexDefine.WifiDevice.Shards, 
			replicas = BusinessIndexDefine.WifiDevice.replicas,
			refreshInterval = BusinessIndexDefine.WifiDevice.refreshInterval
		)
/*@Document(	indexName = "wifi_device_index9", 
type = "myWifiDevice", 
shards = 5, 
replicas = 1)*/
public class WifiDeviceDocument1 extends AbstractDocument{
	@Id
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String id;//设备mac
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String d_sn;//设备的sn编号

	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String d_origswver;//设备的原始软件版本号
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String d_origvapmodule;//设备的原始软件增值模块版本号

	
	@Field(
			type = FieldType.String,
			searchAnalyzer = "lowercase_whitespace",
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String d_workmodel;//设备的工作模式
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String d_configmodel;//设备的配置模式
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String d_type;//设备的类型

	
	/**
	 * 建立索引的时候 数组为 经度 纬度
	 * 
	 */
	@GeoPointField
	private double[] d_geopoint;//设备所在位置的经纬度坐标 lon,lat
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.analyzed,
			searchAnalyzer = "lowercase_whitespace",
			indexAnalyzer = "ngram_2_to_20_analyzer",
			store = true
	)
	private String d_address;//设备所在位置的详细地址
	
	@Field(
			type = FieldType.String,
			searchAnalyzer = "lowercase_whitespace",
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String d_online;//设备在线状态 0 从未上线 1 在线 2 离线
	
	@Field(
			type = FieldType.String,
			searchAnalyzer = "lowercase_whitespace",
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String d_monline;//设备增值模块在线状态 0 从未上线 1 在线 2 离线
	
//	@Field(
//			type = FieldType.String,
//			index = FieldIndex.analyzed,
//			searchAnalyzer = "lowercase_whitespace",
//			indexAnalyzer = "lowercase_whitespace",
//			store = true
//	)
//	private String d_groups;//wifi设备所属群组
	
//	@Field(
//			type = FieldType.Integer,
//			index = FieldIndex.not_analyzed,
//			store = true
//	)
//	private int d_nvd;//是否是新版本设备

	@Field(
			type = FieldType.Integer,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private int d_hoc;//wifi设备上的移动设备在线数量
	
	@Field(
			type = FieldType.Long,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private long d_lastregedat;//设备的最新的上线时间
	
	@Field(
			type = FieldType.Long,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private long d_lastlogoutat;//设备的最新的下线时间
	
	@Field(
			type = FieldType.Long,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private long d_createdat;//设备的接入时间(入库时间)
	
	@Field(
			type = FieldType.String,
			searchAnalyzer = "lowercase_whitespace",
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String d_dut;//设备的业务线定义
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String d_uptime;//设备在线总时长 单位秒
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String updatedat;//索引记录的更新时间
	
	/*@Field(
			type = FieldType.Date,
			index = FieldIndex.not_analyzed,
			store = true,
			format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss"
	)
	private Date updateAt;*/
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String o_template;//运营模板编号
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.analyzed,
			searchAnalyzer = "lowercase_whitespace",
			//index = FieldIndex.not_analyzed,
			indexAnalyzer = "lowercase_whitespace",
			store = true
	)
	private String o_graylevel;//设备的灰度定义
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String o_batch;//设备的导入批次
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String u_id;//绑定的用户id
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String u_nick;//绑定的用户名称
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String u_mno;///绑定的用户的手机号码
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String u_mcc;//绑定的用户的手机号码的区域号码
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String u_type;//绑定的用户类型 代理商，普通，销售等等
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String a_id;//代理商的用户id
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String a_nick;//代理商的用户昵称
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String a_org;//代理商的公司名称

	@Override
	public String getId() {
		return id;
	}

	public String getD_sn() {
		return d_sn;
	}

	public void setD_sn(String d_sn) {
		this.d_sn = d_sn;
	}

	public String getD_origswver() {
		return d_origswver;
	}

	public void setD_origswver(String d_origswver) {
		this.d_origswver = d_origswver;
	}

	public String getD_origvapmodule() {
		return d_origvapmodule;
	}

	public void setD_origvapmodule(String d_origvapmodule) {
		this.d_origvapmodule = d_origvapmodule;
	}

	public String getD_workmodel() {
		return d_workmodel;
	}

	public void setD_workmodel(String d_workmodel) {
		this.d_workmodel = d_workmodel;
	}

	public String getD_configmodel() {
		return d_configmodel;
	}

	public void setD_configmodel(String d_configmodel) {
		this.d_configmodel = d_configmodel;
	}

	public String getD_type() {
		return d_type;
	}

	public void setD_type(String d_type) {
		this.d_type = d_type;
	}

	public double[] getD_geopoint() {
		return d_geopoint;
	}

	public void setD_geopoint(double[] d_geopoint) {
		this.d_geopoint = d_geopoint;
	}

	public String getD_address() {
		return d_address;
	}

	public void setD_address(String d_address) {
		this.d_address = d_address;
	}

	public String getD_online() {
		return d_online;
	}

	public void setD_online(String d_online) {
		this.d_online = d_online;
	}

	public String getD_monline() {
		return d_monline;
	}

	public void setD_monline(String d_monline) {
		this.d_monline = d_monline;
	}

	public int getD_hoc() {
		return d_hoc;
	}

	public void setD_hoc(int d_hoc) {
		this.d_hoc = d_hoc;
	}

	public long getD_lastregedat() {
		return d_lastregedat;
	}

	public void setD_lastregedat(long d_lastregedat) {
		this.d_lastregedat = d_lastregedat;
	}

	public long getD_lastlogoutat() {
		return d_lastlogoutat;
	}

	public void setD_lastlogoutat(long d_lastlogoutat) {
		this.d_lastlogoutat = d_lastlogoutat;
	}
	
	public long getD_createdat() {
		return d_createdat;
	}

	public void setD_createdat(long d_createdat) {
		this.d_createdat = d_createdat;
	}

	public void setD_dut(String d_dut) {
		this.d_dut = d_dut;
	}

	public String getD_dut() {
		return d_dut;
	}

	public String getD_uptime() {
		return d_uptime;
	}

	public void setD_uptime(String d_uptime) {
		this.d_uptime = d_uptime;
	}

	public String getUpdatedat() {
		return updatedat;
	}

	public void setUpdatedat(String updatedat) {
		this.updatedat = updatedat;
	}

	public String getO_template() {
		return o_template;
	}

	public void setO_template(String o_template) {
		this.o_template = o_template;
	}

	public String getO_graylevel() {
		return o_graylevel;
	}

	public void setO_graylevel(String o_graylevel) {
		this.o_graylevel = o_graylevel;
	}

	public String getO_batch() {
		return o_batch;
	}

	public void setO_batch(String o_batch) {
		this.o_batch = o_batch;
	}

	public String getU_id() {
		return u_id;
	}

	public void setU_id(String u_id) {
		this.u_id = u_id;
	}

	public String getU_nick() {
		return u_nick;
	}

	public void setU_nick(String u_nick) {
		this.u_nick = u_nick;
	}

	public String getU_mno() {
		return u_mno;
	}

	public void setU_mno(String u_mno) {
		this.u_mno = u_mno;
	}

	public String getU_mcc() {
		return u_mcc;
	}

	public void setU_mcc(String u_mcc) {
		this.u_mcc = u_mcc;
	}

	public String getU_type() {
		return u_type;
	}

	public void setU_type(String u_type) {
		this.u_type = u_type;
	}

	public String getA_id() {
		return a_id;
	}

	public void setA_id(String a_id) {
		this.a_id = a_id;
	}

	public String getA_nick() {
		return a_nick;
	}

	public void setA_nick(String a_nick) {
		this.a_nick = a_nick;
	}

	public String getA_org() {
		return a_org;
	}

	public void setA_org(String a_org) {
		this.a_org = a_org;
	}

	public void setId(String id) {
		this.id = id;
	}

}
