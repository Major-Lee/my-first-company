package com.bhu.vas.business.search.indexable;

import com.smartwork.msip.es.index.IndexableComponent;
import com.smartwork.msip.es.index.field.GeoPointsIndexableField;
/**
 * 用户地理位置索引数据类
 * @author lawliet
 *
 */
public class UserLocationIndexableComponent extends IndexableComponent{
	
	private String id;
	private GeoPointsIndexableField location;
	private String area;
	private long create_at;
	private String i_update_at;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public GeoPointsIndexableField getLocation() {
		return location;
	}
	public void setLocation(GeoPointsIndexableField location) {
		this.location = location;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public void addLocations(double[]... lats_lons){
		if(lats_lons == null) return;
		
		GeoPointsIndexableField location = this.getLocation();
		if(location == null){
			location = new GeoPointsIndexableField();
		}
		
		for(double[] lat_lon : lats_lons){
			location.addLatlon(lat_lon[0], lat_lon[1]);
		}
		this.setLocation(location);
	}
	public long getCreate_at() {
		return create_at;
	}
	public void setCreate_at(long create_at) {
		this.create_at = create_at;
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
	
	
}
