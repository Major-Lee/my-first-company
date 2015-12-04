package com.bhu.vas.business.search.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;

import com.bhu.vas.business.search.BusinessIndexDefine;

@Document(	indexName = BusinessIndexDefine.WifiDevice.IndexName, 
			type = BusinessIndexDefine.WifiDevice.Type, 
			shards = BusinessIndexDefine.WifiDevice.Shards, 
			replicas = BusinessIndexDefine.WifiDevice.replicas,
			refreshInterval = BusinessIndexDefine.WifiDevice.refreshInterval
		)
/*@Document(	indexName = "wifi_device_index9", 
type = "myWifiDevice", 
shards = 5, 
replicas = 1)*/
public class WifiDeviceDocumentBak extends AbstractDocument{
	@Id
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String id;
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String sn;//wifi sn

	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String origswver;//原始软件版本号
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String origvapmodule;//原始软件增值模块版本号

	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String workmodel;//工作模式
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String configmodel;//工作配置模式
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String devicetype;//设备类型

	
	/**
	 * 建立索引的时候 数组为 经度 纬度
	 * 
	 */
	@GeoPointField
	private double[] geopoint;//lon,lat,
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.analyzed,
			searchAnalyzer = "lowercase_whitespace",
			indexAnalyzer = "ngram_2_to_20_analyzer",
			store = true
	)
	private String address;
	
	@Field(
			type = FieldType.Boolean,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private Boolean online;//wifi设备是否在线
	
	@Field(
			type = FieldType.Boolean,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private Boolean moduleonline;//wifi设备增值模块是否在线
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.analyzed,
			searchAnalyzer = "lowercase_whitespace",
			indexAnalyzer = "lowercase_whitespace",
			store = true
	)
	private String groups;//wifi设备所属群组
	
	@Field(
			type = FieldType.Integer,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private int nvd;//是否是新版本设备
	
	@Field(
			type = FieldType.Integer,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private int count;//wifi设备上的移动设备在线数量
	
	@Field(
			type = FieldType.Long,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private long registeredat;//wifi设备的注册时间
	
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

	@Override
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double[] getGeopoint() {
		return geopoint;
	}

	public void setGeopoint(double[] geopoint) {
		this.geopoint = geopoint;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Boolean getOnline() {
		return online;
	}

	public void setOnline(Boolean online) {
		this.online = online;
	}

	public String getGroups() {
		return groups;
	}

	public void setGroups(String groups) {
		this.groups = groups;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getOrigswver() {
		return origswver;
	}

	public void setOrigswver(String origswver) {
		this.origswver = origswver;
	}

	public String getWorkmodel() {
		return workmodel;
	}

	public void setWorkmodel(String workmodel) {
		this.workmodel = workmodel;
	}

	public String getConfigmodel() {
		return configmodel;
	}

	public void setConfigmodel(String configmodel) {
		this.configmodel = configmodel;
	}

	public String getDevicetype() {
		return devicetype;
	}

	public void setDevicetype(String devicetype) {
		this.devicetype = devicetype;
	}

	public int getNvd() {
		return nvd;
	}

	public void setNvd(int nvd) {
		this.nvd = nvd;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public long getRegisteredat() {
		return registeredat;
	}

	public void setRegisteredat(long registeredat) {
		this.registeredat = registeredat;
	}

	public String getUpdatedat() {
		return updatedat;
	}

	public void setUpdatedat(String updatedat) {
		this.updatedat = updatedat;
	}

	public Boolean getModuleonline() {
		return moduleonline;
	}

	public void setModuleonline(Boolean moduleonline) {
		this.moduleonline = moduleonline;
	}

	public String getOrigvapmodule() {
		return origvapmodule;
	}

	public void setOrigvapmodule(String origvapmodule) {
		this.origvapmodule = origvapmodule;
	}

}
