package com.bhu.vas.business.search.indexable;

import com.smartwork.msip.es.index.IndexableComponent;
/**
 * 用户索引数据类
 * @author lawliet
 *
 */
public class UserIndexableComponent extends IndexableComponent{
	
	private String id;
	private String symbol;
	private String shownick;
	private String nick;
	private String avatar;
	private String mobileno;
	private String mno;
	private String pinyin;
	//private GeoPointsIndexableField location;
	private long sort;
	private long create_at;
	private String i_update_at;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getShownick() {
		return shownick;
	}
	public void setShownick(String shownick) {
		this.shownick = shownick;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getMobileno() {
		return mobileno;
	}
	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}
	public String getMno() {
		return mno;
	}
	public void setMno(String mno) {
		this.mno = mno;
	}
	public String getPinyin() {
		return pinyin;
	}
	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}
	/*public GeoPointsIndexableField getLocation() {
		return location;
	}
	public void setLocation(GeoPointsIndexableField location) {
		this.location = location;
	}*/
	public long getSort() {
		return sort;
	}
	public void setSort(long sort) {
		this.sort = sort;
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
