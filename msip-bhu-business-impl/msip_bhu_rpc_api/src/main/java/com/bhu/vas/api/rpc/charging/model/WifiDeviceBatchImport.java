package com.bhu.vas.api.rpc.charging.model;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.bhu.vas.api.rpc.charging.vto.BatchImportVTO;
import com.bhu.vas.api.rpc.sequence.helper.IRedisSequenceGenable;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.model.BaseStringModel;

/**
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class WifiDeviceBatchImport extends BaseStringModel implements IRedisSequenceGenable{
	//ID 格式为 yyyyMMdd-longsequence
	
	//取消导入
	public static final int STATUS_IMPORTING_CANCEL = -1;
	//导入上传中
	public static final int STATUS_IMPORTING_FILE = 0;
	//导入上传成功
	public static final int STATUS_IMPORTED_FILE = 1;
	//确定导入批次
	public static final int STATUS_CONFIRMED_DOING = 2;
	//导入内容正在处理中
	public static final int STATUS_CONTENT_IMPORTING = 3;
	//导入内容处理成功
	public static final int STATUS_CONTENT_IMPORTED = 4;
	//文件存储路径
	private String filepath;
	// 0-文件正在导入 1-文件导入成功 2-确认导入成功
	private int status;
	//导入用户
	private int importor;
	private String mobileno;
	private String sellor;
	private String partner;
	private double owner_percent;
	private boolean canbeturnoff;
	private boolean enterpriselevel;
	private String range_cash_mobile;
	private String range_cash_pc;
	private String access_internet_time;
	//private int total;
	private int succeed;
	private int failed;
	private String remark;
    /**
     * 创建日期
     */
    private Date created_at;
	public String getFilepath() {
		return filepath;
	}
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
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
	public double getOwner_percent() {
		return owner_percent;
	}
	public void setOwner_percent(double owner_percent) {
		this.owner_percent = owner_percent;
	}
	public boolean isEnterpriselevel() {
		return enterpriselevel;
	}
	public void setEnterpriselevel(boolean enterpriselevel) {
		this.enterpriselevel = enterpriselevel;
	}
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}
	
	private static final String FormatSequenceTemplete = "%08d";
	@Override
	public void setSequenceKey(Long key) {
		StringBuilder sb = new StringBuilder();
		sb.append(DateTimeHelper.formatDate(DateTimeHelper.FormatPattern7))
			.append(StringHelper.MINUS_CHAR_GAP)
			.append(String.format(FormatSequenceTemplete, key));
		this.setId(sb.toString());
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	public BatchImportVTO toBatchImportVTO(String importor_nick,String importor_mobileno){
		BatchImportVTO vto = new BatchImportVTO();
		vto.setId(this.getId());
		vto.setImportor(this.getImportor());
		vto.setImportor_nick(importor_nick);
		vto.setImportor_mobileno(importor_mobileno);
		vto.setMobileno(this.getMobileno());
		vto.setSellor(this.getSellor());
		vto.setPartner(this.getPartner());
		vto.setOwner_percent(this.getOwner_percent());
		//vto.setManufacturer_percent(manufacturer_percent);
		vto.setCanbeturnoff(this.isCanbeturnoff());
		vto.setEnterpriselevel(this.isEnterpriselevel());
		vto.setAit(this.getAccess_internet_time());
		vto.setRcm(this.getRange_cash_mobile());
		vto.setRcp(this.getRange_cash_pc());
		vto.setRemark(this.getRemark());
		vto.setStatus(this.getStatus());
		vto.setSucceed(this.getSucceed());
		vto.setFailed(this.getFailed());
		vto.setCreated_at(DateTimeHelper.getDateTime(this.getCreated_at(), DateTimeHelper.FormatPattern0));
		vto.setUpdated_at(DateTimeHelper.getDateTime(this.getCreated_at(), DateTimeHelper.FormatPattern0));
		return vto;
	}
	
	public boolean isCanbeturnoff() {
		return canbeturnoff;
	}
	public void setCanbeturnoff(boolean canbeturnoff) {
		this.canbeturnoff = canbeturnoff;
	}
	public String getRange_cash_mobile() {
		return range_cash_mobile;
	}
	public void setRange_cash_mobile(String range_cash_mobile) {
		this.range_cash_mobile = range_cash_mobile;
	}
	public String getRange_cash_pc() {
		return range_cash_pc;
	}
	public void setRange_cash_pc(String range_cash_pc) {
		this.range_cash_pc = range_cash_pc;
	}
	public String getAccess_internet_time() {
		return access_internet_time;
	}
	public void setAccess_internet_time(String access_internet_time) {
		this.access_internet_time = access_internet_time;
	}
}
