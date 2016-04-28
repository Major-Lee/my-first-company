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
	public static final int STATUS_IMPORTING_FILE = 0;
	public static final int STATUS_IMPORTED_FILE = 1;
	public static final int STATUS_CONFIRMED_DOING = 2;
	public static final int STATUS_CONTENT_IMPORTING = 3;
	public static final int STATUS_CONTENT_IMPORTED = 4;
	//文件存储路径
	private String filepath;
	// 0-文件正在导入 1-文件导入成功 2-确认导入成功
	private int status;
	//导入用户
	private int importor;
	private String mobileno;
	private double manufacturer_percent;
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
		vto.setMobileno(this.getMobileno());
		vto.setManufacturer_percent(manufacturer_percent);
		vto.setRemark(this.getRemark());
		vto.setStatus(this.getStatus());
		vto.setSucceed(this.getSucceed());
		vto.setFailed(this.getFailed());
		return vto;
	}
	
	public double getManufacturer_percent() {
		return manufacturer_percent;
	}
	public void setManufacturer_percent(double manufacturer_percent) {
		this.manufacturer_percent = manufacturer_percent;
	}
	
}
