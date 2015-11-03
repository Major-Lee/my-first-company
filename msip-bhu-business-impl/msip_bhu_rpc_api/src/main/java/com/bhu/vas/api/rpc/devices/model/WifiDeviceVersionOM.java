package com.bhu.vas.api.rpc.devices.model;

import java.util.Date;

import com.bhu.vas.api.vto.device.VersionVTO;
import com.smartwork.msip.cores.helper.DateTimeHelper;
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
	//固件文件下载url
	private String upgrade_url;
	//当前灰度中被引用
	private boolean related;
	private Date created_at;
	
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
		vto.setT(VersionVTO.VersionType_OM);
		vto.setD(DateTimeHelper.formatDate(created_at, DateTimeHelper.FormatPattern1));
		return vto;
	}
}
