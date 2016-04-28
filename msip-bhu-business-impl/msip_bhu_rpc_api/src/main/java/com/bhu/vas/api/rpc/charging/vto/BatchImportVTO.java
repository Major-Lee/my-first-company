package com.bhu.vas.api.rpc.charging.vto;

import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;

@SuppressWarnings("serial")
public class BatchImportVTO implements java.io.Serializable{
	private String id;
	private int status;
	//导入用户
	private int importor;
	private String importor_nick;
	private String importor_mobileno;
	private String mobileno;
	private double manufacturer_percent;
	private boolean canbeturnoff;
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
	public double getManufacturer_percent() {
		return manufacturer_percent;
	}
	public void setManufacturer_percent(double manufacturer_percent) {
		this.manufacturer_percent = manufacturer_percent;
	}
	
	public boolean isCanbeturnoff() {
		return canbeturnoff;
	}
	public void setCanbeturnoff(boolean canbeturnoff) {
		this.canbeturnoff = canbeturnoff;
	}
	public String toAbsoluteFileInputPath(){
		StringBuilder sb = new StringBuilder();
		sb.append(BusinessRuntimeConfiguration.BatchImport_Dir)
			.append(BusinessRuntimeConfiguration.BatchImport_Shipment)
			.append(BusinessRuntimeConfiguration.BatchImport_Sub_Input_Dir)
			.append(this.id).append(".xlsx");
		return sb.toString();
	}
	public String toAbsoluteFileOutputPath(){
		StringBuilder sb = new StringBuilder();
		sb.append(BusinessRuntimeConfiguration.BatchImport_Dir)
			.append(BusinessRuntimeConfiguration.BatchImport_Shipment)
			.append(BusinessRuntimeConfiguration.BatchImport_Sub_Output_Dir)
			.append(this.id).append(".xlsx");
		return sb.toString();
	}
}
