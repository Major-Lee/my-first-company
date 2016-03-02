package com.bhu.vas.api.rpc.commdity.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseIntModel;

/**
 * 商品模型
 * @author tangzichao
 */
@SuppressWarnings("serial")
public class Item extends BaseIntModel{
	//商品类别
	private String category;
	//商品价格
	private String price;
	//商品状态（上架）
	private Integer status;
	//库存量
	private String stock_quantity;
	//用于存储应用端针对不同商品的发货处理细节 json
	private String app_deliver_detail;
	//用于存储系统针对不同商品的订单处理细节 json
	private String item_order_detail;
	//商品创建时间
	private Date created_at;
	
	public Item() {
		super();
	}

	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
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

	public String getItem_order_detail() {
		return item_order_detail;
	}

	public void setItem_order_detail(String item_order_detail) {
		this.item_order_detail = item_order_detail;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
}
