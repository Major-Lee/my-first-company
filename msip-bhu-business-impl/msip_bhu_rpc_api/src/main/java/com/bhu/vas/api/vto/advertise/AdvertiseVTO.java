package com.bhu.vas.api.vto.advertise;

import java.util.Date;

import com.bhu.vas.business.search.model.advertise.AdvertiseDocument;

@SuppressWarnings("serial")
public class AdvertiseVTO implements java.io.Serializable{
	private String id;
	private int type;
	private String title;
	private String description;
	private String ownerName;
	private String mobileno;
	//省
	private String province;
	//市
	private String city;
	//区
	private String district;
	private String cash;
	private String url;
	private String image;
	private String domain;
	private Date start;
	private Date end;
	//广告覆盖的设备数
	private long count;
	private int state;
	private String extparams;
	private String reject_reason;
	private double lat;
	private double lon;
	private String distance;
	
	private Date create_at;
	
	
	public Date getCreate_at() {
		return create_at;
	}
	public void setCreate_at(Date create_at) {
		this.create_at = create_at;
	}
	public String getReject_reason() {
		return reject_reason;
	}
	public void setReject_reason(String reject_reason) {
		this.reject_reason = reject_reason;
	}
	//可取消标识
	private boolean escapeFlag=false;
	
	public boolean isEscapeFlag() {
		return escapeFlag;
	}
	public void setEscapeFlag(boolean escapeFlag) {
		this.escapeFlag = escapeFlag;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	public String getMobileno() {
		return mobileno;
	}
	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getCash() {
		return cash;
	}
	public void setCash(String cash) {
		this.cash = cash;
	}
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	public Date getEnd() {
		return end;
	}
	public void setEnd(Date end) {
		this.end = end;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public String getExtparams() {
		return extparams;
	}
	public void setExtparams(String extparams) {
		this.extparams = extparams;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	
	public void document2VTO(AdvertiseDocument doc){
		this.id = doc.getId();
		this.type = doc.getA_type();
		this.title = doc.getA_title();
		this.state = doc.getA_state();
		this.description = doc.getA_desc();
		this.province = doc.getA_province();
		this.city = doc.getA_city();
		this.district = doc.getA_district();
		this.lat=doc.getA_geopoint()[0];
		this.lon= doc.getA_geopoint()[1];
		this.distance = doc.getA_distance();
		this.url = doc.getA_url();
		this.count = doc.getA_count();
		this.domain = doc.getA_domain();
		this.image = doc.getA_image();
		this.extparams = doc.getA_extparams();
		this.reject_reason = doc.getA_reject_reason();
	}
}
