package com.bhu.vas.business.search.model.advertise;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;

import com.bhu.vas.business.search.BusinessIndexDefine;
import com.bhu.vas.business.search.model.AbstractDocument;

@Document(	indexName = BusinessIndexDefine.Advertise.IndexName, 
			type = BusinessIndexDefine.Advertise.Type, 
			shards = BusinessIndexDefine.Advertise.Shards, 
			replicas = BusinessIndexDefine.Advertise.replicas,
			refreshInterval = BusinessIndexDefine.Advertise.refreshInterval
)
public class AdvertiseDocument extends AbstractDocument{
	
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
	private String a_title;
	
	@Field(
			type = FieldType.Integer,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private int a_type;
	
	@Field(
			type = FieldType.Integer,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private int a_top;
	
	@Field(
			type = FieldType.Integer,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private int a_tag;
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String a_desc;
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String a_image;
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String a_url;
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String a_domain;
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String a_province;
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String a_city;
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String a_district;
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String a_adcode;
	
	@GeoPointField
	private double[] a_geopoint;
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String a_distance;
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String a_cash;
	
	@Field(
			type = FieldType.Integer,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private long a_count;
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String a_start;
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String a_end;
	
	@Field(
			type = FieldType.Integer,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private int a_duration;
	
	@Field(
			type = FieldType.Long,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private long a_abledevices_num;
	
	@Field(
			type = FieldType.Integer,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private int a_state;
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String a_reject_reason;
	
	@Field(
			type = FieldType.Integer,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private int a_verify_uid;
	
	@Field(
			type = FieldType.Integer,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private int a_process_state;
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String a_extparams;
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String a_created_at;
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String a_updated_at;
	
	@Field(
			type = FieldType.Long,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private long a_score;
	
	@Field(
			type = FieldType.String,
			index = FieldIndex.not_analyzed,
			store = true
	)
	private String u_id;
	
	@Override
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getA_title() {
		return a_title;
	}

	public void setA_title(String a_title) {
		this.a_title = a_title;
	}

	public int getA_type() {
		return a_type;
	}

	public void setA_type(int a_type) {
		this.a_type = a_type;
	}

	public int getA_top() {
		return a_top;
	}

	public void setA_top(int a_top) {
		this.a_top = a_top;
	}

	public int getA_tag() {
		return a_tag;
	}

	public void setA_tag(int a_tag) {
		this.a_tag = a_tag;
	}

	public String getA_desc() {
		return a_desc;
	}

	public void setA_desc(String a_desc) {
		this.a_desc = a_desc;
	}

	public String getA_image() {
		return a_image;
	}

	public void setA_image(String a_image) {
		this.a_image = a_image;
	}

	public String getA_url() {
		return a_url;
	}

	public void setA_url(String a_url) {
		this.a_url = a_url;
	}

	public String getA_domain() {
		return a_domain;
	}

	public void setA_domain(String a_domain) {
		this.a_domain = a_domain;
	}

	public String getA_province() {
		return a_province;
	}

	public void setA_province(String a_province) {
		this.a_province = a_province;
	}

	public String getA_city() {
		return a_city;
	}

	public void setA_city(String a_city) {
		this.a_city = a_city;
	}

	public String getA_district() {
		return a_district;
	}

	public void setA_district(String a_district) {
		this.a_district = a_district;
	}

	public double[] getA_geopoint() {
		return a_geopoint;
	}

	public void setA_geopoint(double[] a_geopoint) {
		this.a_geopoint = a_geopoint;
	}

	public String getA_distance() {
		return a_distance;
	}

	public void setA_distance(String a_distance) {
		this.a_distance = a_distance;
	}

	public String getA_cash() {
		return a_cash;
	}

	public void setA_cash(String a_cash) {
		this.a_cash = a_cash;
	}

	public long getA_count() {
		return a_count;
	}

	public void setA_count(long a_count) {
		this.a_count = a_count;
	}

	public String getA_start() {
		return a_start;
	}

	public void setA_start(String a_start) {
		this.a_start = a_start;
	}

	public String getA_end() {
		return a_end;
	}

	public void setA_end(String a_end) {
		this.a_end = a_end;
	}

	public int getA_duration() {
		return a_duration;
	}

	public void setA_duration(int a_duration) {
		this.a_duration = a_duration;
	}

	public Long getA_abledevices_num() {
		return a_abledevices_num;
	}

	public void setA_abledevices_num(long a_abledevices_num) {
		this.a_abledevices_num = a_abledevices_num;
	}

	public int getA_state() {
		return a_state;
	}

	public void setA_state(int a_state) {
		this.a_state = a_state;
	}

	public String getA_reject_reason() {
		return a_reject_reason;
	}

	public void setA_reject_reason(String a_reject_reason) {
		this.a_reject_reason = a_reject_reason;
	}

	public int getA_verify_uid() {
		return a_verify_uid;
	}

	public void setA_verify_uid(int a_verify_uid) {
		this.a_verify_uid = a_verify_uid;
	}

	public int getA_process_state() {
		return a_process_state;
	}

	public void setA_process_state(int a_process_state) {
		this.a_process_state = a_process_state;
	}

	public String getA_extparams() {
		return a_extparams;
	}

	public void setA_extparams(String a_extparams) {
		this.a_extparams = a_extparams;
	}

	public String getA_created_at() {
		return a_created_at;
	}

	public void setA_created_at(String a_created_at) {
		this.a_created_at = a_created_at;
	}

	public String getA_updated_at() {
		return a_updated_at;
	}

	public void setA_updated_at(String a_updated_at) {
		this.a_updated_at = a_updated_at;
	}
	
	public String getA_adcode() {
		return a_adcode;
	}

	public void setA_adcode(String a_adcode) {
		this.a_adcode = a_adcode;
	}

	public long getA_score() {
		return a_score;
	}

	public void setA_score(long a_score) {
		this.a_score = a_score;
	}

	public String getU_id() {
		return u_id;
	}

	public void setU_id(String u_id) {
		this.u_id = u_id;
	}
	
}
