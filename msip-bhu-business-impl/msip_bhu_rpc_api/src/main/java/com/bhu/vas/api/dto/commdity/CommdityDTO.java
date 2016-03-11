package com.bhu.vas.api.dto.commdity;


/**
 * 商品DTO
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class CommdityDTO implements java.io.Serializable{
	//商品id
	private Integer id;
	//商品类别
	private Integer category;
	//商品价格(两种方式 10.25 和 5.25-10.25)
	private String price;
	//商品状态
	private Integer status;
	//商品标签
	private String tags;
	//库存量
	private String stock_quantity;
	//用于存储应用端针对不同商品的发货处理细节 (对于限时上网服务 存储的是限时时间)
	private String app_deliver_detail;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getCategory() {
		return category;
	}
	public void setCategory(Integer category) {
		this.category = category;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public String getStock_quantity() {
		return stock_quantity;
	}
	public void setStock_quantity(String stock_quantity) {
		this.stock_quantity = stock_quantity;
	}
	public String getApp_deliver_detail() {
		return app_deliver_detail;
	}
	public void setApp_deliver_detail(String app_deliver_detail) {
		this.app_deliver_detail = app_deliver_detail;
	}
}

