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
	
	private int distributor;
	private String distributor_nick;
	private int distributor_l2;
	private String distributor_l2_nick;
	private String sellor;
	private String partner;
	private String owner_percent;
	private String manufacturer_percent;
	private String distributor_percent;
	private String distributor_l2_percent;
	private boolean canbeturnoff;
	private boolean noapp;
	private boolean enterpriselevel;
	private boolean customized = false;
	private String rcp;
	private String rcm;
	private String ait;
	private int succeed;
	private int failed;
	private String remark;
	private String channel_lv1;
	private String channel_lv2;
	private String opsid;
	private String distributor_type;
	private String created_at;
	private String updated_at;
	
	
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
	
	public String getOwner_percent() {
		return owner_percent;
	}
	public void setOwner_percent(String owner_percent) {
		this.owner_percent = owner_percent;
	}
	
	public boolean isCanbeturnoff() {
		return canbeturnoff;
	}
	public void setCanbeturnoff(boolean canbeturnoff) {
		this.canbeturnoff = canbeturnoff;
	}
	
	public String getSellor() {
		return sellor;
	}
	public void setSellor(String sellor) {
		this.sellor = sellor;
	}
	public String getPartner() {
		return partner;
	}
	public void setPartner(String partner) {
		this.partner = partner;
	}
	public boolean isEnterpriselevel() {
		return enterpriselevel;
	}
	public void setEnterpriselevel(boolean enterpriselevel) {
		this.enterpriselevel = enterpriselevel;
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
	
	public String getDownInput(){
		StringBuilder sb = new StringBuilder();
		sb.append(BusinessRuntimeConfiguration.Unicorn_Http_Res_UrlPrefix)
			.append(BusinessRuntimeConfiguration.BatchImport_Shipment)
			.append(BusinessRuntimeConfiguration.BatchImport_Sub_Input_Dir)
			.append(this.id).append(".xlsx");
		return sb.toString();
	}
	public String getDownOuput(){
		StringBuilder sb = new StringBuilder();
		sb.append(BusinessRuntimeConfiguration.Unicorn_Http_Res_UrlPrefix)
			.append(BusinessRuntimeConfiguration.BatchImport_Shipment)
			.append(BusinessRuntimeConfiguration.BatchImport_Sub_Output_Dir)
			.append(this.id).append(".xlsx");
		return sb.toString();
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public String getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}
	public String getRcp() {
		return rcp;
	}
	public void setRcp(String rcp) {
		this.rcp = rcp;
	}
	public String getRcm() {
		return rcm;
	}
	public void setRcm(String rcm) {
		this.rcm = rcm;
	}
	public String getAit() {
		return ait;
	}
	public void setAit(String ait) {
		this.ait = ait;
	}
	public boolean isCustomized() {
		return customized;
	}
	public void setCustomized(boolean customized) {
		this.customized = customized;
	}
	public int getDistributor() {
		return distributor;
	}
	public void setDistributor(int distributor) {
		this.distributor = distributor;
	}
	public String getManufacturer_percent() {
		return manufacturer_percent;
	}
	public void setManufacturer_percent(String manufacturer_percent) {
		this.manufacturer_percent = manufacturer_percent;
	}
	public String getDistributor_percent() {
		return distributor_percent;
	}
	public void setDistributor_percent(String distributor_percent) {
		this.distributor_percent = distributor_percent;
	}
	public String getDistributor_nick() {
		return distributor_nick;
	}
	public void setDistributor_nick(String distributor_nick) {
		this.distributor_nick = distributor_nick;
	}
	public String getChannel_lv1() {
		return channel_lv1;
	}
	public void setChannel_lv1(String channel_lv1) {
		this.channel_lv1 = channel_lv1;
	}
	public String getChannel_lv2() {
		return channel_lv2;
	}
	public void setChannel_lv2(String channel_lv2) {
		this.channel_lv2 = channel_lv2;
	}
	public String getOpsid() {
		return opsid;
	}
	public void setOpsid(String opsid) {
		this.opsid = opsid;
	}
	public String getDistributor_type() {
		return distributor_type;
	}
	public void setDistributor_type(String distributor_type) {
		this.distributor_type = distributor_type;
	}
	public int getDistributor_l2() {
		return distributor_l2;
	}
	public void setDistributor_l2(int distributor_l2) {
		this.distributor_l2 = distributor_l2;
	}
	public String getDistributor_l2_nick() {
		return distributor_l2_nick;
	}
	public void setDistributor_l2_nick(String distributor_l2_nick) {
		this.distributor_l2_nick = distributor_l2_nick;
	}
	public String getDistributor_l2_percent() {
		return distributor_l2_percent;
	}
	public void setDistributor_l2_percent(String distributor_l2_percent) {
		this.distributor_l2_percent = distributor_l2_percent;
	}
	public boolean isNoapp() {
		return noapp;
	}
	public void setNoapp(boolean noapp) {
		this.noapp = noapp;
	}
	
}
