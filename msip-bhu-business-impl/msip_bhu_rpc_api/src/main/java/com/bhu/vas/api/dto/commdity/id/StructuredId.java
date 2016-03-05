package com.bhu.vas.api.dto.commdity.id;
/**
 * 结构化id实体
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class StructuredId implements java.io.Serializable{
	//应用id
	private int appid;
	//YYYYMMdd
	private String date;
	//扩展业务dto
	private StructuredExtSegment extSegment;
	//autoid
	private Long autoid;
	
	public int getAppid() {
		return appid;
	}
	public void setAppid(int appid) {
		this.appid = appid;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public StructuredExtSegment getExtSegment() {
		return extSegment;
	}
	public void setExtSegment(StructuredExtSegment extSegment) {
		this.extSegment = extSegment;
	}
	public Long getAutoid() {
		return autoid;
	}
	public void setAutoid(Long autoid) {
		this.autoid = autoid;
	}
}
