package com.bhu.vas.business.ds.agent.mdto;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.smartwork.msip.cores.helper.StringHelper;

/**
 * 此表数据来源于每月结算生成代理商结算清单
 * @author Edmond
 *
 */
@Document(collection = "t_agent_settlements_record")
public class AgentSettlementsRecordMDTO {
	public static final int Settlement_Illegal = -1000;
	public static final int Settlement_View_All = -1;
	//public static final int Settlement_Created = 0;
	//public static final int Settlement_Done = 1;
	
	public static final int Settlement_Bill_Created = 0;
	public static final int Settlement_Bill_Parted = 1;
	public static final int Settlement_Bill_Done = 2;
	
	
	@Id
	private String id;//2015-09_user
	//yyyyMMdd
	private String date;
	private int agent;
	//结算人
	private int reckoner;
	
	//需要结算金额
	private double iSVPrice;
	//settledPrice 已经结算的金额
	private double sdPrice;
	//结算单状态
	private int status;
	//结算单生成日期
	private String created_at;
	//结算日期
	private String settled_at;
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getAgent() {
		return agent;
	}

	public void setAgent(int agent) {
		this.agent = agent;
	}

	public int getReckoner() {
		return reckoner;
	}

	public void setReckoner(int reckoner) {
		this.reckoner = reckoner;
	}

	public double getiSVPrice() {
		return iSVPrice;
	}

	public void setiSVPrice(double iSVPrice) {
		this.iSVPrice = iSVPrice;
	}
	
	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getSettled_at() {
		return settled_at;
	}

	public void setSettled_at(String settled_at) {
		this.settled_at = settled_at;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public double getSdPrice() {
		return sdPrice;
	}

	public void setSdPrice(double sdPrice) {
		this.sdPrice = sdPrice;
	}

	public static String generateId(String date, int agent){
		StringBuffer idstring = new StringBuffer();
		idstring.append(date).append(StringHelper.UNDERLINE_STRING_GAP).append(agent);
		return idstring.toString();
	}
}
