package com.bhu.vas.business.search.model.wifi;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;

import com.bhu.vas.business.search.BusinessIndexDefine;
import com.bhu.vas.business.search.model.AbstractDocument;

@Document(	indexName = BusinessIndexDefine.Ssid.IndexName, 
			type = BusinessIndexDefine.Ssid.Type, 
			shards = BusinessIndexDefine.Ssid.Shards, 
			replicas = BusinessIndexDefine.Ssid.replicas,
			refreshInterval = BusinessIndexDefine.Ssid.refreshInterval
)
public class SsidDocument extends AbstractDocument{
	
	@Id
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String id;
	
	@Field(
			type = FieldType.String,
			searchAnalyzer = "whitespace",
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String s_ssid;

	@Field(
			type = FieldType.String,
			searchAnalyzer = "whitespace",
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String s_mode;

	@Field(
			type = FieldType.String,
			searchAnalyzer = "whitespace",
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String s_device;

	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String s_pwd;

	@GeoPointField
	private double[] s_geopoint;
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String s_created_at;
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String s_updated_at;
	
	@Override
	public String getId() {
		return this.id;
	}
	
	

	public String getS_mode() {
		return s_mode;
	}



	public void setS_mode(String s_mode) {
		this.s_mode = s_mode;
	}



	public void setId(String id) {
		this.id = id;
	}

	public String getS_ssid() {
		return s_ssid;
	}

	public void setS_ssid(String s_ssid) {
		this.s_ssid = s_ssid;
	}

	public String getS_device() {
		return s_device;
	}

	public void setS_device(String s_device) {
		this.s_device = s_device;
	}

	public String getS_pwd() {
		return s_pwd;
	}

	public void setS_pwd(String s_pwd) {
		this.s_pwd = s_pwd;
	}

	public double[] getS_geopoint() {
		return s_geopoint;
	}

	public void setS_geopoint(double[] a_geopoint) {
		this.s_geopoint = a_geopoint;
	}

	public String getS_created_at() {
		return s_created_at;
	}

	public void setS_created_at(String a_created_at) {
		this.s_created_at = a_created_at;
	}

	public String getS_updated_at() {
		return s_updated_at;
	}

	public void setS_updated_at(String a_updated_at) {
		this.s_updated_at = a_updated_at;
	}
}
