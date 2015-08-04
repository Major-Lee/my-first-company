package com.bhu.vas.business.search.indexable;

import com.smartwork.msip.es.index.IndexableComponent;
import com.smartwork.msip.es.index.field.GeoPointsIndexableField;
/**
 * wifi设备数据索引类
 * @author lawliet
 *
 */
public class WifiDeviceIndexableComponent extends IndexableComponent{
	
	private String id;
	private GeoPointsIndexableField ghash;
	private String address;
	private String showaddress;
	private String origswver;
	private String workmodel;
	private String configmodel;
	private String devicetype;
	private int online;
	private String groups;
	private int nvd;
	private int count;
	private long register_at;
	private String i_update_at;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public GeoPointsIndexableField getGhash() {
		return ghash;
	}
	public void setGhash(GeoPointsIndexableField ghash) {
		this.ghash = ghash;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getShowaddress() {
		return showaddress;
	}
	public void setShowaddress(String showaddress) {
		this.showaddress = showaddress;
	}
	public String getOrigswver() {
		return origswver;
	}
	public void setOrigswver(String origswver) {
		this.origswver = origswver;
	}
	public String getConfigmodel() {
		return configmodel;
	}
	public void setConfigmodel(String configmodel) {
		this.configmodel = configmodel;
	}
	public String getWorkmodel() {
		return workmodel;
	}
	public void setWorkmodel(String workmodel) {
		this.workmodel = workmodel;
	}
	public String getDevicetype() {
		return devicetype;
	}
	public void setDevicetype(String devicetype) {
		this.devicetype = devicetype;
	}
	public int getOnline() {
		return online;
	}
	public void setOnline(int online) {
		this.online = online;
	}
	public String getGroups() {
		return groups;
	}
	public void setGroups(String groups) {
		this.groups = groups;
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
	public void addLocation(double[] lats_lons){
		if(lats_lons == null || lats_lons.length != 2) return;
		
		GeoPointsIndexableField location = this.getGhash();
		if(location == null){
			location = new GeoPointsIndexableField();
		}
		
//		for(double[] lat_lon : lats_lons){
		location.addLatlon(lats_lons[0], lats_lons[1]);
//		}
		this.setGhash(location);
	}
	public long getRegister_at() {
		return register_at;
	}
	public void setRegister_at(long register_at) {
		this.register_at = register_at;
	}
	public String getI_update_at() {
		return i_update_at;
	}
	public void setI_update_at(String i_update_at) {
		this.i_update_at = i_update_at;
	}
	
	@Override
	public String id() {
		return id;
	}
	@Override
	public String routing() {
		return null;
	}
	
//	public static void main(String[] args){
//		WifiDeviceIndexableComponent a = new WifiDeviceIndexableComponent();
//		a.addLocation(null);
//	}
	
}
