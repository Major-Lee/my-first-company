package com.bhu.vas.api.rpc.tag.model;

import java.io.Serializable;
import java.util.Date;

import com.smartwork.msip.cores.orm.model.extjson.SetJsonExtIntModel;

/**
 * 2016/09/12
 * @author xiaowei
 *
 */
@SuppressWarnings("serial")
public class TagGroupSortMessage extends SetJsonExtIntModel<String> implements Serializable{
	
	public static final String deafult =  "pending";
	public static final String doing = "doing";
	public static final String done = "done";
	public static final int commdityId = 13;
	public static final String commdityDesc = "虎钻消费 用于短信营销";
	
	private int gid;
	private int uid;
	private String orderid;
	private String context;
	private String start;
	private String end;
	private int connect;
	private int smtotal;
	private String state = deafult;
	private Date created_at;
	private Date update_at;
	
	public int getGid() {
		return gid;
	}

	public void setGid(int gid) {
		this.gid = gid;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public int getConnect() {
		return connect;
	}

	public void setConnect(int connect) {
		this.connect = connect;
	}

	public int getSmtotal() {
		return smtotal;
	}

	public void setSmtotal(int smtotal) {
		this.smtotal = smtotal;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public Date getUpdate_at() {
		return update_at;
	}

	public void setUpdate_at(Date update_at) {
		this.update_at = update_at;
	}

	public static String getDeafult() {
		return deafult;
	}

	public static String getDoing() {
		return doing;
	}

	public static String getDone() {
		return done;
	}

	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}
	
	@Override
	public void preUpdate() {
		this.update_at = new Date();
		super.preUpdate();
	}
	@Override
	public Class<String> getJsonParserModel() {
		return String.class;
	}
}
