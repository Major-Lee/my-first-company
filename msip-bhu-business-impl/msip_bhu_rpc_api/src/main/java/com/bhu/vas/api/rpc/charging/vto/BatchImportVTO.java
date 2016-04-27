package com.bhu.vas.api.rpc.charging.vto;

@SuppressWarnings("serial")
public class BatchImportVTO implements java.io.Serializable{
	private String id;
	private int status;
	//导入用户
	private int importor;
	private String importor_nick;
	private String importor_mobileno;
	private String mobileno;
	private int succeed;
	private int failed;
	private String remark;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getImportor() {
		return importor;
	}
	public void setImportor(int importor) {
		this.importor = importor;
	}
	public String getMobileno() {
		return mobileno;
	}
	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}
	public int getSucceed() {
		return succeed;
	}
	public void setSucceed(int succeed) {
		this.succeed = succeed;
	}
	public int getFailed() {
		return failed;
	}
	public void setFailed(int failed) {
		this.failed = failed;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getImportor_nick() {
		return importor_nick;
	}
	public void setImportor_nick(String importor_nick) {
		this.importor_nick = importor_nick;
	}
	public String getImportor_mobileno() {
		return importor_mobileno;
	}
	public void setImportor_mobileno(String importor_mobileno) {
		this.importor_mobileno = importor_mobileno;
	}
}
