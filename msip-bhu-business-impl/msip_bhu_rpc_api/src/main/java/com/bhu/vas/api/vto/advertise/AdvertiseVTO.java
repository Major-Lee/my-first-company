package com.bhu.vas.api.vto.advertise;

import java.util.Date;

@SuppressWarnings("serial")
public class AdvertiseVTO implements java.io.Serializable{
	private String id;
	private int type;
	private int tag;
	private int top = -1;//-1：默认 1：置顶  0：取消置顶
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
	//阅读数
	private String pv;
	//点赞数
	private String act;
	private long comment_sum;
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
	public int getTag() {
		return tag;
	}
	public void setTag(int tag) {
		this.tag = tag;
	}
	public int isTop() {
		return top;
	}
	public void setTop(int top) {
		this.top = top;
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
	public String getPv() {
		return pv;
	}
	public void setPv(String pv) {
		this.pv = pv;
	}
	public String getAct() {
		return act;
	}
	public void setAct(String act) {
		this.act = act;
	}
	public long getComment_sum() {
		return comment_sum;
	}
	public void setComment_sum(long comment_sum) {
		this.comment_sum = comment_sum;
	}
	public int getTop() {
		return top;
	}
}
