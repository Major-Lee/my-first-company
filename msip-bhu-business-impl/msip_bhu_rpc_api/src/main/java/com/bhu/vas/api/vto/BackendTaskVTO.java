package com.bhu.vas.api.vto;

@SuppressWarnings("serial")
public class BackendTaskVTO implements java.io.Serializable {

	private long id;

	private int uid;

	private long gid;

	private long total;

	private long current;
	
	private String state;

	private String opt;
	
	private String subopt;
	
	private String context_var;

	private String description;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public long getGid() {
		return gid;
	}

	public void setGid(long gid) {
		this.gid = gid;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public long getCurrent() {
		return current;
	}

	public void setCurrent(long current) {
		this.current = current;
	}

	public String getSubopt() {
		return subopt;
	}

	public void setSubopt(String subopt) {
		this.subopt = subopt;
	}

	public String getContext_var() {
		return context_var;
	}

	public void setContext_var(String context_var) {
		this.context_var = context_var;
	}
	
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getOpt() {
		return opt;
	}

	public void setOpt(String opt) {
		this.opt = opt;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
