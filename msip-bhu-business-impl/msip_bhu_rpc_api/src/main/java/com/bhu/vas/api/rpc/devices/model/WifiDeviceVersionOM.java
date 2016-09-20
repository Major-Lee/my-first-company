package com.bhu.vas.api.rpc.devices.model;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.vto.device.VersionVTO;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.model.BaseStringModel;


/**
 * 运营模块Operation module版本定义及管理
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class WifiDeviceVersionOM extends BaseStringModel{
	//id 采用运营模块版本 eg： H108V1.2.10M8299.tgz 中的H108V1.2.10M8299，H106V1.3.2M8888
	//固件名称
	private String name;
	//组件文件路径下载url
	private String upgrade_url;
	//备用组件文件路径下载url 逗号分割
	private String upgrade_slaver_urls;

	//适用的产品类型
	private String duts;
	//当前灰度中被引用
	private boolean related;
	private Date created_at;
	
	//版本描述
	private String context;
	//上传者
	private String creator;
	
	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUpgrade_url() {
		return upgrade_url;
	}

	public void setUpgrade_url(String upgrade_url) {
		this.upgrade_url = upgrade_url;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	
	public boolean isRelated() {
		return related;
	}

	public void setRelated(boolean related) {
		this.related = related;
	}

	public String getDuts() {
		return duts;
	}

	public void setDuts(String dut) {
		this.duts = dut;
	}
	
	public String getUpgrade_slaver_urls() {
		return upgrade_slaver_urls;
	}

	public void setUpgrade_slaver_urls(String upgrade_slaver_urls) {
		this.upgrade_slaver_urls = upgrade_slaver_urls;
	}

	public boolean valid(){
		return StringUtils.isNotEmpty(id) && StringUtils.isNotEmpty(upgrade_url);
	}
	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}
	public VersionVTO toVersionVTO(){
		VersionVTO vto = new VersionVTO();
		vto.setId(id);
		vto.setN(name);
		vto.setDut(duts);
		vto.setMinid(StringHelper.MINUS_STRING_GAP);
		vto.setR(related);
		vto.setT(VersionVTO.VersionType_OM);
		vto.setD(DateTimeHelper.formatDate(created_at, DateTimeHelper.FormatPattern1));
		vto.setContext(context);
		vto.setCreator(creator);
		return vto;
	}
	
	public static VersionVTO toEmptyVTO(){
		VersionVTO vto = new VersionVTO();
		vto.setId(StringHelper.MINUS_STRING_GAP);
		vto.setN("无");
		vto.setDut(StringHelper.MINUS_STRING_GAP);
		vto.setMinid(StringHelper.MINUS_STRING_GAP);
		vto.setR(false);
		vto.setT(VersionVTO.VersionType_OM);
		return vto;
	}
}
